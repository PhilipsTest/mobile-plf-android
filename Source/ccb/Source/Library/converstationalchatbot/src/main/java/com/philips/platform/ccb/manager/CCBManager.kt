package com.philips.platform.ccb.manager

import com.philips.platform.ccb.directline.CCBAuthHandler
import com.philips.platform.ccb.directline.CCBAzureSessionHandler
import com.philips.platform.ccb.directline.CCBSessionHandlerInterface
import com.philips.platform.ccb.model.CCBConversation
import com.philips.platform.ccb.model.CCBUser


enum class CCBManager {

    INSTANCE;

    /*var ccbAzureSessionHandler: CCBAzureSessionHandler? =null

    init {
        ccbAzureSessionHandler = CCBAzureSessionHandler()
    }*/

    var ccbConversation: CCBConversation? = null

   /* fun startConversation(ccbUser: CCBUser, ccbAuthHandler: CCBAuthHandler){
        ccbAzureSessionHandler?.authenticateUser(ccbUser,ccbAuthHandler)
    }*/

    fun getccbSessionHandlerInterface(): CCBSessionHandlerInterface{
        val ccbAzureSessionHandler = CCBAzureSessionHandler()
        return ccbAzureSessionHandler
    }
}