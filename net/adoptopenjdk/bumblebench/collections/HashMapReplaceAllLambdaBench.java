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
import java.util.HashMap;
import java.util.Iterator;

public final class HashMapReplaceAllLambdaBench extends CollectionsBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		pauseTimer();
		for (int i = 0; i < numIterations; i+=1) {
			// We're altering the hashMap, so we need a fresh one each time
			HashMap<Integer, Integer> hashMap = newHashMap();
			startTimer();
			hashMap.replaceAll((k, v) -> new Integer(k + v));
			pauseTimer();
		}
		return numIterations;
	}

}

