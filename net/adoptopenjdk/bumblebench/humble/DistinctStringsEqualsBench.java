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

package net.adoptopenjdk.bumblebench.humble;

import net.adoptopenjdk.bumblebench.core.HumbleBench;

public final class DistinctStringsEqualsBench extends HumbleBench {

	public static String[] TEXT_A;
	public static String[] TEXT_B;

	static {
		String s = option("text", "Lorem ipsum dolor sit amet");

		// Different strings with the same length and hash code
		String textA = s + "\u0000\u001f" + s;
		String textB = s + "\u0001\u0000" + s;

		TEXT_A = new String[] { textA, textA };
		TEXT_B = new String[] { textB, textB };
	}

	public DistinctStringsEqualsBench() { super(Workload.class); }

	public static class Workload extends AbstractWorkload {
		@Override
		public void doBatch(HumbleBench bench, int numIterations) {
			String textA0 = TEXT_A[0];
			String textA1 = TEXT_A[1];
			String textB0 = TEXT_B[0];
			String textB1 = TEXT_B[1];

			long equalCount = 0;

			for (int i = 0; i < numIterations; i++) {
				String textA, textB;
				if (i % 2 == 0) {
					textA = textA0;
					textB = textB0;
				} else {
					textA = textA1;
					textB = textB1;
				}
				if (textA.equals(textB)) {
					equalCount++;
				}
			}

			if (equalCount != 0) {
				throw new AssertionError("equalCount");
			}
		}
	}

}
