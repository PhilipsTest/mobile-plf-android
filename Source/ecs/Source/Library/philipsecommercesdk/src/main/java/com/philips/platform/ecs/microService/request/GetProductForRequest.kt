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

import android.util.Base64
import com.android.volley.VolleyError
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.model.error.HybrisError
import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.ecs.microService.util.ECSDataHolder
import com.philips.platform.ecs.microService.util.getData
import com.philips.platform.ecs.microService.util.getJsonError
import com.philips.platform.ecs.microService.util.replaceParam
import org.json.JSONObject

class GetProductForRequest(private val ctn: String, private val ecsCallback: ECSCallback<ECSProduct, Exception>) : ECSJsonRequest() {


    var url = "https://acc.eu-west-1.api.philips.com/commerce-service/product/%ctn%?siteId=%siteId%&language=%language%&country=%country%"



    override fun getURL(): String {
        return url.replaceParam(getReplaceURLMap())
    }

    override fun getReplaceURLMap(): MutableMap<String, String> {
        val replaceURLMap = super.getReplaceURLMap()
        replaceURLMap["ctn"] = ctn.replace('/', '_')
        return replaceURLMap
    }

    override fun onErrorResponse(error: VolleyError) {
        val jsonError = error.getJsonError()
        val hybrisError = jsonError?.getData(HybrisError::class.java)
        ecsCallback.onFailure(Exception(hybrisError.toString()))
    }

    override fun onResponse(response: JSONObject) {
        val ecsProduct = response.getData(ECSProduct::class.java)
        ecsCallback.onResponse(ecsProduct)
    }

}