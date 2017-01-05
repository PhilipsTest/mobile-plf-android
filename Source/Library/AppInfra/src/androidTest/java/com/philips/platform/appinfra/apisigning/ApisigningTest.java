package com.philips.platform.appinfra.apisigning;

import com.philips.platform.appinfra.MockitoTestCase;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by 310190722 on 11/25/2016.
 */

public class ApisigningTest extends MockitoTestCase {

    private PshmacLib pshmacLib;
    private HSDPPHSApiSigning hsdpphsApiSigning;
    @Override
    protected void setUp() throws Exception {
        pshmacLib = new PshmacLib();
        hsdpphsApiSigning = new HSDPPHSApiSigning("cafebabe-1234-dead-dead-1234567890ab","e124794bab4949cd4affc267d446ddd95c938a7428d75d7901992e0cb4bc320cd94c28dae1e56d83eaf19010ccc8574d6d83fb687cf5d12ff2afddbaf73801b5");
        super.setUp();
    }

    public void testApisigning(){
        Map<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("SignedDate","2016-11-09T13:31:13.492+0000");
        String result = hsdpphsApiSigning.createSignature("POST","applicationName=uGrow",headers,"/authentication/login/social",null).trim();
        assertEquals("HmacSHA256;Credential:cafebabe-1234-dead-dead-1234567890ab;SignedHeaders:SignedDate,;Signature:UDFszoNOtDoqBsdD91S0Wl/IsT/JL9T3xNNy8JjXG1M=",result);
    }

    public void testPsHmacLibReturnByteArray(){
        byte[] key = hexStringToByteArray("e124794bab4949cd4affc267d446ddd95c938a7428d75d7901992e0cb4bc320cd94c28dae1e56d83eaf19010ccc8574d6d83fb687cf5d12ff2afddbaf73801b5e1");
        byte[] httpMethodType = {'P','O','S','T'};
        byte[] resultBytes = pshmacLib.createHmac(key,httpMethodType);
        assertEquals("aefb42a311b78e4514ea4dc59120211bf421edc4978426309ab11b2e205a329b",bytesToHex(resultBytes));
    }


    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private static String bytesToHex(byte[] input) {
        final StringBuilder builder = new StringBuilder();
        for(byte b : input) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

}
