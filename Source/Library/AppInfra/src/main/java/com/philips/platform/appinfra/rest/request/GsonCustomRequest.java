
/* Copyright (c) Koninklijke Philips N.V. 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.appinfra.rest.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.philips.platform.appinfra.rest.RestManager;
import com.philips.platform.appinfra.rest.ServiceIDUrlFormatting;
import com.philips.platform.appinfra.rest.TokenProviderInterface;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by 310243577 on 11/10/2016.
 */

public class GsonCustomRequest<T> extends Request<T> {

    private final Response.Listener<T> listener;
    private final Gson gson = new Gson();
    private final Class<T> clazz;
    private Map<String, String> mHeader;
    private TokenProviderInterface mProvider;


    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url     URL of the request to make
     * @param clazz   Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     */
    public GsonCustomRequest(int method, String url, Class<T> clazz, Map<String, String> headers,
                             Response.Listener<T> listener, Response.ErrorListener errorListener,
                             Map<String, String> header,
                             TokenProviderInterface tokenProviderInterface) {
        super(method, url, errorListener);
        this.clazz = clazz;
        this.mProvider = tokenProviderInterface;
        this.mHeader = header;
        this.listener = listener;
    }

    public GsonCustomRequest(int method, String serviceID, ServiceIDUrlFormatting.SERVICEPREFERENCE pref,
                             String urlExtension, Class<T> clazz, Map<String, String> headers,
                             Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, ServiceIDUrlFormatting.formatUrl(serviceID, pref, urlExtension), errorListener);
        this.clazz = clazz;
        this.mHeader = headers;
        this.listener = listener;
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

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(
                    gson.fromJson(json, clazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}
