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
import com.philips.platform.ecs.microService.request.GetCartRequest
import com.philips.platform.ecs.model.cart.ECSShoppingCart

class ECSCartManager {

    internal var requestHandler = RequestHandler()

    fun fetchShoppingCart(ecsCallback: ECSCallback<ECSShoppingCart, ECSError>){
        val ecsException = ECSApiValidator().getECSException(APIType.LocaleHybrisAndAuth)

        ecsException?.let { throw ecsException } ?: kotlin.run {
          requestHandler.handleRequest(GetCartRequest(ecsCallback))
        }
    }
}