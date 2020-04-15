/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.features

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.philips.platform.mec.common.CommonViewModel
import com.philips.cdp.prxclient.datamodels.features.FeaturesModel

class ProductFeaturesViewModel : com.philips.platform.mec.common.CommonViewModel() {

    val features =  MutableLiveData<FeaturesModel>()

    fun fetchProductFeatures(context: Context,ctn : String){
        ProductFeaturesRepository().fetchProductFeatures(context,ctn,this)
    }
}