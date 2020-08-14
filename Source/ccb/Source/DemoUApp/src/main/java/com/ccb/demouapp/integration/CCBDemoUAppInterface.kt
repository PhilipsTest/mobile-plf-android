/*
 * Copyright (c) Koninklijke Philips N.V., 2020
 *
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 *
 */

package com.ccb.demouapp.integration

import android.content.Context
import android.content.Intent
import com.ccb.demouapp.CCBDemoUAppActivity
import com.philips.platform.ccb.integration.CCBInterface
import com.philips.platform.uappframework.UappInterface
import com.philips.platform.uappframework.launcher.ActivityLauncher
import com.philips.platform.uappframework.launcher.UiLauncher
import com.philips.platform.uappframework.uappinput.UappDependencies
import com.philips.platform.uappframework.uappinput.UappLaunchInput
import com.philips.platform.uappframework.uappinput.UappSettings

class CCBDemoUAppInterface : UappInterface {

    private lateinit var context: Context

    override fun init(uappDependencies: UappDependencies, uappSettings: UappSettings) {
        context = uappSettings.context

        var ccbInterface = CCBInterface()
        ccbInterface.init(uappDependencies,uappSettings)
    }

    override fun launch(uiLauncher: UiLauncher, uiLaunchInput: UappLaunchInput) {
        val intent = Intent(context, CCBDemoUAppActivity::class.java)
        (uiLauncher as ActivityLauncher).activityContext.startActivity(intent)
    }
}