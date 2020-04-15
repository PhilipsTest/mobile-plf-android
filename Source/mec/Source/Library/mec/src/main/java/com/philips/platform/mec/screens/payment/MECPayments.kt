/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.payment

import com.philips.platform.mec.utils.MECConstant


class MECPayments(val payments:MutableList<MECPayment>, var isPaymentDownloaded: Boolean ){


    fun isNewCardPresent() : Boolean{

        for (payment in payments){

            if(payment.ecsPayment.id.equals(MECConstant.NEW_CARD_PAYMENT,true)){
                return true
            }
        }
        return false
    }

    fun getNewCard() : MECPayment?{

        for (payment in payments){

            if(payment.ecsPayment.id.equals(MECConstant.NEW_CARD_PAYMENT,true)){
                return payment
            }
        }
        return null
    }

    fun getSelectedPayment() : MECPayment?{

        for (payment in payments){

            if(payment.isSelected){
                return payment
            }
        }
        return null
    }

    fun setSelection(selectedPayment: MECPayment){
        for (payment in payments){
            payment.isSelected = payment.ecsPayment.id.equals(selectedPayment.ecsPayment.id,true)
        }
    }
}