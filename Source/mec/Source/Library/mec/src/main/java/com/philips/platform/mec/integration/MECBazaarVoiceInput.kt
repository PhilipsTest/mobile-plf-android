/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.integration

import com.philips.platform.mec.screens.reviews.MECBazaarVoiceEnvironment

open class MECBazaarVoiceInput {

    open fun getBazaarVoiceClientID (): String{

        return "philipsglobal";
    }

    open fun getBazaarVoiceConversationAPIKey (): String{
        return "caAyWvBUz6K3xq4SXedraFDzuFoVK71xMplaDk1oO5P4E"
    }

    open fun getBazaarVoiceEnvironment() : MECBazaarVoiceEnvironment{
        return MECBazaarVoiceEnvironment.PRODUCTION
    }
}