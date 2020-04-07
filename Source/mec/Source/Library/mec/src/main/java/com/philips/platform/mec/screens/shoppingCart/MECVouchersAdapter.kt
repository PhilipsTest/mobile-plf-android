/* Copyright (c) Koninklijke Philips N.V., 2020
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.mec.screens.shoppingCart


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.philips.cdp.di.ecs.model.cart.AppliedVoucherEntity
import com.philips.platform.mec.common.ItemClickListener
import com.philips.platform.mec.databinding.MecVoucherItemBinding

class MECVouchersAdapter(private val voucherList: MutableList<AppliedVoucherEntity>,private val itemClickListener : ItemClickListener): RecyclerView.Adapter<RecyclerView.ViewHolder>()  {


    private lateinit var voucher: AppliedVoucherEntity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MECVoucherHolder(MecVoucherItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return voucherList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        voucher = voucherList.get(position)
        val mecVoucherHolder = holder as MECVoucherHolder
        mecVoucherHolder.bind(voucher,itemClickListener)

    }

    fun getVoucher(): AppliedVoucherEntity {
        return voucher
    }


}