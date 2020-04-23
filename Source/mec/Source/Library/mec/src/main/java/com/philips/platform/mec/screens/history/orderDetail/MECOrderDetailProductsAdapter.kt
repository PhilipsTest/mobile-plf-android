/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.history.orderDetail


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.philips.platform.ecs.model.cart.ECSShoppingCart
import com.philips.platform.ecs.model.orders.ECSOrderDetail
import com.philips.platform.mec.databinding.MecOrderDetailCartItemsBinding
import com.philips.platform.mec.databinding.MecOrderSummaryCartItemsBinding
import com.philips.platform.mec.screens.orderSummary.MECOrderSummaryViewHolder


class MECOrderDetailProductsAdapter(private val mecCart: ECSOrderDetail?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MECOrderDetailViewHolder(MecOrderDetailCartItemsBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return mecCart!!.entries.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val cartSummary = mecCart!!.entries[position]
        val viewHolder = holder as MECOrderDetailViewHolder
        viewHolder.bind(cartSummary)
    }

}

