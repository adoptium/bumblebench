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

package net.adoptopenjdk.bumblebench.examples;

import net.adoptopenjdk.bumblebench.core.MicroBench;

public final class LockContention extends MicroBench {
	static final boolean BUSY_WAIT = Boolean.parseBoolean(System.getProperty("LockContention.BUSY_WAIT"));

	static final Synch synch = new Synch();

	static Boolean init = false;

	protected long doBatch(long numIterations) throws InterruptedException {
		if (!init) {
			synchronized(init) {
				if (!init) {
					System.out.println("[INPUT] BUSY_WAIT: " + BUSY_WAIT);
					System.out.println("[INPUT] WORK_INTERVAL: " + Synch.INTERVAL);
					System.out.println("[INPUT] NUM_THREADS: " + System.getProperty("BumbleBench.parallelInstances"));
					init = true;
				}
			}
		}
		
		for (long i = 0; i < numIterations; i++) {
			if (BUSY_WAIT) {
				synch.doWorkBusyWait(i);
			} else {
				synch.doWorkThreadSleep(i);
			}
		}

		return numIterations;
	}
}
