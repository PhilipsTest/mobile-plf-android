/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.catalog

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.philips.platform.mec.common.ItemClickListener

abstract class MECProductCatalogAbstractViewHolder(open val binding: ViewDataBinding, open val itemClickListener: ItemClickListener) : RecyclerView.ViewHolder(binding.root) {

    open fun bind(item: MECProductReview){

        binding.root.setOnClickListener { itemClickListener.onItemClick(item as Object) }
    }
}