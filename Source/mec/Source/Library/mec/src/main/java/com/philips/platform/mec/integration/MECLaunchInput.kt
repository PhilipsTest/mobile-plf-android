/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.integration



import com.philips.platform.pif.DataInterface.MEC.listeners.MECCartUpdateListener
import com.philips.platform.uappframework.uappinput.UappLaunchInput

import java.io.Serializable

/**
 * MECLaunchInput is responsible for initializing the settings required for launching.
 * @since 1.0.0
 */
class MECLaunchInput : UappLaunchInput(), Serializable {


    var mecBazaarVoiceInput = MECBazaarVoiceInput()
    var mecBannerConfigurator: MECBannerConfigurator ? = null
    var mecOrderFlowCompletion: MECOrderFlowCompletion? = null
    var voucherCode : String = ""
    var maxCartCount : Int = 0

    var mecCartUpdateListener: MECCartUpdateListener? =null
    var blackListedRetailerNames : List<String>? = null
    var flowConfigurator: MECFlowConfigurator ? =null

    var supportsHybris: Boolean = true
    var supportsRetailer : Boolean = true

}
