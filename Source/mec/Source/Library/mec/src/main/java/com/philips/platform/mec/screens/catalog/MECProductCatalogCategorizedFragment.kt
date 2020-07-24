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



    override fun executeRequest(){

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


            if(mPILProductsWithReview.size==0) {
                showProgressBar(binding.mecCatalogProgress.mecProgressBarContainer)
            }else{
                binding.progressBar.visibility = View.VISIBLE
            }
           // ecsProductViewModel.initCategorized(currentPage, pageSize, categorizedCtns)
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

        if(mPILProductsWithReview.size ==0) return

        if(isCallEnded()){
            isCallOnProgress = false
            binding.progressBar.visibility = View.GONE
        }else{
            isCallOnProgress = true
            binding.progressBar.visibility = View.VISIBLE
        }
    }




    private fun isCallEnded(): Boolean {
        return  true
    }


}

