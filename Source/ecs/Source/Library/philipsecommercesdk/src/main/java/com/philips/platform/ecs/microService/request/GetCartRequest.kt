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
import com.philips.platform.ecs.microService.constant.ECSConstants.Companion.SERVICEID_ECS_GET_CART
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.error.ErrorHandler
import com.philips.platform.ecs.microService.model.cart.ECSShoppingCart
import com.philips.platform.ecs.microService.util.getData
import com.philips.platform.ecs.microService.util.replaceParam

import org.json.JSONObject

class GetCartRequest(ecsCallback: ECSCallback<ECSShoppingCart, ECSError>) : AbstractCartRequest(ecsCallback) {

    override fun getServiceID(): String {
        return SERVICEID_ECS_GET_CART
    }

    override fun getHeader(): MutableMap<String, String>? {
        val header = super.getHeader()
        header?.remove("Content-Type") //As getCart does not need content type
        return header
    }

    override fun getReplaceURLMap(): MutableMap<String, String> {
        val replaceURLMap = super.getReplaceURLMap()
        replaceURLMap["cartId"]="current"
        return replaceURLMap
    }
}