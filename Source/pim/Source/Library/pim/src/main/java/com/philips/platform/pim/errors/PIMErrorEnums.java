package com.philips.platform.pim.errors;

import android.content.Context;

import com.philips.platform.pim.R;

import net.openid.appauth.AuthorizationException;

public enum PIMErrorEnums {
    //General Errors
    INVALID_DISCOVERY_DOCUMENT(PIMErrorCodes.INVALID_DISCOVERY_DOCUMENT, AuthorizationException.GeneralErrors.INVALID_DISCOVERY_DOCUMENT.code, R.string.PIM_Error_Msg),
    USER_CANCELED_AUTH_FLOW(PIMErrorCodes.USER_CANCELED_AUTH_FLOW, AuthorizationException.GeneralErrors.USER_CANCELED_AUTH_FLOW.code, R.string.PIM_Error_Msg),
    PROGRAM_CANCELED_AUTH_FLOW(PIMErrorCodes.PROGRAM_CANCELED_AUTH_FLOW, AuthorizationException.GeneralErrors.PROGRAM_CANCELED_AUTH_FLOW.code, R.string.PIM_Error_Msg),
    NETWORK_ERROR(PIMErrorCodes.NETWORK_ERROR, AuthorizationException.GeneralErrors.NETWORK_ERROR.code, R.string.PIM_Internet_Error_Msg),
    SERVER_ERROR(PIMErrorCodes.SERVER_ERROR, AuthorizationException.GeneralErrors.SERVER_ERROR.code, R.string.PIM_Error_Msg),
    JSON_DESERIALIZATION_ERROR(PIMErrorCodes.JSON_DESERIALIZATION_ERROR, AuthorizationException.GeneralErrors.JSON_DESERIALIZATION_ERROR.code, R.string.PIM_Error_Msg),
    TOKEN_RESPONSE_CONSTRUCTION_ERROR(PIMErrorCodes.TOKEN_RESPONSE_CONSTRUCTION_ERROR, AuthorizationException.GeneralErrors.TOKEN_RESPONSE_CONSTRUCTION_ERROR.code, R.string.PIM_Error_Msg),
    INVALID_REGISTRATION_RESPONSE(PIMErrorCodes.INVALID_REGISTRATION_RESPONSE, AuthorizationException.GeneralErrors.INVALID_REGISTRATION_RESPONSE.code, R.string.PIM_Error_Msg),

    //AuthorizationRequestErrors,
    AUTH_REQUEST_STATE_MISMATCH(PIMErrorCodes.AUTH_REQUEST_STATE_MISMATCH, AuthorizationException.AuthorizationRequestErrors.STATE_MISMATCH.code, R.string.PIM_Error_Msg),
    AUTH_REQUEST_INVALID_REQUEST(PIMErrorCodes.AUTH_REQUEST_INVALID_REQUEST, AuthorizationException.AuthorizationRequestErrors.INVALID_REQUEST.code, R.string.PIM_Error_Msg),
    AUTH_REQUEST_UNAUTHORIZED_CLIENT(PIMErrorCodes.AUTH_REQUEST_UNAUTHORIZED_CLIENT, AuthorizationException.AuthorizationRequestErrors.UNAUTHORIZED_CLIENT.code, R.string.PIM_Error_Msg),
    AUTH_REQUEST_ACCESS_DENIED(PIMErrorCodes.AUTH_REQUEST_ACCESS_DENIED, AuthorizationException.AuthorizationRequestErrors.ACCESS_DENIED.code, R.string.PIM_Error_Msg),
    AUTH_REQUEST_UNSUPPORTED_RESPONSE_TYPE(PIMErrorCodes.AUTH_REQUEST_UNSUPPORTED_RESPONSE_TYPE, AuthorizationException.AuthorizationRequestErrors.UNSUPPORTED_RESPONSE_TYPE.code, R.string.PIM_Error_Msg),
    AUTH_REQUEST_INVALID_SCOPE(PIMErrorCodes.AUTH_REQUEST_INVALID_SCOPE, AuthorizationException.AuthorizationRequestErrors.INVALID_SCOPE.code, R.string.PIM_Error_Msg),
    AUTH_REQUEST_SERVER_ERROR(PIMErrorCodes.AUTH_REQUEST_SERVER_ERROR, AuthorizationException.AuthorizationRequestErrors.SERVER_ERROR.code, R.string.PIM_Error_Msg),
    AUTH_REQUEST_TEMPORARILY_UNAVAILABLE(PIMErrorCodes.AUTH_REQUEST_TEMPORARILY_UNAVAILABLE, AuthorizationException.AuthorizationRequestErrors.TEMPORARILY_UNAVAILABLE.code, R.string.PIM_Error_Msg),
    AUTH_REQUEST_CLIENT_ERROR(PIMErrorCodes.AUTH_REQUEST_CLIENT_ERROR, AuthorizationException.AuthorizationRequestErrors.CLIENT_ERROR.code, R.string.PIM_Error_Msg),
    AUTH_REQUEST_OTHERS(PIMErrorCodes.AUTH_REQUEST_OTHERS, AuthorizationException.AuthorizationRequestErrors.OTHER.code, R.string.PIM_Error_Msg),

    //TokenRequestErrors
    TOKEN_REQUEST_INVALID_REQUEST(PIMErrorCodes.TOKEN_REQUEST_INVALID_REQUEST, AuthorizationException.TokenRequestErrors.INVALID_REQUEST.code, R.string.PIM_Error_Msg),
    TOKEN_REQUEST_INVALID_CLIENT(PIMErrorCodes.TOKEN_REQUEST_INVALID_CLIENT, AuthorizationException.TokenRequestErrors.INVALID_CLIENT.code, R.string.PIM_Error_Msg),
    TOKEN_REQUEST_INVALID_GRANT(PIMErrorCodes.TOKEN_REQUEST_INVALID_GRANT, AuthorizationException.TokenRequestErrors.INVALID_GRANT.code, R.string.PIM_Error_Msg),
    TOKEN_REQUEST_UNAUTHORIZED_CLIENT(PIMErrorCodes.TOKEN_REQUEST_UNAUTHORIZED_CLIENT, AuthorizationException.TokenRequestErrors.UNAUTHORIZED_CLIENT.code, R.string.PIM_Error_Msg),
    TOKEN_REQUEST_UNSUPPORTED_GRANT_TYPE(PIMErrorCodes.TOKEN_REQUEST_UNSUPPORTED_GRANT_TYPE, AuthorizationException.TokenRequestErrors.UNSUPPORTED_GRANT_TYPE.code, R.string.PIM_Error_Msg),
    TOKEN_REQUEST_INVALID_SCOPE(PIMErrorCodes.TOKEN_REQUEST_INVALID_SCOPE, AuthorizationException.TokenRequestErrors.INVALID_SCOPE.code, R.string.PIM_Error_Msg),
    TOKEN_REQUEST_CLIENT_ERROR(PIMErrorCodes.TOKEN_REQUEST_CLIENT_ERROR, AuthorizationException.TokenRequestErrors.CLIENT_ERROR.code, R.string.PIM_Error_Msg),
    TOKEN_REQUEST_OTHERS(PIMErrorCodes.TOKEN_REQUEST_OTHERS, AuthorizationException.TokenRequestErrors.OTHER.code, R.string.PIM_Error_Msg),

    //RegistrationRequestErrors
    REGISTRATION_REQUEST_INVALID_REQUEST(PIMErrorCodes.REGISTRATION_REQUEST_INVALID_REQUEST, AuthorizationException.RegistrationRequestErrors.INVALID_REQUEST.code, R.string.PIM_Error_Msg),
    REGISTRATION_REQUEST_INVALID_REDIRECT_URI(PIMErrorCodes.REGISTRATION_REQUEST_INVALID_REDIRECT_URI, AuthorizationException.RegistrationRequestErrors.INVALID_REDIRECT_URI.code, R.string.PIM_Error_Msg),
    REGISTRATION_REQUEST_INVALID_CLIENT_METADATA(PIMErrorCodes.REGISTRATION_REQUEST_INVALID_CLIENT_METADATA, AuthorizationException.RegistrationRequestErrors.INVALID_CLIENT_METADATA.code, R.string.PIM_Error_Msg),
    REGISTRATION_REQUEST_CLIENT_ERROR(PIMErrorCodes.REGISTRATION_REQUEST_CLIENT_ERROR, AuthorizationException.RegistrationRequestErrors.CLIENT_ERROR.code, R.string.PIM_Error_Msg),
    REGISTRATION_REQUEST_OTHERS(PIMErrorCodes.REGISTRATION_REQUEST_OTHERS, AuthorizationException.RegistrationRequestErrors.OTHER.code, R.string.PIM_Error_Msg),

    //CIM Error
    MIGRATION_FAILED(PIMErrorCodes.MIGRATION_FAILED, R.string.PIM_Error_Msg),
    MARKETING_OPTIN_ERROR(PIMErrorCodes.MARKETING_OPTIN_ERROR, R.string.PIM_Error_Msg),


    //Service Discovery Error
    SERVICE_DISCOVERY_ERROR(PIMErrorCodes.SERVICE_DISCOVERY_ERROR, R.string.PIM_Error_Msg),

    //OIDC download failed
    OIDC_DOWNLOAD_FAILED_ERROR(PIMErrorCodes.OIDC_DOWNLOAD_FAILED_ERROR, R.string.PIM_Error_Msg);

    public int errorCode;
    public int stringId;
    private int innerCode;

    PIMErrorEnums(int errorCode, int stringId) {
        this.errorCode = errorCode;
        this.stringId = stringId;
    }

    PIMErrorEnums(int errorCode, int innerCode, int stringId) {
        this.errorCode = errorCode;
        this.stringId = stringId;
        this.innerCode = innerCode;
    }

    public static int getErrorCode(int innerCode) {
        for (PIMErrorEnums pimErrorEnums : PIMErrorEnums.values()) {
            if (innerCode == pimErrorEnums.innerCode) {
                return pimErrorEnums.errorCode;
            }
        }
        return innerCode;
    }

    public static String getLocalisedErrorDesc(Context context, int errorCode) {
        return String.format(context.getResources().getString(getResIDFrmErrCode(errorCode)), errorCode);
    }

    private static int getResIDFrmErrCode(int errorCode) {
        for (PIMErrorEnums pimErrorEnums : PIMErrorEnums.values()) {
            if (errorCode == pimErrorEnums.errorCode) {
                return pimErrorEnums.stringId;
            }
        }
        return -1;
    }
}
