package com.ccb.demouapp.fragments

import androidx.fragment.app.Fragment
import com.philips.platform.ccb.integration.CCBDeviceCapabilityInterface



open class CCBBaseFragment : Fragment(), CCBDeviceCapabilityInterface {

    protected var isDeviceConnected = false


    fun addFragment(newFragment: CCBBaseFragment,
                    isAddWithBackStack: Boolean) {
        if (null != activity && !activity!!.isFinishing) {
            val transaction = activity!!.supportFragmentManager.beginTransaction()
            val simpleName = newFragment.javaClass.simpleName
            transaction.add(id, newFragment, simpleName)
            if (isAddWithBackStack) {
                transaction.addToBackStack(simpleName)
            }
            transaction.commitAllowingStateLoss()
        }
    }

    fun replaceFragment(newFragment: CCBBaseFragment,
                        isReplaceWithBackStack: Boolean) {
        if (null != activity && !activity!!.isFinishing) {

            val transaction = activity!!.supportFragmentManager.beginTransaction()
            val simpleName = newFragment.javaClass.simpleName

            transaction.replace(id, newFragment, simpleName)
            if (isReplaceWithBackStack) {
                transaction.addToBackStack(simpleName)
            }
            transaction.commitAllowingStateLoss()
        }
    }

    override fun isDeviceConnected(deviceID: String): Boolean {
        return isDeviceConnected;
    }
}
