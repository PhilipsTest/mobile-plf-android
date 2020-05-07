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
import com.philips.platform.ecs.microService.util.getData
import com.philips.platform.ecs.model.products.ECSProduct
import com.philips.platform.ecs.store.ECSURLBuilder
import org.json.JSONObject

class GetProductForRequest(private val ctn: String, private val ecsCallback: ECSCallback<ECSProduct, Exception>) : ECSJsonRequest() {


    override fun getURL(): String {
        //TODO
        return ECSURLBuilder().getProduct(ctn)
    }

    override fun onErrorResponse(error: VolleyError) {

    }

    override fun onResponse(response: JSONObject) {
        val ecsProduct = response.getData(ECSProduct::class.java)
        ecsCallback.onResponse(ecsProduct)
    }

}