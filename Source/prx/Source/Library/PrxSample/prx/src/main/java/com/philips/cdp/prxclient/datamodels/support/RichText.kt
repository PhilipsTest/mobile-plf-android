package com.philips.cdp.prxclient.datamodels.support

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Rich Text Class.
 */
class RichText {
    /**
     *
     * @return
     * The type
     */
    /**
     *
     * @param type
     * The type
     */
    @SerializedName("type")
    @Expose
    var type: String? = null

    /**
     *
     * @return
     * The chapter
     */
    /**
     *
     * @param chapter
     * The chapter
     */
    @SerializedName("chapter")
    @Expose
    var chapter: Chapter? = null

    /**
     *
     * @return
     * The item
     */
    /**
     *
     * @param item
     * The item
     */
    @SerializedName("item")
    @Expose
    var item: List<Item> = ArrayList()

}