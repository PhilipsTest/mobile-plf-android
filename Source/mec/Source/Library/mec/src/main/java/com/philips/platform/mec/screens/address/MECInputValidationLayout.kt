package com.philips.platform.mec.screens.address

import android.content.Context
import android.util.AttributeSet
import android.widget.Toast
import com.philips.platform.uid.view.widget.InputValidationLayout

class MECInputValidationLayout(context: Context,attributes: AttributeSet) : InputValidationLayout(context,attributes){


    override fun showError() {
        super.showError()
        val error = errorLabelView.text
        //TODO tag the error here
        Toast.makeText(context,error,Toast.LENGTH_SHORT).show()
    }
}