package com.philips.cdp.registration.controller;

import android.content.Context;

import com.philips.cdp.registration.User;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.dao.UserRegistrationFailureInfo;
import com.philips.cdp.registration.events.UserRegistrationHelper;
import com.philips.cdp.registration.handlers.LoginHandler;
import com.philips.cdp.registration.handlers.SocialLoginProviderHandler;
import com.philips.cdp.registration.hsdp.HsdpUser;
import com.philips.cdp.registration.listener.HSDPAuthenticationListener;
import com.philips.cdp.registration.ui.utils.FieldsValidator;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.ThreadUtils;

public class HSDPLoginService {
    private static final String TAG = "HSDPLoginService";
    private Context mContext;
    private User mUser;
    HsdpUser hsdpUser;

    public HSDPLoginService(Context mContext) {
        this.mContext = mContext;
        this.mUser = new User(mContext);
        hsdpUser = new HsdpUser(mContext);
    }

    String getUserEmailOrMobile(User user) {
        String emailorMobile;
        if (FieldsValidator.isValidEmail(user.getEmail())) {
            emailorMobile = user.getEmail();
        } else {
            emailorMobile = user.getMobile();
        }
        return emailorMobile;
    }

    public void hsdpLogin(String accessToken, String emailOrMobile, HSDPAuthenticationListener hsdpAuthenticationListener) {

        hsdpUser.login(emailOrMobile, accessToken, new LoginHandler() {

            @Override
            public void onLoginSuccess() {
                if (RegistrationConfiguration.getInstance().isHSDPSkipLoginConfigurationAvailable())
                    hsdpAuthenticationListener.onHSDPLoginSuccess();
                UserRegistrationHelper.getInstance().notifyOnHSDPLoginSuccess();
                RLog.d(TAG, "onSuccess : if : HSDPAuthenticationListener : onLoginSuccess : is called with :" + mUser.getUserLoginState());
            }

            @Override
            public void onLoginFailedWithError(UserRegistrationFailureInfo userRegistrationFailureInfo) {
                if (RegistrationConfiguration.getInstance().isHSDPSkipLoginConfigurationAvailable())
                    hsdpAuthenticationListener.onHSDPLoginFailure(userRegistrationFailureInfo.getErrorCode(), userRegistrationFailureInfo.getErrorDescription());
                UserRegistrationHelper.getInstance().notifyOnHSDPLoginFailure(userRegistrationFailureInfo.getErrorCode(), userRegistrationFailureInfo.getErrorDescription());
                RLog.d(TAG, "onLoginFailedWithError : if : HSDPAuthenticationListener : onLoginFailedWithError : is called :" + userRegistrationFailureInfo.getErrorCode());
            }
        });
    }

    void hsdpLogin(String accessToken, String emailOrMobile, LoginHandler loginHandler) {
        HsdpUser hsdpUser = new HsdpUser(mContext);
        RLog.d(TAG, "hsdpLogin : with SocialLoginProviderHandler");
        hsdpUser.login(emailOrMobile, accessToken, new LoginHandler() {


            @Override
            public void onLoginSuccess() {

                UserRegistrationHelper.getInstance().notifyOnHSDPLoginSuccess();
                if (loginHandler instanceof SocialLoginProviderHandler) {
                    ThreadUtils.postInMainThread(mContext, ((SocialLoginProviderHandler) loginHandler)::onContinueSocialProviderLoginSuccess);
                } else {
                    loginHandler.onLoginSuccess();
                }
                RLog.d(TAG, "onSuccess : if : SocialLoginProviderHandler : onLoginSuccess : is called with :" + mUser.getUserLoginState());
            }

            @Override
            public void onLoginFailedWithError(UserRegistrationFailureInfo userRegistrationFailureInfo) {
                if (loginHandler instanceof SocialLoginProviderHandler) {
                    ThreadUtils.postInMainThread(mContext, () ->
                            loginHandler.onLoginFailedWithError(userRegistrationFailureInfo));
                } else {
                    loginHandler.onLoginFailedWithError(userRegistrationFailureInfo);
                }


                UserRegistrationHelper.getInstance().notifyOnHSDPLoginFailure(userRegistrationFailureInfo.getErrorCode(), userRegistrationFailureInfo.getErrorDescription());
                RLog.d(TAG, "onLoginFailedWithError : if : SocialLoginProviderHandler : onLoginFailedWithError : is called :" + userRegistrationFailureInfo.getErrorCode());
            }
        });
    }

}
