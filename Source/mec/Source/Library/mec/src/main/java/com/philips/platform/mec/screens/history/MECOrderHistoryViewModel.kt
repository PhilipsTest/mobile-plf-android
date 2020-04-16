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
import com.philips.cdp.di.ecs.error.ECSError
import com.philips.cdp.di.ecs.integration.ECSCallback
import com.philips.cdp.di.ecs.model.oauth.ECSOAuthData
import com.philips.cdp.di.ecs.model.orders.ECSOrderHistory
import com.philips.cdp.di.ecs.model.orders.ECSOrders
import com.philips.cdp.di.ecs.util.ECSConfiguration
import com.philips.platform.mec.auth.HybrisAuth
import com.philips.platform.mec.common.CommonViewModel
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.mec.utils.MECutility

class MECOrderHistoryViewModel : CommonViewModel() , ECSCallback<ECSOAuthData, Exception> {

    private var ecsService = MECDataHolder.INSTANCE.eCSServices
    private var mecOrderHistoryRepository = MECOrderHistoryRepository(ecsService)
    private var ecsOrderHistoryCallback = ECSOrderHistoryCallback(this)
    private var ecsOrderDetailForOrdersCallback = ECSOrderDetailForOrdersCallback(this)

    var ecsOrderHistory = MutableLiveData<ECSOrderHistory>()

    var mPageNumber = 0
    var mPageSize = 20
    var mECSOrders : ECSOrders? = null

    fun retryAPI(mECRequestType: MECRequestType) {

        var retryAPI = selectAPIcall(mECRequestType)
        authAndCallAPIagain(retryAPI,authFailCallback)
    }

    private fun selectAPIcall(mECRequestType: MECRequestType): () -> Unit {

        lateinit  var APIcall: () -> Unit
        when(mECRequestType) {
            MECRequestType.MEC_FETCH_ORDER_HISTORY  -> APIcall = { fetchOrderSummary(mPageNumber,mPageSize) }
            MECRequestType.MEC_FETCH_ORDER_DETAILS_FOR_ORDERS  -> APIcall = { mECSOrders?.let { fetchOrderDetail(it) } }
        }
        return APIcall
    }

    fun fetchOrderSummary(pageNumber : Int ,pageSize :Int) {
        mPageNumber=pageNumber
        mPageSize = mPageSize
        if(!MECutility.isExistingUser() || ECSConfiguration.INSTANCE.accessToken == null) {
            HybrisAuth.hybrisAuthentication(this)
        }else{
            mecOrderHistoryRepository.fetchOrderSummary(pageNumber, pageSize, ecsOrderHistoryCallback)
        }
    }

     fun fetchOrderDetail(ecsOrders : ECSOrders){
        mECSOrders = ecsOrders
        mecOrderHistoryRepository.fetchOrderDetail(mECSOrders!!,ecsOrderDetailForOrdersCallback )
    }

    override fun onResponse(result: ECSOAuthData?) {
        fetchOrderSummary(mPageNumber,mPageSize)
    }

    override fun onFailure(error: Exception?, ecsError: ECSError?) {
        val error = MecError(error, ecsError, MECRequestType.MEC_HYBRIS_AUTH)
        mecError.value = error
    }


}