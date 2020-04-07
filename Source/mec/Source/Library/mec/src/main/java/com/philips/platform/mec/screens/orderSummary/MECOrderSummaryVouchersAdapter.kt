/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.orderSummary


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.philips.cdp.di.ecs.model.cart.AppliedVoucherEntity
import com.philips.platform.mec.databinding.MecOrderSummaryVoucherItemBinding

class MECOrderSummaryVouchersAdapter(private val voucherList: MutableList<AppliedVoucherEntity>): RecyclerView.Adapter<RecyclerView.ViewHolder>()  {


    private lateinit var voucher: AppliedVoucherEntity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MECOrderSummaryVoucherHolder(MecOrderSummaryVoucherItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return voucherList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        voucher = voucherList.get(position)
        val mecVoucherHolder = holder as MECOrderSummaryVoucherHolder
        mecVoucherHolder.bind(voucher)
    }

}