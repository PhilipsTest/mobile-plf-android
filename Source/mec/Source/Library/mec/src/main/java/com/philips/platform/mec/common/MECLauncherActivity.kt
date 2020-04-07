/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.common

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.philips.platform.mec.R
import com.philips.platform.mec.databinding.MecActivityLauncherBinding
import com.philips.platform.mec.integration.FragmentSelector
import com.philips.platform.mec.integration.MECFlowConfigurator
import com.philips.platform.mec.utils.MECConstant
import com.philips.platform.mec.utils.MECConstant.DEFAULT_THEME
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.pif.DataInterface.MEC.listeners.MECCartUpdateListener
import com.philips.platform.pif.DataInterface.MEC.listeners.MECFetchCartListener
import com.philips.platform.uappframework.listener.ActionBarListener
import com.philips.platform.uappframework.listener.BackEventListener
import com.philips.platform.uid.thememanager.*
import com.philips.platform.uid.utils.UIDActivity
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.mec_action_bar.*
import java.util.*


class MECLauncherActivity : UIDActivity(), View.OnClickListener , ActionBarListener, MECFetchCartListener, MECCartUpdateListener {



     /**
     * For setting the title of action bar and to set back key Enabled/Disabled
     * @param resString The String to be displayed
     * @param enableBackKey To set back key enabled or disabled
     * @since 1.0.0
     */
    override fun updateActionBar(resString: String?, visibility: Boolean) {
        mec_actionBar_headerTitle_lebel.setText(resString)
        if (visibility) {
            mec_header_back_button_framelayout.setVisibility(View.VISIBLE)
            // For arabic, Hebrew and Perssian the back arrow change from left to right
            if (Locale.getDefault().language.contentEquals("ar") || Locale.getDefault().language.contentEquals("fa") || Locale.getDefault().language.contentEquals("he")) {
                mec_header_back_button_framelayout.setRotation(180f)
            }
        } else {
            mec_header_back_button_framelayout.setVisibility(View.GONE)
        }
    }

    /**
     * For setting the title of action bar and to set back key Enabled/Disabled
     * @param resId The resource Id of the String to be displayed
     * @param enableBackKey To set back key enabled or disabled
     * @since 1.0.0
     */
    override fun updateActionBar(resId: Int, visibility: Boolean) {
        updateActionBar(getString(resId),visibility)


    }

     override fun attachBaseContext(newBase: Context) {
         super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
     }

    lateinit var bundle: Bundle

    private var isHybris : Boolean = false

    private var flowConfigurator: MECFlowConfigurator ? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initTheme()
        DataBindingUtil.setContentView<MecActivityLauncherBinding>(this, R.layout.mec_activity_launcher)

        setSupportActionBar(mec_toolbar)
        getSupportActionBar()?.setDisplayShowTitleEnabled(false)

        mec_header_back_button_framelayout.setOnClickListener(this)
        bundle = intent.getExtras()!!

        isHybris = bundle.getBoolean(MECConstant.KEY_IS_HYBRIS)
        flowConfigurator = bundle.getSerializable(MECConstant.KEY_FLOW_CONFIGURATION) as MECFlowConfigurator

        createActionBar()
        launchMECasFragment(isHybris)
    }

     private fun createActionBar() {
         mec_header_back_button_framelayout.setOnClickListener { onBackPressed() }
         val mBackDrawable = VectorDrawableCompat.create(resources, R.drawable.mec_back_arrow, theme)
         mec_iv_header_back_button.background=mBackDrawable
         title = getString(R.string.app_name)
         val mCartIconDrawable = VectorDrawableCompat.create(resources, R.drawable.mec_shopping_cart, theme)
         cart_icon.background = mCartIconDrawable
         cart_icon.setOnClickListener { //todo launch Shopping cart
             }

     }

    override fun onClick(v: View?) {
        onBackPressed()
    }

     override fun onGetCartCount(count: Int) {
         if (count > 0) {
             mec_cart_item_count.text=count.toString()
             mec_cart_item_count.visibility=View.VISIBLE
         } else {
             mec_cart_item_count.visibility=View.GONE

         }
     }






     override fun shouldShowCart(shouldShow: Boolean) {
         if(shouldShow) {
             mec_cart_item_count.visibility=View.VISIBLE
         }else{
             mec_cart_item_count.visibility=View.GONE
         }

     }

     /**
      * Notifies when product count in cart is updated
      * @since 1.0.0
      */
     override fun onUpdateCartCount(count: Int) {

     }

     override fun onFailure(exception: Exception) {

     }

     override fun onBackPressed() {

         val fragmentManager = supportFragmentManager
         val currentFrag = fragmentManager.findFragmentById(R.id.mec_fragment_container)
         var backState = false

         if (currentFrag != null && currentFrag is BackEventListener) {
             backState = (currentFrag as BackEventListener).handleBackEvent()
         }
         if (!backState) {
             super.onBackPressed()
         }
     }

     private fun initTheme() {
         UIDHelper.injectCalligraphyFonts()
         var themeIndex = intent.getIntExtra(MECConstant.MEC_KEY_ACTIVITY_THEME, DEFAULT_THEME)
         if (themeIndex <= 0) {
             themeIndex = DEFAULT_THEME
         }
         theme.applyStyle(themeIndex, true)
         UIDHelper.init(ThemeConfiguration(this, ContentColor.ULTRA_LIGHT, NavigationColor.BRIGHT, AccentRange.ORANGE))
     }


     // Launch inner MEC  Landing fragment

     private fun launchMECasFragment(hybris: Boolean) {

         val bundle = bundle

         val mecLandingFragment = FragmentSelector(). getLandingFragment(hybris, flowConfigurator!!,bundle)

         bundle.putInt("fragment_container",R.id.mec_fragment_container) // frame_layout for fragment
         mecLandingFragment?.arguments = bundle


         MECDataHolder.INSTANCE.setUpdateCartListener(this, this)
         val tag = mecLandingFragment?.javaClass?.name
         val transaction = supportFragmentManager.beginTransaction()
         transaction.replace(R.id.mec_fragment_container, mecLandingFragment!!, tag)
         transaction.commitAllowingStateLoss()
     }

}