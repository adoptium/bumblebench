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

import java.util.stream.IntStream;

public abstract class DoitgenBench extends GPUBase {
    final int[] output = new int [ROWS*ROWS*ROWS];
    final int[] sum    = new int [ROWS*ROWS*ROWS];
    final int[] C4     = new int [ROWS*ROWS];
    final int[] verify = new int [ROWS*ROWS*ROWS];

    protected boolean verify() {
        if (VERIFY) {
            for (int i = 0; i < ROWS*ROWS; i++) {
                if (verify[i] != output[i]) {
                    System.out.printf("Error:  output[%d]=%d but verify[%d]=%d\n",
                                      i, output[i], i, verify[i]);
                    return false;
                }
            }
        }
        return true;
    }

	public static final class CPU extends DoitgenBench {
    	protected long doBatch(long numIterations) throws InterruptedException {
        	for (long count = 0; count < numIterations; count+=1) {
            	for (int r = 0; r < ROWS; r++) {
                	for (int q = 0; q < ROWS; q++)  {
                    	for (int p = 0; p < ROWS; p++)  {
                        	sum[r*ROWS*ROWS + q*ROWS + p] = 0;
                        	for (int s = 0; s < ROWS; s++) {
                            	sum[r*ROWS*ROWS + q*ROWS + p] = sum[r*ROWS*ROWS + q*ROWS + p] + output[r*ROWS*ROWS + q*ROWS + s] * C4[s*ROWS+p];
                        	}
                    	}
                    	for (int p = 0; p < ROWS; p++) {
                        	output[r*ROWS*ROWS+q*ROWS+p] = sum[r*ROWS*ROWS+q*ROWS+p];
                    	}
                	}
            	}
        	}
        	return numIterations;
    	}
	}

	public static final class GPULambda extends DoitgenBench {
    	protected long doBatch(long numIterations) throws InterruptedException {
 			for (long count = 0; count < numIterations; count+=1) {
            	IntStream.range(0, ROWS*ROWS).parallel().forEach( elementIndex -> {
                	int r = elementIndex / ROWS;
                	int q = elementIndex % ROWS;
                	for (int p = 0; p < ROWS; p++)  {
                    	sum[r*ROWS*ROWS + q*ROWS + p] = 0;
                    	for (int s = 0; s < ROWS; s++) {
                        	sum[r*ROWS*ROWS + q*ROWS + p] = sum[r*ROWS*ROWS + q*ROWS + p] + output[r*ROWS*ROWS + q*ROWS + s] * C4[s*ROWS+p];
                    	}
                	}
                	for (int p = 0; p < ROWS; p++) {
                    	output[r*ROWS*ROWS+q*ROWS+p] = sum[r*ROWS*ROWS+q*ROWS+p];
                	}
            	} );
        	}
        	return numIterations;
    	}
	}
}

