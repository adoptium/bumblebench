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
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import net.adoptopenjdk.bumblebench.core.MicroBench;

public final class RSABench extends MicroBench {

    static KeyPairGenerator keyPairGenerator;
    static KeyGenerator keyGenerator;
    static Cipher     ka;
    static Cipher     kb;
    static KeyPair    alice_key_pair;
    static SecretKey  bob_key_pair;
    static final int        rsaSize = option("rsa_size", 2048);
    static final int        keySize = option("size", 128);

    static {
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyGenerator = KeyGenerator.getInstance(option("generator", "AES"));
            ka = Cipher.getInstance(option("agreement", "RSA"));
            kb = Cipher.getInstance(option("agreement", "RSA"));
            
            keyPairGenerator.initialize(rsaSize);
            keyGenerator.init(keySize);

            // System.out.print("Generating Alice DHkeypair....");
            alice_key_pair = keyPairGenerator.generateKeyPair();
            // System.out.println("Done.");

            // System.out.print("Generating Bob DHkeypair....");
            bob_key_pair = keyGenerator.generateKey();
            // System.out.println("Done.");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected long doBatch(long numIterations) throws InterruptedException {
        try {
            for (int i = 0; i < numIterations; i++) {
                PrivateKey alice_privateKey = (PrivateKey) alice_key_pair.getPrivate();
                // System.out.println("Alice DH Private Key:" +
                // alice_privateKey.toString());
                PublicKey alice_publicKey = (PublicKey) alice_key_pair.getPublic();
                // System.out.println("Alice DH Public Key:" +
                // alice_publicKey.toString());

                byte[] message = bob_key_pair.getEncoded();
                ka.init(Cipher.ENCRYPT_MODE, alice_publicKey);
                byte[] secretMessage = ka.doFinal(message);

                kb.init(Cipher.DECRYPT_MODE, alice_privateKey);
                byte [] sharedKey = kb.doFinal(secretMessage);
                
                if (!Arrays.equals(sharedKey, message)) {
                    throw new RuntimeException("Functional problem exists");
                }
            }
        } catch (InvalidKeyException e) {
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
