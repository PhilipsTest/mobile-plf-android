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

import android.util.Log
import com.android.volley.VolleyError
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.util.ECSDataHolder
import com.philips.platform.ecs.microService.util.getData
import com.philips.platform.ecs.microService.util.replaceParam
import com.philips.platform.ecs.model.products.ECSProduct
import com.philips.platform.ecs.store.ECSURLBuilder
import org.json.JSONObject

class GetProductForRequest(private val ctn: String, private val ecsCallback: ECSCallback<ECSProduct, Exception>) : ECSJsonRequest() {


    var url = "https://acc.eu-west-1.api.philips.com/commerce-service/product/%ctn%?siteId=%siteId%&language=%language%&country=%country%"

    override fun getURL(): String {
        return url.replaceParam(getReplaceURLMap())
    }

    override fun getServiceID(): String {
        return "ecs.productForCTN"
    }

    override fun getReplaceURLMap(): Map<String, String> {
        var map = HashMap<String,String>()
        ECSDataHolder.config.siteId?.let { map.put("siteId", it) }
       // ECSDataHolder.locale?.let { map.put("language", it) }
        //ECSDataHolder.locale?.let { map.put("country", it) }
        map.put("language", "de")
        map.put("country", "DE")
        map["ctn"] = ctn
        return map
    }

    override fun onErrorResponse(error: VolleyError) {
        Log.d("error",""+error.message)
    }

    override fun onResponse(response: JSONObject) {
        val ecsProduct = response.getData(ECSProduct::class.java)
        ecsCallback.onResponse(ecsProduct)
    }

}