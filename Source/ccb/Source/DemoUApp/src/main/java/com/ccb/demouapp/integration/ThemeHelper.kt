package com.ccb.demouapp.integration

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.preference.PreferenceManager
import com.philips.platform.uid.thememanager.ColorRange
import com.philips.platform.uid.thememanager.ContentColor
import com.philips.platform.uid.thememanager.UIDHelper

/*
 * (C) Koninklijke Philips N.V., 2016.
 * All rights reserved.
 *
 */
class ThemeHelper(context: Activity) {
    var sharedPreferences: SharedPreferences
    val mContext: Context

    init {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        mContext = context
    }

    fun initColorRange(): ColorRange {
        val color = sharedPreferences.getString(UIDHelper.COLOR_RANGE, ColorRange.GROUP_BLUE.name)
        return ColorRange.valueOf(color!!)
    }

    fun initContentTotalRange(): ContentColor {
        val tonalRange = sharedPreferences.getString(UIDHelper.CONTENT_TONAL_RANGE, ContentColor.ULTRA_LIGHT.name)
        return ContentColor.valueOf(tonalRange!!)
    }

    val themeResourceId: Int
        get() = getColorResourceId(mContext.resources, initColorRange().name, initContentTotalRange().name, mContext.packageName)

    private fun getColorResourceId(resources: Resources, colorRange: String, tonalRange: String, packageName: String): Int {
        val themeName = String.format("Theme.DLS.%s.%s", toCamelCase(colorRange), toCamelCase(tonalRange))
        return resources.getIdentifier(themeName, "style", packageName)
    }

    private fun toCamelCase(s: String): String {
        val parts = s.split("_".toRegex()).toTypedArray()
        var camelCaseString = ""
        for (part in parts) {
            camelCaseString = camelCaseString + toProperCase(part)
        }
        return camelCaseString
    }

    companion object {
        fun toProperCase(s: String): String {
            return s.substring(0, 1).toUpperCase() +
                    s.substring(1).toLowerCase()
        }
    }
}