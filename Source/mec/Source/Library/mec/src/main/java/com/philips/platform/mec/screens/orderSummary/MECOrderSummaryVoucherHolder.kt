/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.orderSummary


import androidx.recyclerview.widget.RecyclerView
import com.philips.cdp.di.ecs.model.cart.AppliedVoucherEntity
import com.philips.platform.mec.databinding.MecOrderSummaryVoucherItemBinding

class MECOrderSummaryVoucherHolder(val binding: MecOrderSummaryVoucherItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(voucher: AppliedVoucherEntity) {
        binding.voucher = voucher
    }
}