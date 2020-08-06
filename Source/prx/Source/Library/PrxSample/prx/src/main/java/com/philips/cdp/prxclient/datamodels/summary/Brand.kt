package com.philips.cdp.prxclient.datamodels.summary

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Description :
 * Project : PRX Common Component.
 * Created by naveen@philips.com on 02-Nov-15.
 */
class Brand {
    /**
     *
     * @return
     * The brandLogo
     */
    /**
     *
     * @param brandLogo
     * The brandLogo
     */
    @SerializedName("brandLogo")
    @Expose
    var brandLogo: String? = null

    /**
     * No args constructor for use in serialization
     *
     */
    constructor() {}

    /**
     *
     * @param brandLogo
     */
    constructor(brandLogo: String?) {
        this.brandLogo = brandLogo
    }

}