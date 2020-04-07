/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.features

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.philips.platform.mec.databinding.MecFeaturesItemChildBinding
import com.philips.cdp.prxclient.datamodels.features.FeatureItem

class ProductFeatureChildViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root){

     fun bind(item: FeatureItem) {

         val mecFeatureItemChildBinding = binding as MecFeaturesItemChildBinding
         mecFeatureItemChildBinding.featureItem = item
     }
}