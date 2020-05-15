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
import com.philips.platform.ecs.microService.constant.ECSConstants.Companion.SERVICEID_ECS_PRODUCT_DETAILS
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.error.VolleyHandler
import com.philips.platform.ecs.microService.manager.ECSProductManager
import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.ecs.microService.util.getData
import com.philips.platform.ecs.microService.util.replaceParam
import org.json.JSONObject

class GetProductForRequest(private val ctn: String, private val ecsCallback: ECSCallback<ECSProduct?, ECSError>) : ECSJsonRequest() {


    var url = "https://acc.eu-west-1.api.philips.com/commerce-service/product/%ctn%?siteId=%siteId%&language=%language%&country=%country%"

    var ecsProductManager = ECSProductManager()

    override fun getURL(): String {
        return url.replaceParam(getReplaceURLMap())
    }

    override fun getServiceID(): String {
        return SERVICEID_ECS_PRODUCT_DETAILS
    }

    override fun getReplaceURLMap(): MutableMap<String, String> {
        val replaceURLMap = super.getReplaceURLMap()
        replaceURLMap["ctn"] = ctn.replace('/', '_')
        return replaceURLMap
    }

    override fun onErrorResponse(error: VolleyError) {
        ecsCallback.onFailure(VolleyHandler().getECSError(error))
    }

    override fun onResponse(response: JSONObject) {
        val ecsProduct = response.getData(ECSProduct::class.java)
        ecsProduct?.id?.let {getSummaryForProduct(ecsProduct)} ?: kotlin.run {  ecsCallback.onResponse(null) }
    }

    private fun getSummaryForProduct(ecsProduct : ECSProduct){
        ecsProductManager.getSummaryForSingleProduct(ecsProduct,ecsCallback)
    }

}