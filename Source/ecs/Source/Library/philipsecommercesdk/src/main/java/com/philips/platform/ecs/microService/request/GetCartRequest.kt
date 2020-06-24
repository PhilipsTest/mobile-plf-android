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
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.error.ErrorHandler
import com.philips.platform.ecs.microService.model.cart.ECSPILShoppingCart
import com.philips.platform.ecs.microService.util.getData

import org.json.JSONObject

class GetCartRequest(val ecsCallback: ECSCallback<ECSPILShoppingCart, ECSError>) : ECSJsonAuthRequest(ecsCallback) {

    override fun getServiceID(): String {
        return "getCart"
    }

    override fun onResponse(response: JSONObject?) {
        val ecsShoppingCart = response?.getData(ECSPILShoppingCart::class.java)

        ecsShoppingCart ?.let {   } ?: kotlin.run {  ecsCallback.onFailure( ErrorHandler().getECSError(null))}
    }

}