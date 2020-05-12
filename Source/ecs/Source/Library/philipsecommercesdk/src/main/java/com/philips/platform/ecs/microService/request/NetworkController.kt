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

import com.philips.platform.ecs.microService.util.ECSDataHolder

class NetworkController() {

    fun executeRequest(ecsRequest: ECSRequestInterface) {

        when (ecsRequest.getRequestType()) {

            RequestType.JSON -> ECSDataHolder.appInfra?.restClient?.requestQueue?.add(ecsRequest.getAppInfraJSONObject())
            RequestType.STRING -> ECSDataHolder.appInfra?.restClient?.requestQueue?.add(ecsRequest.getAppInfraStringRequest())
        }
    }

}