/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.request;

import com.android.volley.Response;
import com.philips.platform.appinfra.rest.TokenProviderInterface;
import com.philips.platform.ecs.network.NetworkController;

import org.json.JSONObject;

import java.util.Map;

public abstract class AppInfraAbstractRequest implements APPInfraRequest {

    public void executeRequest(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                new NetworkController(AppInfraAbstractRequest.this).executeRequest();
            }
        }).start();

    }

    @Override
    public Response.Listener<JSONObject> getJSONSuccessResponseListener() {
        return null;
    }

    @Override
    public Response.ErrorListener getJSONFailureResponseListener() {
        return this;
    }

    @Override
    public Response.Listener<String> getStringSuccessResponseListener() {
        return null;
    }

    @Override
    public JSONObject getJSONRequest() {
        return null;
    }

    @Override
    public Map<String, String> getHeader() {
        return null;
    }

    @Override
    public Map<String, String> getParams() {
        return null;
    }

    @Override
    public Token getToken() {
        return null;
    }

    @Override
    public TokenProviderInterface getTokenProviderInterface() {
        return null;
    }

}
