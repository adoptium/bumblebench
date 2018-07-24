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

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.List;
import java.util.stream.Stream;

import net.adoptopenjdk.bumblebench.core.MiniBench;

public final class SieveBench extends MiniBench {

	final static int MAX_PRIMES = option("maxPrimes", 1000);

	protected int maxIterationsPerLoop(){ return MAX_PRIMES; }

	protected long doBatch(long numLoops, int numIterationsPerLoop) throws InterruptedException {
		// The algorithm seems to act like it's n^2 so we sqrt the iterations to
		// make the score less dependent on MAX_PRIMES
		//
		int numPrimes = sqrt(numIterationsPerLoop);

		List<Integer> output = new ArrayList<>();
		for (long i = 0; i < numLoops; i++) {
			startTimer();
			Sieve.primes(numPrimes, output);
			pauseTimer();
		}
		return numLoops * numIterationsPerLoop;
	}

	static class Sieve {
		
		/*
		 * Naive(!!) Java 8 implementation of Sieve of Eratosthenes, using lambdas
		 */
		
		static public List<Integer> primes(int n, List<Integer> output) { 
			
			List<Integer> primes = new ArrayList<>(); 				//create list to hold primes (gets consumed via filters)
			Stream<Integer> s = Stream.iterate(2, x -> x + 1);		//create stream of integers larger than 2
			
			
			//main loop finds first n primes
			for (int i = 0; i < n; i++) { 
				int k = i; 
				Consumer<Integer> kthPrime = kthPrime(k, primes);	//create consumer  
				s = s.peek(kthPrime).filter(x -> {int p = currPrime(k,primes); 
															return x == p || x % p != 0;}); 
			}
			
			s.limit(n).forEach(x -> output.add(x));	//maybe a timing hit? TODO: test later this vs adding from primes
			return output;
		} 
		
	  /*
		* Find the prime that is used in the kth filter. That is the kth prime 
		* if it has been discovered.  Otherwise it is a smaller prime that 
		* is currently passing through the stream and must be let through the 
		* filter. 
		*/ 
		
		static private Integer currPrime(int k, List<Integer> primes) { 
			return primes.get(Math.min(k,primes.size()-1)); 
		} 
		
		/*
		 *  Mark the kth prime number for reference in a subsequent filter. 
		 */ 
		
		static private Consumer<Integer> kthPrime(int k, List<Integer> primes) { 
			return new Consumer<Integer>() { 
			  private int idx; 
		
			  @Override 
			  public void accept(Integer p) { 
				 if (idx > k) return; 
				 if (idx == k) primes.add(p); 
				 idx++; 
			  } 
			}; 
		} 
	}
}

