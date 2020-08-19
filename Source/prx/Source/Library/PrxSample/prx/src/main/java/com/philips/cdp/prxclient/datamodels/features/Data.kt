package com.philips.cdp.prxclient.datamodels.features

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

class Data : Serializable {
    companion object {
        private const val serialVersionUID = 6960659089260274640L
        var videoExtensionList: List<*>? = null
        var videoExtensions = arrayOf("WEBM", "MPG", "MP2", "MPEG", "MPE", "MPV", "OGG", "MP4", "M4P", "M4V", "AVI", "WMV", "MOV", "QT", "FLV", "SWF", "AVCHD")

        init {
            videoExtensionList = Arrays.asList(*videoExtensions)
        }
    }

    @SerializedName("keyBenefitArea")
    var keyBenefitArea: List<KeyBenefitAreaItem?>? = null

    @SerializedName("code")
    var code: List<CodeItem>? = null

    @SerializedName("featureHighlight")
    var featureHighlight: List<FeatureHighlightItem>? = null
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