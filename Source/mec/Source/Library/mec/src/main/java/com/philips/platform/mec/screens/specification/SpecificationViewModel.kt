/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.specification

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.philips.platform.mec.common.CommonViewModel
import com.philips.cdp.prxclient.datamodels.specification.SpecificationModel

class SpecificationViewModel : com.philips.platform.mec.common.CommonViewModel() {

    val specification = MutableLiveData<SpecificationModel>()


    fun fetchSpecification(context: Context,ctn :String){
        SpecificationRepository().fetchSpecification(context,ctn,this)
    }

}