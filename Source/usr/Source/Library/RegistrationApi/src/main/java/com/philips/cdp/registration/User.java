/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration;

import android.app.Activity;
import android.content.Context;

import com.facebook.login.LoginManager;
import com.janrain.android.Jump;
import com.janrain.android.capture.CaptureRecord;
import com.janrain.android.engage.session.JRSession;
import com.philips.cdp.registration.app.tagging.AppTagging;
import com.philips.cdp.registration.app.tagging.AppTagingConstants;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.controller.ForgotPassword;
import com.philips.cdp.registration.controller.HSDPLoginService;
import com.philips.cdp.registration.controller.LoginSocialNativeProvider;
import com.philips.cdp.registration.controller.LoginSocialProvider;
import com.philips.cdp.registration.controller.LoginTraditional;
import com.philips.cdp.registration.controller.RefreshUserSession;
import com.philips.cdp.registration.controller.RegisterSocial;
import com.philips.cdp.registration.controller.RegisterTraditional;
import com.philips.cdp.registration.controller.ResendVerificationEmail;
import com.philips.cdp.registration.controller.UpdateDateOfBirth;
import com.philips.cdp.registration.controller.UpdateGender;
import com.philips.cdp.registration.controller.UpdateReceiveMarketingEmail;
import com.philips.cdp.registration.dao.DIUserProfile;
import com.philips.cdp.registration.dao.UserRegistrationFailureInfo;
import com.philips.cdp.registration.errors.ErrorCodes;
import com.philips.cdp.registration.errors.ErrorType;
import com.philips.cdp.registration.errors.URError;
import com.philips.cdp.registration.events.UserRegistrationHelper;
import com.philips.cdp.registration.handlers.ForgotPasswordHandler;
import com.philips.cdp.registration.handlers.LoginHandler;
import com.philips.cdp.registration.handlers.LogoutHandler;
import com.philips.cdp.registration.handlers.RefreshLoginSessionHandler;
import com.philips.cdp.registration.handlers.RefreshUserHandler;
import com.philips.cdp.registration.handlers.RefreshandUpdateUserHandler;
import com.philips.cdp.registration.handlers.ResendVerificationEmailHandler;
import com.philips.cdp.registration.handlers.SocialLoginProviderHandler;
import com.philips.cdp.registration.handlers.TraditionalRegistrationHandler;
import com.philips.cdp.registration.hsdp.HsdpUser;
import com.philips.cdp.registration.hsdp.HsdpUserRecordV2;
import com.philips.cdp.registration.listener.HSDPAuthenticationListener;
import com.philips.cdp.registration.listener.UserRegistrationListener;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.ui.utils.FieldsValidator;
import com.philips.cdp.registration.ui.utils.Gender;
import com.philips.cdp.registration.ui.utils.NetworkUtility;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegConstants;
import com.philips.cdp.registration.ui.utils.ThreadUtils;
import com.philips.platform.appinfra.logging.CloudLoggingInterface;
import com.philips.platform.pif.DataInterface.USR.listeners.UpdateUserDetailsHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import static com.philips.cdp.registration.ui.utils.RegConstants.SOCIAL_PROVIDER_FACEBOOK;
import static com.philips.cdp.registration.ui.utils.RegPreferenceUtility.getPreferenceValue;

/**
 * {@code User} class represents information related to a logged in user of USR.
 * Additionally, it exposes APIs to login, logout and refresh operations for traditional and social accounts.
 *
 * @since 1.0.0
 *
 * @deprecated since 1903
 */
@Deprecated
public class User {

    private final String TAG = "User";
    private final CloudLoggingInterface cloudLoggingInterface;

    @Inject
    NetworkUtility networkUtility;

    private Context mContext;

    private String USER_EMAIL = "email";

    private String USER_MOBILE = "mobileNumber";

    private String USER_MOBILE_VERIFIED = "mobileNumberVerified";

    private String USER_GIVEN_NAME = "givenName";

    private String USER_FAMILY_NAME = "familyName";

    private String USER_DISPLAY_NAME = "displayName";

    private String USER_RECEIVE_MARKETING_EMAIL = "receiveMarketingEmail";

    private String USER_JANRAIN_UUID = "uuid";

    private String USER_EMAIL_VERIFIED = "emailVerified";

    private String USER_CAPTURE = "capture";

    private String CONSUMER_COUNTRY = "country";

    private String CONSUMER_PREFERED_LANGUAGE = "preferredLanguage";

    private String CONSUMER_PRIMARY_ADDRESS = "primaryAddress";

    private String MARKETING_OPT_IN = "marketingOptIn";

    private String MARKETING_CONSENT_TIME_STAMP = "timestamp";

    /**
     * Constructor
     *
     * @param context application context
     * @since 1.0.0
     */
    public User(Context context) {
        RegistrationConfiguration.getInstance().getComponent().inject(this);
        cloudLoggingInterface = RegistrationConfiguration.getInstance().getComponent().getCloudLoggingInterface();
        mContext = context;
    }


    /**
     * {@code loginUsingTraditional} method logs in a user with a traditional account.
     *
     * @param emailAddress email ID of the User
     * @param password     password of the User
     * @param loginHandler instance of SocialLoginProviderHandler
     * @since 1.0.0
     */
    public void loginUsingTraditional(final String emailAddress, final String password,
                                      final LoginHandler loginHandler) {
        RLog.d(TAG, "loginUsingTraditional called");
        if (loginHandler == null && emailAddress == null && password == null) {
            throw new RuntimeException("Email , Password , LoginHandler can't be null");
        }
        new Thread(() -> {


            LoginTraditional loginTraditionalResultHandler = new LoginTraditional(
                    new LoginHandler() {
                        @Override
                        public void onLoginSuccess() {
                            DIUserProfile diUserProfile = getUserInstance();
                            if (diUserProfile != null && loginHandler != null) {
                                diUserProfile.setPassword(password);//TODO: check what is replacing password
                                RLog.d(TAG, "loginUsingTraditional onLoginSuccess with DIUserProfile " + diUserProfile);
                                ThreadUtils.postInMainThread(mContext, loginHandler::onLoginSuccess);
                            } else {
                                if (loginHandler != null) {
                                    UserRegistrationFailureInfo userRegistrationFailureInfo = new UserRegistrationFailureInfo(mContext);
                                    userRegistrationFailureInfo.setErrorCode(ErrorCodes.UNKNOWN_ERROR);
                                    RLog.e(TAG, "loginUsingTraditional onLoginSuccess without DIUserProfile, So throw onLoginFailedWithError" + userRegistrationFailureInfo.getErrorDescription());
                                    ThreadUtils.postInMainThread(mContext, () -> {
                                        loginHandler.
                                                onLoginFailedWithError(userRegistrationFailureInfo);
                                    });
                                }
                            }
                        }

                        @Override
                        public void onLoginFailedWithError(UserRegistrationFailureInfo
                                                                   userRegistrationFailureInfo) {
                            if (loginHandler == null)
                                return;
                            RLog.e(TAG, "loginUsingTraditional onLoginFailedWithError" + userRegistrationFailureInfo.getErrorDescription());
                            ThreadUtils.postInMainThread(mContext, () -> loginHandler.
                                    onLoginFailedWithError(userRegistrationFailureInfo));
                        }
                    }, mContext, emailAddress, password);
            loginTraditionalResultHandler.loginTraditionally(emailAddress, password);
        }).start();
    }


    /**
     * {@code loginUserUsingSocialProvider} logs in a user via a social login provider
     *
     * @param activity                         activity
     * @param providerName                     social login provider name
     * @param socialSocialLoginProviderHandler instance of  SocialProviderLoginHandler
     * @param mergeToken                       token generated of two distinct account created by same User
     * @since 1.0.0
     */
    public void loginUserUsingSocialProvider(final Activity activity, final String providerName,
                                             final SocialLoginProviderHandler socialSocialLoginProviderHandler,
                                             final String mergeToken) {
        RLog.d(TAG, "loginUserUsingSocialProvider called");
        new Thread(() -> {
            if (providerName != null && activity != null) {
                LoginSocialProvider loginSocialResultHandler = new LoginSocialProvider(
                        socialSocialLoginProviderHandler, activity);
                RLog.i(TAG, "loginUserUsingSocialProvider with providename = " + providerName + " and activity is not null");
                loginSocialResultHandler.loginSocial(activity, providerName, mergeToken);
            } else {
                if (null == socialSocialLoginProviderHandler) return;
                UserRegistrationFailureInfo userRegistrationFailureInfo =
                        new UserRegistrationFailureInfo(mContext);
                userRegistrationFailureInfo.setErrorCode(ErrorCodes.UNKNOWN_ERROR);
                RLog.e(TAG, "Error occurred in loginUserUsingSocialProvider , might be provider name is null or activity is null " + userRegistrationFailureInfo.getErrorDescription());
                ThreadUtils.postInMainThread(activity, () ->
                        socialSocialLoginProviderHandler.onLoginFailedWithError(userRegistrationFailureInfo));
            }
        }).start();
    }

    /**
     * @param activity
     * @param providerName               - for example "facebook" or "wechat"
     * @param socialLoginProviderHandler - object of SocialProviderLoginHandler
     * @param mergeToken                 - mergeToken when gets a merge token from janrain
     * @param accessToken                - accessToken from social provider
     */
    public void startTokenAuthForNativeProvider(final Activity activity, final String providerName, final SocialLoginProviderHandler socialLoginProviderHandler, final String mergeToken, final String accessToken) {
        RLog.d(TAG, "startTokenAuthForNativeProvider called");
        new Thread(() -> {
            if (providerName != null && activity != null) {
                LoginSocialProvider loginSocialResultHandler = new LoginSocialProvider(
                        socialLoginProviderHandler, activity);
                RLog.i(TAG, "startTokenAuthForNativeProvider with providename = " + providerName + " and activity is not null");
                loginSocialResultHandler.startTokenAuthForNativeProvider(activity, providerName, mergeToken, accessToken);
            } else {
                if (null == socialLoginProviderHandler) return;
                UserRegistrationFailureInfo userRegistrationFailureInfo =
                        new UserRegistrationFailureInfo(mContext);
                userRegistrationFailureInfo.setErrorCode(ErrorCodes.NETWORK_ERROR);
                RLog.e(TAG, "Error occurred in startTokenAuthForNativeProvider , might be provider name is null or activity is null " + userRegistrationFailureInfo.getErrorDescription());
                ThreadUtils.postInMainThread(activity, () ->
                        socialLoginProviderHandler.onLoginFailedWithError(userRegistrationFailureInfo));
            }
        }).start();


    }


    /**
     * {@code loginUserUsingSocialNativeProvider} logs in a user via a native social login provider like we chat.
     *
     * @param activity                   activity .
     * @param providerName               social logIn provider name
     * @param accessToken                access token social logIn provider
     * @param tokenSecret                secret token of social logIn provider
     * @param socialLoginProviderHandler instance of SocialLoginProviderHandler
     * @param mergeToken                 token generated of two distinct account created by same User
     * @since 1.0.0
     */
    public void loginUserUsingSocialNativeProvider(final Activity activity,
                                                   final String providerName,
                                                   final String accessToken,
                                                   final String tokenSecret,
                                                   final SocialLoginProviderHandler socialLoginProviderHandler,
                                                   final String mergeToken) {
        new Thread(() -> {
            if (providerName != null && activity != null) {
                LoginSocialNativeProvider loginSocialResultHandler = new LoginSocialNativeProvider(
                        socialLoginProviderHandler, mContext);
                RLog.d(TAG, "loginUserUsingSocialNativeProvider with providename = " + providerName + " and activity is not null");
                loginSocialResultHandler.loginSocial(activity, providerName, accessToken,
                        tokenSecret, mergeToken);
            } else {
                if (socialLoginProviderHandler == null) return;
                UserRegistrationFailureInfo userRegistrationFailureInfo = new UserRegistrationFailureInfo(mContext);
                userRegistrationFailureInfo.setErrorCode(ErrorCodes.NETWORK_ERROR);
                RLog.e(TAG, "Error occurred in loginUserUsingSocialNativeProvider, might be provider name is null or activity is null " + userRegistrationFailureInfo.getErrorDescription());
                ThreadUtils.postInMainThread(mContext, () ->
                        socialLoginProviderHandler.onLoginFailedWithError(userRegistrationFailureInfo));
            }
        }).start();
    }


    /**
     * {@code registerUserInfoForTraditional} method creates a user account.
     *
     * @param firstName                  User's first name
     * @param givenName                  User's last name
     * @param userEmail                  User's email id/mobile number
     * @param password                   User's password
     * @param olderThanAgeLimit          is user older than the defined age limit
     * @param isReceiveMarketingEmail    is user opted for ReceiveMarketingEmail
     * @param traditionalRegisterHandler traditional user register handler
     * @since 1.0.0
     */
    public void registerUserInfoForTraditional(String firstName, final String givenName, final String userEmail,
                                               final String password,
                                               final boolean olderThanAgeLimit,
                                               final boolean isReceiveMarketingEmail,
                                               final TraditionalRegistrationHandler traditionalRegisterHandler) {
        new Thread(() -> {
            RegisterTraditional registerTraditional = new RegisterTraditional(traditionalRegisterHandler, mContext);
            RLog.d(TAG, "registerUserInfoForTraditional with = " + registerTraditional.toString());
            registerTraditional.registerUserInfoForTraditional(firstName, givenName, userEmail,
                    password, olderThanAgeLimit, isReceiveMarketingEmail);
        }).start();
    }


    /**
     * {@code forgotPassword} method retrieves a lost password.
     *
     * @param emailAddress          User's email Address
     * @param forgotPasswordHandler Instance of ForgotPasswordHandler
     * @since 1.0.0
     */
    public void forgotPassword(final String emailAddress, final ForgotPasswordHandler forgotPasswordHandler) {
        if (emailAddress != null) {
            RLog.d(TAG, "forgotPassword with email address and performForgotPassword");
            ForgotPassword forgotPasswordResultHandler = new ForgotPassword(mContext, forgotPasswordHandler);
            forgotPasswordResultHandler.performForgotPassword(emailAddress);
        } else {
            UserRegistrationFailureInfo userRegistrationFailureInfo = new UserRegistrationFailureInfo(mContext);
            userRegistrationFailureInfo.setErrorCode(ErrorCodes.NETWORK_ERROR);
            RLog.e(TAG, "forgotPassword without email address  So onSendForgotPasswordFailedWithError" + userRegistrationFailureInfo.getErrorDescription());
            ThreadUtils.postInMainThread(mContext, () -> {
                forgotPasswordHandler.onSendForgotPasswordFailedWithError(userRegistrationFailureInfo);
            });
        }
    }

    /**
     * {@code refreshLoginSession} method refreshes the session of an already logged in user.
     *
     * @param refreshLoginSessionHandler instance of RefreshLoginSessionHandler
     * @since 1.0.0
     */
    public void refreshLoginSession(final RefreshLoginSessionHandler refreshLoginSessionHandler) {
        RLog.d(TAG, "refreshLoginSession");
        if (getUserLoginState().ordinal() < UserLoginState.PENDING_HSDP_LOGIN.ordinal()) {
            refreshLoginSessionHandler.onRefreshLoginSessionFailedWithError(getUserLoginState().ordinal());
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (new NetworkUtility(mContext).isInternetAvailable()) {
                    RefreshUserSession refreshUserSession = new RefreshUserSession(refreshLoginSessionHandler, mContext);
                    refreshUserSession.refreshUserSession();
                } else {
                    ThreadUtils.postInMainThread(mContext, () -> refreshLoginSessionHandler.onRefreshLoginSessionFailedWithError(ErrorCodes.NO_NETWORK));
                }
            }
        }).start();
    }


    /**
     * {@code refreshLoginSession} method refreshes the session of an already logged in user.
     *
     * @param refreshLoginSessionHandler instance of RefreshLoginSessionHandler
     * @since 1.0.0
     */
    public void refreshHSDPLoginSession(final RefreshLoginSessionHandler refreshLoginSessionHandler) {
        RLog.d(TAG, "refreshHSDPLoginSession");
        if (getUserLoginState().ordinal() < UserLoginState.USER_LOGGED_IN.ordinal()) {
            refreshLoginSessionHandler.onRefreshLoginSessionFailedWithError(getUserLoginState().ordinal());
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (new NetworkUtility(mContext).isInternetAvailable()) {
                    RefreshUserSession refreshUserSession = new RefreshUserSession(refreshLoginSessionHandler, mContext);
                    refreshUserSession.refreshHsdpSession();
                } else {
                    ThreadUtils.postInMainThread(mContext, () -> refreshLoginSessionHandler.onRefreshLoginSessionFailedWithError(ErrorCodes.NO_NETWORK));
                }
            }
        }).start();
    }


    /**
     * {@code resendVerificationEmail} method sends a verification mail in case an already sent mail is not received.
     *
     * @param emailAddress            email Address of User
     * @param resendVerificationEmail instance of ResendVerificationEmailHandler
     * @since 1.0.0
     */
    public void resendVerificationMail(final String emailAddress,
                                       final ResendVerificationEmailHandler resendVerificationEmail) {
        if (emailAddress != null) {
            ResendVerificationEmail resendVerificationEmailHandler = new ResendVerificationEmail(mContext, resendVerificationEmail);
            RLog.d(TAG, "resendVerificationMail initiated resend verification email");
            resendVerificationEmailHandler.resendVerificationMail(emailAddress);
        } else {
            UserRegistrationFailureInfo userRegistrationFailureInfo = new UserRegistrationFailureInfo(mContext);
            //TODO: Change error code from NETWORK_ERROR
            userRegistrationFailureInfo.setErrorCode(ErrorCodes.NETWORK_ERROR);
            RLog.e(TAG, "resendVerificationMail not initiated due email is null" + userRegistrationFailureInfo.getErrorDescription());
            ThreadUtils.postInMainThread(mContext, () ->
                    resendVerificationEmail.onResendVerificationEmailFailedWithError(userRegistrationFailureInfo));
        }
    }

    private void mergeTraditionalAccount(final String emailAddress, final String password, final String mergeToken,
                                         final LoginHandler loginHandler) {
        if (emailAddress != null && password != null) {
            LoginTraditional loginTraditionalResultHandler = new LoginTraditional(
                    loginHandler, mContext, emailAddress,
                    password);
            RLog.d(TAG, "mergeTraditionalAccount with email address and password");
            loginTraditionalResultHandler.mergeTraditionally(emailAddress, password, mergeToken);
        } else {
            UserRegistrationFailureInfo userRegistrationFailureInfo = new UserRegistrationFailureInfo(mContext);
            userRegistrationFailureInfo.setErrorCode(ErrorCodes.UNKNOWN_ERROR);
            RLog.d(TAG, "mergeTraditionalAccount without email address and password, So called onLoginFailedWithError" + userRegistrationFailureInfo.getErrorDescription());
            ThreadUtils.postInMainThread(mContext, () ->
                    loginHandler.onLoginFailedWithError(userRegistrationFailureInfo));
        }

    }

    /**
     * {@code mergeToTraditionalAccount} method merges a traditional account to other existing account
     *
     * @param emailAddress email address of User
     * @param password     password of User
     * @param mergeToken   token generated of two distinct account created by same User
     * @param loginHandler instance of LoginHandler
     * @since 1.0.0
     */
    public void mergeToTraditionalAccount(final String emailAddress, final String password, final String mergeToken,
                                          final LoginHandler loginHandler) {
        mergeTraditionalAccount(emailAddress, password, mergeToken, loginHandler);
    }

    /**
     * {@code registerUserInfoForSocial} methods creates a new account using social provider.
     *
     * @param givenName                  given name of User
     * @param displayName                display name of User
     * @param familyName                 family name of User
     * @param userEmail                  email address of user
     * @param olderThanAgeLimit          is user older than the defined age limit
     * @param isReceiveMarketingEmail    is User wants to  receive marketing email
     * @param socialLoginProviderHandler instance of   SocialLoginProviderHandler socialLoginProviderHandler
     * @param socialRegistrationToken    social provider login registration token
     * @since 1.0.0
     */
    public void registerUserInfoForSocial(final String givenName, final String displayName, final String familyName,
                                          final String userEmail, final boolean olderThanAgeLimit, final boolean isReceiveMarketingEmail,
                                          final SocialLoginProviderHandler socialLoginProviderHandler, final String socialRegistrationToken) {
        new Thread(() -> {
            if (socialLoginProviderHandler != null) {
                RLog.d(TAG, "registerUserInfoForSocial ");
                RegisterSocial registerSocial = new RegisterSocial(socialLoginProviderHandler, mContext);
                registerSocial.registerUserForSocial(givenName, displayName, familyName, userEmail, olderThanAgeLimit, isReceiveMarketingEmail, socialRegistrationToken);
            }
        }).start();
    }


    /**
     * Get DIUserProfile instance
     *
     * @return DIUserProfile instance or null if not logged in
     * @since 1.0.0
     */
    public DIUserProfile getUserInstance() {
        try {
            CaptureRecord captureRecord = Jump.getSignedInUser();

            if (captureRecord == null) {
                RLog.d(TAG, "DIUserProfile getUserInstance captureRecord = " + null);
                return null;
            }
            DIUserProfile diUserProfile = new DIUserProfile();
            HsdpUser hsdpUser = new HsdpUser(mContext);
            HsdpUserRecordV2 hsdpUserRecordV2 = hsdpUser.getHsdpUserRecord();
            if (hsdpUserRecordV2 != null) {
                diUserProfile.setHsdpUUID(hsdpUserRecordV2.getUserUUID());
                diUserProfile.setHsdpAccessToken(hsdpUserRecordV2.getAccessCredential().getAccessToken());
                RLog.d(TAG, "DIUserProfile getUserInstance HsdpUserRecordV2 = " + hsdpUserRecordV2.toString());
            }

            diUserProfile.setEmail(captureRecord.getString(USER_EMAIL));
            diUserProfile.setGivenName(captureRecord.getString(USER_GIVEN_NAME));
            diUserProfile.setFamilyName(captureRecord.getString(USER_FAMILY_NAME));
            diUserProfile.setDisplayName(captureRecord.getString(USER_DISPLAY_NAME));
            diUserProfile
                    .setReceiveMarketingEmail(captureRecord.getBoolean(USER_RECEIVE_MARKETING_EMAIL));
            JSONObject marketingOptIn = captureRecord.getJSONObject(MARKETING_OPT_IN);
            diUserProfile.setConsentTimestamp(marketingOptIn.getString(MARKETING_CONSENT_TIME_STAMP));

            diUserProfile.setJanrainUUID(captureRecord.getString(USER_JANRAIN_UUID));
            JSONObject userAddress = new JSONObject(captureRecord.getString(CONSUMER_PRIMARY_ADDRESS));
            diUserProfile.setCountryCode(userAddress.getString(CONSUMER_COUNTRY));
            diUserProfile.setLanguageCode(captureRecord.getString(CONSUMER_PREFERED_LANGUAGE));
            //Need to change in better way
            diUserProfile.setMobile(captureRecord.getString(USER_MOBILE));

            String gender = captureRecord.getString(UpdateGender.USER_GENDER);
            if (null != gender) {
                if (gender.equalsIgnoreCase(Gender.MALE.toString())) {
                    diUserProfile.setGender(Gender.MALE);
                } else if (gender.equalsIgnoreCase(Gender.FEMALE.toString())) {
                    diUserProfile.setGender(Gender.FEMALE);
                } else {
                    diUserProfile.setGender(Gender.NONE);
                }
            }

            String dob = captureRecord.getString(UpdateDateOfBirth.USER_DATE_OF_BIRTH);
            if (null != dob && !dob.equalsIgnoreCase("null")) {
                DateFormat formatter = new SimpleDateFormat(UpdateDateOfBirth.DATE_FORMAT_FOR_DOB, Locale.ROOT);
                Date date = formatter.parse(dob);
                diUserProfile.setDateOfBirth(date);
            }
            return diUserProfile;
        } catch (Exception e) {
            RLog.e(TAG, "DIUserProfile getUserInstance Exception occurred = " + e.getMessage());
        }
        return null;
    }


    private boolean isLoginTypeVerified(String loginType) {
        try {
            CaptureRecord captured = Jump.getSignedInUser();
            if (captured == null)
                return false;
            else {
                JSONObject mObject = new JSONObject(captured.toString());
                if (!mObject.isNull(loginType)) {
                    RLog.d(TAG, "DIUserProfile isLoginTypeVerified= " + captured.toString());
                    return true;
                }
            }
        } catch (JSONException e) {
            RLog.e(TAG, "DIUserProfile isLoginTypeVerified Exception occurred = " + e.getMessage());
        }
        return false;
    }

    /**
     * Is email varified
     *
     * @return true if verified
     * @since 1.0.0
     */
    public boolean isEmailVerified() {
        return isLoginTypeVerified(USER_EMAIL_VERIFIED);
    }

    /**
     * Is mobile no is verified .
     *
     * @return true if mobile no is verified
     * @since 1.0.0
     */
    public boolean isMobileVerified() {
        return isLoginTypeVerified(USER_MOBILE_VERIFIED);
    }

    /**
     * {@code getUserSignInState} method checks a user is logged in state
     *
     * @return UserLoginState
     * @since 1804.0
     */
    public UserLoginState getUserLoginState() {
        CaptureRecord capturedRecord = Jump.getSignedInUser();
        if (capturedRecord == null) {
            RLog.i(TAG, "getUserLoginState captureRecord is NULL");
            capturedRecord = CaptureRecord.loadFromDisk(mContext);
            if (capturedRecord == null) {
                RLog.i(TAG, "getUserLoginState captureRecord from disk is NULL");
                return UserLoginState.USER_NOT_LOGGED_IN;
            }
        }

        if (capturedRecord.getAccessToken() == null) {
            RLog.i(TAG, "getUserLoginState captureRecord.getAccessToken is NULL");
            return UserLoginState.USER_NOT_LOGGED_IN;
        }

        boolean isHsdpFlow = RegistrationConfiguration.getInstance().isHsdpFlow();
        if (!isAccountVerificationSignIn(capturedRecord)) {
            RLog.i(TAG, "getUserLoginState PENDING_VERIFICATION");
            return UserLoginState.PENDING_VERIFICATION;
        }
        if (!isSignedInOnAcceptedTermsAndConditions()) {
            RLog.i(TAG, "getUserLoginState PENDING_TERM_CONDITION");
            return UserLoginState.PENDING_TERM_CONDITION;
        }
        if (isHsdpFlow && !isHSDPUserSignedIn()) {
            RLog.i(TAG, "getUserLoginState PENDING_HSDP_LOGIN");
            return UserLoginState.PENDING_HSDP_LOGIN;
        }
        RLog.d(TAG, "getUserLoginState USER_LOGGED_IN");
        return UserLoginState.USER_LOGGED_IN;
    }


    /**
     * {@code authorizeHSDP} method authorize a user is log-in in HSDP Backend
     *
     * @param hsdpAuthenticationListener Pass the HSDPAuthenticationListener listner in parameter
     * @since 1804.0
     */
    public void authorizeHSDP(HSDPAuthenticationListener hsdpAuthenticationListener) {
        final boolean hsdpFlow = RegistrationConfiguration.getInstance().isHsdpFlow();
        RLog.d(TAG, "authorizeHSDP: proposition called this public api");
        if (networkUtility.isNetworkAvailable()) {

            if (getUserLoginState() == UserLoginState.USER_NOT_LOGGED_IN) {
                UserRegistrationHelper.getInstance().notifyOnHSDPLoginFailure(ErrorCodes.HSDP_JANRAIN_NOT_SUCCESS_ERROR_7012, "Janrain Login not success");
            }
            RLog.d(TAG, "authorizeHSDP: Janrain user signed-in not an HSDP So making HSDP call");
            if (hsdpFlow)
                hsdpLogin(hsdpAuthenticationListener);
            else {
                RLog.d(TAG, "authorizeHSDP:  hsdpUser :" + hsdpFlow + " and hsdpUser.getHsdpUserRecord() is " +
                        "null");
                throw new RuntimeException("Please configured HSDP configuration before making authorizeHSDP api call");
            }
        } else {
            RLog.d(TAG, "authorizeHSDP: Network not available");
            UserRegistrationHelper.getInstance().notifyOnHSDPLoginFailure(ErrorCodes.NO_NETWORK, new URError(mContext).getLocalizedError(ErrorType.NETWOK, ErrorCodes.NO_NETWORK));
        }


    }

    private void hsdpLogin(HSDPAuthenticationListener hsdpAuthenticationListener) {
        RLog.d(TAG, "authorizeHSDP:hsdpLogin: " + RegistrationConfiguration.getInstance().isHSDPSkipLoginConfigurationAvailable());

        if (RegistrationConfiguration.getInstance().isHSDPSkipLoginConfigurationAvailable()) {
            RLog.d(TAG, "authorizeHSDP:hsdpLogin: HSDP Flow = ");
            HSDPLoginService hsdpLoginService = new HSDPLoginService(mContext);
            if (hsdpAuthenticationListener != null) {
                RLog.d(TAG, "authorizeHSDP: with mTraditionalLoginHandler ");
                hsdpLoginService.hsdpLogin(getAccessToken(), getUserId(), hsdpAuthenticationListener);
            } else {
                throw new RuntimeException("Please provide HSDPAuthentiationListner");
            }
        }
    }

    private String getUserId() {
        if (RegistrationHelper.getInstance().isMobileFlow() && getEmail().equals("null")) {
            return getMobile();
        } else {
            return getEmail();
        }
    }

    /**
     * {@code isUserSignIn} method checks if a user is logged in
     *
     * @return boolean
     * @since 1.0.0
     * Its deprecated since 1804.0, request to please use getUserLoginState to get the User login state instead of isUserSignIn() api
     * @deprecated
     */

    @Deprecated
    public boolean isUserSignIn() {
        return (getUserLoginState() == UserLoginState.USER_LOGGED_IN);
    }

    private boolean isSignedInOnAcceptedTermsAndConditions() {
        boolean isAcceptTerms = RegistrationConfiguration.getInstance().isTermsAndConditionsAcceptanceRequired();
        if (isAcceptTerms) {
            RLog.d(TAG, "isUserSignIn isAcceptTerms : " + isAcceptTerms);

            if (!isTermsAndConditionAccepted()) {
                RLog.d(TAG, "isSignedInOnAcceptedTermsAndConditions isTermsAndConditionAccepted clear data on SignIn :" + false);
                return false;
            }
        }
        return true;
    }

    private boolean isHSDPUserSignedIn() {
        HsdpUser hsdpUser = new HsdpUser(mContext);
        final boolean hsdpFlow = RegistrationConfiguration.getInstance().isHsdpFlow();
        boolean hsdpUserSignedIn = false;
        if (hsdpFlow) {
            hsdpUserSignedIn = hsdpUser.isHsdpUserSignedIn();
        }
        return hsdpUserSignedIn;
    }

    private boolean isAccountVerificationSignIn(CaptureRecord capturedRecord) {
        boolean isTermsAndConditionsAccepted = false;
        boolean isEmailVerificationRequired = RegistrationConfiguration.getInstance().isEmailVerificationRequired();

        if (isEmailVerificationRequired) {
            isTermsAndConditionsAccepted = !capturedRecord.isNull(USER_EMAIL_VERIFIED) ||
                    !capturedRecord.isNull(USER_MOBILE_VERIFIED);
        }
        return isTermsAndConditionsAccepted;
    }

    /**
     * {@code isTermsAndConditionAccepted} method checks if a user is accepted terms and condition or no
     *
     * @since 1.0.0
     */
    public boolean isTermsAndConditionAccepted() {
        String mobileNo = getMobile();
        String email = getEmail();
        boolean isValidMobileNo = FieldsValidator.isValidMobileNumber(mobileNo);
        boolean isValidEmail = FieldsValidator.isValidEmail(email);
        if (isValidMobileNo && isValidEmail) {
            return getPreferenceValue(mContext, RegConstants.TERMS_N_CONDITIONS_ACCEPTED, mobileNo) &&
                    getPreferenceValue(mContext, RegConstants.TERMS_N_CONDITIONS_ACCEPTED, email);
        }
        if (isValidMobileNo) {
            return getPreferenceValue(mContext, RegConstants.TERMS_N_CONDITIONS_ACCEPTED, mobileNo);
        }
        return isValidEmail && getPreferenceValue(mContext, RegConstants.TERMS_N_CONDITIONS_ACCEPTED, email);
    }

    /**
     * Handle merge flow error
     *
     * @param existingProvider existing social logIn provider
     * @return
     * @since 1.0.0
     */
    public boolean handleMergeFlowError(String existingProvider) {
        RLog.d(TAG, "handleMergeFlowError for existingProvider: " + existingProvider + "status " + existingProvider.equals(USER_CAPTURE));
        return existingProvider.equals(USER_CAPTURE);
    }

    /**
     * Update the receive marketing email.
     *
     * @param updateUserDetailsHandler instance of UpdateUserDetailsHandler callback
     * @param receiveMarketingEmail    does User want to receive marketing email or not.
     *                                 Pass true if User wants to receive or else false .
     * @since 1.0.0
     */
    public void updateReceiveMarketingEmail(
            final UpdateUserDetailsHandler updateUserDetailsHandler,
            final boolean receiveMarketingEmail) {
        if (getUserNotLoggedInState()) {
            updateUserDetailsHandler.onUpdateFailedWithError(getUserLoginState().ordinal());
            return;
        }
        UpdateReceiveMarketingEmail updateReceiveMarketingEmailHandler = new
                UpdateReceiveMarketingEmail(
                mContext);
        RLog.d(TAG, "updateReceiveMarketingEmail called : " + receiveMarketingEmail);
        updateReceiveMarketingEmailHandler.
                updateMarketingEmailStatus(updateUserDetailsHandler, receiveMarketingEmail);
    }

    /**
     * Update Date of birth of user.
     *
     * @param updateUserDetailsHandler instance of UpdateUserDetailsHandler
     * @param date                     date of birth of User
     * @since 1.0.0
     */
    public void updateDateOfBirth(
            final UpdateUserDetailsHandler updateUserDetailsHandler,
            final Date date) {
        if (getUserNotLoggedInState()) {
            updateUserDetailsHandler.onUpdateFailedWithError(getUserLoginState().ordinal());
            return;
        }
        UpdateDateOfBirth updateDateOfBirth = new UpdateDateOfBirth(mContext);
        RLog.d(TAG, "updateDateOfBirth called : " + date.toString());
        updateDateOfBirth.updateDateOfBirth(updateUserDetailsHandler, date);
    }


    private boolean getUserNotLoggedInState() {
        return getUserLoginState().ordinal() < UserLoginState.PENDING_VERIFICATION.ordinal();
    }


    /**
     * Update Date of birth of user.
     *
     * @param updateUserDetailsHandler instance of UpdateUserDetailsHandler
     * @param gender                   instance of Gender
     * @since 1.0.0
     */
    public void updateGender(
            final UpdateUserDetailsHandler updateUserDetailsHandler,
            final Gender gender) {
        if (getUserNotLoggedInState()) {
            updateUserDetailsHandler.onUpdateFailedWithError(getUserLoginState().ordinal());
            return;
        }
        UpdateGender updateGender = new UpdateGender(mContext);
        RLog.d(TAG, "updateGender called : " + gender.toString());
        updateGender.updateGender(updateUserDetailsHandler, gender);
    }


    /**
     * {@code logout} method logs out a logged in user.
     *
     * @param logoutHandler instance of LogoutHandler
     * @since 1.0.0
     */
    public void logout(LogoutHandler logoutHandler) {
        HsdpUser hsdpUser = new HsdpUser(mContext);
        if (RegistrationConfiguration.getInstance().isHsdpFlow() && null != hsdpUser.getHsdpUserRecord()) {
            RLog.d(TAG, "logout : isUserSign logout from HSDP");
            logoutHsdp(logoutHandler);
        } else {
            AppTagging.trackAction(AppTagingConstants.SEND_DATA, AppTagingConstants.SPECIAL_EVENTS,
                    AppTagingConstants.LOGOUT_SUCCESS);
            RLog.d(TAG, "logout : isUserSign logout clearData");
            clearData();
            if (logoutHandler != null) {
                RegistrationHelper.getInstance().getUserRegistrationListener()
                        .notifyOnUserLogoutSuccess();
                logoutHandler.onLogoutSuccess();
            }
        }
        List<String> providersForCountry = RegistrationConfiguration.getInstance().getProvidersForCountry(getCountryCode());

        if (RegistrationConfiguration.getInstance().isFacebookSDKSupport()
                && providersForCountry != null && providersForCountry.contains(SOCIAL_PROVIDER_FACEBOOK))
            LoginManager.getInstance().logOut();
    }

    /**
     * Returns the access token of the User in String
     *
     * @return access token
     * @since 1.0.0
     */
    public String getAccessToken() {
        CaptureRecord captureRecord = Jump.getSignedInUser();

        if (captureRecord == null) {
            return null;
        }
        RLog.d(TAG, "getAccessToken : " + captureRecord.getAccessToken());
        return captureRecord.getAccessToken();
    }


    /**
     * Refresh User object and align with Server
     *
     * @param handler instance of RefreshUserHandler
     * @since 1.0.0
     */
    public void refreshUser(final RefreshUserHandler handler) {
        if (networkUtility.isNetworkAvailable()) {
            RLog.d(TAG, "refreshUser called");
            new RefreshandUpdateUserHandler(mContext).refreshAndUpdateUser(handler, this);
        } else {
            RLog.e(TAG, "refreshUser failed because of network offline");
            ThreadUtils.postInMainThread(mContext, () ->
                    handler.onRefreshUserFailed(ErrorCodes.NO_NETWORK));
        }
    }

    private void logoutHsdp(final LogoutHandler logoutHandler) {
        final HsdpUser hsdpUser = new HsdpUser(mContext);
        hsdpUser.logOut(new LogoutHandler() {
            @Override
            public void onLogoutSuccess() {
                RLog.d(TAG, "logoutHsdp clearData");
                clearData();
                AppTagging.trackAction(AppTagingConstants.SEND_DATA, AppTagingConstants.SPECIAL_EVENTS,
                        AppTagingConstants.LOGOUT_SUCCESS);
                if (logoutHandler != null) {
                    ThreadUtils.postInMainThread(mContext, logoutHandler::onLogoutSuccess);
                }
                RegistrationHelper.getInstance().getUserRegistrationListener()
                        .notifyOnUserLogoutSuccess();
            }

            @Override
            public void onLogoutFailure(int responseCode, String message) {
                if (responseCode == ErrorCodes.HSDP_INPUT_ERROR_1009
                        || responseCode == ErrorCodes.HSDP_INPUT_ERROR_1151) {
                    clearData();
                    RLog.e(TAG, "onLogoutFailure logout INVALID_ACCESS_TOKEN_CODE and INVALID_REFRESH_TOKEN_CODE:" + responseCode);
                    if (logoutHandler != null) {
                        ThreadUtils.postInMainThread(mContext, logoutHandler::onLogoutSuccess);
                    }
                    RegistrationHelper.getInstance().getUserRegistrationListener()
                            .notifyOnLogoutSuccessWithInvalidAccessToken();
                } else {
                    RLog.e(TAG, "onLogoutFailure logout :" + responseCode);

                    if (logoutHandler != null) {
                        ThreadUtils.postInMainThread(mContext, () ->
                                logoutHandler.onLogoutFailure(responseCode, message));
                    }
                    RegistrationHelper.getInstance().getUserRegistrationListener()
                            .notifyOnUserLogoutFailure();
                }
            }
        });
    }

    /**
     * {@code getEmail} method returns the email address of a logged in user.
     *
     * @return String
     * @since 1.0.0
     */
    public String getEmail() {
        DIUserProfile diUserProfile = getUserInstance();
        if (diUserProfile == null) {
            return null;
        }
        RLog.d(TAG, "getEmail diUserProfile : " + diUserProfile.getEmail());
        return diUserProfile.getEmail();
    }

    /**
     * {@code getMobile} method returns the Mobile Number of a logged in user.
     *
     * @return String
     * @since 1.0.0
     */
    public String getMobile() {
        DIUserProfile diUserProfile = getUserInstance();
        if (diUserProfile == null) {
            return null;
        }
        RLog.d(TAG, "getMobile diUserProfile : " + diUserProfile.getMobile());
        return diUserProfile.getMobile();
    }

    /**
     * {@code getGivenName} method returns the given name of a logged in user.
     *
     * @return String
     * @since 1.0.0
     */
    public String getGivenName() {
        DIUserProfile diUserProfile = getUserInstance();
        if (diUserProfile == null) {
            return null;
        }
        RLog.d(TAG, "getGivenName diUserProfile : " + diUserProfile.getGivenName());
        return diUserProfile.getGivenName();
    }

    /**
     * Get older than age limit.
     *
     * @return true if older than age limits as per countries specific .
     * @since 1.0.0
     */
    public boolean getOlderThanAgeLimit() {
        DIUserProfile diUserProfile = getUserInstance();
        if (diUserProfile == null) {
            return false;
        }
        RLog.d(TAG, "getOlderThanAgeLimit diUserProfile : " + diUserProfile.getOlderThanAgeLimit());
        return diUserProfile.getOlderThanAgeLimit();
    }

    /**
     * {@code getReceiveMarketingEmail} method checks if the user has subscribed to receive marketing email.
     *
     * @return boolean
     * @since 1.0.0
     */
    public boolean getReceiveMarketingEmail() {
        DIUserProfile diUserProfile = getUserInstance();
        if (diUserProfile == null) {
            return false;
        }
        RLog.d(TAG, "getReceiveMarketingEmail diUserProfile : " + diUserProfile.getReceiveMarketingEmail());
        return diUserProfile.getReceiveMarketingEmail();
    }

    public String getLastModifiedDateTimeOfMarketingEmailConsent() {

        DIUserProfile diUserProfile = getUserInstance();
        if (diUserProfile == null) {
            return null;
        }
        RLog.d(TAG, "getReceiveMarketingEmail diUserProfile : " + diUserProfile.getReceiveMarketingEmail());
        return diUserProfile.getConsentTimestamp();
    }

    /**
     * Get Date of birth
     *
     * @return Date object
     * @since 1.0.0
     */
    public Date getDateOfBirth() {
        DIUserProfile diUserProfile = getUserInstance();
        if (diUserProfile == null) {
            return null;
        }
        RLog.d(TAG, "getDateOfBirth diUserProfile : " + diUserProfile.getDateOfBirth());
        return diUserProfile.getDateOfBirth();
    }

    /**
     * Get Date of birth
     *
     * @return Date object
     * @since 1.0.0
     */
    public Gender getGender() {
        DIUserProfile diUserProfile = getUserInstance();
        if (diUserProfile == null) {
            return null;
        }
        RLog.d(TAG, "getGender diUserProfile : " + diUserProfile.getGender());
        return diUserProfile.getGender();
    }


    /**
     * {@code getGivenName} method returns the display name of a logged in user.
     *
     * @return String
     * @since 1.0.0
     */
    public String getDisplayName() {
        DIUserProfile diUserProfile = getUserInstance();
        if (diUserProfile == null) {
            return null;
        }
        RLog.d(TAG, "getDisplayName diUserProfile : " + diUserProfile.getDisplayName());
        return diUserProfile.getDisplayName();
    }

    /**
     * {@code getFamilyName} method returns the family name of a logged in user.
     *
     * @return String
     * @since 1.0.0
     */
    public String getFamilyName() {
        DIUserProfile diUserProfile = getUserInstance();
        if (diUserProfile == null) {
            return null;
        }
        RLog.d(TAG, "getFamilyName diUserProfile : " + diUserProfile.getFamilyName());
        return diUserProfile.getFamilyName();
    }

    /**
     * {@code getJanrainUUID} method returns the Janrain UUID of a logged in user.
     *
     * @return String
     * @since 1.0.0
     */
    public String getJanrainUUID() {
        DIUserProfile diUserProfile = getUserInstance();
        if (diUserProfile == null) {
            return null;
        }
        RLog.d(TAG, "getJanrainUUID diUserProfile : " + diUserProfile.getJanrainUUID());
        return diUserProfile.getJanrainUUID();
    }

    /**
     * {@code getHsdpUUID} method returns the HSDP UUID of a logged in user.
     *
     * @return String
     * @since 1.0.0
     */
    public String getHsdpUUID() {
        DIUserProfile diUserProfile = getUserInstance();
        if (diUserProfile == null) {
            return null;
        }
        RLog.d(TAG, "getHsdpUUID diUserProfile : " + diUserProfile.getHsdpUUID());
        return diUserProfile.getHsdpUUID();

    }

    /**
     * {@code getHsdpAccessToken} method returns the access token for a logged in user.
     *
     * @return String
     * @since 1.0.0
     */
    public String getHsdpAccessToken() {
        DIUserProfile diUserProfile = getUserInstance();
        if (diUserProfile == null) {
            return null;
        }
        RLog.d(TAG, "getHsdpAccessToken diUserProfile : " + diUserProfile.getHsdpAccessToken());
        return diUserProfile.getHsdpAccessToken();
    }

    /**
     * {@code getLanguageCode} method returns the language code for a logged in user
     *
     * @return String
     * @since 1.0.0
     */
    public String getLanguageCode() {
        DIUserProfile diUserProfile = getUserInstance();
        if (diUserProfile == null) {
            return null;
        }
        RLog.d(TAG, "getLanguageCode diUserProfile : " + diUserProfile.getLanguageCode());
        return diUserProfile.getLanguageCode();
    }

    /**
     * {@code getCountryCode} method returns country code for a logged in user.
     *
     * @return String
     * @since 1.0.0
     */
    public String getCountryCode() {
        DIUserProfile diUserProfile = getUserInstance();
        if (diUserProfile == null) {
            return null;
        }
        RLog.d(TAG, "getCountryCode diUserProfile : " + diUserProfile.getCountryCode());
        return diUserProfile.getCountryCode();
    }

    private void clearData() {
        HsdpUser hsdpUser = new HsdpUser(mContext);
        hsdpUser.deleteFromDisk();
        if (cloudLoggingInterface != null) {
            cloudLoggingInterface.setHSDPUserUUID(null);
        }
        if (JRSession.getInstance() != null) {
            JRSession.getInstance().signOutAllAuthenticatedUsers();
        }
        Jump.signOutCaptureUser(mContext);
        RLog.d(TAG, "clearData from HSDP and Jump");
    }

    /**
     * register User Registration Listener
     *
     * @param userRegistrationListener instance of UserRegistrationListener
     * @since 1.0.0
     */
    public void registerUserRegistrationListener(UserRegistrationListener userRegistrationListener) {
        RLog.d(TAG, "registerUserRegistrationListener");
        RegistrationHelper.getInstance().registerUserRegistrationListener(userRegistrationListener);
    }

    /**
     * remove  User Registration Listener
     *
     * @param userRegistrationListener instance of UserRegistrationListener which is  previously registered.
     * @since 1.0.0
     */
    public void unRegisterUserRegistrationListener(UserRegistrationListener
                                                           userRegistrationListener) {
        RLog.d(TAG, "unRegisterUserRegistrationListener");
        RegistrationHelper.getInstance().unRegisterUserRegistrationListener(
                userRegistrationListener);
    }

    /**
     * register User Registration Listener
     *
     * @param hsdpAuthenticationListener instance of UserRegistrationListener
     * @since 1.0.0
     */
    public void registerHSDPAuthenticationListener(HSDPAuthenticationListener hsdpAuthenticationListener) {
        RLog.d(TAG, "registerUserRegistrationListener");
        RegistrationHelper.getInstance().registerHSDPAuthenticationListener(hsdpAuthenticationListener);
    }

    /**
     * remove  User Registration Listener
     *
     * @param hsdpAuthenticationListener instance of UserRegistrationListener which is  previously registered.
     * @since 1.0.0
     */
    public void unRegisterHSDPAuthenticationListener(HSDPAuthenticationListener
                                                             hsdpAuthenticationListener) {
        RLog.d(TAG, "unRegisterUserRegistrationListener");
        RegistrationHelper.getInstance().unRegisterHSDPAuthenticationListener(
                hsdpAuthenticationListener);
    }

}
