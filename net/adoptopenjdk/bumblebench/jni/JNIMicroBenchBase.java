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

import net.adoptopenjdk.bumblebench.core.MicroBench;

public abstract class JNIMicroBenchBase extends MicroBench {

	// nonXPLINK simulates z/OS Java-2-COBOL inter-operation
	static final boolean isNonXPLINK = option("NONXPLINK", false);

	public JNIMicroBenchBase() {
		try {
			if (isNonXPLINK)
				System.loadLibrary("stdlinkjnibench");
			else
				System.loadLibrary("jnibench");
		} catch (UnsatisfiedLinkError e) {
			System.err.println("Native code library libjnibench.so failed to load.\n");
			System.err.println(
					"Build the native library using the provided build-dll.sh script and make ensure the library is in LD_LIBRARY_PATH or LIBPATH.\n"
							+ e);
			System.exit(1);
		}
	}
}
