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
            val getCartRequest = GetCartRequest(ecsCallback)
            getCartRequest.url = "https://acc.eu-west-1.api.philips.com/commerce-service/cart/%cartId%?siteId=%siteId%&language=%language%&country=%country%"
            getCartRequest.executeRequest()

        }
    }

    fun createECSShoppingCart(ctn: String, quantity: Int = 1,ecsCallback: ECSCallback<ECSShoppingCart, ECSError>){
        ECSApiValidator().isValidCTN(ctn)?.let{ ecsCallback.onFailure(it)}?:run {
            val createCartRequest = CreateCartRequest(ctn, quantity, ecsCallback)
            createCartRequest.url = "https://acc.eu-west-1.api.philips.com/commerce-service/cart?siteId=%siteId%&language=%language%&country=%country%&productId=%ctn%&quantity=%quantity%"
            val ecsException = ECSApiValidator().getECSException(APIType.LocaleHybrisAndAuth)
            ecsException?.let { throw ecsException } ?: kotlin.run {
                    createCartRequest.executeRequest()
                    // requestHandler.handleRequest(createCartRequest)
            }
        }
    }


}