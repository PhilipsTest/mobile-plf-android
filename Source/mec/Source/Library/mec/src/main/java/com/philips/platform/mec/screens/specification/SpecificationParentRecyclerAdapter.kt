/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.specification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.philips.platform.mec.databinding.MecSpecItemParentBinding
import com.philips.cdp.prxclient.datamodels.specification.SpecificationModel

class SpecificationParentRecyclerAdapter (val items: SpecificationModel) : RecyclerView.Adapter<SpecificationParentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): SpecificationParentViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = MecSpecItemParentBinding.inflate(inflater)
        return SpecificationParentViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return items.data.csChapter.size
    }

    override fun onBindViewHolder(viewHolder : SpecificationParentViewHolder, position: Int) {

        viewHolder.bind(items.data.csChapter.get(position))
    }
}