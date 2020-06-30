package com.philips.platform.ccb.rest

interface CCBRequestInterface {

    fun getUrl(): String

    fun getHeader(): Map<String, String>

    fun getBody(): String?

    fun getMethodType(): Int
}