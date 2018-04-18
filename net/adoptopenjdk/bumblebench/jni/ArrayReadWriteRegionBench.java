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
package net.adoptopenjdk.bumblebench.jni;

public final class ArrayReadWriteRegionBench extends JNIMicroBenchBase {

	private static final int chunkSize = 10;
	private static final int arraySize = 1000;

	private long[] array;
	private long[] result;

	public ArrayReadWriteRegionBench() {
		super();

		// Initialize
		array = new long[arraySize];
		result = new long[arraySize];

		for (int i = 0; i < arraySize; ++i) {
			array[i] = i + 17;
		}
	}

	protected long doBatch(long numIterations) throws InterruptedException {
		final CallOverheadTestcases callOverheadTest = new CallOverheadTestcases();

		for (long i = 0; i < numIterations; i++)
			callOverheadTest.testArrayReadWriteRegion(array, i, result, chunkSize);

		return numIterations;
	}
}
