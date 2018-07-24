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

import javax.xml.bind.DatatypeConverter;

import net.adoptopenjdk.bumblebench.core.MicroBench;

public final class DigestBench extends MicroBench {

    static final int     payload = option("payload", 4096);
    static final String  alg     = option("algorithm", "SHA");

    static final byte[]  data    = new byte[payload];
    static MessageDigest md;
    static MessageDigest mdVerify; 

    static {
        Random r = new Random(10);
        r.nextBytes(data);

        try {
            md = MessageDigest.getInstance(alg);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        try {
            mdVerify = MessageDigest.getInstance("S"+alg);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Could not find verifying algorithm S"+alg);
        }
    }

    protected long doBatch(long numIterations) throws InterruptedException {
        for (int inner = 0; inner < numIterations; inner++) {
            md.reset();
            md.update(data);
            byte[] result = md.digest();
            if (mdVerify!=null) {
                mdVerify.reset();
                mdVerify.update(data);
                byte[] expected = mdVerify.digest();
                if (!Arrays.equals(expected, result)) {
                    throw new RuntimeException("Wrong SHA\nExpected: " + DatatypeConverter.printHexBinary(expected) + "\nActual: " + DatatypeConverter.printHexBinary(result));
                } else {
                    //System.err.print('.');
                }
            }
        }
        return numIterations;
    }

}
