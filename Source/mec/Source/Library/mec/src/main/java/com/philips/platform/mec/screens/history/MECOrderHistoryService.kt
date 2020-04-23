/*
 *  Copyright (c) Koninklijke Philips N.V., 2020
 *
 *  * All rights are reserved. Reproduction or dissemination
 *
 *  * in whole or in part is prohibited without the prior written
 *
 *  * consent of the copyright holder.
 *
 *
 */

package com.philips.platform.mec.screens.history

import androidx.recyclerview.widget.LinearLayoutManager
import com.philips.platform.mec.utils.MECLog
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MECOrderHistoryService {

    fun isScrollDown(lay: LinearLayoutManager): Boolean {
        val visibleItemCount = lay.childCount
        val firstVisibleItemPosition = lay.findFirstVisibleItemPosition()
        return visibleItemCount + firstVisibleItemPosition >= lay.itemCount && firstVisibleItemPosition >= 0
    }

    fun getFormattedDate(date: String?): String? {
        if(date==null) return ""
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        var convertedDate: Date? = null
        try {
            convertedDate = dateFormat.parse(date)
        } catch (e: ParseException) {
            MECLog.d("MECOrderHistoryService", e.message)
        }
        val sdf = SimpleDateFormat("EEEE MMM dd, yyyy") // Set your date format
        return sdf.format(convertedDate)
    }

}