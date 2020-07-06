package com.philips.platform.ccb.listeners

interface BotResponseListener {
    fun onMessageReceived(jsonResponse: String)
}