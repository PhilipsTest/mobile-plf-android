/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.payment

import androidx.lifecycle.MutableLiveData
import com.philips.cdp.di.ecs.model.address.ECSAddress
import com.philips.cdp.di.ecs.model.orders.ECSOrderDetail
import com.philips.cdp.di.ecs.model.payment.ECSPaymentProvider
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.utils.MECDataHolder

class PaymentViewModel : com.philips.platform.mec.common.CommonViewModel() {

    var mecPayments = MutableLiveData<MECPayments>()

    var ecsOrderDetail = MutableLiveData<ECSOrderDetail>()

    var eCSPaymentProvider = MutableLiveData<ECSPaymentProvider>()

    var ecsServices = MECDataHolder.INSTANCE.eCSServices

    var paymentListCallback = PaymentListCallback(this)

    var submitOrderCallback = SubmitOrderCallback(this)

    var makePaymentCallback = MakePaymentCallback(this)

    var paymentRepository = PaymentRepository(ecsServices)


    var mCVV :String? =null

    lateinit var  mOrderDetail: ECSOrderDetail

    lateinit var  mBillingAdress: ECSAddress

    fun fetchPaymentDetails(){
        paymentListCallback.mECRequestType=MECRequestType.MEC_FETCH_PAYMENT_DETAILS
        paymentRepository.fetchPaymentDetails(paymentListCallback)
    }

    fun submitOrder(cvv :String?){
        mCVV=cvv
        submitOrderCallback.mECRequestType=MECRequestType.MEC_SUBMIT_ORDER
        paymentRepository.submitOrder(cvv,submitOrderCallback)
    }

    fun makePayment(orderDetail: ECSOrderDetail, billingAddress: ECSAddress){
        mOrderDetail=orderDetail
        mBillingAdress =billingAddress


        makePaymentCallback.mECRequestType=MECRequestType.MEC_MAKE_PAYMENT
        paymentRepository.makePayment(orderDetail,billingAddress,makePaymentCallback )
    }

    fun retryAPI(mecRequestType: MECRequestType) {
        var retryAPI = selectAPIcall(mecRequestType)
        authAndCallAPIagain(retryAPI, authFailCallback)
    }

    fun selectAPIcall(mecRequestType: MECRequestType): () -> Unit {

        lateinit var APIcall: () -> Unit
        when (mecRequestType) {
            MECRequestType.MEC_FETCH_PAYMENT_DETAILS -> APIcall = { fetchPaymentDetails() }
            MECRequestType.MEC_SUBMIT_ORDER          -> APIcall = { submitOrder(mCVV) }
            MECRequestType.MEC_MAKE_PAYMENT          -> APIcall = { makePayment(mOrderDetail,mBillingAdress) }
        }
        return APIcall
    }

}