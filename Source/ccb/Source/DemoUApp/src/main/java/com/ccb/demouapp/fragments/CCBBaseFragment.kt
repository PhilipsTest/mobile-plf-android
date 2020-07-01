package com.ccb.demouapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.philips.platform.ccbdemouapp.R
import com.philips.platform.uappframework.launcher.FragmentLauncher


open class CCBBaseFragment : Fragment() {

    fun addFragment(newFragment: CCBBaseFragment,
                    isAddWithBackStack: Boolean) {
            if (null!=activity && !activity!!.isFinishing) {
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
            if (null!=activity && !activity!!.isFinishing) {

                val transaction = activity!!.supportFragmentManager.beginTransaction()
                val simpleName = newFragment.javaClass.simpleName



                transaction.replace(id, newFragment, simpleName)
                if (isReplaceWithBackStack) {
                    transaction.addToBackStack(simpleName)
                }
                transaction.commitAllowingStateLoss()
            }
    }
}
