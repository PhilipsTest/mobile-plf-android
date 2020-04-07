/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.specification

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.philips.platform.mec.databinding.MecSpecItemChildBinding
import com.philips.cdp.prxclient.datamodels.specification.CsItemItem

class SpecificationChildViewHolder( val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root){

     fun bind(item: CsItemItem) {
         val mecSpecItemChildBinding = binding as MecSpecItemChildBinding
         mecSpecItemChildBinding.csItemItem = item
     }
}