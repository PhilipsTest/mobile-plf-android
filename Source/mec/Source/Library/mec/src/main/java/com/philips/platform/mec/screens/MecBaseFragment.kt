/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens

import android.app.ProgressDialog
import android.content.Context
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.philips.platform.mec.R
import com.philips.platform.mec.common.MECLauncherActivity
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.screens.catalog.MECProductCatalogCategorizedFragment
import com.philips.platform.mec.screens.catalog.MECProductCatalogFragment
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.mec.utils.MECutility
import com.philips.platform.uappframework.listener.BackEventListener
import com.philips.platform.uid.view.widget.ProgressBar
import com.philips.platform.uid.view.widget.ProgressBarWithLabel


abstract class MecBaseFragment : Fragment(), BackEventListener, Observer<MecError> {
    private var mContext: Context? = null


    internal var mTitle = ""

    protected val SMALL = 0
    protected val MEDIUM = 1
    protected val BIG = 2
    private var mProgressDialog: ProgressDialog? = null
    private var mMECBaseFragmentProgressBar: ProgressBar? = null

    enum class AnimationType {
        NONE
    }

    override fun handleBackEvent(): Boolean {
        return false
    }

    fun replaceFragment(newFragment: MecBaseFragment,
                        newFragmentTag: String, isReplaceWithBackStack: Boolean) {
        if (MECDataHolder.INSTANCE.actionbarUpdateListener == null || MECDataHolder.INSTANCE.mecCartUpdateListener == null)
            RuntimeException("ActionBarListner and MECListner cant be null")
        else {
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


    fun addFragment(newFragment: MecBaseFragment,
                        newFragmentTag: String, isAddWithBackStack: Boolean) {
        if (MECDataHolder.INSTANCE.actionbarUpdateListener == null || MECDataHolder.INSTANCE.mecCartUpdateListener == null)
            RuntimeException("ActionBarListner and MECListner cant be null")
        else {
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
    }

    fun showProductCatalogFragment(fragmentTag: String) {
        val fragment = fragmentManager!!.findFragmentByTag(MECProductCatalogFragment.TAG)
        if (fragment == null) {
            val fragment = fragmentManager!!.findFragmentByTag(MECProductCatalogCategorizedFragment.TAG)
            if (fragment == null) {
                fragmentManager!!.popBackStack(fragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                replaceFragment(MECProductCatalogFragment(),  MECProductCatalogFragment.TAG, true)
            }else{
                fragmentManager!!.popBackStack(MECProductCatalogCategorizedFragment.TAG, 0)
            }
        } else {
            fragmentManager!!.popBackStack(MECProductCatalogFragment.TAG, 0)
        }
    }


    protected fun setTitleAndBackButtonVisibility(resourceId: Int, isVisible: Boolean) {
        if (MECDataHolder.INSTANCE.actionbarUpdateListener != null)
            MECDataHolder.INSTANCE.actionbarUpdateListener!!.updateActionBar(resourceId, isVisible)
    }


    protected fun setTitleAndBackButtonVisibility(title: String, isVisible: Boolean) {
        if (MECDataHolder.INSTANCE.actionbarUpdateListener != null)
            MECDataHolder.INSTANCE.actionbarUpdateListener!!.updateActionBar(title, isVisible)
    }

    fun updateCount(count: Int) {
        if (MECDataHolder.INSTANCE.mecCartUpdateListener  != null) {
            MECDataHolder.INSTANCE.mecCartUpdateListener!!.onUpdateCartCount(count)
        }
    }

    fun setCartIconVisibility(shouldShow: Boolean) {
        if (MECDataHolder.INSTANCE.mecCartUpdateListener != null) {
            if (isUserLoggedIn() && MECDataHolder.INSTANCE.hybrisEnabled) {
                MECDataHolder.INSTANCE.mecCartUpdateListener!!.shouldShowCart(shouldShow)
            } else {
                MECDataHolder.INSTANCE.mecCartUpdateListener!!.shouldShowCart(false)
            }
        }
    }

    protected fun isUserLoggedIn(): Boolean {
        return MECDataHolder.INSTANCE.isUserLoggedIn()

    }

    fun hideProgressBar() {
        if (mMECBaseFragmentProgressBar != null) {
            mMECBaseFragmentProgressBar!!.setVisibility(View.GONE)
            if (activity != null) {
                activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }

    fun showProgressBar(mecProgressBar: FrameLayout?) {
        mecProgressBar?.visibility = View.VISIBLE
        if (activity != null) {
            activity?.getWindow()?.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    fun showProgressBarWithText(mecProgressBar: FrameLayout?, text: String){
        mecProgressBar?.visibility = View.VISIBLE
        val mecProgressBarText = mecProgressBar?.findViewById(R.id.mec_progress_bar_text) as ProgressBarWithLabel
        mecProgressBarText?.setText(text)
        if (activity != null) {
            activity?.getWindow()?.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    fun dismissProgressBar(mecProgressBar: FrameLayout?){
        mecProgressBar?.visibility = View.GONE
        if (activity != null) {
            activity?.getWindow()?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    private fun finishActivity() {
        if (activity != null && activity is MECLauncherActivity && !activity!!.isFinishing) {
            activity!!.finish()
        }
    }

    override fun onChanged(mecError: MecError?) {
        hideProgressBar()
        processError(mecError,true)
    }

    open fun processError(mecError: MecError?, showDialog: Boolean) {
        MECutility.tagAndShowError(mecError,showDialog,fragmentManager,context)
    }

    abstract fun getFragmentTag():String

    fun <T> LiveData<T>.reObserve(owner: LifecycleOwner, observer: Observer<T>) {
        removeObserver(observer)
        observe(owner, observer)
    }


    fun moveToCaller(isSuccess: Boolean, fragmentTag: String) {
        val mecOrderFlowCompletion = MECDataHolder.INSTANCE.mecOrderFlowCompletion

        if (isSuccess) {
            MECDataHolder.INSTANCE.mecOrderFlowCompletion?.onOrderPlace()
        } else {
            MECDataHolder.INSTANCE.mecOrderFlowCompletion?.onOrderCancel()
        }

        val shouldMoveToProductList = mecOrderFlowCompletion?.shouldMoveToProductList() ?: true

        if(shouldMoveToProductList){
            showProductCatalogFragment(fragmentTag)
        }else{
            exitMEC()
        }

    }



    private fun removeMECFragments() {

            if(activity!=null) {
                val fragManager = activity!!.supportFragmentManager
                var count = fragManager.backStackEntryCount
                while (count >= 0) {
                    val fragmentList = fragManager.fragments
                    if (fragmentList != null && fragmentList.size > 0) {

                        val fragment = fragmentList[0]
                        if(fragment is MecBaseFragment) {
                            fragManager.popBackStack()
                        }
                    }
                    count--
                }
            }
    }

    private fun exitMEC() {
        removeMECFragments()
        finishActivity()
    }
}
