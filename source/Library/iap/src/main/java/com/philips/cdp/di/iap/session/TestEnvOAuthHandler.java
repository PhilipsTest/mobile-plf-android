/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */

package com.philips.cdp.di.iap.session;

import android.os.Message;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.philips.cdp.di.iap.model.AbstractModel;
import com.philips.cdp.di.iap.model.ModelConstants;
import com.philips.cdp.di.iap.model.NewOAuthRequest;
import com.philips.cdp.di.iap.model.RefreshOAuthRequest;
import com.philips.cdp.di.iap.response.error.Error;
import com.philips.cdp.di.iap.response.error.ServerError;
import com.philips.cdp.di.iap.store.Store;
import com.philips.cdp.di.iap.utils.IAPLog;

import java.util.HashMap;

public class TestEnvOAuthHandler implements OAuthHandler {
    private final String TAG = TestEnvOAuthHandler.class.getSimpleName();
    private final String TYPE_INVALID_GRANT_ERROR = "InvalidGrantError";

    private String access_token;
    private NewOAuthRequest mOAuthRequest;
    private Store mStore;

    @Override
    public String getAccessToken() {
        if (mOAuthRequest == null) {
            mStore = HybrisDelegate.getInstance().getStore();
            mOAuthRequest = new NewOAuthRequest(mStore, null);
        }
        if (access_token == null) {
            requestSyncOAuthToken(null);
        }
        return access_token;
    }

    @Override
    public void refreshToken(RequestListener listener) {
        IAPLog.d(TAG,"requesting new access token using refreshtoken");
        HashMap<String, String> params = new HashMap<>();
        params.put(ModelConstants.REFRESH_TOKEN,mOAuthRequest.getrefreshToken());
        RefreshOAuthRequest request = new RefreshOAuthRequest(mStore, params);
        requestSyncRefreshToken(request, listener);
    }

    @Override
    public void resetAccessToken() {
        access_token = null;
    }

    private void requestSyncOAuthToken(final RequestListener listener) {
        SynchronizedNetwork network = new SynchronizedNetwork(new IAPHurlStack(null).getHurlStack());
        network.performRequest(createOAuthRequest(mOAuthRequest), new SynchronizedNetworkCallBack() {
            @Override
            public void onSyncRequestSuccess(final Response response) {
                if (response != null && response.result != null) {
                    mOAuthRequest.parseResponse(response.result);
                    access_token = mOAuthRequest.getAccessToken();
                }
                notifySuccessListener(response, listener);
            }

            @Override
            public void onSyncRequestError(final VolleyError volleyError) {
                if (volleyError instanceof com.android.volley.ServerError) {
                    mStore.refreshLoginSession();
                    if (mStore.getUser().isTokenRefreshSuccessful()) {
                        requestSyncOAuthToken(listener);
                    } else {

                    }
                } else {
                    notifyErrorListener(volleyError, listener);
                }
            }
        });
    }

    private void requestSyncRefreshToken(RefreshOAuthRequest requestModel, final RequestListener listener) {
        SynchronizedNetwork network = new SynchronizedNetwork(new IAPHurlStack(mOAuthRequest).getHurlStack());
        network.performRequest(createOAuthRequest(requestModel), new SynchronizedNetworkCallBack() {
            @Override
            public void onSyncRequestSuccess(final Response response) {
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

    private void notifyErrorListener(final VolleyError volleyError, final RequestListener listener) {
        if(listener == null) return;

        Message msg = Message.obtain();
        msg.obj = volleyError;
        listener.onError(msg);
    }

    private void notifySuccessListener(final Response response, final RequestListener listener) {
        if(listener == null) return;

        Message msg = Message.obtain();
        msg.obj = response;
        listener.onSuccess(msg);
    }

    private IAPJsonRequest createOAuthRequest(final AbstractModel request) {
        return new IAPJsonRequest(request.getMethod(), request.getUrl(),
                request.requestBody(),null,null);
    }

    private boolean isInvalidGrantError(VolleyError volleyError) {
        if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
            ServerError response = (new Gson().fromJson(new String(volleyError
                    .networkResponse.data), ServerError.class));
            if (response.getErrors() != null) {
                Error error = response.getErrors().get(0);
                if (TYPE_INVALID_GRANT_ERROR.equals(error.getType())) {
                    return true;
                }
            }
        }
        return false;
    }
}