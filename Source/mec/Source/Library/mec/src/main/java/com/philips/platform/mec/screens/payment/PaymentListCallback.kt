/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.payment

import com.philips.cdp.di.ecs.error.ECSError
import com.philips.cdp.di.ecs.integration.ECSCallback
import com.philips.cdp.di.ecs.model.payment.ECSPayment
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECutility

class PaymentListCallback(private val paymentViewModel: PaymentViewModel) : ECSCallback<List<ECSPayment>, Exception> {

    lateinit var mECRequestType : MECRequestType

    override fun onResponse(payments: List<ECSPayment>?) {

        val mecPaymentList : MutableList<MECPayment> = mutableListOf()

        if (payments != null) {

            for (ecsPayment in payments){
                mecPaymentList.add(MECPayment(ecsPayment))
            }
        }

        val value = paymentViewModel.mecPayments.value
        value?.payments?.toMutableList()?.addAll(mecPaymentList)

        paymentViewModel.mecPayments.value = MECPayments(mecPaymentList, true)
    }

    override fun onFailure(error: Exception?, ecsError: ECSError?) {

        if (MECutility.isAuthError(ecsError)) {
            paymentViewModel.retryAPI(mECRequestType)
        }else{
            val mecError = MecError(error, ecsError,mECRequestType)
            paymentViewModel.mecError.value = mecError
        }
    }
}