package com.philips.cdp.prxclient.datamodels.assets

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Description :
 * Project : PRX Common Component.
 * Created by naveen@philips.com on 02-Nov-15.
 */
class Data {
    /**
     *
     * @return
     * The assets
     */
    /**
     *
     * @param assets
     * The assets
     */
    @SerializedName("assets")
    @Expose
    var assets: Assets? = null

    /**
     * No args constructor for use in serialization
     *
     */
    constructor() {}

    /**
     *
     * @param assets
     */
    constructor(assets: Assets?) {
        this.assets = assets
    }

}