/*
 * Copyright (c) 2017 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.catk.network;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.JsonArray;
import com.philips.platform.appinfra.rest.RestInterface;
import com.philips.platform.catk.CatkConstants;
import com.philips.platform.catk.ConsentAccessToolKit;
import com.philips.platform.catk.error.ConsentNetworkError;
import com.philips.platform.catk.listener.ConsentRequestListener;
import com.philips.platform.catk.listener.RefreshTokenListener;
import com.philips.platform.catk.request.ConsentRequest;

import javax.inject.Inject;

public class NetworkController implements ConsentRequestListener, Response.ErrorListener {
    private NetworkAbstractModel model;
    private Handler mHandler;

    @Inject
    RestInterface restInterface;

    public NetworkController() {
        init();
    }

    protected void init() {
        ConsentAccessToolKit.getInstance().getCatkComponent().inject(this);
        this.mHandler = new Handler(Looper.getMainLooper());
    }

    public void sendConsentRequest(final NetworkAbstractModel model) {
        this.model = model;
        ConsentRequest request = getConsentJsonRequest(model);
        addRequestToQueue(request);
    }

    public void addRequestToQueue(ConsentRequest consentRequest) {
        if (consentRequest != null) {
            if (restInterface != null) {
                restInterface.getRequestQueue().add(consentRequest);
            } else {
                // Need to error handle
                Log.e("Rest client", "Couldn't initialise REST Client");
            }
        }
    }

    protected ConsentRequest getConsentJsonRequest(final NetworkAbstractModel model) {
        return new ConsentRequest(model.getMethod(), model.getUrl(), model.requestHeader(), model.requestBody(), this, this);
    }

    @Override
    public void onResponse(ConsentRequest request, JsonArray response) {
        postSuccessResponseOnUIThread(response);
    }

    @Override
    public void onErrorResponse(ConsentRequest request, VolleyError error) {
        if (error instanceof AuthFailureError) {
            performRefreshToken(request, error);
        } else {
            postErrorResponseOnUIThread(error);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        postErrorResponseOnUIThread(error);
    }

    private void performRefreshToken(final ConsentRequest request, final VolleyError error) {
        NetworkHelper.getInstance().refreshAccessToken(new RefreshTokenListener() {
            @Override
            public void onRefreshSuccess() {
                try {
                    NetworkAbstractModel.addAuthorization(request.getHeaders());
                } catch (AuthFailureError authFailureError) {
                    authFailureError.printStackTrace();
                }
                addRequestToQueue(request);
            }

            @Override
            public void onRefreshFailed(int errCode) {
                postErrorResponseOnUIThread(error);
            }
        });
    }

    private void postSuccessResponseOnUIThread(final JsonArray response) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (model != null) {
                    Message msg = Message.obtain();
                    msg.what = model.getMethod();

                    if (response != null && response.size() == 0) {
                        msg.obj = CatkConstants.EMPTY_RESPONSE;
                    } else {
                        msg.obj = model.parseResponse(response);
                    }
                    model.onResponseSuccess(msg);
                }
            }
        });
    }

    private void postErrorResponseOnUIThread(final VolleyError error) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (model != null && error != null) {
                    new ConsentNetworkError(error, model.getMethod(), model);
                }
            }
        });
    }
}
