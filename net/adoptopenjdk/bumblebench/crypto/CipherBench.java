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
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import net.adoptopenjdk.bumblebench.core.MicroBench;

public final class CipherBench extends MicroBench {

    static final SecretKeySpec skey       = new SecretKeySpec(new byte[] { -80, -103, -1, 68, -29, -94, 61, -52, 93, -59, -128, 105, 110, 88, 44, 105 }, "AES");
    static final SecretKeySpec skeydesede = new SecretKeySpec(new byte[] { -80, -103, -1, 68, -29, -94, 61, -52, 93, -59, -128, 105, 110, 88, 44, 105, 29, -94, 61, -52, 93, -59,
            -128, 105                    }, "DESede");
    static final SecretKeySpec skeydes    = new SecretKeySpec(new byte[] { 29, -94, 61, -52, 93, -59, -128, 105 }, "DES");

    static final SecretKeySpec keyArr[]   = { skeydes, skeydesede, skey };

    static final byte[]        iv         = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
    static final byte[]        ivdes      = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
    static final byte[][]      ivArr      = { ivdes, ivdes, iv };

    static final int           len;
    static final String        alg;
    static final String        mode;
    static final int           algIndex;
    static final byte[]        data;

    static Cipher              ciphera;
    static Cipher              cipherb;
    static {
        len = option("payload", 4096);
        alg = option("algorithm", "AES");
        mode = option("mode", "CBC");
        if (alg.equals("DES")) {
            algIndex = 0;
        } else if (alg.equals("DESede")) {
            algIndex = 1;
        } else if (alg.equals("AES")) {
            algIndex = 2;
        } else {
            algIndex = -1;
        }

        data = new byte[len];
        Random r = new Random(10);
        r.nextBytes(data);

        try {
            ciphera = Cipher.getInstance(alg + '/' + mode + "/PKCS5Padding");
            cipherb = Cipher.getInstance(alg + '/' + mode + "/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    protected long doBatch(long numIterations) throws InterruptedException {
        IvParameterSpec iviv = new IvParameterSpec(ivArr[algIndex]);

        try {
            ciphera.init(Cipher.ENCRYPT_MODE, keyArr[algIndex], iviv);
            cipherb.init(Cipher.DECRYPT_MODE, keyArr[algIndex], iviv);

            for (int i = 0; i < numIterations; i++) {
                byte[] out11 = ciphera.doFinal(data);
                byte[] out2 = cipherb.doFinal(out11);
            }
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return numIterations;
    }

}
