/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.orderSummary


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.philips.cdp.di.ecs.model.cart.ECSShoppingCart
import com.philips.platform.mec.databinding.MecOrderSummaryCartItemsBinding


class MECOrderSummaryProductsAdapter(private val mecCart: ECSShoppingCart) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MECOrderSummaryViewHolder(MecOrderSummaryCartItemsBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return mecCart.entries.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val cartSummary = mecCart.entries[position]
        val viewHolder = holder as MECOrderSummaryViewHolder
        viewHolder.bind(cartSummary)
    }

}

