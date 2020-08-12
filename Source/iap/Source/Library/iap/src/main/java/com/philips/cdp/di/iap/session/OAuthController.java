/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.session;

import android.os.Message;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.philips.cdp.di.iap.model.AbstractModel;
import com.philips.cdp.di.iap.model.OAuthRequest;
import com.philips.cdp.di.iap.model.RefreshOAuthRequest;
import com.philips.cdp.di.iap.response.error.Error;
import com.philips.cdp.di.iap.response.error.ServerError;
import com.philips.cdp.di.iap.store.StoreListener;
import com.philips.cdp.di.iap.utils.IAPLog;
import com.philips.cdp.di.iap.utils.ModelConstants;

import org.json.JSONObject;

import java.util.HashMap;

public class OAuthController implements OAuthListener {
    private final String TAG = OAuthController.class.getSimpleName();
    private String access_token;
    private OAuthRequest mOAuthRequest;
    private StoreListener mStore;
    private HurlStack mRetryHurlStack;

    @Override
    public String getAccessToken() {
        if (mOAuthRequest == null) {
            mStore = HybrisDelegate.getInstance().getStore();
            mOAuthRequest = new OAuthRequest(mStore, null);
            initRetryHurlStack();
        }
        if (access_token == null) {
            requestSyncOAuthToken(null);
        }
        return access_token;
    }

    private void initRetryHurlStack() {
        IAPHurlStack stack  =new IAPHurlStack(mOAuthRequest);
        mRetryHurlStack = stack.getHurlStack();
    }

    @Override
    public void refreshToken(RequestListener listener) {
        if(mOAuthRequest!=null && mOAuthRequest.getrefreshToken()!=null &&  mStore!=null) {
            HashMap<String, String> params = new HashMap<>();
            params.put(ModelConstants.REFRESH_TOKEN, mOAuthRequest.getrefreshToken());
            RefreshOAuthRequest request = new RefreshOAuthRequest(mStore, params);
            requestSyncRefreshToken(request, listener);
        }

    }

    @Override
    public void resetAccessToken() {
        access_token = null;
    }

    protected void requestSyncOAuthToken(final RequestListener listener) {
        SynchronizedNetwork network = new SynchronizedNetwork(mRetryHurlStack);
        network.performRequest(createOAuthRequest(mOAuthRequest), new SynchronizedNetworkListener() {
            @Override
            public void onSyncRequestSuccess(final Response<JSONObject> response) {
                if (response != null && response.result != null) {
                    mOAuthRequest.parseResponse(response.result);
                    access_token = mOAuthRequest.getAccessToken();
                }
                notifySuccessListener(response, listener);
            }

            @Override
            public void onSyncRequestError(final VolleyError volleyError) {
                if (volleyError instanceof com.android.volley.ServerError ||  volleyError instanceof com.android.volley.AuthFailureError) {
                    mStore.refreshLoginSession();
                    if (mStore.getUser().isTokenRefreshSuccessful()) {
                        requestSyncOAuthToken(listener);
                    } else {
                        notifyErrorListener(volleyError, listener);
                    }
                } else {
                    notifyErrorListener(volleyError, listener);
                }
            }
        });
    }

    protected void requestSyncRefreshToken(RefreshOAuthRequest requestModel, final RequestListener listener) {
        SynchronizedNetwork network = new SynchronizedNetwork(mRetryHurlStack);
        network.performRequest(createOAuthRequest(requestModel), new SynchronizedNetworkListener() {
            @Override
            public void onSyncRequestSuccess(final Response<JSONObject> response) {
                if (response != null && response.result != null) {
                    mOAuthRequest.parseResponse(response.result);
                    access_token = mOAuthRequest.getAccessToken();
                }
                notifySuccessListener(response, listener);
            }

            @Override
            public void onSyncRequestError(final VolleyError volleyError) {
                if (isInvalidGrantError(volleyError)) {
                    requestSyncOAuthToken(listener);
                } else {
                    notifyErrorListener(volleyError, listener);
                }
            }
        });
    }

    protected IAPJsonRequest createOAuthRequest(final AbstractModel request) {
        return new IAPJsonRequest(request.getMethod(), request.getUrl(),
                request.requestBody(),null,null);
    }

    protected void notifyErrorListener(final VolleyError volleyError, final RequestListener listener) {
        if(listener == null) return;

        Message msg = Message.obtain();
        msg.obj = volleyError;
        listener.onError(msg);
    }

    @SuppressWarnings("rawtypes")
    protected void notifySuccessListener(final Response response, final RequestListener listener) {
        if(listener == null) return;

        Message msg = Message.obtain();
        msg.obj = response;
        listener.onSuccess(msg);
    }

    // Ideally it should never get exception, until we really get bad response or bad JSON resp
    protected boolean isInvalidGrantError(VolleyError volleyError) {
        try {
            if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                ServerError response = (new Gson().fromJson(new String(volleyError
                        .networkResponse.data), ServerError.class));
                if (response.getErrors() != null) {
                    Error error = response.getErrors().get(0);
                    final String TYPE_INVALID_GRANT_ERROR = "InvalidGrantError";
                    if (TYPE_INVALID_GRANT_ERROR.equals(error.getType())) {
                        return true;
                    }
                }
            }
        } catch (JsonSyntaxException e) {
            IAPLog.d(TAG, "isInvalidGrantError-> JsonSyntaxException");
        }
        return false;
    }
}