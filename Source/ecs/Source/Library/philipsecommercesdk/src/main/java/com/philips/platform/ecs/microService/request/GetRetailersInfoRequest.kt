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
import com.philips.platform.ecs.microService.model.retailer.ECSRetailerList
import com.philips.platform.ecs.microService.util.ECSDataHolder
import com.philips.platform.ecs.microService.util.getData


import org.json.JSONObject

class GetRetailersInfoRequest (val ctn :String ,val ecsCallback: ECSCallback<ECSRetailerList?, ECSError>) : ECSJsonRequest(ecsCallback) {

    val PREFIX_RETAILERS = "www.philips.com/api/wtb/v1"
    val RETAILERS_ALTER = "online-retailers?product=%s"
    val PRX_SECTOR_CODE = "B2C"


    override fun getURL(): String {
        return createURL()
    }

    override fun getServiceID(): String {
        return ""
    }

    private fun createURL():String{
            val builder = StringBuilder("https://")
            builder.append(PREFIX_RETAILERS).append("/")
            builder.append(PRX_SECTOR_CODE).append("/")
            builder.append(ECSDataHolder.locale).append("/")
            builder.append(RETAILERS_ALTER)
            return String.format(builder.toString(), ctn)
    }

    override fun getHeader(): MutableMap<String, String>? {
        return null
    }

    override fun onResponse(response: JSONObject?) {
        val ecsRetailerList = response?.getData(ECSRetailerList::class.java)
        ecsCallback.onResponse(ecsRetailerList)

    }

}