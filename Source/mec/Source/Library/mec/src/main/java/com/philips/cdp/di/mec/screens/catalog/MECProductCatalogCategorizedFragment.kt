package com.philips.cdp.di.mec.screens.catalog

import android.os.Bundle
import android.view.View
import com.philips.cdp.di.mec.R
import com.philips.cdp.di.mec.utils.MECConstant
import com.philips.platform.uid.view.widget.AlertDialogFragment
import kotlinx.android.synthetic.main.mec_main_activity.*

class MECProductCatalogCategorizedFragment : MECProductCatalogFragment() {
    override fun getFragmentTag(): String {
        return "MECProductCatalogCategorizedFragment"
    }

    companion object {
        val TAG:String="MECProductCatalogCategorizedFragment"
    }



    override fun executeRequest(){
        
        if(isAllProductsFound()) {
            binding.progressBar.visibility = View.GONE
            hideProgressBar()
        }else{
            isCallOnProgress =true
            ecsProductViewModel.initCategorized(currentPage, pageSize, categorizedCtns)
        }
    }

    override fun isPaginationSupported(): Boolean {
        return true
    }

    override fun showNoProduct() {

        isCallOnProgress =false

        currentPage += 1
        if (currentPage < totalPages) {
            showCategorizedFetchDialog()
        }else{
            super.showNoProduct()
        }
    }


    private fun showCategorizedFetchDialog(){

        val builder = AlertDialogFragment.Builder(context)
        builder.setCancelable(false)

        var alertDialogFragment = builder.create()
        builder.setMessage(resources.getString(R.string.mec_threshold_message))
        builder.setPositiveButton(getString(R.string.mec_ok), fun(it: View) {


            if(productList.size==0) {
                createCustomProgressBar(container, MEDIUM)
            }else{
                binding.progressBar.visibility = View.VISIBLE
            }
            ecsProductViewModel.initCategorized(currentPage, pageSize, categorizedCtns)
            alertDialogFragment.dismiss()
        })


        builder.setNegativeButton(getString(R.string.mec_cancel), fun(it: View) {
            super.showNoProduct()
            alertDialogFragment.dismiss()
        })

        builder.setTitle(resources.getString(R.string.mec_threshold_title))
        fragmentManager?.let { alertDialogFragment.show(it,"ALERT_DIALOG_TAG") }

    }

    override fun isCategorizedHybrisPagination(): Boolean {
        return true
    }

    override fun doProgressbarOperation() {

           if(productList.size ==0) return

           if(isCallEnded()){
               isCallOnProgress = false
               binding.progressBar.visibility = View.GONE
           }else{
               isCallOnProgress = true
               binding.progressBar.visibility = View.VISIBLE
           }
    }

    private fun didProductsFondReachPageSize() = (productList.size / (currentPage+1)) == pageSize

    private fun isProductNotFound() = productList.size == 0

    private fun isAllProductsFound() = totalProductsTobeSearched == productList.size

    private fun didReachThreshold() =  0 == (currentPage + 1) % MECConstant.THRESHOLD

    private fun didReachLastPage() = currentPage == totalPages-1


    private fun isCallEnded(): Boolean {
        return  didReachLastPage( ) ||
                isAllProductsFound() ||
                didProductsFondReachPageSize()
    }


}

