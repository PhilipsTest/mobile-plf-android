package com.ccb.demouapp.fragments


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.ccb.demouapp.util.CCBUtility
import com.philips.platform.ccb.integration.CCBInterface
import com.philips.platform.ccb.integration.CCBLaunchInput

import com.philips.platform.ccbdemouapp.R
import com.philips.platform.uappframework.launcher.FragmentLauncher
import kotlinx.android.synthetic.main.fragment_home.view.*

/**
 * A simple [Fragment] subclass.
 */
class CCBDemoUAppHomeFragment : CCBBaseFragment() {

    private val LOCATION_REQ_CODE = 101

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

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
        val fragmentLauncher = FragmentLauncher(activity, R.id.fragment_container, null)
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
}
