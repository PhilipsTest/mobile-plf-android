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
import com.philips.platform.ecs.microService.util.ECSDataHolder
import java.util.ArrayList

class RequestHandler{


    fun handleRequest(ecsAbstractRequest: ECSAbstractRequest){
        val serviceIDList = mutableListOf<String>()
        serviceIDList.add(ecsAbstractRequest.getServiceID())
        ECSDataHolder.appInfra?.serviceDiscovery?.getServicesWithCountryPreference(serviceIDList as ArrayList<String>, getServiceListener(ecsAbstractRequest), ecsAbstractRequest.getReplaceURLMap())
    }

    internal fun getServiceListener(ecsAbstractRequest: ECSAbstractRequest):ServiceDiscoveryInterface.OnGetServiceUrlMapListener{

       return object:ServiceDiscoveryInterface.OnGetServiceUrlMapListener{
            override fun onSuccess(urlMap: MutableMap<String, ServiceDiscoveryService>?) {
                val url = urlMap?.get(ecsAbstractRequest.getServiceID())?.configUrls

                url?.let {
                    ecsAbstractRequest.url = it
                    ecsAbstractRequest.locale = urlMap[ecsAbstractRequest.getServiceID()]?.locale ?: ""
                    ecsAbstractRequest.executeRequest()
                }?:run {

                    ecsAbstractRequest.ecsErrorCallback.onFailure(ECSError(urlMap?.get(ecsAbstractRequest.getServiceID())?.getmError()?:"",null,null))
                }

            }

            override fun onError(error: ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES?, message: String?) {
                ecsAbstractRequest.ecsErrorCallback.onFailure(ECSError(message?:"",null,null))
            }
        }
    }
}