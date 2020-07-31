package com.ccb.demouapp.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import com.philips.platform.ccb.integration.FetchAppDataHandler


open class CCBBaseFragment : Fragment(), FetchAppDataHandler {

    var deviceSettingsListener: FetchAppDataHandler.DeviceSettingsListener? = null

    fun addFragment(newFragment: CCBBaseFragment,
                    isAddWithBackStack: Boolean) {
        if (null != activity && !activity!!.isFinishing) {
            val transaction = activity!!.supportFragmentManager.beginTransaction()
            val simpleName = newFragment.javaClass.simpleName
            transaction.add(id, newFragment, simpleName)
            if (isAddWithBackStack) {
                transaction.addToBackStack(simpleName)
            }
            transaction.commitAllowingStateLoss()
        }
    }

    fun replaceFragment(newFragment: CCBBaseFragment,
                        isReplaceWithBackStack: Boolean) {
        if (null != activity && !activity!!.isFinishing) {

            val transaction = activity!!.supportFragmentManager.beginTransaction()
            val simpleName = newFragment.javaClass.simpleName

            transaction.replace(id, newFragment, simpleName)
            if (isReplaceWithBackStack) {
                transaction.addToBackStack(simpleName)
            }
            transaction.commitAllowingStateLoss()
        }
    }

    override fun getDataFromApp(key: String): String? {
        when (key) {
            "GET_WiFi" ->
                return if (isWiFiConnected())
                    "ON"
                else
                    "OFF"

            "PUT_SSID" ->
                return getWifiSSD()
        }
        return null
    }

    override fun changeDeviceSettings(key: String, deviceSettingsListener: FetchAppDataHandler.DeviceSettingsListener) {
        when (key) {
            "ACTION_WiFi" -> {
                this.deviceSettingsListener = deviceSettingsListener
                Log.i("SHASHI", "getDataFromApp : $key")
                toggleWifi(true)
            }
        }
    }

     private fun isWiFiConnected(): Boolean {
        val connMgr = context?.applicationContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var isWifiConn: Boolean = false
        var isMobileConn: Boolean = false
        connMgr.allNetworks.forEach { network ->
            connMgr.getNetworkInfo(network).apply {
                if (type == ConnectivityManager.TYPE_WIFI) {
                    isWifiConn = isWifiConn or isConnected
                }
                if (type == ConnectivityManager.TYPE_MOBILE) {
                    isMobileConn = isMobileConn or isConnected
                }
            }
        }
        return isWifiConn
    }

    private fun getWifiSSD(): String? {
        if (!isWiFiConnected()) return null
        val wifiManager = context?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifiManager.connectionInfo.ssid
    }

    private fun toggleWifi(enable: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val panelIntent = Intent(Settings.Panel.ACTION_WIFI)
            startActivityForResult(panelIntent, 100)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
       // super.onActivityResult(requestCode, resultCode, data)
        Log.i("Shashi","onActivityResult : $requestCode  $resultCode")
        if (requestCode == 100 && isWiFiConnected()){
            deviceSettingsListener?.onSuccess("APP_SYNC-WiFi_Enabled")
        }else{
            deviceSettingsListener?.onFailure()
        }


    }
}
