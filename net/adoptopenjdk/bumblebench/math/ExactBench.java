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

package net.adoptopenjdk.bumblebench.math;

import net.adoptopenjdk.bumblebench.core.MicroBench;

public final class ExactBench extends MicroBench {

	static final boolean USE_FIXED_INCREMENT = option("useFixedIncrement", false);
	static final int     INCREMENT           = option("increment", 3);

	protected long doBatch(long numIterations) throws InterruptedException {
		int sum = 1;
		long i = 0;
		try {
			for (i = 0; i < numIterations; i++) {
				if (USE_FIXED_INCREMENT)
					sum = Math.addExact(sum, INCREMENT);
				else
					sum = Math.addExact(sum, (int)(i & INCREMENT));
			}
		} catch (ArithmeticException e) { }
		return i;
	}

}

