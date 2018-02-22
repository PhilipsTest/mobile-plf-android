package com.philips.cdp.registration.restclient;

import android.os.Handler;
import android.os.Looper;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.platform.appinfra.rest.request.StringRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 */

public class URRestClientStringRequest extends StringRequest {
    public static final String TAG = URRestClientStringRequest.class
            .getSimpleName();
    private static int DEFAULT_TIMEOUT_MS = 3000;//30 SECONDS
    private String mBody = "";
    private Response.Listener<String> mResponseListener;
    private Response.ErrorListener mErrorListener;
    private Map<String, String> mHeaders;
    private Handler mHandler;


    public URRestClientStringRequest(String url, String body, Map<String, String> pHeader, Response.Listener<String> successListener, Response.ErrorListener errorListener) {
        super(Method.POST, url, successListener, errorListener, null, null, null);

        mBody = body;
        mResponseListener = successListener;
        mErrorListener = errorListener;
        mHandler = new Handler(Looper.getMainLooper());
        mHeaders = pHeader;
    }

    @Override
    public Request<?> setRetryPolicy(final RetryPolicy retryPolicy) {
        return super.setRetryPolicy(new DefaultRetryPolicy(
                URRestClientStringRequest.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=UTF-8";
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> params = new HashMap<String, String>();
        params.put("cache-control", "no-cache");
        params.put("Content-type", "application/x-www-form-urlencoded");
        // params.put("Content-Type", mHeaders);
        if (mHeaders != null)
            params.putAll(mHeaders);

        return params;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (mBody != null)
            return mBody.getBytes();
        else
            return "".getBytes();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String jsonString = null;
        RLog.d(TAG, "Response = " + response.statusCode);
        RLog.d(TAG, "Response = " + response.data.toString());
        try {
            jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            RLog.e(TAG, "UnsupportedEncodingException : " + e.getMessage());
        }

        return Response.success(jsonString, getCacheEntry());

    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
            final String message = new String(volleyError.networkResponse.data);
            RLog.e(TAG, "parseNetworkError =" + message);
            VolleyError error = new VolleyError(message);
            volleyError = error;
        }

        return volleyError;
    }

    @Override
    protected void deliverResponse(String response) {
        RLog.d(TAG, "Response deliverResponse= " + response);
        postSuccessResponseOnUIThread(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        if (error.networkResponse != null) {
            RLog.d(TAG, "deliverError Response error= " + error);
            String body;
            //get status code here
            String statusCode = String.valueOf(error.networkResponse.statusCode);
            RLog.d(TAG, "deliverError Response statusCode= " + statusCode);
            //get response body and parse with appropriate encoding
            if (error.networkResponse.data != null) {
                try {
                    body = new String(error.networkResponse.data, "UTF-8");
                    RLog.d(TAG, "deliverError Response body= " + body);
                } catch (UnsupportedEncodingException e) {
                    RLog.d(TAG, " deliverError= " + e.getMessage());
                }
            }
        }
        postErrorResponseOnUIThread(error);
    }

    private void postSuccessResponseOnUIThread(final String jsonObject) {
        RLog.d(TAG, jsonObject);
        mHandler.post(() -> {
            mResponseListener.onResponse(jsonObject);
        });
    }

    private void postErrorResponseOnUIThread(final VolleyError volleyError) {
        mHandler.post(() -> mErrorListener.onErrorResponse(volleyError));
    }

    private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }
}
