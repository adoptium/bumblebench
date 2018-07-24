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

abstract public class MethodDispatch extends MicroBench {

	public static final int DISPATCH_COUNT 		= 10000;
	public static final int DISPATCH_NOOP_COUNT = 1000000;
	
		
	/* Used for complicated math in mdispatch */
	static int i = 0;
	static int j = 0;
	
	static V_method dispatch_class_obj = new V_method() {
		
		@Override
		public void call() {
			if(i++ > j++){
				i = i/j;
			}
			if(j > 100000){
				j = 1;
			}
		}
	};
	static Benchmark dispatch_class = () -> {
		for(int i = 0; i < DISPATCH_COUNT; i++){
			dispatch_class_obj.call();
			dispatch_class_obj.call();
			dispatch_class_obj.call();
			dispatch_class_obj.call();
			dispatch_class_obj.call();
			dispatch_class_obj.call();
			dispatch_class_obj.call();
			dispatch_class_obj.call();
			dispatch_class_obj.call();
			dispatch_class_obj.call();
			dispatch_class_obj.call();
			dispatch_class_obj.call();
			dispatch_class_obj.call();
			dispatch_class_obj.call();
			dispatch_class_obj.call();
			dispatch_class_obj.call();
			dispatch_class_obj.call();
			dispatch_class_obj.call();
			dispatch_class_obj.call();
			dispatch_class_obj.call();
		}
	};

	static V_method dispatch_class_small_obj = new V_method() {
		public void call() {
			i++;
		}
	};
	static Benchmark dispatch_class_small = () -> {
		for(int i = 0; i < DISPATCH_COUNT; i++){
			dispatch_class_small_obj.call();
			dispatch_class_small_obj.call();
			dispatch_class_small_obj.call();
			dispatch_class_small_obj.call();
			dispatch_class_small_obj.call();
			dispatch_class_small_obj.call();
			dispatch_class_small_obj.call();
			dispatch_class_small_obj.call();
			dispatch_class_small_obj.call();
			dispatch_class_small_obj.call();
			dispatch_class_small_obj.call();
			dispatch_class_small_obj.call();
			dispatch_class_small_obj.call();
			dispatch_class_small_obj.call();
			dispatch_class_small_obj.call();
			dispatch_class_small_obj.call();
			dispatch_class_small_obj.call();
			dispatch_class_small_obj.call();
			dispatch_class_small_obj.call();
			dispatch_class_small_obj.call();
		}
	};

	static V_method dispatch_class_noop_obj = new V_method() {
		public void call() {
		}
	};
	static Benchmark dispatch_class_noop = () -> {
		for(int i = 0; i < DISPATCH_COUNT; i++){
			dispatch_class_noop_obj.call();
			dispatch_class_noop_obj.call();
			dispatch_class_noop_obj.call();
			dispatch_class_noop_obj.call();
			dispatch_class_noop_obj.call();
			dispatch_class_noop_obj.call();
			dispatch_class_noop_obj.call();
			dispatch_class_noop_obj.call();
			dispatch_class_noop_obj.call();
			dispatch_class_noop_obj.call();
			dispatch_class_noop_obj.call();
			dispatch_class_noop_obj.call();
			dispatch_class_noop_obj.call();
			dispatch_class_noop_obj.call();
			dispatch_class_noop_obj.call();
			dispatch_class_noop_obj.call();
			dispatch_class_noop_obj.call();
			dispatch_class_noop_obj.call();
			dispatch_class_noop_obj.call();
			dispatch_class_noop_obj.call();
		}
	};

	static V_method mdispatch = () -> {
		if(i++ > j++){
			i = i/j;
		}
		if(j > 100000){
			j = 1;
		}
	};

	static Benchmark dispatch = () -> {
		for(int i = 0; i < DISPATCH_COUNT; i++){
			mdispatch.call();
			mdispatch.call();
			mdispatch.call();
			mdispatch.call();
			mdispatch.call();
			mdispatch.call();
			mdispatch.call();
			mdispatch.call();
			mdispatch.call();
			mdispatch.call();
			mdispatch.call();
			mdispatch.call();
			mdispatch.call();
			mdispatch.call();
			mdispatch.call();
			mdispatch.call();
			mdispatch.call();
			mdispatch.call();
			mdispatch.call();
			mdispatch.call();
		}
	};

	static V_method mdispatch_small = () -> {
		i++;
	};

	static Benchmark dispatch_small = () -> {
		for(int i = 0; i < DISPATCH_COUNT; i++) {
			mdispatch_small.call();
			mdispatch_small.call();
			mdispatch_small.call();
			mdispatch_small.call();
			mdispatch_small.call();
			mdispatch_small.call();
			mdispatch_small.call();
			mdispatch_small.call();
			mdispatch_small.call();
			mdispatch_small.call();
			mdispatch_small.call();
			mdispatch_small.call();
			mdispatch_small.call();
			mdispatch_small.call();
			mdispatch_small.call();
			mdispatch_small.call();
			mdispatch_small.call();
			mdispatch_small.call();
			mdispatch_small.call();
			mdispatch_small.call();
		}
	};

	static V_method mdispatch_noop = () -> { };
	
	static Benchmark dispatch_noop = () -> {
			for(int i = 0; i < DISPATCH_NOOP_COUNT; i++){
				mdispatch_noop.call();
				mdispatch_noop.call();
				mdispatch_noop.call();
				mdispatch_noop.call();
				mdispatch_noop.call();
				mdispatch_noop.call();
				mdispatch_noop.call();
				mdispatch_noop.call();
				mdispatch_noop.call();
				mdispatch_noop.call();
				mdispatch_noop.call();
				mdispatch_noop.call();
				mdispatch_noop.call();
				mdispatch_noop.call();
				mdispatch_noop.call();
				mdispatch_noop.call();
				mdispatch_noop.call();
				mdispatch_noop.call();
				mdispatch_noop.call();
				mdispatch_noop.call();
			}
	};
	
	@Override
	int doIterations(int numIterations) {
		
		return 0;
	}	
}


