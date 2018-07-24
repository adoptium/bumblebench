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

public final class ParallelBench extends BumbleBench {

	final WorkerThread[] _workerThreads;

	public enum Style { MIN, AVERAGE, SUM };

	final Style AGGREGATION_STYLE = Style.valueOf(option("aggregationStyle", "AVERAGE"));

	protected float attempt(float targetScore) throws InterruptedException {
		Float instanceTargetScore = (AGGREGATION_STYLE == Style.SUM)? (targetScore / _workerThreads.length) : targetScore;
		for (WorkerThread w: _workerThreads)
			w._workload._targetScores.put(instanceTargetScore);
		switch (AGGREGATION_STYLE) {
			case MIN:
				float worstScore = Float.POSITIVE_INFINITY;
				for (WorkerThread w: _workerThreads)
					worstScore = Math.min(worstScore, w._workload._resultScores.take());
				return worstScore;
			case AVERAGE:
			case SUM:
				float sum = 0F;
				for (WorkerThread w: _workerThreads)
					sum += w._workload._resultScores.take();
				return (AGGREGATION_STYLE == Style.SUM)? sum : (sum / _workerThreads.length);
		}
		throw new RuntimeException("Unexpected aggregation style " + AGGREGATION_STYLE);
	}

	public static ParallelBench create(int numThreads, Class class0) throws InterruptedException {
		return new ParallelBench(numThreads, (BumbleBench)newInstance(class0));
	}

	protected ParallelBench(int numThreads, BumbleBench instance0) {
		super(instance0._name);
		instance0.makeWorker(1);
		WorkerThread[] workerThreads = new WorkerThread[numThreads];
		workerThreads[0] = new WorkerThread(instance0);

		Class class0 = instance0.getClass();
		for (int i = 1; i < workerThreads.length; i++) {
			BumbleBench workerInstance = (BumbleBench)newInstanceOfPossiblyFreshlyLoadedClass(class0, CLASS_PER_INSTANCE);
			workerInstance.makeWorker(1);
			workerThreads[i] = new WorkerThread(workerInstance);
		}
		_workerThreads = workerThreads;
	}

	/** Set classPerInstance if your benchmark has mutable static data.  This
	 * will cause the framework to load a separate copy of your class for each
	 * benchmark instance, each with its own copy of the static data.
	 *
	 * This mode is not recommended because it can cause the jit to do weird
	 * things like compile the exact same code repeatedly for each instance.
	 * It's meant as a workaround to get easy parallel runs of a benchmark that
	 * was not designed with parallelism in mind.  If you care about parallel
	 * performance measurement, it's better to write the benchmark to use
	 * instance variables instead of statics.
	 *
	 * Note that only the main benchmark class is loaded multiple times.  If
	 * your benchmark uses other global data, it probably needs to be modified
	 * in order to run in parallel.
	 */
	final static boolean CLASS_PER_INSTANCE = option("classPerInstance", false);

	void startWorkers() {
		// Start the workers after the constructor is finished so we can be sure
		// the final fields will be visible to the worker threads.
		for (WorkerThread w: _workerThreads)
			w.start();
	}

	void stopWorkers() throws InterruptedException {
		for (WorkerThread w: _workerThreads)
			w.interrupt();
		for (WorkerThread w: _workerThreads)
			w.join();
	}

	public void bumbleMain() throws Exception {
		startWorkers();
		super.bumbleMain();
		stopWorkers();
	}
}

