
package com.philips.cdp.prodreg.model_request;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Meta {

    @SerializedName("sendEmail")
    private Boolean mSendEmail;

    public Boolean getSendEmail() {
        return mSendEmail;
    }

    public void setSendEmail(Boolean sendEmail) {
        mSendEmail = sendEmail;
    }

}
