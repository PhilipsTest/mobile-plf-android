package com.philips.platform.ccbdemo

import android.app.Application
import android.content.Context
import com.philips.platform.appinfra.AppInfra
import com.philips.platform.appinfra.AppInfraInterface
import com.philips.platform.uid.thememanager.UIDHelper

class CCBDemoApplication  : Application() {

    private lateinit var appInfraInterface: AppInfraInterface

    override fun onCreate() {
        super.onCreate()

        appInfraInterface = AppInfra.Builder().build(this)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        UIDHelper.injectCalligraphyFonts()
    }

    fun getAppInfra(): AppInfraInterface {
        return appInfraInterface
    }
}