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

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.philips.platform.ecs.model.orders.ECSOrders
import com.philips.platform.mec.common.ItemClickListener
import com.philips.platform.mec.databinding.MecItemOrdersRecyclerBinding

class OrdersViewHolder(val binding: ViewDataBinding, val itemClickListener: ItemClickListener) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ECSOrders) {
        val mecOrderHistoryItemBinding = binding as MecItemOrdersRecyclerBinding

        val mLayoutManager = object : LinearLayoutManager(binding.root.context) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        mecOrderHistoryItemBinding.recyclerProductDetail.layoutManager = mLayoutManager
        mecOrderHistoryItemBinding.ecsOrders = item
        mecOrderHistoryItemBinding.service = MECOrderHistoryService()
        mecOrderHistoryItemBinding.itemClickListener = itemClickListener

        mecOrderHistoryItemBinding.tvNoDetail.setOnClickListener { itemClickListener.onItemClick(item) }

    }
}