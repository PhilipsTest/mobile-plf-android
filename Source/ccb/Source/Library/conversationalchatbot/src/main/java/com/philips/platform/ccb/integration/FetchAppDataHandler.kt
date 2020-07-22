package com.philips.platform.ccb.integration

interface FetchAppDataHandler {
    fun getDataFromApp(key: String): String?
}