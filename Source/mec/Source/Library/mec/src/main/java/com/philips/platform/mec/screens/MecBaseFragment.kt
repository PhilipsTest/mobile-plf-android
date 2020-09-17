/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens

import android.content.Context
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.philips.platform.mec.R
import com.philips.platform.mec.common.MECLauncherActivity
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.integration.serviceDiscovery.MECManager
import com.philips.platform.mec.screens.catalog.MECProductCatalogCategorizedFragment
import com.philips.platform.mec.screens.catalog.MECProductCatalogFragment
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.mec.utils.MECutility
import com.philips.platform.uappframework.listener.BackEventListener
import com.philips.platform.uid.view.widget.ProgressBarWithLabel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


abstract class MecBaseFragment : Fragment(), BackEventListener, Observer<MecError> {
    private var mContext: Context? = null


    internal var mTitle = ""

    protected val SMALL = 0
    protected val MEDIUM = 1
    protected val BIG = 2

    override fun handleBackEvent(): Boolean {
        return false
    }

    fun popSelfAndReplaceFragment(newFragment: MecBaseFragment,
                                  newFragmentTag: String, isReplaceWithBackStack: Boolean){
        fragmentManager?.popBackStack()
        replaceFragment(newFragment,newFragmentTag,isReplaceWithBackStack)
    }

    fun replaceFragment(newFragment: MecBaseFragment,
                        newFragmentTag: String, isReplaceWithBackStack: Boolean) {
        if (MECDataHolder.INSTANCE.actionbarUpdateListener == null || MECDataHolder.INSTANCE.mecCartUpdateListener == null)
            RuntimeException("ActionBarListner and MECListner cant be null")
        else {
            if (activity?.isFinishing == false) {

                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.replace(id, newFragment, newFragmentTag)
                if (isReplaceWithBackStack) {
                    transaction?.addToBackStack(newFragmentTag)
                }
                transaction?.commitAllowingStateLoss()
            }
        }
    }


    fun addFragment(newFragment: MecBaseFragment,
                    newFragmentTag: String, isAddWithBackStack: Boolean) {
        if (MECDataHolder.INSTANCE.actionbarUpdateListener == null || MECDataHolder.INSTANCE.mecCartUpdateListener == null)
            RuntimeException("ActionBarListner and MECListner cant be null")
        else {
            if (activity?.isFinishing == false) {
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.add(id, newFragment, newFragmentTag)
                if (isAddWithBackStack) {
                    transaction?.addToBackStack(newFragmentTag)
                }
                transaction?.commitAllowingStateLoss()
            }
        }
    }

    fun showProductCatalogFragment(fragmentTag: String) {
        val fragment = activity?.supportFragmentManager?.findFragmentByTag(MECProductCatalogFragment.TAG)
        if (fragment == null) {
            val catalogCategorizedFragment = activity?.supportFragmentManager?.findFragmentByTag(MECProductCatalogCategorizedFragment.TAG)
            if (catalogCategorizedFragment == null) {
                activity?.supportFragmentManager?.popBackStack(MECDataHolder.INSTANCE.mecLaunchingFragmentName, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                replaceFragment(MECProductCatalogFragment(), MECProductCatalogFragment.TAG, true)
            } else {
                activity?.supportFragmentManager?.popBackStack(MECProductCatalogCategorizedFragment.TAG, 0)
            }
        } else {
            activity?.supportFragmentManager?.popBackStack(MECProductCatalogFragment.TAG, 0)
        }
    }


    protected fun setTitleAndBackButtonVisibility(resourceId: Int, isVisible: Boolean) {
        MECDataHolder.INSTANCE.actionbarUpdateListener?.updateActionBar(resourceId, isVisible)
    }


    protected fun setTitleAndBackButtonVisibility(title: String, isVisible: Boolean) {
        MECDataHolder.INSTANCE.actionbarUpdateListener?.updateActionBar(title, isVisible)
    }

    fun updateCount(count: Int) {
        MECDataHolder.INSTANCE.mecCartUpdateListener?.onUpdateCartCount(count)
    }

    fun setCartIconVisibility(shouldShow: Boolean) {
        if (isUserLoggedIn() && MECDataHolder.INSTANCE.hybrisEnabled) {
            MECDataHolder.INSTANCE.mecCartUpdateListener?.shouldShowCart(shouldShow)
        } else {
            MECDataHolder.INSTANCE.mecCartUpdateListener?.shouldShowCart(false)
        }
    }

    internal fun fetchCartQuantity() {
        if (isUserLoggedIn() && MECDataHolder.INSTANCE.hybrisEnabled) {
            GlobalScope.launch {
                MECDataHolder.INSTANCE.mecCartUpdateListener?.let { MECManager().getShoppingCartData(it) }
            }
        }
    }

    protected fun isUserLoggedIn(): Boolean {
        return MECDataHolder.INSTANCE.isUserLoggedIn()
    }

    fun showProgressBar(mecProgressBar: FrameLayout?) {
        mecProgressBar?.visibility = View.VISIBLE
        activity?.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

    }

    fun showProgressBarWithText(mecProgressBar: FrameLayout?, text: String) {
        mecProgressBar?.visibility = View.VISIBLE
        val mecProgressBarText = mecProgressBar?.findViewById(R.id.mec_progress_bar_text) as ProgressBarWithLabel?
        mecProgressBarText?.setText(text)
        activity?.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

    }

    fun dismissProgressBar(mecProgressBar: FrameLayout?) {
        mecProgressBar?.visibility = View.GONE
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

    }

    private fun finishActivity() {
        if (activity is MECLauncherActivity && activity?.isFinishing == false) activity?.finish()
    }

    override fun onChanged(mecError: MecError?) {
        processError(mecError, true)
    }

    open fun processError(mecError: MecError?, showDialog: Boolean) {
        context?.let { MECutility.tagAndShowError(mecError, showDialog, activity?.supportFragmentManager, it) }
    }

    abstract fun getFragmentTag(): String


    fun moveToCaller(isSuccess: Boolean, fragmentTag: String) {
        val mecOrderFlowCompletion = MECDataHolder.INSTANCE.mecOrderFlowCompletion

        if (isSuccess) {
            MECDataHolder.INSTANCE.mecOrderFlowCompletion?.onOrderPlace()
        } else {
            MECDataHolder.INSTANCE.mecOrderFlowCompletion?.onOrderCancel()
        }

        val shouldMoveToProductList = mecOrderFlowCompletion?.shouldMoveToProductList() ?: true

        if (shouldMoveToProductList) {
            showProductCatalogFragment(fragmentTag)
        } else {
            exitMEC()
        }

    }

    private fun removeMECFragments() {

        val fragManager = activity?.supportFragmentManager
        var count = fragManager?.backStackEntryCount ?: 0
        while (count >= 0) {
            val fragmentList = fragManager?.fragments
            if (fragmentList?.size ?: 0 > 0) {

                val fragment = fragmentList?.get(0)
                if (fragment is MecBaseFragment) {
                    fragManager.popBackStack()
                }
            }
            count--
        }
    }

    fun exitMEC() {
        removeMECFragments()
        finishActivity()
    }
}
