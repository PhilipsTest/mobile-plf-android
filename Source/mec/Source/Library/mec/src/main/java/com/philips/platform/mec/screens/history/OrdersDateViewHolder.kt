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

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.philips.platform.mec.common.ItemClickListener
import com.philips.platform.mec.databinding.MecItemDateRecyclerBinding

class OrdersDateViewHolder(val binding: ViewDataBinding, val itemClickListener: ItemClickListener) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: MECDateOrdersHolder) {
        val mecOrderHistoryItemBinding = binding as MecItemDateRecyclerBinding

        val mLayoutManager = object : LinearLayoutManager(binding.root.context) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        mecOrderHistoryItemBinding.recyclerOrders.layoutManager = mLayoutManager
        mecOrderHistoryItemBinding.itemClickListener = itemClickListener
        mecOrderHistoryItemBinding.mecOrdersHolder = item
    }
}