/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.retailers

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.philips.cdp.di.ecs.model.retailers.ECSRetailer
import com.philips.platform.mec.common.ItemClickListener
import com.philips.platform.mec.databinding.MecRetailersItemBinding

class MECRetailersAdapter (private val mecRetailers: List<ECSRetailer>? ,val itemClickListener: ItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(MecRetailersItemBinding.inflate(LayoutInflater.from(parent.context)),itemClickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val retailer = mecRetailers?.get(position)
        val viewHolder = holder as ViewHolder
        viewHolder.bind(retailer!!)
    }

    private class ViewHolder(val binding: MecRetailersItemBinding , val itemClickListener: ItemClickListener) : RecyclerView.ViewHolder(binding.root){

        open fun bind(retailer: ECSRetailer) {
            binding.mecRetailer = retailer
            binding.itemClickListener = itemClickListener

            if (TextUtils.isDigitsOnly(retailer.philipsOnlinePrice)) {
                binding.mecRetailerItemPrice.visibility = View.GONE
            } else {
                binding.mecRetailerItemPrice.visibility = View.VISIBLE
            }

        }
    }

    override fun getItemCount(): Int {
        return mecRetailers?.size!!
    }

}