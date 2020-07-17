package com.philips.platform.ccb.util

import com.philips.platform.appinfra.logging.LoggingInterface

object CCBLog {

    var appInfraLoggingInterface: LoggingInterface? = null


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