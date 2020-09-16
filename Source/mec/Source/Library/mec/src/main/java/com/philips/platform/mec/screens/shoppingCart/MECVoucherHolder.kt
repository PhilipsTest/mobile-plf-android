/* Copyright (c) Koninklijke Philips N.V., 2020
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.mec.screens.shoppingCart


import androidx.recyclerview.widget.RecyclerView
import com.philips.platform.ecs.microService.model.cart.Voucher
import com.philips.platform.mec.common.ItemClickListener
import com.philips.platform.mec.databinding.MecVoucherItemBinding

class MECVoucherHolder(val binding: MecVoucherItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(voucher: Voucher, itemClickListener: ItemClickListener){
        binding.voucher = voucher
        binding.mecCross.setOnClickListener { itemClickListener.onItemClick(voucher as Object) }
    }
}