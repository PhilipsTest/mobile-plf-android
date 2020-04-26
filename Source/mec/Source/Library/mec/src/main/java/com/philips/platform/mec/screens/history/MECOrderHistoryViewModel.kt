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

import androidx.lifecycle.MutableLiveData
import com.philips.platform.ecs.error.ECSError
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.model.oauth.ECSOAuthData
import com.philips.platform.ecs.model.orders.ECSOrderHistory
import com.philips.platform.ecs.model.orders.ECSOrders
import com.philips.platform.mec.auth.HybrisAuth
import com.philips.platform.mec.common.CommonViewModel
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECDataHolder

class MECOrderHistoryViewModel : CommonViewModel() , ECSCallback<ECSOAuthData, Exception> {

    private var ecsService = MECDataHolder.INSTANCE.eCSServices
    var mecOrderHistoryRepository = MECOrderHistoryRepository(ecsService)
    var ecsOrderHistoryCallback = ECSOrderHistoryCallback(this)
    var ecsOrderDetailForOrdersCallback = ECSOrderDetailForOrdersCallback(this)
    var mECOrderHistoryService = MECOrderHistoryService()

    var ecsOrderHistory = MutableLiveData<ECSOrderHistory>()

    var ecsOrders = MutableLiveData<ECSOrders>()

    var mPageNumber = 0
    var mPageSize = 20
    var mECSOrders : ECSOrders? = null

    var callCount :Int = 0

    fun setThreadCount(threadCount:Int) {
        callCount = threadCount
    }

    fun countDownThread() {
         callCount --
    }

    fun retryAPI(mECRequestType: MECRequestType) {

        var retryAPI = selectAPIcall(mECRequestType)
        authAndCallAPIagain(retryAPI,authFailCallback)
    }

    fun selectAPIcall(mECRequestType: MECRequestType): () -> Unit {

        lateinit  var APIcall: () -> Unit
        when(mECRequestType) {
            MECRequestType.MEC_FETCH_ORDER_HISTORY  -> APIcall = { fetchOrderHistory(mPageNumber,mPageSize) }
            MECRequestType.MEC_FETCH_ORDER_DETAILS_FOR_ORDERS  -> APIcall = { mECSOrders?.let { fetchOrderDetail(it) } }
        }
        return APIcall
    }

    fun fetchOrderHistory(pageNumber : Int, pageSize :Int) {
        mPageNumber=pageNumber
        mPageSize = pageSize
        if(mECOrderHistoryService.shouldCallAuth()) {
            HybrisAuth.hybrisAuthentication(this)
        }else{
            mecOrderHistoryRepository.fetchOrderHistory(pageNumber, pageSize, ecsOrderHistoryCallback)
        }
    }



    fun fetchOrderDetail(ecsOrders : ECSOrders){
        mECSOrders = ecsOrders
        mecOrderHistoryRepository.fetchOrderDetail(mECSOrders!!,ecsOrderDetailForOrdersCallback )
    }

    override fun onResponse(result: ECSOAuthData?) {
        fetchOrderHistory(mPageNumber,mPageSize)
    }

    override fun onFailure(error: Exception?, ecsError: ECSError?) {
        val error = MecError(error, ecsError, MECRequestType.MEC_HYBRIS_AUTH)
        mecError.value = error
    }


}