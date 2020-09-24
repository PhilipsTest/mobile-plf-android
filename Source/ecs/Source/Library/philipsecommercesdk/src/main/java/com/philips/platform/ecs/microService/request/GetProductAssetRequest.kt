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

import com.philips.cdp.prxclient.PrxConstants
import com.philips.cdp.prxclient.datamodels.assets.AssetModel
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.constant.ECSConstants
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.ecs.microService.util.getData
import org.json.JSONObject
import java.util.HashMap

class GetProductAssetRequest(val ecsProduct: ECSProduct, val ecsCallback: ECSCallback<ECSProduct, ECSError>) : ECSJsonRequest(ecsCallback) {


    override fun getServiceID(): String {
        return ECSConstants.SERVICEID_PRX_ASSETS
    }

    override fun onResponse(response: JSONObject) {
        var resp = response.getData(AssetModel::class.java)
        val assets = resp?.data?.assets
        ecsProduct.assets =assets
        ecsCallback.onResponse(ecsProduct)
    }

    override fun getReplaceURLMap(): MutableMap<String, String> {

        val replaceUrl: MutableMap<String, String> = HashMap()
        replaceUrl["sector"] = PrxConstants.Sector.B2C.toString()
        replaceUrl["catalog"] = PrxConstants.Catalog.CONSUMER.toString()
        replaceUrl["ctn"] = ecsProduct.ctn.replace('/', '_')
        return replaceUrl
    }

    override fun getHeader(): MutableMap<String, String>? {
        return null
    }
}