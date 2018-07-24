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

package net.adoptopenjdk.bumblebench.indy;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.H_INVOKESTATIC;
import static org.objectweb.asm.Opcodes.V1_7;
import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import net.adoptopenjdk.bumblebench.core.MiniBench;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;

import static java.lang.invoke.MethodHandles.lookup;
import static java.lang.invoke.MethodHandles.constant;
import static java.lang.invoke.MethodHandles.guardWithTest;
import static java.lang.invoke.MethodHandles.insertArguments;
import static java.lang.invoke.MethodHandles.permuteArguments;
import static java.lang.invoke.MethodType.methodType;

import java.util.Random;

public class FastPathGuardsBench extends MiniBench {

	/*
	 * MiniBench entry point
	 */

	protected final long doBatch(long numLoops, int numIterationsPerLoop) throws InterruptedException {
		if (_workload == null)
			_workload = (Workload)newInstance(generateIndyClass());
		Random random = newRandom();
		long iterationsPerformed = 0;
		for (long loop = 0; loop < numLoops; loop++) {
			int[] ints = new int[NUM_INTS];
			for (int i = 0; i < ints.length; i++)
				ints[i] = random.nextInt();

			startTimer();
			iterationsPerformed += doIndys(_workload, ints, numIterationsPerLoop);
			pauseTimer();
		}
		return iterationsPerformed;
	}

	static Workload _workload = null;

	final int doIndys(Workload workload, int[] ints, int numIterationsPerLoop) {
		int sum = 0;
		int i;
		for (i = 0; i < numIterationsPerLoop; i += ints.length) {
			for(int value: ints) {
				sum = workload.compute(sum, value, 16777619);
			}
		}
		_escape = sum;
		return i;
	}

	protected final int maxIterationsPerLoop(){ return ITERATIONS_PER_INDY; }

	static final int ITERATIONS_PER_INDY = option("iterationsPerIndy", 100000);
	static final int NUM_INTS            = option("numInts",           100);

	int  _escape; // To prevent the optimizer from removing the whole method

	/*
	 * The workload class, containing an invokedynamic
	 */

	public abstract static class Workload {
		public abstract int compute(int arg1, int arg2, int arg3);
	}

	final Class generateIndyClass() {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		String className = "Indy";
		String Super = getInternalName(Workload.class);
		String[] interfaces = {};
		cw.visit(V1_7, ACC_PUBLIC | ACC_FINAL | ACC_SUPER, className, null, Super, interfaces);
		visitSourceFileInfo(cw);

		// int compute(int a1, int a2, int a3)
		{
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC|ACC_FINAL, "compute", "(III)I", null, null);
		mv.visitCode();

		// return computeViaIndy(a1, a2, a3);  <-- invokedynamic
		mv.visitVarInsn(ILOAD, 1);
		mv.visitVarInsn(ILOAD, 2);
		mv.visitVarInsn(ILOAD, 3);
		visitLineNumberInfo(mv);
		mv.visitInvokeDynamicInsn("computeViaIndy", "(III)I", 
			new Handle(
				H_INVOKESTATIC,
				getInternalName(FastPathGuardsBench.class),
				"bootstrap",
				"(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;"));
		mv.visitInsn(IRETURN);

		mv.visitMaxs(3, 0);
		mv.visitEnd();
		}

		// Empty constructor
		{
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();

		// super()
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, Super, "<init>", "()V");
		mv.visitInsn(RETURN);

		mv.visitMaxs(1, 0);
		mv.visitEnd();
		}

		cw.visitEnd();
		return loadClass(className, cw.toByteArray());
	}

	public static CallSite bootstrap(MethodHandles.Lookup lookup, String name, MethodType type) {
		try {
			_callSite = new MutableCallSite(lookup.findStatic(
				FastPathGuardsBench.class,
				"expensiveCalculation",
				methodType(int.class, int.class, int.class, int.class)));
			return _callSite;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static int expensiveCalculation(int accumulator, int newData, int fnvPrime) {
		doSpecializationLogic(fnvPrime);
		// FNV-1a hash.  Not exactly expensive, but at least nontrivial.
		return (accumulator ^ newData) * fnvPrime;
	}

	/*
	 * Super smart dynamic specialization logic
	 */

	private static MutableCallSite _callSite;

	static final void doSpecializationLogic(int fnvPrime) {
		try {
			if (fnvPrime == _prevFNVPrime) {
				if (++_runLength >= FAST_PATH_THRESHOLD) {
					_runLength = -1000000000; // Don't specialize again until fnvPrime changes again

					// JSR292 legalese
					Class This = FastPathGuardsBench.class;
					MethodType IIZ  = methodType(boolean.class, int.class, int.class);
					MethodType IIIZ = methodType(boolean.class, int.class, int.class, int.class);
					MethodType IIII = methodType(int.class, int.class, int.class, int.class);
					MethodHandle intsAreEqual = lookup().findStatic(This, "intsAreEqual", IIZ);
					MethodHandle expensiveCalculation = lookup().findStatic(This, "expensiveCalculation", IIII);

					// Insert a specialization guard
					_callSite.setTarget(
						guardWithTest(
							permuteArguments(
								insertArguments(intsAreEqual, 1, fnvPrime),
								IIIZ, 2
							),
							permuteArguments( // Specialized handle that passes the most common value
								insertArguments(expensiveCalculation, 2, fnvPrime),
								IIII, 0, 1
							),
							_callSite.getTarget() // Original unspecialized handle
						)
					);
				}
			} else {
				// Starting a new run
				_prevFNVPrime = fnvPrime;
				_runLength = 1;
			}
		} catch (IllegalAccessException|NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	static final int FAST_PATH_THRESHOLD = option("fastPathThreshold", 1000);

	private static int _prevFNVPrime;
	private static int _runLength;

	static boolean intsAreEqual(int left, int right){ return left == right; }

	/*
	 * Code generation utilities
	 */

	static void visitSourceFileInfo(ClassWriter cw) {
		cw.visitSource(Thread.currentThread().getStackTrace()[3].getFileName(), null);
	}

	static void visitLineNumberInfo(MethodVisitor mv) {
		Label label = new Label();
		mv.visitLabel(label);
		mv.visitLineNumber(Thread.currentThread().getStackTrace()[3].getLineNumber(), label);
	}

	static String getInternalName(Class c) {
		return c.getName().replace('.', '/');
	}

	/*
	 * Debris
	 */

	public static int gcd(int left, int right) {
		// http://en.wikipedia.org/wiki/Binary_GCD_algorithm
		// UNTESTED
		int u = left;
		int v = right;
		int shift = 0;
		while (true) {
			if (u == 0)
				return v;
			else if (v == 0)
				return u;
			else if ((u & 1) == 0) {
				u <<= 1;
				if ((v & 1) == 0) {
					// Both even
					++shift;
					v <<= 1;
				}
			} else {
				if ((v & 1) == 0) {
					v <<= 1;
				} else {
					// both odd
					if (u > v)
						u -= v;
					else
						v -= u;
				}
			}
		}
	}

}

