/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.address

import android.util.Log
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.philips.cdp.di.ecs.util.ECSConfiguration
import com.philips.platform.uid.view.widget.InputValidationLayout
import com.philips.platform.uid.view.widget.ValidationEditText

class PhoneNumberInputValidator(private val valPhoneNumberValidationEditText: ValidationEditText,val phoneNumberUtil: PhoneNumberUtil) : InputValidationLayout.Validator {

    override fun validate(msg: CharSequence?): Boolean {

        if(msg.isNullOrEmpty()){
            return false
        }
        return validatePhoneNumber(msg.toString())
    }

    //This method can be further refactored as this is not testableF
    private fun validatePhoneNumber(message :String): Boolean {
        try {
            val phoneNumber = phoneNumberUtil.parse(valPhoneNumberValidationEditText.text.toString(), ECSConfiguration.INSTANCE.country)
            return phoneNumberUtil.isValidNumber(phoneNumber)
        } catch (e: Exception) {
            Log.d("ShippingAddressFragment", "NumberParseException")
        }
        return false
    }

    fun getFormattedPhoneNumber(message: String) : String{
        val phoneNumber = phoneNumberUtil.parse(message, ECSConfiguration.INSTANCE.country)
        return phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)
    }
}