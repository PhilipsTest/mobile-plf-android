/*
 *  Copyright (c) Koninklijke Philips N.V., 2020
 *
 *  * All rights are reserved. Reproduction or dissemination
 *
 *  * in whole or in part is prohibited without the prior written
 *
 *  * consent of the copyright holder.
 *
 *
 */

package com.philips.platform.ecs.microService.request

import com.android.volley.Request
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.constant.ECSConstants.Companion.SERVICEID_ECS_NOTIFY_ME
import com.philips.platform.ecs.microService.error.ECSError
import org.json.JSONObject

class ProductAvailabilityRequest(val email: String, val ctn: String, val ecsCallback: ECSCallback<Boolean, ECSError>) : ECSJsonRequest(ecsCallback) {

    override fun getServiceID(): String {
        return SERVICEID_ECS_NOTIFY_ME
    }

    override fun getRequestMethod(): Int {
        return Request.Method.POST
    }

    override fun onResponse(response: JSONObject?) {
        val isRegistered = response?.getBoolean("success") ?: false
        ecsCallback.onResponse(isRegistered)
    }

    override fun getBody(): Map<String, String>? {
        val map = HashMap<String, String>()
        map["email"] = email
        map["productId"] = ctn
        return map
    }
}