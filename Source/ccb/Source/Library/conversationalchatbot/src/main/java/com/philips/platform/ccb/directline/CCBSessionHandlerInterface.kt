package com.philips.platform.ccb.directline

import com.philips.platform.ccb.errors.CCBError
import com.philips.platform.ccb.model.CCBConversation
import com.philips.platform.ccb.model.CCBUser

interface CCBSessionHandlerInterface {

    fun authenticateUser(ccbUser: CCBUser, completionHandler: (Boolean, CCBError?) -> Unit)

    fun startConversation(completionHandler: (CCBConversation?, CCBError?) -> Unit)

    fun refreshSession(completionHandler: (Boolean, CCBError?) -> Unit)

    fun endConversation(completionHandler: (Boolean, CCBError?) -> Unit)
}