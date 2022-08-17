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

public class Synch {
	static final int INTERVAL = Integer.parseInt(System.getProperty("Synch.WORK_INTERVAL"));

	private long count;

	public Synch() {
		if ((INTERVAL < 0) && (INTERVAL > 999999)) {
			throw new IllegalArgumentException("SYNCH.WORK_INTERVAL should be between 0-999999");
		}
		count = 0;
	}

	public void doWorkBusyWait(long value) {
		synchronized (this) {
			this.count += value;
			
			/* Busy wait. This can cause CPU thrashing. */
			long start = System.nanoTime();
			long end = 0;
			do {
				end = System.nanoTime();
			} while(start + INTERVAL >= end);
		}
	}

	public void doWorkThreadSleep(long value) throws InterruptedException {
		synchronized (this) {
			this.count += value;
			
			/* Thread.sleep has an overhead. Use busy wait to avoid this overhead. */
			Thread.sleep(0, INTERVAL);
		}
	}

	public long getCount() {
		return count;
	}
}
