/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.detail

import com.philips.platform.ecs.error.ECSError
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.model.products.ECSProduct
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
        val TAG:String="MECLandingProductDetailsFragment"
    }

  override fun executeRequest() {

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

      MECDataHolder.INSTANCE.eCSServices.fetchProductSummaries(Arrays.asList(product.code) , object : com.philips.platform.ecs.integration.ECSCallback<List<com.philips.platform.ecs.model.products.ECSProduct>, Exception> {
      override fun onResponse(result: List<com.philips.platform.ecs.model.products.ECSProduct>?) {
        product = result?.get(0) ?: product
        callParentExecute()
      }

      override fun onFailure(error: Exception?, ecsError: com.philips.platform.ecs.error.ECSError?) {
        val mecError = MecError(error, ecsError,null)
        processError(mecError,true)
      }
    })
  }

  private fun fetchProductForHybris() {
      MECDataHolder.INSTANCE.eCSServices.fetchProduct(product.code, object : com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.products.ECSProduct, Exception> {
      override fun onResponse(result: com.philips.platform.ecs.model.products.ECSProduct?) {
        product = result!!
        callParentExecute()
      }

      override fun onFailure(error: Exception?, ecsError: com.philips.platform.ecs.error.ECSError?) {
        val mecError = MecError(error, ecsError,null)
        processError(mecError,true)
      }
    })
  }

  private fun callParentExecute(){
    super.executeRequest()
  }

}
