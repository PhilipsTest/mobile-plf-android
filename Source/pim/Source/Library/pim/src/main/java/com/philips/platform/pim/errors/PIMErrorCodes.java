package com.philips.platform.pim.errors;

public class PIMErrorCodes {

    //General Errors
    public static final int INVALID_DISCOVERY_DOCUMENT = 7000;
    public static final int USER_CANCELED_AUTH_FLOW = 7001;
    public static final int PROGRAM_CANCELED_AUTH_FLOW = 7002;
    public static final int NETWORK_ERROR = 7003;
    public static final int SERVER_ERROR = 7004;
    public static final int JSON_DESERIALIZATION_ERROR = 7005;
    public static final int TOKEN_RESPONSE_CONSTRUCTION_ERROR = 7006;
    public static final int INVALID_REGISTRATION_RESPONSE = 7007;

    //AuthorizationRequestErrors
    public static final int AUTH_REQUEST_STATE_MISMATCH = 7009;
    public static final int AUTH_REQUEST_INVALID_REQUEST = 7100;
    public static final int AUTH_REQUEST_UNAUTHORIZED_CLIENT = 7101;
    public static final int AUTH_REQUEST_ACCESS_DENIED = 7102;
    public static final int AUTH_REQUEST_UNSUPPORTED_RESPONSE_TYPE = 7103;
    public static final int AUTH_REQUEST_INVALID_SCOPE = 7104;
    public static final int AUTH_REQUEST_SERVER_ERROR = 7105;
    public static final int AUTH_REQUEST_TEMPORARILY_UNAVAILABLE = 7106;
    public static final int AUTH_REQUEST_CLIENT_ERROR = 7107;
    public static final int AUTH_REQUEST_OTHERS = 7108;

    //TokenRequestErrors
    public static final int TOKEN_REQUEST_INVALID_REQUEST = 7200;
    public static final int TOKEN_REQUEST_INVALID_CLIENT = 7201;
    public static final int TOKEN_REQUEST_INVALID_GRANT = 7202;
    public static final int TOKEN_REQUEST_UNAUTHORIZED_CLIENT = 7203;
    public static final int TOKEN_REQUEST_UNSUPPORTED_GRANT_TYPE = 7204;
    public static final int TOKEN_REQUEST_INVALID_SCOPE = 7205;
    public static final int TOKEN_REQUEST_CLIENT_ERROR = 7206;
    public static final int TOKEN_REQUEST_OTHERS = 7207;

    //RegistrationRequestErrors
    public static final int REGISTRATION_REQUEST_INVALID_REQUEST = 7300;
    public static final int REGISTRATION_REQUEST_INVALID_REDIRECT_URI = 7301;
    public static final int REGISTRATION_REQUEST_INVALID_CLIENT_METADATA = 7302;
    public static final int REGISTRATION_REQUEST_CLIENT_ERROR = 7303;
    public static final int REGISTRATION_REQUEST_OTHERS = 7304;

    //CIM Error
    public static final int MIGRATION_FAILED = 7500;
    public static final int MARKETING_OPTIN_ERROR = 7501;

    //Service Discovery Error
    public static final int SERVICE_DISCOVERY_ERROR = 7600;

    //OIDC download failed
    public static final int OIDC_DOWNLOAD_FAILED_ERROR = 7700;

}
