/* Copyright (c) Koninklijke Philips N.V., 2020
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.mec.screens.shoppingCart

import android.content.Context
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView

import com.philips.platform.mec.R
import com.philips.platform.uid.thememanager.UIDHelper
import com.philips.platform.uid.view.widget.UIPicker

class MecCountDropDown(private val anchor: View, private val context: Context, private val mStart: Int, private val mEnd: Int, private var mCurrent: Int, private val mUpdateListener: CountUpdateListener?) : AdapterView.OnItemClickListener {
    var popUpWindow: UIPicker? = null
    private var mCurrentViewIndex: Int = 0

    interface CountUpdateListener {
        fun countUpdate(oldCount: Int, newCount: Int)
    }

    constructor(anchor: View, context: Context, end: Int, currentCount: Int, listener: CountUpdateListener) : this(anchor, context, 1, end, currentCount, listener) {}


    init {
        mCurrentViewIndex = mCurrent - mStart
    }

    fun show() {
        popUpWindow!!.show()
        popUpWindow!!.listView!!.choiceMode = AbsListView.CHOICE_MODE_SINGLE
        popUpWindow!!.setSelection(mCurrentViewIndex)
    }

    fun dismiss() {
        if (popUpWindow != null)
            popUpWindow!!.dismiss()
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        if (mUpdateListener != null) {
            val count = Integer.parseInt(parent.getItemAtPosition(position) as String)
            mUpdateListener.countUpdate(mCurrent, count)
            mCurrent = count
            mCurrentViewIndex = position
        }
        popUpWindow!!.dismiss()
    }

    fun createPopUp(v: View, quantity: Int) {
        val popupThemedContext = UIDHelper.getPopupThemedContext(context)
        popUpWindow = UIPicker(popupThemedContext)

        popUpWindow!!.setAdapter(MECUIPickerAdapter(popupThemedContext, R.layout.mec_uipicker_item_text, createRowItems(quantity)))
        popUpWindow!!.anchorView = v
        popUpWindow!!.isModal = true
        popUpWindow!!.setOnItemClickListener(this)
    }


    private fun createRowItems(items: Int): Array<String?> {
        val rowItems = arrayOfNulls<String>(items)
        for (i in 0 until items) {
            rowItems[i] = (i + 1).toString()
        }
        return rowItems
    }
}