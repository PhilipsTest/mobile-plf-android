/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.history.orderDetail


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.philips.platform.ecs.model.cart.AppliedVoucherEntity
import com.philips.platform.ecs.model.voucher.ECSVoucher
import com.philips.platform.mec.databinding.MecOrderDetailVoucherItemBinding

class MECOrderDetailVouchersAdapter(private val voucherList: MutableList<ECSVoucher>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private lateinit var voucher: ECSVoucher

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MECOrderDetailVoucherHolder(MecOrderDetailVoucherItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return voucherList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        voucher = voucherList[position]
        val mecVoucherHolder = holder as MECOrderDetailVoucherHolder
        mecVoucherHolder.bind(voucher)
    }

}