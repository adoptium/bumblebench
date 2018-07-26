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

public class CallOverheadTestcases {

	private int intField;
	private long longField;

	private static long longFieldStatic = 56789;

	public CallOverheadTestcases() {
		intField = 123;
		longField = 12345;
	}

	/**
	 * \brief Test JNI call with no parameter nor return value
	 * <p>
	 * call with no parameters and returns nothing
	 * 
	 */
	public final native void testNoParamNoRet();

	/**
	 * \brief Test JNI call with a parameter and no return value
	 * <p>
	 * call that takes an long as parameter, increment the long in native code but
	 * returns nothing.
	 */
	public final native void testParamNoRet(long i);

	/**
	 * \brief Test with parameters and a return value
	 * <p>
	 * takes input long i, increment by a constant amount, and return the result
	 * 
	 */
	public final native double testParamAndRet(long i, int j, boolean k, byte x, char y, short z, 
			float f1, float f2, float f3, float f4, float f5, 
			double d1, double d2, double d3);

	/**
	 * \brief Test set long field
	 * <p>
	 * get the field ID of a non-static long field, and assign input long value to
	 * it
	 */
	public final native void testSetLongField(long j);

	/**
	 * \brief Test set a static long field
	 * <p>
	 * static version of the one above. uses a static long field.
	 */
	public final static native void testSetLongFieldStatic(long j);

	/**
	 * \brief test array read and write with region
	 * <p>
	 * The native side doesn't make copies of arrays Java
	 * passes an array of const length (e.g. long[] of lenth 1000) to C++, which
	 * increments each element by j and writes results back to Java array via JNI.
	 * i.e this is performing an element-wise operation: result = array + j.
	 */
	public final native void testArrayReadWriteRegion(long[] array, long j, long[] result, int len);
}
