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
import java.lang.RuntimeException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import net.adoptopenjdk.bumblebench.core.MicroBench;

public final class GCMBench extends MicroBench {
    // 128 bits
    static final SecretKeySpec skey_128 = new SecretKeySpec(new byte[] { -80, -103, -1, 68, -29, -94, 61, -52, 93, -59, -128, 105, 110, 88, 44, 105 }, "AES");
    // 256 bits
    static final SecretKeySpec skey_256 = new SecretKeySpec(new byte[] { -80, -103, -1, 68, -29, -94, 61, -52, 93, -59, -128, 105, 110, 88, 44, 105, -80, -103, -1, 68, -29, -94, 61, -52, 93, -59, -128, 105, 110, 88, 44, 105 }, "AES");
    static final SecretKeySpec skey;
    // 128 bits
    static byte[]        iv;
    static AlgorithmParameterSpec iva;

    static final int           len;
    static final String        alg = "AES";
    static final String        mode = "GCM";
    static final String        padding = "NoPadding";
    static final byte[]        data;
    static final byte[]        out11;
    static final int           modeInt;
    static int                buffer;
    // 128 bit
    static final byte[] aada = new byte[16];
    static Cipher              cipher;
    static Random r;
    static {
        len = option("payload", 4096);
        int ivSize = option("IVSize", 12);

        String algorithm = option("algorithm", "AES-128-GCM");
        String mode      = option("mode", "encrypt");
        buffer = option("bufferSize", 0);
        // make sure largeBuffer >= len and largeBuffer is a multiple of len
        if (buffer < len)
            buffer = len;
        if (buffer % len != 0)
            buffer += (len - (buffer % len));

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

        data = new byte[buffer];
        out11 = new byte[(buffer / len) * (len+16)];
        iv = new byte[ivSize];
        r = new Random(10);
        r.nextBytes(data);


        String provider = option("provider_name", "");
     try {
            if (provider.equals("")) {
                cipher = Cipher.getInstance("AES/GCM/NoPadding");
            } else {
                if (provider.equals("IBMJCEPlus")) {
                   java.security.Provider java_provider = java.security.Security.getProvider("IBMJCEPlus");
                   if( java_provider == null ) {
                        java_provider = (java.security.Provider)Class.forName("com.ibm.crypto.plus.provider.IBMJCEPlus").newInstance();
                        java.security.Security.insertProviderAt( java_provider, 1 );
                   }
                   cipher = Cipher.getInstance("AES/GCM/NoPadding", java_provider);
                } else if (provider.equals("J9JCE")) {
                   java.security.Provider java_provider = java.security.Security.getProvider("J9JCE");
                   if( java_provider == null ) {
                        java_provider = (java.security.Provider)Class.forName("com.ibm.security.J9JCE").newInstance();
                        java.security.Security.insertProviderAt( java_provider, 1 );
                   }
                   cipher = Cipher.getInstance("AES_128/GCM/NoPadding", java_provider);

                } else {
                   cipher = Cipher.getInstance("AES/GCM/NoPadding", provider);
                }
            }
            System.out.println("Using Provider " + cipher.getProvider().getName());
            System.out.println("Payload size: "+ len + " bytes");
        } catch (Exception e) {
        System.out.println("Error instantiating IBMJCEPlus provider." );
            e.printStackTrace();
            System.exit(1);
        }

        // Decryption
        if (modeInt == 0) {
            if (len != buffer)
                throw new RuntimeException("No support for large buffer mode on GCM decrypt");
            try {
                r.nextBytes(iv);
                iva = new GCMParameterSpec(16 * 8, iv);
                cipher.init(Cipher.ENCRYPT_MODE, skey, iva);
                cipher.updateAAD(aada, 0, aada.length);
                cipher.doFinal(data, 0, data.length, out11);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(1);
            }
            
        }
    }

    protected long doBatch(long numBytes) throws InterruptedException {
        long numIterations = java.lang.Math.round((double)numBytes/data.length)+1;
        long numBuffers = buffer / len;
        try {
            for (long i = 0; i < numIterations; i++) {
                if (modeInt == 1) {
                    r.nextBytes(iv);
                    iva = new GCMParameterSpec(16 * 8, iv);
                    cipher.init(Cipher.ENCRYPT_MODE, skey, iva);
                    cipher.updateAAD(aada, 0, aada.length);
                    cipher.doFinal(data, (int) ((i % numBuffers) * len), (int) len, out11, (int) ((i % numBuffers) * (len + 16)));
                } else if (modeInt == 0) {
                    cipher.init(Cipher.DECRYPT_MODE, skey, iva);
                    cipher.updateAAD(aada, 0, aada.length);
                    cipher.doFinal(out11, 0, out11.length, data);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return numIterations*len;
    }
}
