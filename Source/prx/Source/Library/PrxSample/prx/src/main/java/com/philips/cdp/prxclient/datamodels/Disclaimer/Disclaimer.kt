package com.philips.cdp.prxclient.datamodels.Disclaimer

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Disclaimer {
    @SerializedName("disclaimerText")
    @Expose
    var disclaimerText: String? = null

    @SerializedName("code")
    @Expose
    var code: String? = null

    @SerializedName("rank")
    @Expose
    var rank: String? = null

    /* public List<String> getDisclaimElements() {
        return disclaimElements;
    }

    public void setDisclaimElements(List<String> disclaimElements) {
        this.disclaimElements = disclaimElements;
    }*/
    @SerializedName("referenceName")
    @Expose
    var referenceName: String? = null
    /* @SerializedName("disclaimElements")
    @Expose
    private List<String> disclaimElements = new ArrayList<String>();*/
    /**
     * No args constructor for use in serialization
     */
    constructor() {}
    constructor(disclaimerText: String?, code: String?, rank: String?, referenceName: String?, disclaimElements: List<String?>?) {
        this.disclaimerText = disclaimerText
        this.code = code
        this.rank = rank
        this.referenceName = referenceName
        //this.disclaimElements = disclaimElements;
    }

}