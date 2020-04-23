/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.history.orderDetail


import androidx.recyclerview.widget.RecyclerView
import com.philips.platform.ecs.model.cart.ECSEntries
import com.philips.platform.ecs.model.orders.Entries
import com.philips.platform.mec.databinding.MecOrderDetailCartItemsBinding
import com.philips.platform.mec.databinding.MecOrderSummaryCartItemsBinding


class MECOrderDetailViewHolder(val binding: MecOrderDetailCartItemsBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(orderEntries: Entries) {
        binding.entries = orderEntries
    }

}
