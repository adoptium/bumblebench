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

import java.text.DecimalFormat;
import java.util.concurrent.Semaphore;

public class HumbleBench extends MiniBench {

	private static final int LOAD_FACTOR = option("loadFactor", 300);
	private static final boolean TIME_SETUP = option("timeSetup", false);
	private static final int FANOUT;

	private DelayThread _delayThread;
	private int _workloadIndex;
	private final AbstractWorkload[] _workloads;

	static {
		int defaultFanout = Math.min(12, LOAD_FACTOR);
		int requestedFanout = option("fanout", defaultFanout);
		FANOUT = Math.min(requestedFanout, LOAD_FACTOR);

		float targetUnpausedFraction = (float)FANOUT / (float)LOAD_FACTOR;
		float targetPausedFraction = 1.f - targetUnpausedFraction;

		DecimalFormat dfmt = new DecimalFormat("0.0");
		System.out.println(
			"HumbleBench target %paused: "
			+ String.format("%5s", dfmt.format(100 * targetPausedFraction))
			+ "%");

		MicroBench.Defaults.TARGET_INCLUDES_PAUSES = false;
		MicroBench.Defaults.BATCH_TARGET_DURATION =
			(int)(1000 - (1000 - 120) * targetPausedFraction / 0.96);
		if (MicroBench.Defaults.BATCH_TARGET_DURATION < 80) {
			MicroBench.Defaults.BATCH_TARGET_DURATION = 80;
		}

		System.out.println(
			"HumbleBench set default batchTargetDuration to "
			+ MicroBench.Defaults.BATCH_TARGET_DURATION);
	}

	public HumbleBench(Class<?> workloadClass) {
		_delayThread = new DelayThread();
		_workloadIndex = 0;
		_workloads = new AbstractWorkload[FANOUT];
		for (int i = 0; i < FANOUT; i++) {
			Object o = newInstanceOfFreshlyLoadedClass(workloadClass);
			_workloads[i] = (AbstractWorkload)o;
		}
	}

	public static abstract class AbstractWorkload {
		public abstract void doBatch(HumbleBench bench, int numIterations);
	}

	@Override
	protected int maxIterationsPerLoop() {
		return 8192;
	}

	protected void setup(int numIterations) { }

	@Override
	protected final long doBatch(long numLoops, int numIterationsPerLoop)
		throws InterruptedException
	{
		long startTime = System.nanoTime();
		long workloadTime = 0;
		int workloadIndex = _workloadIndex;
		int fanout = FANOUT;
		for (long i = 0; i < numLoops; i++) {
			if (!TIME_SETUP) {
				setup(numIterationsPerLoop);
			}

			AbstractWorkload workload = _workloads[workloadIndex++];
			if (workloadIndex >= fanout) {
				workloadIndex -= fanout;
			}

			long loopStart = startTimer();
			if (TIME_SETUP) {
				setup(numIterationsPerLoop);
			}
			workload.doBatch(this, numIterationsPerLoop);
			long loopEnd = pauseTimer();

			workloadTime += loopEnd - loopStart;

			if (LOAD_FACTOR * workloadTime > FANOUT * (loopEnd - startTime)) {
				long targetTime = startTime + workloadTime * LOAD_FACTOR / FANOUT;
				_delayThread.delay(targetTime - loopEnd);
			}
		}

		_workloadIndex = workloadIndex;

		return numLoops * numIterationsPerLoop;
	}

	private static class DelayThread extends Thread {
		private static int DELAY_FANOUT = option("delayFanout", 32);
		private static int MIN_DELAY_MS = option("minDelay", 5);
		private static long MIN_DELAY_NS = MIN_DELAY_MS * 1000000L;

		private final Semaphore _semSpin;
		private final Semaphore _semWork;
		private final AbstractDelay[] _delays;
		private long _duration;

		public DelayThread() {
			_semSpin = new Semaphore(1);
			acquire(_semSpin);

			_semWork = new Semaphore(1);
			acquire(_semWork);

			_delays = new AbstractDelay[DELAY_FANOUT];
			for (int i = 0; i < DELAY_FANOUT; i++) {
				Object o = newInstanceOfFreshlyLoadedClass(Delay.class);
				_delays[i] = (AbstractDelay)o;
			}

			setDaemon(true);
			start();
		}

		public void delay(long duration) {
			if (duration < MIN_DELAY_NS) {
				return;
			}

			_duration = duration;
			_semSpin.release();
			acquire(_semWork);
		}

		@Override
		public void run() {
			int delayIndex = 0;
			while (true) {
				acquire(_semSpin);
				if (_duration <= 0) {
					throw new AssertionError("_duration");
				}

				_delays[delayIndex++].delay(System.nanoTime(), _duration);
				if (delayIndex >= DELAY_FANOUT) {
					delayIndex -= DELAY_FANOUT;
				}

				_duration = 0;
				_semWork.release();
			}
		}

		private static void acquire(Semaphore sem) {
			try {
				sem.acquire();
			} catch (InterruptedException exc) {
				throw new RuntimeException(exc);
			}
		}

		public static abstract class AbstractDelay {
			public abstract void delay(long start, long duration);
		}

		public static class Delay extends AbstractDelay {
			@Override
			public void delay(long start, long duration) {
				while (System.nanoTime() - start < duration) { }
			}
		}
	}

}
