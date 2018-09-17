/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.controller;


import android.content.Context;

import com.janrain.android.Jump;
import com.janrain.android.capture.CaptureRecord;
import com.janrain.android.engage.session.JRSession;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.errors.ErrorCodes;
import com.philips.cdp.registration.events.JumpFlowDownloadStatusListener;
import com.philips.cdp.registration.handlers.RefreshLoginSessionHandler;
import com.philips.cdp.registration.hsdp.HsdpUser;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.settings.UserRegistrationInitializer;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegConstants;
import com.philips.cdp.registration.ui.utils.ThreadUtils;

public class RefreshUserSession implements RefreshLoginSessionHandler, JumpFlowDownloadStatusListener {

    private RefreshLoginSessionHandler mRefreshLoginSessionHandler;
    private Context mContext;
    private static final String TAG = "RefreshUserSession";


    public RefreshUserSession(RefreshLoginSessionHandler refreshLoginSessionHandler, Context context) {
        mRefreshLoginSessionHandler = refreshLoginSessionHandler;
        mContext = context;
    }


    private void refreshSession() {
        if (!UserRegistrationInitializer.getInstance().isRefreshUserSessionInProgress()) {
            CaptureRecord captureRecord = Jump.getSignedInUser();
            RLog.d(TAG,"refreshSession : if : false isRefreshUserSessionInProgress");
            if (captureRecord == null) {
                RLog.d(TAG,"refreshSession : captureRecord is null");
                return;
            }
            UserRegistrationInitializer.getInstance().setRefreshUserSessionInProgress(true);
            captureRecord.refreshAccessToken(new RefreshLoginSession(this), mContext);
        } else {
            ThreadUtils.postInMainThread(mContext, () ->
                    mRefreshLoginSessionHandler.onRefreshLoginSessionInProgress("Refresh already scheduled"));
            RLog.d(TAG,"refreshSession : else : true isRefreshUserSessionInProgress");
        }
    }

    private void refreshHsdpAccessToken() {
        RLog.d(TAG,"refreshHsdpAccessToken : is called");
        final HsdpUser hsdpUser = new HsdpUser(mContext);
        hsdpUser.refreshToken(new RefreshLoginSessionHandler() {
            @Override
            public void onRefreshLoginSessionSuccess() {
                UserRegistrationInitializer.getInstance().setRefreshUserSessionInProgress(false);
                ThreadUtils.postInMainThread(mContext, () ->
                        mRefreshLoginSessionHandler.onRefreshLoginSessionSuccess());
                RLog.d(TAG,"refreshHsdpAccessToken : RefreshLoginSessionHandler : onRefreshLoginSessionSuccess is called");
            }

            @Override
            public void onRefreshLoginSessionFailedWithError(int error) {
                RLog.d(TAG,"refreshHsdpAccessToken : RefreshLoginSessionHandler : onRefreshLoginSessionFailedWithError is called");
                UserRegistrationInitializer.getInstance().setRefreshUserSessionInProgress(false);
                if (error == Integer.parseInt(RegConstants.INVALID_ACCESS_TOKEN_CODE)
                        || error == Integer.parseInt(RegConstants.INVALID_REFRESH_TOKEN_CODE)) {

                    clearData();
                    RegistrationHelper.getInstance().getUserRegistrationListener().notifyOnLogoutSuccessWithInvalidAccessToken();
                }
                ThreadUtils.postInMainThread(mContext, () ->
                        mRefreshLoginSessionHandler.onRefreshLoginSessionFailedWithError(error));
            }

            @Override
            public void onRefreshLoginSessionInProgress(String message) {
                RLog.d(TAG,"refreshHsdpAccessToken : RefreshLoginSessionHandler : onRefreshLoginSessionInProgress is called");
                ThreadUtils.postInMainThread(mContext, () ->
                        mRefreshLoginSessionHandler.onRefreshLoginSessionInProgress(message));
            }
        });
    }

    public void refreshUserSession() {

        if (!UserRegistrationInitializer.getInstance().isJumpInitializated()) {
            UserRegistrationInitializer.getInstance().registerJumpFlowDownloadListener(this);
        } else {
            RLog.d(TAG, "refreshUserSession : Jump initialized");

            refreshSession();
            return;

        }
        if (!UserRegistrationInitializer.getInstance().isRegInitializationInProgress()) {
            RLog.d(TAG, "refreshUserSession jump initialization on progress");
            RegistrationHelper.getInstance().initializeUserRegistration(mContext);
        }
    }

    @Override
    public void onFlowDownloadSuccess() {
        RLog.d(TAG, "onFlowDownloadSuccess : Jump  initialized now after coming to this screen,  was in progress earlier, now performing forgot password");
        refreshSession();
        UserRegistrationInitializer.getInstance().unregisterJumpFlowDownloadListener();
    }

    @Override
    public void onFlowDownloadFailure() {
        RLog.d(TAG, "onFlowDownloadFailure : Jump not initialized, was initialized but failed");
        ThreadUtils.postInMainThread(mContext, () ->
                mRefreshLoginSessionHandler.onRefreshLoginSessionFailedWithError(ErrorCodes.UNKNOWN_ERROR));
        UserRegistrationInitializer.getInstance().unregisterJumpFlowDownloadListener();
    }

    @Override
    public void onRefreshLoginSessionSuccess() {
        RLog.d(TAG, "onRefreshLoginSessionSuccess : is called");
        if (RegistrationConfiguration.getInstance().isHsdpFlow()) {
            refreshHsdpAccessToken();
            RLog.d(TAG, "onRefreshLoginSessionSuccess : is HsdpFlow");
            return;
        }
        UserRegistrationInitializer.getInstance().setRefreshUserSessionInProgress(false);
        mRefreshLoginSessionHandler.onRefreshLoginSessionSuccess();

    }

    @Override
    public void onRefreshLoginSessionFailedWithError(int error) {
        RLog.d(TAG, "onRefreshLoginSessionFailedWithError : error"+error);
        if (error == Integer.parseInt(RegConstants.INVALID_JANRAIN_NO_ACCESS_GRANT_CODE)) {
            clearData();
            RegistrationHelper.getInstance().getUserRegistrationListener().notifyOnLogoutSuccessWithInvalidAccessToken();
        }
        UserRegistrationInitializer.getInstance().setRefreshUserSessionInProgress(false);
        mRefreshLoginSessionHandler.onRefreshLoginSessionFailedWithError(error);
    }

    @Override
    public void onRefreshLoginSessionInProgress(String message) {
        RLog.d(TAG, "onRefreshLoginSessionInProgress : is called");
        mRefreshLoginSessionHandler.onRefreshLoginSessionInProgress(message);
    }

    private void clearData() {
        RLog.d(TAG, "clearData : is called");
        HsdpUser hsdpUser = new HsdpUser(mContext);
        hsdpUser.deleteFromDisk();
        if (JRSession.getInstance() != null) {
            JRSession.getInstance().signOutAllAuthenticatedUsers();
        }
        CaptureRecord.deleteFromDisk(mContext);
    }
}
