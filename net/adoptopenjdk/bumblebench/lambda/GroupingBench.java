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
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import net.adoptopenjdk.bumblebench.core.MiniBench;

public abstract class GroupingBench extends MiniBench {

	static final int NUM_PEOPLE = option("numPeople", 10000);

	protected int maxIterationsPerLoop(){ return NUM_PEOPLE; }

	protected long doBatch(long numLoops, int numIterationsPerLoop) throws InterruptedException {
		for (long i = 0; i < numLoops; i++) {
			List<Person> list = newList(numIterationsPerLoop);
			startTimer();
			Object result = coreOperation(list);
			pauseTimer();
			_escape += result.hashCode();
		}
		return numLoops * numIterationsPerLoop;
	}

	abstract Object coreOperation(List<Person> list);

	public final static class Serial extends GroupingBench {
		Object coreOperation(List<Person> list) {
			return list.stream().collect(Collectors.groupingBy(Person::getGender));
		}
	}

	public final static class Parallel extends GroupingBench {
		Object coreOperation(List<Person> list) {
			return list.stream().collect(Collectors.groupingByConcurrent(Person::getGender));
		}
	}

	int _escape;

	List<Person> newList(int size) {
		List<Person> result = new ArrayList<Person>(size);
		Random r = newRandom();
		for (int i = 0; i < size; i++) {
			result.add(new Person(
				r.nextBoolean()? "Jamie" : "Terry",
				r.nextBoolean()? Person.Gender.MALE: Person.Gender.FEMALE));
		}
		return result;
	}

	static class Person {
		public enum Gender { MALE, FEMALE }
	
		String _name;
		Gender _gender;

		public Person(String name, Gender gender){
			_name   = name;
			_gender = gender;
		}

		public String getName()   { return _name;   }
		public Gender getGender() { return _gender; }

	}
}

