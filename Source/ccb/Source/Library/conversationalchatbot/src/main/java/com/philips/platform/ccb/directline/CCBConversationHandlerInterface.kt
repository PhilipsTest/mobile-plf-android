package com.philips.platform.ccb.directline

import com.philips.platform.ccb.errors.CCBError
import com.philips.platform.ccb.model.CCBConversation
import com.philips.platform.ccb.model.CCBMessage
import java.util.ArrayList

interface CCBConversationHandlerInterface {

    fun postMessage(message: String?, completionHandler: (Boolean, CCBError?) -> Unit)

    fun getAllMessages(ccbConversation: CCBConversation, completionHandler: (Boolean, CCBError?) -> Unit)
}