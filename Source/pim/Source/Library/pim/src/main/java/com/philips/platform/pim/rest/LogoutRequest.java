package com.philips.platform.pim.rest;

import com.android.volley.Request;

import net.openid.appauth.AuthState;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to create logout request
 */
public class LogoutRequest implements PIMRequestInterface {
    private AuthState mAuthState;
    private String mClientId;


    public LogoutRequest(AuthState authState, String clientId) {
        mAuthState = authState;
        mClientId = clientId;
    }

    @Override
    public String getUrl() {
        return mAuthState.getLastAuthorizationResponse().request.configuration.discoveryDoc.getIssuer() + "/token/revoke";
    }

    @Override
    public Map<String, String> getHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put("content-type", "application/x-www-form-urlencoded");
        headers.put("cache-control", "no-cache");
        return headers;
    }

    @Override
    public Map<String, String> getParams() {
        return null;
    }

    @Override
    public String getBody() {
        return "token=" + mAuthState.getAccessToken() + "&client_id=" + mClientId + "&token_type_hint=access_token";
    }

    @Override
    public int getMethodType() {
        return Request.Method.POST;
    }

}
