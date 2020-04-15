/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.address

import com.philips.platform.uid.view.widget.InputValidationLayout

class EmptyInputValidator : InputValidationLayout.Validator  {

    override fun validate(msg: CharSequence?): Boolean {
       return !msg?.trim().isNullOrEmpty()
    }
}