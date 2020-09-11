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

package com.philips.platform.mec.screens.catalog

import com.philips.platform.ecs.microService.model.filter.ProductFilter
import com.philips.platform.ecs.microService.model.product.ECSProduct
import java.util.ArrayList

class MECProductCatalogService {

    fun getCategorizedProducts(categorizedCtns: ArrayList<String>?, commerceProducts: List<ECSProduct>): List<ECSProduct> {
        val categorizedProducts = mutableListOf<ECSProduct>()
        categorizedCtns?.let {
            for (commerceProduct in commerceProducts){
                if(categorizedCtns.contains(commerceProduct.ctn)){
                    categorizedProducts.add(commerceProduct)
                }
            }
        }
        return categorizedProducts
    }

    fun isNoFilterApplied(filter: ProductFilter) : Boolean{
        return filter.sortType == null && filter.stockLevelSet?.isEmpty() == true
    }

    fun isSameDataClass(originalProductFilter: ProductFilter,modifiedProductFilter: ProductFilter) : Boolean{
        return originalProductFilter == modifiedProductFilter
    }

}