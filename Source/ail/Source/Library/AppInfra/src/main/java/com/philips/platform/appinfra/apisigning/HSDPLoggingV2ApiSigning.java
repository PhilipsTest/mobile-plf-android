package com.philips.platform.appinfra.apisigning;

import android.util.Base64;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by abhishek on 5/31/18.
 */

public class HSDPLoggingV2ApiSigning implements ApiSigningInterface {
    private static final long serialVersionUID = 3562929075126940567L;
    private byte[] secretKey;
    public static final Charset UTF_8_CHARSET = Charset.forName("UTF-8");

    public HSDPLoggingV2ApiSigning(String secretKey) {
        this.secretKey = hexStringToByteArray(secretKey);
    }


    @Override
    public String createSignature(String requestMethod, String queryString, Map<String, String> headers, String dhpUrl, String requestbody) {
        return null;
    }

    public String createSignatureForCloudLogging(String date) {
        PsLib psLib = new PsLib();
        final byte[] dateByteArray;
        dateByteArray = date.getBytes(UTF_8_CHARSET);
        String dateBase64 = Base64.encodeToString(dateByteArray, Base64.NO_WRAP);
        byte[] signatureArray = psLib.createHmac(secretKey, dateBase64.getBytes(Charset.forName("UTF-8")));
        return Base64.encodeToString(signatureArray, Base64.NO_WRAP);
    }

    private static byte[] hexStringToByteArray(String s) {
        final int len = s.length();
        final byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
//        Log.v(AppInfraLogEventID.AI_API_SIGNING, "hexString To ByteArray type Casting");
        return data;
    }
}
