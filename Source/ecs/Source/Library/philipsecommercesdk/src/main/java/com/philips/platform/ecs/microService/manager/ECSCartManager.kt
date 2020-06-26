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

package com.philips.platform.ecs.microService.manager

import com.android.volley.Request
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.model.cart.ECSShoppingCart
import com.philips.platform.ecs.microService.request.CreateCartRequest
import com.philips.platform.ecs.microService.request.GetCartRequest


class ECSCartManager {

    internal var requestHandler = RequestHandler()

    fun fetchShoppingCart(ecsCallback: ECSCallback<ECSShoppingCart, ECSError>){
        val ecsException = ECSApiValidator().getECSException(APIType.LocaleHybrisAndAuth)

        ecsException?.let { throw ecsException } ?: kotlin.run {
          //requestHandler.handleRequest(GetCartRequest(ecsCallback))
            GetCartRequest(ecsCallback).executeRequest()
        }
    }

    fun createECSShoppingCart(ctn: String, quantity: Int = 1,ecsCallback: ECSCallback<ECSShoppingCart, ECSError>){
        val createCartRequest= CreateCartRequest(ctn,quantity,ecsCallback)
        createCartRequest.requestMethod= Request.Method.POST

        val ecsException = ECSApiValidator().getECSException(APIType.LocaleHybrisAndAuth)
        ecsException?.let { throw ecsException } ?: kotlin.run {
            createCartRequest.executeRequest()
           // requestHandler.handleRequest(createCartRequest)
        }
    }
}