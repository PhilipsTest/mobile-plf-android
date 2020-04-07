/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.catalog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.philips.platform.mec.common.ItemClickListener
import com.philips.platform.mec.databinding.MecProductCatalogItemGridBinding
import com.philips.platform.mec.databinding.MecProductCatalogItemListBinding


class MECProductCatalogAdapter(items: MutableList<MECProductReview>, private val itemClickListener : ItemClickListener) :MECProductCatalogBaseAbstractAdapter(items) {

    private lateinit var binding: ViewDataBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MECProductCatalogAbstractViewHolder {


        val inflater = LayoutInflater.from(parent.context)
        if(catalogView == CatalogView.LIST){
            binding = MecProductCatalogItemListBinding.inflate(inflater)
        }else{
            binding = MecProductCatalogItemGridBinding.inflate(inflater)
        }

        return MECProductCatalogHolder( binding ,itemClickListener!!)
    }


}