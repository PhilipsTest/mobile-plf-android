package com.philips.cdp.prxclient.network;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.philips.cdp.prxclient.PrxJsonRequest;
import com.philips.cdp.prxclient.SSLCertificateManager;
import com.philips.cdp.prxclient.prxdatabuilder.PrxRequest;
import com.philips.cdp.prxclient.response.ResponseData;
import com.philips.cdp.prxclient.response.ResponseListener;

import org.json.JSONObject;

import prxclient.cdp.philips.com.prxclientlib.R;

/**
 * Description : This is the Network Wrapper class.
 * Project : PRX Common Component.
 * Created by naveen@philips.com on 02-Nov-15.
 */
public class NetworkWrapper {

    private static final String TAG = NetworkWrapper.class.getSimpleName();
    private Context mContext = null;
    private RequestQueue mVolleyRequest;
    private boolean isHttpsRequest = true;

    public NetworkWrapper(Context context) {
        mContext = context;
        mVolleyRequest = Volley.newRequestQueue(mContext);
    }

   /* public void executeJsonObjectRequest(final PrxRequest prxRequest, final ResponseListener listener) {
        mVolleyRequest = Volley.newRequestQueue(mContext);
        PrxLogger.d(TAG, "Url : " + prxRequest.getRequestUrl());
        JsonObjectRequest mJsonObjectRequest = new JsonObjectRequest(0, prxRequest.getRequestUrl(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ResponseData responseData = prxRequest.getResponseData(response);
                listener.onResponseSuccess(responseData);

                PrxLogger.d(TAG, "Response : " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null) {
                    try {
                        listener.onResponseError(error.toString(), error.networkResponse.statusCode);
                    } catch (Exception e) {
                        PrxLogger.e(TAG, "Volley Error : " + e);
                        listener.onResponseError(error.toString(), 0);
                    }
                }
            }
        });
        if (isHttpsRequest)
            SSLCertificateManager.setSSLSocketFactory();
        mVolleyRequest.add(mJsonObjectRequest);
    }*/

    public void executeCustomJsonRequest(final PrxRequest prxRequest, final ResponseListener listener) {
        final Response.Listener<JSONObject> responseListener = getVolleyResponseListener(prxRequest, listener);
        final Response.ErrorListener errorListener = getVolleyErrorListener(listener);
        PrxJsonRequest request = new PrxJsonRequest(prxRequest.getRequestType(), prxRequest.getRequestUrl(), prxRequest.getParams(), prxRequest.getHeaders(), responseListener, errorListener);
        if (isHttpsRequest)
            SSLCertificateManager.setSSLSocketFactory();
        mVolleyRequest.add(request);
    }

    @NonNull
    private Response.ErrorListener getVolleyErrorListener(final ResponseListener listener) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                if (error != null) {
                    final NetworkResponse networkResponse = error.networkResponse;
                    try {
                        if (networkResponse != null)
                            listener.onResponseError(error.toString(), networkResponse.statusCode);
                        else if (error instanceof NoConnectionError) {
                            listener.onResponseError(mContext.getString(R.string.no_internet_message), 501);
                        } else if (error instanceof TimeoutError) {
                            listener.onResponseError("Time out Exception", 504);
                        } else
                            listener.onResponseError("Unknown exception", -1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    @NonNull
    private Response.Listener<JSONObject> getVolleyResponseListener(final PrxRequest prxRequest, final ResponseListener listener) {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                ResponseData responseData = prxRequest.getResponseData(response);
                listener.onResponseSuccess(responseData);
            }
        };
    }

    public void setHttpsRequest(boolean isHttpsRequest) {
        this.isHttpsRequest = isHttpsRequest;
    }
}
