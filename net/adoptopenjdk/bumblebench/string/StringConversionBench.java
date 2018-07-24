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

/**
 * 
 * on z13 and newer z/Architectures with vector facilities enabled, string
 * toUpper and toLower can be accelerated by vector instructions.
 * 
 * This benchmark can generate strings of fixed/variable lengths and invokes
 * toUpper/toLower to measure string conversion perf.
 * 
 * The reported score is in terms of number of string conversions per sec.
 * String legnth is by default 877, and can be randomnized using options.
 * 
 * Note: adding to much randomnization to bumblebench workloads seems to make
 * the benchmark framework a little to aggressive in terms of estimating the
 * target threashold.
 * 
 * 
 * Convertable ranges:
 * 
 * A to Z [0x41, 0x5a] a to z [0x61, 0x7a]
 * 
 * [0xc0, 0xde] excluding 0xd7 [0xe0, 0xfe] excluding 0xf7
 * 
 * */

public final class StringConversionBench extends MiniBench {

	private static final int MAX_NUM_STRINGS = option("maxNumStrings", 1000);
	private static final int MIN_NUM_STRINGS = option("minNumStrings", 1);
	private static final int MAX_CHAR = option("maxChar", 0x00ff);
	private static final int MIN_CHAR = option("minChar", 0x0000);

	// 877 is just a reasonably big prime number.
	private static final int MAX_STRING_LENGTH = option("maxStringLen", 877);
	private static final int MIN_STRING_LENGTH = option("minStringLen", 877);

	private static final boolean IS_TESTING_TO_UPPER = option("testUpper",
			false);
	private static final boolean IS_RANDOM_GEN = option("random", true);
	private static final boolean IS_DEBUG = option("debug", false);
	private static final boolean IS_VERBOSE = option("verbose", false);
	private static final boolean IS_VERIFY = option("verifyResult", true);
	private static final boolean IS_BYTE_ARRAY_TEST = option("byteArrayTest",
			false);

	static {
		System.out.println("Testing "
				+ ((IS_TESTING_TO_UPPER) ? "toUpper" : "toLower"));

		if (MAX_CHAR > 0x0ff) {
			System.out
					.println("this bench is not able to handle max_char > 0xff, clippig it to 0xff");
		}
	}

	// dump converted char to a file
	private static final boolean IS_DUMP_CONVERTED = option(
			"dumpConvertedChar", false);

	private int stringLength = 51;
	private int numString = 256; // number of strings to convert per iteration
	private String _escape; // To prevent the optimizer from removing the whole
							// method

	private byte[] _escapeByte;

	protected int maxIterationsPerLoop() {
		return MAX_NUM_STRINGS;
	}

	private void printArrays(byte[] before, byte[] after) {
		System.out.println("before: " + Arrays.toString(before));
		System.out.println("after: " + Arrays.toString(after));
	}

	// this will be a recognized function handled by the codegen directly
	// for compressed string conversion test
	public byte[] byteArrayToUpperWrapper(byte[] input, byte[] output) {
		return byteArrayToUpper(input, output);
	}

	public byte[] byteArrayToLowerWrapper(byte[] input, byte[] output) {
		return byteArrayToLower(input, output);

	}

	private final byte[] byteArrayToUpper(byte[] input, byte[] output) {
		return null;
	}

	private final byte[] byteArrayToLower(byte[] input, byte[] output) {
		return null;
	}

	/**
	 * This temporary hack function is intended to test the JIT's ability to do
	 * case conversion on byte arrays. each byte (0x00 to 0xff) is a char in the
	 * ascii world.
	 * */
	private long runByteArrayTest() {
		long counter = 0;
		Random rand = new Random();
		int numString = 256;
		int stringLength = 0;

		File diffFile = new File("./conversionDiff");
		PrintWriter writer = null;

		if (IS_DUMP_CONVERTED) {
			if (!diffFile.exists()) {

				try {
					writer = new PrintWriter(diffFile);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

		for (int i = 0; i < numString; i++) {
			stringLength = (IS_RANDOM_GEN) ? rand
					.nextInt((MAX_STRING_LENGTH - MIN_STRING_LENGTH) + 1)
					+ MIN_STRING_LENGTH : 256;

			byte[] byteArray = new byte[stringLength];
			byte[] converted = null;

			// construct char array sequentially/randomly
			for (int j = 0; j < stringLength; j++) {
				byteArray[j] = (byte) (rand.nextInt((MAX_CHAR - MIN_CHAR) + 1) + MIN_CHAR);
			}
			_escapeByte = byteArray;

			if (IS_TESTING_TO_UPPER) {
				startTimer();
				converted = byteArrayToUpperWrapper(byteArray,
						new byte[byteArray.length]);
				pauseTimer();
			} else {
				startTimer();
				converted = byteArrayToLowerWrapper(byteArray,
						new byte[byteArray.length]);
				pauseTimer();
			}

			// compare strings one char at a time
			if (IS_VERIFY) {
				for (int k = 0; k < stringLength; k++) {
					int b = (int) (byteArray[k] & 0xff);
					int a = (int) (converted[k] & 0xff);

					if (byteArray.length != converted.length) {
						assert false : "byte array legnth mismatch.";
					}

					if (writer != null && a != b) {
						writer.print(b + " -> " + a + "\n");
					}

					if (IS_TESTING_TO_UPPER) {
						if (((b >= 0x0061 && b <= 0x007a) || (b >= 0x00e0 && b <= 0x00fe))
								&& b != 0x00f7) {

							// inside valid range. can convert to upper
							assert (b - a) == 0x0020 : "Should have converted this char: "
									+ (int) b
									+ " to "
									+ (int) a
									+ " at index "
									+ k;
						} else {
							assert a == b : "should not have converted this char: "
									+ (int) b
									+ " to "
									+ (int) a
									+ " at index "
									+ k;
						}
					} else {
						if (((b >= 0x0041 && b <= 0x005a) || (b >= 0x00c0 && b <= 0x00de))
								&& b != 0x00d7) {
							// inside valid range. can convert to lower
							assert (a - b) == 0x0020 : "Should have converted this char: "
									+ (int) b
									+ " to "
									+ (int) a
									+ " at index "
									+ k;
						} else {
							assert a == b : "should not have converted this char: "
									+ (int) b
									+ " to "
									+ (int) a
									+ " at index "
									+ k;
						}
					}
				}
			}
		}

		if (writer != null) {
			writer.close();
		}

		return 1;
	}

	/**
	 * This method converts 256 strings to upper or lower. Each String is of
	 * fixed length (877 chars) unless otherwise specified in via options.
	 * 
	 * @return returns 1 on success, meaning it's finished 1 run.
	 * */
	private long runTest() {
		long counter = 0;
		Random rand = new Random();
		/*
		 * numString = (IS_RANDOM_GEN) ? rand .nextInt((MAX_NUM_STRINGS -
		 * MIN_NUM_STRINGS) + 1) + MIN_NUM_STRINGS : 256;
		 */

		numString = 256;
		File diffFile = null;
		PrintWriter writer = null;

		if (IS_DEBUG) {
			try {
				diffFile = new File("./conversionDiff");
				writer = new PrintWriter(diffFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		for (int i = 0; i < numString; i++) {
			String converted = null;
			String inputStr = null;

			/*stringLength = (IS_RANDOM_GEN) ? rand
					.nextInt((MAX_STRING_LENGTH - MIN_STRING_LENGTH) + 1)
					+ MIN_STRING_LENGTH : 256;
*/
			stringLength = 256;
			char[] charArray = new char[stringLength];

			// construct char array sequentially/randomly
			for (int j = 0; j < stringLength; j++) {
				if (IS_RANDOM_GEN) {
					charArray[j] = (char) (rand
							.nextInt((MAX_CHAR - MIN_CHAR) + 1) + MIN_CHAR);
				} else {
					// sequential
					charArray[j] = (char) (i * 256 + j);
				}
			}

			inputStr = String.valueOf(charArray);

			if (IS_DEBUG)
				System.out.println(inputStr);

			if (IS_TESTING_TO_UPPER) {
				startTimer();
				converted = inputStr.toUpperCase();
				pauseTimer();
			} else {
				startTimer();
				converted = inputStr.toLowerCase();
				pauseTimer();
			}

			_escape = converted;

			// compare strings one char at a time
			if (IS_VERIFY) {
				char[] before = inputStr.toCharArray();
				char[] after = converted.toCharArray();

				if (before.length != after.length) {
					assert false : "string length mismatch...";
				}
				for (int k = 0; k < stringLength; k++) {
					int b = (int) before[k];
					int a = (int) after[k];

					if (writer != null && a != b) {
						writer.print(b + " -> " + a + "\n");
					}
					if (IS_TESTING_TO_UPPER) {
						if (((b >= 0x0061 && b <= 0x007a) || (b >= 0x00e0 && b <= 0x00fe))
								&& b != 0x00f7 && b != 0x00b5 && b != 0x00df) {
							// inside valid range. can convert to upper
							assert (b - a) == 0x0020 : "Should have converted this char: "
									+ (int) b;
						} else if (b == 0x00b5) {
							// mu should become 0x039c
							assert (a == 0x039c) : "wrong conversion of mu";
							if (IS_VERBOSE) {
								System.out.println("mu conversion ok");
							}
						} else if (b == 0x00f7) {
							// division sign
							assert (a == b) : "should not have converted the division sign";
							if (IS_VERBOSE) {
								System.out.println("division conversion ok");
							}
						} else if (b == 0x00df) {
							// Greek sharp s becomes 0x0053 0x0053
							k++;
							int tmp = (int) after[k];
							assert (a == 0x0053 && tmp == 0x0053) : "wrong conversion of greek sharp S";
							if (IS_VERBOSE) {
								System.out
										.println("Greek sharp S conversion ok");
							}
						} else {
							assert a == b : "should not have converted this char: "
									+ (int) b;
						}
					} else {
						if (((b >= 0x0041 && b <= 0x005a) || (b >= 0x00c0 && b <= 0x00de))
								&& b != 0x00d7) {
							// inside valid range. can convert to lower
							assert (a - b) == 0x0020 : "Should have converted this char: "
									+ (int) b;
						} else {
							assert a == b : "should not have converted this char: "
									+ (int) b;
						}
					}
				}
			}
		}

		if (writer != null) {
			writer.close();
		}

		return 1;
	}

	protected long doBatch(long numLoops, int numIterationsPerLoop)
			throws InterruptedException {

		if (IS_VERBOSE) {
			String msg = "JVM Language: " + Locale.getDefault().getLanguage()
					+ ". ";
			msg += IS_TESTING_TO_UPPER ? " Testing to Upper. "
					: " Testing to Lower. ";
			msg += (IS_RANDOM_GEN) ? " Random Gen. " : " Not Random. ";
			msg += "Character range [" + MIN_CHAR + " , " + MAX_CHAR + "].";
			msg += "Number of iterations: " + numLoops + ".";

			System.out.println(msg);
		}
		long counter = 0;

		for (long loop = 0; loop < numLoops; loop++) {
			if (IS_BYTE_ARRAY_TEST) {
				counter += runByteArrayTest();
			} else {
				counter += runTest();
			}
		}

		return counter;
	}
}
