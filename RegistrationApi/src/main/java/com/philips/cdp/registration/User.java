package com.philips.cdp.registration;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.janrain.android.Jump;
import com.janrain.android.Jump.CaptureApiResultHandler;
import com.janrain.android.capture.Capture.InvalidApidChangeException;
import com.janrain.android.capture.CaptureRecord;
import com.philips.cdp.registration.controller.AddConsumerInterest;
import com.philips.cdp.registration.controller.ContinueSocialProviderLogin;
import com.philips.cdp.registration.controller.ForgotPassword;
import com.philips.cdp.registration.controller.LoginSocialProvider;
import com.philips.cdp.registration.controller.LoginTraditional;
import com.philips.cdp.registration.controller.RefreshLoginSession;
import com.philips.cdp.registration.controller.RegisterTraditional;
import com.philips.cdp.registration.controller.ResendVerificationEmail;
import com.philips.cdp.registration.controller.UpdateReceiveMarketingEmail;
import com.philips.cdp.registration.controller.UpdateUserRecord;
import com.philips.cdp.registration.coppa.CoppaConfiguration;
import com.philips.cdp.registration.dao.ConsumerArray;
import com.philips.cdp.registration.dao.ConsumerInterest;
import com.philips.cdp.registration.dao.DIUserProfile;
import com.philips.cdp.registration.dao.UserRegistrationFailureInfo;
import com.philips.cdp.registration.handlers.AddConsumerInterestHandler;
import com.philips.cdp.registration.handlers.ForgotPasswordHandler;
import com.philips.cdp.registration.handlers.RefreshLoginSessionHandler;
import com.philips.cdp.registration.handlers.RefreshUserHandler;
import com.philips.cdp.registration.handlers.ResendVerificationEmailHandler;
import com.philips.cdp.registration.handlers.SocialProviderLoginHandler;
import com.philips.cdp.registration.handlers.TraditionalLoginHandler;
import com.philips.cdp.registration.handlers.TraditionalRegistrationHandler;
import com.philips.cdp.registration.handlers.UpdateReceiveMarketingEmailHandler;
import com.philips.cdp.registration.handlers.UpdateUserRecordHandler;
import com.philips.cdp.registration.hsdp.HsdpUser;
import com.philips.cdp.registration.hsdp.handler.LogoutHandler;
import com.philips.cdp.registration.ui.utils.RegConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class User {

    public String mEmail, mGivenName, mPassword, mDisplayName;

    private boolean mOlderThanAgeLimit, mReceiveMarketingEmails, mEmailVerified;

    private Context mContext;

    private JSONObject mConsumerInterestObject;

    private JSONArray mConsumerInterestArray;

    private CaptureRecord mCapturedData;

    private String USER_EMAIL = "email";

    private String USER_GIVEN_NAME = "givenName";

    private String USER_FAMILY_NAME = "familyName";

    private String USER_PASSWORD = "password";

    private String USER_DISPLAY_NAME = "displayName";

    private String USER_OLDER_THAN_AGE_LIMIT = "olderThanAgeLimit";

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

    private UpdateUserRecordHandler mUpdateUserRecordHandler;

    public User(Context context) {
        mContext = context;
        mUpdateUserRecordHandler = new UpdateUserRecord(context);
    }

    // For Traditional SignIn
    public void loginUsingTraditional(String emailAddress, String password,
                                      TraditionalLoginHandler traditionalLoginHandler) {

        if (emailAddress != null && password != null) {
            LoginTraditional loginTraditionalResultHandler = new LoginTraditional(
                    traditionalLoginHandler, mContext, mUpdateUserRecordHandler, emailAddress,
                    password);
            Jump.performTraditionalSignIn(emailAddress, password, loginTraditionalResultHandler,
                    null);
        } else {
            UserRegistrationFailureInfo userRegistrationFailureInfo = new UserRegistrationFailureInfo();
            userRegistrationFailureInfo.setErrorCode(RegConstants.DI_PROFILE_NULL_ERROR_CODE);
            traditionalLoginHandler.onLoginFailedWithError(userRegistrationFailureInfo);
        }
    }

    // For Social SignIn Using Provider
    public void loginUserUsingSocialProvider(Activity activity, String providerName,
                                             SocialProviderLoginHandler socialLoginHandler, String mergeToken) {
        if (providerName != null && activity != null) {
            LoginSocialProvider loginSocialResultHandler = new LoginSocialProvider(
                    socialLoginHandler, mContext, mUpdateUserRecordHandler);
            Jump.showSignInDialog(activity, providerName, loginSocialResultHandler, mergeToken);
        } else {
            UserRegistrationFailureInfo userRegistrationFailureInfo = new UserRegistrationFailureInfo();
            userRegistrationFailureInfo.setErrorCode(RegConstants.DI_PROFILE_NULL_ERROR_CODE);
            socialLoginHandler.onLoginFailedWithError(userRegistrationFailureInfo);
        }
    }

    // moved app logic to set user info (traditional login) in diuserprofile to
    // framework.
    public void registerUserInfoForTraditional(String mGivenName, String mUserEmail,
                                               String password, boolean olderThanAgeLimit, boolean isReceiveMarketingEmail,
                                               TraditionalRegistrationHandler traditionalRegisterHandler) {

        DIUserProfile profile = new DIUserProfile();
        profile.setGivenName(mGivenName);
        profile.setEmail(mUserEmail);
        profile.setPassword(password);
        profile.setOlderThanAgeLimit(olderThanAgeLimit);
        profile.setReceiveMarketingEmail(isReceiveMarketingEmail);

        registerNewUserUsingTraditional(profile, traditionalRegisterHandler);

    }

    // For Traditional Registration
    public void registerNewUserUsingTraditional(DIUserProfile diUserProfile,
                                                TraditionalRegistrationHandler traditionalRegisterHandler) {

        if (diUserProfile != null) {

            mEmail = diUserProfile.getEmail();
            mGivenName = diUserProfile.getGivenName();
            mPassword = diUserProfile.getPassword();
            mOlderThanAgeLimit = diUserProfile.getOlderThanAgeLimit();
            mReceiveMarketingEmails = diUserProfile.getReceiveMarketingEmail();

            JSONObject newUser = new JSONObject();
            try {
                newUser.put(USER_EMAIL, mEmail).put(USER_GIVEN_NAME, mGivenName)
                        .put(USER_PASSWORD, mPassword)
                        .put(USER_OLDER_THAN_AGE_LIMIT, mOlderThanAgeLimit)
                        .put(USER_RECEIVE_MARKETING_EMAIL, mReceiveMarketingEmails);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "On registerNewUserUsingTraditional,Caught JSON Exception");
            }
            RegisterTraditional traditionalRegisterResultHandler = new RegisterTraditional(
                    traditionalRegisterHandler, mContext, mUpdateUserRecordHandler);
            Jump.registerNewUser(newUser, null, traditionalRegisterResultHandler);
        } else {
            UserRegistrationFailureInfo userRegistrationFailureInfo = new UserRegistrationFailureInfo();
            userRegistrationFailureInfo.setErrorCode(RegConstants.DI_PROFILE_NULL_ERROR_CODE);
            traditionalRegisterHandler.onRegisterFailedWithFailure(userRegistrationFailureInfo);
        }
    }

    // For Forgot password
    public void forgotPassword(String emailAddress, ForgotPasswordHandler forgotPasswordHandler) {

        if (emailAddress != null) {
            ForgotPassword forgotPasswordResultHandler = new ForgotPassword(forgotPasswordHandler);
            Jump.performForgotPassword(emailAddress, forgotPasswordResultHandler);
        } else {
            UserRegistrationFailureInfo userRegistrationFailureInfo = new UserRegistrationFailureInfo();
            userRegistrationFailureInfo.setErrorCode(RegConstants.DI_PROFILE_NULL_ERROR_CODE);

            forgotPasswordHandler.onSendForgotPasswordFailedWithError(userRegistrationFailureInfo);
        }
    }

    // For Refresh login Session
    public void refreshLoginSession(RefreshLoginSessionHandler refreshLoginSessionHandler, Context context) {
        CaptureRecord captureRecord = CaptureRecord.loadFromDisk(mContext);
        if (captureRecord == null) {
            return;
        }
        RefreshLoginSession refreshLoginhandler = new RefreshLoginSession(
                refreshLoginSessionHandler);
        captureRecord.refreshAccessToken(refreshLoginhandler, context);
    }

    // For Resend verification emails
    public void resendVerificationMail(String emailAddress,
                                       ResendVerificationEmailHandler resendVerificationEmail) {

        if (emailAddress != null) {
            ResendVerificationEmail resendVerificationEmailHandler = new ResendVerificationEmail(
                    resendVerificationEmail);
            Jump.resendEmailVerification(emailAddress, resendVerificationEmailHandler);
        } else {
            UserRegistrationFailureInfo userRegistrationFailureInfo = new UserRegistrationFailureInfo();
            userRegistrationFailureInfo.setErrorCode(RegConstants.DI_PROFILE_NULL_ERROR_CODE);

            resendVerificationEmail
                    .onResendVerificationEmailFailedWithError(userRegistrationFailureInfo);
        }
    }

    // For handling merge scenario
    public void mergeToTraditionalAccount(String emailAddress, String password, String mergeToken,
                                          TraditionalLoginHandler traditionalLoginHandler) {

        if (emailAddress != null && password != null) {
            LoginTraditional loginTraditionalResultHandler = new LoginTraditional(
                    traditionalLoginHandler, mContext, mUpdateUserRecordHandler, emailAddress,
                    password);
            Jump.performTraditionalSignIn(emailAddress, password, loginTraditionalResultHandler,
                    mergeToken);
        } else {
            UserRegistrationFailureInfo userRegistrationFailureInfo = new UserRegistrationFailureInfo();
            userRegistrationFailureInfo.setErrorCode(RegConstants.DI_PROFILE_NULL_ERROR_CODE);

            traditionalLoginHandler.onLoginFailedWithError(userRegistrationFailureInfo);
        }
    }

    // moved app logic to set user info ( social sign in ) in diuserprofile to
    // framework.
    public void registerUserInfoForSocial(String givenName, String displayName, String familyName,
                                          String userEmail, boolean olderThanAgeLimit, boolean isReceiveMarketingEmail,
                                          SocialProviderLoginHandler socialProviderLoginHandler, String socialRegistrationToken) {

        DIUserProfile profile = new DIUserProfile();
        profile.setGivenName(givenName);
        profile.setDisplayName(displayName);
        profile.setFamilyName(familyName);
        profile.setEmail(userEmail);
        profile.setOlderThanAgeLimit(olderThanAgeLimit);
        profile.setReceiveMarketingEmail(isReceiveMarketingEmail);

        completeSocialProviderLogin(profile, socialProviderLoginHandler, socialRegistrationToken);

    }

    // For Two Step registration
    public void completeSocialProviderLogin(DIUserProfile diUserProfile,
                                            SocialProviderLoginHandler socialProviderLoginHandler, String socialRegistrationToken) {
        String familyName = "";
        if (diUserProfile != null) {

            mEmail = diUserProfile.getEmail();
            mGivenName = diUserProfile.getGivenName();
            familyName = diUserProfile.getFamilyName();
            mPassword = diUserProfile.getPassword();
            mDisplayName = diUserProfile.getDisplayName();
            mOlderThanAgeLimit = diUserProfile.getOlderThanAgeLimit();
            mReceiveMarketingEmails = diUserProfile.getReceiveMarketingEmail();

            JSONObject newUser = new JSONObject();
            try {
                newUser.put(USER_EMAIL, mEmail).put(USER_GIVEN_NAME, mGivenName)
                        .put(USER_FAMILY_NAME, familyName).put(USER_PASSWORD, mPassword)
                        .put(USER_DISPLAY_NAME, mDisplayName)
                        .put(USER_OLDER_THAN_AGE_LIMIT, mOlderThanAgeLimit)
                        .put(USER_RECEIVE_MARKETING_EMAIL, mReceiveMarketingEmails);

            } catch (JSONException e) {
                Log.e(LOG_TAG, "On completeSocialProviderLogin,Caught JSON Exception");
            }

            ContinueSocialProviderLogin continueSocialProviderLogin = new ContinueSocialProviderLogin(
                    socialProviderLoginHandler, mContext, mUpdateUserRecordHandler);
            Jump.registerNewUser(newUser, socialRegistrationToken, continueSocialProviderLogin);
        } else {
            UserRegistrationFailureInfo userRegistrationFailureInfo = new UserRegistrationFailureInfo();
            userRegistrationFailureInfo.setErrorCode(RegConstants.DI_PROFILE_NULL_ERROR_CODE);

            socialProviderLoginHandler
                    .onContinueSocialProviderLoginFailure(userRegistrationFailureInfo);
        }
    }

    // For getting values from Captured and Saved Json object
    public DIUserProfile getUserInstance(Context context) {
        DIUserProfile diUserProfile = new DIUserProfile();
        CaptureRecord captured = CaptureRecord.loadFromDisk(context);

        if (captured == null)
            return null;
        try {

            JSONObject mObject = new JSONObject(captured.toString());
            diUserProfile.setEmail(mObject.getString(USER_EMAIL));
            diUserProfile.setGivenName(mObject.getString(USER_GIVEN_NAME));
            diUserProfile.setDisplayName(mObject.getString(USER_DISPLAY_NAME));
            diUserProfile
                    .setReceiveMarketingEmail(mObject.getBoolean(USER_RECEIVE_MARKETING_EMAIL));
            diUserProfile.setJanrainUUID(mObject.getString(USER_JANRAIN_UUID));

        } catch (JSONException e) {
            Log.e(LOG_TAG, "On getUserInstance,Caught JSON Exception");
        }
        return diUserProfile;
    }

    // For checking email verification
    public boolean getEmailVerificationStatus(Context context) {
        mEmailVerified = false;
        CaptureRecord captured = CaptureRecord.loadFromDisk(context);

        if (captured == null)
            return false;
        try {
            JSONObject mObject = new JSONObject(captured.toString());
            if (mObject.isNull(USER_EMAIL_VERIFIED)) {
                mEmailVerified = false;
            } else {
                mEmailVerified = true;
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "On getEmailVerificationStatus,Caught JSON Exception");
        }
        return mEmailVerified;
    }

    // check merge flow error for capture
    public boolean handleMergeFlowError(String existingProvider) {
        if (existingProvider.equals(USER_CAPTURE)) {
            return true;
        }
        return false;
    }

    // For update receive marketing email
    public void updateReceiveMarketingEmail(
            final UpdateReceiveMarketingEmailHandler updateReceiveMarketingEmail,
            final boolean receiveMarketingEmail) {
        final User user = new User(mContext);
        user.refreshLoginSession(new RefreshLoginSessionHandler() {

            @Override
            public void onRefreshLoginSessionSuccess() {
                updateMarketingEmailAfterRefreshAccessToken(updateReceiveMarketingEmail,
                        receiveMarketingEmail);
            }

            @Override
            public void onRefreshLoginSessionFailedWithError(int error) {
                updateReceiveMarketingEmail.onUpdateReceiveMarketingEmailFailedWithError(0);
            }
        }, mContext);
    }

    private void updateMarketingEmailAfterRefreshAccessToken(
            UpdateReceiveMarketingEmailHandler updateReceiveMarketingEmail,
            boolean receiveMarketingEmail) {
        mCapturedData = CaptureRecord.loadFromDisk(mContext);
        JSONObject originalUserInfo = CaptureRecord.loadFromDisk(mContext);
        UpdateReceiveMarketingEmail updateReceiveMarketingEmailHandler = new UpdateReceiveMarketingEmail(
                updateReceiveMarketingEmail, mContext, receiveMarketingEmail);
        if (mCapturedData != null) {
            try {
                mCapturedData.put(USER_RECEIVE_MARKETING_EMAIL, receiveMarketingEmail);
                try {
                    mCapturedData.synchronize(updateReceiveMarketingEmailHandler, originalUserInfo);
                } catch (InvalidApidChangeException e) {
                    Log.e(LOG_TAG,
                            "On updateReceiveMarketingEmail,Caught InvalidApidChange Exception");
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "On updateReceiveMarketingEmail,Caught JSON Exception");
            }
        }
    }

    // For updating consumer interests
    public void addConsumerInterest(AddConsumerInterestHandler addConsumerInterestHandler,
                                    ConsumerArray consumerArray) {

        AddConsumerInterest addConsumerInterest = new AddConsumerInterest(
                addConsumerInterestHandler);
        CaptureRecord captured = CaptureRecord.loadFromDisk(mContext);
        JSONObject originalUserInfo = CaptureRecord.loadFromDisk(mContext);
        mConsumerInterestArray = new JSONArray();
        ConsumerArray consumer = ConsumerArray.getInstance();

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
                    Log.e(LOG_TAG, "On addConsumerInterest,Caught JSON Exception");
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

    // For Log out
    public void logout() {
        CoppaConfiguration.clearConfiguration();
        Jump.signOutCaptureUser(mContext);
        CaptureRecord.deleteFromDisk(mContext);
    }

    // For getting access token
    public String getAccessToken() {

        CaptureRecord captureRecord = CaptureRecord.loadFromDisk(mContext);

        if (captureRecord == null) {
            return null;
        }
        return captureRecord.getAccessToken();
    }

    /**
     * Refresh User object and align with Server
     *
     * @param context Application context
     * @param handler Callback handler
     */
    public void refreshUser(final Context context, final RefreshUserHandler handler) {
        if (Jump.getSignedInUser() == null) {
            handler.onRefreshUserFailed(0);
            return;
        }
        Jump.fetchCaptureUserFromServer(new CaptureApiResultHandler() {

            @Override
            public void onSuccess(JSONObject response) {
                Jump.saveToDisk(context);
                buildCoppaConfiguration();
                handler.onRefreshUserSuccess();
            }

            @Override
            public void onFailure(CaptureAPIError failureParam) {
                handler.onRefreshUserFailed(0);
            }
        });
    }

    public void buildCoppaConfiguration() {
        if (Jump.getSignedInUser() != null) {
            CoppaConfiguration.getCoopaConfigurationFlields(Jump.getSignedInUser());
        }
    }

    public void logoutHsdp(final LogoutHandler logoutHandler) {

        final HsdpUser hsdpUser = new HsdpUser(mContext);
        hsdpUser.hsdpLogOut(new LogoutHandler() {
            @Override
            public void onHsdpLogoutSuccess() {
                CoppaConfiguration.clearConfiguration();
                Jump.signOutCaptureUser(mContext);
                CaptureRecord.deleteFromDisk(mContext);
                hsdpUser.deleteFromDisk();
                logoutHandler.onHsdpLogoutSuccess();
            }

            @Override
            public void onHsdpLogoutFailure(int responseCode, String message) {
                logoutHandler.onHsdpLogoutFailure(responseCode, message);
            }
        });

    }
}
