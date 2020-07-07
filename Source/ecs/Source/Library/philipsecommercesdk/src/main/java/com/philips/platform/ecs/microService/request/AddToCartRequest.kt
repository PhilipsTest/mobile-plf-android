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
import com.philips.platform.ecs.microService.constant.ECSConstants.Companion.SERVICEID_ECS_ADD_TO_CART
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.model.cart.ECSShoppingCart

class AddToCartRequest(ctn: String, quantity: Int,ecsCallback: ECSCallback<ECSShoppingCart, ECSError>)  : CreateCartRequest(ctn,quantity,ecsCallback){

    override fun getServiceID(): String {
       return  SERVICEID_ECS_ADD_TO_CART
    }

    override fun getReplaceURLMap(): MutableMap<String, String> {
        val replaceURLMap = super.getReplaceURLMap()
        replaceURLMap["cartId"]="current"
        return replaceURLMap
    }
}