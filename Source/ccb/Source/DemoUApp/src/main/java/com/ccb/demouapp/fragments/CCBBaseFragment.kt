package com.ccb.demouapp.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import androidx.fragment.app.Fragment
import com.philips.platform.ccb.integration.FetchAppDataHandler


open class CCBBaseFragment : Fragment(), FetchAppDataHandler {

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

            "PUT_SSD" ->
                return getWifiSSD()
        }
        return null
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
}
