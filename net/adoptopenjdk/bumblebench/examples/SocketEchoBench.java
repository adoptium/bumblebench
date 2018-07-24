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

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import net.adoptopenjdk.bumblebench.core.MicroBench;

public final class SocketEchoBench extends MicroBench {

	// Illustrates a MicroBench with two threads, and a complex setup step
	// before the batches begin.  Overrides bumbleMain to perform the setup, and
	// uses a helper thread inheriting CountDownLatch to coordinate the starting
	// of the two threads.

	BufferedReader _clientIn;
	PrintWriter    _clientOut;

	static final String STRING_TO_SEND = repeat(
		option("stringToSend", "All work and no play makes Jack a dull boy"),
		option("repsPerSend", 100)
		).intern();

	protected long doBatch(long numIterations) throws InterruptedException {
		long bytesRead = 0;
		try {
			while (bytesRead < numIterations) {
				_clientOut.println(STRING_TO_SEND);
				bytesRead += _clientIn.readLine().length();
			}
		} catch (IOException e) {
			throw new Error(e);
		}
		return bytesRead;
	}

	static final int PORT = option("port", 60000);

	static class EchoServer extends CountDownLatch implements Runnable {

		public void run() {
			try ( 
    			ServerSocket serverSocket = openServerSocket(PORT);
    			Socket socket = serverSocket.accept();
    			BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    			PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), true);
			) {
				String receivedString;
				while ((receivedString = serverIn.readLine()) != null)
					serverOut.println(receivedString);
			} catch (Exception e) {
				if (VERBOSE) {
					out().println(this.getClass().getSimpleName() + " caught " + e.getClass().getSimpleName());
					e.printStackTrace(out());
				}
			}
		}

		ServerSocket openServerSocket(int port) throws Exception {
			ServerSocket result = new ServerSocket(PORT);
			countDown(); // Release other threads waiting for this one to listen on the port
			return result;
		}

		EchoServer() { super(1); }

	}

	public void bumbleMain() throws Exception {
		// Ordinarily, the sort of initialization we're doing here could go in
		// the constructor.  However, we must also shut down the server thread
		// when we're done or the benchmark hangs.  Overriding bumbleMain gives
		// us a chance to do some cleanup at the end.

		// Setup
		//
		EchoServer s = new EchoServer();
		Thread serverThread = new Thread(s, "EchoServer");
		serverThread.start();
		s.await(); // Wait until the server is ready to accept client connections
		System.gc();
		Socket socket = new Socket("localhost", PORT);
    	_clientIn  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    	_clientOut = new PrintWriter(socket.getOutputStream(), true);

		// Run the benchmark
		//
		super.bumbleMain();

		// Cleanup
		//
		_clientOut.close();
		_clientIn.close();
		serverThread.interrupt();
		serverThread.join();
	}

	static String repeat(String base, int count) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++)
			sb.append(base);
		return sb.toString();
	}

}

