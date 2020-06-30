package com.philips.platform.ccb.directline

import java.lang.Error

interface CCBAuthHandler {

    fun onAuthSucccess()

    fun onAuthFailureWithError(error:Error)
}