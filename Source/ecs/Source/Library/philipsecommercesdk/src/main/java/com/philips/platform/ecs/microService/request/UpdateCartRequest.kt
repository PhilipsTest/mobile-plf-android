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

import com.android.volley.Request
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.constant.ECSConstants.Companion.SERVICEID_ECS_CREATE_CART
import com.philips.platform.ecs.microService.constant.ECSConstants.Companion.SERVICEID_ECS_UPDATE_CART
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.error.ErrorHandler
import com.philips.platform.ecs.microService.model.cart.ECSShoppingCart
import com.philips.platform.ecs.microService.util.getData
import com.philips.platform.ecs.microService.util.replaceParam
import org.json.JSONObject

class UpdateCartRequest(private val entryNumber: String, private val quantity: Int, ecsCallback: ECSCallback<ECSShoppingCart, ECSError>)  : AbstractCartRequest(ecsCallback){

    override fun getServiceID(): String {
       return  SERVICEID_ECS_UPDATE_CART
    }

    override fun getRequestMethod(): Int {
        return Request.Method.PUT
    }

    override fun getReplaceURLMap(): MutableMap<String, String> {
        val replaceURLMap = super.getReplaceURLMap()
        replaceURLMap["entryNumber"] = entryNumber
        replaceURLMap["quantity"] = ""+quantity
        replaceURLMap["cartId"]="current"
        return replaceURLMap
    }

}