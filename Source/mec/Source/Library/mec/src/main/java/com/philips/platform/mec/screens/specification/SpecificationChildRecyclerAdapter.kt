/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.specification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.philips.platform.mec.databinding.MecSpecItemChildBinding
import com.philips.cdp.prxclient.datamodels.specification.CsItemItem


class SpecificationChildRecyclerAdapter (private val csItem: List<CsItemItem>) : RecyclerView.Adapter<SpecificationChildViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): SpecificationChildViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MecSpecItemChildBinding.inflate(inflater)
        return SpecificationChildViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return csItem.size
    }

    override fun onBindViewHolder(viewHolder : SpecificationChildViewHolder, position: Int) {
       viewHolder.bind(csItem[position])
    }
}