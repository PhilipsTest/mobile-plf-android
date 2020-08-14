/*
 * Copyright (c) Koninklijke Philips N.V., 2020
 *
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 *
 */

package com.philips.platform.ccb.rest

import com.android.volley.Response
import com.philips.platform.ccb.manager.CCBSettingsManager

class CCBRestClient {

    fun invokeRequest(ccbRequestInterface: CCBRequestInterface, successListener: Response.Listener<String>, errorListener: Response.ErrorListener) {
        val request: CCBRequest = makeCcbRequest(ccbRequestInterface, successListener, errorListener)
        CCBSettingsManager.mRestInterface.requestQueue.add(request)
    }

    private fun makeCcbRequest(ccbRequestInterface: CCBRequestInterface, successListener: Response.Listener<String>, errorListener: Response.ErrorListener): CCBRequest {
        return CCBRequest(ccbRequestInterface.getMethodType(), ccbRequestInterface.getUrl(), ccbRequestInterface.getBody(),successListener, errorListener, ccbRequestInterface.getHeader())
    }
}