/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.payment

import android.content.Context

class MECPaymentConfirmationService {

    fun getTitle(paymentStatus: PaymentStatus, context: Context) : String{

        when(paymentStatus){

            PaymentStatus.SUCCESS -> "Success"
            else -> "fail"
        }
        return "fail"
    }


}