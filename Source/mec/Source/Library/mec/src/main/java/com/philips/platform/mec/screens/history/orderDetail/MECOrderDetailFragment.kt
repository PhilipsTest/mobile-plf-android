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
import com.philips.cdp.prxclient.datamodels.contacts.ContactPhone
import com.philips.platform.ecs.model.cart.AppliedVoucherEntity
import com.philips.platform.ecs.model.orders.ECSOrderDetail
import com.philips.platform.ecs.model.orders.ECSOrders
import com.philips.platform.mec.R
import com.philips.platform.mec.databinding.MecOrderHistoryDetailBinding
import com.philips.platform.mec.screens.MecBaseFragment
import com.philips.platform.mec.screens.history.MECOrderHistoryService
import com.philips.platform.mec.screens.shoppingCart.MECCartSummary
import com.philips.platform.mec.screens.shoppingCart.MECCartSummaryAdapter
import com.philips.platform.mec.utils.MECConstant
import com.philips.platform.mec.utils.MECConstant.MEC_ORDER_CUSTOMER_CARE_HOLIDAY_WORKING_HOUR
import com.philips.platform.mec.utils.MECConstant.MEC_ORDER_CUSTOMER_CARE_PHONE
import com.philips.platform.mec.utils.MECConstant.MEC_ORDER_CUSTOMER_CARE_WEEK_WORKING_HOUR
import com.philips.platform.mec.utils.MECConstant.MEC_ORDER_NUMBER

class MECOrderDetailFragment : MecBaseFragment() {

    private lateinit var binding: MecOrderHistoryDetailBinding
    private var ecsOrders: ECSOrders? = null
    private lateinit var mecOrderDetailViewModel: MECOrderDetailViewModel
    private var mecOrderDetailService = MECOrderDetailService()
    private var cartSummaryAdapter: MECCartSummaryAdapter? = null
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
        binding.contactPhone = contactPhone
        mContactphone= contactPhone
        binding.mecOrderHistoryDetailCallBtn.setOnClickListener { callPhone(contactPhone.phoneNumber) }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setCartIconVisibility(false)
        cartSummaryList = mutableListOf()
        voucherList = mutableListOf()

        binding = MecOrderHistoryDetailBinding.inflate(inflater, container, false)
        binding.mecOrderHistoryService = mECOrderHistoryService
        mecOrderDetailViewModel = ViewModelProvider(this).get(MECOrderDetailViewModel::class.java)

        mecOrderDetailViewModel.contactPhone.observe(viewLifecycleOwner, contactsObserver)
        mecOrderDetailViewModel.mecError.observe(viewLifecycleOwner, this)
        ecsOrders = arguments?.getSerializable(MECConstant.MEC_ORDERS) as ECSOrders?
        binding.ecsOrders = ecsOrders

        val subCategory = mecOrderDetailService.getProductSubcategory(ecsOrders?.orderDetail)
        context?.let { subCategory?.let { it1 -> mecOrderDetailViewModel.fetchContacts(it, it1) } }

        cartSummaryList.clear()
        cartSummaryAdapter = MECCartSummaryAdapter(addCartSummaryList(ecsOrders?.orderDetail))
        productsAdapter = MECOrderDetailProductsAdapter(ecsOrders?.orderDetail, this)
        vouchersAdapter = MECOrderDetailVouchersAdapter(ecsOrders?.orderDetail!!.appliedVouchers)
        binding.mecAcceptedCodeRecyclerView.adapter = vouchersAdapter
        binding.mecCartSummaryRecyclerView.adapter = productsAdapter
        binding.mecPriceSummaryRecyclerView.adapter = cartSummaryAdapter

        binding.mecOrderHistoryCancelOrderBtn.setOnClickListener { onCancelOrder() }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setTitleAndBackButtonVisibility(R.string.mec_order_details, true)
        setCartIconVisibility(false)

    }


    private fun addCartSummaryList(orderDetail: ECSOrderDetail?): MutableList<MECCartSummary> {
        mecOrderDetailService.addAppliedOrderPromotionsToCartSummaryList(orderDetail!!, cartSummaryList)
        mecOrderDetailService.addAppliedVoucherToCartSummaryList(orderDetail, cartSummaryList)
        mecOrderDetailService.addDeliveryCostToCartSummaryList(binding.mecDeliveryModeDescription.context, orderDetail, cartSummaryList)
        cartSummaryAdapter?.notifyDataSetChanged()
        return cartSummaryList
    }

    fun onCancelOrder() {
        var mECCancelOrderFragment : MECCancelOrderFragment = MECCancelOrderFragment()
        if( mContactphone!=null && mContactphone!!.openingHoursWeekdays!! !=null && mContactphone!!.openingHoursSaturday!! !=null) {
            var arguments: Bundle = Bundle()
            arguments.putString(MEC_ORDER_NUMBER,  binding.ecsOrders?.code)
            arguments.putString(MEC_ORDER_CUSTOMER_CARE_PHONE, mContactphone!!.phoneNumber)
            arguments.putString(MEC_ORDER_CUSTOMER_CARE_WEEK_WORKING_HOUR, mContactphone!!.openingHoursWeekdays)
            arguments.putString(MEC_ORDER_CUSTOMER_CARE_HOLIDAY_WORKING_HOUR, mContactphone!!.openingHoursSaturday)
            mECCancelOrderFragment.arguments = arguments
        }
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
            myintent.data = Uri.parse("tel:" + phone!!)
            myintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(myintent)

           /* val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone))
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context?.let { startActivity(it, intent, null) }*/
        } catch (e: NullPointerException) {
        }
    }


}