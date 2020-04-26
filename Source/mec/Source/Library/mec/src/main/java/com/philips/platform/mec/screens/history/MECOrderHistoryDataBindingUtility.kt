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

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.philips.platform.ecs.model.orders.ECSOrders
import com.philips.platform.mec.common.ItemClickListener


class MECOrderHistoryDataBindingUtility {

    companion object {

        @JvmStatic
        @BindingAdapter("ecsOrders","itemClickListener")
        fun setOrderDetailAdapter(recyclerView: RecyclerView, ecsOrders: ECSOrders?, itemClickListener: ItemClickListener) {
            if(ecsOrders?.orderDetail != null && !ecsOrders.orderDetail.entries.isNullOrEmpty())
                recyclerView.adapter = MECOrderDetailAdapter(ecsOrders,itemClickListener)
        }

        @JvmStatic
        @BindingAdapter("ecsOrdersList","ordersClickListener")
        fun setOrdersAdapter(recyclerView: RecyclerView, ecsOrdersList: MutableList<ECSOrders>, itemClickListener: ItemClickListener) {

            if(!ecsOrdersList.isNullOrEmpty()){
                val mecOrdersAdapter = MECOrdersAdapter(ecsOrdersList, itemClickListener)
                recyclerView.adapter = mecOrdersAdapter
            }

        }
    }
}