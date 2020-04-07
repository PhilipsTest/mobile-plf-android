/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.payment

import com.philips.cdp.di.ecs.ECSServices
import com.philips.cdp.di.ecs.integration.ECSCallback
import com.philips.cdp.di.ecs.model.address.ECSAddress
import com.philips.cdp.di.ecs.model.orders.ECSOrderDetail
import com.philips.cdp.di.ecs.model.payment.ECSPaymentProvider
import com.philips.platform.mec.screens.address.AddressService

class PaymentRepository(val ecsServices: ECSServices) {


    private var addressService = AddressService()

    fun fetchPaymentDetails(paymentListCallback: PaymentListCallback){
        ecsServices.fetchPaymentsDetails(paymentListCallback)
    }

    fun submitOrder(cvv : String? , submitOrderCallback: SubmitOrderCallback) {
        ecsServices.submitOrder(cvv,submitOrderCallback)
    }

    fun makePayment(orderDetail: ECSOrderDetail, billingAddress: ECSAddress, makePaymentCallback : ECSCallback<ECSPaymentProvider, Exception> ){
        addressService.setEnglishSalutation(billingAddress)
        ecsServices.makePayment(orderDetail,billingAddress,makePaymentCallback )
    }

}