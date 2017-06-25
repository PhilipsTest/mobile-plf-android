package com.philips.amwelluapp.sdkerrors;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.SDKErrorReason;
import com.americanwell.sdk.entity.SDKResponseSuggestion;

public class PTHSDKError {
    SDKError sdkError;

    public SDKError getSdkError() {
        return sdkError;
    }

    public void setSdkError(SDKError sdkError) {
        this.sdkError = sdkError;
    }

    public SDKErrorReason getSDKErrorReason(){
        return sdkError.getSDKErrorReason();
    }

    public SDKResponseSuggestion getSDKResponseSuggestion(){
        return sdkError.getSDKResponseSuggestion();
    }

    public String getMessage(){
        return sdkError.getMessage();
    }

    public String getCsrPhoneNumber(){
        return sdkError.getCsrPhoneNumber();
    }

    public int getHttpResponseCode(){
        return sdkError.getHttpResponseCode();
    }
}
