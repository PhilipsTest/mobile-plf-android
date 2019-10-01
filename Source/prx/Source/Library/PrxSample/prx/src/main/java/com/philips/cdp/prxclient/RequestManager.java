/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.cdp.prxclient;

import android.support.annotation.Nullable;
import android.util.Log;

import com.philips.cdp.prxclient.error.PrxError;
import com.philips.cdp.prxclient.network.NetworkWrapper;
import com.philips.cdp.prxclient.request.PrxRequest;
import com.philips.cdp.prxclient.response.ResponseListener;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;

import static android.content.ContentValues.TAG;

/**
 * This is the entry class to start the PRX Request.
 * It provides set of public APIs for placing requests from client.
 *
 * @since 1.0.0
 */
public class RequestManager {

    private @Nullable
    PRXDependencies mPrxDependencies;

    /**
     * Initialises RequestManager instance.
     *
     * @param prxDependencies PRX dependencies
     * @since 2.2.0
     */
    public void init(@Nullable PRXDependencies prxDependencies) {
        mPrxDependencies = prxDependencies;
        if (mPrxDependencies == null) {
            return;
        }

        AppInfraInterface appInfra = mPrxDependencies.getAppInfra();
        if (appInfra == null) {
            return;
        }

        if (mPrxDependencies.getParentTLA() != null) {
            mPrxDependencies.mAppInfraLogging = appInfra.getLogging().createInstanceForComponent(String.format("%s/prx", mPrxDependencies.getParentTLA()), getLibVersion());
            mPrxDependencies.mAppInfraLogging.log(LoggingInterface.LogLevel.DEBUG, PrxConstants.PRX_REQUEST_MANAGER, String.format("PRX is initialized with  %s", mPrxDependencies.getParentTLA()));

        } else {
            mPrxDependencies.mAppInfraLogging = appInfra.getLogging().createInstanceForComponent("/prx", getLibVersion());
            mPrxDependencies.mAppInfraLogging.log(LoggingInterface.LogLevel.INFO, PrxConstants.PRX_REQUEST_MANAGER, "PRX is initialized ");
        }
    }

    /**
     * Performs a network request.
     *
     * @param prxRequest PRX Request
     * @param listener   Response listener
     * @since 1.0.0
     */
    public void executeRequest(PrxRequest prxRequest, ResponseListener listener) {
        try {
            mPrxDependencies.mAppInfraLogging.log(LoggingInterface.LogLevel.INFO, PrxConstants.PRX_REQUEST_MANAGER, "execute prx request");
            new NetworkWrapper(mPrxDependencies).executeCustomJsonRequest(prxRequest, listener);
        } catch (Exception e) {
            mPrxDependencies.mAppInfraLogging.log(LoggingInterface.LogLevel.ERROR, PrxConstants.PRX_REQUEST_MANAGER, "Error in execute prx request");
            listener.onResponseError(new PrxError(PrxError.PrxErrorType.UNKNOWN_EXCEPTION.getDescription(), PrxError.PrxErrorType.UNKNOWN_EXCEPTION.getId()));
        }
    }

    private String getLibVersion() {
        String mAppVersion = null;
        try {
            mAppVersion = BuildConfig.VERSION_NAME;
        } catch (Exception e) {
            Log.d(TAG, "Error in Version name ");
        }
        if (mAppVersion.isEmpty()) {
            throw new IllegalArgumentException("Prx Appversion cannot be null");
        } else {
            if (!mAppVersion.matches("[0-9]+\\.[0-9]+\\.[0-9]+([_(-].*)?")) {
                throw new IllegalArgumentException("AppVersion should in this format " +
                        "\" [0-9]+\\.[0-9]+\\.[0-9]+([_(-].*)?]\" ");
            }
        }
        return mAppVersion;
    }

}
