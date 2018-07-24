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

import net.adoptopenjdk.bumblebench.core.MiniBench;

public final class StringHashBench extends MiniBench {

	// Illustrates a MiniBench with a significant setup cost per loop

	static final int MAX_NUM_STRINGS = option("maxNumStrings", 10000);
	static final boolean SAME_STRING_EACH_TIME = option("sameStringEachTime", false);

	protected int maxIterationsPerLoop(){ return MAX_NUM_STRINGS; }

	long _counter = 0x1234567890L; // Big so that the number of digits doesn't change much during the run
	int  _escape; // To prevent the optimizer from removing the whole method

	int doHashes(String[] strings) {
		int total = 0;
		for (int i = 0; i < strings.length; i++)
			total += strings[i].hashCode();
		return total;
	}

	protected long doBatch(long numLoops, int numIterationsPerLoop) throws InterruptedException {
		// The main challenge here is that hashes are cached, so we can't hash
		// the same string twice and measure anything meaningful.  Thus, we need
		// many, many unique strings, and actually constructing and tracking all
		// those strings can take longer than hashing them.  We allocate them in
		// large batches to amortize the cost of pause and resume, but if the
		// batches are too large, we can get an OutOfMemoryError on 32-bit heaps.
		//
		String[] strings = new String[numIterationsPerLoop];
		for (long loop = 0; loop < numLoops; loop++) {
			for (int i = 0; i < strings.length; i++) {
				if (SAME_STRING_EACH_TIME)
					strings[i] = "Same string each time";
				else
					strings[i] = Long.toHexString(_counter++); // Picked toHexString because it's faster than toString
			}

			// The actual workload we want to measure is hiding in here:
			//
			startTimer();
			int total = doHashes(strings);
			pauseTimer();
			_escape = total;
		}
		return numLoops * strings.length;
	}

}

