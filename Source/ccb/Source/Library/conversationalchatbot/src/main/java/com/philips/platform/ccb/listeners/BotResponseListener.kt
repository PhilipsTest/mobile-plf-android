/*
 * Copyright (c) Koninklijke Philips N.V., 2020
 *
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 *
 */

package com.philips.platform.ccb.listeners

interface BotResponseListener {
    fun onOpen()
    fun onFailure()
    fun onMessageReceived(jsonResponse: String)
    fun onClosed()
}