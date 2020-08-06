package com.philips.cdp.prxclient.datamodels.support

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Rich Texts.
 */
class RichTexts {
    /**
     *
     * @return
     * The richText
     */
    /**
     *
     * @param richText
     * The richText
     */
    @SerializedName("richText")
    @Expose
    var richText: List<RichText> = ArrayList()

}