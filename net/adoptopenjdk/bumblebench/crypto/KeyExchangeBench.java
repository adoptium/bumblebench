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
import java.security.InvalidAlgorithmParameterException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

import javax.crypto.KeyAgreement;

import net.adoptopenjdk.bumblebench.core.MicroBench;

import java.security.spec.AlgorithmParameterSpec;
import java.security.AlgorithmParameters;
import java.security.AlgorithmParameterGenerator;
import javax.crypto.spec.DHParameterSpec;

import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.EllipticCurve;

public final class KeyExchangeBench extends MicroBench {

    static KeyPairGenerator keyPairGeneratora;
    static KeyPairGenerator keyPairGeneratorb;
    static KeyPair alice_key_pair;
    static KeyPair bob_key_pair;
    static KeyAgreement     ka;
    static KeyAgreement     kb;
    static final String  algorithm = option("algorithm", "DH");
    static final String  curve = option("curve", "secp256r1");

    static {
        try {
            AlgorithmParameterSpec params = null;
            String generator = "";
            String agreement = "";
            if (algorithm.equals("DH")) {
                agreement = generator = "DH";
                AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
                paramGen.init(1024);
                AlgorithmParameters params1 = paramGen.generateParameters();
                params = (DHParameterSpec)params1.getParameterSpec
                    (DHParameterSpec.class);
            } else if (algorithm.equals("ECDH")) {
                generator = "EC";
                agreement = "ECDH";
                params = new ECGenParameterSpec(curve);
            } else {
                System.err.println("Unrecognized algorithm: "+ algorithm);
                System.exit(1);
            }

            String provider = option("provider_name", "");
            if (provider.equals("")) {
                keyPairGeneratora = KeyPairGenerator.getInstance(generator);
                keyPairGeneratorb = KeyPairGenerator.getInstance(generator);
                ka = KeyAgreement.getInstance(agreement);
                kb = KeyAgreement.getInstance(agreement);
            } else {
                if (provider.equals("IBMJCEPlus")) {
                    java.security.Provider java_provider = java.security.Security.getProvider("IBMJCEPlus");
                    if( java_provider == null ) {
                        java_provider = (java.security.Provider)Class.forName("com.ibm.crypto.plus.provider.IBMJCEPlus").newInstance();
                        java.security.Security.insertProviderAt( java_provider, 1 );
                    }
                    keyPairGeneratora = KeyPairGenerator.getInstance(generator, java_provider);
                    keyPairGeneratorb = KeyPairGenerator.getInstance(generator, java_provider);
                    ka = KeyAgreement.getInstance(agreement, java_provider);
                    kb = KeyAgreement.getInstance(agreement, java_provider);
                } else {
                    keyPairGeneratora = KeyPairGenerator.getInstance(generator, provider);
                    keyPairGeneratorb = KeyPairGenerator.getInstance(generator, provider);
                    ka = KeyAgreement.getInstance(agreement, provider);
                    kb = KeyAgreement.getInstance(agreement, provider);
                }
            }
            keyPairGeneratora.initialize(params);
            keyPairGeneratorb.initialize(params);

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected long doBatch(long numIterations) throws InterruptedException {
        try {
            for (long i = 0; i < numIterations; i++) {
                KeyPair alice_key_pair = keyPairGeneratora.generateKeyPair();
                KeyPair bob_key_pair = keyPairGeneratorb.generateKeyPair();
                ka.init(alice_key_pair.getPrivate());
                ka.doPhase(bob_key_pair.getPublic(), true);

                // Generate the secret key
                byte[] secretKey1 = ka.generateSecret();

                // kb.init(bob_privateKey);
                // kb.doPhase(alice_publicKey, true);

                // Generate the secret key
                //byte[] secretKey2 = kb.generateSecret();

                //if (!Arrays.equals(secretKey1, secretKey2))
                //    throw new RuntimeException("Functional error.");

            }
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return numIterations;
    }

}
