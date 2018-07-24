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

import java.util.Random;

import net.adoptopenjdk.bumblebench.core.MicroBench;

public final class NoisyBench extends MicroBench {

	final static float MAX_SLEEP_FRACTION = option("maxSleepFraction", 0.1f);

	protected long doBatch(long numIterations) throws InterruptedException {
		double argument = 0.1;
		for (long i = 0; i < numIterations; i++)
			argument = 4.6 * Math.sin(argument); // Chaos!
		int maxSleepTime = (int)(MicroBench.Options.BATCH_TARGET_DURATION * MAX_SLEEP_FRACTION);
		Thread.sleep(r.nextInt(maxSleepTime));
		return numIterations;
	}

	// NOTE: THIS USE OF Random IS NOT RECOMMENDED.
	//
	// The purpose of this benchmark is to test how BumbleBench responds to a
	// fluctuating benchmark.  Unless you are masochistic, you probably want to
	// inherit MiniBench and call BumbleBench.newRandom at the start of your
	// doBatch method to allocate a random number generator that will
	// return the same pseudorandom sequence in every batch, thereby giving very
	// predictable results.
	//
	// See RandomSortBench for a better example to follow.
	//
	final static Random r = newRandom();

}
