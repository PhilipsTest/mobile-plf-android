/*
 * Copyright (c) Koninklijke Philips N.V., 2020
 *
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 *
 */

package com.ccb.demouapp.fragments


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ccb.demouapp.util.CCBUtility
import com.philips.platform.ccb.integration.CCBInterface
import com.philips.platform.ccb.integration.CCBLaunchInput
import com.philips.platform.ccbdemouapp.R
import com.philips.platform.uappframework.launcher.FragmentLauncher
import com.philips.platform.uappframework.listener.ActionBarListener
import com.philips.platform.uappframework.listener.BackEventListener
import kotlinx.android.synthetic.main.fragment_home.view.*

/**
 * A simple [Fragment] subclass.
 */
class CCBDemoUAppHomeFragment : CCBBaseFragment(), BackEventListener, ActionBarListener{

    private val LOCATION_REQ_CODE = 101
    private lateinit var mBackImage: ImageView
    private lateinit var text: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        mBackImage = activity!!.findViewById(R.id.ccb_demo_app_iv_header_back_button)
        text = activity!!.findViewById(R.id.ccb_demo_app_header_title)
        rootView.launchChatbotUiButton.setOnClickListener {
            if (hasLocationPermission())
                launchChatBot()
            else {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQ_CODE)
            }
        }
        rootView.launchDirectLineApiButton.setOnClickListener { launchDirectlineApi() }

        rootView.switchConnectDevice.setOnCheckedChangeListener { buttonView, isChecked ->
            isDeviceConnected = isChecked
        }
        return rootView
    }

    private fun launchChatBot() {
        if (!CCBUtility.isNetworkAvailable(context)) {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
            return
        }

        val ccbInterface = CCBInterface()
        val fragmentLauncher = FragmentLauncher(activity, R.id.fragment_container, this)
        val ccbLaunchInput = CCBLaunchInput()
        ccbLaunchInput.ccbDeviceCapabilityInterface = this
        ccbInterface.launch(fragmentLauncher, ccbLaunchInput)
    }

    private fun launchDirectlineApi() {
        replaceFragment(CCBLaunchApiResponseFragment(), true)
    }

    private fun hasLocationPermission(): Boolean {
        if (context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) } ===
                PackageManager.PERMISSION_GRANTED)
            return true
        return false
    }

    override fun handleBackEvent(): Boolean {
        return false
    }

    fun setTextView(tv: TextView) {
        text = tv
    }

    override fun updateActionBar(resId: Int, enableBackKey: Boolean) {
        if (context != null) {
            updateActionBar(getString(resId), enableBackKey)
        }
    }

    override fun updateActionBar(resString: String?, enableBackKey: Boolean) {
        text.text = resString
        if (enableBackKey) {
            mBackImage.setVisibility(View.VISIBLE)
        } else {
            mBackImage.setVisibility(View.GONE)
        }
    }
}
