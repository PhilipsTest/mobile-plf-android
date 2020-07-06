package com.philips.platform.ccb.manager

import com.philips.platform.appinfra.AppInfraInterface
import com.philips.platform.appinfra.logging.LoggingInterface
import com.philips.platform.appinfra.rest.RestInterface
import com.philips.platform.appinfra.tagging.AppTaggingInterface
import com.philips.platform.ccb.BuildConfig
import com.philips.platform.uappframework.uappinput.UappDependencies

object CCBSettingsManager {

    private val COMPONENT_TAGS_ID = "ccb"
    lateinit var mAppInfraInterface: AppInfraInterface
    lateinit var mLoggingInterface: LoggingInterface
    lateinit var mTaggingInterface: AppTaggingInterface
    lateinit var mRestInterface: RestInterface

    fun init(ccbDependencies: UappDependencies) {
        mAppInfraInterface = ccbDependencies.appInfra
        mLoggingInterface = mAppInfraInterface.logging.createInstanceForComponent(COMPONENT_TAGS_ID, BuildConfig.VERSION_NAME)
        mTaggingInterface = mAppInfraInterface.tagging.createInstanceForComponent(COMPONENT_TAGS_ID, BuildConfig.VERSION_NAME)
        mRestInterface = mAppInfraInterface.restClient
    }

}