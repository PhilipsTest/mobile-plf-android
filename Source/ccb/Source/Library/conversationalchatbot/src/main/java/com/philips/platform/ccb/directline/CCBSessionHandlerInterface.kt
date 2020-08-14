/*
 * Copyright (c) Koninklijke Philips N.V., 2020
 *
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 *
 */

package com.philips.platform.ccb.directline

import com.philips.platform.ccb.errors.CCBError
import com.philips.platform.ccb.model.CCBConversation
import com.philips.platform.ccb.model.CCBUser

interface CCBSessionHandlerInterface {

    fun authenticateUser(ccbUser: CCBUser, completionHandler: (Boolean, CCBError?) -> Unit)

    fun startConversation(ccbUser: CCBUser,completionHandler: (Boolean, CCBError?) -> Unit)

    fun refreshSession(completionHandler: (Boolean, CCBError?) -> Unit)

    fun updateConversation(completionHandler: (Boolean, CCBError?) -> Unit)

    fun endConversation(completionHandler: (Boolean, CCBError?) -> Unit)
}