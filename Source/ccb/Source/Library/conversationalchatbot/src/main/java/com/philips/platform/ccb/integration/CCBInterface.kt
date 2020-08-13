package com.philips.platform.ccb.integration

import android.content.Context
import androidx.fragment.app.Fragment
import com.philips.platform.ccb.directline.CCBAzureSessionHandler
import com.philips.platform.ccb.directline.CCBSessionHandlerInterface
import com.philips.platform.ccb.fragment.CCBConversationalFragment
import com.philips.platform.ccb.fragment.CCBFragment
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
    }

    override fun launch(uiLauncher: UiLauncher, uappLaunchInput: UappLaunchInput) {
        if (uiLauncher is FragmentLauncher) {
            val ccbFragment = CCBConversationalFragment()
            addFragment(uiLauncher, ccbFragment)
        }
    }

    private fun addFragment(uiLauncher: FragmentLauncher, fragment: Fragment) {
        uiLauncher.fragmentActivity.supportFragmentManager
                .beginTransaction()
                .replace(uiLauncher.parentContainerResourceID, fragment, fragment.tag)
                .addToBackStack(fragment.javaClass.simpleName)
                .commit()
    }

    fun getccbSessionHandlerInterface(): CCBSessionHandlerInterface {
        val ccbAzureSessionHandler = CCBAzureSessionHandler()
        return ccbAzureSessionHandler
    }
}