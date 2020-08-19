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
import com.philips.platform.ccb.model.CCBMessage

interface CCBConversationHandlerInterface {

    fun postMessage(text:String?,completionHandler: (Boolean, CCBError?) -> Unit)

    fun getAllMessages(completionHandler: (CCBMessage?, CCBError?) -> Unit)
}