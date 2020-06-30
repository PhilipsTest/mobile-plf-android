package com.philips.platform.ccb.directline

import com.philips.platform.ccb.model.CCBConversation
import com.philips.platform.ccb.model.CCBMessage
import java.util.ArrayList

interface CCBConversationHandlerInterface {

    fun postMessage(ccbConversation: CCBConversation)

    fun getAllMessages(ccbConversation: CCBConversation)
}