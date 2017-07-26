
/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.controller;

import android.content.Context;
import android.util.Log;

import com.janrain.android.Jump;
import com.janrain.android.capture.CaptureApiError;
import com.philips.cdp.registration.R;
import com.philips.cdp.registration.dao.DIUserProfile;
import com.philips.cdp.registration.dao.UserRegistrationFailureInfo;
import com.philips.cdp.registration.events.JumpFlowDownloadStatusListener;
import com.philips.cdp.registration.handlers.TraditionalRegistrationHandler;
import com.philips.cdp.registration.handlers.UpdateUserRecordHandler;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.settings.UserRegistrationInitializer;
import com.philips.cdp.registration.ui.utils.FieldsValidator;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegConstants;
import com.philips.cdp.registration.ui.utils.ThreadUtils;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

public class RegisterTraditional implements Jump.SignInResultHandler, Jump.SignInCodeHandler, JumpFlowDownloadStatusListener, TraditionalRegistrationHandler {

    private String LOG_TAG = "RegisterTraditional";

    private Context mContext;

    private TraditionalRegistrationHandler mTraditionalRegisterHandler;

    private UpdateUserRecordHandler mUpdateUserRecordHandler;

    private DIUserProfile mProfile;

    @Inject
    ServiceDiscoveryInterface serviceDiscoveryInterface;

    public RegisterTraditional(TraditionalRegistrationHandler traditionalRegisterHandler,
                               Context context, UpdateUserRecordHandler updateUserRecordHandler) {
        mTraditionalRegisterHandler = traditionalRegisterHandler;
        mContext = context;
        mUpdateUserRecordHandler = updateUserRecordHandler;
    }

    @Override
    public void onSuccess() {
        Jump.saveToDisk(mContext);
        mUpdateUserRecordHandler.updateUserRecordRegister();
        ThreadUtils.postInMainThread(mContext, () ->
                mTraditionalRegisterHandler.onRegisterSuccess());
    }

    @Override
    public void onCode(String code) {

    }

    @Override
    public void onFailure(SignInError error) {
        try {
            UserRegistrationFailureInfo userRegistrationFailureInfo = new UserRegistrationFailureInfo();
            userRegistrationFailureInfo.setError(error.captureApiError);
            if (error.captureApiError.code == -1) {
                userRegistrationFailureInfo.setErrorDescription(mContext.getString(R.string.reg_JanRain_Server_Connection_Failed));
            }
            handleInvalidInputs(error.captureApiError, userRegistrationFailureInfo);
            userRegistrationFailureInfo.setErrorCode(error.captureApiError.code);
            ThreadUtils.postInMainThread(mContext, () ->
                    mTraditionalRegisterHandler.onRegisterFailedWithFailure(userRegistrationFailureInfo));
        } catch (Exception e) {
            Log.e("Exception :", "SignInError :" + e.getMessage());
        }

    }

    private void handleInvalidInputs(CaptureApiError error,
                                     UserRegistrationFailureInfo userRegistrationFailureInfo) {
        if (null != error && null != error.error
                && error.error.equals(RegConstants.INVALID_FORM_FIELDS)) {
            try {
                JSONObject object = error.raw_response;
                JSONObject jsonObject = (JSONObject) object.get(RegConstants.INVALID_FIELDS);
                if (jsonObject != null) {

                    if (!jsonObject.isNull(RegConstants.TRADITIONAL_REGISTRATION_FIRST_NAME)) {
                        userRegistrationFailureInfo
                                .setFirstNameErrorMessage(getErrorMessage(jsonObject
                                        .getJSONArray(RegConstants.TRADITIONAL_REGISTRATION_FIRST_NAME)));
                    }

                    if (!jsonObject.isNull(RegConstants.TRADITIONAL_REGISTRATION_EMAIL_ADDRESS)) {
                        userRegistrationFailureInfo
                                .setEmailErrorMessage(getErrorMessage(jsonObject
                                        .getJSONArray(RegConstants.TRADITIONAL_REGISTRATION_EMAIL_ADDRESS)));
                    }

                    if (!jsonObject.isNull(RegConstants.TRADITIONAL_REGISTRATION_PASSWORD)) {
                        userRegistrationFailureInfo
                                .setPasswordErrorMessage(getErrorMessage(jsonObject
                                        .getJSONArray(RegConstants.TRADITIONAL_REGISTRATION_PASSWORD)));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private String getErrorMessage(JSONArray jsonArray)
            throws JSONException {
        if (null == jsonArray) {
            return null;
        }
        return (String) jsonArray.get(0);
    }


    // moved app logic to set user info (traditional login) in diuserprofile to
    // framework.
    public void registerUserInfoForTraditional(String mGivenName, String mUserEmailorMobile,
                                               String password, boolean olderThanAgeLimit, boolean isReceiveMarketingEmail
    ) {

        mProfile = new DIUserProfile();
        mProfile.setGivenName(mGivenName);
        if (FieldsValidator.isValidEmail(mUserEmailorMobile)) {
            mProfile.setEmail(mUserEmailorMobile);
        } else {
            mProfile.setMobile(mUserEmailorMobile);
        }
        mProfile.setPassword(password);
        mProfile.setOlderThanAgeLimit(olderThanAgeLimit);
        mProfile.setReceiveMarketingEmail(isReceiveMarketingEmail);

        if (!UserRegistrationInitializer.getInstance().isJumpInitializated()) {
            UserRegistrationInitializer.getInstance().registerJumpFlowDownloadListener(this);
        } else {
            RLog.i(LOG_TAG, "Jump initialized, registering");
            if (mTraditionalRegisterHandler != null) {
                registerNewUserUsingTraditional();
            }
            return;


        }
        if (!UserRegistrationInitializer.getInstance().isRegInitializationInProgress()) {
            RLog.i(LOG_TAG, "Jump not initialized, initializing");
            RegistrationHelper.getInstance().initializeUserRegistration(mContext);
        }


    }


    // For Traditional Registration
    private void registerNewUserUsingTraditional() {

        if (mProfile != null) {

            JSONObject newUser = new JSONObject();
            try {
                newUser.put("email", mProfile.getEmail())
                        .put("mobileNumber", mProfile.getMobile())
                        .put("givenName", mProfile.getGivenName())
                        .put("password", mProfile.getPassword())
                        .put("olderThanAgeLimit", mProfile.getOlderThanAgeLimit())
                        .put("receiveMarketingEmail", mProfile.getReceiveMarketingEmail());


                addifRussia(newUser);


            } catch (JSONException e) {
                Log.e(LOG_TAG, "On registerNewUserUsingTraditional,Caught JSON Exception");
            }

            Jump.registerNewUser(newUser, null, this);
        } else {
            UserRegistrationFailureInfo userRegistrationFailureInfo = new UserRegistrationFailureInfo();
            userRegistrationFailureInfo.setErrorCode(RegConstants.DI_PROFILE_NULL_ERROR_CODE);
            userRegistrationFailureInfo.setErrorDescription(mContext.getString(R.string.reg_JanRain_Server_Connection_Failed));
            ThreadUtils.postInMainThread(mContext, () ->
                    mTraditionalRegisterHandler.onRegisterFailedWithFailure(userRegistrationFailureInfo));
        }
    }

    private void addifRussia(JSONObject newUser) {
        String currentCountry = serviceDiscoveryInterface.getHomeCountry();
        if (currentCountry.equalsIgnoreCase("RU")) {
            try {
                JSONObject one = new JSONObject();
                one.put("one", "true");
                JSONObject control = new JSONObject();
                control.put("controlFields", one);
                newUser.put("janrain", control);
            } catch (Exception e) {
            }
        }
    }


    @Override
    public void onFlowDownloadSuccess() {
        if (mTraditionalRegisterHandler != null) {
            RLog.i(LOG_TAG, "Jump  initialized now after coming to this screen,  was in progress earlier, registering user");
            registerNewUserUsingTraditional();
        }
        UserRegistrationInitializer.getInstance().unregisterJumpFlowDownloadListener();
    }

    @Override
    public void onFlowDownloadFailure() {
        RLog.i(LOG_TAG, "Jump not initialized, was initialized but failed");
        if (mTraditionalRegisterHandler != null) {
            UserRegistrationFailureInfo userRegistrationFailureInfo = new UserRegistrationFailureInfo();
            userRegistrationFailureInfo.setErrorDescription(mContext.getString(R.string.reg_JanRain_Server_Connection_Failed));
            userRegistrationFailureInfo.setErrorCode(RegConstants.REGISTER_TRADITIONAL_FAILED_SERVER_ERROR);
            ThreadUtils.postInMainThread(mContext, () ->
                    mTraditionalRegisterHandler.onRegisterFailedWithFailure(userRegistrationFailureInfo));
        }

    }


    @Override
    public void onRegisterSuccess() {
        ThreadUtils.postInMainThread(mContext, () ->
                mTraditionalRegisterHandler.onRegisterSuccess());
    }

    @Override
    public void onRegisterFailedWithFailure(UserRegistrationFailureInfo userRegistrationFailureInfo) {
        ThreadUtils.postInMainThread(mContext, () ->
                mTraditionalRegisterHandler.onRegisterFailedWithFailure(userRegistrationFailureInfo));
    }
}
