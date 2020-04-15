/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.catalog

import androidx.databinding.ViewDataBinding
import com.philips.platform.mec.common.ItemClickListener
import com.philips.platform.mec.databinding.MecProductCatalogItemGridBinding

class MECProductCatalogGridHolder(override val binding: ViewDataBinding, itemClickListener: ItemClickListener) : MECProductCatalogAbstractViewHolder(binding,itemClickListener) {

    override fun bind(item: MECProductReview) {
            super.bind(item)
            val mecProductCatalogItemGridBinding = binding as MecProductCatalogItemGridBinding
            mecProductCatalogItemGridBinding.product = item
            mecProductCatalogItemGridBinding.executePendingBindings()
    }
}