/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.catalog

import android.view.View
import com.philips.platform.mec.R
import com.philips.platform.uid.view.widget.AlertDialogFragment

class MECProductCatalogCategorizedFragment : MECProductCatalogFragment() {
    override fun getFragmentTag(): String {
        return TAG
    }

    companion object {
        val TAG:String="MECProductCatalogCategorizedFragment"
    }




    override fun isPaginationSupported(): Boolean {
        return true
    }

    override fun showNoProduct() {
        super.showNoProduct()
    }


    private fun showCategorizedFetchDialog(){

        val builder = AlertDialogFragment.Builder(context)
        builder.setCancelable(false)

        var alertDialogFragment = builder.create()
        builder.setMessage(resources.getString(R.string.mec_threshold_message))
        builder.setPositiveButton(getString(R.string.mec_ok), fun(it: View) {


            executeCategorizedRequest()
            alertDialogFragment.dismiss()
        })


        builder.setNegativeButton(getString(R.string.mec_cancel), fun(it: View) {
            super.showNoProduct()
            alertDialogFragment.dismiss()
        })

        builder.setTitle(resources.getString(R.string.mec_threshold_title))
        fragmentManager?.let { alertDialogFragment.show(it,"ALERT_DIALOG_TAG") }

    }

    private fun executeCategorizedRequest() {
        if (mProductsWithReview.size == 0) {
            showProgressBar(binding.mecCatalogProgress.mecProgressBarContainer)
        } else {
            binding.progressBar.visibility = View.VISIBLE
        }
        executeRequest()
    }

    override fun isCategorizedHybrisPagination(): Boolean {
        return true
    }

    override fun handleHybrisCategorized() {

        dismissPaginationProgressBar()
        dismissProgressBar(binding.mecCatalogProgress.mecProgressBarContainer)
        if(offSet == 0 && mProductsWithReview.size == 0){
            showCategorizedFetchDialog()
        }else{
            if(!isAllProductDownloaded && !isAllCategorizedProductFound()){

                if(mProductsWithReview.size == 0 || mProductsWithReview.size % limit !=0) { //TODO for zero product found first
                    executeCategorizedRequest()
                }
            }

        }
    }

    private fun isAllCategorizedProductFound() :Boolean{
        return categorizedCtns?.size == mProductsWithReview.size
    }
}

