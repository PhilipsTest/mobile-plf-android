/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.payment

import android.content.Context
import android.os.Bundle
import android.webkit.CookieManager
import com.philips.platform.mec.R
import com.philips.platform.mec.utils.AlertListener
import com.philips.platform.mec.utils.MECConstant
import com.philips.platform.mec.utils.MECLog
import com.philips.platform.mec.utils.MECutility

class MECWebPaymentFragment : MECWebFragment() , AlertListener {



    override fun getFragmentTag(): String {
        return "MECWebPaymentFragment"
    }

    companion object {
        val TAG:String="MECWebPaymentFragment"
    }

    private var mContext: Context? = null
    private var mIsPaymentFailed: Boolean = false
    private lateinit var orderNumber:String

    private val SUCCESS_KEY = "successURL"
    private val PENDING_KEY = "pendingURL"
    private val FAILURE_KEY = "failureURL"
    private val CANCEL_KEY = "cancelURL"

    private val PAYMENT_SUCCESS_CALLBACK_URL = "http://www.philips.com/paymentSuccess"
    private val PAYMENT_PENDING_CALLBACK_URL = "http://www.philips.com/paymentPending"
    private val PAYMENT_FAILURE_CALLBACK_URL = "http://www.philips.com/paymentFailure"
    private val PAYMENT_CANCEL_CALLBACK_URL = "http://www.philips.com/paymentCancel"


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onResume() {
        super.onResume()
        setTitleAndBackButtonVisibility(R.string.mec_payment, false)
        setCartIconVisibility(false)
    }




    override fun getWebUrl(): String {
        val arguments = arguments
        orderNumber= arguments?.getString(MECConstant.ORDER_NUMBER)!!
        if (arguments == null || !arguments.containsKey(MECConstant.WEB_PAY_URL)) {
            MECLog.v(TAG, "payment URL must be provided")

        }
        val builder = StringBuilder()
        builder.append(arguments!!.getString(MECConstant.WEB_PAY_URL))
        builder.append("&$SUCCESS_KEY=$PAYMENT_SUCCESS_CALLBACK_URL")
        builder.append("&$PENDING_KEY=$PAYMENT_PENDING_CALLBACK_URL")
        builder.append("&$FAILURE_KEY=$PAYMENT_FAILURE_CALLBACK_URL")
        builder.append("&$CANCEL_KEY=$PAYMENT_CANCEL_CALLBACK_URL")
        return builder.toString()
    }

    private fun createSuccessBundle(paymentCompleted: Boolean): Bundle {
        val bundle = Bundle()
        bundle.putString(MECConstant.ORDER_NUMBER,orderNumber )
        bundle.putBoolean(MECConstant.PAYMENT_SUCCESS_STATUS, paymentCompleted)
        return bundle
    }

    private fun createErrorBundle(bundle: Bundle): Bundle {
        bundle.putBoolean(MECConstant.PAYMENT_SUCCESS_STATUS, false)
        return bundle
    }

    private fun launchConfirmationScreen(bundle: Bundle) {
       val  mECPaymentConfirmationFragment: MECPaymentConfirmationFragment = MECPaymentConfirmationFragment()
        mECPaymentConfirmationFragment.arguments=bundle
        addFragment(mECPaymentConfirmationFragment,MECPaymentConfirmationFragment.TAG,true)
    }

    override fun shouldOverrideUrlLoading(url: String): Boolean {
        return verifyResultCallBacks(url)
    }

    private fun verifyResultCallBacks(url: String): Boolean {
        updateCount(0) // reset cart count to 0 as current shopping cart is deleted now as result of submit order API call
        var match = true
        if (url.startsWith(PAYMENT_SUCCESS_CALLBACK_URL)) {
            MECLog.v("PAY_SUCCESS", url)
            launchConfirmationScreen(createSuccessBundle(true))
        } else if (url.startsWith(PAYMENT_PENDING_CALLBACK_URL)) {
            MECLog.v("PAY_PEND", url)
            val bundle = Bundle()
            launchConfirmationScreen(createSuccessBundle(false))
        } else if (url.startsWith(PAYMENT_FAILURE_CALLBACK_URL)) {
            // todo  handle failure
            MECLog.v("PAY_FAIL", url)
            mIsPaymentFailed = true

            MECutility.showActionDialog(mContext!!, mContext!!.getString(R.string.mec_ok), null, mContext!!.getString(R.string.mec_payment), mContext!!.getString(R.string.mec_payment_failed_message), fragmentManager!!, object:AlertListener{
                override fun onPositiveBtnClick() {
                    moveToCaller(mIsPaymentFailed,TAG)
                }
            })
        } else if (url.startsWith(PAYMENT_CANCEL_CALLBACK_URL)) {
            MECLog.v("PAY_CANC", url)
            moveToCaller(mIsPaymentFailed,TAG)
        } else {
            match = false
        }
        if (match) {
            clearCookies()
        }
        return match
    }


    override fun handleBackEvent(): Boolean {
        mIsPaymentFailed = false
        MECutility.showActionDialog(mContext!!, mContext!!.getString(R.string.mec_ok), mContext!!.getString(R.string.mec_cancel), mContext!!.getString(R.string.mec_payment), mContext!!.getString(R.string.mec_cancel_payment), fragmentManager!!, this)
        return true
    }

    private fun handleNavigation() {
        moveToCaller(false,TAG)
    }

    override fun onPositiveBtnClick() {
        clearCookies()
        handleNavigation()
    }

    override fun onNegativeBtnClick() {
        if (mIsPaymentFailed) {
            handleNavigation()
        }
    }

    private fun clearCookies() {
        CookieManager.getInstance().removeSessionCookies(null)
    }

}