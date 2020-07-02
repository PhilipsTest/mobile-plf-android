package com.philips.platform.ccb.manager

import com.philips.platform.ccb.directline.CCBAzureSessionHandler
import com.philips.platform.ccb.directline.CCBSessionHandlerInterface
import com.philips.platform.ccb.model.CCBConversation


object CCBManager {

    init {

    }

    private val ccbAzureSessionHandler by lazy {
        CCBAzureSessionHandler()
    }

    var ccbConversation: CCBConversation? = null

    var token: String = ""

    @JvmStatic
    fun  getCCBSessionHandlerInterface(): CCBSessionHandlerInterface{
        return ccbAzureSessionHandler
    }
}