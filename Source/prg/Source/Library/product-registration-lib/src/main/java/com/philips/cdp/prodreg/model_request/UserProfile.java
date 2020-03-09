
package com.philips.cdp.prodreg.model_request;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class UserProfile {

    @SerializedName("optIn")
    private Boolean mOptIn;

    public Boolean getOptIn() {
        return mOptIn;
    }

    public void setOptIn(Boolean optIn) {
        mOptIn = optIn;
    }

}
