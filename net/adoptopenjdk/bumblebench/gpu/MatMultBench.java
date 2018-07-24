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

import java.util.Random;
import java.util.stream.IntStream;

public abstract class MatMultBench extends GPUBase {
    protected double[] input1 = new double [ROWS*ROWS];
    protected double[] input2 = new double [ROWS*ROWS];
    protected double[] output = new double [ROWS*ROWS];
    protected double[] verify = new double [ROWS*ROWS];

    public MatMultBench() {
        super();
        Random random = newRandom();
        for (int i = 0; i < ROWS*ROWS; i++) {
            input1[i] = (double)random.nextInt(1000);
            input2[i] = (double)random.nextInt(1000);
        }
        if (VERIFY) {
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < ROWS; j++) {
                    double sum = 0;
                    for (int k = 0; k < ROWS; k++) {
                        sum += input1[i*ROWS + k] * input2[k*ROWS + j];
                    }
                    verify[i*ROWS + j] = sum;
                }
            }
        }
    }

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

	public static final class CPU extends MatMultBench {
    	protected long doBatch(long numIterations) throws InterruptedException {
        	for (long count = 0; count < numIterations; count+=1) {
            	for (int i = 0; i < ROWS; i++) {
                	for (int j = 0; j < ROWS; j++) {
                    	double sum = 0;
                    	for (int k = 0; k < ROWS; k++) {
                        	sum += input1[i*ROWS + k] * input2[k*ROWS + j];
                    	}
                    	output[i*ROWS + j] = sum;
                	}
            	}
        	}
        	return numIterations;
    	}
	}

	public static final class GPULambda extends MatMultBench {
    	protected long doBatch(long numIterations) throws InterruptedException {
 			for (long count = 0; count < numIterations; count+=1) {
            	IntStream.range(0, ROWS*ROWS).parallel().forEach( elementIndex -> {
                	int i = elementIndex / ROWS;
                	int j = elementIndex % ROWS;
                	double sum = 0;
	
                	for (int k = 0; k < ROWS; k++) {
                    	sum += input1[i*ROWS + k] * input2[k*ROWS + j] /* *Math.sin(sum) * Math.sqrt(sum) */;
                	}
                	//output[i*ROWS + j] = sum;
                	output[elementIndex] = sum;
            	} );
			}
			return numIterations;
    	}
	}

}
