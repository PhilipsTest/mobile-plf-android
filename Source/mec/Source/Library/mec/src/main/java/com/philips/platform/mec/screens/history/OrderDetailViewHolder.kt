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
import androidx.recyclerview.widget.RecyclerView
import com.philips.platform.ecs.model.orders.Entries
import com.philips.platform.mec.databinding.MecItemOrderDetailRecyclerBinding

class OrderDetailViewHolder (val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root){

    fun bind(item: Entries) {
        val mecOrderEntriesBinding = binding as MecItemOrderDetailRecyclerBinding
        mecOrderEntriesBinding.ecsEntries = item
    }
}