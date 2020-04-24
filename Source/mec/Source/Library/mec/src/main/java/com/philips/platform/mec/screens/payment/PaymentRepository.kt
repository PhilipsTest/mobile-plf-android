/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.payment

import com.philips.platform.mec.screens.address.AddressService

class PaymentRepository(val ecsServices: com.philips.platform.ecs.ECSServices) {


    private var addressService = AddressService()

    fun fetchPaymentDetails(paymentListCallback: PaymentListCallback){
        ecsServices.fetchPaymentsDetails(paymentListCallback)
    }

    fun submitOrder(cvv : String? , submitOrderCallback: SubmitOrderCallback) {
        ecsServices.submitOrder(cvv,submitOrderCallback)
    }

    fun makePayment(orderDetail: com.philips.platform.ecs.model.orders.ECSOrderDetail, billingAddress: com.philips.platform.ecs.model.address.ECSAddress, makePaymentCallback : com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.payment.ECSPaymentProvider, Exception>){
        addressService.setEnglishSalutation(billingAddress)
        ecsServices.makePayment(orderDetail,billingAddress,makePaymentCallback )
    }

}