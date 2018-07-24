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

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;

import static java.lang.invoke.MethodType.*;
import static java.lang.invoke.MethodHandles.*;


public class BootstrapMethods {
	
	public static CallSite gwtBootstrap(Lookup ignored, String name, MethodType type) throws Throwable {
		Lookup lookup = lookup();
		MethodHandle double_string = lookup.findStatic(BootstrapMethods.class, "dup", methodType(String.class, String.class));
		MethodHandle double_integer = lookup.findStatic(BootstrapMethods.class, "dup", methodType(String.class, Integer.class));
		MethodHandle double_object = lookup.findStatic(BootstrapMethods.class, "dup", methodType(String.class, Object.class));
		MethodHandle isInteger = lookup.findStatic(BootstrapMethods.class, "isInteger", methodType(boolean.class, Object.class));
		MethodHandle isString = lookup.findStatic(BootstrapMethods.class, "isString", methodType(boolean.class, Object.class));

		MethodHandle handle = guardWithTest(
				isString, 
				double_string.asType(methodType(String.class, Object.class)),
				double_object);
		handle = guardWithTest(
				isInteger,
				double_integer.asType(methodType(String.class, Object.class)),
				handle);
		return new MutableCallSite(handle);
	}

	static boolean isString(Object o) {
		return o instanceof String;
	}
	static boolean isInteger(Object o) {
		return o instanceof Integer;
	}
	
	static String dup(String s) {
		return s+s;
	}

	static String dup(Integer i) {
		return "" + (i + i);
	}

	static String dup(Object o) {
		return "DoesNotUnderStand: " + o.getClass() + " message: double";
	}

	public static CallSite fibBootstrap(Lookup ignored, String name, MethodType type) throws Throwable {
		Lookup lookup = lookup();
		MethodHandle fib = lookup.findStatic(BootstrapMethods.class, "fib", methodType(int.class, int.class));
		return new ConstantCallSite(fib);
	}

	static int fib(int n) {
		if (n <= 1)
			return 1;
		else
			return ComplexIndy.fibIndy(n-1) + ComplexIndy.fibIndy(n-2);
	}
}
