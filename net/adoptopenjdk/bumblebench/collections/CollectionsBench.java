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

import net.adoptopenjdk.bumblebench.core.MicroBench;

abstract class CollectionsBench extends MicroBench {
	protected final int ARRAY_SIZE    = option("arraySize", 100);
	protected final int HASH_MAP_SIZE = option("hashMapSize", 100);

	protected ArrayList<Integer> newArrayList() {
		ArrayList<Integer> result = new ArrayList<Integer>(ARRAY_SIZE);
		for(int i = 0; i < ARRAY_SIZE; i++){
			result.add(new Integer(i));
		}
		return result;
	}

	protected HashMap<Integer, Integer> newHashMap() {
		HashMap<Integer, Integer> result = new HashMap<Integer, Integer>(HASH_MAP_SIZE);
		for(int i = 0; i < HASH_MAP_SIZE; i++){
			result.put(new Integer(i), new Integer(i));
		}
		return result;
	}
}

