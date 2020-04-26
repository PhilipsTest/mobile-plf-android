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

package com.philips.platform.mec.screens.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.philips.platform.ecs.model.orders.ECSOrderHistory
import com.philips.platform.ecs.model.orders.ECSOrders
import com.philips.platform.mec.R
import com.philips.platform.mec.common.ItemClickListener
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.databinding.MecOrderHistoryFragmentBinding
import com.philips.platform.mec.screens.MecBaseFragment
import com.philips.platform.mec.screens.history.orderDetail.MECOrderDetailFragment
import com.philips.platform.mec.utils.AlertListener
import com.philips.platform.mec.utils.MECConstant
import com.philips.platform.mec.utils.MECutility


class MECOrderHistoryFragment : MecBaseFragment(),ItemClickListener {

    private lateinit var mecOrderHistoryAdapter: MECOrderHistoryAdapter
    private lateinit var mecOrderHistoryViewModel: MECOrderHistoryViewModel
    private var mRootView: View? = null
    private lateinit var binding: MecOrderHistoryFragmentBinding

    private var pageNumber = 0
    private var pageSize = 10
    private var totalPage = 0


    private var ordersList = mutableListOf<ECSOrders>()
    private var dateOrdersMap = LinkedHashMap<String, MutableList<ECSOrders>>()

    private var isCallOnProgress = false

    private var mecOrderHistoryService = MECOrderHistoryService()

    override fun getFragmentTag(): String {
        return "MECOrderHistoryFragment"
    }

    private val orderHistoryObserver: Observer<ECSOrderHistory> = Observer { ecsOrderHistory ->

        if (ecsOrderHistory.pagination.totalResults == 0) {
            showOrderEmptyMessage()
        } else {
            totalPage = ecsOrderHistory.pagination.totalPages
            pageNumber = ecsOrderHistory.pagination.currentPage

            ordersList.addAll(ecsOrderHistory.orders)
            ordersList.sortByDescending { it.placed }
            fetchOrderDetailForOrders(ordersList)
        }
    }

    private val orderDetailObserver: Observer<ECSOrders> = Observer { ecsOrders ->

        if(mecOrderHistoryViewModel.callCount == 0){
            showData()
        }
    }

    private fun fetchOrderDetailForOrders(orderList: MutableList<ECSOrders>) {
        mecOrderHistoryViewModel.setThreadCount(orderList.size)

        for (orders in orderList) {
            mecOrderHistoryViewModel.fetchOrderDetail(orders)
        }
    }


    private fun showData(){
        mecOrderHistoryService.getDateOrderMap(dateOrdersMap,ordersList)
        hidePaginationProgressBar()
        hideFullScreenProgressBar()
        isCallOnProgress = false
        mecOrderHistoryAdapter.notifyDataSetChanged()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setCartIconVisibility(false)
        if (null == mRootView) {
            binding = MecOrderHistoryFragmentBinding.inflate(inflater, container, false)

            mecOrderHistoryViewModel = ViewModelProvider(this).get(MECOrderHistoryViewModel::class.java)
            mecOrderHistoryViewModel.ecsOrderHistory.observe(viewLifecycleOwner, orderHistoryObserver)
            mecOrderHistoryViewModel.ecsOrders.observe(viewLifecycleOwner,orderDetailObserver)
            mecOrderHistoryViewModel.mecError.observe(viewLifecycleOwner, this)

            mecOrderHistoryAdapter = MECOrderHistoryAdapter(dateOrdersMap,this )

            binding.dateRecyclerView.adapter = mecOrderHistoryAdapter
            binding.mecEmptyHistory.btnContinueShopping.setOnClickListener { showProductCatalogFragment(getFragmentTag()) }

            showFullScreenProgressBar()
            executeRequest()
            handlePagination()

            mRootView = binding.root
        }
        return mRootView
    }

    private fun handlePagination() {
        binding.dateRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, verticalScrollPixel: Int) {

                if (verticalScrollPixel > 0 && shouldFetchNextPage()) {
                    pageNumber++
                    executeRequest()
                    showPaginationProgressBar()
                }
            }
        })
    }

    private fun executeRequest() {
        isCallOnProgress = true
        mecOrderHistoryViewModel.fetchOrderHistory(pageNumber, pageSize)
    }

    override fun processError(mecError: MecError?, showDialog: Boolean) {
        super.processError(mecError, false)
        isCallOnProgress = false

        if(mecError?.mECRequestType==MECRequestType.MEC_FETCH_ORDER_DETAILS_FOR_ORDERS){
            if(mecOrderHistoryViewModel.callCount == 0) showData()
        }else{
            showErrorDialog(mecError)
        }

    }


    fun shouldFetchNextPage(): Boolean {

        if (!isCallOnProgress) {
            val lay = binding.dateRecyclerView.layoutManager as LinearLayoutManager

            if (mecOrderHistoryService.isScrollDown(lay)) {
                if (pageNumber != totalPage - 1) {
                    return true
                }
            }
            return false
        }else{
            return false
        }
    }

    private fun showPaginationProgressBar() {
        binding.mecProgressLayout.visibility = View.VISIBLE
    }

    private fun hidePaginationProgressBar() {
        binding.mecProgressLayout.visibility = View.GONE
    }

    private fun showFullScreenProgressBar() {
        binding.mecOrderHistoryProgress.mecProgressBarContainer.visibility = View.VISIBLE
    }

    private fun hideFullScreenProgressBar() {
        binding.mecOrderHistoryProgress.mecProgressBarContainer.visibility = View.GONE
    }

    private fun showOrderEmptyMessage() {
        hidePaginationProgressBar()
        hideFullScreenProgressBar()
        isCallOnProgress = false
        binding.dateRecyclerView.visibility = View.GONE
        binding.mecEmptyHistory.rlEmptyHistory.visibility = View.VISIBLE
    }

    private fun showErrorDialog(mecError: MecError?) {

        context?.let {
            fragmentManager?.let { it1 ->
                MECutility.showPositiveActionDialog(it, context!!.getString(R.string.mec_ok), context!!.getString(R.string.mec_order_summary), mecError!!.exception!!.message.toString(), it1, object : AlertListener {
                    override fun onPositiveBtnClick() {
                        exitMEC()
                    }
                })
            }
        }
    }

    override fun onItemClick(item: Any) {

        val ecsOrders = item as ECSOrders
        val fragment = MECOrderDetailFragment()
        val bundle = Bundle()
        bundle.putSerializable(MECConstant.MEC_ORDERS,ecsOrders)
        fragment.arguments = bundle
        replaceFragment(fragment,fragment.getFragmentTag(),true)
    }
}