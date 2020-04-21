/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.network;

import com.philips.platform.ecs.request.APPInfraRequest;
import com.philips.platform.ecs.util.ECSConfiguration;
import com.philips.platform.appinfra.rest.request.JsonObjectRequest;
import com.philips.platform.appinfra.rest.request.StringRequest;
import com.philips.platform.ecs.util.ECSConfiguration;

public class NetworkController {


    JsonObjectRequest jsonObjectRequest;
    StringRequest stringRequest;

    public NetworkController(APPInfraRequest appInfraJSONRequest) {
        if (appInfraJSONRequest.getJSONSuccessResponseListener() != null) {
            jsonObjectRequest = getAppInfraJSONObject(appInfraJSONRequest);
        } else if (appInfraJSONRequest.getStringSuccessResponseListener() != null) {
            stringRequest = getStringRequest(appInfraJSONRequest);
        }
    }

    public JsonObjectRequest getAppInfraJSONObject(APPInfraRequest appInfraJSONRequest) {

//        Log.d("Network Controller URL:",appInfraJSONRequest.getURL());
        return new JsonObjectRequest(appInfraJSONRequest.getMethod(), appInfraJSONRequest.getURL(), appInfraJSONRequest.getJSONRequest()
                , appInfraJSONRequest.getJSONSuccessResponseListener(), appInfraJSONRequest.getJSONFailureResponseListener(),
                appInfraJSONRequest.getHeader(), appInfraJSONRequest.getParams(), appInfraJSONRequest.getTokenProviderInterface());
    }

    public void executeRequest() {
        if (jsonObjectRequest != null) {
            if (null != ECSConfiguration.INSTANCE.getDefaultRetryPolicy()) {
                //Set a retry policy in case of SocketTimeout & ConnectionTimeout Exceptions.
                //Volley does retry for you if you have specified the policy.
                jsonObjectRequest.setRetryPolicy(ECSConfiguration.INSTANCE.getDefaultRetryPolicy());
            }
            ECSConfiguration.INSTANCE.getAppInfra().getRestClient().getRequestQueue().add(jsonObjectRequest);
        } else if (stringRequest != null) {
            if (null != ECSConfiguration.INSTANCE.getDefaultRetryPolicy()) {
                //Set a retry policy in case of SocketTimeout & ConnectionTimeout Exceptions.
                //Volley does retry for you if you have specified the policy.
                stringRequest.setRetryPolicy(ECSConfiguration.INSTANCE.getDefaultRetryPolicy());
            }
            ECSConfiguration.INSTANCE.getAppInfra().getRestClient().getRequestQueue().add(stringRequest);
        }
    }

    private StringRequest getStringRequest(APPInfraRequest appInfraJSONRequest) {
        return new StringRequest(appInfraJSONRequest.getMethod(), appInfraJSONRequest.getURL()
                , appInfraJSONRequest.getStringSuccessResponseListener(), appInfraJSONRequest.getJSONFailureResponseListener(),
                appInfraJSONRequest.getHeader(), appInfraJSONRequest.getParams(), appInfraJSONRequest.getTokenProviderInterface());
    }
}
