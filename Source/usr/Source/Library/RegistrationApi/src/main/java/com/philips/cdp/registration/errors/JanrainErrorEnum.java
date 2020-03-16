package com.philips.cdp.registration.errors;

import android.content.Context;

import com.philips.cdp.registration.R;
import com.philips.cdp.registration.ui.utils.RegConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by philips on 4/17/18.
 */

public enum JanrainErrorEnum {

    JANRAIN_UNKNOWN_ERROR(ErrorCodes.UNKNOWN_ERROR, R.string.USR_UnexpectedInternalError_ErrorMsg),
    JANRAIN_CONNECTION_LOST_NO_ARGUMENT(ErrorCodes.JANRAIN_CONNECTION_LOST_NO_ARGUMENT, R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg),
    JANRAIN_CONNECTION_LOST_INVALID_ARGUMENT(ErrorCodes.JANRAIN_CONNECTION_LOST_INVALID_ARGUMENT, R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg),
    JANRAIN_CONNECTION_LOST_MISMATCH_ARGUMENT(ErrorCodes.JANRAIN_CONNECTION_LOST_MISMATCH_ARGUMENT, R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg),
    JANRAIN_CONNECTION_LOST_INVALID_REQUEST_TYPE(ErrorCodes.JANRAIN_CONNECTION_LOST_INVALID_REQUEST_TYPE, R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg),
    JANRAIN_WRONG_USERID_PASSWORD(ErrorCodes.JANRAIN_WRONG_USERID_PASSWORD, R.string.USR_Janrain_Invalid_Credentials_ErrorMsg),
    JANRAIN_WRONG_USERID_PASSWORD_SOCIAL(ErrorCodes.JANRAIN_WRONG_USERID_PASSWORD_SOCIAL, R.string.USR_Janrain_Invalid_Credentials_ErrorMsg),
    JANRAIN_EMAIL_ADDRESS_NOT_AVAILABLE(ErrorCodes.JANRAIN_EMAIL_ADDRESS_NOT_AVAILABLE, R.string.USR_Janrain_Invalid_Credentials_ErrorMsg),
    JANRAIN_WRONG_PASSWORD(ErrorCodes.JANRAIN_WRONG_PASSWORD, R.string.USR_Janrain_Invalid_Credentials_ErrorMsg),
    JANRAIN_CONNECTION_LOST_APPID_NOT_EXIST(ErrorCodes.JANRAIN_CONNECTION_LOST_APPID_NOT_EXIST, R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg),
    JANRAIN_CONNECTION_LOST_ENTITY_TYPE_NOT_EXIST(ErrorCodes.JANRAIN_CONNECTION_LOST_ENTITY_TYPE_NOT_EXIST, R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg),
    JANRAIN_CONNECTION_LOST_ATTRIBUTE_NOT_EXIST(ErrorCodes.JANRAIN_CONNECTION_LOST_ATTRIBUTE_NOT_EXIST, R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg),
    JANRAIN_CONNECTION_LOST_NO_APPLICATION(ErrorCodes.JANRAIN_CONNECTION_LOST_NO_APPLICATION, R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg),
    JANRAIN_CONNECTION_LOST_MISCONFIGURED_FLOW(ErrorCodes.JANRAIN_CONNECTION_LOST_MISCONFIGURED_FLOW, R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg),
    JANRAIN_CONNECTION_LOST_ENITY_ALREADY_EXIST(ErrorCodes.JANRAIN_CONNECTION_LOST_ENITY_ALREADY_EXIST, R.string.USR_Janrain_EntityAlreadyExists_ErrorMsg),
    JANRAIN_CONNECTION_LOST_ATTRIBUTE_ALREADY_EXIST(ErrorCodes.JANRAIN_CONNECTION_LOST_ATTRIBUTE_ALREADY_EXIST, R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg),
    JANRAIN_CONNECTION_LOST_MODIFY_ATTRIBUTE(ErrorCodes.JANRAIN_CONNECTION_LOST_MODIFY_ATTRIBUTE, R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg),
    JANRAIN_CONNECTION_LOST_CREATE_RECORD_FAILED(ErrorCodes.JANRAIN_CONNECTION_LOST_CREATE_RECORD_FAILED, R.string.USR_UnexpectedInternalError_ErrorMsg),
    JANRAIN_CONNECTION_LOST_ENTITY_NOT_AVAILABLE(ErrorCodes.JANRAIN_CONNECTION_LOST_ENTITY_NOT_AVAILABLE, R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg),
    JANRAIN_CONNECTION_LOST_CREATE_RECORD_ID_FAILED(ErrorCodes.JANRAIN_CONNECTION_LOST_CREATE_RECORD_ID_FAILED, R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg),
    JANRAIN_CONNECTION_LOST_ARGUMENT_NOT_MATCHIN(ErrorCodes.JANRAIN_CONNECTION_LOST_ARGUMENT_NOT_MATCHIN, R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg),
    JANRAIN_CONNECTION_LOST_JSON_FORMAT_ERROR(ErrorCodes.JANRAIN_CONNECTION_LOST_JSON_FORMAT_ERROR, R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg),
    JANRAIN_CONNECTION_LOST_JSON_VALUE_MISMATCH(ErrorCodes.JANRAIN_CONNECTION_LOST_JSON_VALUE_MISMATCH, R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg),
    JANRAIN_CONNECTION_LOST_INVALID_DATE(ErrorCodes.JANRAIN_CONNECTION_LOST_INVALID_DATE, R.string.USR_Janrain_HSDP_ServerErrorMsg),
    JANRAIN_CONNECTION_LOST_CONSTRAINT_VIOLATED(ErrorCodes.JANRAIN_CONNECTION_LOST_CONSTRAINT_VIOLATED, R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg),
    JANRAIN_CONNECTION_LOST_UNIQUE_CONSTRAINT_VIOLATED(ErrorCodes.JANRAIN_CONNECTION_LOST_UNIQUE_CONSTRAINT_VIOLATED, R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg),
    JANRAIN_CONNECTION_LOST_EMPTY_OR_NULL_CONSTRAINT(ErrorCodes.JANRAIN_CONNECTION_LOST_EMPTY_OR_NULL_CONSTRAINT, R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg),
    JANRAIN_ATTRIBUTE_CONSTRAINT_LENGHT_VIOLATION(ErrorCodes.JANRAIN_ATTRIBUTE_CONSTRAINT_LENGHT_VIOLATION, R.string.USR_MaxiumCharacters_ErrorMsg),
    JANRAIN_USER_ALREADY_EXIST(ErrorCodes.JANRAIN_USER_ALREADY_EXIST, R.string.USR_Janrain_EmailAddressInUse_ErrorMsg),
    JANRAIN_INVALID_DATA_FOR_VALIDATION(ErrorCodes.JANRAIN_INVALID_DATA_FOR_VALIDATION, R.string.USR_Janrain_HSDP_ServerErrorMsg),
    JANRAIN_WRONG_CLIENT_ID(ErrorCodes.JANRAIN_WRONG_CLIENT_ID, R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg),
    JANRAIN_UNAUTHORIZED_CLIENT(ErrorCodes.JANRAIN_UNAUTHORIZED_CLIENT, R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg),
    JANRAIN_UNAUTHORIZED_USER(ErrorCodes.JANRAIN_UNAUTHORIZED_USER, R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg),
    JANRAIN_ACCESS_TOKEN_EXPIRED(ErrorCodes.JANRAIN_ACCESS_TOKEN_EXPIRED, R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg),
    JANRAIN_AUTHORIZATION_CODE_EXPIRED(ErrorCodes.JANRAIN_AUTHORIZATION_CODE_EXPIRED, R.string.USR_Generic_Network_ErrorMsg),
    JANRAIN_VERIFICATION_CODE_EXPIRED(ErrorCodes.JANRAIN_VERIFICATION_CODE_EXPIRED, R.string.USR_Janrain_VerificationCodeExpired_ErrorMsg),
    JANRAIN_CREATION_TOKEN_EXPIRED(ErrorCodes.JANRAIN_CREATION_TOKEN_EXPIRED, R.string.USR_Error_PleaseTryLater_Txt),
    JANRAIN_REDIRECT_URI_MISMATCH(ErrorCodes.JANRAIN_REDIRECT_URI_MISMATCH, R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg),
    JANRAIN_OPERATION_TEMPORARY_UNAVAILABLE(ErrorCodes.JANRAIN_OPERATION_TEMPORARY_UNAVAILABLE, R.string.USR_Janrain_HSDP_ServerErrorMsg),
    JANRAIN_UNEXPECTED_INTERNAL_ERROR(ErrorCodes.JANRAIN_UNEXPECTED_INTERNAL_ERROR, R.string.USR_UnexpectedInternalError_ErrorMsg),
    JANRAIN_API_CALL_LIMIT_REACHED(ErrorCodes.JANRAIN_API_CALL_LIMIT_REACHED, R.string.USR_Janrain_LimitError_ErrorMsg),
    TRADITIONAL_LOGIN_FAILED_SERVER_ERROR(ErrorCodes.TRADITIONAL_LOGIN_FAILED_SERVER_ERROR, R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg),
    FORGOT_PASSWORD_FAILED_SERVER_ERROR(ErrorCodes.FORGOT_PASSWORD_FAILED_SERVER_ERROR, R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg),
    SOCIAL_LOGIN_FAILED_SERVER_ERROR(ErrorCodes.SOCIAL_LOGIN_FAILED_SERVER_ERROR, R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg),
    RESEND_MAIL_FAILED_SERVER_ERROR(ErrorCodes.RESEND_MAIL_FAILED_SERVER_ERROR, R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg),
    JANRAIN_FLOW_DOWNLOAD_ERROR(ErrorCodes.JANRAIN_FLOW_DOWNLOAD_ERROR, R.string.USR_Janrain_HSDP_ServerErrorMsg),
    JANRAIN_ERROR_ON_FLOW(ErrorCodes.JANRAIN_ERROR_ON_FLOW, R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg),
    UPDATE_USER_DETAILS_ERROR(ErrorCodes.UPDATE_USER_DETAILS_ERROR,R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg);

    int errorCode;
    int stringId;

    private int getStringId() {
        return stringId;
    }

    JanrainErrorEnum(int errorCode, int stringId) {
        this.errorCode = errorCode;
        this.stringId = stringId;
    }

    public static int getStringId(int errorCode) {
        for (JanrainErrorEnum janrainErrorEnum : JanrainErrorEnum.values()) {
            if (errorCode == janrainErrorEnum.errorCode) {
                return janrainErrorEnum.getStringId();
            }
        }
        return RegConstants.UNKNOWN_ERROR_ID;
    }


    private static final List<Integer> janrainErrorCode = new ArrayList<>();


    static {
        janrainErrorCode.add(100);
        janrainErrorCode.add(200);
        janrainErrorCode.add(201);
        janrainErrorCode.add(205);
        janrainErrorCode.add(221);
        janrainErrorCode.add(222);
        janrainErrorCode.add(223);
        janrainErrorCode.add(224);
        janrainErrorCode.add(226);
        janrainErrorCode.add(233);
        janrainErrorCode.add(234);
        janrainErrorCode.add(310);
        janrainErrorCode.add(320);
        janrainErrorCode.add(330);
        janrainErrorCode.add(340);
        janrainErrorCode.add(341);
        janrainErrorCode.add(342);
        janrainErrorCode.add(360);
        janrainErrorCode.add(361);
        janrainErrorCode.add(362);
        janrainErrorCode.add(390);
        janrainErrorCode.add(402);
        janrainErrorCode.add(403);
        janrainErrorCode.add(413);
        janrainErrorCode.add(420);
        janrainErrorCode.add(480);
        janrainErrorCode.add(540);
    }

    public static String getLocalizedError(Context context, int errorCode) {
        if (janrainErrorCode.contains(errorCode)) {
            return String.format(getUSR_JanrainFormattedError(context, errorCode), errorCode);
        }
        return getUSR_JanrainFormattedError(context, errorCode);
    }

    private static String getUSR_JanrainFormattedError(Context context, int errorCode) {

        int stringId = getStringId(errorCode);

        if (stringId == R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg) {
            return String.format(context.getResources().getString(stringId), errorCode);
        } else if (stringId == R.string.USR_Janrain_Invalid_Credentials_ErrorMsg) {
            return context.getString(R.string.USR_Janrain_Invalid_Credentials);
        } else if (stringId == R.string.USR_Janrain_EntityAlreadyExists_ErrorMsg) {
            return String.format(context.getString(getStringId(errorCode)), context.getString(R.string.USR_DLS_Email_Phone_Label_Text));
        } else if (stringId == R.string.USR_Janrain_AuthorizationCodeExpired_ErrorMsg) {
            return String.format(context.getString(getStringId(errorCode)), context.getString(R.string.USR_UnexpectedInternalError_ErrorMsg));
        }
        if (stringId == RegConstants.UNKNOWN_ERROR_ID) {
            stringId = R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg;

            return String.format(context.getResources().getString(stringId), errorCode);

        }
        return context.getString(stringId);
    }
}
