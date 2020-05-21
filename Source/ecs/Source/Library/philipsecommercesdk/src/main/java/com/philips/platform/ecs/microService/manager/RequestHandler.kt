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

import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.request.ECSAbstractRequest
import com.philips.platform.ecs.microService.request.NetworkController
import com.philips.platform.ecs.microService.util.ECSDataHolder
import java.util.ArrayList

class RequestHandler (private val ecsAbstractRequest: ECSAbstractRequest){


    private val serviceURLListener = object:ServiceDiscoveryInterface.OnGetServiceUrlMapListener{
        override fun onSuccess(urlMap: MutableMap<String, ServiceDiscoveryService>?) {
            ecsAbstractRequest.url = urlMap?.get(ecsAbstractRequest.getServiceID())?.configUrls ?: ""
            ecsAbstractRequest.locale = urlMap?.get(ecsAbstractRequest.getServiceID())?.locale ?: ""
            NetworkController().executeRequest(ecsAbstractRequest)
        }

        override fun onError(error: ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES?, message: String?) {
            ecsAbstractRequest.ecsErrorCallback.onFailure(ECSError(message?:"",null,null))
        }
    }

    fun handleRequest(){
        ECSDataHolder.appInfra?.serviceDiscovery?.getServicesWithCountryPreference(listOf(ecsAbstractRequest.getServiceID()) as ArrayList<String>?, serviceURLListener, ecsAbstractRequest.getReplaceURLMap())
    }
}