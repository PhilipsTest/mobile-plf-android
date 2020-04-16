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
import com.philips.cdp.di.ecs.model.orders.ECSOrderHistory
import com.philips.cdp.di.ecs.model.orders.ECSOrders
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.databinding.MecOrderHistoryFragmentBinding
import com.philips.platform.mec.screens.MecBaseFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class MECOrderHistoryFragment : MecBaseFragment() {

    private lateinit var mecOrderHistoryViewModel: MECOrderHistoryViewModel
    private var mRootView: View? = null
    private lateinit var binding: MecOrderHistoryFragmentBinding

    private var pageNumber = 0
    private var pageSize = 20
    private var totalPage = 0

    private var ordersList = mutableListOf<ECSOrders>()

    private var isCallOnProgress = false

    private var mecOrderHistoryService = MECOrderHistoryService()

    override fun getFragmentTag(): String {
        return "MECOrderHistoryFragment"
    }

    private val orderHistoryObserver: Observer<ECSOrderHistory> = Observer { ecsOrderHistory ->
        totalPage = ecsOrderHistory.pagination.totalPages
        pageNumber = ecsOrderHistory.pagination.currentPage

        ordersList.addAll(ecsOrderHistory.orders)
        ordersList.sortByDescending { it.placed }
        fetchOrderDetailForOrders(ordersList)
    }

    private fun fetchOrderDetailForOrders(orderList: MutableList<ECSOrders>) {

        // wait this for loop to be over using kotlin co-routine
        CoroutineScope(IO).launch {

            for (ecsOrders in orderList) {
                mecOrderHistoryViewModel.fetchOrderDetail(ecsOrders)
            }

            binding.mecOrdersModel = MECOrdersModel(orderList)
            hidePaginationProgressBar()
            hideFullScreenProgressBar()
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setCartIconVisibility(false)
        if (null == mRootView) {
            binding = MecOrderHistoryFragmentBinding.inflate(inflater, container, false)

            mecOrderHistoryViewModel = ViewModelProvider(this).get(MECOrderHistoryViewModel::class.java)
            mecOrderHistoryViewModel.ecsOrderHistory.observe(viewLifecycleOwner, orderHistoryObserver)
            mecOrderHistoryViewModel.mecError.observe(viewLifecycleOwner, this)

            showFullScreenProgressBar()
            executeRequest()
            handlePagination()

            mRootView = binding.root
        }
        return mRootView
    }

    private fun handlePagination() {
        binding.recyclerOrderHistory.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                if (dy > 0 && shouldFetchNextPage()) {
                    pageNumber++
                    executeRequest()
                    showPaginationProgressBar()
                }
            }
        })
    }

    private fun executeRequest() {
        isCallOnProgress = true
        mecOrderHistoryViewModel.fetchOrderSummary(pageNumber, pageSize)
    }

    override fun processError(mecError: MecError?, showDialog: Boolean) {
        super.processError(mecError, showDialog)
        isCallOnProgress = false
    }


    fun shouldFetchNextPage(): Boolean {
        val lay = binding.recyclerOrderHistory.layoutManager as LinearLayoutManager

        if (mecOrderHistoryService.isScrollDown(lay)) {
            if (pageNumber != totalPage - 1) {
                return true
            }
        }
        return false
    }

    private fun  showPaginationProgressBar(){
       binding.paginationProgressBar.visibility = View.GONE
    }

    private fun hidePaginationProgressBar(){
        binding.paginationProgressBar.visibility = View.VISIBLE
    }

    private fun showFullScreenProgressBar(){
        binding.mecOrderHistoryProgress.mecProgressBarContainer.visibility = View.VISIBLE
    }

    private fun hideFullScreenProgressBar(){
        binding.mecOrderHistoryProgress.mecProgressBarContainer.visibility = View.GONE
    }
}