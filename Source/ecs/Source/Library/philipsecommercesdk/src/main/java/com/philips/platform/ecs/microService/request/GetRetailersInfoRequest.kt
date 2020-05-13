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
import com.philips.platform.ecs.microService.model.error.HybrisError
import com.philips.platform.ecs.microService.model.retailers.ECSRetailerList
import com.philips.platform.ecs.microService.util.getData
import com.philips.platform.ecs.microService.util.getJsonError


import org.json.JSONObject

class GetRetailersInfoRequest (val ctn :String ,val ecsCallback: ECSCallback<ECSRetailerList?, ECSError>) : ECSJsonRequest() {

    val PREFIX_RETAILERS = "www.philips.com/api/wtb/v1"
    val RETAILERS_ALTER = "online-retailers?product=%s&lang=en"
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
            builder.append(com.philips.platform.ecs.util.ECSConfiguration.INSTANCE.locale).append("/")
            builder.append(RETAILERS_ALTER)
            return String.format(builder.toString(), ctn)
    }

    override fun onErrorResponse(error: VolleyError?) {
        //TODO to check parsing
        val jsonError = error?.getJsonError()
        val hybrisError = jsonError?.getData(HybrisError::class.java)
        ecsCallback.onFailure(ECSError(hybrisError.toString(),null,null))
    }

    override fun onResponse(response: JSONObject?) {
        val ecsRetailerList = response?.getData(ECSRetailerList::class.java)
        ecsCallback.onResponse(ecsRetailerList)

    }

}