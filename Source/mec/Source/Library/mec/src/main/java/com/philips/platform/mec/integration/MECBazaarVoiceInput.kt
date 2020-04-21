/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.integration

import com.philips.platform.mec.screens.reviews.MECBazaarVoiceEnvironment

open class MECBazaarVoiceInput {

    open fun getBazaarVoiceClientID (): String{
        return "philipsglobal"
    }

    open fun getBazaarVoiceConversationAPIKey (): String{
        return "ca23LB5V0eOKLe0cX6kPTz6LpAEJ7SGnZHe21XiWJcshc"
    }

    open fun getBazaarVoiceEnvironment() : MECBazaarVoiceEnvironment{
        return MECBazaarVoiceEnvironment.STAGING
    }
}