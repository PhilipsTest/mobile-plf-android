package com.philips.cdp.di.mec.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.graphics.drawable.VectorDrawableCompat
import android.view.View

import com.philips.cdp.di.mec.R
import com.philips.cdp.di.mec.databinding.MecActivityLauncherBinding

import com.philips.cdp.di.mec.integration.MECListener
import com.philips.cdp.di.mec.utils.MECConstant
import com.philips.platform.uappframework.listener.ActionBarListener
import com.philips.platform.uid.utils.UIDActivity
import kotlinx.android.synthetic.main.mec_action_bar.*
import java.util.*


 class MECLauncherActivity : UIDActivity(), View.OnClickListener , ActionBarListener, MECListener {


     /**
     * For setting the title of action bar and to set back key Enabled/Disabled
     * @param resString The String to be displayed
     * @param enableBackKey To set back key enabled or disabled
     * @since 1.0.0
     */
    override fun updateActionBar(resString: String?, visibility: Boolean) {
        mec_actionBar_headerTitle_lebel.setText(resString)
        if (visibility) {
            mec_header_back_button.setVisibility(View.VISIBLE)
            // For arabic, Hebrew and Perssian the back arrow change from left to right
            if (Locale.getDefault().language.contentEquals("ar") || Locale.getDefault().language.contentEquals("fa") || Locale.getDefault().language.contentEquals("he")) {
                mec_header_back_button.setRotation(180f)
            }
        } else {
            mec_header_back_button.setVisibility(View.GONE)
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

    lateinit var bundle: Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DataBindingUtil.setContentView<MecActivityLauncherBinding>(this, R.layout.mec_activity_launcher)

        setSupportActionBar(mec_toolbar)
        getSupportActionBar()?.setDisplayShowTitleEnabled(false)

        mec_header_back_button.setOnClickListener(this)
        bundle = intent.getExtras()
        val landingScreen:Int = bundle.getInt(MECConstant.MEC_LANDING_SCREEN)


        createActionBar();
        loadDecisionFragment();
    }

     private fun createActionBar() {
         mec_header_back_button.setOnClickListener { onBackPressed() }
         val mBackDrawable = VectorDrawableCompat.create(resources, R.drawable.mec_back_arrow, theme)
         mec_iv_header_back_button.background=mBackDrawable
         title = getString(R.string.mec_app_name)
         val mCartIconDrawable = VectorDrawableCompat.create(resources, R.drawable.mec_shopping_cart, theme)
         cart_icon.background = mCartIconDrawable
         cart_icon.setOnClickListener { //todo launch Shopping cart
             }

     }

    override fun onClick(v: View?) {
        onBackPressed()
    }

    private fun loadDecisionFragment( ){
            val mECFragmentLauncher = MECFragmentLauncher()
            mECFragmentLauncher.setActionBarListener(this, this)
             mECFragmentLauncher.arguments = bundle
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(com.philips.cdp.di.mec.R.id.mec_fragment_container, mECFragmentLauncher, mECFragmentLauncher.TAG)
            transaction.addToBackStack(mECFragmentLauncher.TAG)
            transaction.commitAllowingStateLoss()

    }

     override fun onGetCartCount(count: Int) {
         if (count > 0) {
             mec_cart_item_count.text=count.toString()
             mec_cart_item_count.visibility=View.VISIBLE
         } else {
             mec_cart_item_count.visibility=View.GONE

         }
     }

     override fun onUpdateCartCount() {
         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
     }

     override fun updateCartIconVisibility(shouldShow: Boolean) {
         if(shouldShow) {
             mec_cart_item_count.visibility=View.VISIBLE
         }else{
             mec_cart_item_count.visibility=View.GONE
         }

     }

     override fun onGetCompleteProductList(productList: ArrayList<String>?) {
         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
     }

     override fun onSuccess() {
         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
     }

     override fun onSuccess(bool: Boolean) {
         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
     }

     override fun onFailure(errorCode: Int) {
         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
     }

}