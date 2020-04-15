/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.features

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.philips.platform.mec.databinding.MecFeaturesItemChildBinding
import com.philips.cdp.prxclient.datamodels.features.FeatureItem


class ProductFeatureChildRecyclerAdapter(private val featureItems: List<FeatureItem>) : RecyclerView.Adapter<ProductFeatureChildViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ProductFeatureChildViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MecFeaturesItemChildBinding.inflate(inflater)
        return ProductFeatureChildViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return featureItems.size
    }

    override fun onBindViewHolder(viewHolder : ProductFeatureChildViewHolder, position: Int) {
       viewHolder.bind(featureItems[position])
    }
}