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

package net.adoptopenjdk.bumblebench.arraycopy;

import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * 
 * 
 * This benchmark simply tests the speed of copying char arrays in either backward
 * or forward direction. This direction can be specified by options.
 * 
 * The reported score is in terms of number of array copies done in one second.
 * Length of array is fixed to 8192 but type can be changed using options.
 * 
 * Also this benchmark in all iterations copies 4000 elements which can be changed
 * through options.
 * 
 * */


public final class ArrayCopyObject extends MicroBench {
	private static final boolean BACKWARD_ARRAYCOPY = option("backwardArrayCopy", false);
	private static final int ARRAY_LENGTH = option("arrayLength", 8192);
	private static final int COPY_ELEMENTS = option("copyElements", 4000);
	private static final int MAX_ITERATIONS_PER_LOOP = option("maxIterations", 10000000);

	private static String[] objArray = new String[ARRAY_LENGTH];
	private static int dstPoint;
	private static int srcPoint;
	
	static {
		for (int i=0; i < ARRAY_LENGTH; i++) {
			objArray[i] = Integer.toString(i);
		}

		if (BACKWARD_ARRAYCOPY) {
			srcPoint = 0;
			dstPoint = 10;
		}
		else {
			srcPoint=10;
			dstPoint=0;
		}
	}

	protected int maxIterationsPerLoop() {
		return MAX_ITERATIONS_PER_LOOP;
	}

	@Override
	protected long doBatch(long numIterations) throws InterruptedException {
		for (long loop = 0; loop < numIterations; loop++) {
			System.arraycopy(objArray, srcPoint, objArray, dstPoint, COPY_ELEMENTS);
		}
		return numIterations;
	}
}
