package com.philips.cdp.prxclient.datamodels.support

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Item model class.
 */
class Item {
    /**
     *
     * @return
     * The head
     */
    /**
     *
     * @param head
     * The head
     */
    @SerializedName("head")
    @Expose
    var head: String? = null

    /**
     *
     * @return
     * The code
     */
    /**
     *
     * @param code
     * The code
     */
    @SerializedName("code")
    @Expose
    var code: String? = null

    /**
     *
     * @return
     * The rank
     */
    /**
     *
     * @param rank
     * The rank
     */
    @SerializedName("rank")
    @Expose
    var rank: String? = null

    /**
     *
     * @return
     * The lang
     */
    /**
     *
     * @param lang
     * The lang
     */
    @SerializedName("lang")
    @Expose
    var lang: String? = null

    /**
     *
     * @return
     * The asset
     */
    /**
     *
     * @param asset
     * The asset
     */
    @SerializedName("asset")
    @Expose
    var asset: String? = null

}