/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.features

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.philips.platform.mec.databinding.MecFeaturesItemParentBinding
import com.philips.cdp.prxclient.datamodels.features.FeaturesModel

class ProductFeatureParentRecyclerAdapter (private val featuresModel:FeaturesModel) : RecyclerView.Adapter<ProductFeatureParentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ProductFeatureParentViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = MecFeaturesItemParentBinding.inflate(inflater)
        return ProductFeatureParentViewHolder(binding,featuresModel)
    }


    override fun getItemCount(): Int {
        return featuresModel.data.keyBenefitArea.size
    }

    override fun onBindViewHolder(viewHolder : ProductFeatureParentViewHolder, position: Int) {
        viewHolder.bind(featuresModel.data.keyBenefitArea[position])
    }
}