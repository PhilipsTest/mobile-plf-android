package com.philips.platform.ccb.integration

interface CCBDeviceCapabilityInterface {
    fun isDeviceConnected(deviceID: String): Boolean
}