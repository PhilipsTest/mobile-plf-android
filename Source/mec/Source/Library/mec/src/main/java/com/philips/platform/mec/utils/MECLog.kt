/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.utils

import com.philips.platform.appinfra.logging.LoggingInterface

object MECLog {

    var isLoggingEnabled = false
    var appInfraLoggingInterface: LoggingInterface? = null


    fun d(tag: String?, message: String?) {
        if (isLoggingEnabled) appInfraLoggingInterface?.log(LoggingInterface.LogLevel.DEBUG, tag, message)
    }

    fun e(tag: String?, message: String?) {
        if (isLoggingEnabled) appInfraLoggingInterface?.log(LoggingInterface.LogLevel.ERROR, tag, message)
    }

    fun i(tag: String?, message: String?) {
        if (isLoggingEnabled) appInfraLoggingInterface?.log(LoggingInterface.LogLevel.INFO, tag, message)
    }

    fun v(tag: String?, message: String?) {
        if (isLoggingEnabled) appInfraLoggingInterface?.log(LoggingInterface.LogLevel.VERBOSE, tag, message)
    }
}