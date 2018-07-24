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

public abstract class KmeansBench extends GPUBase {
    final static int GRID_DIM_X = 14*32; //TODO:change to variable when getGridDimX works
    Random random = newRandom();
    //TODO: large data set
    final static int n    = 4*(14*32*512); //TODO: make variable
    final static int dims = 8;             //TODO: make variable
    final static int k    = 256;           //TODO: make variable
    //TODO: small data set
    //final static int n    = 10; //TODO: make variable
    //final static int dims = 2;  //TODO: make variable
    //final static int k    = 3;  //TODO: make variable
    double[] coordinates = new double[n*dims];
    double[] means       = new double[k*dims];
    final static int GRID_DIM = 14*32; //TODO: make variable?
    int[] clusters;
    int[] counts;
    double[] s1;
    double[] s2;
    int[] countsVerify;
    double[] s1Verify;
    double[] s2Verify;

    protected void clear() {
        for (int i=0; i<n*dims; i++) {
            coordinates[i] = (double)random.nextInt(1000);
        }
        for (int i=0; i<k*dims; i++) {
            means[i] = (double)random.nextInt(1000);
        }
        clusters = new int[n];
        counts = new int[GRID_DIM*k];
        s1 = new double[GRID_DIM*k * dims];
        s2 = new double[GRID_DIM*k * dims];
        countsVerify = null;
        s1Verify = null;
        s2Verify = null;
    }

    protected static double distance(int dims, double[] a, int aOffset, double[] b, int bOffset) {
        double sum = 0;
        for (int i = 0; i < dims; ++i) {
            double diff = a[aOffset + i] - b[bOffset + i];
            sum += diff * diff;
        }
        return sum;
    }

    protected boolean verify() {
        if (VERIFY) {
            if (countsVerify == null) {
                countsVerify = new int[k];
                s1Verify = new double[k * dims];
                s2Verify = new double[k * dims];
                for (int i = 0; i < n; ++i) {
                    int bestCluster = 0;
                    double bestDistance = 0;
                    for (int c = 0; c < k; ++c) {
                        double dist = distance(dims, coordinates, i * dims, means, c * dims);
                        if (c == 0 || bestDistance > dist) {
                            bestCluster  = c;
                            bestDistance = dist;
                        }
                    }
                    countsVerify[bestCluster] += 1;
                    for (int d = 0; d < dims; ++d) {
                        double coord = coordinates[i * dims + d];
                        s1Verify[bestCluster * dims + d] += coord;
                        s2Verify[bestCluster * dims + d] += coord * coord;
                    }
                }
            }
            double errorMargin = 0.00001;
            for (int i = 0; i < k; i++) {
                if (counts[i] != countsVerify[i]) {
                System.out.printf( "Error:  counts[%d]=%d but countsVerify[%d]=%d\n", i, counts[i], i, countsVerify[i]);
                    return false;
                }
            }
            for (int i = 0; i < k * dims; i++) {
                if(s1[i] > s1Verify[i]*(1+errorMargin) || s1[i] < s1Verify[i]*(1-errorMargin)) {
                System.out.printf( "Error:  s1[%d]=%f but s1Verify[%d]=%f\n", i, s1[i], i, s1Verify[i]);
                    return false;
                }
            }
            for (int i = 0; i < k * dims; i++) {
                if(s2[i] > s2Verify[i]*(1+errorMargin) || s2[i] < s2Verify[i]*(1-errorMargin)) {
                System.out.printf( "Error:  s2[%d]=%f but s2Verify[%d]=%f\n", i, s2[i], i, s2Verify[i]);
                    return false;
                }
            }
        }
        return true;
    }

	public static final class CPU extends KmeansBench {
    	protected long doBatch(long numIterations) throws InterruptedException {
        	for (long count = 0; count < numIterations; count+=1) {
					pauseTimer();
            	clear();
					startTimer();
            	for (int i = 0; i < n; ++i) {
                	int bestCluster = 0;
                	double bestDistance = 0;
                	for (int c = 0; c < k; ++c) {
                    	double dist = distance(dims, coordinates, i * dims, means, c * dims);
                    	if (c == 0 || bestDistance > dist) {
                        	bestCluster  = c;
                        	bestDistance = dist;
                    	}
                	}
                	counts[bestCluster] += 1;
                	for (int d = 0; d < dims; ++d) {
                    	double coord = coordinates[i * dims + d];
                    	s1[bestCluster * dims + d] += coord;
                    	s2[bestCluster * dims + d] += coord * coord;
                	}
            	}
        	}
        	return numIterations;
    	}
	}

	public final class GPULambda extends KmeansBench {
    	protected long doBatch(long numIterations) throws InterruptedException {
        	for (long countNI = 0; countNI < numIterations; countNI+=1) {
					pauseTimer();
            	clear();
					startTimer();
            	IntStream.range(0, n).parallel().forEach( i -> {
                	int bestCluster = 0;
                	double bestDistance = 0;
                	for (int cluster = 0; cluster < k; ++cluster) {
                    	double dist = 0.0;
                    	for (int dim = 0; dim < dims; ++dim) {
                        	double diff = coordinates[i * dims + dim] - means[cluster * dims + dim];
                        	dist += diff * diff;
                    	}
                    	if (bestDistance > dist || cluster == 0) {
                        	bestCluster  = cluster;
                        	bestDistance = dist;
                    	}
                	}
                	clusters[i] = bestCluster;
            	} );
	
            	final int blockSize = (n + GRID_DIM_X - 1) / GRID_DIM_X;
            	IntStream.range(0, GRID_DIM_X*k*dims).parallel().forEach( i -> {
                	int seg = i / (k*dims);
                	int cluster = (i/dims)%k;
                	int dim = i % dims;
                	final int begin     = seg * blockSize;
                	final int end       = n < (begin + blockSize) ? n : begin + blockSize;
                	if (dim == 0) {
                    	int count = 0;
                    	for (int j = begin; j < end; ++j) {
                        	if (clusters[j] == cluster) {
                            	count += 1;
                        	}
                    	}
                    	counts[seg * k + cluster] = count;
                	}
                	double sum1 = 0;
                	double sum2 = 0;
                	for (int j = begin; j < end; ++j) {
                    	if (clusters[j] == cluster) {
                        	double coord = coordinates[j * dims + dim];
                        	sum1 += coord;
                        	sum2 += coord * coord;
                    	}
                	}
                	final int index = (seg * k + cluster) * dims + dim;
                	s1[index] = sum1;
                	s2[index] = sum2;
            	} );
	
            	IntStream.range(0, k*dims).parallel().forEach( i -> {
                	int cluster = i / dims;
                	int dim = i % dims;
                	if (dim == 0) {
                    	int count = 0;
                    	for (int j=0; j < GRID_DIM_X; ++j) {
                        	count += counts[j*k + cluster];
                    	}
                    	counts[cluster] = count;
                	}
                	final int            start = cluster * dims + dim;
                	final int            kd    = k       * dims;
                	double               sum1  = 0;
                	double               sum2  = 0;
                	for (int j=0; j < GRID_DIM_X; ++j) {
                    	sum1 += s1[j*kd+start];
                    	sum2 += s2[j*kd+start];
                	}
                	s1[start] = sum1;
                	s2[start] = sum2;
            	} );
        	}
        	return numIterations;
    	}
	}

}

