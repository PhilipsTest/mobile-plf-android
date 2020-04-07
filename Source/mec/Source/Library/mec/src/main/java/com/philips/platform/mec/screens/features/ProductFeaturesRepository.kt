/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.features

import android.content.Context
import com.philips.cdp.di.ecs.constants.NetworkConstants
import com.philips.platform.mec.utils.MECConstant
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.cdp.prxclient.PRXDependencies
import com.philips.cdp.prxclient.PrxConstants
import com.philips.cdp.prxclient.RequestManager
import com.philips.cdp.prxclient.request.ProductFeaturesRequest

class ProductFeaturesRepository  {

    fun fetchProductFeatures(context: Context, ctn: String, featuresViewModel: ProductFeaturesViewModel){
        // "SCF251/02"
        var productFeaturesRequest  = ProductFeaturesRequest(ctn,null)
        productFeaturesRequest.sector = PrxConstants.Sector.B2C;
        productFeaturesRequest.catalog = PrxConstants.Catalog.CONSUMER;
        productFeaturesRequest.requestTimeOut = NetworkConstants.DEFAULT_TIMEOUT_MS

        val mRequestManager = RequestManager()
        val prxDependencies = PRXDependencies(context, MECDataHolder.INSTANCE.appinfra, MECConstant.COMPONENT_NAME)
        mRequestManager.init(prxDependencies)
        mRequestManager.executeRequest(productFeaturesRequest, PRXProductFeaturesResponseCallback(featuresViewModel))
    }
}