/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.detail


import android.view.View
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.error.ECSException

import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.ecs.microService.model.product.ECSProducts

import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECDataHolder
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class MECLandingProductDetailsFragment : MECProductDetailsFragment() {

    override fun getFragmentTag(): String {
        return "MECLandingProductDetailsFragment"
    }

    companion object {
        val TAG: String = "MECLandingProductDetailsFragment"
    }

    override fun executeRequest() {
        binding.rlParentContent.visibility = View.INVISIBLE
        showProgressBar(binding.mecProgress.mecProgressBarContainer)
        fetchProductDetailForCtn(MECDataHolder.INSTANCE.hybrisEnabled)
    }

    private fun fetchProductDetailForCtn(isHybris: Boolean) {


        if (isHybris) {

            fetchProductForHybris()

        } else {

            fetchProductForRetailers()


        }
    }

    private fun fetchProductForRetailers() {

        try {
            MECDataHolder.INSTANCE.eCSServices.microService.fetchProductSummaries(Arrays.asList(product.ctn), object : ECSCallback<ECSProducts, ECSError> {

                override fun onResponse(result: ECSProducts) {
                    product = result.commerceProducts[0]
                    callParentExecute()
                }

                override fun onFailure(ecsError: ECSError) {

                    val occECSError = com.philips.platform.ecs.error.ECSError(ecsError.errorCode
                            ?: -100, ecsError.errorType?.name)
                    val mecError = MecError(Exception(ecsError.errorMessage), occECSError, null)
                    processError(mecError, true)
                }

            })

        } catch (e: ECSException) {
            val ecsError = ECSError(e.message ?: "", e.errorCode, null)
            val occECSError = com.philips.platform.ecs.error.ECSError(ecsError.errorCode
                    ?: -100, ecsError.errorType?.name)
            val mecError = MecError(Exception(ecsError.errorMessage), occECSError, null)
            processError(mecError, true)
        }
    }

    private fun fetchProductForHybris() {

        try {
            MECDataHolder.INSTANCE.eCSServices.microService.fetchProduct(product.ctn, object : ECSCallback<ECSProduct?, ECSError> {

                override fun onResponse(result: ECSProduct?) {
                    product = result!!
                    callParentExecute()
                }

                override fun onFailure(ecsError: ECSError) {
                    val occECSError = com.philips.platform.ecs.error.ECSError(ecsError.errorCode
                            ?: -100, ecsError.errorType?.name)
                    val mecError = MecError(Exception(ecsError.errorMessage), occECSError, null)
                    processError(mecError, true)
                }

            })
        } catch (e: ECSException) {
            val ecsError = ECSError(e.message ?: "", e.errorCode, null)
            val occECSError = com.philips.platform.ecs.error.ECSError(ecsError.errorCode
                    ?: -100, ecsError.errorType?.name)
            val mecError = MecError(Exception(ecsError.errorMessage), occECSError, null)
            processError(mecError, true)
        }
    }

    private fun callParentExecute() {
        super.executeRequest()
    }

}
