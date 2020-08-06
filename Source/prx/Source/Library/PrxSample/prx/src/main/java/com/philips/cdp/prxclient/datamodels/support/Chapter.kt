package com.philips.cdp.prxclient.datamodels.support

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Chapter {
    /**
     *
     * @return
     * The name
     */
    /**
     *
     * @param name
     * The name
     */
    @SerializedName("name")
    @Expose
    var name: String? = null

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
     * The referenceName
     */
    /**
     *
     * @param referenceName
     * The referenceName
     */
    @SerializedName("referenceName")
    @Expose
    var referenceName: String? = null

}