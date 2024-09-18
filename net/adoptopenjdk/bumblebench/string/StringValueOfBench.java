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

package net.adoptopenjdk.bumblebench.string;

import net.adoptopenjdk.bumblebench.core.MicroBench;

import java.util.Random;

/**
 *
 * String.valueOf can be accelerated by vector instructions.
 *
 * This benchmark generates a char array of fixed lengths and invokes
 * valueOf to measure performance.
 *
 * The reported score is in terms of number of string valueOfs per sec.
 *
 * */

public final class StringValueOfBench extends MicroBench {

	private static final int MAX_ITERATIONS_PER_LOOP = option("maxIterations", 10000000);

	private static final int arraySize = 1024;
	private static char[] chars;

	private static int value = 0;

	static {
		Random rand = new Random();
		int length;

		chars = new char[arraySize];
		for (int i = 0; i < arraySize; i++) {
			chars[i] = StringTestData.POSSIBLE_CHARS[rand.nextInt(StringTestData.POSSIBLE_CHARS.length)];
		}
	}

	protected int maxIterationsPerLoop() {
		return MAX_ITERATIONS_PER_LOOP;
	}

	protected long doBatch(long numIterations) throws InterruptedException {
		for (long loop = 0; loop < numIterations; loop++) {
			String x = String.valueOf(chars, 0, ((int)loop) % arraySize);
			value += x.length();
		}
		return numIterations;
	}
}
