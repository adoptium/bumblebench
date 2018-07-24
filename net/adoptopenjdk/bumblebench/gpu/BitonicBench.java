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

package net.adoptopenjdk.bumblebench.gpu;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public abstract class BitonicBench extends GPUBase {
    final static int LOG2BLOCKDIM = option("LOG2BLOCKDIM", 9);
    Random random = newRandom();
    //int length = COLS; // < Use ROWS rather than COLS, the same as the other
    final int length = ROWS;   // < GPU benchmarks, to make the shell scripting easier.
    final int[] inputData  = new int[length];
    final int[] data       = new int[length];
    final int[] dataVerify = new int[length];

    protected void clear() {
        for (int i=0; i<length; i++)
				data[i] = inputData[i] = dataVerify[i] = random.nextInt();
    }

    protected static int highestOneBit(int i) {
        i |= i >>  1;
        i |= i >>  2;
        i |= i >>  4;
        i |= i >>  8;
        i |= i >> 16;
        return i - (i >> 1);
    }

    protected boolean verify() {
        if (VERIFY) {
            Arrays.parallelSort(dataVerify);

            for (int i=0; i < length; i++) {
                if (data[i] != dataVerify[i]) {
                    System.out.printf( "Error:  data[%d]=%d but dataVerify[%d]=%d\n", i, data[i], i, dataVerify[i]);
                        return false;
                    }
            }
        }
        return true;
    }

	public final static class CPU extends BitonicBench {
    	protected long doBatch(long numIterations) throws InterruptedException {
        	for (long count = 0; count < numIterations; count+=1) {
					pauseTimer();
            	clear();
					startTimer();
            	for (int phaseStride = 1; phaseStride < length; phaseStride <<= 1) {
                	int stepFirstIter = (highestOneBit(((length - 1) >> LOG2BLOCKDIM) | 1))*(1 << LOG2BLOCKDIM);
                	for (int stepFirstIndex = 0; stepFirstIndex < stepFirstIter; stepFirstIndex++) {
                    	int stepFirstLi    = (stepFirstIndex << 1) - (stepFirstIndex & (phaseStride - 1));
                    	int stepFirstRi    = stepFirstLi ^ ((phaseStride << 1) - 1);
                    	if (stepFirstRi < length) {
                        	int stepFirstLd = data[stepFirstLi];
                        	int stepFirstRd = data[stepFirstRi];
                        	if (stepFirstLd > stepFirstRd) {
                            	data[stepFirstLi] = stepFirstRd;
                            	data[stepFirstRi] = stepFirstLd;
                        	}
                    	}
                	}
                	for (int stepStride = phaseStride; (stepStride >>= 1) != 0;) {
                    	int stepOtherIter = ((length + ((2 << LOG2BLOCKDIM) - 2)) >> (LOG2BLOCKDIM + 1))*(1 << LOG2BLOCKDIM);
                    	for (int stepOtherIndex = 0; stepOtherIndex < stepOtherIter ; stepOtherIndex++) {
                        	int stepOtherLi    = (stepOtherIndex << 1) - (stepOtherIndex & (stepStride - 1));
                        	int stepOtherRi    = stepOtherLi + stepStride;
                        	if (stepOtherRi < length) {
                            	int stepOtherLd = data[stepOtherLi];
                            	int stepOtherRd = data[stepOtherRi];
                            	if (stepOtherLd > stepOtherRd) {
                                	data[stepOtherLi] = stepOtherRd;
                                	data[stepOtherRi] = stepOtherLd;
                            	}
                        	}
                    	}
                	}
            	}
        	}
        	return numIterations;
    	}
	}

	public static final class GPULambda extends BitonicBench {
    	protected long doBatch(long numIterations) throws InterruptedException {
 			for (long count = 0; count < numIterations; count+=1) {
					pauseTimer();
            	clear();
					startTimer();
            	for (int phaseStrideLoopVar = 1; phaseStrideLoopVar < length; phaseStrideLoopVar <<= 1) {
                	final int phaseStride = phaseStrideLoopVar;
                	int stepFirstIter = (highestOneBit(((length - 1) >> LOG2BLOCKDIM) | 1))*(1 << LOG2BLOCKDIM);
                	//for (int stepFirstIndex = 0; stepFirstIndex < stepFirstIter; stepFirstIndex++)
                	IntStream.range(0, stepFirstIter).parallel().forEach( stepFirstIndex -> {
                    	int stepFirstLi    = (stepFirstIndex << 1) - (stepFirstIndex & (phaseStride - 1));
                    	int stepFirstRi    = stepFirstLi ^ ((phaseStride << 1) - 1);
                    	if (stepFirstRi < length) {
                        	int stepFirstLd = data[stepFirstLi];
                        	int stepFirstRd = data[stepFirstRi];
                        	if (stepFirstLd > stepFirstRd) {
                            	data[stepFirstLi] = stepFirstRd;
                            	data[stepFirstRi] = stepFirstLd;
                        	}
                    	}
                	} );
                	for (int stepStrideLoopVar = phaseStride; (stepStrideLoopVar >>= 1) != 0;) {
                    	final int stepStride = stepStrideLoopVar;
                    	int stepOtherIter = ((length + ((2 << LOG2BLOCKDIM) - 2)) >> (LOG2BLOCKDIM + 1))*(1 << LOG2BLOCKDIM);
                    	//for(int stepOtherIndex = 0; stepOtherIndex < stepOtherIter ; stepOtherIndex++)
                    	IntStream.range(0, stepOtherIter).parallel().forEach( stepOtherIndex -> {
                        	int stepOtherLi    = (stepOtherIndex << 1) - (stepOtherIndex & (stepStride - 1));
                        	int stepOtherRi    = stepOtherLi + stepStride;
                        	if (stepOtherRi < length) {
                            	int stepOtherLd = data[stepOtherLi];
                            	int stepOtherRd = data[stepOtherRi];
                            	if (stepOtherLd > stepOtherRd) {
                                	data[stepOtherLi] = stepOtherRd;
                                	data[stepOtherRi] = stepOtherLd;
                            	}
                        	}
                    	} );
                	}
            	}
			}
			return numIterations;
    	}
	}
}
