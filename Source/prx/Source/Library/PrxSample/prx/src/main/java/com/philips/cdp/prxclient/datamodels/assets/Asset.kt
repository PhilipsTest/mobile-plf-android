package com.philips.cdp.prxclient.datamodels.assets

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Description :
 * Project : PRX Common Component.
 * Created by naveen@philips.com on 02-Nov-15.
 */
class Asset {
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
     * The description
     */
    /**
     *
     * @param description
     * The description
     */
    @SerializedName("description")
    @Expose
    var description: String? = null

    /**
     *
     * @return
     * The extension
     */
    /**
     *
     * @param extension
     * The extension
     */
    @SerializedName("extension")
    @Expose
    var extension: String? = null

    /**
     *
     * @return
     * The extent
     */
    /**
     *
     * @param extent
     * The extent
     */
    @SerializedName("extent")
    @Expose
    var extent: String? = null

    /**
     *
     * @return
     * The lastModified
     */
    /**
     *
     * @param lastModified
     * The lastModified
     */
    @SerializedName("lastModified")
    @Expose
    var lastModified: String? = null

    /**
     *
     * @return
     * The locale
     */
    /**
     *
     * @param locale
     * The locale
     */
    @SerializedName("locale")
    @Expose
    var locale: String? = null

    /**
     *
     * @return
     * The number
     */
    /**
     *
     * @param number
     * The number
     */
    @SerializedName("number")
    @Expose
    var number: String? = null

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

    /**
     * No args constructor for use in serialization
     *
     */
    constructor() {}

    /**
     *
     * @param extension
     * @param asset
     * @param lastModified
     * @param extent
     * @param description
     * @param locale
     * @param number
     * @param code
     * @param type
     */
    constructor(code: String?, description: String?, extension: String?, extent: String?, lastModified: String?, locale: String?, number: String?, type: String?, asset: String?) {
        this.code = code
        this.description = description
        this.extension = extension
        this.extent = extent
        this.lastModified = lastModified
        this.locale = locale
        this.number = number
        this.type = type
        this.asset = asset
    }

}