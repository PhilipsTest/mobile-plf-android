package com.philips.cdp.registration.controller;

import android.content.Context;

import com.janrain.android.Jump;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.app.tagging.AppTaggingErrors;
import com.philips.cdp.registration.app.tagging.AppTagingConstants;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.dao.UserRegistrationFailureInfo;
import com.philips.cdp.registration.events.UserRegistrationHelper;
import com.philips.cdp.registration.handlers.LoginHandler;
import com.philips.cdp.registration.hsdp.HsdpUser;
import com.philips.cdp.registration.listener.HSDPAuthenticationListener;
import com.philips.cdp.registration.ui.utils.FieldsValidator;
import com.philips.cdp.registration.ui.utils.RLog;

import org.json.JSONObject;

public class BaseHSDPLogin {
    private static final String TAG = BaseHSDPLogin.class.getSimpleName();
    private Context mContext;
    private User mUser;

    public BaseHSDPLogin(Context mContext) {
        this.mContext = mContext;
        this.mUser = new User(mContext);
    }

    public String getUserEmailOrMobile(User user) {
        String emailorMobile;
        if (FieldsValidator.isValidEmail(user.getEmail())) {
            emailorMobile = user.getEmail();
        } else {
            emailorMobile = user.getMobile();
        }
        return emailorMobile;
    }

    public void hsdpLogin(String accessToken, String emailOrMobile, HSDPAuthenticationListener hsdpAuthenticationListener) {
        HsdpUser hsdpUser = new HsdpUser(mContext);
        hsdpUser.login(emailOrMobile, accessToken, Jump.getRefreshSecret(), new LoginHandler() {


            @Override
            public void onLoginSuccess() {
                if (RegistrationConfiguration.getInstance().isSkippedHsdpLoginEnabled())
                    hsdpAuthenticationListener.onHSDPLoginSuccess();
                UserRegistrationHelper.getInstance().notifyOnHSDPLoginSuccess();
                RLog.d(TAG, "onSuccess : if : HSDPAuthenticationListener : onLoginSuccess : is called with :" + mUser.getUserLoginState());
            }

            @Override
            public void onLoginFailedWithError(UserRegistrationFailureInfo userRegistrationFailureInfo) {
                AppTaggingErrors.trackActionRegisterError(userRegistrationFailureInfo, AppTagingConstants.HSDP);
                if (RegistrationConfiguration.getInstance().isSkippedHsdpLoginEnabled())
                    hsdpAuthenticationListener.onHSDPLoginFailure(userRegistrationFailureInfo.getErrorCode(), userRegistrationFailureInfo.getErrorDescription());
                UserRegistrationHelper.getInstance().notifyOnHSDPLoginFailure(userRegistrationFailureInfo.getErrorCode(), userRegistrationFailureInfo.getErrorDescription());
                RLog.d(TAG, "onLoginFailedWithError : if : HSDPAuthenticationListener : onLoginFailedWithError : is called :" + userRegistrationFailureInfo.getErrorCode());
            }

            @Override
            public void onLoginFailedWithTwoStepError(JSONObject prefilledRecord, String socialRegistrationToken) {
                //NOPE
            }

            @Override
            public void onLoginFailedWithMergeFlowError(String mergeToken, String existingProvider, String conflictingIdentityProvider, String conflictingIdpNameLocalized, String existingIdpNameLocalized, String emailId) {
                //NOPE
            }

            @Override
            public void onContinueSocialProviderLoginSuccess() {
                //NOPE
            }

            @Override
            public void onContinueSocialProviderLoginFailure(UserRegistrationFailureInfo userRegistrationFailureInfo) {
                //NOPE
            }
        });
    }

    void hsdpLogin(String accessToken, String emailOrMobile, LoginHandler loginHandler) {
        HsdpUser hsdpUser = new HsdpUser(mContext);
        RLog.d(TAG, "hsdpLogin : with LoginHandler");
        hsdpUser.login(emailOrMobile, accessToken, Jump.getRefreshSecret(), new LoginHandler() {


            @Override
            public void onLoginSuccess() {
                loginHandler.onLoginSuccess();
                UserRegistrationHelper.getInstance().notifyOnHSDPLoginSuccess();
                RLog.d(TAG, "onSuccess : if : LoginHandler : onLoginSuccess : is called with :" + mUser.getUserLoginState());
            }

            @Override
            public void onLoginFailedWithError(UserRegistrationFailureInfo userRegistrationFailureInfo) {
                AppTaggingErrors.trackActionRegisterError(userRegistrationFailureInfo, AppTagingConstants.HSDP);
                if (RegistrationConfiguration.getInstance().isSkippedHsdpLoginEnabled())
                    loginHandler.onLoginFailedWithError(userRegistrationFailureInfo);
                UserRegistrationHelper.getInstance().notifyOnHSDPLoginFailure(userRegistrationFailureInfo.getErrorCode(), userRegistrationFailureInfo.getErrorDescription());
                RLog.d(TAG, "onLoginFailedWithError : if : LoginHandler : onLoginFailedWithError : is called :" + userRegistrationFailureInfo.getErrorCode());
            }

            @Override
            public void onLoginFailedWithTwoStepError(JSONObject prefilledRecord, String socialRegistrationToken) {
                //NOPE
            }

            @Override
            public void onLoginFailedWithMergeFlowError(String mergeToken, String existingProvider, String conflictingIdentityProvider, String conflictingIdpNameLocalized, String existingIdpNameLocalized, String emailId) {
                //NOPE
            }

            @Override
            public void onContinueSocialProviderLoginSuccess() {
                //NOPE
            }

            @Override
            public void onContinueSocialProviderLoginFailure(UserRegistrationFailureInfo userRegistrationFailureInfo) {
                //NOPE
            }
        });
    }

}
