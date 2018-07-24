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

public final class StartupBench extends MicroBench {

	protected final long doBatch(long numIterations) throws InterruptedException {
		verbose("Starting " + numIterations + " iterations");
		for (long i = 0; i < numIterations; i++) {
			// Program a worker to do a series of batches and then stop
			reallyVerbose("Create worker to do " + BATCHES_PER_CLASS + " batches of " + ITERATIONS_PER_BATCH);
			BumbleBench workerInstance = (BumbleBench)newInstanceOfFreshlyLoadedClass(_workloadClass);
			workerInstance.makeWorker(BATCHES_PER_CLASS+1);
			float target = (float)ITERATIONS_PER_BATCH;
			for (int batch = 0; batch < BATCHES_PER_CLASS; batch++)
				workerInstance._targetScores.put(target);
			workerInstance._targetScores.put(Float.NaN);

			// Run the worker
			startTimer();
			try {
				workerInstance.bumbleMain();
			} catch (InterruptedException e) {
				throw e;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			pauseTimer();

			// Collect the results
			float resultTotal = 0F;
			for (int batch = 0; batch < BATCHES_PER_CLASS; batch++)
				{
				float result = (float)workerInstance._resultScores.take();
				reallyVerbose("  Batch " + batch + " result " + result);
				resultTotal += result;
				}

			reallyVerbose("Worker did " + resultTotal + " iterations");
		}
		verbose("Finished " + numIterations + " iterations");
		return numIterations;
	}

	static final int ITERATIONS_PER_BATCH = option("iterationsPerBatch", 500);
	static final int BATCHES_PER_CLASS    = option("batchesPerClass", 5);

	static final boolean VERBOSE_STARTUP  = option("verboseStartup", false);
	static final boolean REALLY_VERBOSE_STARTUP  = option("reallyVerboseStartup", false);

	final Class _workloadClass;

	private StartupBench(Class<BumbleBench> workloadClass) {
		_workloadClass = workloadClass;
	}

	static StartupBench create(Class<BumbleBench> workloadClass) {
		return new StartupBench(workloadClass);
	}

	void verbose(String message) {
		if (VERBOSE_STARTUP)
			out().println(message);
	}

	void reallyVerbose(String message) {
		if (REALLY_VERBOSE_STARTUP)
			out().println(message);
	}

}

