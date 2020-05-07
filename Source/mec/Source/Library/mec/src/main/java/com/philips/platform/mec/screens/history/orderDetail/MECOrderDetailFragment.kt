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
    private  var mECOrderHistoryService= MECOrderHistoryService()


    override fun getFragmentTag(): String {
        return "MECOrderDetailFragment"
    }

    private val contactsObserver: Observer<ContactPhone> = Observer { contactPhone ->
        binding.contactPhone = contactPhone
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setCartIconVisibility(false)
        cartSummaryList = mutableListOf()
        voucherList = mutableListOf()

        binding = MecOrderHistoryDetailBinding.inflate(inflater, container, false)
        binding.fragment=this
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
        productsAdapter = MECOrderDetailProductsAdapter(ecsOrders?.orderDetail,this )
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

     fun showTrackUrlFragment(url :String ) {
        val bundle = Bundle()
        bundle.putString(MECConstant.MEC_TRACK_ORDER_URL, url)
        val mECOrderDetailTrackUrlFragment = MECOrderDetailTrackUrlFragment()
        mECOrderDetailTrackUrlFragment.arguments = bundle
        replaceFragment(mECOrderDetailTrackUrlFragment, mECOrderDetailTrackUrlFragment.getFragmentTag(), true)
    }

   /* fun checkPermissionAndCall(phone: String) {
        if (context?.let {
                    ContextCompat.checkSelfPermission(it,
                            Manifest.permission.CALL_PHONE)
                }
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.CALL_PHONE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                activity?.let {
                    ActivityCompat.requestPermissions(it,
                            arrayOf(Manifest.permission.CALL_PHONE),
                            123)
                }
            }
        } else {
            // Permission has already been granted
            callPhone(phone)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 123) {
            // If request is cancelled, the result arrays are empty.
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // permission was granted, yay!
                callPhone()
            } else {
                // permission denied, boo! Disable the
                // functionality
            }
            return
        }
    }*/

    fun callPhone(phone :String){
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone))
        startActivity(intent)
    }

}