/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import com.philips.platform.uid.thememanager.UIDHelper
import com.philips.platform.uid.view.widget.Label

class UIPickerAdapter(context: Context, private val resID: Int, states: Array<String>) : ArrayAdapter<String>(context, resID, states) {

    private var inflater: LayoutInflater? = null

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater = inflater!!.cloneInContext(UIDHelper.getPopupThemedContext(context))
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: Label = if (convertView != null) {
            convertView as Label
        } else {
            inflater!!.inflate(resID, parent, false) as Label
        }
        view.text = getItem(position)
        return view
    }
}