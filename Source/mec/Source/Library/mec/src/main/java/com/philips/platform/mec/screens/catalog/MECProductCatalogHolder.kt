/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.catalog

import android.view.View
import androidx.databinding.ViewDataBinding
import com.philips.platform.mec.common.ItemClickListener
import com.philips.platform.mec.databinding.MecProductCatalogItemGridBinding
import com.philips.platform.mec.databinding.MecProductCatalogItemListBinding

class MECProductCatalogHolder(override var binding: ViewDataBinding, override var itemClickListener: ItemClickListener) : MECProductCatalogAbstractViewHolder(binding, itemClickListener) {

    override fun bind(item: MECProductReview) {
        super.bind(item)

        if(binding is  MecProductCatalogItemListBinding) {
            val mecProductCatalogItemListBinding = binding as MecProductCatalogItemListBinding
            mecProductCatalogItemListBinding.product = item
            mecProductCatalogItemListBinding.executePendingBindings()
        }else{
            val mecProductCatalogItemGridBinding = binding as MecProductCatalogItemGridBinding
            mecProductCatalogItemGridBinding.product = item
            mecProductCatalogItemGridBinding.executePendingBindings()
            if (adapterPosition % 2 == 0) {
                    (binding as MecProductCatalogItemGridBinding).verticleView.visibility = View.VISIBLE
            } else {
                (binding as MecProductCatalogItemGridBinding).verticleView.visibility = View.GONE
            }
        }
    }
}