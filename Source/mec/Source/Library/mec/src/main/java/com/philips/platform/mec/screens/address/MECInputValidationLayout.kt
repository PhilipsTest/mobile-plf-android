package com.philips.platform.mec.screens.address

import android.content.Context
import android.util.AttributeSet
import com.philips.platform.mec.analytics.MECAnalytics
import com.philips.platform.uid.view.widget.InputValidationLayout

class MECInputValidationLayout(context: Context,attributes: AttributeSet) : InputValidationLayout(context,attributes){

    override fun showError() {
        super.showError()
        val error = errorLabelView.text // inline error text box
        MECAnalytics.trackUserError(error.toString())
    }
}