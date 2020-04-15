/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.catalog
import android.view.View
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECConstant
import kotlinx.android.synthetic.main.mec_catalog_fragment.*

class MECCategorizedRetailerFragment : MECProductCatalogFragment(){

    override fun getFragmentTag(): String {
        return "MECCategorizedRetailerFragment"
    }

    companion object {
        val TAG:String="MECCategorizedRetailerFragment"
    }

    override fun isPaginationSupported(): Boolean {
        return false
    }


    override fun executeRequest(){
        ecsProductViewModel.initCategorizedRetailer(categorizedCtns)
    }

    override fun isCategorizedHybrisPagination(): Boolean {
        return false
    }

    override fun processError(mecError: MecError?, bool: Boolean) {
        ll_banner_place_holder.visibility = View.GONE
        mec_productCatalog_emptyText_label.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        setCartIconVisibility(false)
    }

}
