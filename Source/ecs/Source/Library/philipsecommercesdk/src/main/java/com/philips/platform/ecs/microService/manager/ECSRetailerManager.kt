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
import com.philips.platform.ecs.microService.model.retailer.ECSRetailerList
import com.philips.platform.ecs.microService.request.GetRetailersInfoRequest

class ECSRetailerManager {

    fun fetchRetailers(ctn: String, ecsCallback: ECSCallback<ECSRetailerList?, ECSError>) {

        val ecsException = ECSApiValidator().getECSException(APIType.Locale)

        ecsException?.let { throw ecsException } ?: kotlin.run {
            val getRetailersInfoRequest = GetRetailersInfoRequest(ctn, ecsCallback)
            RequestHandler(getRetailersInfoRequest).handleRequest()
        }
    }
}