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
import com.philips.platform.ecs.error.ECSError
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.model.orders.ECSOrderHistory
import com.philips.platform.ecs.model.orders.ECSOrders
import com.philips.platform.mec.R
import com.philips.platform.mec.analytics.MECAnalyticPageNames.orderHistory
import com.philips.platform.mec.analytics.MECAnalytics
import com.philips.platform.mec.common.ItemClickListener
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.databinding.MecOrderHistoryFragmentBinding
import com.philips.platform.mec.screens.MecBaseFragment
import com.philips.platform.mec.screens.history.orderDetail.MECOrderDetailFragment
import com.philips.platform.mec.utils.AlertListener
import com.philips.platform.mec.utils.MECConstant
import com.philips.platform.mec.utils.MECutility
import java.util.concurrent.atomic.AtomicInteger


class MECOrderHistoryFragment : MecBaseFragment(),ItemClickListener {

    private lateinit var mecOrderHistoryAdapter: MECOrderHistoryAdapter
    private lateinit var mecOrderHistoryViewModel: MECOrderHistoryViewModel
    private var mRootView: View? = null
    private lateinit var binding: MecOrderHistoryFragmentBinding

    private var pageNumber = 0
    private var pageSize = 10
    private var totalPage = 0


    private  var mOrdersList : MutableList<ECSOrders> = mutableListOf<ECSOrders>()
    private var dateOrdersMap = LinkedHashMap<String, MutableList<ECSOrders>>()

    private var isCallOnProgress = false

    private var mecOrderHistoryService = MECOrderHistoryService()

    override fun getFragmentTag(): String {
        return "MECOrderHistoryFragment"
    }

    private val orderHistoryObserver: Observer<ECSOrderHistory> = Observer { ecsOrderHistory ->

        if (ecsOrderHistory?.pagination?.totalResults == 0) {
            showOrderEmptyMessage()
        } else {
            totalPage = ecsOrderHistory?.pagination?.totalPages ?: 0
            pageNumber = ecsOrderHistory?.pagination?.currentPage ?:0
            fetchOrderDetailForOrders(ecsOrderHistory.orders)
        }
    }

    private fun fetchOrderDetailForOrders(orderList: MutableList<ECSOrders>) {

        val numberOfDetailsToBeFetched = AtomicInteger()
        numberOfDetailsToBeFetched.set(orderList.size)

        for (orders in orderList) {
            mecOrderHistoryViewModel.fetchOrderDetail(orders, object: ECSCallback<ECSOrders, Exception>{
                override fun onResponse(result: ECSOrders?) {
                    result?.let { mOrdersList.add(it) }
                    val decrementAndGet = numberOfDetailsToBeFetched.decrementAndGet()
                    if(decrementAndGet == 0) showData()
                }

                override fun onFailure(error: Exception?, ecsError: ECSError?) {
                    mOrdersList.add(orders)
                    val decrementAndGet = numberOfDetailsToBeFetched.decrementAndGet()
                    if(decrementAndGet == 0) showData()
                }
            })
        }
    }


    private fun showData(){
        mOrdersList.sortByDescending { it.placed }
        mecOrderHistoryService.getDateOrderMap(dateOrdersMap,mOrdersList)
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
            mecOrderHistoryViewModel.ecsOrderHistory.observe(this, orderHistoryObserver)
            mecOrderHistoryViewModel.mecError.observe(this, this)

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

    override fun onStart() {
        super.onStart()
        setTitleAndBackButtonVisibility(R.string.mec_my_orders, true)
        setCartIconVisibility(false)
        MECAnalytics.trackPage(orderHistory)
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
        showErrorDialog(mecError)

    }


    fun shouldFetchNextPage(): Boolean {

        if (!isCallOnProgress) {
            val lay = binding.dateRecyclerView.layoutManager as LinearLayoutManager

            if (isScrollDown(lay)) {
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

        var errorString = ""
        if (mecError != null) {
            context?.let { errorString = MECutility.getErrorString(mecError, it) }
        }

        context?.let {
            fragmentManager?.let { it1 ->
                context?.getString(R.string.mec_ok)?.let { it2 ->
                    MECutility.showPositiveActionDialog(it, it2, context?.getString(R.string.mec_orders)?: "", errorString, it1, object : AlertListener {
                        override fun onPositiveBtnClick() {
                            exitMEC()
                        }
                    })
                }
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

    private fun isScrollDown(lay: LinearLayoutManager): Boolean {
        val visibleItemCount: Int = lay.childCount
        val totalItemCount: Int = lay.itemCount
        val pastVisibleItems: Int = lay.findFirstVisibleItemPosition()
        return (pastVisibleItems + visibleItemCount >= totalItemCount)
    }
}