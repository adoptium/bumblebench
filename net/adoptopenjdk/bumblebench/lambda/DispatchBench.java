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

package net.adoptopenjdk.bumblebench.lambda;

import net.adoptopenjdk.bumblebench.core.MiniBench;
import net.adoptopenjdk.bumblebench.lambda.interfaces.Nullary;

import java.util.Arrays;
import java.util.Random;

public abstract class DispatchBench extends MiniBench {

	static final int POLYMORPHISM              = option("polymorphism", 10);
	static final int MAX_SEQUENCE_LENGTH       = option("maxSequenceLength", 200); // Enough to defeat branch predictors, but not to burden dcache
	static final long CALLS_PER_SEQUENCE       = option("callsPerSequence", 144000L); // Amortize the cost of generating random numbers and starting/pausing timer
	static final boolean PREDICTABLE_TARGETS   = option("predictableTargets", false);
	static final boolean MONOMORPHIC_SEQUENCES = option("monomorphicSequences", false);
	static final double FREQUENCY_RATIO        = option("frequencyRatio", 1.0); // Make each target k times more probable than the next; 1.0 == uniform
	static final boolean VERBOSE_SEQUENCES     = option("verboseSequences", false);

	abstract Nullary getCallee(int which);
	final Nullary[] _callees = computeCallees();

	Nullary[] computeCallees() {
		Nullary[] result = new Nullary[POLYMORPHISM];
		for (int i = 0; i < result.length; i++)
			result[i] = getCallee(i);
		return result;
	}

	protected int maxIterationsPerLoop(){ return MAX_SEQUENCE_LENGTH; }

	protected long doBatch(long numLoops, int numIterationsPerLoop) {
		// A "loop" here is one run through the callee sequence.  We actually
		// have a triply-nested loop structure here, which is evident in the fact
		// that timedLoop itself contains two loops.  We'll use MiniBench to size
		// the innermost loop, and we'll cope with the outer two loops here.
		//
		long numSequenceReps = 1 + ((CALLS_PER_SEQUENCE-1)/numIterationsPerLoop);
		if (numSequenceReps > numLoops)
			numSequenceReps = numLoops;

		Random random = newRandom();
		long totalIterations = 0;
		for (long loopCount = 0; loopCount < numLoops; loopCount += numSequenceReps) {
			Nullary[] calleeSequence = new Nullary[numIterationsPerLoop];
			StringBuilder sb = null;
			int[] counts = new int[_callees.length];
			if (VERBOSE_SEQUENCES)
				sb = new StringBuilder("Sequence: ");
			int loopID = random.nextInt(Integer.MAX_VALUE);
			for (int i=0; i < calleeSequence.length; i++) {
				int whichCallee;
				if (MONOMORPHIC_SEQUENCES) {
					whichCallee = (int)(loopID % _callees.length);
				} else if (PREDICTABLE_TARGETS) {
					whichCallee = (int)((loopID ^ i) % _callees.length);
				} else {
					whichCallee = nextCalleeGeomeric(_callees.length, random);
				}
				calleeSequence[i] = _callees[whichCallee];
				if (VERBOSE_SEQUENCES) {
					sb.append(' ').append(whichCallee);
					counts[whichCallee]++;
				}
			}
			if (VERBOSE_SEQUENCES) {
				System.err.println(sb);
				System.err.println("  Distribution/" + calleeSequence.length + ": " + Arrays.toString(counts));
			}
			startTimer();
			int sum = timedLoop(numSequenceReps, calleeSequence);
			pauseTimer();
			_counter += sum;
			totalIterations += numSequenceReps * calleeSequence.length;
		}
		return totalIterations;
	}

	int nextCalleeGeomeric(int numCallees, Random random) {
		// This could be pretty useful.  Consider moving up to the BumbleBench class.
		double uniform = random.nextDouble();
		if (FREQUENCY_RATIO <= 1.01) {
			return (int)(uniform * numCallees);
		} else {
			// Formula due to Chris Black
			final double r = 1.0/FREQUENCY_RATIO;
			final double logr = Math.log(r);
			double result = Math.log(1 - uniform + uniform * Math.pow(r, numCallees)) / logr;
			if (result >= numCallees)
				result = numCallees-1;
			return (int)result;
		}
	}

	protected int timedLoop(long numSequenceReps, Nullary[] calleeSequence) {
		int sum = 0;
		for (long i = 0; i < numSequenceReps; i++) {
			for (int j = 0; j < calleeSequence.length; j++)
				sum += calleeSequence[j].call();
		}
		return sum;
	}

	/*
	 * For N identical target classes, we can actually just load the same target
	 * class N times.  Note that the original class itself counts as one, so we
	 * only use freshly-loaded copies after the zeroth one.  Otherwise,
	 * polymorphsm=N will actually create N+1 subclasses of Nullary.
	 */

	public static final class InnerClasses extends DispatchBench {
		Nullary getCallee(int which) {
			return (Nullary)newInstanceOfPossiblyFreshlyLoadedClass(C.class, which != 0);
		}
		public static final class C implements Nullary { public int call(){ return workload(0xcafe); } }
	}

	public static final class InnerClassesNop extends DispatchBench {
		Nullary getCallee(int which) {
			return (Nullary)newInstanceOfPossiblyFreshlyLoadedClass(C.class, which != 0);
		}
		public static final class C implements Nullary { public int call(){ return 0xcafe; } }
	}

	/*
	 * To make all the target classes distinct, we need to use switches to get different consts into the lambdas / inner classes.
	 * TODO: Is there a way to get the non-distinct Lambda ones to use the class loader trick?
	 */

	public static final class InnerClassesDistinct extends DispatchBench {
		Nullary getCallee(int which) {
			switch (which) {
				case 0:  return new Nullary(){ public int call(){ return workload(0xcafe000); } };
				case 1:  return new Nullary(){ public int call(){ return workload(0xcafe001); } };
				case 2:  return new Nullary(){ public int call(){ return workload(0xcafe002); } };
				case 3:  return new Nullary(){ public int call(){ return workload(0xcafe003); } };
				case 4:  return new Nullary(){ public int call(){ return workload(0xcafe004); } };
				case 5:  return new Nullary(){ public int call(){ return workload(0xcafe005); } };
				case 6:  return new Nullary(){ public int call(){ return workload(0xcafe006); } };
				case 7:  return new Nullary(){ public int call(){ return workload(0xcafe007); } };
				case 8:  return new Nullary(){ public int call(){ return workload(0xcafe008); } };
				case 9:  return new Nullary(){ public int call(){ return workload(0xcafe009); } };
				case 10: return new Nullary(){ public int call(){ return workload(0xcafe00a); } };
				case 11: return new Nullary(){ public int call(){ return workload(0xcafe00b); } };
				case 12: return new Nullary(){ public int call(){ return workload(0xcafe00c); } };
				case 13: return new Nullary(){ public int call(){ return workload(0xcafe00d); } };
				case 14: return new Nullary(){ public int call(){ return workload(0xcafe00e); } };
				case 15: return new Nullary(){ public int call(){ return workload(0xcafe00f); } };
				default: warnAboutBias(); return getCallee(0);
			}
		}
	}

	public static final class InnerClassesNopDistinct extends DispatchBench {
		Nullary getCallee(int which) {
			switch (which) {
				case 0:  return new Nullary(){ public int call(){ return 0xcafe000; } };
				case 1:  return new Nullary(){ public int call(){ return 0xcafe001; } };
				case 2:  return new Nullary(){ public int call(){ return 0xcafe002; } };
				case 3:  return new Nullary(){ public int call(){ return 0xcafe003; } };
				case 4:  return new Nullary(){ public int call(){ return 0xcafe004; } };
				case 5:  return new Nullary(){ public int call(){ return 0xcafe005; } };
				case 6:  return new Nullary(){ public int call(){ return 0xcafe006; } };
				case 7:  return new Nullary(){ public int call(){ return 0xcafe007; } };
				case 8:  return new Nullary(){ public int call(){ return 0xcafe008; } };
				case 9:  return new Nullary(){ public int call(){ return 0xcafe009; } };
				case 10: return new Nullary(){ public int call(){ return 0xcafe00a; } };
				case 11: return new Nullary(){ public int call(){ return 0xcafe00b; } };
				case 12: return new Nullary(){ public int call(){ return 0xcafe00c; } };
				case 13: return new Nullary(){ public int call(){ return 0xcafe00d; } };
				case 14: return new Nullary(){ public int call(){ return 0xcafe00e; } };
				case 15: return new Nullary(){ public int call(){ return 0xcafe00f; } };
				default: warnAboutBias(); return getCallee(0);
			}
		}
	}

	public static final class Lambdas extends DispatchBench {
		Nullary getCallee(int which){
			switch (which) {
				case 0:  return () -> { return workload(0xcafe); };
				case 1:  return () -> { return workload(0xcafe); };
				case 2:  return () -> { return workload(0xcafe); };
				case 3:  return () -> { return workload(0xcafe); };
				case 4:  return () -> { return workload(0xcafe); };
				case 5:  return () -> { return workload(0xcafe); };
				case 6:  return () -> { return workload(0xcafe); };
				case 7:  return () -> { return workload(0xcafe); };
				case 8:  return () -> { return workload(0xcafe); };
				case 9:  return () -> { return workload(0xcafe); };
				case 10: return () -> { return workload(0xcafe); };
				case 11: return () -> { return workload(0xcafe); };
				case 12: return () -> { return workload(0xcafe); };
				case 13: return () -> { return workload(0xcafe); };
				case 14: return () -> { return workload(0xcafe); };
				case 15: return () -> { return workload(0xcafe); };
				default: warnAboutBias(); return getCallee(0);
			}
		}
	}

	public static final class LambdasDistinct extends DispatchBench {
		Nullary getCallee(int which){
			switch (which) {
				case 0:  return () -> { return workload(0xcafe000); };
				case 1:  return () -> { return workload(0xcafe001); };
				case 2:  return () -> { return workload(0xcafe002); };
				case 3:  return () -> { return workload(0xcafe003); };
				case 4:  return () -> { return workload(0xcafe004); };
				case 5:  return () -> { return workload(0xcafe005); };
				case 6:  return () -> { return workload(0xcafe006); };
				case 7:  return () -> { return workload(0xcafe007); };
				case 8:  return () -> { return workload(0xcafe008); };
				case 9:  return () -> { return workload(0xcafe009); };
				case 10: return () -> { return workload(0xcafe00a); };
				case 11: return () -> { return workload(0xcafe00b); };
				case 12: return () -> { return workload(0xcafe00c); };
				case 13: return () -> { return workload(0xcafe00d); };
				case 14: return () -> { return workload(0xcafe00e); };
				case 15: return () -> { return workload(0xcafe00f); };
				default: warnAboutBias(); return getCallee(0);
			}
		}
	}

	public static final class LambdasNop extends DispatchBench {
		Nullary getCallee(int which){
			switch (which) {
				case 0:  return () -> { return 0xcafe; };
				case 1:  return () -> { return 0xcafe; };
				case 2:  return () -> { return 0xcafe; };
				case 3:  return () -> { return 0xcafe; };
				case 4:  return () -> { return 0xcafe; };
				case 5:  return () -> { return 0xcafe; };
				case 6:  return () -> { return 0xcafe; };
				case 7:  return () -> { return 0xcafe; };
				case 8:  return () -> { return 0xcafe; };
				case 9:  return () -> { return 0xcafe; };
				case 10: return () -> { return 0xcafe; };
				case 11: return () -> { return 0xcafe; };
				case 12: return () -> { return 0xcafe; };
				case 13: return () -> { return 0xcafe; };
				case 14: return () -> { return 0xcafe; };
				case 15: return () -> { return 0xcafe; };
				default: warnAboutBias(); return getCallee(0);
			}
		}
	}

	public static final class LambdasNopDistinct extends DispatchBench {
		Nullary getCallee(int which){
			switch (which) {
				case 0:  return () -> { return 0xcafe000; };
				case 1:  return () -> { return 0xcafe001; };
				case 2:  return () -> { return 0xcafe002; };
				case 3:  return () -> { return 0xcafe003; };
				case 4:  return () -> { return 0xcafe004; };
				case 5:  return () -> { return 0xcafe005; };
				case 6:  return () -> { return 0xcafe006; };
				case 7:  return () -> { return 0xcafe007; };
				case 8:  return () -> { return 0xcafe008; };
				case 9:  return () -> { return 0xcafe009; };
				case 10: return () -> { return 0xcafe00a; };
				case 11: return () -> { return 0xcafe00b; };
				case 12: return () -> { return 0xcafe00c; };
				case 13: return () -> { return 0xcafe00d; };
				case 14: return () -> { return 0xcafe00e; };
				case 15: return () -> { return 0xcafe00f; };
				default: warnAboutBias(); return getCallee(0);
			}
		}
	}

	public static int _counter = 0;
	public static int workload(int arg) { return _counter += arg; } // Something the optimizer can't eliminate entirely

	boolean _alreadyWarned = false;
	void warnAboutBias() {
		if (!_alreadyWarned) {
			_alreadyWarned = true;
			out().println(" *** WARNING: Not all targets are unique -- dispatches will be biased.  If this is undesirable, try a lower polymorphism setting ***");
		}
	}

	// For comparison, these do no dispatches
	public static final class None extends DispatchBench {
		protected long doBatch(long numLoops, int numIterationsPerLoop) {
			long i=0, j=0;
			int sum = 0;
			startTimer();
			for (j = 0; j < numLoops; j++) for (i = 0; i < numIterationsPerLoop; i += 20) {
				sum += workload(1); sum += workload(1); sum += workload(1); sum += workload(1); sum += workload(1);
				sum += workload(1); sum += workload(1); sum += workload(1); sum += workload(1); sum += workload(1);
				sum += workload(1); sum += workload(1); sum += workload(1); sum += workload(1); sum += workload(1);
				sum += workload(1); sum += workload(1); sum += workload(1); sum += workload(1); sum += workload(1);
			}
			_counter += sum;
			return j * (i-20);
		}

		Nullary getCallee(int which){ return null; }
	
	}

}

