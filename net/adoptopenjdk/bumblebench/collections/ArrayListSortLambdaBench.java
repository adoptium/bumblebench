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

public final class ArrayListSortLambdaBench extends CollectionsBench {
	private final ArrayList<Integer> _arrayList = newArrayList();

	protected long doBatch(long numIterations) throws InterruptedException {
		long numLoopIterations = numIterations / 2;
		for (long i = 0; i < numLoopIterations; i+=2) {
			_arrayList.sort((a, b) -> {
				if(a.intValue() > b.intValue()){
					return -1;
				}
				if(b.intValue() > a.intValue()){
					return 1;
				}
				return 0;
			});
			_arrayList.sort((a, b) -> {
				if(a.intValue() > b.intValue()){
					return 1;
				}
				if(b.intValue() > a.intValue()){
					return -1;
				}
				return 0;
			});
		}
		return numIterations;
	}

}

