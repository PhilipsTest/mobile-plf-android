package com.philips.cdp.registration.configuration;

/**
 * Created by 310190722 on 8/25/2015.
 */
public class Development {

    private String sharedKey;
    private String secretKey;
    private String baseURL;

    public String getSharedKey() {
        return sharedKey;
    }

    public void setSharedKey(String sharedKey) {
        this.sharedKey = sharedKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }
}
