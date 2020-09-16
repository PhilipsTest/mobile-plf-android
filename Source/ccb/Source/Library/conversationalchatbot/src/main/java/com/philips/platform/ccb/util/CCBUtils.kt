package com.philips.platform.ccb.util

import android.net.Uri
import com.philips.platform.ccb.analytics.CCBAnalytics
import com.philips.platform.ccb.analytics.CCBAnalyticsConstant.exitlinkparameter
import com.philips.platform.ccb.manager.CCBSettingsManager
import java.net.MalformedURLException
import java.net.URL

class CCBUtils {
    companion object {

        fun getPhilipsFormattedUrl(url: String): String? {
            val appName: String = CCBSettingsManager.mAppInfraInterface.appIdentity.appName
            val localeTag: String = CCBSettingsManager.mAppInfraInterface.internationalization.uiLocaleString
            val builder = Uri.Builder().appendQueryParameter("origin", String.format(exitlinkparameter, localeTag, appName, appName))
            return if (isParameterizedURL(url)) {
                url + "&" + builder.toString().replace("?", "")
            } else {
                url + builder.toString()
            }
        }

        private fun isParameterizedURL(url: String): Boolean {
            try {
                val urlString = URL(url)
                return urlString.query != null
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }
    }
}