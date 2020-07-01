package com.philips.platform.ccb.directline

import com.philips.platform.ccb.integration.ccbCallback
import com.philips.platform.ccb.model.CCBConversation
import com.philips.platform.ccb.model.CCBUser
import java.lang.Exception

interface CCBSessionHandlerInterface {

    fun authenticateUser(ccbUser: CCBUser, ccbCallback: ccbCallback<Boolean,Exception>?)

    fun startConversation(ccbCallback: ccbCallback<CCBConversation,Exception>?)

    fun refreshSession(ccbCallback: ccbCallback<Boolean,Exception>?)

    fun endConversation(ccbCallback: ccbCallback<Boolean,Exception>?)
}