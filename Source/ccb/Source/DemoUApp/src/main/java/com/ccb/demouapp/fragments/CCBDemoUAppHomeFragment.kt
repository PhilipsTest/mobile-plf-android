package com.ccb.demouapp.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.philips.platform.ccb.integration.CCBInterface
import com.philips.platform.ccb.integration.CCBLaunchInput

import com.philips.platform.ccbdemouapp.R
import com.philips.platform.uappframework.launcher.FragmentLauncher
import com.philips.platform.uappframework.uappinput.UappLaunchInput
import kotlinx.android.synthetic.main.fragment_home.view.*

/**
 * A simple [Fragment] subclass.
 */
class CCBDemoUAppHomeFragment : CCBBaseFragment() {



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        rootView.launchChatbotUiButton.setOnClickListener { launchChatBot() }
        rootView.launchDirectLineApiButton.setOnClickListener { launchDirectlineApi() }

        return rootView
    }

    private fun launchChatBot(){
        val ccbInterface =  CCBInterface()
        val fragmentLauncher = FragmentLauncher(activity, R.id.fragment_container, null)
        val ccbLaunchInput = CCBLaunchInput()
        ccbLaunchInput.fetchAppDataHandler = this
        ccbInterface.launch(fragmentLauncher, ccbLaunchInput)
    }

    private fun launchDirectlineApi(){
        replaceFragment(CCBLaunchApiResponseFragment(),true)
    }


}
