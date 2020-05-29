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
import com.philips.platform.ecs.microService.callBack.BaseURLCallback
import com.philips.platform.ecs.microService.constant.ECSConstants
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.model.config.ECSConfig
import com.philips.platform.ecs.microService.request.GetConfigurationRequest
import com.philips.platform.ecs.microService.util.ECSDataHolder
import java.util.ArrayList


class ECSConfigManager {


    fun getConfigObject(ecsCallback: ECSCallback<ECSConfig, ECSError>){
        val getConfigurationRequest = GetConfigurationRequest(ecsCallback)
        ECSDataHolder.appInfra?.serviceDiscovery?.getServicesWithCountryPreference(getBaseURLServiceID(),BaseURLCallback(getConfigurationRequest), null)

    }

    fun getBaseURLServiceID() : ArrayList<String> {
        val listOFServiceID = mutableListOf<String>()
        listOFServiceID.add(ECSConstants.SERVICEID_IAP_BASEURL)
        return listOFServiceID as ArrayList<String>
    }
}