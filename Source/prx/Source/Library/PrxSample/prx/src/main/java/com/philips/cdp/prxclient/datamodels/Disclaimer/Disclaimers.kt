package com.philips.cdp.prxclient.datamodels.Disclaimer

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class Disclaimers {
    @SerializedName("disclaimer")
    @Expose
    var disclaimer: List<Disclaimer> = ArrayList()

    /**
     * No args constructor for use in serialization
     */
    constructor() {}

    /**
     * @param disclaimer
     */
    constructor(disclaimer: List<Disclaimer>) {
        this.disclaimer = disclaimer
    }

}