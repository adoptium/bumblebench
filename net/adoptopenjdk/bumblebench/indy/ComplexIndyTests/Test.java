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

public class Test {

	public static void main(String[] args) throws Throwable {
		int iterations = 1;
		
		if (args != null && args.length == 1) {
			iterations = Integer.parseInt(args[0]);
		}
		for (int i = 0; i < iterations; i++) {
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			//test_gwtTest_String();
			//test_gwtTest_Integer();
			//test_gwtTest_Object();
			test_fibTest();
		}
	}
	
	public static void test_gwtTest_String() {
		String s = ComplexIndy.gwtTest("a");
		if (!s.equals("aa")) throw new Error("Wrong string returned'" + s +"'");
	}
	public static void test_gwtTest_Integer() {
		String s = ComplexIndy.gwtTest(new Integer(1));
		if (!s.equals("2")) throw new Error("Wrong string returned'" + s +"'");
	}
	public static void test_gwtTest_Object() {
		String s = ComplexIndy.gwtTest(new Object());
		if (!s.equals("DoesNotUnderStand: class java.lang.Object message: double")) throw new Error("Wrong string returned'" + s +"'");
	}

	public static void test_fibTest() {
		int result = ComplexIndy.fibIndy(10);
		if (result != 89)
			throw new Error("test_fibTest expected 89; got" + result);
	}
	
}
