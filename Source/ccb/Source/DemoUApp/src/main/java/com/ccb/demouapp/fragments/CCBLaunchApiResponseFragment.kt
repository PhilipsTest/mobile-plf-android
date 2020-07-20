package com.ccb.demouapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ccb.demouapp.util.CCBUtility
import com.philips.platform.ccbdemouapp.R
import com.philips.platform.uappframework.launcher.FragmentLauncher
import kotlinx.android.synthetic.main.launch_api_response_fragment.view.*

class CCBLaunchApiResponseFragment : CCBBaseFragment() {

    val fragmentLauncher = FragmentLauncher(activity, R.id.fragment_container, null)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.launch_api_response_fragment, container, false)
        //rootView.authenticateUser.setOnClickListener{if (isNetworkAvailable()) replaceFragment(CCBFetchAuthDetailsFragment(),true) else Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show() }
        rootView.startConversation.setOnClickListener{if (CCBUtility.isNetworkAvailable(context)) replaceFragment(CCBFetchConversationDetailsFragment(),true) else Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show() }
        rootView.refreshSession.setOnClickListener{if (CCBUtility.isNetworkAvailable(context)) replaceFragment(CCBRefreshTokenFragment(),true) else Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show() }
        rootView.postMessage.setOnClickListener{if (CCBUtility.isNetworkAvailable(context)) replaceFragment(CCBPostMessageFragment(),true) else Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show() }
        rootView.getAllMessages.setOnClickListener{if (CCBUtility.isNetworkAvailable(context)) replaceFragment(CCBGetMessagesFragment(),true) else Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show() }
        rootView.endConversation.setOnClickListener{if (CCBUtility.isNetworkAvailable(context)) replaceFragment(CCBEndConversationFragment(),true) else Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show() }
        return rootView
    }
    
}
