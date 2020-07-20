package com.philips.platform.ccb.directline

import com.philips.platform.ccb.errors.CCBError
import com.philips.platform.ccb.model.CCBMessage

interface CCBConversationHandlerInterface {

    fun postMessage(text:String?,completionHandler: (Boolean, CCBError?) -> Unit)

    fun getAllMessages(completionHandler: (CCBMessage?, CCBError?) -> Unit)
}