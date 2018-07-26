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

public final class ParamAndRetBench extends JNIMicroBenchBase {

	private int count = 517;
	private long[] i;
	private int[] j;
	private boolean[] k;
	private byte[] x;
	private char[] y;
	private short[] z;
	private float[] f1;
	private float[] f2;
	private float[] f3;
	private float[] f4;
	private float[] f5;
	private double[] d1;
	private double[] d2;
	private double[] d3;

	private static String testName = "testNoParamNoRet";

	public ParamAndRetBench() {
		super();

		count = 517;
		i = new long[count];
		j = new int[count];
		k = new boolean[count];
		x = new byte[count];
		y = new char[count];
		z = new short[count];
		f1 = new float[count];
		f2 = new float[count];
		f3 = new float[count];
		f4 = new float[count];
		f5 = new float[count];
		d1 = new double[count];
		d2 = new double[count];
		d3 = new double[count];

		for (int iter = 0; iter < count; ++iter) {
			i[iter] = iter * 17;
			j[iter] = iter;
			k[iter] = (iter % 7 == 1) ? true : false;

			x[iter] = (byte) (iter % 256);
			y[iter] = (char) (iter % 112);
			z[iter] = (short) ((iter * 11) % 123);

			f1[iter] = (float) Math.sqrt(iter);
			f2[iter] = (float) Math.sqrt(iter % 11);
			f3[iter] = (float) Math.sqrt(iter % 7);
			f4[iter] = (float) Math.sqrt(iter % 13);
			f5[iter] = (float) Math.sqrt(iter % 17);

			d1[iter] = Math.sqrt(iter % 7);
			d2[iter] = Math.sqrt(iter);
			d3[iter] = Math.log(iter + 2);
		}
	}

	protected long doBatch(long numIterations) throws InterruptedException {
		final CallOverheadTestcases callOverheadTest = new CallOverheadTestcases();

		for (long iter = 0; iter < numIterations; iter++) {
			int index = (int)(iter % count);
			callOverheadTest.testParamAndRet(i[index], j[index], k[index], x[index], y[index], z[index], f1[index],
					f2[index], f3[index], f4[index], f5[index], d1[index], d2[index], d3[index]);

		}

		return numIterations;
	}
}
