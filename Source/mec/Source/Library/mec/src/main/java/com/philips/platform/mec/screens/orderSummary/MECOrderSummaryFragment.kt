/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.orderSummary


import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.philips.cdp.di.ecs.model.address.ECSAddress
import com.philips.cdp.di.ecs.model.cart.AppliedVoucherEntity
import com.philips.cdp.di.ecs.model.cart.ECSShoppingCart
import com.philips.cdp.di.ecs.model.orders.ECSOrderDetail
import com.philips.cdp.di.ecs.model.payment.ECSPaymentProvider
import com.philips.platform.mec.R
import com.philips.platform.mec.common.ItemClickListener
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.databinding.MecOrderSummaryFragmentBinding
import com.philips.platform.mec.screens.payment.MECPayment
import com.philips.platform.mec.screens.payment.PaymentViewModel
import com.philips.platform.mec.screens.MecBaseFragment
import com.philips.platform.mec.screens.catalog.MecPrivacyFragment
import com.philips.platform.mec.screens.payment.MECWebPaymentFragment
import com.philips.platform.mec.screens.shoppingCart.MECCartSummary
import com.philips.platform.mec.screens.shoppingCart.MECCartSummaryAdapter
import com.philips.platform.mec.screens.shoppingCart.MECShoppingCartFragment
import com.philips.platform.mec.utils.MECConstant
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.mec.utils.MECLog


/**
 * A simple [Fragment] subclass.
 */
class MECOrderSummaryFragment : MecBaseFragment(), ItemClickListener {
    companion object {
        val TAG = "MECOrderSummaryFragment"
    }

    private lateinit var mecOrderSummaryService: MECOrderSummaryServices
    private lateinit var binding: MecOrderSummaryFragmentBinding
    private lateinit var ecsShoppingCart: ECSShoppingCart
    private lateinit var ecsAddress: ECSAddress
    private lateinit var mecPayment: MECPayment
    private var cartSummaryAdapter: MECCartSummaryAdapter? = null
    private var productsAdapter: MECOrderSummaryProductsAdapter? = null
    private var vouchersAdapter: MECOrderSummaryVouchersAdapter? = null
    private lateinit var cartSummaryList: MutableList<MECCartSummary>
    private lateinit var voucherList: MutableList<AppliedVoucherEntity>
    private lateinit var paymentViewModel: PaymentViewModel
    private lateinit var orderNumber: String
    override fun onItemClick(item: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getFragmentTag(): String {
        return "MECOrderSummaryFragment"
    }

    private val orderObserver: Observer<ECSOrderDetail> = Observer<ECSOrderDetail> { eCSOrderDetail ->
        MECLog.v("orderObserver ", "" + eCSOrderDetail.code)
        updateCount(0) // reset cart count to 0 as current shopping cart is deleted now as result of submit order API call
        orderNumber = eCSOrderDetail.code
        paymentViewModel.makePayment(eCSOrderDetail, mecPayment.ecsPayment.billingAddress)
    }

    private val makePaymentObserver: Observer<ECSPaymentProvider> = Observer<ECSPaymentProvider> { eCSPaymentProvider ->
        MECLog.v("mkPaymentObs ", "" + eCSPaymentProvider.worldpayUrl)
        val mECWebPaymentFragment = MECWebPaymentFragment()
        val bundle = Bundle()
        bundle.putString(MECConstant.ORDER_NUMBER, orderNumber)
        bundle.putString(MECConstant.WEB_PAY_URL, eCSPaymentProvider.worldpayUrl)
        mECWebPaymentFragment.arguments = bundle
        dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
        addFragment(mECWebPaymentFragment, MECWebPaymentFragment.TAG, true)


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mecOrderSummaryService = MECOrderSummaryServices()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = MecOrderSummaryFragmentBinding.inflate(inflater, container, false)
        binding.fragment = this
        binding.shoppingCart
        ecsAddress = arguments?.getSerializable(MECConstant.KEY_ECS_ADDRESS) as ECSAddress
        ecsShoppingCart = arguments?.getSerializable(MECConstant.KEY_ECS_SHOPPING_CART) as ECSShoppingCart
        mecPayment = arguments?.getSerializable(MECConstant.MEC_PAYMENT_METHOD) as MECPayment
        binding.ecsAddressShipping = ecsAddress
        binding.shoppingCart = ecsShoppingCart
        binding.mecPayment = mecPayment
        cartSummaryList = mutableListOf()
        voucherList = mutableListOf()
        if (ecsShoppingCart.appliedVouchers.size > 0) {
            ecsShoppingCart.appliedVouchers?.let { voucherList.addAll(it) }
        }
        cartSummaryList.clear()
        cartSummaryAdapter = MECCartSummaryAdapter(addCartSummaryList(ecsShoppingCart))
        productsAdapter = MECOrderSummaryProductsAdapter(ecsShoppingCart)
        vouchersAdapter = MECOrderSummaryVouchersAdapter(voucherList)
        binding.mecCartSummaryRecyclerView.adapter = productsAdapter
        binding.mecAcceptedCodeRecyclerView.adapter = vouchersAdapter
        binding.mecPriceSummaryRecyclerView.adapter = cartSummaryAdapter

        paymentViewModel = ViewModelProviders.of(this).get(PaymentViewModel::class.java)
        paymentViewModel.ecsOrderDetail.observe(this, orderObserver)
        paymentViewModel.eCSPaymentProvider.observe(this, makePaymentObserver)
        paymentViewModel.mecError.observe(this,this)


        if (MECDataHolder.INSTANCE.getPrivacyUrl() != null && MECDataHolder.INSTANCE.getFaqUrl() != null && MECDataHolder.INSTANCE.getTermsUrl() != null) {
            binding.mecPrivacy.visibility = View.VISIBLE
            privacyTextView(binding.mecPrivacy)
        } else {
            binding.mecPrivacy.visibility = View.GONE
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setTitleAndBackButtonVisibility(R.string.mec_checkout, true)
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
    }


    private fun addCartSummaryList(ecsShoppingCart: ECSShoppingCart): MutableList<MECCartSummary> {
        mecOrderSummaryService.addAppliedOrderPromotionsToCartSummaryList(ecsShoppingCart, cartSummaryList)
        mecOrderSummaryService.addAppliedVoucherToCartSummaryList(ecsShoppingCart, cartSummaryList)
        mecOrderSummaryService.addDeliveryCostToCartSummaryList(binding.mecDeliveryModeDescription.context, ecsShoppingCart, cartSummaryList)
        cartSummaryAdapter?.notifyDataSetChanged()
        return cartSummaryList
    }


    fun onClickPay() {

        if (isPaymentMethodAvailable()) { // user with saved payment method
            showCVV()
        } else {    // first time user
            showProgressBar(binding.mecProgress.mecProgressBarContainer)
            paymentViewModel.submitOrder(null)
        }

    }

    private fun isPaymentMethodAvailable(): Boolean {// is user has selected a already saved payment
        return !mecPayment.ecsPayment.id.equals(MECConstant.NEW_CARD_PAYMENT, true)
    }

    fun showCVV() {
        val bundle = Bundle()
        bundle.putSerializable(MECConstant.MEC_PAYMENT_METHOD, mecPayment.ecsPayment)
        val mecCvvBottomSheetFragment = MECCVVFragment()
        mecCvvBottomSheetFragment.arguments = bundle
        mecCvvBottomSheetFragment.setTargetFragment(this, MECConstant.PAYMENT_REQUEST_CODE)
        fragmentManager?.let { mecCvvBottomSheetFragment.show(it, mecCvvBottomSheetFragment.tag) }

    }

    fun onClickBackToShoppingCart() {
        fragmentManager!!.popBackStack(MECShoppingCartFragment.TAG, 0)
    }

    private fun privacyTextView(view: TextView) {
        val spanTxt = SpannableStringBuilder(
                getString(R.string.mec_read_privacy))
        spanTxt.append(" ")
        spanTxt.append(getString(R.string.mec_privacy))
        spanTxt.append(" ")
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                showPrivacyFragment(getString(R.string.mec_privacy))
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = true
                ds.color = R.attr.uidHyperlinkDefaultPressedTextColor
            }
        }, spanTxt.length - getString(R.string.mec_privacy).length - 1, spanTxt.length, 0)
        spanTxt.append(getString(R.string.mec_questions))
        spanTxt.append(" ")
        spanTxt.append(getString(R.string.mec_faq))
        spanTxt.append(" ")
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                showPrivacyFragment(getString(R.string.mec_faq))
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = true
                ds.color = R.attr.uidHyperlinkDefaultPressedTextColor
            }
        }, spanTxt.length - getString(R.string.mec_faq).length - 1, spanTxt.length, 0)
        spanTxt.append(getString(R.string.mec_page))
        spanTxt.append(getString(R.string.mec_accept_terms))
        spanTxt.append(" ")
        spanTxt.append(getString(R.string.mec_terms_conditions))
        spanTxt.append(" ")
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                showPrivacyFragment(getString(R.string.mec_terms_conditions))
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = true
                ds.color = R.attr.uidHyperlinkDefaultPressedTextColor
            }
        }, spanTxt.length - getString(R.string.mec_terms_conditions).length - 1, spanTxt.length, 0)
        binding.mecPrivacy.highlightColor = Color.TRANSPARENT
        view.movementMethod = LinkMovementMethod.getInstance()
        view.setText(spanTxt, TextView.BufferType.SPANNABLE)
    }

    private fun showPrivacyFragment(stringRes: String) {
        val bundle = Bundle()
        when (stringRes) {
            getString(R.string.mec_privacy) -> {
                bundle.putString(MECConstant.MEC_PRIVACY_URL, MECDataHolder.INSTANCE.getPrivacyUrl())
            }
            getString(R.string.mec_faq) -> {
                bundle.putString(MECConstant.MEC_PRIVACY_URL, MECDataHolder.INSTANCE.getFaqUrl())
            }
            getString(R.string.mec_terms_conditions) -> {
                bundle.putString(MECConstant.MEC_PRIVACY_URL, MECDataHolder.INSTANCE.getTermsUrl())
            }

        }
        val mecPrivacyFragment = MecPrivacyFragment()
        mecPrivacyFragment.arguments = bundle
        replaceFragment(mecPrivacyFragment, MecPrivacyFragment.TAG, true)
    }

    override fun processError(mecError: MecError?, showDialog: Boolean) {
        dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
        super.processError(mecError, showDialog)
    }
}
