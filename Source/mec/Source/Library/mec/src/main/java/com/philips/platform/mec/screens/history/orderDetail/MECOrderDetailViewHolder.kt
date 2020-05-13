/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.history.orderDetail


import androidx.recyclerview.widget.RecyclerView
import com.philips.platform.ecs.model.orders.Entries
import com.philips.platform.mec.common.ItemClickListener
import com.philips.platform.mec.databinding.MecOrderDetailCartItemsBinding


class MECOrderDetailViewHolder(val binding: MecOrderDetailCartItemsBinding, val itemClickListener: ItemClickListener) : RecyclerView.ViewHolder(binding.root) {

    fun bind(orderEntries: Entries, url: String?) {
        binding.entries = orderEntries
        binding.trackUrl = url
        binding.mecOrderDetailTrackingBtn.setOnClickListener {
            url?.let { itemClickListener.onItemClick(url as String) }
        }
    }

}
