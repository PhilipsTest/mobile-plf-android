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



import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.constant.ECSConstants.Companion.SERVICEID_ECS_RETAILERS
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.model.retailer.ECSRetailerList
import com.philips.platform.ecs.microService.util.ECSDataHolder
import com.philips.platform.ecs.microService.util.getData
import com.philips.platform.ecs.microService.util.replaceParam
import org.json.JSONObject

class GetRetailersInfoRequest (val ctn :String ,val ecsCallback: ECSCallback<ECSRetailerList?, ECSError>) : ECSJsonRequest(ecsCallback) {



    override fun getURL(): String {
        return url.replaceParam(getReplaceURLMap())
    }

    override fun getServiceID(): String {
        return SERVICEID_ECS_RETAILERS
    }


    override fun getHeader(): MutableMap<String, String>? {
        return null
    }

    override fun onResponse(response: JSONObject?) {
        val ecsRetailerList = response?.getData(ECSRetailerList::class.java)
        ecsCallback.onResponse(ecsRetailerList)

    }

    override fun getReplaceURLMap(): MutableMap<String, String> {
        val map = HashMap<String, String>()
        map.put("ctn",ctn)
        ECSDataHolder.locale?.let {
            map.put("locale",it)
        }
        return map
    }


}