/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.model;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.philips.cdp.di.iap.response.oauth.OAuthResponse;
import com.philips.cdp.di.iap.session.OAuthListener;
import com.philips.cdp.di.iap.session.RequestListener;
import com.philips.cdp.di.iap.store.StoreListener;
import com.philips.cdp.di.iap.utils.IAPLog;

import java.util.HashMap;
import java.util.Map;

public class OAuthRequest extends AbstractModel implements OAuthListener {


    OAuthResponse mOAuthResponse;

    public OAuthRequest(final StoreListener store, final Map<String, String> query) {
        super(store, query);
    }

    @Override
    public Object parseResponse(final Object response) {
        mOAuthResponse = new Gson().fromJson(response.toString(), OAuthResponse.class);
        return mOAuthResponse;
    }

    @Override
    public int getMethod() {
        IAPLog.d(IAPLog.LOG, "POST");
        return Request.Method.POST;
    }

    @Override
    public Map<String, String> requestBody() {
        return store.getUser().getJanrainOAuth();
    }

    @Override
    public String getUrl() {
        IAPLog.d(IAPLog.LOG, "Request URL = " + store.getOauthUrl());
        return store.getOauthUrl();
    }

    @Override
    public String getAccessToken() {
        if (mOAuthResponse == null) {
            return null;
        }
        return mOAuthResponse.getAccessToken();
    }

    @Override
    public void refreshToken(final RequestListener listener) {

    }

    @Override
    public void resetAccessToken() {
        mOAuthResponse = null;
    }

    public String getrefreshToken() {
        if (mOAuthResponse == null) {
            return ""; //Avoid NPE in Volley
        }
        return mOAuthResponse.getRefreshToken();
    }
}
