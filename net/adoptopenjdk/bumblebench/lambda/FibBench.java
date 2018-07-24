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

package net.adoptopenjdk.bumblebench.lambda;

import net.adoptopenjdk.bumblebench.core.MicroBench;
import net.adoptopenjdk.bumblebench.lambda.interfaces.Binary;
import net.adoptopenjdk.bumblebench.lambda.interfaces.Nullary;
import net.adoptopenjdk.bumblebench.lambda.interfaces.Unary;

public abstract class FibBench extends MicroBench {
	public static final int FIB_ARGUMENT = option("fibArgument", 20);

	public final static class Vanilla extends FibBench {

		protected long doBatch(long numIterations) throws InterruptedException {
			for (long i = 0; i < numIterations; i++)
				fib(FIB_ARGUMENT);
			return numIterations;
		}

		static int fib(int n) {
			if (n <= 1)
				return n;
			else
				return fib(n-1) + fib(n-2);
		}

	}

	public final static class InnerClass extends FibBench {

		protected long doBatch(long numIterations) throws InterruptedException {
			for (long i = 0; i < numIterations; i++)
				fib.call(FIB_ARGUMENT);
			return numIterations;
		}

		static final Unary fib = new Unary() {
			public int call(int n) {
				if (n <= 1)
					return n;
				else
					return getFib().call(n-1) + getFib().call(n-2);
			}
		};

		static final Unary getFib(){ return fib; }

	}

	public final static class Lambda extends FibBench {

		protected long doBatch(long numIterations) throws InterruptedException {
			for (long i = 0; i < numIterations; i++)
				fib.call(FIB_ARGUMENT);
			return numIterations;
		}

		static final Unary fib = (n) -> {
			if (n <= 1)
				return n;
			else
				return getFib().call(n-1) + getFib().call(n-2);
		};

		static final Unary getFib(){ return fib; }

	}

	public final static class LocalLambda extends FibBench {

		protected long doBatch(long numIterations) throws InterruptedException {
			for (long i = 0; i < numIterations; i++)
				fib.call(FIB_ARGUMENT);
			return numIterations;
		}

		static final Unary fib = (n) -> {
			Nullary localFib = () -> {
				if (n <= 1)
					return n;
				else
					return getFib().call(n-1) + getFib().call(n-2);
			};
			return localFib.call();
		};

		static final Unary getFib(){ return fib; }

	}

	public final static class DynamicLambda extends FibBench {

		protected long doBatch(long numIterations) throws InterruptedException {
			for (long i = 0; i < numIterations; i++)
				generateFib(FIB_ARGUMENT).call();
			return numIterations;
		}

		static final Nullary generateFib(int n) {
			Nullary result;
			if (n <= 1) {
				result = () -> { return n; };
			} else {
				result = () -> { return generateFib(n-1).call() + generateFib(n-2).call(); };
			}
			return result;
		}

	}

	public final static class LocalMethodReferences extends FibBench {

		protected long doBatch(long numIterations) throws InterruptedException {
			for (long i = 0; i < numIterations; i++)
				fib(FIB_ARGUMENT);
			return numIterations;
		}

		static int fib(int n) {
			if (n <= 1)
				return n;
			Unary  fibRef = LocalMethodReferences::fib;
			Binary addRef = FibBench::add;
			return addRef.call(fibRef.call(n-1), fibRef.call(n-2));
		}

	}

	public static int add(int a, int b){
		return a + b;
	}

}

