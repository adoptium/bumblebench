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

import net.adoptopenjdk.bumblebench.core.MicroBench;

import java.io.*;
import java.net.*;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.*;

public class SSLSocketBench extends MicroBench {
    static final TrustManager[] trustAllCerts = new TrustManager[] { 
        new X509TrustManager() {     
            public java.security.cert.X509Certificate[] getAcceptedIssuers() { 
                return new X509Certificate[0];
            } 
            public void checkClientTrusted( 
                    java.security.cert.X509Certificate[] certs, String authType) {
                    } 
            public void checkServerTrusted( 
                    java.security.cert.X509Certificate[] certs, String authType) {
                    }
        } 
    }; 

    static {
        KeyStore ks;
        InputStream ksIs = null;
        try {
            ks = KeyStore.getInstance("JKS");
            ksIs = new FileInputStream("/home/vpapro/mySrvKeystore");
            ks.load(ksIs, "Liberty".toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, "Liberty".toCharArray());

            SSLContext sc = SSLContext.getInstance("TLSv1");
            //sc.init(kmf.getKeyManagers(), trustAllCerts, new java.security.SecureRandom());
            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected long doBatch(long numIterations) throws InterruptedException {
        String line;
        String result = "";
        try {
            for (long i = 0; i < numIterations; i++) {
                SSLContext sc = SSLContext.getInstance("TLSv1");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                URL url = new URL("https://localhost:9443/HelloLibertyApplication/HelloLiberty");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                InputStream instr = conn.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(instr));

                while ((line = rd.readLine()) != null) {
                    result += line;
                }
                //rd.close();
                //instr.close();
                //System.out.println(result);
                conn.disconnect();
            }

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (KeyManagementException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return numIterations;
    }

}
