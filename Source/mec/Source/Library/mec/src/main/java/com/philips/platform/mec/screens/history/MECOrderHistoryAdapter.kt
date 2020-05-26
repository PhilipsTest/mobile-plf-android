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
import com.philips.platform.mec.databinding.MecItemDateRecyclerBinding

class MECOrderHistoryAdapter(val dateMap: LinkedHashMap<String, MutableList<ECSOrders>>, val itemClickListener: ItemClickListener) : RecyclerView.Adapter<OrdersDateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): OrdersDateViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = MecItemDateRecyclerBinding.inflate(inflater)
        return OrdersDateViewHolder(binding,itemClickListener)
    }


    override fun getItemCount(): Int {
        return dateMap.size
    }

    override fun onBindViewHolder(viewHolder : OrdersDateViewHolder, position: Int) {


        val date = dateMap.keys.toTypedArray()[position]
        val ecsOrdersList = dateMap.values.toTypedArray()[position]
        val mecDateOrdersHolder = MECDateOrdersHolder(date,ecsOrdersList)
        viewHolder.bind(mecDateOrdersHolder)
    }
}