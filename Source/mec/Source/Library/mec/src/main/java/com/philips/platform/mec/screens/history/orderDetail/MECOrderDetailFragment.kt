/*
 *  Copyright (c) Koninklijke Philips N.V., 2020
 *
 *  * All rights are reserved. Reproduction or dissemination
 *
 *  * in whole or in part is prohibited without the prior written
 *
 *  * consent of the copyright holder.
 *
 *
 */

package com.philips.platform.mec.screens.history.orderDetail


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.philips.cdp.prxclient.datamodels.cdls.ContactPhone
import com.philips.platform.ecs.model.cart.AppliedVoucherEntity
import com.philips.platform.ecs.model.orders.ECSOrderDetail
import com.philips.platform.ecs.model.orders.ECSOrders
import com.philips.platform.mec.R
import com.philips.platform.mec.analytics.MECAnalyticPageNames.orderDetail
import com.philips.platform.mec.analytics.MECAnalytics
import com.philips.platform.mec.common.ItemClickListener
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.databinding.MecOrderHistoryDetailBinding
import com.philips.platform.mec.screens.MecBaseFragment
import com.philips.platform.mec.screens.history.MECOrderHistoryService
import com.philips.platform.mec.screens.shoppingCart.MECCartSummary
import com.philips.platform.mec.screens.shoppingCart.MECCartSummaryAdapter
import com.philips.platform.mec.utils.MECConstant
import com.philips.platform.mec.utils.MECConstant.MEC_ORDER_CUSTOMER_CARE_PHONE
import com.philips.platform.mec.utils.MECConstant.MEC_ORDER_NUMBER
import com.philips.platform.mec.utils.MECutility

class MECOrderDetailFragment : MecBaseFragment(), ItemClickListener {


    private lateinit var binding: MecOrderHistoryDetailBinding
    private var ecsOrders: ECSOrders? = null
    private lateinit var mecOrderDetailViewModel: MECOrderDetailViewModel
    private var mecOrderDetailService = MECOrderDetailService()
    private var priceAdapter: MECCartSummaryAdapter? = null
    private var productsAdapter: MECOrderDetailProductsAdapter? = null
    private var vouchersAdapter: MECOrderDetailVouchersAdapter? = null
    private lateinit var cartSummaryList: MutableList<MECCartSummary>
    private lateinit var voucherList: MutableList<AppliedVoucherEntity>
    private var mECOrderHistoryService = MECOrderHistoryService()
    private var mContactphone:ContactPhone?=null


    override fun getFragmentTag(): String {
        return "MECOrderDetailFragment"
    }

    private val contactsObserver: Observer<ContactPhone> = Observer { contactPhone ->
        mContactphone= contactPhone
        binding.contactPhone = contactPhone
        dismissProgressBar(binding.mecOrderHistoryDetailProgress.mecProgressBarContainer)
        binding.mecOrderHistoryDetailCallBtn.setOnClickListener { callPhone(contactPhone.phoneNumber) }
    }

    private fun  updateUI(){

        cartSummaryList.clear()
        priceAdapter = ecsOrders?.orderDetail?.let { addCartSummaryList(it) }?.let { MECCartSummaryAdapter(it) }
        productsAdapter = ecsOrders?.orderDetail?.let { MECOrderDetailProductsAdapter(it, this) }
        vouchersAdapter = MECOrderDetailVouchersAdapter(ecsOrders?.orderDetail!!.appliedVouchers)

        binding.mecOrderHistoryDetailAcceptedCodesRecyclerView.adapter = vouchersAdapter
        binding.mecOrderHistoryDetailProductRecyclerView.adapter = productsAdapter
        binding.mecOrderHistoryDetailPriceRecyclerView.adapter = priceAdapter


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setCartIconVisibility(false)
        cartSummaryList = mutableListOf()
        voucherList = mutableListOf()
        binding = MecOrderHistoryDetailBinding.inflate(inflater, container, false)
        binding.mecOrderHistoryService = mECOrderHistoryService
        mecOrderDetailViewModel = ViewModelProvider(this).get(MECOrderDetailViewModel::class.java)
        mecOrderDetailViewModel.contactPhone.observe(this, contactsObserver)
        mecOrderDetailViewModel.mecError.observe(this, this)
        ecsOrders = arguments?.getSerializable(MECConstant.MEC_ORDERS) as ECSOrders?
        binding.ecsOrders = ecsOrders
        binding.cardInfo = ecsOrders?.orderDetail?.paymentInfo?.let { MECutility().constructCardDetails(it) }
        binding.cardExpiry = ecsOrders?.orderDetail?.paymentInfo?.let { MECutility().constructCardValidityDetails(it) }
        updateUI()
        val subCategory = mecOrderDetailService.getProductSubcategory(ecsOrders?.orderDetail)

        context?.let { subCategory?.let { it1 ->
            showProgressBar(binding.mecOrderHistoryDetailProgress.mecProgressBarContainer)
            mecOrderDetailViewModel.fetchContacts(it, it1) } }

        binding.mecOrderHistoryCancelOrderBtn.setOnClickListener { onCancelOrder() }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setTitleAndBackButtonVisibility(R.string.mec_order_details, true)
        setCartIconVisibility(false)
        MECAnalytics.trackPage(orderDetail)
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissProgressBar(binding.mecOrderHistoryDetailProgress.mecProgressBarContainer)
    }


    private fun addCartSummaryList(orderDetail: ECSOrderDetail): MutableList<MECCartSummary> {
        mecOrderDetailService.addAppliedOrderPromotionsToCartSummaryList(orderDetail, cartSummaryList)
        mecOrderDetailService.addAppliedVoucherToCartSummaryList(orderDetail, cartSummaryList)
        mecOrderDetailService.addDeliveryCostToCartSummaryList(binding.mecDeliveryModeDescription.context, orderDetail, cartSummaryList)
        return cartSummaryList
    }

    fun onCancelOrder() {
        var mECCancelOrderFragment : MECCancelOrderFragment = MECCancelOrderFragment()
        var arguments: Bundle = Bundle()
        arguments.putString(MEC_ORDER_NUMBER,  binding.ecsOrders?.code)
        if( mContactphone!=null ) {
            arguments.putSerializable(MEC_ORDER_CUSTOMER_CARE_PHONE, mContactphone)
        }
        mECCancelOrderFragment.arguments = arguments
        replaceFragment(mECCancelOrderFragment,mECCancelOrderFragment.getFragmentTag(),true)
    }

    fun showTrackUrlFragment(url: String) {
        val bundle = Bundle()
        bundle.putString(MECConstant.MEC_TRACK_ORDER_URL, url)
        val mECOrderDetailTrackUrlFragment = MECOrderDetailTrackUrlFragment()
        mECOrderDetailTrackUrlFragment.arguments = bundle
        replaceFragment(mECOrderDetailTrackUrlFragment, mECOrderDetailTrackUrlFragment.getFragmentTag(), true)
    }


    fun callPhone(phone: String) {
        try {
            val myintent = Intent(Intent.ACTION_DIAL)
            myintent.data = Uri.parse("tel:" + phone)
            startActivity(myintent)
        } catch (e: NullPointerException) {
        }
    }



    override fun processError(mecError: MecError?, showDialog: Boolean) {
        super.processError(mecError, showDialog)
        dismissProgressBar(binding.mecOrderHistoryDetailProgress.mecProgressBarContainer)
    }

    override fun onItemClick(item: Any) {
       showTrackUrlFragment(item as String)
    }


}