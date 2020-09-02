/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.orderSummary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.philips.platform.ecs.microService.model.cart.ECSShoppingCart
import com.philips.platform.ecs.model.orders.ECSOrderDetail
import com.philips.platform.ecs.model.payment.ECSPayment
import com.philips.platform.mec.R
import com.philips.platform.mec.analytics.MECAnalyticPageNames.cvvPage
import com.philips.platform.mec.analytics.MECAnalytics
import com.philips.platform.mec.analytics.MECAnalyticsConstant.old
import com.philips.platform.mec.analytics.MECAnalyticsConstant.paymentFailure
import com.philips.platform.mec.analytics.MECAnalyticsConstant.paymentType
import com.philips.platform.mec.analytics.MECAnalyticsConstant.specialEvents
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.databinding.MecCvcCodeFragmentBinding
import com.philips.platform.mec.screens.payment.PaymentViewModel
import com.philips.platform.mec.screens.MecBaseFragment
import com.philips.platform.mec.screens.payment.MECPaymentConfirmationFragment
import com.philips.platform.mec.utils.*
import kotlinx.android.synthetic.main.mec_cvc_code_fragment.view.*

class MECCVVFragment : BottomSheetDialogFragment() {

    private lateinit var binding: MecCvcCodeFragmentBinding
    private lateinit var paymentViewModel: PaymentViewModel
    private var mEcsOrderDetail: ECSOrderDetail? = null
    private var mEcsShoppingCart: ECSShoppingCart? = null
    private var containerID: Int = 0

    companion object {
        const val TAG: String = "MECCVVFragment"
    }

    private val orderDetailObserver: Observer<ECSOrderDetail> = Observer(fun(ecsOrderDetail: ECSOrderDetail) {
        MECLog.d(javaClass.simpleName, ecsOrderDetail.code)
        mEcsOrderDetail = ecsOrderDetail
        binding.root.mec_progress.visibility = View.GONE
        gotoPaymentConfirmationFragment()
    })

    private val errorObserver: Observer<MecError> = Observer(fun(mecError: MecError?) {
        var actionMap = HashMap<String, String>()
        actionMap.put(paymentType, old)
        actionMap.put(specialEvents, paymentFailure)
        mEcsShoppingCart?.data?.attributes?.items?.let { MECAnalytics.tagActionsWithOrderProductsInfo(actionMap, it) }
        context?.let { MECutility.tagAndShowError(mecError, false, fragmentManager, it) }
        showErrorDialog()
        binding.root.mec_progress.visibility = View.GONE
    })

    private fun showErrorDialog() {
        context?.let { fragmentManager?.let { it1 -> MECutility.showErrorDialog(it, it1, getString(R.string.mec_ok), getString(R.string.mec_payment), R.string.mec_payment_failed_message) } }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = MecCvcCodeFragmentBinding.inflate(inflater, container, false)
        binding.fragment = this
        val bundle = arguments
        val ecsPayment = bundle?.getSerializable(MECConstant.MEC_PAYMENT_METHOD) as ECSPayment
        mEcsShoppingCart = bundle.getParcelable(MECConstant.MEC_SHOPPING_CART)
        binding.paymentMethod = ecsPayment

        containerID = bundle.getInt(MECConstant.MEC_FRAGMENT_CONTAINER_ID)

        paymentViewModel = ViewModelProviders.of(this).get(PaymentViewModel::class.java)
        paymentViewModel.ecsOrderDetail.observe(this, orderDetailObserver)
        paymentViewModel.mecError.observe(this, errorObserver)
        MECAnalytics.trackPage(cvvPage)
        return binding.root
    }

    fun onClickContinue() {

        val cvv = binding.root.mec_cvv_digits.text.toString()
        if (cvv.trim().isNotEmpty()) {
            binding.root.mec_progress.visibility = View.VISIBLE
            paymentViewModel.submitOrder(cvv)
        } else {
            binding.root.mec_cvv_digits.startAnimation(MECutility.getShakeAnimation())
            this.context?.let { MECAnalytics.getDefaultString(it, R.string.mec_blank_cvv_error) }?.let { MECAnalytics.trackUserError(it) }
        }

    }

    private fun gotoPaymentConfirmationFragment() {
        val mecPaymentConfirmationFragment: MECPaymentConfirmationFragment = MECPaymentConfirmationFragment()
        val bundle = Bundle()
        bundle.putParcelable(MECConstant.MEC_ORDER_DETAIL, mEcsOrderDetail)
        bundle.putBoolean(MECConstant.PAYMENT_SUCCESS_STATUS, java.lang.Boolean.TRUE)
        bundle.putString(paymentType, old)
        mecPaymentConfirmationFragment.arguments = bundle
        dismiss()
        replaceFragment(mecPaymentConfirmationFragment, true)
    }

    private fun replaceFragment(newFragment: MecBaseFragment, isReplaceWithBackStack: Boolean) {
        if (MECDataHolder.INSTANCE.actionbarUpdateListener == null || MECDataHolder.INSTANCE.mecCartUpdateListener == null)
            RuntimeException("ActionBarListner and MECListner cant be null")
        else {
            if (activity?.isFinishing == false) {

                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.replace(containerID, newFragment, newFragment.getFragmentTag())
                if (isReplaceWithBackStack) {
                    transaction?.addToBackStack(newFragment.getFragmentTag())
                }
                transaction?.commitAllowingStateLoss()
            }
        }
    }

}