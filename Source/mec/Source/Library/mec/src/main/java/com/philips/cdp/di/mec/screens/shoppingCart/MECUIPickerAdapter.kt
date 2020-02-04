package com.philips.cdp.di.mec.screens.shoppingCart

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import com.philips.platform.uid.thememanager.UIDHelper
import com.philips.platform.uid.view.widget.Label

class MECUIPickerAdapter(context: Context, private val resID: Int, states: Array<String?>) : ArrayAdapter<String>(context, resID, states) {

    private var inflater: LayoutInflater? = null

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater = inflater!!.cloneInContext(UIDHelper.getPopupThemedContext(context))
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: Label
        if (convertView == null) {
            view = inflater!!.inflate(resID, parent, false) as Label
        } else {
            view = (convertView as Label?)!!
        }
        view.text = getItem(position)
        return view
    }
}