/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.mec.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.AdapterView
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat


import com.philips.cdp.di.mec.R
import com.philips.cdp.di.mec.screens.address.AddressViewModel
import com.philips.cdp.di.mec.utils.MECutility
import com.philips.platform.uid.thememanager.UIDHelper
import com.philips.platform.uid.view.widget.UIPicker
import com.philips.platform.uid.view.widget.ValidationEditText

class MECDropDown(private val validationEditText: ValidationEditText, private val dropDownStrings : Array<String>) {

    private var mPopUp: UIPicker? = null

    private var mSalutationListener: DropDownSelectListener? = null

    private val mListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
        val salutation = parent.getItemAtPosition(position) as String

        validationEditText.setText(salutation)

        mSalutationListener?.onDropDownSelect(validationEditText,salutation)

        validationEditText.setCompoundDrawables(null, null, MECutility.getImageArrow(validationEditText.context), null)
        dismiss()
    }


    private fun isShowing() = mPopUp!!.isShowing

    interface DropDownSelectListener {
        fun onDropDownSelect(validationEditText: ValidationEditText, salutation: String)
    }

    fun setDataChangeListener( salutationListener: DropDownSelectListener){
        mSalutationListener = salutationListener
    }

    fun createPopUp() {
        val popupThemedContext = UIDHelper.getPopupThemedContext(validationEditText.context)
        mPopUp = UIPicker(popupThemedContext)
        mPopUp!!.setAdapter(UIPickerAdapter(popupThemedContext, R.layout.mec_uipicker_item_text, dropDownStrings))
        mPopUp!!.anchorView = validationEditText
        mPopUp!!.isModal = true
        mPopUp!!.setOnItemClickListener(mListener)
    }


    fun show() {
        if (!isShowing()) {
            mPopUp!!.show()
        }
    }

    fun dismiss() {
        mPopUp!!.dismiss()
    }

}