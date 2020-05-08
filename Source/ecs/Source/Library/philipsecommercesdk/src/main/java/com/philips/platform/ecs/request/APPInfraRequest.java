/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.request;

import com.android.volley.Response;
import com.philips.platform.appinfra.rest.TokenProviderInterface;

import org.json.JSONObject;

import java.util.Map;

public interface APPInfraRequest extends Response.ErrorListener,TokenProviderInterface{

    int getMethod();

    String getURL();

    JSONObject getJSONRequest();

    com.android.volley.Response.Listener<JSONObject> getJSONSuccessResponseListener();
    com.android.volley.Response.Listener<String> getStringSuccessResponseListener();

    Response.ErrorListener getJSONFailureResponseListener();

    Map<String, String> getHeader();

    Map<String, String> getParams();

    com.philips.platform.appinfra.rest.TokenProviderInterface getTokenProviderInterface();
}
