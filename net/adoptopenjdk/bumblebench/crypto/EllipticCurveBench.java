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
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;

import javax.crypto.KeyAgreement;

import net.adoptopenjdk.bumblebench.core.MicroBench;

public final class EllipticCurveBench extends MicroBench {

    static KeyPairGenerator keyPairGeneratora;
    static KeyPairGenerator keyPairGeneratorb;
    static KeyAgreement     ka;
    static KeyAgreement     kb;
    static final String     curve = option("curve", "secp256r1");

    static {
        try {
            keyPairGeneratora = KeyPairGenerator.getInstance(option("generator", "EC"));
            keyPairGeneratorb = KeyPairGenerator.getInstance(option("generator", "EC"));
            ka = KeyAgreement.getInstance(option("agreement", "ECDH"));
            kb = KeyAgreement.getInstance(option("agreement", "ECDH"));
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected long doBatch(long numIterations) throws InterruptedException {
        try {
            for (int i = 0; i < numIterations; i++) {
                keyPairGeneratora.initialize(new ECGenParameterSpec(curve), null);
                keyPairGeneratorb.initialize(new ECGenParameterSpec(curve), null);

                // System.out.print("Generating Alice DHkeypair....");
                KeyPair alice_key_pair = keyPairGeneratora.generateKeyPair();
                // System.out.println("Done.");

                // System.out.print("Generating Bob DHkeypair....");
                KeyPair bob_key_pair = keyPairGeneratorb.generateKeyPair();
                // System.out.println("Done.");

                PrivateKey alice_privateKey = (PrivateKey) alice_key_pair.getPrivate();
                // System.out.println("Alice DH Private Key:" +
                // alice_privateKey.toString());
                PublicKey alice_publicKey = (PublicKey) alice_key_pair.getPublic();
                // System.out.println("Alice DH Public Key:" +
                // alice_publicKey.toString());

                PrivateKey bob_privateKey = (PrivateKey) bob_key_pair.getPrivate();
                // System.out.println("Bob DH Private Key:" +
                // bob_privateKey.toString());
                PublicKey bob_publicKey = (PublicKey) bob_key_pair.getPublic();
                // System.out.println("Bob DH Public Key:" +
                // bob_publicKey.toString());

                ka.init(alice_privateKey);
                ka.doPhase(bob_publicKey, true);

                // Generate the secret key
                byte[] secretKey1 = ka.generateSecret();

                kb.init(bob_privateKey);
                kb.doPhase(alice_publicKey, true);

                // Generate the secret key
                byte[] secretKey2 = kb.generateSecret();
                
                if (!Arrays.equals(secretKey1, secretKey2))
                    throw new RuntimeException("Functional error.");

            }
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return numIterations;
    }

}
