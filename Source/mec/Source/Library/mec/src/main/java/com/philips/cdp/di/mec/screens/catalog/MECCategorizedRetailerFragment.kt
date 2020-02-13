package com.philips.cdp.di.mec.screens.catalog
import android.view.View
import com.philips.cdp.di.mec.common.MecError
import com.philips.cdp.di.mec.utils.MECConstant
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

    override fun processError(mecError: MecError?) {
        ll_banner_place_holder.visibility = View.GONE
        mec_productCatalog_emptyText_label.visibility = View.VISIBLE
    }

}
