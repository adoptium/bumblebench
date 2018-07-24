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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SerializeBenchmark extends MicroBench {
	private static final int SERIALIZE_COUNT = 10_000;
	
	/* Used so that the jit won't melt us away */
	static int i = 0;
	static int j = 0;

	
	private static <T> T reserialize(T o) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		
		oos.writeObject(o);
		
		oos.close();
		
		ObjectInputStream iis = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
		
		try {
			o = (T)iis.readObject();
		} catch (ClassNotFoundException e) {
			throw new IOException(e);
		}
		iis.close();
		return o;
	}
	
	static V_method serialize_lambda = () -> {
		final int local_j = j++;
		I_method lambda = (I_method & Serializable) (value) -> {
			return value+local_j;
		};
		try {
			j = reserialize(lambda).call(i);
		}catch(Exception e){e.printStackTrace();}
	};
	
	static Benchmark serializer = () -> {
		for(int i = 0; i < SERIALIZE_COUNT; i++){
			serialize_lambda.call();
		}
	};


	final int doIterations(int numIterations) {
		for (int i = 0; i < numIterations; i++)
			serializer.run();
		return numIterations;
	}

	public static void main(String[] args){ new SerializeBenchmark().run(args); }

}
