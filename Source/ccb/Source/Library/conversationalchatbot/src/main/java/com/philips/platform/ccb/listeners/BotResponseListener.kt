package com.philips.platform.ccb.listeners

interface BotResponseListener {
    fun onOpen()
    fun onFailure()
    fun onMessageReceived(jsonResponse: String)
    fun onClosed()
}