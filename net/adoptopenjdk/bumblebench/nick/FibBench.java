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

import java.text.SimpleDateFormat;
import java.io.Serializable;

public abstract class FibBench extends MicroBench {
	static final int FIB_COUNT         = option("fibCount", 20);
	static final int FIBGEN_ITERATIONS = option("fibGenIterations", 100000);

	/*
	 * JFIB (uses method refs)
	 */

	static Benchmark jfib_lambda = () -> {
		jfib(FIB_COUNT);
	};

	public static int jfib(int n){
		if (n == 0) {
			return 0;
		}
		if (n == 1) {
			return n;
		}
		I_method jfib_ref = FibBench::jfib;
		II_method add_ref = FibBench::add;
		return add_ref.call(jfib_ref.call(n-1), jfib_ref.call(n-2));
	}

	public static int add(int a, int b){
		return a + b;
	}
	
	/*
	 * Inner class fib normal (fib_class_normal)
	 */
	static I_method fib_class_normal_obj = new I_method() {
		public int call(int n) {
			if(n == 0 || n == 1){
				return n;
			}
			return call(n-1) + call(n-2);
		}
	};
	static Benchmark fib_class_normal = () -> {
		fib_class_normal_obj.call(FIB_COUNT);
	};
	
	/*
	 * JAVA FIB
	 */
	static Benchmark fib_normal = () -> {
		java_fib(FIB_COUNT);
	};
	public static int java_fib(int n){
		if(n == 0 || n == 1){
			return n;
		}
		return java_fib(n-1) + java_fib(n-2);
	}

	static I_method fib = (i) -> {
		if(i == 0){
			return 0;
		}else if(i == 1){
			return 1;
		}
		return (getfib().call(i-1) + getfib().call(i-2));
	};
	
	static I_method getfib() {
		return fib;
	}
	static Benchmark fibtest = () -> {
		fib.call(FIB_COUNT);
	};
	
	static I_method special_fib = (i) -> {
		VI_method custom_fib = () -> {
			if(i == 0){
				return 0;
			}else if(i == 1){
				return 1;
			}
			return (getspecial_fib().call(i-1) + getspecial_fib().call(i-2));
		};
		return custom_fib.call();
	};
	static I_method getspecial_fib() {
		return special_fib;
	}
	/* fib_custom */
	static Benchmark fibtest_custom = () -> {
		special_fib.call(FIB_COUNT);
	};
	
	static FibGen fibgen = (n) -> {
		if(n == 0){
			I_method r = (x) -> {
				return 0;
			};
			return r;
		} else if (n == 1) {
			I_method r = (x) -> {
				return n;
			};
			return r;
		}
		
		I_method m1 = (x) -> {
			return getfibgen().eval(x-1).call(x);
		};
		I_method m2 = (x) -> {
			return getfibgen().eval(x-2).call(x);
		};
		
		I_method r = (x) -> {
			return m1.call(n) + m2.call(n);
		};
		
		return r;
	};
	static FibGen getfibgen() {
		return fibgen;
	}
	/* Fibgen */
	static Benchmark fibtest_fibgen = () -> {
		fibgen.eval(FIB_COUNT).call(FIB_COUNT);
	};
	static Benchmark fibgen_only = () -> {
		for(int i = 0; i < FIBGEN_ITERATIONS; i++){
			fibgen.eval(FIB_COUNT);
		}
	};
}
