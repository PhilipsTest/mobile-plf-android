package com.philips.cdp.prxclient.datamodels.assets

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Description :
 * Project : PRX Common Component.
 * Created by naveen@philips.com on 02-Nov-15.
 */
class Assets {
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
    var asset: List<Asset> = ArrayList()

    /**
     * No args constructor for use in serialization
     *
     */
    constructor() {}

    /**
     *
     * @param asset
     */
    constructor(asset: List<Asset>) {
        this.asset = asset
    }

}