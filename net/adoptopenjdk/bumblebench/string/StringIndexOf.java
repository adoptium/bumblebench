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
import java.lang.StringBuilder;

/**
 * 
 * String.indexOf can be accelerated by vector instructions.
 * 
 * This benchmark can generate strings of fixed/variable lengths and invokes
 * indexOf to measure performance.
 * 
 * The reported score is in terms of number of string indexOfs per sec.
 * 
 * */

public final class StringIndexOf extends MicroBench {

	private static final int MAX_ITERATIONS_PER_LOOP = option("maxIterations", 10000000);
	private static final int INDEX_OF_VALUE = option("indexOfValue", 10);
	private static int STRING_LENGTH = option("stringLength", 0);
	private static final boolean IS_PSUEDO_RANDOM = option("random",false);

	private static String string;
	private static char[] possibleChars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
		'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E',
		'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
		'Y', 'Z', ' ', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

	private static int value = 0;

	static {
		STRING_LENGTH = (STRING_LENGTH == 0) ? INDEX_OF_VALUE + 1 : STRING_LENGTH;

		Random rand = new Random();
		int length;
		StringBuilder sb = new StringBuilder(1000);

		if (!IS_PSUEDO_RANDOM) {
			rand.setSeed(12345L);
		}

		for (int j = 0; j < STRING_LENGTH; ++j){
			sb.append(possibleChars[rand.nextInt(possibleChars.length)]);
		}

		// If the options options specify a length smaller than the index,
		// the search character shouldn't be found and indexOf() will return 0
		if (INDEX_OF_VALUE < STRING_LENGTH) {
			sb.setCharAt(INDEX_OF_VALUE, '.');
		}

		string = sb.toString();
	}

	protected int maxIterationsPerLoop() {
		return MAX_ITERATIONS_PER_LOOP;
	}

	protected long doBatch(long numIterations) throws InterruptedException {
		for (long loop = 0; loop < numIterations; loop++) {
			value += string.indexOf('.');
		}
		return numIterations;
	}
}

