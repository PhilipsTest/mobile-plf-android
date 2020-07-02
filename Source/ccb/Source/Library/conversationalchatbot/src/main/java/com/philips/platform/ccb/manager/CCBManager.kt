package com.philips.platform.ccb.manager

import com.philips.platform.ccb.directline.CCBAzureSessionHandler
import com.philips.platform.ccb.directline.CCBSessionHandlerInterface
import com.philips.platform.ccb.model.CCBConversation


object CCBManager {

    private val ccbAzureSessionHandler by lazy {
        CCBAzureSessionHandler()
    }

    var ccbConversation: CCBConversation? = null

    @JvmStatic
    fun  getCCBSessionHandlerInterface(): CCBSessionHandlerInterface{
        return ccbAzureSessionHandler
    }
}