/*
 * Copyright (c) Koninklijke Philips N.V., 2020
 *
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 *
 */

package com.philips.platform.ccb.integration

import android.content.Context
import androidx.fragment.app.Fragment
import com.philips.platform.ccb.analytics.CCBAnalytics
import com.philips.platform.ccb.directline.CCBAzureSessionHandler
import com.philips.platform.ccb.directline.CCBSessionHandlerInterface
import com.philips.platform.ccb.fragment.CCBConversationalFragment
import com.philips.platform.ccb.manager.CCBSettingsManager
import com.philips.platform.uappframework.UappInterface
import com.philips.platform.uappframework.launcher.FragmentLauncher
import com.philips.platform.uappframework.launcher.UiLauncher
import com.philips.platform.uappframework.uappinput.UappDependencies
import com.philips.platform.uappframework.uappinput.UappLaunchInput
import com.philips.platform.uappframework.uappinput.UappSettings

class CCBInterface: UappInterface {

    private lateinit var context: Context

    override fun init(uappDependencies: UappDependencies, uappSettings: UappSettings) {
       context = uappSettings.context.applicationContext
        CCBSettingsManager.init(uappDependencies)
        CCBAnalytics.initCCBAnalytics(((uappDependencies)))
    }

    override fun launch(uiLauncher: UiLauncher, uappLaunchInput: UappLaunchInput) {
        if (uiLauncher is FragmentLauncher) {
            CCBSettingsManager.ccbDeviceCapabilityInterface = (uappLaunchInput as CCBLaunchInput).ccbDeviceCapabilityInterface
            if(uiLauncher.actionbarListener!=null){
                CCBSettingsManager.actionbarUpdateListener(uiLauncher.actionbarListener)
            }
            val ccbFragment = CCBConversationalFragment()
            addFragment(uiLauncher, ccbFragment)
        }
    }

    private fun addFragment(uiLauncher: FragmentLauncher, fragment: Fragment) {
        if (CCBSettingsManager.actionbarUpdateListener == null)
            RuntimeException("ActionBarListener cannot be null")
        else {
            uiLauncher.fragmentActivity.supportFragmentManager
                    .beginTransaction()
                    .replace(uiLauncher.parentContainerResourceID, fragment, fragment.tag)
                    .addToBackStack(fragment.javaClass.simpleName)
                    .commit()
        }
    }

    fun getccbSessionHandlerInterface(): CCBSessionHandlerInterface {
        val ccbAzureSessionHandler = CCBAzureSessionHandler()
        return ccbAzureSessionHandler
    }
}