/*
 * Copyright (c) Koninklijke Philips N.V., 2020
 *
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 *
 */

package com.philips.platform.ccb.integration

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.net.wifi.WifiManager
import com.philips.platform.ccb.constant.CCBConstants
import com.philips.platform.ccb.manager.CCBSettingsManager

class CCBDeviceUtility(context: Context) {
    val mContext = context
    fun performCommand(actionTitle: String, completionHandler: (String) -> Unit) {
        val commandList = actionTitle.split(":")
        val command = commandList.last()
        when (commandList.get(1)) {
            CCBConstants.commandUSR -> {
                executeUserCommand(command) {
                    completionHandler.invoke(it)
                }
            }
            CCBConstants.commandBot -> {
                executeBotCommand(command) {
                    completionHandler.invoke(it)
                }
            }
        }
    }

    private fun executeBotCommand(command: String, completionHandler: (String) -> Unit) {
        when (command) {
            CCBConstants.commandBLE -> {
                val responseString = CCBConstants.BLE.plus(" ").plus(getBLEStatus())
                completionHandler.invoke(responseString)
            }

            CCBConstants.commandWiFi -> {

                val wifiConnected = isWifiConnected()
                if (wifiConnected)
                    completionHandler.invoke(CCBConstants.WiFi.plus(" On"))
                else
                    completionHandler.invoke(CCBConstants.WiFi.plus(" Off"))
            }
        }
    }

    private fun executeUserCommand(command: String, completionHandler: (String) -> Unit) {
        when (command) {
            CCBConstants.commandDevice -> {
                val deviceConnected = CCBSettingsManager.ccbDeviceCapabilityInterface?.isDeviceConnected("Device ID")
                if (deviceConnected!!)
                    completionHandler.invoke(CCBConstants.Device.plus(" On"))
                else {
                    completionHandler.invoke(CCBConstants.Device.plus(" Off"))
                }
            }
        }
    }

    private fun getBLEStatus(): String {
        val defaultAdapter = BluetoothAdapter.getDefaultAdapter()
        if (defaultAdapter != null && defaultAdapter.isEnabled)
            return "ON"
        else
            return "OFF"
    }

    private fun isWifiConnected(): Boolean {
        val wifiManager = mContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifiManager.connectionInfo.networkId != -1
    }
}