/*
 * Copyright (c) Koninklijke Philips N.V., 2020
 *
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 *
 */

package com.philips.platform.ccbdemo

import android.app.Application
import android.content.Context
import com.philips.platform.appinfra.AppInfra
import com.philips.platform.appinfra.AppInfraInterface
import com.philips.platform.uid.thememanager.*

class CCBDemoApplication  : Application() {

    private lateinit var appInfraInterface: AppInfraInterface

    override fun onCreate() {
        super.onCreate()
        UIDHelper.injectCalligraphyFonts()
        theme.applyStyle(R.style.Theme_DLS_Blue_UltraLight, true)
        UIDHelper.init(ThemeConfiguration(this, ContentColor.ULTRA_LIGHT, NavigationColor.BRIGHT, AccentRange.ORANGE))
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