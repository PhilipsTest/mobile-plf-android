package com.philips.cdp.prxclient.datamodels.features

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Data(var keyBenefitArea: List<KeyBenefitAreaItem?>? = null, var code: List<CodeItem>? = null, var featureHighlight: List<FeatureHighlightItem>? = null) : Parcelable {
    companion object {
        var videoExtensionList: List<*>? = null
        var videoExtensions = arrayOf("WEBM", "MPG", "MP2", "MPEG", "MPE", "MPV", "OGG", "MP4", "M4P", "M4V", "AVI", "WMV", "MOV", "QT", "FLV", "SWF", "AVCHD")

        init {
            videoExtensionList = Arrays.asList(*videoExtensions)
        }
    }


    fun getSingleAssetImageFromFeatureCode(featureCode: String?): String? {
        if (code == null) return null
        for (codeItem in code!!) {
            if (codeItem.code.equals(featureCode, ignoreCase = true)) {
                if (isImage(codeItem.extension)) {
                    return codeItem.asset
                }
            }
        }
        return null
    }

    private fun isImage(extension: String?): Boolean {
        return !videoExtensionList!!.contains(extension)
    }
}