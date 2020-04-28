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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.philips.cdp.prxclient.datamodels.contacts.ContactsModel
import com.philips.platform.ecs.model.cart.AppliedVoucherEntity
import com.philips.platform.ecs.model.cart.ECSShoppingCart
import com.philips.platform.ecs.model.orders.ECSOrderDetail
import com.philips.platform.ecs.model.orders.ECSOrders
import com.philips.platform.mec.R
import com.philips.platform.mec.databinding.MecOrderHistoryDetailBinding
import com.philips.platform.mec.screens.MecBaseFragment
import com.philips.platform.mec.screens.orderSummary.MECOrderSummaryVouchersAdapter
import com.philips.platform.mec.screens.shoppingCart.MECCartSummary
import com.philips.platform.mec.screens.shoppingCart.MECCartSummaryAdapter
import com.philips.platform.mec.utils.MECConstant

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


    override fun getFragmentTag(): String {
        return "MECOrderDetailFragment"
    }

    private val contactsObserver: Observer<ContactsModel> = Observer { contactsModel ->
        binding.contact = contactsModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setCartIconVisibility(false)
        cartSummaryList = mutableListOf()
        voucherList = mutableListOf()
        binding = MecOrderHistoryDetailBinding.inflate(inflater, container, false)

        mecOrderDetailViewModel = ViewModelProvider(this).get(MECOrderDetailViewModel::class.java)

        mecOrderDetailViewModel.contacts.observe(viewLifecycleOwner, contactsObserver)
        mecOrderDetailViewModel.mecError.observe(viewLifecycleOwner, this)
        ecsOrders = arguments?.getSerializable(MECConstant.MEC_ORDERS) as ECSOrders?
        binding.ecsOrders = ecsOrders

        val subCategory = mecOrderDetailService.getProductSubcategory(ecsOrders?.orderDetail)
        context?.let { subCategory?.let { it1 -> mecOrderDetailViewModel.fetchContacts(it, it1) } }

        cartSummaryList.clear()
        cartSummaryAdapter = MECCartSummaryAdapter(addCartSummaryList(ecsOrders?.orderDetail))
        productsAdapter = MECOrderDetailProductsAdapter(ecsOrders?.orderDetail)
        vouchersAdapter = MECOrderDetailVouchersAdapter(ecsOrders?.orderDetail!!.appliedVouchers)
        binding.mecCartSummaryRecyclerView.adapter = productsAdapter
        binding.mecAcceptedCodeRecyclerView.adapter = vouchersAdapter
        binding.mecPriceSummaryRecyclerView.adapter = cartSummaryAdapter

        return binding.root
    }

    override fun onStart() {
        super.onStart()
            setTitleAndBackButtonVisibility(R.string.mec_my_orders, true)
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

    }
}