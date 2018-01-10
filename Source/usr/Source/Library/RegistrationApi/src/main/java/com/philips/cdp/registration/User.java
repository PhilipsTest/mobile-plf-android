/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration;

import android.app.*;
import android.content.*;
import android.support.annotation.*;

import com.janrain.android.*;
import com.janrain.android.capture.Capture.*;
import com.janrain.android.capture.*;
import com.janrain.android.engage.session.*;
import com.philips.cdp.registration.app.tagging.*;
import com.philips.cdp.registration.configuration.*;
import com.philips.cdp.registration.controller.*;
import com.philips.cdp.registration.dao.*;
import com.philips.cdp.registration.handlers.*;
import com.philips.cdp.registration.hsdp.*;
import com.philips.cdp.registration.listener.*;
import com.philips.cdp.registration.settings.*;
import com.philips.cdp.registration.ui.utils.*;

import org.json.*;

import java.text.*;
import java.util.*;

import javax.inject.*;

import static com.philips.cdp.registration.ui.utils.RegPreferenceUtility.*;

/**
 * {@code User} class represents information related to a logged in user of USR.
 * Additionally, it exposes APIs to login, logout and refresh operations for traditional and social accounts.
 * @since 1.0.0
 */
public class User {

    @Inject
    NetworkUtility networkUtility;

    private boolean mEmailVerified;

    private Context mContext;

    private JSONObject mConsumerInterestObject;

    private JSONArray mConsumerInterestArray;

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

    private String CONSUMER_CAMPAIGN_NAME = "campaignName";

    private String CONSUMER_SUBJECT_AREA = "subjectArea";

    private String CONSUMER_TOPIC_COMMUNICATION_KEY = "topicCommunicationKey";

    private String CONSUMER_TOPIC_VALUE = "topicValue";

    private String CONSUMER_INTERESTS = "consumerInterests";

    private String LOG_TAG = "User Registration";

    private String CONSUMER_COUNTRY = "country";

    private String CONSUMER_PREFERED_LANGUAGE = "preferredLanguage";

    private String CONSUMER_PRIMARY_ADDRESS = "primaryAddress";

    private UpdateUserRecordHandler mUpdateUserRecordHandler;

    /**
     * Constructor
     *
     * @param context  application context
     * @since 1.0.0
     */
    public User(Context context) {
        RegistrationConfiguration.getInstance().getComponent().inject(this);
        mContext = context;
        mUpdateUserRecordHandler = new UpdateUserRecord(mContext);
    }


    /**
     * {@code loginUsingTraditional} method logs in a user with a traditional account.
     *
     * @param emailAddress            email ID of the User
     * @param password                password of the User
     * @param traditionalLoginHandler instance of TraditionalLoginHandler
     *                                @since  1.0.0
     */
    public void loginUsingTraditional(final String emailAddress, final String password,
                                      final TraditionalLoginHandler traditionalLoginHandler) {
        if (traditionalLoginHandler == null && emailAddress == null && password == null) {
            throw new RuntimeException("Email , Password , TraditionalLoginHandler can't be null");
        }
        new Thread(() -> {
            LoginTraditional loginTraditionalResultHandler = new LoginTraditional(
                    new TraditionalLoginHandler() {
                        @Override
                        public void onLoginSuccess() {
                            DIUserProfile diUserProfile = getUserInstance();
                            if (diUserProfile != null && traditionalLoginHandler != null) {
                                diUserProfile.setPassword(password);
                                ThreadUtils.postInMainThread(mContext, traditionalLoginHandler::onLoginSuccess);
                            } else {
                                if (traditionalLoginHandler != null) {
                                    UserRegistrationFailureInfo
                                            userRegistrationFailureInfo =
                                            new UserRegistrationFailureInfo();
                                    userRegistrationFailureInfo.
                                            setErrorCode(RegConstants.
                                                    DI_PROFILE_NULL_ERROR_CODE);
                                    ThreadUtils.postInMainThread(mContext, () -> traditionalLoginHandler.
                                            onLoginFailedWithError(userRegistrationFailureInfo
                                            ));
                                }
                            }
                        }

                        @Override
                        public void onLoginFailedWithError(UserRegistrationFailureInfo
                                                                   userRegistrationFailureInfo) {
                            if (traditionalLoginHandler != null) {
                                ThreadUtils.postInMainThread(mContext, () -> traditionalLoginHandler.
                                        onLoginFailedWithError(userRegistrationFailureInfo));
                            }
                        }
                    }, mContext, mUpdateUserRecordHandler, emailAddress,
                    password);
            loginTraditionalResultHandler.loginTraditionally(emailAddress, password);
        }).start();
    }


    /**
     * {@code loginUserUsingSocialProvider} logs in a user via a social login provider
     *
     * @param activity           activity
     * @param providerName       social login provider name
     * @param socialLoginHandler instance of  SocialProviderLoginHandler
     * @param mergeToken         token generated of two distinct account created by same User
     *                           @since 1.0.0
     */
    public void loginUserUsingSocialProvider(final Activity activity, final String providerName,
                                             final SocialProviderLoginHandler socialLoginHandler,
                                             final String mergeToken) {
        new Thread(() -> {
            if (providerName != null && activity != null) {
                LoginSocialProvider loginSocialResultHandler = new LoginSocialProvider(
                        socialLoginHandler, activity, mUpdateUserRecordHandler);
                loginSocialResultHandler.loginSocial(activity, providerName, mergeToken);
            } else {
                if (socialLoginHandler != null) {
                    UserRegistrationFailureInfo userRegistrationFailureInfo =
                            new UserRegistrationFailureInfo();
                    userRegistrationFailureInfo.setErrorCode(RegConstants.
                            DI_PROFILE_NULL_ERROR_CODE);
                    ThreadUtils.postInMainThread(activity, () ->
                            socialLoginHandler.onLoginFailedWithError(userRegistrationFailureInfo));
                }
            }
        }).start();
    }


    /**
     * {@code loginUserUsingSocialNativeProvider} logs in a user via a native social login provider like we chat.
     *
     * @param activity           activity .
     * @param providerName       social logIn provider name
     * @param accessToken        access token social logIn provider
     * @param tokenSecret        secret token of social logIn provider
     * @param socialLoginHandler instance of SocialProviderLoginHandler
     * @param mergeToken         token generated of two distinct account created by same User
     *                           @since 1.0.0
     */
    public void loginUserUsingSocialNativeProvider(final Activity activity,
                                                   final String providerName,
                                                   final String accessToken,
                                                   final String tokenSecret,
                                                   final SocialProviderLoginHandler
                                                           socialLoginHandler,
                                                   final String mergeToken) {
        new Thread(() -> {
            if (providerName != null && activity != null) {
                LoginSocialNativeProvider loginSocialResultHandler = new LoginSocialNativeProvider(
                        socialLoginHandler, mContext, mUpdateUserRecordHandler);
                loginSocialResultHandler.loginSocial(activity, providerName, accessToken,
                        tokenSecret, mergeToken);
            } else {
                if (socialLoginHandler != null) {
                    UserRegistrationFailureInfo userRegistrationFailureInfo = new UserRegistrationFailureInfo();
                    userRegistrationFailureInfo.setErrorCode(RegConstants.DI_PROFILE_NULL_ERROR_CODE);
                    ThreadUtils.postInMainThread(mContext, () ->
                            socialLoginHandler.onLoginFailedWithError(userRegistrationFailureInfo));
                }
            }
        }).start();
    }


    /**
     * {@code registerUserInfoForTraditional} method creates a user account.
     *
     * @param firstName  User's first name
     * @param givenName  User's last name
     * @param userEmail  User's email id/mobile number
     * @param password   User's password
     * @param olderThanAgeLimit is user older than the defined age limit
     * @param isReceiveMarketingEmail is user opted for ReceiveMarketingEmail
     * @param traditionalRegisterHandler traditional user register handler
     * @since 1.0.0
     */
    public void registerUserInfoForTraditional(String firstName, final String givenName, final String userEmail,
                                               final String password,
                                               final boolean olderThanAgeLimit,
                                               final boolean isReceiveMarketingEmail,
                                               final TraditionalRegistrationHandler traditionalRegisterHandler) {
        new Thread(() -> {
            RegisterTraditional registerTraditional = new RegisterTraditional(traditionalRegisterHandler, mContext, mUpdateUserRecordHandler);
            ABCD.getInstance().setmP(password);
            registerTraditional.registerUserInfoForTraditional(firstName,givenName, userEmail,
                    password, olderThanAgeLimit, isReceiveMarketingEmail);
        }).start();
    }


    /**
     * {@code forgotPassword} method retrieves a lost password.
     *
     * @param emailAddress          User's email Address
     * @param forgotPasswordHandler Instance of ForgotPasswordHandler
     *                              @since 1.0.0
     */
    public void forgotPassword(final String emailAddress, final ForgotPasswordHandler forgotPasswordHandler) {
        if (emailAddress != null) {
            ForgotPassword forgotPasswordResultHandler = new ForgotPassword(mContext, forgotPasswordHandler);
            forgotPasswordResultHandler.performForgotPassword(emailAddress);
        } else {
            UserRegistrationFailureInfo userRegistrationFailureInfo = new UserRegistrationFailureInfo();
            userRegistrationFailureInfo.setErrorCode(RegConstants.DI_PROFILE_NULL_ERROR_CODE);
            ThreadUtils.postInMainThread(mContext, () ->
                    forgotPasswordHandler.onSendForgotPasswordFailedWithError(userRegistrationFailureInfo));
        }
    }

    /**
     * {@code refreshLoginSession} method refreshes the session of an already logged in user.
     *
     * @param refreshLoginSessionHandler instance of RefreshLoginSessionHandler
     *                                   @since 1.0.0
     */
    public void refreshLoginSession(final RefreshLoginSessionHandler refreshLoginSessionHandler) {
        RefreshUserSession refreshUserSession = new RefreshUserSession(refreshLoginSessionHandler, mContext);
        refreshUserSession.refreshUserSession();
    }


    /**
     * {@code resendVerificationEmail} method sends a verification mail in case an already sent mail is not received.
     *
     * @param emailAddress            email Address of User
     * @param resendVerificationEmail instance of ResendVerificationEmailHandler
     *                                @since 1.0.0
     */
    public void resendVerificationMail(final String emailAddress,
                                       final ResendVerificationEmailHandler resendVerificationEmail) {
        if (emailAddress != null) {
            ResendVerificationEmail resendVerificationEmailHandler = new ResendVerificationEmail(mContext, resendVerificationEmail);
            resendVerificationEmailHandler.resendVerificationMail(emailAddress);
        } else {
            UserRegistrationFailureInfo userRegistrationFailureInfo = new UserRegistrationFailureInfo();
            userRegistrationFailureInfo.setErrorCode(RegConstants.DI_PROFILE_NULL_ERROR_CODE);
            ThreadUtils.postInMainThread(mContext, () ->
                    resendVerificationEmail.onResendVerificationEmailFailedWithError(userRegistrationFailureInfo));
        }
    }

    private void mergeTraditionalAccount(final String emailAddress, final String password, final String mergeToken,
                                         final TraditionalLoginHandler traditionalLoginHandler) {
        if (emailAddress != null && password != null) {
            LoginTraditional loginTraditionalResultHandler = new LoginTraditional(
                    traditionalLoginHandler, mContext, mUpdateUserRecordHandler, emailAddress,
                    password);
            loginTraditionalResultHandler.mergeTraditionally(emailAddress, password, mergeToken);
        } else {
            UserRegistrationFailureInfo userRegistrationFailureInfo = new UserRegistrationFailureInfo();
            userRegistrationFailureInfo.setErrorCode(RegConstants.DI_PROFILE_NULL_ERROR_CODE);
            ThreadUtils.postInMainThread(mContext, () ->
                    traditionalLoginHandler.onLoginFailedWithError(userRegistrationFailureInfo));
        }

    }

    /**
     * {@code mergeToTraditionalAccount} method merges a traditional account to other existing account
     *
     * @param emailAddress  email address of User
     * @param password password of User
     * @param mergeToken token generated of two distinct account created by same User
     * @param traditionalLoginHandler instance of TraditionalLoginHandler
     * @since 1.0.0
     */
    public void mergeToTraditionalAccount(final String emailAddress, final String password, final String mergeToken,
                                          final TraditionalLoginHandler traditionalLoginHandler) {
        mergeTraditionalAccount(emailAddress, password, mergeToken, traditionalLoginHandler);
    }

    /**
     * {@code registerUserInfoForSocial} methods creates a new account using social provider.
     *
     * @param givenName given name of User
     * @param displayName display name of User
     * @param familyName family name of User
     * @param userEmail  email address of user
     * @param olderThanAgeLimit is user older than the defined age limit
     * @param isReceiveMarketingEmail is User wants to  receive marketing email
     * @param socialProviderLoginHandler instance of  SocialProviderLoginHandler socialProviderLoginHandler
     * @param socialRegistrationToken  social provider login registration token
     * @since 1.0.0
     */
    public void registerUserInfoForSocial(final String givenName, final String displayName, final String familyName,
                                          final String userEmail, final boolean olderThanAgeLimit, final boolean isReceiveMarketingEmail,
                                          final SocialProviderLoginHandler socialProviderLoginHandler, final String socialRegistrationToken) {
        new Thread(() -> {
            if (socialProviderLoginHandler != null) {
                RegisterSocial registerSocial = new RegisterSocial(socialProviderLoginHandler, mContext, mUpdateUserRecordHandler);
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
        CaptureRecord captureRecord = Jump.getSignedInUser();

        if (captureRecord == null) {
            return null;
        }
        DIUserProfile diUserProfile = new DIUserProfile();
        HsdpUser hsdpUser = new HsdpUser(mContext);
        HsdpUserRecord hsdpUserRecord = hsdpUser.getHsdpUserRecord();
        if (hsdpUserRecord != null) {
            diUserProfile.setHsdpUUID(hsdpUserRecord.getUserUUID());
            diUserProfile.setHsdpAccessToken(hsdpUserRecord.getAccessCredential().getAccessToken());
        }

        try {
            diUserProfile.setEmail(captureRecord.getString(USER_EMAIL));
            diUserProfile.setGivenName(captureRecord.getString(USER_GIVEN_NAME));
            diUserProfile.setFamilyName(captureRecord.getString(USER_FAMILY_NAME));
            diUserProfile.setDisplayName(captureRecord.getString(USER_DISPLAY_NAME));
            diUserProfile
                    .setReceiveMarketingEmail(captureRecord.getBoolean(USER_RECEIVE_MARKETING_EMAIL));
            diUserProfile.setJanrainUUID(captureRecord.getString(USER_JANRAIN_UUID));
            JSONObject userAddress = new JSONObject(captureRecord.getString(CONSUMER_PRIMARY_ADDRESS));
            diUserProfile.setCountryCode(userAddress.getString(CONSUMER_COUNTRY));
            diUserProfile.setLanguageCode(captureRecord.getString(CONSUMER_PREFERED_LANGUAGE));
            //Need to change in better way
            try {
                diUserProfile.setMobile(captureRecord.getString(USER_MOBILE));
            } catch (Exception ignored) {

            }

            String gender = captureRecord.getString(UpdateGender.USER_GENDER);
            if (null != gender) {
                if (gender.equalsIgnoreCase(Gender.MALE.toString())) {
                    diUserProfile.setGender(Gender.MALE);
                } else if(gender.equalsIgnoreCase(Gender.FEMALE.toString())) {
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
        } catch (JSONException e) {
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return diUserProfile;
    }

    /**
     * Get Email verification status
     * @deprecated reason individual methods are added for mobile(isEmailVerified()) and email(isEmailVerified()) verification.
     * This is no more needed and will be removed from 2018.1.0
     * @return status in boolean
     * @since 1.0.0
     */
    @Deprecated
    public boolean getEmailVerificationStatus() {
        return (isEmailVerified() || isMobileVerified());
    }

    private boolean isLoginTypeVerified(String loginType) {
        CaptureRecord captured = Jump.getSignedInUser();
        if (captured == null)
            return false;
        try {
            JSONObject mObject = new JSONObject(captured.toString());
            if (!mObject.isNull(loginType)) {
                return true;
            }
        } catch (JSONException e) {
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
     * {@code isUserSignIn} method checks if a user is logged in
     *
     * @return boolean
     * @since 1.0.0
     */
    public boolean isUserSignIn() {
        CaptureRecord capturedRecord = Jump.getSignedInUser();
        if (capturedRecord == null) {
            capturedRecord = CaptureRecord.loadFromDisk(mContext);
            RLog.d("isUserSign", "captureRecord"+ (capturedRecord==null));
        }
        if (capturedRecord == null) {
            return false;
        }

        boolean isEmailVerificationRequired = RegistrationConfiguration.getInstance().
                isEmailVerificationRequired();
        boolean isHsdpFlow = RegistrationConfiguration.getInstance().isHsdpFlow();
        boolean isAcceptTerms = RegistrationConfiguration.getInstance().
                isTermsAndConditionsAcceptanceRequired();

        boolean signedIn = true;
        if (isEmailVerificationRequired) {
            signedIn = !capturedRecord.isNull(USER_EMAIL_VERIFIED) ||
                    !capturedRecord.isNull(USER_MOBILE_VERIFIED);
            RLog.d("isUserSign", "isEmailVerificationRequired signin"+ signedIn);

        }
        if (isHsdpFlow) {
            if (!isEmailVerificationRequired) {
                RLog.d("isUserSign", "!isEmailVerificationRequired signin");
                throw new RuntimeException("Please set emailVerificationRequired field as true");
            }
            HsdpUser hsdpUser = new HsdpUser(mContext);
            signedIn = signedIn && hsdpUser.isHsdpUserSignedIn();
            RLog.d("isUserSign", "!isHsdpFlow signin"+signedIn);

        }
        if (RegistrationConfiguration.getInstance().getRegistrationClientId(RegUtility.
                getConfiguration(
                        RegistrationConfiguration.getInstance().getRegistrationEnvironment())) != null) {
            signedIn = signedIn && capturedRecord.getAccessToken() != null;
            RLog.d("isUserSign", "signedIn && capturedRecord.getAccessToken()"+signedIn);

        }

        if (isAcceptTerms) {
            RLog.d("isUserSign", "isAcceptTerms"+signedIn);

            if (!isTermsAndConditionAccepted()) {
                signedIn = false;
                RLog.d("isUserSign", "isTermsAndConditionAccepted cleardata"+signedIn);

      //          clearData();
            }
        }
        return signedIn;
    }

    /**
     * {@code isTermsAndConditionAccepted} method checks if a user is accepted terms and condition or no
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
        return isValidEmail && getPreferenceValue(mContext,RegConstants.TERMS_N_CONDITIONS_ACCEPTED, email);
    }

    /**
     * Handle merge flow error
     *
     * @param existingProvider existing social logIn provider
     * @return
     * @since 1.0.0
     */
    public boolean handleMergeFlowError(String existingProvider) {
        if (existingProvider.equals(USER_CAPTURE)) {
            return true;
        }
        return false;
    }

    /**
     * Update the receive marketing email.
     *
     * @param updateReceiveMarketingEmail instance of UpdateUserDetailsHandler call back
     * @param receiveMarketingEmail       does User want to receive marketing email or not.
     *                                    Pass true if User wants to receive or else false .
     *                                    @since 1.0.0
     */
    public void updateReceiveMarketingEmail(
            final UpdateUserDetailsHandler updateReceiveMarketingEmail,
            final boolean receiveMarketingEmail) {
        UpdateReceiveMarketingEmail updateReceiveMarketingEmailHandler = new
                UpdateReceiveMarketingEmail(
                mContext);
        updateReceiveMarketingEmailHandler.
                updateMarketingEmailStatus(updateReceiveMarketingEmail, receiveMarketingEmail);
    }

    /**
     * Update Date of birth of user.
     *
     * @param updateUserDetailsHandler instance of UpdateUserDetailsHandler
     * @param date date of birth of User
     * @since 1.0.0
     */
    public void updateDateOfBirth(
            final UpdateUserDetailsHandler updateUserDetailsHandler,
            final Date date) {
        UpdateDateOfBirth updateDateOfBirth = new UpdateDateOfBirth(mContext);
        updateDateOfBirth.updateDateOfBirth(updateUserDetailsHandler, date);
    }


    /**
     * Update Date of birth of user.
     *
     * @param updateUserDetailsHandler instance of UpdateUserDetailsHandler
     * @param gender instance of Gender
     * @since 1.0.0
     */
    public void updateGender(
            final UpdateUserDetailsHandler updateUserDetailsHandler,
            final Gender gender) {
        UpdateGender updateGender = new UpdateGender(mContext);
        updateGender.updateGender(updateUserDetailsHandler, gender);
    }

    /**
     *
     * @param addConsumerInterestHandler instance of AddConsumerInterestHandler
     * @param consumerArray all consumer interests
     * @since 1.0.0
     *
     * This is no more needed and will be removed from 2018.1.0
     */

    @Deprecated
    private void addConsumerInterest(AddConsumerInterestHandler addConsumerInterestHandler,
                                    ConsumerArray consumerArray) {

        AddConsumerInterest addConsumerInterest = new AddConsumerInterest(
                addConsumerInterestHandler);
        CaptureRecord captured = Jump.getSignedInUser();
        JSONObject originalUserInfo = getCurrentUserAsJsonObject();
        mConsumerInterestArray = new JSONArray();
        ConsumerArray consumer = consumerArray;

        if (consumer != null) {
            for (ConsumerInterest diConsumerInterest : consumer.getConsumerArraylist()) {
                try {

                    mConsumerInterestObject = new JSONObject();
                    mConsumerInterestObject.put(CONSUMER_CAMPAIGN_NAME,
                            diConsumerInterest.getCampaignName());
                    mConsumerInterestObject.put(CONSUMER_SUBJECT_AREA,
                            diConsumerInterest.getSubjectArea());
                    mConsumerInterestObject.put(CONSUMER_TOPIC_COMMUNICATION_KEY,
                            diConsumerInterest.getTopicCommunicationKey());
                    mConsumerInterestObject.put(CONSUMER_TOPIC_VALUE,
                            diConsumerInterest.getTopicValue());

                } catch (JSONException e) {
                }
                mConsumerInterestArray.put(mConsumerInterestObject);
            }
        }

        if (captured != null) {
            try {
                captured.remove(CONSUMER_INTERESTS);
                captured.put(CONSUMER_INTERESTS, mConsumerInterestArray);
                try {
                    captured.synchronize(addConsumerInterest, originalUserInfo);

                } catch (InvalidApidChangeException e) {

                    e.printStackTrace();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Nullable
    private JSONObject getCurrentUserAsJsonObject() {
        JSONObject userData = null;
        try {
            userData = new JSONObject(Jump.getSignedInUser().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userData;
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
            logoutHsdp(logoutHandler);
        } else {
            AppTagging.trackAction(AppTagingConstants.SEND_DATA, AppTagingConstants.SPECIAL_EVENTS,
                    AppTagingConstants.LOGOUT_SUCCESS);
            System.out.println("isUserSign logout clearData");

            clearData();
            if (logoutHandler != null) {

                RegistrationHelper.getInstance().getUserRegistrationListener()
                        .notifyOnUserLogoutSuccess();
                logoutHandler.onLogoutSuccess();
            }
        }
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
        return captureRecord.getAccessToken();
    }


    /**
     * Refresh User object and align with Server
     *
     * @param handler instance of RefreshUserHandler
     *                @since 1.0.0
     */
    public void refreshUser(final RefreshUserHandler handler) {
        if (networkUtility.isNetworkAvailable()) {
            new RefreshandUpdateUserHandler(mUpdateUserRecordHandler, mContext).refreshAndUpdateUser(handler, this, ABCD.getInstance().getmP());
        } else {
            ThreadUtils.postInMainThread(mContext, () ->
                    handler.onRefreshUserFailed(-1));
        }
    }

    private void logoutHsdp(final LogoutHandler logoutHandler) {
        final HsdpUser hsdpUser = new HsdpUser(mContext);
        hsdpUser.logOut(new LogoutHandler() {
            @Override
            public void onLogoutSuccess() {
                System.out.println("isUserSign logoutHsdp clearData");

                clearData();
                if (logoutHandler != null) {
                    AppTagging.trackAction(AppTagingConstants.SEND_DATA, AppTagingConstants.SPECIAL_EVENTS,
                            AppTagingConstants.LOGOUT_SUCCESS);
                    ThreadUtils.postInMainThread(mContext, () ->
                            logoutHandler.onLogoutSuccess());
                    RegistrationHelper.getInstance().getUserRegistrationListener()
                            .notifyOnUserLogoutSuccess();
                }
            }

            @Override
            public void onLogoutFailure(int responseCode, String message) {

                if (responseCode == Integer.parseInt(RegConstants.INVALID_ACCESS_TOKEN_CODE)
                        || responseCode == Integer.parseInt(RegConstants.INVALID_REFRESH_TOKEN_CODE)) {
                    System.out.println("isUserSign logoutHsdp failure clearData");

                    clearData();
                    if (logoutHandler != null) {
                        ThreadUtils.postInMainThread(mContext, () ->
                                logoutHandler.onLogoutSuccess());
                        RegistrationHelper.getInstance().getUserRegistrationListener()
                                .notifyOnLogoutSuccessWithInvalidAccessToken();
                    }
                    return;
                } else {
                    if (logoutHandler != null) {
                        ThreadUtils.postInMainThread(mContext, () ->
                                logoutHandler.onLogoutFailure(responseCode, message));
                        RegistrationHelper.getInstance().getUserRegistrationListener()
                                .notifyOnUserLogoutFailure();
                    }
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
        return diUserProfile.getMobile();
    }


    private String getPassword() {
        DIUserProfile diUserProfile = getUserInstance();
        if (diUserProfile == null) {
            return null;
        }
        return diUserProfile.getPassword();
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
        return diUserProfile.getReceiveMarketingEmail();
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
        return diUserProfile.getCountryCode();
    }

    private void clearData() {
        System.out.println("isUserSign ClearData");

        HsdpUser hsdpUser = new HsdpUser(mContext);
        hsdpUser.deleteFromDisk();
        if (JRSession.getInstance() != null) {
            JRSession.getInstance().signOutAllAuthenticatedUsers();
        }
        Jump.signOutCaptureUser(mContext);
    }

    /**
     * register User Registration Listener
     *
     * @param userRegistrationListener instance of UserRegistrationListener
     *                                 @since 1.0.0
     */
    public void registerUserRegistrationListener(UserRegistrationListener userRegistrationListener) {
        RegistrationHelper.getInstance().registerUserRegistrationListener(userRegistrationListener);
    }

    /**
     * remove  User Registration Listener
     *
     * @param userRegistrationListener instance of UserRegistrationListener which is  previously registered.
     *                                 @since 1.0.0
     */
    public void unRegisterUserRegistrationListener(UserRegistrationListener
                                                           userRegistrationListener) {
        RegistrationHelper.getInstance().unRegisterUserRegistrationListener(
                userRegistrationListener);
    }

}
