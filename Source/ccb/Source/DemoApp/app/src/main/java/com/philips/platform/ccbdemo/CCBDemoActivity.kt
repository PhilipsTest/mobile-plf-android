/*
 * Copyright (c) Koninklijke Philips N.V., 2020
 *
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 *
 */

package com.philips.platform.ccbdemo

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.app.AppCompatActivity
import com.ccb.demouapp.integration.CCBDemoUAppDependencies
import com.ccb.demouapp.integration.CCBDemoUAppInterface
import com.ccb.demouapp.integration.CCBDemoUAppSettings
import com.philips.platform.appinfra.AppInfraInterface
import com.philips.platform.uappframework.launcher.ActivityLauncher
import com.philips.platform.uappframework.uappinput.UappLaunchInput
import kotlinx.android.synthetic.main.activity_ccbdemo.*

class CCBDemoActivity : AppCompatActivity() {

    private lateinit var appInfraInterface: AppInfraInterface
    private lateinit var demoAppInterface: CCBDemoUAppInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ccbdemo)

        val ccbDemoApplication = applicationContext as CCBDemoApplication;
        appInfraInterface = ccbDemoApplication.getAppInfra();

        launchButton.setOnClickListener { launchDemoUApp() }

        initDemoUApp()
    }

    private fun initDemoUApp() {
        val demoUAppDependencies = CCBDemoUAppDependencies(appInfraInterface)
        val demoUAppSettings = CCBDemoUAppSettings(applicationContext)
        demoAppInterface = CCBDemoUAppInterface()
        demoAppInterface.init(demoUAppDependencies, demoUAppSettings)
    }

    private fun launchDemoUApp() {
        demoAppInterface.launch(ActivityLauncher(this, ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_UNSPECIFIED, null, 0, null), UappLaunchInput())
    }

}
