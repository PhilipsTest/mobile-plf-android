package com.philips.cdp.di.iap.session;

import com.android.volley.toolbox.HurlStack;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class IAPHurlStack {
    private final OAuthHandler mOAuthHandler;
    private static final String PHILIPS_HOST = "philips.com";

    public IAPHurlStack(OAuthHandler oAuthHandler) {
        mOAuthHandler = oAuthHandler;
    }

    HurlStack getHurlStack() {
        return new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(final URL url) throws IOException {
                HttpURLConnection connection = super.createConnection(url);
                if (connection instanceof HttpsURLConnection) {
                    ((HttpsURLConnection) connection).setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(final String hostname, final SSLSession session) {
                            return hostname.contains(PHILIPS_HOST);
                        }
                    });
                    connection.setRequestProperty("Authorization", "Bearer " + mOAuthHandler.getAccessToken());
                }
                return connection;
            }
        };
    }
}