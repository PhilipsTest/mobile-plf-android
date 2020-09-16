package com.philips.platform.ccb.analytics

import com.philips.platform.appinfra.BuildConfig
import com.philips.platform.appinfra.tagging.AppTaggingInterface
import com.philips.platform.ccb.util.CCBLog
import com.philips.platform.uappframework.uappinput.UappDependencies
import java.util.*

class CCBAnalytics {

    companion object {
        private val TAG: String = CCBAnalytics::class.java.simpleName

        var mAppTaggingInterface: AppTaggingInterface? = null

        @JvmStatic
        fun initCCBAnalytics(dependencies: UappDependencies) {
            try {
                mAppTaggingInterface = dependencies.appInfra.tagging.createInstanceForComponent(CCBAnalyticsConstant.COMPONENT_NAME, BuildConfig.VERSION_NAME)
            } catch (e: Exception) {
                CCBLog.d(TAG, "Exception :" + e.message);
            }
        }

        fun trackPage(pageName: String?) {
            mAppTaggingInterface?.trackPageWithInfo(pageName, null)
        }

        fun trackAction(event: String?, key: String, value: String) {
            val commonGoalsMap: MutableMap<String, String> = HashMap()
            commonGoalsMap[key] = value
            mAppTaggingInterface?.trackActionWithInfo(event, commonGoalsMap)
        }

    }

}