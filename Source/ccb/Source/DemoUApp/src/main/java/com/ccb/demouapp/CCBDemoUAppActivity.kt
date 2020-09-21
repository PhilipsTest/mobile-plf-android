/*
 * Copyright (c) Koninklijke Philips N.V., 2020
 *
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 *
 */

package com.ccb.demouapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ccb.demouapp.fragments.CCBDemoUAppHomeFragment
import com.ccb.demouapp.integration.ThemeHelper
import com.philips.platform.ccbdemouapp.R
import com.philips.platform.uid.thememanager.*

class CCBDemoUAppActivity : AppCompatActivity() {
    private val DEFAULT_THEME = R.style.Theme_DLS_Blue_UltraLight

    override fun onCreate(savedInstanceState: Bundle?) {
        initTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ccbdemo_uapp)

        supportFragmentManager.beginTransaction().add(R.id.fragment_container,CCBDemoUAppHomeFragment()).commit()

    }

    private fun initTheme() {
        UIDHelper.injectCalligraphyFonts()
        val themeResourceID: Int = ThemeHelper(this).themeResourceId
        var themeIndex = themeResourceID
        if (themeIndex <= 0) {
            themeIndex = DEFAULT_THEME
        }
        theme.applyStyle(themeIndex, true)
        UIDHelper.init(ThemeConfiguration(this, ContentColor.ULTRA_LIGHT, NavigationColor.BRIGHT, AccentRange.ORANGE))
    }
}
