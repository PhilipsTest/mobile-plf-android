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

import com.android.volley.VolleyError
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.error.VolleyHandler
import com.philips.platform.ecs.microService.util.ECSDataHolder

abstract class ECSAbstractRequest(val ecsErrorCallback: ECSCallback<*, ECSError>) : ECSRequestInterface {

    lateinit var url: String
    lateinit var locale: String

    override fun getURL(): String {
        return url
    }

    override fun onErrorResponse(error: VolleyError?) {
        ecsErrorCallback.onFailure(VolleyHandler().getECSError(error))
    }

    override fun getReplaceURLMap(): MutableMap<String, String> {
        val map = HashMap<String, String>()
        ECSDataHolder.config.siteId?.let { map.put("siteId", it) }
        ECSDataHolder.locale?.let {
            val langCountryArray = it.split("_")
            if (langCountryArray.size >= 2) {
                map["language"] = langCountryArray[0]
                map["country"]  =  langCountryArray[1]
            }
        }
        return map
    }
}