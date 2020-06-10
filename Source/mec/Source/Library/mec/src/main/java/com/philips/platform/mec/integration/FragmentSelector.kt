/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.integration

import android.os.Bundle
import com.philips.platform.ecs.model.products.ECSProduct
import com.philips.platform.mec.screens.MecBaseFragment
import com.philips.platform.mec.screens.catalog.MECCategorizedRetailerFragment
import com.philips.platform.mec.screens.catalog.MECProductCatalogCategorizedFragment
import com.philips.platform.mec.screens.catalog.MECProductCatalogFragment
import com.philips.platform.mec.screens.detail.MECLandingProductDetailsFragment
import com.philips.platform.mec.screens.history.MECOrderHistoryFragment
import com.philips.platform.mec.screens.shoppingCart.MECShoppingCartFragment
import com.philips.platform.mec.utils.MECConstant
import java.util.*

class FragmentSelector {

    fun getLandingFragment(isHybris: Boolean, mecFlowConfigurator: MECFlowConfigurator? ,bundle: Bundle): MecBaseFragment {
        var fragment: MecBaseFragment = MECProductCatalogFragment()
        if(mecFlowConfigurator==null) return fragment

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
            MECFlowConfigurator.MECLandingView.MEC_ORDER_HISTORY -> {
                fragment = MECOrderHistoryFragment()
            }
        }
        putCtnsToBundle(bundle,mecFlowConfigurator)
        return fragment
    }

    private fun getCategorizedFragment(isHybris: Boolean): MecBaseFragment {
        return if (isHybris) {
            MECProductCatalogCategorizedFragment()
        } else {
            MECCategorizedRetailerFragment()
        }
    }

    private fun putCtnsToBundle(bundle: Bundle, mecFlowConfigurator: MECFlowConfigurator){

        val ctnList: ArrayList<String> = ArrayList()

        if (mecFlowConfigurator.productCTNs != null) {

            for (ctn in mecFlowConfigurator.productCTNs!!) {
                ctnList.add(ctn.replace("_", "/"))
            }
        }

        bundle.putStringArrayList(MECConstant.CATEGORISED_PRODUCT_CTNS, ctnList)

        val ecsProduct = ECSProduct()
        if(ctnList.size !=0) {
            ecsProduct.code = ctnList[0]
        }else{
            ecsProduct.code = ""
        }

        bundle.putSerializable(MECConstant.MEC_KEY_PRODUCT,ecsProduct)
    }
}