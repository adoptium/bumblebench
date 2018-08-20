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

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import net.adoptopenjdk.bumblebench.core.MicroBench;

public final class CipherBench extends MicroBench {

    // 128 bits
    static final SecretKeySpec skey_128 = new SecretKeySpec(new byte[] { -80, -103, -1, 68, -29, -94, 61, -52, 93, -59, -128, 105, 110, 88, 44, 105 }, "AES");
    // 256 bits
    static final SecretKeySpec skey_256 = new SecretKeySpec(new byte[] { -80, -103, -1, 68, -29, -94, 61, -52, 93, -59, -128, 105, 110, 88, 44, 105, -80, -103, -1, 68, -29, -94, 61, -52, 93, -59, -128, 105, 110, 88, 44, 105 }, "AES");
    static final SecretKeySpec skey;

    // 128 bits
    static final byte[]        iv;
    static final int           len;
    static final byte[]        data;
    static final byte[]        out;
    static final int           modeInt;
    // 128 bit
    static Cipher              cipher;
    static {

        // 
        len = option("payload", 4096);
        String algorithm = option("algorithm", "AES-128-CBC");

        data = new byte[len];
	    out  = new byte[len];
        iv   = new byte[16];
        Random r = new Random(10);
        r.nextBytes(data);
        r.nextBytes(iv);

        String mode = option("mode", "encrypt");
        if (mode.equals("encrypt")) {
            modeInt = 1;
        } else if (mode.equals("decrypt")) {
            modeInt = 0;
        } else {
            throw new RuntimeException("Unsupported mode");
        }

        if (algorithm.contains("128")) {
            skey = skey_128;
        } else if (algorithm.contains("256")) {
            skey = skey_256;
        } else {
            skey = null;
            throw new RuntimeException("Unsupported key size");
        }

        String cipherMode;
        if (algorithm.contains("CBC")) {
            cipherMode = "AES/CBC/NoPadding";
        } else if (algorithm.contains("CTR")) {
            cipherMode = "AES/CTR/NoPadding";
        } else {
            throw new RuntimeException("Only CBC and CTR cipher modes available");
        }

        String provider = option("provider_name", "");
        try {
            if (provider.equals("")) {
                cipher = Cipher.getInstance(cipherMode);
	    } else { if (provider.equals("IBMJCEPlus")) {
                   java.security.Provider java_provider = java.security.Security.getProvider("IBMJCEPlus");
                   if( java_provider == null ) { 
                        java_provider = (java.security.Provider)Class.forName("com.ibm.crypto.plus.provider.IBMJCEPlus").newInstance();
                        java.security.Security.insertProviderAt( java_provider, 1 );
                   }
                   cipher = Cipher.getInstance(cipherMode, java_provider);

            } else {
                cipher = Cipher.getInstance(cipherMode, provider);
            }
	    }

            System.out.println("Using Provider " + cipher.getProvider().getName());
            System.out.println("Payload size: "+ data.length + " bytes");
            AlgorithmParameterSpec iviv = new IvParameterSpec(iv);
            if (modeInt == 0) {
                cipher.init(Cipher.DECRYPT_MODE, skey, iviv);
            } else {
                cipher.init(Cipher.ENCRYPT_MODE, skey, iviv);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    protected long doBatch(long numBytes) throws InterruptedException {
        long numIterations = java.lang.Math.round((double)numBytes/data.length)+1;
        
        for (long i = 0; i < numIterations; i++) {
            try {
                cipher.update(data, 0, data.length, out);
            } catch(Exception e) {
                System.exit(1);
            }
        }
        return numIterations*data.length;
    }
}
