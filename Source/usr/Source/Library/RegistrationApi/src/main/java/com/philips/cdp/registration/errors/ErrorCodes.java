package com.philips.cdp.registration.errors;

public class ErrorCodes {

    public static final int UNKNOWN_ERROR = -1;

    public static final int SOCIAL_SIGIN_IN_ONLY_CODE = 540;

    public static final int NETWORK_ERROR = -101;

    public static final int NO_NETWORK = -100;

    //URX

    public static final int URX_SUCCESS = 0;

    public static final int URX_INVALID_PHONENUMBER = 10;

    public static final int URX_PHONENUMBER_NOT_REGISTERED = 15;

    public static final int URX_PHONENUMBER_UNAVAILABLE = 20;

    public static final int URX_UNSUPPORTED_COUNTRY = 30;

    public static final int URX_SMS_LIMIT_REACHED = 40;

    public static final int URX_SMS_INTERNAL_SERVER_ERROR = 50;

    public static final int URX_NO_INFO_AVAILABLE = 60;

    public static final int URX_SMS_NOT_SENT = 70;

    public static final int URX_SMS_ACCOUNT_ALREADY_VERIFIED = 90;

    public static final int URX_MOBILE_ACCOUNT_FAIURE = 3200;

    public static final int URX_INVALID_VERIFICATION_CODE = 200;

    //JANRAIN

    public static final int JANRAIN_CONNECTION_LOST_NO_ARGUMENT = 100;

    public static final int JANRAIN_CONNECTION_LOST_INVALID_ARGUMENT = 200;

    public static final int JANRAIN_CONNECTION_LOST_MISMATCH_ARGUMENT = 201;

    public static final int JANRAIN_CONNECTION_LOST_INVALID_REQUEST_TYPE = 205;

    public static final int JANRAIN_WRONG_USERID_PASSWORD = 210;

    public static final int JANRAIN_WRONG_USERID_PASSWORD_SOCIAL = 211;

    public static final int JANRAIN_EMAIL_ADDRESS_NOT_AVAILABLE = 212;

    public static final int JANRAIN_WRONG_PASSWORD = 213;

    public static final int JANRAIN_CONNECTION_LOST_APPID_NOT_EXIST = 221;

    public static final int JANRAIN_CONNECTION_LOST_ENTITY_TYPE_NOT_EXIST = 222;

    public static final int JANRAIN_CONNECTION_LOST_ATTRIBUTE_NOT_EXIST = 223;

    public static final int JANRAIN_CONNECTION_LOST_NO_APPLICATION = 224;

    public static final int JANRAIN_CONNECTION_LOST_MISCONFIGURED_FLOW = 226;

    public static final int JANRAIN_CONNECTION_LOST_ENITY_ALREADY_EXIST = 232;

    public static final int JANRAIN_CONNECTION_LOST_ATTRIBUTE_ALREADY_EXIST = 233;

    public static final int JANRAIN_CONNECTION_LOST_MODIFY_ATTRIBUTE = 234;

    public static final int JANRAIN_CONNECTION_LOST_CREATE_RECORD_FAILED = 300;

    public static final int JANRAIN_CONNECTION_LOST_ENTITY_NOT_AVAILABLE = 310;

    public static final int JANRAIN_CONNECTION_LOST_CREATE_RECORD_ID_FAILED = 320;

    public static final int JANRAIN_CONNECTION_LOST_ARGUMENT_NOT_MATCHIN = 330;

    public static final int JANRAIN_CONNECTION_LOST_JSON_FORMAT_ERROR = 340;

    public static final int JANRAIN_CONNECTION_LOST_JSON_VALUE_MISMATCH = 341;

    public static final int JANRAIN_CONNECTION_LOST_INVALID_DATE = 342;

    public static final int JANRAIN_CONNECTION_LOST_CONSTRAINT_VIOLATED = 360;

    public static final int JANRAIN_CONNECTION_LOST_UNIQUE_CONSTRAINT_VIOLATED = 361;

    public static final int JANRAIN_CONNECTION_LOST_EMPTY_OR_NULL_CONSTRAINT = 362;

    public static final int JANRAIN_ATTRIBUTE_CONSTRAINT_LENGHT_VIOLATION = 363;

    public static final int JANRAIN_USER_ALREADY_EXIST = 380;

    public static final int JANRAIN_INVALID_DATA_FOR_VALIDATION = 390;

    public static final int JANRAIN_WRONG_CLIENT_ID = 402;

    public static final int JANRAIN_UNAUTHORIZED_CLIENT = 403;

    public static final int JANRAIN_UNAUTHORIZED_USER = 413;

    public static final int JANRAIN_ACCESS_TOKEN_EXPIRED = 414;

    public static final int JANRAIN_AUTHORIZATION_CODE_EXPIRED = 415;

    public static final int JANRAIN_VERIFICATION_CODE_EXPIRED = 416;

    public static final int JANRAIN_CREATION_TOKEN_EXPIRED = 417;

    public static final int JANRAIN_REDIRECT_URI_MISMATCH = 420;

    public static final int JANRAIN_OPERATION_TEMPORARY_UNAVAILABLE = 480;

    public static final int JANRAIN_UNEXPECTED_INTERNAL_ERROR = 500;

    public static final int JANRAIN_API_CALL_LIMIT_REACHED = 510;

    public static final int JANRAIN_ERROR_ON_FLOW = 540;


    //HSDP

    public static final int HSDP_SYSTEM_ERROR_403 = 403;

    public static final int HSDP_SYSTEM_ERROR_100 = 100;

    public static final int HSDP_CANNOT_LOGIN_NOW_104 = 104;

    public static final int HSDP_INPUT_ERROR_1008 = 1008;

    public static final int HSDP_INPUT_ERROR_1009 = 1009;

    public static final int HSDP_INPUT_ERROR_1112 = 1112;

    public static final int HSDP_INPUT_ERROR_1114 = 1114;

    public static final int HSDP_INPUT_ERROR_1151 = 1151;

    public static final int HSDP_CANNOT_LOGIN_NOW_1149 = 1149;

    public static final int HSDP_CANNOT_LOGIN_NOW_1150 = 1150;

    public static final int HSDP_INPUT_ERROR_1165 = 1165;

    public static final int HSDP_INPUT_ERROR_1166 = 1166;

    public static final int HSDP_INPUT_ERROR_1167 = 1167;

    public static final int HSDP_INPUT_ERROR_1175 = 1175;

    public static final int HSDP_INPUT_ERROR_1251 = 1251;

    public static final int HSDP_INPUT_ERROR_1252 = 1252;

    public static final int HSDP_INPUT_ERROR_1253 = 1253;

    public static final int HSDP_CHECK = 1254;

    public static final int HSDP_INPUT_ERROR_1260 = 1260;

    public static final int HSDP_INPUT_ERROR_1261 = 1261;

    public static final int HSDP_INPUT_ERROR_1262 = 1262;

    public static final int HSDP_INPUT_ERROR_1263 = 1263;

    public static final int HSDP_INPUT_ERROR_1265 = 1265;

    public static final int HSDP_INPUT_ERROR_1266 = 1266;

    public static final int HSDP_INPUT_ERROR_1267 = 1267;

    public static final int HSDP_INPUT_ERROR_1271 = 1271;

    public static final int HSDP_INPUT_ERROR_1272 = 1272;

    public static final int HSDP_INPUT_ERROR_1312 = 1312;

    public static final int HSDP_INVALID_1437 = 1437;

    public static final int HSDP_INPUT_ERROR_1571 = 1571;

    public static final int HSDP_INPUT_ERROR_1572 = 1572;

    public static final int HSDP_INPUT_ERROR_3055 = 3055;

    public static final int HSDP_TIME_ERROR_3056 = 3056;

    public static final int HSDP_INPUT_ERROR_3073 = 3073;

    public static final int HSDP_INPUT_ERROR_3074 = 3074;

    public static final int HSDP_INPUT_ERROR_3081 = 3081;

    public static final int HSDP_INPUT_ERROR_3160 = 3160;

    public static final int HSDP_INPUT_ERROR_3061 = 3161;

    public static final int HSDP_ACTIVATE_ACCOUNT_FAILED = 10000;

    //General
    public static final int TRADITIONAL_LOGIN_FAILED_SERVER_ERROR = 7001;

    public static final int SOCIAL_LOGIN_FAILED_SERVER_ERROR = 7002;

    public static final int REGISTER_TRADITIONAL_FAILED_SERVER_ERROR = 7003;

    public static final int FORGOT_PASSWORD_FAILED_SERVER_ERROR = 7004;

    public static final int RESEND_MAIL_FAILED_SERVER_ERROR = 7005;

    public static final int BAD_RESPONSE_ERROR_CODE = 7008;

    public static final int AUTHENTICATION_CANCELLED_BY_USER = 7010;

    public static final int HSDP_JANRAIN_NOT_SUCCESS_ERROR_7012 = 7012;

    public static final int UPDATE_USER_DETAILS_ERROR = 7013;

    public static final int JANRAIN_FLOW_DOWNLOAD_ERROR = 2000;

}
