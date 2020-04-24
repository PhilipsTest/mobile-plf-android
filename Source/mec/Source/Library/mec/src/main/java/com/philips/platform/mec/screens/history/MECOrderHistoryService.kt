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

import androidx.recyclerview.widget.LinearLayoutManager
import com.philips.platform.ecs.model.orders.ECSOrderDetail
import com.philips.platform.ecs.model.orders.ECSOrders
import com.philips.platform.ecs.util.ECSConfiguration
import com.philips.platform.mec.utils.MECConstant
import com.philips.platform.mec.utils.MECLog
import com.philips.platform.mec.utils.MECutility
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MECOrderHistoryService {

    fun isScrollDown(lay: LinearLayoutManager): Boolean {
        val visibleItemCount = lay.childCount
        val firstVisibleItemPosition = lay.findFirstVisibleItemPosition()
        return visibleItemCount + firstVisibleItemPosition >= lay.itemCount && firstVisibleItemPosition >= 0
    }

    fun getFormattedDate(date: String?): String {
        if(date==null) return ""
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        var convertedDate: Date? = null
        try {
            convertedDate = dateFormat.parse(date)
        } catch (e: ParseException) {
            MECLog.d("MECOrderHistoryService", e.message)
            return ""
        }
        val sdf = SimpleDateFormat("EEEE MMM dd, yyyy") // Set your date format
        return sdf.format(convertedDate)
    }

     fun shouldCallAuth() = !MECutility.isExistingUser() || ECSConfiguration.INSTANCE.accessToken == null

    fun getDateOrderMap(dateOrdersMap: LinkedHashMap<String, MutableList<ECSOrders>>, ordersList: MutableList<ECSOrders>) {

        for(ecsOrders in ordersList){
            val formattedDate = getFormattedDate(ecsOrders.placed)
            if(dateOrdersMap.containsKey(formattedDate)){
                dateOrdersMap[formattedDate]?.add(ecsOrders)
            }else{

                var ordersListValueForMap = mutableListOf<ECSOrders>()
                ordersListValueForMap.add(ecsOrders)
                dateOrdersMap[formattedDate] = ordersListValueForMap
            }
        }
    }

    fun isAllDetailsHaveFetched(ordersList: MutableList<ECSOrders>): Boolean {

        for (orders in ordersList){
            val detailsNotFetched = orders.orderDetail == null
            if(detailsNotFetched) return detailsNotFetched
        }
        return true
    }

    fun handleOrderDetailFetchFailed(ecsOrders: ECSOrders) {
        val ecsOrderDetail = ECSOrderDetail()
        ecsOrderDetail.code = MECConstant.MEC_DETAIL_NOT_AVAILABLE
        ecsOrders.orderDetail =  ecsOrders.orderDetail ?: ecsOrderDetail
    }

}