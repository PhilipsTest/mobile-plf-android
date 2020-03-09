package com.philips.cdp.di.mec.integration


import com.philips.platform.uappframework.uappinput.UappLaunchInput

import java.io.Serializable

/**
 * MECLaunchInput is responsible for initializing the settings required for launching.
 * @since 1.0.0
 */
class MECLaunchInput : UappLaunchInput(), Serializable {


    var mecBazaarVoiceInput = MECBazaarVoiceInput()
    var mecBannerConfigurator: MECBannerConfigurator ? = null


    var mecListener: MECListener ? =null
    //TODO :-Blacklist retailer
    var blackListedRetailerNames : List<String>? = null
    var flowConfigurator: MECFlowConfigurator ? =null

    var supportsHybris: Boolean = true
    var supportsRetailer : Boolean = true

}
