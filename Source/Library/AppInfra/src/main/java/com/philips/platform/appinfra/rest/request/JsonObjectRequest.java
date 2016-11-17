/* Copyright (c) Koninklijke Philips N.V. 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.appinfra.rest.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.philips.platform.appinfra.rest.RestManager;
import com.philips.platform.appinfra.rest.ServiceIDUrlFormatting;
import com.philips.platform.appinfra.rest.TokenProviderInterface;

import org.json.JSONObject;

import java.util.Map;


public class JsonObjectRequest extends com.android.volley.toolbox.JsonObjectRequest {

    private Map<String, String> mHeader;
    private TokenProviderInterface mProvider;

    public JsonObjectRequest(int method, String url, JSONObject jsonRequest,
                             Response.Listener<JSONObject> listener,
                             Response.ErrorListener errorListener , Map<String, String> header,
                             TokenProviderInterface tokenProviderInterface )  {
        super(method, url, jsonRequest, listener, errorListener);
        this.mProvider = tokenProviderInterface;
        this.mHeader = header;
    }


    public JsonObjectRequest(int method, String serviceID, ServiceIDUrlFormatting.SERVICEPREFERENCE pref, String urlExtension, JSONObject jsonRequest,
                             Response.Listener<JSONObject> listener, Response.ErrorListener errorListener)  {
        super(method, ServiceIDUrlFormatting.formatUrl(serviceID, pref, urlExtension), jsonRequest, listener, errorListener);
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (mProvider != null) {
            Map<String, String> tokenHeader = RestManager.setTokenProvider(mProvider);
            mHeader.putAll(tokenHeader);
            return mHeader;
        }
        return super.getHeaders();
    }
}
