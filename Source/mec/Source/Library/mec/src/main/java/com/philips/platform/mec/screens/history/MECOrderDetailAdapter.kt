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
package com.philips.platform.mec.screens.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.philips.platform.mec.databinding.MecOrderEntriesBinding

class MECOrderDetailAdapter (val items: MECEntriesModel) : RecyclerView.Adapter<OrderDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): OrderDetailViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = MecOrderEntriesBinding.inflate(inflater)
        return OrderDetailViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return items.entriesList.size
    }

    override fun onBindViewHolder(viewHolder : OrderDetailViewHolder, position: Int) {
        viewHolder.bind(items.entriesList[position])
    }
}