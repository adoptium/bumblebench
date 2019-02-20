/*******************************************************************************
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*******************************************************************************/

package net.adoptopenjdk.bumblebench.core;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.lang.Math;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


/**
 * Base class for all BumbleBench benchmarks.
 * <p>
 * Implements the core self-tuning trial-and-error experimentation system, plus a number of core facilities such as options processing
 */

public abstract class BumbleBench extends Util implements Runnable {

	static final int SERIES_NUMBER   = 7;  // Increment whenever a change can alter reported benchmark scores
	static final int VERSION_NUMBER  = 5;  // Increment whenever new features are added that new benchmarks may rely on
	static final int REVISION_NUMBER = 10; // Increment whenever you want

	/*
	 * The workload
	 */

	/** Run repeatedly by {@link #bumbleMain()} with varying values of
	 * <tt>targetScore</tt> in order to find the highest target score for which
	 * the attempt succeeds.
	 * <p>
	 * The exact meaning of the <em>score</em> differs from benchmark to
	 * benchmark, but is very often some number of operations performed per
	 * second.
	 * <p>
	 * After completing an attempt, the benchmark may choose to return an
	 * estimate of the largest <tt>targetScore</tt> that might succeed.  This
	 * return value doubles as a success indicator: returning a value less than
	 * <tt>targetScore</tt> indicates failure to achieve the target score, while
	 * any other value indicates success.
	 * <p>
	 * If the benchmark is unable or unwilling to provide an estimate, it can
	 * instead return {@link #UNSPECIFIED_SUCCESS} or {@link #UNSPECIFIED_FAILURE}.
	 *
	 * @param  targetScore  the benchmark score to attempt
	 * @return an estimate of the highest achievable score, or {@link #UNSPECIFIED_SUCCESS} or {@link #UNSPECIFIED_FAILURE}
	 */
	protected abstract float attempt(float targetScore) throws InterruptedException;

	/** Optionally implemented by subclasses and called at the end of a run to verify
	 * whether the run was correct or not. Defaults to true. If false, an ERROR message
	 * is printed instead of the final score. Implementing methods may output their own
	 * error message(s) as well.
	 */
	protected boolean verify() {return true;}

	/** Returned by {@link #attempt(float targetScore)} to indicate that the
	 * attempt succeeded without providing an estimate of the highest achievable score.
	 */
	public static final float UNSPECIFIED_SUCCESS = Float.NaN;

	/** Returned by {@link #attempt(float targetScore)} to indicate that the
	 * attempt failed without providing an estimate of the highest achievable score.
	 */
	public static final float UNSPECIFIED_FAILURE = Float.NEGATIVE_INFINITY;

	/*
	 * Core heuristic
	 */

	// Benchmark execution state
	//
	private float _maxPeak     = Float.NEGATIVE_INFINITY;
	private float _recentPeak  = Float.NEGATIVE_INFINITY;
	private float _estimate = 1F;
	private float _uncertainty = 0.5F;
	private float _maxPeakUncertainty = Float.POSITIVE_INFINITY;
	private long  _startTime;

	/** Indicates that the user has requested additional information from the
	 * benchmark, usually to follow its progress or understand its operation.
	 *
	 * Benchmarks are encouraged to use this to guard status and progress
	 * messages.  When VERBOSE is false, benchmarks should be silent.
	 */
	static public final boolean VERBOSE = option("verbose", false);

	/** Indicates that the user doesn't trust the benchmark's correctness has
	 * requested additional information to verify its proper operation.
	 *
	 * Benchmarks are encouraged to use this to guard all messages that trace
	 * fine-grained execution steps and report internal state.
	 */
	static public final boolean DEBUG = option("debug", false);

	/** Handy helper to print a debug message in the appropriate way.
	 *
	 * Requires that {@link DEBUG} is true.  This forces the caller to check
	 * <code>DEBUG</code> before calling this method.  This design helps to avoid
	 * accidentally executing the code to construct the message string itself
	 * when <code>DEBUG</code> is false.  The usual idiom would be:
	 *
	 * 	<code>if (DEBUG) debug(...);</code>
	 */
	final void debug(String message) {
		assert(DEBUG);
		out().println("DEBUG: " + message);
	}

	final boolean runAttempt(boolean lowball) throws InterruptedException {
		float under = _estimate * (1-_uncertainty/2);
		float over  = _estimate * (1+_uncertainty/2);
		float target = lowball? under : over;

		// Run an experiment
		//
		if (VERBOSE)
			out().println("attempt(" + target + ")");
		float result = attempt(target);

		// Analyze the results
		//
		boolean runSucceeded;                    // Was the target score achieved?
		boolean guessWasCorrect;                 // Was runSucceeded what we expected it to be based on the lowball/highball setting?
		boolean newEstimateWasSpecified = false; // Did the run provide an estimate of the score it can achieve?
		float oldEstimate = _estimate;
		if (result >= target && result < Float.POSITIVE_INFINITY) {
			runSucceeded = true;
			recordSuccess(target);
			guessWasCorrect = lowball;
			_estimate = result;
			newEstimateWasSpecified = true;
		} else if (result <= 0F) {
			// UNSPECIFIED_FAILURE
			runSucceeded = false;
			guessWasCorrect = !lowball;
			_estimate = lowball? _estimate * (1-_uncertainty) : _estimate;
		} else if (result < target) {
			runSucceeded = false;
			guessWasCorrect = !lowball;
			_estimate = result; // This is why we can't handle result==0 here.  Estimate hits zero and never recovers
			newEstimateWasSpecified = true;
		} else {
			// UNSPECIFIED_SUCCESS
			runSucceeded = true;
			recordSuccess(target);
			guessWasCorrect = lowball;
			_estimate = lowball? _estimate : _estimate * (1+_uncertainty);
		}
		if (_recentPeak == _maxPeak) {
			if (under <= _maxPeak && _maxPeak <= over)
				_maxPeakUncertainty = Math.min(_maxPeakUncertainty, _uncertainty);
		}
		if (!runSucceeded && target < _recentPeak)
			_recentPeak = Float.NEGATIVE_INFINITY;
		float oldUncertainty = _uncertainty;
		if (runSucceeded && target >= Float.POSITIVE_INFINITY) {
			// lowball guess or not, if we thought it was infinitely fast and the
			// runSucceeded, we were right.  Otherwise, we may never terminate,
			// always attempting to increase the already-infinite target score ever higher.
			guessWasCorrect = true;
		}
		if (newEstimateWasSpecified) {
			float impliedUncertainty = Math.abs(oldEstimate - result) / target;
			if (impliedUncertainty > _uncertainty) {
				if (TAME_UNCERTAINTY) {
					// If the estimate was way off, just bump up the _uncertainty as though our guess was incorrect
					_uncertainty *= INCORRECT_GUESS_ADJUSTMENT;
				} else {
					_uncertainty = impliedUncertainty;
				}
			} else {
				_uncertainty *= guessWasCorrect? CORRECT_GUESS_ADJUSTMENT : INCORRECT_GUESS_ADJUSTMENT;
			}
		} else {
			_uncertainty *= guessWasCorrect? CORRECT_GUESS_ADJUSTMENT : INCORRECT_GUESS_ADJUSTMENT;
		}
		_uncertainty = Math.min(_uncertainty, MAX_UNCERTAINTY); 
		report(target, result, oldUncertainty, lowball, guessWasCorrect, runSucceeded);
		return guessWasCorrect;
	}

	final void recordSuccess(float target) {
		_recentPeak  = max(_recentPeak, target);
		_maxPeak     = max(_maxPeak,    target);
	}

	public float currentEstimatedScore(){ return _estimate; }

	static final boolean LOWBALL  = true;
	static final boolean HIGHBALL = false;

	static final int   MIN_WARMUP_SECONDS        = option("minWarmupSeconds", 10);
	static final int   MAX_WARMUP_SECONDS        = option("maxWarmupSeconds", 150);
	static final float WARMUP_TARGET_UNCERTAINTY = option("warmupTargetUncertainty", 0.1F);
	static final int   BALLPARK_ITERATIONS       = option("ballparkIterations", 20);
	static final int   FINALE_ITERATIONS         = option("finaleIterations", BALLPARK_ITERATIONS/2);

	static final float   CORRECT_GUESS_ADJUSTMENT   = option("correctGuessAdjustment", 0.6F);
	static final float   INCORRECT_GUESS_ADJUSTMENT = option("incorrectGuessAdjustment", 1.2F);
	static final float   MAX_UNCERTAINTY            = option("maxUncertainty", 0.40F);
	static final boolean TAME_UNCERTAINTY           = option("tameUncertainty", false);

	public void run() {
		out().println("\n-= BumbleBench series " + SERIES_NUMBER + " version " + VERSION_NUMBER + "." + REVISION_NUMBER + " running " + _name + "  " + new java.util.Date() + " =-\n");
		_startTime = System.currentTimeMillis();
		try {
			_estimate = option("initialEstimate", 100F);
			_uncertainty = 0.2F;
			reportHeader();
			long startTime = System.currentTimeMillis();
			long minEndTime = startTime + 1000 * MIN_WARMUP_SECONDS;
			long maxEndTime = startTime + 1000 * MAX_WARMUP_SECONDS;
			if (DEBUG) debug("Starting warmup");
			while (true) {
				long currentTime = System.currentTimeMillis();
				if (currentTime > maxEndTime)
					break;
				else if (currentTime > minEndTime && _uncertainty <= WARMUP_TARGET_UNCERTAINTY)
					break;
				else if (_estimate >= Float.POSITIVE_INFINITY)
					break;

				if (DEBUG) debug("Warmup: runAttempt(HIGHBALL)...");
				while (!runAttempt(HIGHBALL)) { if (currentTime > maxEndTime) break; }
				if (DEBUG) debug("Warmup: runAttempt(LOWBALL)...");
				while (!runAttempt(LOWBALL))  { if (currentTime > maxEndTime) break; }
			}
			if (DEBUG) debug("...Warmup completed: "+((System.currentTimeMillis()-startTime)/1000)+" seconds.");
			out().println("   -- ballpark --");
			for (int i = 0; i < BALLPARK_ITERATIONS; i+=2) {
				while (!runAttempt(HIGHBALL)){}
				while (!runAttempt(LOWBALL)){}
			}
			out().println("   -- finale --");
			_maxPeak = _recentPeak;
			_maxPeakUncertainty = Float.POSITIVE_INFINITY;
			for (int i = 0; i < FINALE_ITERATIONS; i+=2) {
				while (!runAttempt(HIGHBALL)){}
				while (!runAttempt(LOWBALL)){}
			}
		} catch (InterruptedException e) {
			out().println("   -- interrupted: " + e.getMessage() + " --");
		}

		String spaces = String.format("%" + (_name.length()-5) + "s", "");
		if (verify()) {
			out().println("\n  " + _name + " score: " + String.format("%f",_maxPeak) + " (" + score(_maxPeak) + " " + logPoints(_maxPeak) + "%)");
		out().println("  " + spaces + "uncertainty: " + percentage(_uncertainty) + "%");
		} else {
			out().println("ERROR: failed verification.");
		}
	}

	/** The main entry point for a BumbleBench program.
	 * <p>
	 * Can be overridden to provide additional startup or shutdown functionality
	 * around the entire benchmark run.  Subclasses can call super.bumbleMain to
	 * run the benchmark.
	 */
	public void bumbleMain() throws Exception {
		Thread watchdog = DISABLE_WATCHDOG? null : new WatchdogThread(Thread.currentThread());
		if (watchdog != null)
			watchdog.start();
		try {
			if (_targetScores == null)
				run(); // Normal run
			else
				runAsWorker();
		} finally {
			if (watchdog != null) {
				watchdog.interrupt();
				try {
					watchdog.join();
				} catch (InterruptedException e) {
					// Shouldn't get interrupted, but if we do, clean everything up
					System.exit(1);
				}
			}
		}
	}

	/*
	 * Support for parallel runs
	 */

	volatile BlockingQueue<Float> _targetScores, _resultScores;

	void makeWorker(int queueSize) {
		_targetScores = new ArrayBlockingQueue<Float>(queueSize);
		_resultScores = new ArrayBlockingQueue<Float>(queueSize);
	}

	void runAsWorker() {
		BlockingQueue<Float> targetScores = _targetScores, resultScores = _resultScores;
		try {
			// Keep taking targets until we hit a Nan or get interrupted
			for (float targetScore = targetScores.take(); !Thread.interrupted() && !Float.isNaN(targetScore); targetScore = targetScores.take())
				resultScores.put(attempt(targetScore));
		} catch (InterruptedException e) {
			// Workers get interrupted at shutdown.  Exit normally.
			return;
		}
	}

	class WorkerThread extends Thread implements Runnable {
		BumbleBench _workload;

		public void run() {
			try {
				Launcher.runBumbleMainOn(_workload);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		public WorkerThread(BumbleBench workload) {
			_workload = workload;
		}
	}

	/*
	 * Watchdog functionality
	 */

	class WatchdogThread extends Thread {
		Thread _benchmarkThread;

		WatchdogThread(Thread benchmarkThread) { _benchmarkThread = benchmarkThread; }

		public void run() {
			try {
				Thread.sleep(1000*watchdogSeconds());
				out().println("!!! WATCHDOG TIMER ELAPSED !!!");
				System.exit(1);
			} catch (InterruptedException e) {}
		}
	}

	static final boolean DISABLE_WATCHDOG = option("disableWatchdog", true);

	int watchdogSeconds(){ return option("defaultWatchdogSeconds", 300); }

	/*
	 * Output
	 */

	final String _name;

	BumbleBench() {
		StringBuilder sb = new StringBuilder();
		buildSimpleInnerClassName(getClass(), sb);
		_name = sb.toString();
	}

	private boolean buildSimpleInnerClassName(Class c, StringBuilder sb) {
		if (c == null) {
			return false;
		} else {
			if (buildSimpleInnerClassName(c.getEnclosingClass(), sb))
				sb.append('.');
			sb.append(c.getSimpleName());
			return true;
		}
	}

	BumbleBench(String name) {
		_name = name;
	}

	void report(float target, float result, float oldUncertainty, boolean lowball, boolean guessWasCorrect, boolean runSucceeded) {
		boolean unspecified = (result < 0F) || Float.isNaN(result);
		out().println("  " + timestamp() + ": " + (unspecified? '?':' ') + (runSucceeded? '>':'<') + (guessWasCorrect? ' ':'!')
			+ ' ' + score(target)
			+ '\t' + score(_estimate)
			+ '\t' + percentage(_uncertainty)
			+ '\t' + score(_maxPeak)
			+ '\t' + score(_recentPeak)
			+ '\t' + logPoints(_recentPeak)
			+ extraReportInfo()
			);
	}

	void reportHeader() {
		out().println("              Target\tEst\tUncert%\tMaxPeak\tPeak\tPeak%" + extraReportHeader());
	}

	String extraReportInfo()   { return ""; }
	String extraReportHeader() { return ""; }

	static final DecimalFormat ONE_DECIMAL_PLACE = initOneDecimalPlace();

	private static DecimalFormat initOneDecimalPlace() {
		DecimalFormatSymbols sym = new DecimalFormatSymbols();
		sym.setInfinity("inf");
		return new DecimalFormat("0.0", sym);
	}

	static final boolean SPREADSHEET_MODE = option("spreadsheetMode", false);

	final String spreadsheet(double value) {
		try {
			return new java.math.BigDecimal(value).toString();
		} catch (NumberFormatException e) {
			return Double.toString(value);
		}
	}

	final String timestamp() {
		double elapsedSeconds = (System.currentTimeMillis() - _startTime) / 1000.0;
		if (SPREADSHEET_MODE)
			return spreadsheet(elapsedSeconds);
		else
			return String.format("%5ss", ONE_DECIMAL_PLACE.format(elapsedSeconds));
	}

	final String percentage(double value) {
		if (SPREADSHEET_MODE)
			return spreadsheet(100*value);
		else
			return String.format("%5s", ONE_DECIMAL_PLACE.format(100*value));
	}

	static final MathContext PRETTY_MODE = new MathContext(option("sigFigs", 4));

	final String siSuffixed(String exponential) {
		int eIndex = exponential.indexOf('E');
		if (eIndex > 0) {
			StringBuilder sb = new StringBuilder(exponential);
			if (exponential.endsWith("E-9"))
				sb.replace(eIndex, sb.length(), "n");
			else if (exponential.endsWith("E-6"))
				sb.replace(eIndex, sb.length(), "u");
			else if (exponential.endsWith("E-3"))
				sb.replace(eIndex, sb.length(), "m");
			else if (exponential.endsWith("E+3"))
				sb.replace(eIndex, sb.length(), "K");
			else if (exponential.endsWith("E+6"))
				sb.replace(eIndex, sb.length(), "M");
			else if (exponential.endsWith("E+9"))
				sb.replace(eIndex, sb.length(), "G");
			else if (exponential.endsWith("E+12"))
				sb.replace(eIndex, sb.length(), "T");
			else
				return exponential; // I give up, just leave the string alone
			return sb.toString();
		}

		// default
		return exponential;
	}

	final String pretty(double value) {
		if (Double.isNaN(value)) {
			return "NaN";
		} else try {
			return siSuffixed(new BigDecimal(value, PRETTY_MODE).toEngineeringString());
		} catch (NumberFormatException e) {
			return ONE_DECIMAL_PLACE.format(value);
		}
	}

	final String logPoints(double value) {
		if (value <= 0)
			return "--";
		else
			return ONE_DECIMAL_PLACE.format(100*Math.log(value));
	}

	static final boolean DISPLAY_RAW_SCORES       = option("displayRawScores", false);
	static final boolean REPORT_RECIPROCAL_SCORES = option("reportReciprocalScores", false);

	final String rawScore(double value) {
		if (REPORT_RECIPROCAL_SCORES)
			return Double.toString(1/value);
		else
			return Double.toString(value);
	}

	final String score(double value) {
		if (SPREADSHEET_MODE)
			return spreadsheet(value);
		else if (DISPLAY_RAW_SCORES)
			return rawScore(value);
		else if (REPORT_RECIPROCAL_SCORES)
			return pretty(1/value);
		else
			return pretty(value);
	}

	static float max(float left, float right) {
		// Math.max doesn't handle NaN vs. Inifinity well
		if (left > right)
			return left;
		else if (right > left)
			return right;
		else
			return left;
	}

	/*
	 * Odds and ends
	 */

	static final long RANDOM_SEED = option("randomSeed", 123L);

	public static Random newRandom(){ return new Random(RANDOM_SEED); }

	static final boolean VERBOSE_LOGN = option("verboseLogN", false);

	protected static int log2(long n) {
		int result = 63 - Long.numberOfLeadingZeros(n);
		if (VERBOSE_LOGN)
			out().println("log2(" + n + ") = " + result);
		return result;
	}

	protected static long nlogn(long n) { return n * log2(n); }
	protected static int  nlogn( int n) { return n * log2(n); }

	protected static int  inverse_nlogn( int y) { return (int)inverse_nlogn((long)y); }
	protected static long inverse_nlogn(long y) {
		// Given y = x log x, returns an approximation for x.
		// There is no closed-form inverse for nlogn.  This is an approximation,
		// and it usually (but not always) under-estimates.

		if (y < _inverse_nlogn.length) {
			long x = _inverse_nlogn[(int)y];
			if (VERBOSE_LOGN)
				out().println("inverse_nlogn(" + y + ") = " + x + " from array; nlogn=" + x*log2(x));
			return x;
		}

		// For convenience, we write Lx instead of "log x".
		//
		int  Ly = log2(y);
		int LLy = log2(Ly);

		// Derivation:
		//       y = xLx
		//      Ly = Lx + LLx
		//         = Lx * (Lx+LLx)/Lx
		//
		// As an approximation, we say (Lx+LLx)/Lx is roughly (Ly+LLy)/Ly which we can compute.
		// Hence:
		//
		//      Ly ~ Lx * (Ly+LLy)/Ly
		//  so  Lx ~ Ly*Ly / (Ly+LLy)
		// and   x = y/Lx
		//
		int  Lx = Ly*Ly / (Ly + LLy);
		long  x = y/Lx;
		if (VERBOSE_LOGN)
			out().println("inverse_nlogn(" + y + ") = " + x + "; nlogn=" + x*log2(x));
		return x;
	}

	static final byte[] _inverse_nlogn = { 1, 1, 2, 3, 3, 3, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7,
	                                       7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 8, 9, 9, 9,10,10,
	                                       10,11,11,11,12,12,12,13,13,13,14,14,14,15,15,15,
	                                       15,15,15,15,15,15,15,15,15,15,15,15,15,15,15,15 };

	protected static int  sqrt( int y) { return (int)sqrt((long)y); }
	protected static long sqrt(long y) {
		return (long)Math.sqrt((double)y);
	}

}

