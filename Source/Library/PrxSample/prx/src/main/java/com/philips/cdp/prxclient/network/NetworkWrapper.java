package com.philips.cdp.prxclient.network;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.philips.cdp.prxclient.Logger.PrxLogger;
import com.philips.cdp.prxclient.R;
import com.philips.cdp.prxclient.error.PrxError;
import com.philips.cdp.prxclient.request.PrxCustomJsonRequest;
import com.philips.cdp.prxclient.request.PrxRequest;
import com.philips.cdp.prxclient.response.ResponseData;
import com.philips.cdp.prxclient.response.ResponseListener;

import org.json.JSONObject;


/**
 * Description : This is the Network Wrapper class.
 * Project : PRX Common Component.
 * Created by naveen@philips.com on 02-Nov-15.
 */
public class NetworkWrapper {

    private static final String TAG = NetworkWrapper.class.getSimpleName();
    private Context mContext = null;
    private RequestQueue mVolleyRequest;
    private boolean isHttpsRequest = false;

    public NetworkWrapper(Context context) {
        mContext = context;
        VolleyQueue volleyQueue = VolleyQueue.getInstance();
        mVolleyRequest = volleyQueue.getRequestQueue(mContext);
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
        PrxLogger.d(TAG, "Custom JSON Request call..");
        final Response.Listener<JSONObject> responseListener = getVolleyResponseListener(prxRequest, listener);
        final Response.ErrorListener errorListener = getVolleyErrorListener(listener);
        String url = prxRequest.getRequestUrl();
        PrxCustomJsonRequest request = new PrxCustomJsonRequest(prxRequest.getRequestType(), url, prxRequest.getParams(), prxRequest.getHeaders(), responseListener, errorListener);
        request.setShouldCache(true);
        if (url.startsWith("https"))
            isHttpsRequest = true;
        else
            isHttpsRequest = false;
        setSSLSocketFactory();
        mVolleyRequest.add(request);
    }

    private void setSSLSocketFactory() {
        if (isHttpsRequest)
            SSLCertificateManager.setSSLSocketFactory();
    }

    @NonNull
    private Response.ErrorListener getVolleyErrorListener(final ResponseListener listener) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                if (error != null) {
                    final NetworkResponse networkResponse = error.networkResponse;
                    try {
                        if (error instanceof NoConnectionError) {
                            listener.onResponseError(PrxError.NO_INTERNET_CONNECTION);
                        } else if (error instanceof TimeoutError) {
                            listener.onResponseError(PrxError.VOLLEY_TIME_OUT);
                        } else if (error instanceof AuthFailureError) {
                           PrxLogger.d(TAG, "AuthFailureError : " + mContext.getResources().getString(R.string.authFailureError));
                            listener.onResponseError(PrxError.AUTHENTICATION_FAILURE);
                        } else if (error instanceof NetworkError) {
                           PrxLogger.d(TAG, "NetworkError : " + mContext.getResources().getString(R.string.networkError));
                            listener.onResponseError(PrxError.NETWORK_ERROR);
                        } else if (error instanceof ParseError) {
                           PrxLogger.d(TAG, "ParseError : " + mContext.getResources().getString(R.string.parseErrors));
                            listener.onResponseError(PrxError.PARSE_ERROR);
                        } else if (error instanceof ServerError) {
                            PrxLogger.d(TAG, "ServerError : " + mContext.getResources().getString(R.string.serverErrors));
                            listener.onResponseError(PrxError.SERVER_ERROR);
                        } else
                            listener.onResponseError(PrxError.UNKNOWN_EXCEPTION);

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
}
