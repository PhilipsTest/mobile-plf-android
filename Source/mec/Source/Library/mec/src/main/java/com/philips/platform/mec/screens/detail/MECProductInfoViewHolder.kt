/*
 *  Copyright (c) Koninklijke Philips N.V., 2020
 *
 *  * All rights are reserved. Reproduction or dissemination
 *
 *  * in whole or in part is prohibited without the prior written
 *
 *  * consent of the copyright holder.
 *
 *
 */
package com.philips.platform.mec.screens.detail

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.philips.platform.ecs.model.products.ECSProduct
import com.philips.platform.mec.databinding.MecProductInfoItemBinding

class MECProductInfoViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root){

     fun bind(item: ECSProduct) {
         val mecProductInfoItemBinding = binding as MecProductInfoItemBinding
         mecProductInfoItemBinding.product = item
     }
}