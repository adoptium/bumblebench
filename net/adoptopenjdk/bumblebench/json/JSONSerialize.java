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

package net.adoptopenjdk.bumblebench.json;

import java.io.IOException;

import org.boon.IO;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.adoptopenjdk.bumblebench.core.MicroBench;

public final class JSONSerialize extends MicroBench {
	static int BYTE_ARRAY_LENGTH = option("length", 32000);
	static int maxIter = option("maxIter", 1000000);
	static boolean verbose = option("verbose", false);

	/**
	 * parser types:
	 * boon
	 * json4j
	 * gson
	 * jackson
	 * json-fast
	 */

	static String parser = option("parser", "boon");

	/**
	 * Input JSON files available:
	 * simple
	 * complex (nested)
	 * mtg
	 * sf
	 * 512b
	 * 2k
	 * 8k
	 * 32k
	 * 512k
	 * 1m
	 * 4m
	 * 8m
	 * 32m
	 */
	private static String inputJSON;
	private static String jsonType = option("input", "simple");;

	// Max allowable value of numIterationsPerLoop
	protected int maxIterationsPerLoop() {
		return maxIter;
	}

	private static void readJSON() {
		switch (jsonType) {
		case "simple":
			break;
		case "complex":
			break;
		case "mtg":
			break;
		case "sf":
			break;
		case "512b":
			break;
		case "2k":
			break;
		case "8k":
			break;
		case "32k":
			break;
		case "512k":
			break;
		case "1m":
			break;
		case "4m":
			break;
		case "8m":
			break;
		case "32m":
			break;
		default:
		}
		inputJSON = IO.read(jsonType);
	}

	static {
		readJSON();
	}

	@Override
	protected long doBatch(long numIterations) throws InterruptedException {
		pauseTimer();
		for (int inner = 0; inner < numIterations; inner++) {

			switch (parser) {
			case "json4j":
				startTimer();
				pauseTimer();
				break;
			case "gson":
				startTimer();
				pauseTimer();
				break;
			case "jackson":
				startTimer();
				pauseTimer();
				break;
			case "boon":
				startTimer();
				pauseTimer();
				break;
			case "ibm":
				startTimer();
				pauseTimer();
				break;
			case "infoQ":
				startTimer();
				pauseTimer();
				break;
			default:
				startTimer();
				pauseTimer();
				break;
			}
		}

		return numIterations;
	}
}
