/*
 * Copyright (c) 2017 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.catk;

import android.util.Log;

import com.android.volley.VolleyError;
import com.philips.cdp.registration.handlers.RefreshLoginSessionHandler;
import com.philips.platform.appinfra.rest.RestInterface;
import com.philips.platform.catk.error.ConsentNetworkError;
import com.philips.platform.catk.listener.RefreshTokenListener;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;


public class NetworkController implements AuthErrorListener {
    @Inject
    RestInterface restInterface;

    NetworkController() {
        init();
    }

    void init() {
        ConsentAccessToolKit.getInstance().getCatkComponent().inject(this);
    }

    void sendConsentRequest(final NetworkAbstractModel model) {
        ConsentRequest request = getConsentJsonRequest(model);
        request.setShouldCache(false);
        addRequestToQueue(request);
    }

    private void addRequestToQueue(ConsentRequest consentRequest) {
        if (consentRequest != null) {
            if (restInterface != null) {
                restInterface.getRequestQueue().add(consentRequest);
            } else {
                // Need to markErrorAndGetPrevious handle
                Log.e("Rest client", "Couldn't initialise REST Client");
            }
        }
    }

    ConsentRequest getConsentJsonRequest(final NetworkAbstractModel model) {
        return new ConsentRequest(model, model.getMethod(), model.getUrl(), requestHeader(), model.requestBody(), this);
    }

    @Override
    public void onAuthError(NetworkAbstractModel model, VolleyError error) {
        performRefreshToken(model, error);
    }

    public static Map<String, String> requestHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("api-version", "1");
        header.put("content-type", "application/json");
        addAuthorization(header);
        header.put("performerid", ConsentAccessToolKit.getInstance().getCatkComponent().getUser().getHsdpUUID());
        header.put("cache-control", "no-cache");
        return header;
    }

    private static void addAuthorization(Map<String, String> headers) {
        headers.remove("authorization");
        headers.put("authorization", "bearer " + ConsentAccessToolKit.getInstance().getCatkComponent().getUser().getHsdpAccessToken());
    }

    private void performRefreshToken(final NetworkAbstractModel triedModel, final VolleyError error) {
        refreshAccessToken(new RefreshTokenListener() {
            @Override
            public void onRefreshSuccess() {
                NetworkAbstractModel model = triedModel;
                sendConsentRequest(model);
            }

            @Override
            public void onRefreshFailed(int errCode) {
                NetworkAbstractModel model = triedModel;
                if (model != null) {
                    model.onResponseError(new ConsentNetworkError(error));
                }
            }
        });
    }

    private void refreshAccessToken(final RefreshTokenListener refreshTokenListener) {
        ConsentAccessToolKit.getInstance().getCatkComponent().getUser().refreshLoginSession(new RefreshLoginSessionHandler() {
            @Override
            public void onRefreshLoginSessionSuccess() {
                refreshTokenListener.onRefreshSuccess();
            }

            @Override
            public void onRefreshLoginSessionFailedWithError(int errCode) {
                refreshTokenListener.onRefreshFailed(errCode);
            }

            @Override
            public void onRefreshLoginSessionInProgress(String s) {
                // Need to handle
            }
        });
    }
}
