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
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;

import javax.xml.bind.DatatypeConverter;

import net.adoptopenjdk.bumblebench.core.MicroBench;

public final class HMACBench extends MicroBench {

    static final int     len = option("payload", 4096);
    static final String  alg = option("algorithm", "HmacSHA256");

    static final byte[]  data;
    static Mac mac;
    static SecretKeySpec key;
    static {
        data = new byte[len];
        Random r = new Random(10);
        r.nextBytes(data);

        key = new SecretKeySpec(data, alg);

        String provider = option("provider_name", "");
        try {
            if (provider.equals("")) {
                mac = Mac.getInstance(alg);
            } else if (provider.equals("IBMJCEPlus")) {
                java.security.Provider java_provider = java.security.Security.getProvider("IBMJCEPlus");
                if( java_provider == null ) {
                    java_provider = (java.security.Provider)Class.forName("com.ibm.crypto.plus.provider.IBMJCEPlus").newInstance();
                    java.security.Security.insertProviderAt(java_provider, 1);
                }
                mac = Mac.getInstance(alg, java_provider);
            } else {
                mac = Mac.getInstance(alg, provider);
            }
            System.out.println("Using Provider " + mac.getProvider().getName());
            System.out.println("Payload size: "+ data.length + " bytes");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();

        }
    }

    protected long doBatch(long numBytes) throws InterruptedException {
        long numIterations = java.lang.Math.round((double)numBytes/data.length);
        try{
            for (long i = 0; i < numIterations; i++) {
                mac.init(key);
                byte[] result = mac.doFinal(data);
            }
        } catch (InvalidKeyException e){
            e.printStackTrace();
        }
        return numIterations*data.length;
    }
}
