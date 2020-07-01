package com.ccb.demouapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.philips.platform.ccbdemouapp.R
import com.philips.platform.uappframework.launcher.FragmentLauncher
import kotlinx.android.synthetic.main.launch_api_response_fragment.view.*

class CCBLaunchApiResponseFragment : CCBBaseFragment() {

    val fragmentLauncher = FragmentLauncher(activity, R.id.fragment_container, null)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.launch_api_response_fragment, container, false)
        rootView.authenticateUser.setOnClickListener{replaceFragment(CCBFetchAuthDetailsFragment(),true)}

        return rootView
    }

}
