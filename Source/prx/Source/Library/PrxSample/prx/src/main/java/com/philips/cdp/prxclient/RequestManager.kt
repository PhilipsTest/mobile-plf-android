/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */
package com.philips.cdp.prxclient

import android.content.ContentValues
import android.util.Log
import com.philips.cdp.prxclient.error.PrxError
import com.philips.cdp.prxclient.error.PrxError.PrxErrorType
import com.philips.cdp.prxclient.network.NetworkWrapper
import com.philips.cdp.prxclient.request.PrxRequest
import com.philips.cdp.prxclient.response.ResponseListener
import com.philips.platform.appinfra.logging.LoggingInterface

/**
 * This is the entry class to start the PRX Request.
 * It provides set of public APIs for placing requests from client.
 *
 * @since 1.0.0
 */
class RequestManager {
    private var mPrxDependencies: PRXDependencies? = null

    /**
     * Initialises RequestManager instance.
     *
     * @param prxDependencies PRX dependencies
     * @since 2.2.0
     */
    fun init(prxDependencies: PRXDependencies?) {
        mPrxDependencies = prxDependencies
        mPrxDependencies?.let {
             val appInfra = it.appInfra ?: return
            if (it.parentTLA != null) {
                it.mAppInfraLogging = appInfra.logging.createInstanceForComponent(String.format("%s/prx", it.parentTLA), libVersion)
                it.mAppInfraLogging!!.log(LoggingInterface.LogLevel.DEBUG, PrxConstants.PRX_REQUEST_MANAGER, String.format("PRX is initialized with  %s", mPrxDependencies?.parentTLA))
            } else {
                it.mAppInfraLogging = appInfra.logging.createInstanceForComponent("/prx", libVersion)
                it.mAppInfraLogging!!.log(LoggingInterface.LogLevel.INFO, PrxConstants.PRX_REQUEST_MANAGER, "PRX is initialized ")
            }
        }
    }

    /**
     * Performs a network request.
     *
     * @param prxRequest PRX Request
     * @param listener   Response listener
     * @since 1.0.0
     */
    fun executeRequest(prxRequest: PrxRequest, listener: ResponseListener) {
        try {
            mPrxDependencies!!.mAppInfraLogging!!.log(LoggingInterface.LogLevel.INFO, PrxConstants.PRX_REQUEST_MANAGER, "execute prx request")
            NetworkWrapper(mPrxDependencies).executeCustomJsonRequest(prxRequest, listener)
        } catch (e: Exception) {
            mPrxDependencies!!.mAppInfraLogging!!.log(LoggingInterface.LogLevel.ERROR, PrxConstants.PRX_REQUEST_MANAGER, "Error in execute prx request")
            listener.onResponseError(PrxError(PrxErrorType.UNKNOWN_EXCEPTION.description, PrxErrorType.UNKNOWN_EXCEPTION.id))
        }
    }

    private val libVersion: String?
        private get() {
            var mAppVersion: String? = null
            try {
                mAppVersion = BuildConfig.VERSION_NAME
            } catch (e: Exception) {
                Log.d(ContentValues.TAG, "Error in Version name ")
            }
            require(!mAppVersion.isNullOrBlank()) { "Prx Appversion cannot be null" }
            val pattern = Regex( "[0-9]+\\.[0-9]+\\.[0-9]+([_(-].*)?" )
            require(mAppVersion.matches(pattern)) {
                "AppVersion should in this format " +
                        "\" [0-9]+\\.[0-9]+\\.[0-9]+([_(-].*)?]\" "
            }
            return mAppVersion
        }
}