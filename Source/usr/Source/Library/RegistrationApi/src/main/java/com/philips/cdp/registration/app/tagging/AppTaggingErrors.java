
/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.app.tagging;


import com.philips.cdp.registration.dao.UserRegistrationFailureInfo;
import com.philips.cdp.registration.errors.ErrorCodes;
import com.philips.cdp.registration.settings.RegistrationHelper;

public class AppTaggingErrors {

    private static final String EMAIL_ALREADY_IN_USE = "email already in use";

    private static final String MOBILE_ALREADY_IN_USE = "mobile no already in use";

    private static final String EMAIL_IS_NOT_VERIFIED = "email is not verified";

    private static final String WE_RE_HAVING_TROUBLE_LOGINING_USER = "login network error";

    private static final String WE_RE_HAVING_TROUBLE_REGISTRING_USER = "registration network error";

    private static final String RESEND_VERIFICATION_NETWORK_ERROR = "resend verification network error";

    private static final String FAILURE_FORGOT_PASSWORD_ERROR = "forgot password network error";

    public static void trackActionForgotPasswordFailure(UserRegistrationFailureInfo userRegistrationFailureInfo, String flowType) {
        switch (userRegistrationFailureInfo.getErrorCode()) {

            case ErrorCodes.NETWORK_ERROR:
                trackActionForErrorMapping(AppTagingConstants.SEND_DATA,
                        AppTagingConstants.INFORMATIONAL_ERROR, FAILURE_FORGOT_PASSWORD_ERROR);
                break;
            case ErrorCodes.JANRAIN_EMAIL_ADDRESS_NOT_AVAILABLE:
                AppTagging.trackAction(AppTagingConstants.SEND_DATA, AppTagingConstants.USER_ERROR, AppTagingConstants.USER_REGISTRATION + ":" + AppTagingConstants.FAILURE_FORGOT_PASSWORD + ":"
                        + flowType + ":" + userRegistrationFailureInfo.getErrorCode() + ":" + userRegistrationFailureInfo.getErrorTagging());
                break;

            default:
                AppTagging.trackAction(AppTagingConstants.SEND_DATA, AppTagingConstants.TECHNICAL_ERROR, AppTagingConstants.USER_REGISTRATION + ":" + AppTagingConstants.FAILURE_FORGOT_PASSWORD + ":"
                        + flowType + ":" + userRegistrationFailureInfo.getErrorCode() + ":" + userRegistrationFailureInfo.getErrorTagging());
                break;
        }
    }

    public static void trackActionResendNetworkFailure(UserRegistrationFailureInfo userRegistrationFailureInfo, String flowType) {
        switch (userRegistrationFailureInfo.getErrorCode()) {

            case ErrorCodes.NETWORK_ERROR:
                trackActionForErrorMapping(AppTagingConstants.SEND_DATA,
                        AppTagingConstants.INFORMATIONAL_ERROR, RESEND_VERIFICATION_NETWORK_ERROR);
                break;

            default:
                AppTagging.trackAction(AppTagingConstants.SEND_DATA, AppTagingConstants.INFORMATIONAL_ERROR, AppTagingConstants.USER_REGISTRATION + ":" + AppTagingConstants.FAILURE_RESEND_EMAIL + ":"
                        + flowType + ":" + userRegistrationFailureInfo.getErrorCode() + ":" + userRegistrationFailureInfo.getErrorTagging());
                break;
        }
    }

    public static void trackActionForErrorMapping(String sendData, String technicalError,
                                                   String technicalRegistrationError) {
        AppTagging.trackAction(sendData, technicalError, technicalRegistrationError);
    }

    public static void trackActionLoginError(UserRegistrationFailureInfo userRegistrationFailureInfo, String flowType) {

        switch (userRegistrationFailureInfo.getErrorCode()) {
            case ErrorCodes.NETWORK_ERROR:
                trackActionForErrorMapping(AppTagingConstants.SEND_DATA,
                        AppTagingConstants.INFORMATIONAL_ERROR, WE_RE_HAVING_TROUBLE_LOGINING_USER);
                break;
            case ErrorCodes.EMAIL_NOT_VERIFIED_CODE:
                trackActionForErrorMapping(AppTagingConstants.SEND_DATA,
                        AppTagingConstants.USER_ERROR, EMAIL_IS_NOT_VERIFIED);
                break;
                //URX Errors
            case ErrorCodes.URX_INVALID_PHONENUMBER:
            case ErrorCodes.URX_SMS_ACCOUNT_ALREADY_VERIFIED:
                //Janrain Errors
            case ErrorCodes.JANRAIN_WRONG_USERID_PASSWORD:
            case ErrorCodes.JANRAIN_WRONG_USERID_PASSWORD_SOCIAL:
            case ErrorCodes.JANRAIN_EMAIL_ADDRESS_NOT_AVAILABLE:
            case ErrorCodes.JANRAIN_WRONG_PASSWORD:
            case ErrorCodes.JANRAIN_CONNECTION_LOST_ENITY_ALREADY_EXIST:
            case ErrorCodes.JANRAIN_ATTRIBUTE_CONSTRAINT_LENGHT_VIOLATION:
            case ErrorCodes.JANRAIN_USER_ALREADY_EXIST:
            case ErrorCodes.JANRAIN_INVALID_DATA_FOR_VALIDATION:
            case ErrorCodes.JANRAIN_VERIFICATION_CODE_EXPIRED:
                AppTagging.trackAction(AppTagingConstants.SEND_DATA, AppTagingConstants.USER_ERROR, AppTagingConstants.USER_REGISTRATION + ":" + AppTagingConstants.LOGIN_FAILED + ":"
                        + flowType + ":" + userRegistrationFailureInfo.getErrorCode() + ":" + userRegistrationFailureInfo.getErrorTagging());
                break;
            default:
                AppTagging.trackAction(AppTagingConstants.SEND_DATA, AppTagingConstants.TECHNICAL_ERROR, AppTagingConstants.USER_REGISTRATION + ":" + AppTagingConstants.LOGIN_FAILED + ":"
                        + flowType + ":" + userRegistrationFailureInfo.getErrorCode() + ":" + userRegistrationFailureInfo.getErrorTagging());
                break;
        }

    }

    public static void trackActionRegisterError(UserRegistrationFailureInfo userRegistrationFailureInfo, String flowType) {

        switch (userRegistrationFailureInfo.getErrorCode()) {
            case ErrorCodes.NETWORK_ERROR:
                trackActionForErrorMapping(AppTagingConstants.SEND_DATA,
                        AppTagingConstants.INFORMATIONAL_ERROR, WE_RE_HAVING_TROUBLE_REGISTRING_USER);
                break;

            case ErrorCodes.JANRAIN_INVALID_DATA_FOR_VALIDATION:
                if (RegistrationHelper.getInstance().isMobileFlow()) {
                    trackActionForErrorMapping(AppTagingConstants.SEND_DATA,
                            AppTagingConstants.USER_ERROR, MOBILE_ALREADY_IN_USE);
                } else {
                    trackActionForErrorMapping(AppTagingConstants.SEND_DATA,
                            AppTagingConstants.USER_ERROR, EMAIL_ALREADY_IN_USE);
                }
                break;
                 //URX Errors
            case ErrorCodes.URX_INVALID_PHONENUMBER:
            case ErrorCodes.URX_SMS_ACCOUNT_ALREADY_VERIFIED:
                //Janrain Errors
            case ErrorCodes.JANRAIN_WRONG_USERID_PASSWORD:
            case ErrorCodes.JANRAIN_WRONG_USERID_PASSWORD_SOCIAL:
            case ErrorCodes.JANRAIN_EMAIL_ADDRESS_NOT_AVAILABLE:
            case ErrorCodes.JANRAIN_WRONG_PASSWORD:
            case ErrorCodes.JANRAIN_CONNECTION_LOST_ENITY_ALREADY_EXIST:
            case ErrorCodes.JANRAIN_ATTRIBUTE_CONSTRAINT_LENGHT_VIOLATION:
            case ErrorCodes.JANRAIN_USER_ALREADY_EXIST:
            case ErrorCodes.JANRAIN_VERIFICATION_CODE_EXPIRED:
                AppTagging.trackAction(AppTagingConstants.SEND_DATA, AppTagingConstants.USER_ERROR, AppTagingConstants.USER_REGISTRATION + ":" + AppTagingConstants.REGISTRATION_FAILED + ":"
                        + flowType + ":" + userRegistrationFailureInfo.getErrorCode() + ":" + userRegistrationFailureInfo.getErrorTagging());
                break;
            default:
                AppTagging.trackAction(AppTagingConstants.SEND_DATA, AppTagingConstants.TECHNICAL_ERROR, AppTagingConstants.USER_REGISTRATION + ":" + AppTagingConstants.REGISTRATION_FAILED + ":"
                        + flowType + ":" + userRegistrationFailureInfo.getErrorCode() + ":" + userRegistrationFailureInfo.getErrorTagging());
                break;
        }

    }
}
