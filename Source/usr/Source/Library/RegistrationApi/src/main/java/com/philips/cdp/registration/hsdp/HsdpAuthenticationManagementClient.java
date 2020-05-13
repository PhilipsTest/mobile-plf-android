package com.philips.cdp.registration.hsdp;

import android.util.Base64;

import com.philips.cdp.registration.configuration.HSDPConfiguration;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.ServerTime;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HsdpAuthenticationManagementClient extends HsdpRequestClient {

    private String TAG = HsdpAuthenticationManagementClient.class.getSimpleName();
    private HSDPConfiguration hsdpConfiguration;
    String  appName = "";

    HsdpAuthenticationManagementClient(HSDPConfiguration hsdpConfiguration, String hsdpAppName) {
        super(hsdpConfiguration);
        this.hsdpConfiguration = hsdpConfiguration;
        appName = hsdpAppName;
    }

    Map<String, Object> loginSocialProviders(String email, String socialAccessToken, String secret) {
        String apiEndpoint = "/authentication/login/social";
        String queryParams = "applicationName=" + appName;
        Map<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("accessToken", socialAccessToken);
        headers.put("refreshSecret", secret);
        headers.put("Api-version", "2");
        Map<String, String> body = new LinkedHashMap<String, String>();
        body.put("loginId", email);
        return sendSignedRequestForSocialLogin("POST", apiEndpoint, queryParams, headers, body);
    }

    Map<String, Object> logout(String userId, String accessToken) {
        String apiEndpoint = "/authentication/users/" + userId + "/logout";
        String queryParams = "applicationName=" + appName;
        Map<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("accessToken", accessToken);
        return sendRestRequest(apiEndpoint, queryParams, headers, null);
    }

    Map<String, Object> refreshSecret(String userUUID, String accessToken, String refreshSecret) {
        String apiEndpoint = "/authentication/users/" + userUUID + "/refreshAccessToken";
        String queryParams = "applicationName=" + appName;
        Map<String, String> headers = new LinkedHashMap<String, String>();

        String date = getUTCdatetimeAsString();
        String stringToSign = "refresh_access_token\n" + date + "\n" + accessToken + "\n";
        headers.put("refreshSignature", createRefreshSignature(refreshSecret, stringToSign));
        headers.put("refreshSignatureDate", date);
        headers.put("api-version", "2");
        headers.put("accessToken", accessToken);

        return sendSignedRequestForSocialLogin("POST", apiEndpoint, queryParams, headers, null);
    }

    private String createRefreshSignature(String refresh_Secret, String stringToSign) {
        byte[] hash = null;
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            byte[] refreshSecret = refresh_Secret.getBytes("UTF-8");
            SecretKeySpec secret = new SecretKeySpec(refreshSecret, mac.getAlgorithm());
            mac.init(secret);
            hash = mac.doFinal(stringToSign.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException e) {
            RLog.e(TAG, "Error occurred while creating refresh signature: " + e.getMessage());
        }
        return Base64.encodeToString(hash, Base64.NO_WRAP);
    }

    private String getUTCdatetimeAsString() {
        return ServerTime.getCurrentUTCTimeWithFormat("yyyy-MM-dd HH:mm:ss");
    }

}

