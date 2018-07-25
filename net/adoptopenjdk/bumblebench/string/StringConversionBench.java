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

import java.util.Locale;
import java.util.Random;

import net.adoptopenjdk.bumblebench.core.MiniBench;

import java.io.File;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.lang.StringBuilder;

/**
 * 
 * on z13 and newer z/Architectures with vector facilities enabled, string
 * toUpper and toLower can be accelerated by vector instructions.
 * 
 * This benchmark can generate strings of fixed/variable lengths and invokes
 * toUpper/toLower to measure string conversion perf.
 * 
 * The reported score is in terms of number of string conversions per sec.
 * String length is by default 877, and can be randomnized using options.
 * 
 * Note: adding to much randomnization to bumblebench workloads seems to make
 * the benchmark framework a little to aggressive in terms of estimating the
 * target threashold.
 * 
 * */

public final class StringConversionBench extends MiniBench {

	private static final int MAX_NUM_STRINGS = option("maxNumStrings", 1000);

	private static final int STRING_LENGTH = option("stringLength", 0);
	private static final boolean IS_TESTING_TO_UPPER = option("testUpper", false);
	private static final boolean IS_PSUEDO_RANDOM = option("random", false);

	private static Random rand;

	private static String[] strings;

	static {
		strings = new String[MAX_NUM_STRINGS];
		rand = new Random();
		int length;
		StringBuilder sb = new StringBuilder(1000);

		if (!IS_PSUEDO_RANDOM) {
			rand.setSeed(12345L);
		}

		if (STRING_LENGTH <= 0  || STRING_LENGTH > 1000) {
			for (int i = 0; i < MAX_NUM_STRINGS; i++) {
				length = rand.nextInt(1000);
				sb.setLength(0);
				for (int j = 0; j < length; ++j){
					sb.append(StringTestData.POSSIBLE_CHARS[rand.nextInt(StringTestData.POSSIBLE_CHARS.length)]);
				}
				strings[i] = sb.toString();
			}
		} else {
			length = STRING_LENGTH;
			for (int i = 0; i < MAX_NUM_STRINGS; ++i) {
				sb.setLength(0);
				for (int j = 0; j < length; ++j){
					sb.append(StringTestData.POSSIBLE_CHARS[rand.nextInt(StringTestData.POSSIBLE_CHARS.length)]);
				}
				strings[i] = sb.toString();
			}
		}
	}

	private String[] stringsToConvert;

	protected int maxIterationsPerLoop() {
		return MAX_NUM_STRINGS;
	}

	private void doConversion() {
		for (int i = 0; i < stringsToConvert.length; i++) {
			if (IS_TESTING_TO_UPPER) {
				stringsToConvert[i] = stringsToConvert[i].toUpperCase();
			} else {
				stringsToConvert[i] = stringsToConvert[i].toLowerCase();
			}
		}
	}

	protected long doBatch(long numLoops, int numIterationsPerLoop) throws InterruptedException {
		stringsToConvert = new String[numIterationsPerLoop];

		for (long loop = 0; loop < numLoops; loop++) {
			for (int i = 0; i < numIterationsPerLoop; i++){
				stringsToConvert[i] = strings[rand.nextInt(MAX_NUM_STRINGS)];
			}

			startTimer();
			doConversion();
			pauseTimer();
		}

		return numLoops * numIterationsPerLoop;
	}
}
