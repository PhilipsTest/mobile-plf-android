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
import com.philips.platform.ecs.microService.model.cart.ECSShoppingCart
import com.philips.platform.ecs.microService.util.getData
import com.philips.platform.ecs.microService.util.replaceParam
import org.json.JSONObject

abstract class AbstractCartRequest(val ecsCallback: ECSCallback<ECSShoppingCart, ECSError>) :ECSJsonAuthRequest(ecsCallback) {

    //TODO to be removed once service discovery is up
    override fun getURL(): String {
        return url.replaceParam(getReplaceURLMap())
    }

    override fun onResponse(response: JSONObject?) {
        val ecsShoppingCart = response?.getData(ECSShoppingCart::class.java)
        ecsShoppingCart ?.let {  ecsCallback.onResponse(ecsShoppingCart)  } ?: kotlin.run {  ecsCallback.onFailure( ErrorHandler().getECSError(null))}
    }

    override fun getHeader(): MutableMap<String, String>? {
        val header = super.getHeader()
        header?.put("Content-Type","application/json")
        return header
    }
}