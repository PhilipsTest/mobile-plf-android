package com.philips.platform.ccb.integration

interface FetchAppDataHandler {
    fun getDataFromApp(key: String): String?
    fun changeDeviceSettings(key: String, deviceSettingsListener: DeviceSettingsListener)

    interface DeviceSettingsListener{
        fun onSuccess(successAction:String)
        fun onFailure()
    }
}