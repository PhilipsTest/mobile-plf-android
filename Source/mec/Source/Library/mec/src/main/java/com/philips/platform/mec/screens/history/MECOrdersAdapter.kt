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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.philips.platform.ecs.model.orders.ECSOrders
import com.philips.platform.mec.common.ItemClickListener
import com.philips.platform.mec.databinding.MecItemOrdersRecyclerBinding

class MECOrdersAdapter(val items: MutableList<ECSOrders>, val itemClickListener: ItemClickListener) : RecyclerView.Adapter<OrdersViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): OrdersViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = MecItemOrdersRecyclerBinding.inflate(inflater)
        return OrdersViewHolder(binding,itemClickListener)
    }


    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(viewHolder : OrdersViewHolder, position: Int) {
        viewHolder.bind(items[position])
    }
}