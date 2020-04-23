/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.history.orderDetail


import androidx.recyclerview.widget.RecyclerView
import com.philips.platform.ecs.model.cart.AppliedVoucherEntity
import com.philips.platform.ecs.model.voucher.ECSVoucher
import com.philips.platform.mec.databinding.MecOrderDetailVoucherItemBinding
import com.philips.platform.mec.databinding.MecOrderSummaryVoucherItemBinding

class MECOrderDetailVoucherHolder(val binding: MecOrderDetailVoucherItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(voucher: ECSVoucher) {
        binding.voucher = voucher
    }
}