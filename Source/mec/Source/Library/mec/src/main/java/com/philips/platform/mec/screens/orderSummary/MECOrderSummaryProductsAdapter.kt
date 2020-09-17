/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.orderSummary


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.philips.platform.ecs.microService.model.cart.ECSShoppingCart
import com.philips.platform.mec.databinding.MecOrderSummaryCartItemsBinding


class MECOrderSummaryProductsAdapter(private val mecCart: ECSShoppingCart) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MECOrderSummaryViewHolder(MecOrderSummaryCartItemsBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return mecCart.data?.attributes?.items?.size ?:0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val cartSummary = mecCart.data?.attributes?.items?.get(position)
        val viewHolder = holder as MECOrderSummaryViewHolder
        cartSummary?.let { viewHolder.bind(it) }
    }

}

