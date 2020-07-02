package com.philips.platform.ccb.manager

import com.philips.platform.ccb.directline.CCBAzureConversationHandler
import com.philips.platform.ccb.directline.CCBAzureSessionHandler
import com.philips.platform.ccb.directline.CCBConversationHandlerInterface
import com.philips.platform.ccb.directline.CCBSessionHandlerInterface
import com.philips.platform.ccb.model.CCBConversation


object CCBManager {

    init {

    }

    private val ccbAzureSessionHandler by lazy {
        CCBAzureSessionHandler()
    }

    private val ccbAzureConversationHandler by lazy {
        CCBAzureConversationHandler()
    }

    var ccbConversation: CCBConversation? = null

    var token: String = ""

    var streamUrl: String = ""

    var conversationId: String = ""

    @JvmStatic
    fun  getCCBSessionHandlerInterface(): CCBSessionHandlerInterface{
        return ccbAzureSessionHandler
    }

    @JvmStatic
    fun  getCCBConversationHandlerInterface(): CCBConversationHandlerInterface{
        return ccbAzureConversationHandler
    }
}