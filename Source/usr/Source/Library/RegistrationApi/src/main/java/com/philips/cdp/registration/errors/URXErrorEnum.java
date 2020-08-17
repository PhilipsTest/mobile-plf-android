package com.philips.cdp.registration.errors;

import android.content.Context;

import com.philips.cdp.registration.R;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegConstants;

/**
 * Created by philips on 4/17/18.
 */

public enum URXErrorEnum {

    URX_SMS_INVALID_NUMBER(ErrorCodes.URX_INVALID_PHONENUMBER, R.string.USR_URX_SMS_Invalid_PhoneNumber),
    URX_PHONENUMBER_NOT_REGISTERED(ErrorCodes.URX_PHONENUMBER_NOT_REGISTERED, R.string.USR_URX_SMS_Invalid_PhoneNumber),
    URX_SMS__UNAVAILABLE_NUMBER(ErrorCodes.URX_PHONENUMBER_UNAVAILABLE, R.string.USR_URX_SMS_Invalid_PhoneNumber),
    URX_SMS_UNSUPPORTED_COUNTRY(ErrorCodes.URX_UNSUPPORTED_COUNTRY, R.string.USR_URX_SMS_UnSupported_Country_ForSMS),
    URX_SMS_LIMIT_REACHED(ErrorCodes.URX_SMS_LIMIT_REACHED, R.string.USR_URX_SMS_Limit_Reached),
    URX_SMS_INTERNAL_SERVER_ERROR(ErrorCodes.URX_SMS_INTERNAL_SERVER_ERROR, R.string.USR_URX_SMS_InternalServerError),
    URX_SMS_NO_INFO(ErrorCodes.URX_NO_INFO_AVAILABLE, R.string.USR_URX_SMS_NoInformation_Available),
    URX_SMS_NOT_SENT(ErrorCodes.URX_SMS_NOT_SENT, R.string.USR_URX_SMS_Not_Sent),
    URX_SMS_ALREADY_VERIFIED(ErrorCodes.URX_SMS_ACCOUNT_ALREADY_VERIFIED, R.string.USR_URX_SMS_Already_Verified),
    URX_SMS_FAILURE_CASE(ErrorCodes.URX_MOBILE_ACCOUNT_FAIURE, R.string.USR_VerificationCode_ErrorText),
    URX_INVALID_VERIFICATION_CODE(ErrorCodes.URX_INVALID_VERIFICATION_CODE, R.string.USR_VerificationCode_ErrorText);

    int errorCode;
    int stringId;
    private static int TIME_LIMIT = 60;

    private int getStringId() {
        return stringId;
    }

    URXErrorEnum(int errorCode, int stringId) {
        this.errorCode = errorCode;
        this.stringId = stringId;
    }

    public static int getStringId(int errorCode) {

        for (URXErrorEnum urxErrorEnum : URXErrorEnum.values()) {

            if (errorCode == urxErrorEnum.errorCode) {
                return urxErrorEnum.getStringId();
            }
        }
        return RegConstants.UNKNOWN_ERROR_ID;
    }


    public static String getLocalizedError(Context context, int errorCode){
        return getURXFormattedError(context,errorCode);
    }

    private static String getURXFormattedError(Context context, int errorCode) {

        switch (errorCode) {

            case ErrorCodes.URX_UNSUPPORTED_COUNTRY:
                return String.format(context.getString(getStringId(ErrorCodes.URX_UNSUPPORTED_COUNTRY)), context.getString(URXErrorEnum.getStringId(ErrorCodes.URX_INVALID_PHONENUMBER)));

            case ErrorCodes.URX_SMS_INTERNAL_SERVER_ERROR:
                return String.format(context.getString(URXErrorEnum.getStringId(ErrorCodes.URX_SMS_INTERNAL_SERVER_ERROR)), context.getString(R.string.USR_Error_PleaseTryLater_Txt));

            case ErrorCodes.URX_SMS_LIMIT_REACHED:
                return String.format(context.getString(URXErrorEnum.getStringId(ErrorCodes.URX_SMS_LIMIT_REACHED)), TIME_LIMIT);

            case ErrorCodes.URX_NO_INFO_AVAILABLE:
                return String.format(context.getString(URXErrorEnum.getStringId(ErrorCodes.URX_NO_INFO_AVAILABLE)), context.getString(R.string.USR_Error_PleaseTryLater_Txt));

            case ErrorCodes.URX_SMS_NOT_SENT:
                return String.format(context.getString(URXErrorEnum.getStringId(ErrorCodes.URX_SMS_NOT_SENT)), context.getString(R.string.USR_Error_PleaseTryLater_Txt));

               /* case ErrorCodes.JANRAIN_INVALID_DATA_FOR_VALIDATION:
                    RLog.i("TAG", "JANRAIN_INVALID_DATA_FOR_VALIDATION ");
                    return "This number has been registered" +" "+ "["+errorCode+"]";*/

            default:

                if(getStringId(errorCode) == RegConstants.UNKNOWN_ERROR_ID ){
                    return context.getString(R.string.USR_Generic_Network_ErrorMsg) +" "+ "["+errorCode+"]";
                }
                return context.getString(getStringId(errorCode));

        }

    }



}
