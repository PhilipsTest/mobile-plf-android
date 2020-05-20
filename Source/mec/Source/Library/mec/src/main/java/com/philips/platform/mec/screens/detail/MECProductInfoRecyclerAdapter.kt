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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.philips.platform.ecs.model.products.ECSProduct
import com.philips.platform.mec.databinding.MecProductInfoItemBinding


class MECProductInfoRecyclerAdapter (private val items:  MutableList<ECSProduct>) : RecyclerView.Adapter<MECProductInfoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): MECProductInfoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MecProductInfoItemBinding.inflate(inflater)
        return MECProductInfoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(viewHolder : MECProductInfoViewHolder, position: Int) {
       viewHolder.bind(items[position])
    }
}