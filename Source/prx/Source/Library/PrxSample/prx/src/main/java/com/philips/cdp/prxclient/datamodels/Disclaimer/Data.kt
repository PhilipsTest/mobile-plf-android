package com.philips.cdp.prxclient.datamodels.Disclaimer

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Data {

    @SerializedName("disclaimers")
    @Expose
    var disclaimers: Disclaimers? = null

    constructor() {}
    constructor(disclaimers: Disclaimers?) {
        this.disclaimers = disclaimers
    }
}