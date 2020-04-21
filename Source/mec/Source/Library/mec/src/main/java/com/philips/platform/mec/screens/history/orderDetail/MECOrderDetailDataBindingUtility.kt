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

package com.philips.platform.mec.screens.history.orderDetail

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.philips.cdp.di.ecs.model.orders.ECSOrders
import com.philips.platform.mec.common.ItemClickListener
import com.philips.platform.mec.screens.history.MECOrderDetailAdapter

class MECOrderDetailDataBindingUtility {

    companion object {

        @JvmStatic
        @BindingAdapter("ecsOrders","itemClickListener")
        fun setOrderDetailAdapter(recyclerView: RecyclerView, ecsOrders: ECSOrders?,itemClickListener: ItemClickListener) {
            if(ecsOrders?.orderDetail != null && !ecsOrders.orderDetail.entries.isEmpty())
                recyclerView.adapter = MECOrderDetailAdapter(ecsOrders,itemClickListener)
        }
    }
}