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
import com.philips.platform.ecs.microService.constant.ECSConstants
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.error.ServerError
import com.philips.platform.ecs.microService.model.asset.AssetModel
import com.philips.platform.ecs.microService.model.asset.Assets
import com.philips.platform.ecs.microService.prx.PrxConstants
import com.philips.platform.ecs.microService.util.ECSDataHolder
import com.philips.platform.ecs.microService.util.getData
import com.philips.platform.ecs.microService.util.getJsonError
import com.philips.platform.ecs.microService.util.replaceParam
import org.json.JSONObject
import java.util.HashMap

class GetProductAssetRequest(val ctn: String, private val ecsCallback: ECSCallback<Assets?, ECSError>) : ECSJsonRequest() {

    override fun getURL(): String {
        var url = ECSDataHolder.urlMap?.get(getServiceID())?.configUrls ?: ""
        return url.replaceParam(getReplaceURLMap())
    }

    override fun getServiceID(): String {
        return ECSConstants.SERVICEID_PRX_ASSETS
    }

    override fun onErrorResponse(error: VolleyError) {
        var serverError = error.getJsonError()?.getData(ServerError::class.java).toString()
        ecsCallback.onFailure(ECSError(serverError))
    }

    override fun onResponse(response: JSONObject) {
        var resp = response.getData(AssetModel::class.java)
        val assets = resp?.data?.assets
        ecsCallback.onResponse(assets)

    }

    override fun getReplaceURLMap(): MutableMap<String, String> {

        val replaceUrl: MutableMap<String, String> = HashMap()
        replaceUrl["sector"] = PrxConstants.Sector.B2C.toString()
        replaceUrl["catalog"] = PrxConstants.Catalog.CONSUMER.toString()
        replaceUrl["ctn"] = ctn
        return replaceUrl
    }


}