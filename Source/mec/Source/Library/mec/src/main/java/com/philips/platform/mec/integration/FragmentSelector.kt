/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.integration

import android.os.Bundle
import com.philips.cdp.di.ecs.model.products.ECSProduct
import com.philips.platform.mec.screens.MecBaseFragment
import com.philips.platform.mec.screens.catalog.MECCategorizedRetailerFragment
import com.philips.platform.mec.screens.catalog.MECProductCatalogCategorizedFragment
import com.philips.platform.mec.screens.catalog.MECProductCatalogFragment
import com.philips.platform.mec.screens.detail.MECLandingProductDetailsFragment
import com.philips.platform.mec.screens.shoppingCart.MECShoppingCartFragment
import com.philips.platform.mec.utils.MECConstant
import java.util.*

class FragmentSelector {

    fun getLandingFragment(isHybris: Boolean, mecFlowConfigurator: MECFlowConfigurator ,bundle: Bundle): MecBaseFragment? {
        var fragment: MecBaseFragment? = null

        when (mecFlowConfigurator.landingView) {


            MECFlowConfigurator.MECLandingView.MEC_PRODUCT_DETAILS_VIEW -> {
                fragment = MECLandingProductDetailsFragment()

            }

            MECFlowConfigurator.MECLandingView.MEC_SHOPPING_CART_VIEW -> {
                fragment = MECShoppingCartFragment()

            }

            MECFlowConfigurator.MECLandingView.MEC_PRODUCT_LIST_VIEW -> {
                fragment = MECProductCatalogFragment()
            }
            MECFlowConfigurator.MECLandingView.MEC_CATEGORIZED_PRODUCT_LIST_VIEW -> {
                fragment = getCategorizedFragment(isHybris)
            }
        }
        putCtnsToBundle(bundle,mecFlowConfigurator)
        return fragment
    }

    private fun getCategorizedFragment(isHybris: Boolean): MecBaseFragment? {
        if (isHybris) {
            return MECProductCatalogCategorizedFragment()
        } else {
            return MECCategorizedRetailerFragment()
        }
    }

    private fun putCtnsToBundle(bundle: Bundle, mecFlowConfigurator: MECFlowConfigurator){

        var ctnList: ArrayList<String>? = ArrayList()

        if (mecFlowConfigurator.productCTNs != null) {

            for (ctn in mecFlowConfigurator.productCTNs!!) {
                ctnList?.add(ctn.replace("_", "/"))
            }
        }

        bundle?.putStringArrayList(MECConstant.CATEGORISED_PRODUCT_CTNS, ctnList)

        val ecsProduct = ECSProduct()
        if(ctnList?.size !=0) {
            ecsProduct.code = ctnList?.get(0) ?: ""
        }else{
            ecsProduct.code = ""
        }

        bundle?.putSerializable(MECConstant.MEC_KEY_PRODUCT,ecsProduct)

    }
}