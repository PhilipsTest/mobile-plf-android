package com.philips.platform.ccb.integration

import androidx.fragment.app.Fragment
import com.philips.platform.ccb.fragment.CCBFragment
import com.philips.platform.uappframework.UappInterface
import com.philips.platform.uappframework.launcher.FragmentLauncher
import com.philips.platform.uappframework.launcher.UiLauncher
import com.philips.platform.uappframework.uappinput.UappDependencies
import com.philips.platform.uappframework.uappinput.UappLaunchInput
import com.philips.platform.uappframework.uappinput.UappSettings

class CCBInterface: UappInterface {

    override fun init(uappDependencies: UappDependencies, uappSettings: UappSettings) {
        //init called
    }

    override fun launch(uiLauncher: UiLauncher, uappLaunchInput: UappLaunchInput) {
        if (uiLauncher is FragmentLauncher) {
            val ccbFragment = CCBFragment();
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
}