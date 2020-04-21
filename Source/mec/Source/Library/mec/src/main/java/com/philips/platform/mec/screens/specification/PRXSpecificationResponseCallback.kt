/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.specification

import com.philips.platform.ecs.error.ECSError
import com.philips.cdp.prxclient.datamodels.specification.SpecificationModel
import com.philips.cdp.prxclient.error.PrxError
import com.philips.cdp.prxclient.response.ResponseData
import com.philips.cdp.prxclient.response.ResponseListener
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError

class PRXSpecificationResponseCallback(private val prxSpecificationViewModel: SpecificationViewModel) : ResponseListener {
    val mECRequestType : MECRequestType= MECRequestType.MEC_FETCH_SPECIFICATION
    override fun onResponseSuccess(responseData: ResponseData?) {
        prxSpecificationViewModel.specification.value = responseData as SpecificationModel
    }

    override fun onResponseError(prxError: PrxError?) {

        val description = prxError?.description

        val exception = Exception(description)
        val ecsError = com.philips.platform.ecs.error.ECSError(1000, description)
        val mecError = MecError(exception, ecsError,mECRequestType)
        //prxSpecificationViewModel.mecError.value = mecError
    }
}