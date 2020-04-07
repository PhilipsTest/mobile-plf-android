/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.features

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.philips.platform.mec.databinding.MecFeaturesItemParentBinding
import com.philips.cdp.prxclient.datamodels.features.FeaturesModel
import com.philips.cdp.prxclient.datamodels.features.KeyBenefitAreaItem


class ProductFeatureParentViewHolder (val binding: ViewDataBinding, val featuresModel: FeaturesModel) : RecyclerView.ViewHolder(binding.root){

    fun bind(item: KeyBenefitAreaItem) {

        val mecFeaturesItemParentBinding = binding as MecFeaturesItemParentBinding

        //Disabling child recycler view scroll
        val mLayoutManager = object : LinearLayoutManager(binding.root.context) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        mecFeaturesItemParentBinding.recyclerChild.layoutManager = mLayoutManager
        mecFeaturesItemParentBinding.keyBenefitAreaItem = item
        mecFeaturesItemParentBinding.featureModel = featuresModel
    }
}