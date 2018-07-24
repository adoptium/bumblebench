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

import java.io.IOException;

public abstract class MicroBench extends BumbleBench {

	public static class Defaults {
		public static long BATCH_TARGET_DURATION = 1000L;

		static boolean TARGET_INCLUDES_PAUSES = true;
		static float   MIN_UNPAUSED_FACTOR    = 0.2F;
		static float   MAX_TIME_DILATION      = 10F;
		static boolean UNSPECIFIED_ESTIMATE   = false;
	}

	public static class Options {
		public static final long BATCH_TARGET_DURATION = option("batchTargetDuration", Defaults.BATCH_TARGET_DURATION);

		static final boolean TARGET_INCLUDES_PAUSES = option("targetIncludesPauses", Defaults.TARGET_INCLUDES_PAUSES);
		static final float   MIN_UNPAUSED_FACTOR    = option("minUnpausedFactor", Defaults.MIN_UNPAUSED_FACTOR);
		static final float   MAX_TIME_DILATION      = option("maxTimeDilation", Defaults.MAX_TIME_DILATION);
		static final boolean UNSPECIFIED_ESTIMATE   = option("unspecifiedEstimate", Defaults.UNSPECIFIED_ESTIMATE);
	}

	public MicroBench() {
		// load Options
		boolean targetIncludesPauses = Options.TARGET_INCLUDES_PAUSES;
	}

	float _unpausedFraction = 1F;

	protected abstract long doBatch(long numIterations) throws InterruptedException;

	protected final float attempt(float targetScore) throws InterruptedException {
		float iterationRate;
		if (Options.TARGET_INCLUDES_PAUSES) {
			// Scale down the targetIterations so they can finish in a shorter
			// unpaused time, thereby making the total elapsed time (which
			// includes pauses) hit the desired target.
			iterationRate = targetScore * Math.max(_unpausedFraction, Options.MIN_UNPAUSED_FACTOR);
		} else {
			iterationRate = targetScore;
		}

		long targetIterations = (long)(iterationRate * Options.BATCH_TARGET_DURATION / 1000F); // This could saturate, but I'll worry about that when we get computers that can do MAX_LONG calculations per second
		targetIterations = Math.max(targetIterations, 1);

		// The call to doBatch, wrapped as tightly as possible by System.nanoTime()
		//
		resetTimer();
		long startTime = System.nanoTime();
		long measuredIterations = doBatch(targetIterations);
		long returnTime = System.nanoTime();

		// Follow-up calculations.  Not time-critical.
		// 
		long stopTime = isTimerPaused()? _pauseStartTime : returnTime;
		long elapsedTime = stopTime - startTime;
		long unpausedTime = elapsedTime - _pauseTotalDuration;
		long measuredDuration = Options.TARGET_INCLUDES_PAUSES? elapsedTime : unpausedTime;
		float measuredRate = measuredIterations * 1.0e+9f / unpausedTime;

		// Update _unpausedFraction for next time
		if (elapsedTime > 0) {
			_unpausedFraction = (float)unpausedTime / (float)elapsedTime;
			if (Options.TARGET_INCLUDES_PAUSES
				&& elapsedTime / Options.BATCH_TARGET_DURATION > Options.MAX_TIME_DILATION
				&& Options.MIN_UNPAUSED_FACTOR / _unpausedFraction > Options.MAX_TIME_DILATION)
				throw new InterruptedException("Benchmark is spending too much time paused; try increasing option \"maxTimeDilation\" if you don't mind long-running batches");
		}

		if (VERBOSE) {
			out().println(
				  " elapsedTime=" + elapsedTime
				+ " unpausedTime=" + unpausedTime
				+ " targetIterations=" + targetIterations
				+ " measuredIterations=" + measuredIterations
				+ " measuredDuration=" + measuredDuration
				+ " measuredRate=" + measuredRate
				+ " targetScore=" + targetScore
				);
		}

		if (Options.UNSPECIFIED_ESTIMATE)
			measuredRate = (measuredRate >= targetScore)? UNSPECIFIED_SUCCESS : UNSPECIFIED_FAILURE;

		return measuredRate;
	}

	static final int LONG_BATCH_SECONDS = option("longBatchSeconds", 0);

	public void run() {
		super.run();
		if (LONG_BATCH_SECONDS > 0) {
			out().println("\n   -- LONG BATCH --");
			out().println("Press <Enter> to begin a " + LONG_BATCH_SECONDS + "-second batch...");
			try {
				in().readLine();
				out().println("Running for " + LONG_BATCH_SECONDS + " seconds...");
				long targetIterations = Math.max((long)(currentEstimatedScore() * LONG_BATCH_SECONDS), 1L);
				doBatch(targetIterations);
				out().println("...done.");
				// TODO: Report the score achieved during the long batch.  This would require timing the batch.
			} catch (IOException e) {
				out().println("ERROR waiting for <Enter>: " + e);
				e.printStackTrace(out());
			} catch (InterruptedException e) {
				// Don't care
			}
		}
	}

	int watchdogSeconds() {
		long expectedSeconds = MAX_WARMUP_SECONDS + BALLPARK_ITERATIONS*2 * Options.BATCH_TARGET_DURATION / 1000;
		return option("defaultWatchdogSeconds", (int)(2*expectedSeconds));
	}

	String extraReportHeader() { return super.extraReportHeader() + "\t%paused"; }
	String extraReportInfo() {
		String result = super.extraReportInfo();
		float pausedFraction = 1F - _unpausedFraction;
		if (pausedFraction > 0F)
			result = result + "\t" + percentage(pausedFraction);
		return result;
	}

	//
	// Pause/resume functionality
	// Note that both pause and resume are designed to be idempotent.  For
	// example, if you want the timer paused, just call pauseTimer and don't
	// worry about the current state; it will work.
	//

	static final boolean VERBOSE_PAUSE = option("verbosePause", false);

	private long _pauseStartTime, _pauseTotalDuration;
	private boolean _isTimerPaused;

	protected final boolean isTimerPaused(){ return _isTimerPaused; }

	protected final long startTimer() {
		long startTime = System.nanoTime();
		if (isTimerPaused()) {
			_pauseTotalDuration += startTime - _pauseStartTime;
			if (VERBOSE_PAUSE)
				out().println("- started at " + startTime + ": " + _pauseTotalDuration + "ms total pause duration -");
		}
		_isTimerPaused = false;
		return startTime;
	}

	protected final long pauseTimer() {
		long now = System.nanoTime();
		if (!isTimerPaused()) {
			_pauseStartTime = now;
			_isTimerPaused = true;
			if (VERBOSE_PAUSE)
				out().println("- paused  at " + _pauseStartTime + " -");
		}
		return now;
	}

	final void resetTimer() {
		_pauseStartTime = _pauseTotalDuration = 0;
		_isTimerPaused = false;
	}

}

