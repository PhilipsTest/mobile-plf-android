package com.philips.platform.mec.screens.address

import android.content.Context
import android.util.AttributeSet
import com.philips.platform.mec.analytics.MECAnalytics
import com.philips.platform.mec.analytics.MECAnalyticsConstant.userError
import com.philips.platform.mec.utils.MECConstant.COMPONENT_NAME
import com.philips.platform.uid.view.widget.InputValidationLayout

class MECInputValidationLayout(context: Context,attributes: AttributeSet) : InputValidationLayout(context,attributes){


    override fun showError() {
        super.showError()
        val error = errorLabelView.text // inline errro text box
        var errorString: String = COMPONENT_NAME + ":"
        errorString += userError+ ":"
        errorString += error.toString()
        MECAnalytics.trackUserError(errorString)
    }
}