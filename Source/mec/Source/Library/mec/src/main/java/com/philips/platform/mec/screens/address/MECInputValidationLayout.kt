package com.philips.platform.mec.screens.address

import android.content.Context
import android.util.AttributeSet
import com.philips.platform.mec.analytics.MECAnalytics
import com.philips.platform.uid.view.widget.InputValidationLayout




class MECInputValidationLayout(context: Context,attributes: AttributeSet) : InputValidationLayout(context,attributes){

    override fun showError() {
        super.showError()
        val resourceIdString = this.tag as String
        val resourceId : Int = resources.getIdentifier(resourceIdString, "string", context.packageName)
        val englishErrorText= MECAnalytics.getDefaultString(context,resourceId)
        MECAnalytics.trackUserError(englishErrorText)
    }




}