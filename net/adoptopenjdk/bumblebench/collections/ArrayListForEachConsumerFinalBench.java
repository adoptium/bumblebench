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

package net.adoptopenjdk.bumblebench.collections;

import java.util.ArrayList;
import java.util.function.Consumer;

public final class ArrayListForEachConsumerFinalBench extends CollectionsBench {
	private final ArrayList<Integer> _arrayList = newArrayList();
	private long _sum;

	private final Consumer<Integer> consumer = new Consumer<Integer>() {
		@Override
		public void accept(Integer v) {
			_sum += v;
		}
	};

	protected long doBatch(long numIterations) throws InterruptedException {
		for (long i = 0; i < numIterations; i+=1) {
			_sum = 0;
			_arrayList.forEach(consumer);
		}
		return numIterations;
	}

}

