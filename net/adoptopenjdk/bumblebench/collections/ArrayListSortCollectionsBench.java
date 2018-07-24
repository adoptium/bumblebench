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
import java.util.Collections;

public final class ArrayListSortCollectionsBench extends CollectionsBench {
	private final ArrayList<Integer> _arrayList = newArrayList();

	protected long doBatch(long numIterations) throws InterruptedException {
		long numLoopIterations = numIterations / 2;
		for (long i = 0; i < numLoopIterations; i+=2)
		{
			Collections.reverse(_arrayList);
			Collections.sort(_arrayList);
		}
		return numIterations;
	}

}

