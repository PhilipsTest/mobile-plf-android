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
import com.philips.platform.ecs.model.orders.ECSOrders
import com.philips.platform.ecs.util.ECSConfiguration
import com.philips.platform.mec.utils.MECLog
import com.philips.platform.mec.utils.MECutility
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MECOrderHistoryService {

    fun isScrollDown(lay: LinearLayoutManager): Boolean {


        val visibleItemCount: Int = lay.childCount
        val totalItemCount: Int = lay.itemCount
        val pastVisibleItems: Int = lay.findFirstVisibleItemPosition()
        return (pastVisibleItems + visibleItemCount >= totalItemCount)
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

}