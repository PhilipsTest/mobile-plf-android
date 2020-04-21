/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.payment

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.philips.platform.ecs.model.orders.ECSOrderDetail
import com.philips.platform.mec.R
import com.philips.platform.mec.analytics.MECAnalyticPageNames.orderConfirmationPage
import com.philips.platform.mec.analytics.MECAnalytics
import com.philips.platform.mec.databinding.MecPaymentConfirmationBinding
import com.philips.platform.mec.screens.MecBaseFragment
import com.philips.platform.mec.utils.MECConstant
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.pif.DataInterface.USR.UserDetailConstants
import java.util.*

class MECPaymentConfirmationFragment : MecBaseFragment() {
    companion object {
        val TAG = "MECPaymentConfirmationFragment"
    }

    private var paymentStatus: Boolean = false

    private lateinit var binding: MecPaymentConfirmationBinding
    private var mecPaymentConfirmationService = MECPaymentConfirmationService()
    private lateinit var mECSOrderDetail : com.philips.platform.ecs.model.orders.ECSOrderDetail

    override fun getFragmentTag(): String {
        return "MECPaymentConfirmationFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = MecPaymentConfirmationBinding.inflate(inflater, container, false)
        binding.fragment = this
        paymentStatus = arguments!!.getBoolean(MECConstant.PAYMENT_SUCCESS_STATUS, false)
        binding.isPaymentCompleted = paymentStatus
        val arguments = arguments
        if (arguments != null && arguments.containsKey(MECConstant.MEC_ORDER_DETAIL)) {
            binding.tvMecYourOrderNumber.visibility=View.VISIBLE
            binding.tvOrderNumberVal.visibility=View.VISIBLE
            mECSOrderDetail = arguments?.getParcelable<com.philips.platform.ecs.model.orders.ECSOrderDetail>(MECConstant.MEC_ORDER_DETAIL)!!
            binding.orderNumber = mECSOrderDetail.code

        }
        val detailKeys = ArrayList<String>()
        detailKeys.add(UserDetailConstants.EMAIL)
        val userDetails = MECDataHolder.INSTANCE.userDataInterface.getUserDetails(detailKeys)
        val email: String = userDetails.get(UserDetailConstants.EMAIL).toString();


        val emailConfirmation = if (binding.isPaymentCompleted as Boolean) getString(R.string.mec_confirmation_email_msg) else getString(R.string.mec_payment_pending_confirmation)
        val boldCount: Spanned
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            boldCount = Html.fromHtml("$emailConfirmation  <b>$email</b>", Html.FROM_HTML_MODE_LEGACY)
        } else {
            boldCount = Html.fromHtml("$emailConfirmation  <b>$email</b>")
        }

        binding.tvMecConfirmationEmailMsg.text = boldCount
        updateCount(0) // reset cart count to 0 as current shopping cart is deleted now as result of submit order API call

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        context?.let { mecPaymentConfirmationService.getTitle(PaymentStatus.SUCCESS, it) }
        if( binding.isPaymentCompleted as Boolean) {
            setTitleAndBackButtonVisibility(R.string.mec_confirmation, false)
        }else{
            setTitleAndBackButtonVisibility(R.string.mec_payment_is_pending, false)
        }
    }

    override fun onStart() {
        super.onStart()
        MECAnalytics.trackPage(orderConfirmationPage)
        MECAnalytics.tagPurchaseOrder(mECSOrderDetail)
    }

    fun onClickOk(){
        moveToCaller(paymentStatus,MECWebPaymentFragment.TAG)
    }


}