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

package net.adoptopenjdk.bumblebench.examples;

import java.util.Arrays;
import java.util.Random;

import net.adoptopenjdk.bumblebench.core.MiniBench;

public final class RandomSortBench extends MiniBench {

	// Illustrates a benchmark whose workload does not scale linearly in the
	// size of the input, so we adjust the size of the input to keep the
	// workload linear in the number of "iterations" requested per batch.
	//
	// This is rather esoteric, and not really crucial.  If you don't do this,
	// your benchmark will have the following undesirable properties:
	//  - It will scale nonlinearly with processor speed
	//  - It will give different scores for different batchTargetDuration settings

	final static int MAX_ARRAY_LENGTH = option("maxArrayLength", 1000000);

	protected int maxIterationsPerLoop(){ return nlogn(MAX_ARRAY_LENGTH); }

	int escaped;

	protected long doBatch(long numLoops, int numIterationsPerLoop) throws InterruptedException {
		Random r = newRandom();

		// Allocate an array such that sorting requires numIterationsPerLoop comparison operations
		int[] numbers = new int[inverse_nlogn(numIterationsPerLoop)];

		for (int count = 0; count < numLoops; count++) {
			for (int i = 0; i < numbers.length; i++) {
				numbers[i] = r.nextInt();
			}
			startTimer();
			Arrays.sort(numbers);
			pauseTimer();
		}

		escaped = numbers[0];
		return numIterationsPerLoop * numLoops;
	}

}

