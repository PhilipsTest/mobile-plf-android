/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.address

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.philips.cdp.di.ecs.model.address.ECSAddress
import com.philips.platform.mec.databinding.MecAddressCardBinding

class AddressBottomSheetViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root){

    fun bind(ecsAddress: ECSAddress) {
        val mecAddressCardBinding = binding as MecAddressCardBinding
        mecAddressCardBinding.ecsAddress = ecsAddress

      //  mecAddressCardBinding.mecAddressCardView.cardBackgroundColor
    }
}