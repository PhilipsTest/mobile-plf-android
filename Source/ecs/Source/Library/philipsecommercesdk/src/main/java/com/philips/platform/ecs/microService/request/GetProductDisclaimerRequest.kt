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
import com.philips.platform.ecs.microService.constant.ECSConstants
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.model.disclaimer.DisclaimerModel
import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.ecs.microService.prx.PRXError
import com.philips.platform.ecs.microService.prx.PrxConstants
import com.philips.platform.ecs.microService.util.getData
import com.philips.platform.ecs.microService.util.getJsonError
import org.json.JSONObject
import java.util.HashMap

class GetProductDisclaimerRequest(val ecsProduct: ECSProduct, private val ecsCallback: ECSCallback<ECSProduct, ECSError>) : ECSJsonRequest() {

    override fun getServiceID(): String {
        return ECSConstants.SERVICEID_PRX_DISCLAIMERS
    }

    override fun onErrorResponse(error: VolleyError) {
        //TODO
        val ecsError = ECSError(error?.message ?: "",null,null)
        ecsCallback.onFailure(ecsError)
    }

    override fun onResponse(response: JSONObject) {
        var disclaimerModel = response.getData(DisclaimerModel::class.java)
        val disclaimers = disclaimerModel?.data?.disclaimers
        ecsProduct.disclaimers = disclaimers
        ecsCallback.onResponse(ecsProduct)

    }

    override fun getReplaceURLMap(): MutableMap<String, String> {

        val replaceUrl: MutableMap<String, String> = HashMap()
        replaceUrl["sector"] = PrxConstants.Sector.B2C.toString()
        replaceUrl["catalog"] = PrxConstants.Catalog.CONSUMER.toString()
        replaceUrl["ctn"] = ecsProduct.id.replace('/', '_')
        return replaceUrl
    }

    override fun getHeader(): MutableMap<String, String>? {
        return null
    }


}