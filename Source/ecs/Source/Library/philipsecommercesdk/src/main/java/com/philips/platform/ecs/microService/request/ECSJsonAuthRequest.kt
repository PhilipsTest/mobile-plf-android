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

import com.philips.platform.appinfra.rest.TokenProviderInterface
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.util.ECSDataHolder
import com.philips.platform.ecs.util.ECSConfiguration

abstract class ECSJsonAuthRequest(ecsErrorCallback: ECSCallback<*, ECSError>) : ECSJsonRequest(ecsErrorCallback),TokenProviderInterface{

    override fun getHeader(): MutableMap<String, String>? {
        val header = super.getHeader()
        val authToken = ECSDataHolder.authToken
        authToken?.let { header?.put("Authorization","bearer $it") }
        return header
    }

    override fun getToken(): TokenProviderInterface.Token {
        return object : TokenProviderInterface.Token {
            override fun getTokenType(): TokenProviderInterface.TokenType {
                return TokenProviderInterface.TokenType.OAUTH2
            }

            override fun getTokenValue(): String {
                return ECSDataHolder.authToken ?:""
            }
        }
    }



    override fun getTokenProviderInterface(): TokenProviderInterface? {
       return this
    }
}