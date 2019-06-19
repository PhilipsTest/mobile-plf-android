/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.session;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class IAPJsonRequest extends Request<JSONObject> {

    private Listener<JSONObject> mResponseListener;
    private ErrorListener mErrorListener;
    private Map<String, String> params;
    protected Handler mHandler;

    public IAPJsonRequest(int method, String url, Map<String, String> params,
                          Listener<JSONObject> responseListener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mResponseListener = responseListener;
        mErrorListener = errorListener;
        mHandler = new Handler(Looper.getMainLooper());
        this.params = params;
    }

    @Override
    public Request<?> setRetryPolicy(final RetryPolicy retryPolicy) {
        return super.setRetryPolicy(new DefaultRetryPolicy(
                NetworkConstants.DEFAULT_TIMEOUT_MS,
                0,
                0.0f));
    }

    @Override
    public Map<String, String> getParams()
            throws com.android.volley.AuthFailureError {
        return params;
    }

    @Override
    public Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            final String encodedString = Base64.encodeToString(response.data, Base64.DEFAULT);
            final byte[] decode = Base64.decode(encodedString, Base64.DEFAULT);
            final String jsonString = new String(decode);
            JSONObject result = null;
            if (jsonString.length() > 0)
                result = new JSONObject(jsonString);
            return Response.success(result,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        postSuccessResponseOnUIThread(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        final RequestQueue requestQueue = HybrisDelegate.getInstance().controller.mRequestQueue;
        IAPUrlRedirectionHandler iapUrlRedirectionHandler = new IAPUrlRedirectionHandler(this, error);

        // Handle 30x
        if (iapUrlRedirectionHandler.isRedirectionRequired()) {
            final IAPJsonRequest iapJsonRequest = iapUrlRedirectionHandler.getNewRequestWithRedirectedUrl();
            requestQueue.add(iapJsonRequest);
        } else {
            handleMiscErrors(error);
        }

    }

    protected void handleMiscErrors(final VolleyError error) {
        if (error instanceof AuthFailureError) {
            HybrisDelegate.getNetworkController().refreshOAuthToken(new RequestListener() {
                @Override
                public void onSuccess(final Message msg) {
                    postSelfAgain();
                }

                @Override
                public void onError(final Message msg) {
                    postErrorResponseOnUIThread(error);
                }
            });
        } else {
            postErrorResponseOnUIThread(error);
        }


    }

    private void postSelfAgain() {
        SynchronizedNetwork synchronizedNetwork = new SynchronizedNetwork
                (HybrisDelegate.getNetworkController().mIapHurlStack);
        synchronizedNetwork.performRequest(this, new SynchronizedNetworkListener() {
            @Override
            public void onSyncRequestSuccess(final Response<JSONObject> jsonObjectResponse) {
                postSuccessResponseOnUIThread(jsonObjectResponse.result);
            }

            @Override
            public void onSyncRequestError(final VolleyError volleyError) {
                postErrorResponseOnUIThread(volleyError);
            }
        });
    }

    private void postSuccessResponseOnUIThread(final JSONObject jsonObject) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mResponseListener.onResponse(jsonObject);
            }
        });
    }

    private void postErrorResponseOnUIThread(final VolleyError volleyError) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mErrorListener.onErrorResponse(volleyError);
            }
        });
    }
}