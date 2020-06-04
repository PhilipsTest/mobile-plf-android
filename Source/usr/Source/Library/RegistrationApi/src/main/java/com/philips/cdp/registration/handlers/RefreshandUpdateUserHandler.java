/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.handlers;

import android.content.Context;

import com.janrain.android.Jump;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.controller.LoginTraditional;
import com.philips.cdp.registration.dao.UserRegistrationFailureInfo;
import com.philips.cdp.registration.errors.ErrorCodes;
import com.philips.cdp.registration.events.JumpFlowDownloadStatusListener;
import com.philips.cdp.registration.hsdp.HsdpUser;
import com.philips.cdp.registration.hsdp.HsdpUserRecordV2;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.settings.UserRegistrationInitializer;
import com.philips.cdp.registration.ui.utils.RLog;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class RefreshandUpdateUserHandler implements JumpFlowDownloadStatusListener {
    private String TAG = "RefreshandUpdateUserHandler";

    private Context mContext;
    private User user;
    private RefreshUserHandler refreshUserHandler;

    public RefreshandUpdateUserHandler(Context context) {
        mContext = context;
    }

    public void refreshAndUpdateUser(final RefreshUserHandler handler, final User user) {
        RLog.d(TAG, "refreshAndUpdateUser");
        refreshUserHandler = handler;
        this.user = user;
        if (!UserRegistrationInitializer.getInstance().isJumpInitializated() && UserRegistrationInitializer.getInstance().isRegInitializationInProgress()) {
            RLog.d(TAG, "refreshAndUpdateUser : not isJumpInitializated and isRegInitializationInProgress");
            UserRegistrationInitializer.getInstance().registerJumpFlowDownloadListener(this);
            RegistrationHelper.getInstance().initializeUserRegistration(mContext);
            return;
        }

        if (!UserRegistrationInitializer.getInstance().isJumpInitializated() && !UserRegistrationInitializer.getInstance().isRegInitializationInProgress()) {
            RLog.d(TAG, "refreshAndUpdateUser : not isJumpInitializated and RegInitialization Not In Progress");
            UserRegistrationInitializer.getInstance().registerJumpFlowDownloadListener(this);
            RegistrationHelper.getInstance().initializeUserRegistration(mContext);
            return;
        }

        refreshUpdateUser(handler, user);
    }

    private void refreshUpdateUser(final RefreshUserHandler handler, final User user) {
        if (Jump.getSignedInUser() == null) {
            RLog.e(TAG, "refreshUpdateUser : Jump.getSignedInUser() is NULL");
            handler.onRefreshUserFailed(0);
            return;
        }
        Jump.performFetchCaptureData(new Jump.CaptureApiResultHandler() {

            @Override
            public void onSuccess(JSONObject response) {
                Jump.saveToDisk(mContext);
                RLog.d(TAG, "refreshUpdateUser : onSuccess : " + response.toString());
                final RegistrationConfiguration registrationConfiguration = RegistrationConfiguration.getInstance();
                if (!registrationConfiguration.isHsdpFlow()) {
                    handler.onRefreshUserSuccess();
                    RLog.d(TAG, "refreshUpdateUser : is not HSDP flow  ");
                    return;
                }

                if ((user.isEmailVerified() || user.isMobileVerified())) {
                    HsdpUser hsdpUser = new HsdpUser(mContext);
                    HsdpUserRecordV2 hsdpUserRecordV2 = hsdpUser.getHsdpUserRecord();
                    if (hsdpUserRecordV2 == null) {
                        RLog.d(TAG, "refreshUpdateUser : hsdpUserRecordV2 is NULL  ");
                        LoginTraditional loginTraditional = new LoginTraditional(new LoginHandler() {
                            @Override
                            public void onLoginSuccess() {
                                RLog.d(TAG, "refreshUpdateUser : onLoginSuccess  ");
                                handler.onRefreshUserSuccess();
                            }

                            @Override
                            public void onLoginFailedWithError(UserRegistrationFailureInfo userRegistrationFailureInfo) {
                                RLog.e(TAG, "refreshUpdateUser : onLoginFailedWithError  ");
                                handler.onRefreshUserFailed(userRegistrationFailureInfo.getErrorCode());
                            }
                        }, mContext, null, null);
                        RLog.d(TAG, "onSuccess : refreshUpdateUser onSuccess isHSDPSkipLoginConfigurationAvailable :" + registrationConfiguration.isHSDPSkipLoginConfigurationAvailable());
                        RLog.d(TAG, "onSuccess : refreshUpdateUser onSuccess isHsdpFlow" + registrationConfiguration.isHsdpFlow());
                        if (!registrationConfiguration.isHSDPSkipLoginConfigurationAvailable() && registrationConfiguration.isHsdpFlow()) {
                            loginTraditional.loginIntoHsdp();
                        } else {
                            handler.onRefreshUserSuccess();
                        }
                    } else {
                        RLog.d(TAG, "refreshUpdateUser : hsdpUserRecordV2 is not NULL  ");
                        handler.onRefreshUserSuccess();
                    }
                } else {
                    RLog.d(TAG, "refreshUpdateUser : isEmailVerified or isMobileVerified is not Verified  ");
                    handler.onRefreshUserSuccess();
                }
            }

            @Override
            public void onFailure(CaptureAPIError failureParam) {
                try{
                    RLog.e(TAG, "onFailure : refreshUpdateUser error " + failureParam.captureApiError.raw_response);

                if (failureParam.captureApiError !=  null && failureParam.captureApiError.code == 414 && failureParam.captureApiError.error.equalsIgnoreCase("access_token_expired")) {

                    user.refreshLoginSession(new RefreshLoginSessionHandler() {
                        @Override
                        public void onRefreshLoginSessionSuccess() {
                            RLog.d(TAG, "refreshLoginSession : onRefreshLoginSessionSuccess  ");
                            handler.onRefreshUserSuccess();
                        }

                        @Override
                        public void onRefreshLoginSessionFailedWithError(int error) {
                            RLog.d(TAG, "refreshLoginSession : onRefreshLoginSessionFailedWithError  ");
                            handler.onRefreshUserFailed(error);
                        }

                        @Override
                        public void forcedLogout() {
                            handler.onRefreshUserFailed(ErrorCodes.HSDP_INPUT_ERROR_1151);
                        }
                    });
                }
                handler.onRefreshUserFailed(0);
                } catch (Exception e){
                    RLog.e(TAG, "onFailure :  Exception " + e.getMessage());
                }

            }
        });
    }

    public void forceHsdpLogin(final RefreshUserHandler handler,LoginTraditional loginTraditional ){
        final RegistrationConfiguration registrationConfiguration = RegistrationConfiguration.getInstance();

        if (!registrationConfiguration.isHSDPSkipLoginConfigurationAvailable() && registrationConfiguration.isHsdpFlow()) {
            loginTraditional.loginIntoHsdp();
        }else {
            handler.onRefreshUserSuccess();
        }
    }

    @NotNull
    public LoginTraditional getLoginTraditional(RefreshUserHandler handler) {
        return new LoginTraditional(getLoginHandler(handler), mContext, null, null);
    }

    @NotNull
    LoginHandler getLoginHandler(RefreshUserHandler handler) {
        return new LoginHandler() {
                @Override
                public void onLoginSuccess() {
                    handler.onRefreshUserSuccess();
                }
                @Override
                public void onLoginFailedWithError(UserRegistrationFailureInfo userRegistrationFailureInfo) {
                    handler.onRefreshUserFailed(userRegistrationFailureInfo.getErrorCode());
                }
            };
    }


    @Override
    public void onFlowDownloadSuccess() {
        RLog.e(TAG, "onFlowDownloadSuccess is called");
        refreshAndUpdateUser(refreshUserHandler, user);
        UserRegistrationInitializer.getInstance().unregisterJumpFlowDownloadListener();

    }

    @Override
    public void onFlowDownloadFailure() {
        RLog.e(TAG, "onFlowDownloadFailure is called");
        UserRegistrationInitializer.getInstance().unregisterJumpFlowDownloadListener();
        if (refreshUserHandler != null) {
            refreshUserHandler.onRefreshUserFailed(0);
        }

    }
}
