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

public final class SameStringsEqualsBench extends HumbleBench {

	public static String TEXT = option(
		"text",
		"Lorem ipsum dolor sit amet, consectetur adipiscing elit");

	public static String PADDED_TEXT = "#" + TEXT + "#";
   
	public String[] _comparands = new String[8192];

	public SameStringsEqualsBench() { super(Workload.class); }

	@Override
	protected final void setup(int numIterations) {
		if (_comparands.length < numIterations) {
			_comparands = new String[numIterations];
		}

		int len = TEXT.length();
		for (int i = 0; i < numIterations; i++) {
			_comparands[i] = PADDED_TEXT.substring(1, 1 + len);
		}

		for (int i = numIterations; i < _comparands.length; i++) {
			_comparands[i] = null;
		}
	}

	public static class Workload extends AbstractWorkload {
		@Override
		public void doBatch(HumbleBench bench0, int numIterations) {
			SameStringsEqualsBench bench = (SameStringsEqualsBench)bench0;

			String text = TEXT;
			String[] comparands = bench._comparands;

			long equalCount = 0;

			for (int i = 0; i < numIterations; i++) {
				if (text.equals(comparands[i])) {
					equalCount++;
				}
			}

			if (equalCount != numIterations) {
				throw new AssertionError("equalCount");
			}
		}
	}

}
