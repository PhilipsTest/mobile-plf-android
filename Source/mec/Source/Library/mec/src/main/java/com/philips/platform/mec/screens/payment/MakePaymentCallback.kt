/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.payment

import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECutility

class MakePaymentCallback (private val paymentViewModel: PaymentViewModel): com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.payment.ECSPaymentProvider, Exception> {
    lateinit var mECRequestType : MECRequestType
    override fun onResponse(result: com.philips.platform.ecs.model.payment.ECSPaymentProvider?) {
        paymentViewModel.eCSPaymentProvider.value=result
    }


    override fun onFailure(error: Exception?, ecsError: com.philips.platform.ecs.error.ECSError?) {
        if (MECutility.isAuthError(ecsError)) {
            paymentViewModel.retryAPI(mECRequestType)
        }else{
            val mecError = MecError(error, ecsError,mECRequestType)
            paymentViewModel.mecError.value = mecError
        }
    }
}