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

public abstract class MiniBench extends MicroBench {

	protected abstract int maxIterationsPerLoop();
	protected abstract long doBatch(long numLoops, int numIterationsPerLoop) throws InterruptedException;

	static final boolean CALL_SYSTEM_GC = option("callSystemGC", true);
	static final boolean START_PAUSED   = option("startPaused", true); // Extremely fast workloads could become dominated by timer pause/start overhead
	static final boolean MAX_ITERATIONS_PER_LOOP_THRESHOLD_MODE = option("maxIterationsPerLoopThresholdMode", true);
	static final int MAX_ITERATIONS_PER_LOOP_THRESHOLD_FACTOR = option("maxIterationsPerLoopThresholdFactor", 2); // Must be >= 1. Increasing this factor lowers the threshold.

	protected long doBatch(long numIterations) throws InterruptedException {
		if (START_PAUSED)
			pauseTimer();

		int curMaxIterationsPerLoop = maxIterationsPerLoop();
		long numLoops = (numIterations-1) / curMaxIterationsPerLoop + 1;
		int numIterationsPerLoop;

		// Threshold Mode prevents numIterationsPerLoop from oscillating within a given range (this range is determined
		// by the threshold factor). The default value of 2 for the threshold factor prevents numIterationsPerLoop
		// from oscillating non-uniformly between the values of maxIterationsPerLoop() and (maxIterationsPerLoop() - 1) after
		// numIterations crosses numIterationsThreshold.
		if (MAX_ITERATIONS_PER_LOOP_THRESHOLD_MODE) {
			long numIterationsThreshold = (curMaxIterationsPerLoop * (curMaxIterationsPerLoop / MAX_ITERATIONS_PER_LOOP_THRESHOLD_FACTOR));
			if (numIterations < numIterationsThreshold) {
				numIterationsPerLoop = (int)(numIterations / numLoops);
				// Note: the above expression for numIterationsPerLoop can't overflow because
				// numIterationsPerLoop <= maxIterationsPerLoop() which is in the int range
			} else {
				numIterationsPerLoop = curMaxIterationsPerLoop;
			}
		} else {
			numIterationsPerLoop = (int)(numIterations / numLoops);
		}

		if (CALL_SYSTEM_GC)
			System.gc();
		if (VERBOSE)
			out().println(this.getClass().getSimpleName() + ".doBatch(" + numLoops + ", " + numIterationsPerLoop + ") // " + numLoops*numIterationsPerLoop + " total iterations");

		return doBatch(numLoops, numIterationsPerLoop);
	}
}
