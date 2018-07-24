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

import java.util.HashMap;

public final class HashMapForEachTradBench extends CollectionsBench {
	private HashMap<Integer, Integer> _hashMap = newHashMap();
	private long _sum;

	protected long doBatch(long numIterations) throws InterruptedException {
		for (long i = 0; i < numIterations; i+=1) {
			_sum = 0;
			for (HashMap.Entry<Integer, Integer> entry : _hashMap.entrySet()) {
				_sum += entry.getKey() + entry.getValue();
			}
		}
		return numIterations;
	}

}

