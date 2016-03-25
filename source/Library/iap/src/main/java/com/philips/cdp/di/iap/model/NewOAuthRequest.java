/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.model;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.philips.cdp.di.iap.response.oauth.OAuthResponse;
import com.philips.cdp.di.iap.session.OAuthHandler;
import com.philips.cdp.di.iap.store.Store;

import java.util.Map;

public class NewOAuthRequest extends AbstractModel implements OAuthHandler {
    OAuthResponse mOAuthResponse;
    public NewOAuthRequest(final Store store, final Map<String, String> query) {
        super(store, query);
    }

    @Override
    public Object parseResponse(final Object response) {
        mOAuthResponse = new Gson().fromJson(response.toString(), OAuthResponse.class);
        return mOAuthResponse;
    }

    @Override
    public int getMethod() {
        return Request.Method.POST;
    }

    @Override
    public Map<String, String> requestBody() {
        return null;
    }

    @Override
    public String getUrl() {
        return store.getOauthUrl();
    }

    @Override
    public String generateToken() {
        if(mOAuthResponse == null) {
            return null;
        }
        return mOAuthResponse.getAccessToken();
    }
}
