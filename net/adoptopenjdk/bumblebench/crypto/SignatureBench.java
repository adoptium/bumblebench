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

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import net.adoptopenjdk.bumblebench.core.MicroBench;

public final class SignatureBench extends MicroBench {

    static KeyPair   alice_key_pair;
    static Signature sig;
    static byte[]    data;
    static byte[]    signatureBytes;
    
    static int exeMode = -1;

    static {

        String mode = option("mode", "Sign");
        if (mode.equals("Sign")) {
           exeMode = 1;
        } else if (mode.equals("Verify")) {
           exeMode = 2;
        }

        try {
            String keyType = option("key_type", "EC");
            String algorithmName = "SHA256withECDSA";
            if (keyType.equals("EC")) {
                algorithmName = "SHA256withECDSA";
            } else if (keyType.equals("RSA")) {
                algorithmName = "SHA256withRSA";
            } else {
                throw new RuntimeException();
            }
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(keyType);

            String provider = option("provider_name", "");
            if (provider.equals("")) {
                sig = Signature.getInstance(algorithmName);
            } else {
                sig = Signature.getInstance(algorithmName, provider);
                if (sig == null)
                   throw new RuntimeException();
            }
            
            int keySize = option("key_size", 256);
            keyPairGenerator.initialize(keySize);
            signatureBytes = new byte[keySize];
            
            int len = option("payload", 36);
            data = new byte[len];
            Random r = new Random(10);
            r.nextBytes(data);

            alice_key_pair = keyPairGenerator.generateKeyPair();
            if (exeMode == 1) {
                sig.initSign(alice_key_pair.getPrivate());
            }
            else if (exeMode == 2) {
               sig.initSign(alice_key_pair.getPrivate());
               sig.update(data);
               signatureBytes = sig.sign();
               if (signatureBytes == null)
                   throw new RuntimeException();
               sig.initVerify(alice_key_pair.getPublic());
            }
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SignatureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    protected long doBatch(long numIterations) throws InterruptedException {
        try {
            for (long i = 0; i < numIterations; i++) {
                if (exeMode == 1) {
                   sig.update(data);
                   signatureBytes = sig.sign();
                } else if (exeMode == 2) {
                   sig.update(data);
                   sig.verify(signatureBytes, 0, signatureBytes.length);
                }
            }
        } catch (SignatureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return numIterations;
    }

}
