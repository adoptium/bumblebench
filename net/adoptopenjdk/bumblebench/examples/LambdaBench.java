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

public final class LambdaBench extends MicroBench {

	static Fib makeFib() {
		return (int arg) -> {
			if (arg < 1)
				return 1;
			else
				return makeFib().invoke(arg-1) + makeFib().invoke(arg-2);
		};
	}

	static final int FIB_ARGUMENT = option("fibArgument", 20);

	protected long doBatch(long numIterations) throws InterruptedException {
		for (long i = 0; i < numIterations; i++)
			makeFib().invoke(FIB_ARGUMENT);
		return numIterations;
	}

}

interface Fib {
	int invoke(int arg);
}

