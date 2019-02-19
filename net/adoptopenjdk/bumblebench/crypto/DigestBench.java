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

package net.adoptopenjdk.bumblebench.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import java.util.Random;

import net.adoptopenjdk.bumblebench.core.MicroBench;

public final class DigestBench extends MicroBench {

    static final int     len = option("payload", 4096);
    static final String  alg = option("algorithm", "SHA-256");

    static final byte[]  data;
    static MessageDigest md;

    static {
        data = new byte[len];
        Random r = new Random(10);
        r.nextBytes(data);

        String provider = option("provider_name", "");
        try {
            if (provider.equals("")) {
                md = MessageDigest.getInstance(alg);
            } else {
                if (provider.equals("IBMJCEPlus")) {
                    java.security.Provider java_provider = java.security.Security.getProvider("IBMJCEPlus");
                    if( java_provider == null ) { 
                        java_provider = (java.security.Provider)Class.forName("com.ibm.crypto.plus.provider.IBMJCEPlus").newInstance();
                        java.security.Security.insertProviderAt( java_provider, 1 );
                    }
                    md = MessageDigest.getInstance(alg, java_provider);
                } else {
                    md = MessageDigest.getInstance(alg, provider);
                }
            }
            System.out.println("Using Provider " + md.getProvider().getName());
            System.out.println("Payload size: "+ data.length + " bytes");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected long doBatch(long numBytes) throws InterruptedException {

        long numIterations = java.lang.Math.round((double)numBytes/data.length);
        for (long i = 0; i < numIterations; i++) {
            md.reset();
            byte[] result = md.digest(data);
        }
        return numIterations*data.length;
    }

}
