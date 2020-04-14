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
import com.philips.cdp.di.ecs.model.orders.ECSOrderHistory
import com.philips.platform.mec.common.CommonViewModel
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.utils.MECDataHolder

class MECOrderHistoryViewModel : CommonViewModel() {

    var ecsService = MECDataHolder.INSTANCE.eCSServices
    var mecOrderHistoryRepository = MECOrderHistoryRepository(ecsService)
    var ecsOrderHistoryCallback = ECSOrderHistoryCallback(this)

    var ecsOrderHistory = MutableLiveData<ECSOrderHistory>()

    var mPageNumber = 0
    var mPageSize = 20

    fun retryAPI(mECRequestType: MECRequestType) {

        var retryAPI = selectAPIcall(mECRequestType)
        authAndCallAPIagain(retryAPI,authFailCallback)
    }

    private fun selectAPIcall(mECRequestType: MECRequestType): () -> Unit {

        lateinit  var APIcall: () -> Unit
        when(mECRequestType) {
            MECRequestType.MEC_FETCH_ORDER_HISTORY  -> APIcall = { fetchOrderSummary(mPageNumber,mPageSize) }
        }
        return APIcall
    }

    fun fetchOrderSummary(pageNumber : Int ,pageSize :Int) {
        mPageNumber=pageNumber
        mPageSize = mPageSize
        mecOrderHistoryRepository.fetchOrderSummary(pageNumber, pageSize, ecsOrderHistoryCallback)
    }


}