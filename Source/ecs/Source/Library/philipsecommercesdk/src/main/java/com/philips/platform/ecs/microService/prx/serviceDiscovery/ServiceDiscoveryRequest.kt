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
package com.philips.platform.ecs.microService.prx.serviceDiscovery

import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface.OnGetServiceUrlMapListener
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService
import com.philips.platform.ecs.util.ECSConfiguration
import java.util.*

interface ServiceDiscoveryRequest {

     fun getSector():PrxConstants.Sector{
     return PrxConstants.Sector.B2C
     }

     fun getCatalog():PrxConstants.Catalog{
        return PrxConstants.Catalog.CONSUMER
     }

    fun getServiceID():String

    fun getRequestUrlFromAppInfra(listener: OnUrlReceived) {

        val serviceIDList = ArrayList<String>()
        serviceIDList.add(getServiceID())
        ECSConfiguration.INSTANCE.appInfra.serviceDiscovery.getServicesWithCountryPreference(serviceIDList, object : OnGetServiceUrlMapListener {
            override fun onSuccess(urlMap: Map<String, ServiceDiscoveryService>) {
                listener.onSuccess(urlMap[getServiceID()]?.configUrls)
            }

            override fun onError(error: ERRORVALUES, message: String) {
                listener.onError(error, message)
            }
        }, getReplaceURL())
    }

    interface OnUrlReceived : ServiceDiscoveryInterface.OnErrorListener {
        fun onSuccess(url: String?)
    }

    fun getReplaceURL():MutableMap<String,String>{
        val replaceUrl: MutableMap<String, String> = HashMap()
        replaceUrl["sector"] = getSector().toString()
        replaceUrl["catalog"] = getCatalog().toString()
        return replaceUrl
    }


}