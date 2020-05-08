/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.address

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.philips.platform.mec.utils.MECLog
import com.philips.platform.uid.view.widget.InputValidationLayout
import com.philips.platform.uid.view.widget.ValidationEditText

class PhoneNumberInputValidator(private val valPhoneNumberValidationEditText: ValidationEditText, val phoneNumberUtil: PhoneNumberUtil) : InputValidationLayout.Validator {
    private val TAG: String = PhoneNumberInputValidator::class.java.simpleName
    override fun validate(msg: CharSequence?): Boolean {

        if (msg.isNullOrEmpty()) {
            return false
        }
        return validatePhoneNumber(msg.toString())
    }

    //This method can be further refactored as this is not testableF
    private fun validatePhoneNumber(message: String): Boolean {
        try {
            val phoneNumber = phoneNumberUtil.parse(valPhoneNumberValidationEditText.text.toString(), com.philips.platform.ecs.util.ECSConfiguration.INSTANCE.country)
            return phoneNumberUtil.isValidNumber(phoneNumber)
        } catch (e: Exception) {
            MECLog.d(TAG, "NumberParseException")
        }
        return false
    }

    fun getFormattedPhoneNumber(message: String): String {
        val phoneNumber = phoneNumberUtil.parse(message, com.philips.platform.ecs.util.ECSConfiguration.INSTANCE.country)
        return phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)
    }
}