/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.history.orderDetail


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.philips.platform.ecs.model.orders.ECSOrderDetail
import com.philips.platform.mec.common.ItemClickListener
import com.philips.platform.mec.databinding.MecOrderDetailCartItemsBinding


class MECOrderDetailProductsAdapter(private val ecsOrderDetail: ECSOrderDetail, private val itemClickListener: ItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mecOrderDetailService = MECOrderDetailService()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MECOrderDetailViewHolder(MecOrderDetailCartItemsBinding.inflate(LayoutInflater.from(parent.context)),itemClickListener)
    }

    override fun getItemCount(): Int {
        return ecsOrderDetail.entries.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val trackURLstring = mecOrderDetailService.getOrderTrackUrl(mecOrderDetailService.getEntriesFromConsignMent(ecsOrderDetail, ecsOrderDetail.entries[position].product.code))
        val entries = ecsOrderDetail.entries[position]
        val viewHolder = holder as MECOrderDetailViewHolder
        viewHolder.bind(entries,trackURLstring)
    }



}

