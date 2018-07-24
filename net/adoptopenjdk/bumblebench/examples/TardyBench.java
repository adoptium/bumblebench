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

import net.adoptopenjdk.bumblebench.core.MicroBench;

public final class TardyBench extends MicroBench {

	/* This test always misses the target duration.  It also demonstrates the
	 * use of a properties file.
	 */

	protected long doBatch(long numIterations) throws InterruptedException {
		for (long i = 0; i < numIterations; i++)
			Thread.sleep(2 * MicroBench.Options.BATCH_TARGET_DURATION); // Always late!
		return numIterations;
	}

}

