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
import com.philips.platform.mec.databinding.MecOrderDetailCartItemsBinding
import kotlinx.android.synthetic.main.mec_order_detail_cart_items.view.*


class MECOrderDetailProductsAdapter(private val ecsOrderDetail: ECSOrderDetail?, private val mECOrderDetailFragment : MECOrderDetailFragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mecOrderDetailService = MECOrderDetailService()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MECOrderDetailViewHolder(MecOrderDetailCartItemsBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return ecsOrderDetail!!.entries.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val trackURLstring = mecOrderDetailService.getOrderTrackUrl(mecOrderDetailService.getEntriesFromConsignMent(ecsOrderDetail!!, ecsOrderDetail.entries[position].product.code))
        val entries = ecsOrderDetail.entries[position]
        val viewHolder = holder as MECOrderDetailViewHolder
        viewHolder.bind(entries,trackURLstring)
        viewHolder.itemView.mec_order_detail_tracking_btn.setOnClickListener{
            trackURLstring?.let { it1 -> mECOrderDetailFragment.showTrackUrlFragment(it1) }
        }
    }




}

