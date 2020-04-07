/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.reviews

import android.app.Application
import com.bazaarvoice.bvandroidsdk.*
import com.philips.platform.mec.utils.MECDataHolder
import okhttp3.OkHttpClient

class BazaarVoiceHelper {

    fun getBazaarVoiceClient(context: Application): BVConversationsClient {

        val bazaarClientId = MECDataHolder.INSTANCE.mecBazaarVoiceInput.getBazaarVoiceClientID()
        val bazaarApiConversationKey = MECDataHolder.INSTANCE.mecBazaarVoiceInput.getBazaarVoiceConversationAPIKey()
        val bazaarEnvironment = MECDataHolder.INSTANCE.mecBazaarVoiceInput.getBazaarVoiceEnvironment()

        val bvConfigBuilder = BVConfig.Builder()
        bvConfigBuilder.clientId(bazaarClientId)
        bvConfigBuilder.apiKeyConversations(bazaarApiConversationKey)
        val bvsdk = BVSDK.builderWithConfig(context, if (bazaarEnvironment == MECBazaarVoiceEnvironment.PRODUCTION) BazaarEnvironment.PRODUCTION else BazaarEnvironment.STAGING, bvConfigBuilder.build())
                .logLevel(BVLogLevel.ERROR)
                .okHttpClient(getOkHttpClient())
                .build()


        return BVConversationsClient.Builder(bvsdk).build()
    }

    private fun getOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .build()
    }
}