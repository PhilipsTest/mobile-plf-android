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
import com.philips.platform.mec.databinding.MecOrderHistoryFragmentBinding
import com.philips.platform.mec.screens.MecBaseFragment

class MECOrderHistoryFragment : MecBaseFragment() {

    private lateinit var mecOrderHistoryViewModel: MECOrderHistoryViewModel
    private lateinit var mRootView: View
    private lateinit var binding: MecOrderHistoryFragmentBinding

    private var pageNumber = 0
    private var pageSize = 20
    private var totalPage = 0

    private var isCallOnProgress = false

    private var mecOrderHistoryService = MECOrderHistoryService()

    override fun getFragmentTag(): String {
        return "MECOrderHistoryFragment"
    }

    private val orderHistoryObserver: Observer<ECSOrderHistory> = Observer { ecsOrderHistory ->
        totalPage = ecsOrderHistory.pagination.totalPages
        pageNumber = ecsOrderHistory.pagination.currentPage
        isCallOnProgress = false
        binding.orderHistory = ecsOrderHistory
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setCartIconVisibility(false)
        if (null == mRootView) {
            binding = MecOrderHistoryFragmentBinding.inflate(inflater, container, false)

            mecOrderHistoryViewModel = ViewModelProvider(this).get(MECOrderHistoryViewModel::class.java)
            mecOrderHistoryViewModel.ecsOrderHistory.observe(viewLifecycleOwner, orderHistoryObserver)
            mecOrderHistoryViewModel.mecError.observe(viewLifecycleOwner, this)

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
                }
            }
        })
    }

    private fun executeRequest() {
        isCallOnProgress = true
        mecOrderHistoryViewModel.fetchOrderSummary(pageNumber, pageSize)
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
}