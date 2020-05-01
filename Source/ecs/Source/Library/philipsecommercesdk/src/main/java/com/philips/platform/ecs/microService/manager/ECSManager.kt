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

import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.microService.request.GetConfigurationRequest
import com.philips.platform.ecs.model.config.ECSConfig

class ECSManager {

    fun getConfig(eCSCallback: ECSCallback<ECSConfig, Exception>){
     GetConfigurationRequest(eCSCallback).executeRequest()
    }
}