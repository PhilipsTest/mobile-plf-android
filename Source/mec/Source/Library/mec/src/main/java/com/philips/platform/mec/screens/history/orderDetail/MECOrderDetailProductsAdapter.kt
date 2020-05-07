/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.history.orderDetail


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.philips.platform.ecs.model.orders.ConsignmentEntries
import com.philips.platform.ecs.model.orders.ECSOrderDetail
import com.philips.platform.mec.databinding.MecOrderDetailCartItemsBinding
import kotlinx.android.synthetic.main.mec_order_detail_cart_items.view.*


class MECOrderDetailProductsAdapter(private val ecsOrderDetail: ECSOrderDetail?, private val mECOrderDetailFragment : MECOrderDetailFragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MECOrderDetailViewHolder(MecOrderDetailCartItemsBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return ecsOrderDetail!!.entries.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val trackURLstring = getOrderTrackUrl(getEntriesFromConsignMent(ecsOrderDetail!!, ecsOrderDetail.entries[position].product.code))
        val entries = ecsOrderDetail!!.entries[position]
        val viewHolder = holder as MECOrderDetailViewHolder
        viewHolder.bind(entries,trackURLstring)
        viewHolder.itemView.mec_order_detail_tracking_btn.setOnClickListener{
            trackURLstring?.let { it1 -> mECOrderDetailFragment.showTrackUrlFragment(it1) }
        }
    }



    // methods to get product corresponding track url fro ECSOrder
    private fun getEntriesFromConsignMent(detail: ECSOrderDetail, ctn: String): ConsignmentEntries? {
        if (detail.consignments == null) return null
        for (consignment in detail.consignments) {
            for (entries in consignment.entries) {
                val consignmentCtn = entries.orderEntry.product.code
                if (ctn.trim { it <= ' ' }.equals(consignmentCtn.trim({ it <= ' ' }), ignoreCase = true)) {
                    return entries
                }
            }
        }
        return null
    }

    private fun getOrderTrackUrl(entries: ConsignmentEntries?): String? {
        if (entries == null) return null
        if (isArrayNullOrEmpty(entries!!.getTrackAndTraceIDs()) || isArrayNullOrEmpty(entries!!.getTrackAndTraceUrls())) {
            return null
        }
        val trackAndTraceID = entries!!.trackAndTraceIDs.get(0)
        val trackAndTraceUrl = entries!!.trackAndTraceUrls.get(0)
        return getTrackUrl(trackAndTraceID, trackAndTraceUrl)
    }

    private fun getTrackUrl(trackAndTraceID: String, trackAndTraceUrl: String): String {
        //sample URL
        //{300068874=http:\/\/www.fedex.com\/Tracking?action=track&cntry_code=us&tracknumber_list=300068874}
        val urlWithEndCurlyBrace = trackAndTraceUrl.replace("{$trackAndTraceID=", "")
        return urlWithEndCurlyBrace.replace("}", "")
    }

    private fun isArrayNullOrEmpty(traceIdOrTraceURL: List<*>?): Boolean {
        return traceIdOrTraceURL == null || traceIdOrTraceURL.size == 0
    }

}

