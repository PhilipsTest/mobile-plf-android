package com.philips.platform.ths.sdkerrors;


import com.americanwell.sdk.entity.SDKErrorReason;

public class THSSDKSpecificError implements THSErrorHandlerInterface {

    String errorMessage = "";
    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean validate(SDKErrorReason sdkErrorReason) {
        if(sdkErrorReason.name().equalsIgnoreCase(SDKErrorReason.ONDEMAND_SPECIALTY_NOT_FOUND.name())){
            errorMessage = "onDemand provider unavailable";
            return true;
        }else if(sdkErrorReason.name().equalsIgnoreCase(SDKErrorReason.PRIVACY_DISCLAIMER_MISSING.name())){
            errorMessage = "privacy disclaimer missing";
            return true;
        }else if(sdkErrorReason.name().equalsIgnoreCase(SDKErrorReason.VALIDATION_MEMBER_UNDERAGE.name())){
            errorMessage = "Customer underage";
            return true;
        }else if(sdkErrorReason.name().equalsIgnoreCase(SDKErrorReason.VALIDATION_EMAIL_IN_USE.name())){
            errorMessage = "Email is already in use";
            return true;
        }else if(sdkErrorReason.name().equalsIgnoreCase(SDKErrorReason.VALIDATION_REQ_PARAM_TOO_SHORT.name())){
            errorMessage = "";
            return true;
        }else if(sdkErrorReason.name().equalsIgnoreCase(SDKErrorReason.VALIDATION_BAD_COORDINATE_FORMAT.name())){
            errorMessage = "Improperly formatted longitude and/or latitude";
            return true;
        }else if(sdkErrorReason.name().equalsIgnoreCase(SDKErrorReason.VALIDATION_BAD_INTEGER_FORMAT.name())){
            errorMessage = "Improperly formatted value for radius";
            return true;
        }else if(sdkErrorReason.name().equalsIgnoreCase(SDKErrorReason.MEMBER_PRIMARY_PHARMACY_NOT_FOUND.name())){
            errorMessage = "Primary pharmacy not found";
            return true;
        }else if(sdkErrorReason.name().equalsIgnoreCase(SDKErrorReason.CREDIT_CARD_MISSING.name())){
            errorMessage = "Credit card missing";
            return true;
        }else if(sdkErrorReason.name().equalsIgnoreCase(SDKErrorReason.VALIDATION_INVALID_COUPON.name())){
            errorMessage = "Invalid coupon code";
            return true;
        }else {
            return false;
        }
    }
}
