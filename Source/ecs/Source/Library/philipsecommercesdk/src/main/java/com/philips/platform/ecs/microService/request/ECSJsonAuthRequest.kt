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

import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.util.ECSDataHolder

abstract class ECSJsonAuthRequest(ecsErrorCallback: ECSCallback<*, ECSError>) : ECSJsonRequest(ecsErrorCallback){

    override fun getHeader(): MutableMap<String, String>? {
        val header = super.getHeader()
        val authToken = ECSDataHolder.authToken
        authToken?.let { header?.put("Authorization","bearer $it") }
        return header
    }
    override fun getReplaceURLMap(): MutableMap<String, String> {
        val replaceURLMap = super.getReplaceURLMap()
        replaceURLMap["cartId"]="current"
        return replaceURLMap
    }
}