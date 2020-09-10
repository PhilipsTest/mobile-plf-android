/*
 * Copyright (c) Koninklijke Philips N.V., 2020
 *
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 *
 */

package com.philips.platform.ccb.util

import com.philips.platform.appinfra.logging.LoggingInterface
import com.philips.platform.ccb.manager.CCBSettingsManager

object CCBLog {

    var appInfraLoggingInterface: LoggingInterface? = null

    fun init() {
        appInfraLoggingInterface = CCBSettingsManager.mLoggingInterface
    }

    fun d(tag: String?, message: String?) {
        appInfraLoggingInterface?.log(LoggingInterface.LogLevel.DEBUG, tag, message)
    }

    fun e(tag: String?, message: String?) {
        appInfraLoggingInterface?.log(LoggingInterface.LogLevel.ERROR, tag, message)
    }

    fun i(tag: String?, message: String?) {
        appInfraLoggingInterface?.log(LoggingInterface.LogLevel.INFO, tag, message)
    }

    fun v(tag: String?, message: String?) {
        appInfraLoggingInterface?.log(LoggingInterface.LogLevel.VERBOSE, tag, message)
    }
}