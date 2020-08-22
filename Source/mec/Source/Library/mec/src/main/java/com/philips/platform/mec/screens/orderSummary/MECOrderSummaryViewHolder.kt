/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.orderSummary


import androidx.recyclerview.widget.RecyclerView
import com.philips.platform.ecs.microService.model.cart.ECSItem
import com.philips.platform.ecs.model.cart.ECSEntries
import com.philips.platform.mec.databinding.MecOrderSummaryCartItemsBinding


class MECOrderSummaryViewHolder(val binding: MecOrderSummaryCartItemsBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(ecsItem: ECSItem) {
        binding.ecsItem = ecsItem
    }

}
