/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.controller;

import android.content.Context;
import androidx.annotation.Nullable;

import com.janrain.android.Jump;
import com.janrain.android.capture.CaptureRecord;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.errors.ErrorCodes;
import com.philips.cdp.registration.errors.HSDPErrorEnum;
import com.philips.cdp.registration.errors.JanrainErrorEnum;
import com.philips.cdp.registration.handlers.RefreshLoginSessionHandler;
import com.philips.cdp.registration.settings.JanrainInitializer;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.ThreadUtils;
import com.philips.cdp.registration.update.UpdateUser;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pif.DataInterface.USR.listeners.UpdateUserDetailsHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateUserDetailsBase implements
        JanrainInitializer.JanrainInitializeListener,
        UpdateUser.UpdateUserListener, RefreshLoginSessionHandler {


    private String TAG = "UpdateUserDetailsBase";

    protected UpdateUserDetailsHandler mUpdateUserDetails;

    protected Context mContext;

    protected CaptureRecord mUpdatedUserdata;


    protected JanrainInitializer mJanrainInitializer;

    UpdateUserDetailsBase(Context mContext) {
        this.mContext = mContext;
    }


    protected void performActualUpdate() {
        //NOP
    }

    protected void performLocalUpdate() {
        if (null != mUpdatedUserdata)
            RLog.d(TAG, "performLocalUpdate : performLocalUpdate to save to disk");
        mUpdatedUserdata.saveToDisk(mContext);
    }

    @Override
    public void onJanrainInitializeSuccess() {
        RLog.d(TAG, "onJanrainInitializeSuccess : performActualUpdate");
        performActualUpdate();
    }

    @Override
    public void onJanrainInitializeFailed() {
        if (null != mUpdateUserDetails)
            RLog.e(TAG, "onJanrainInitializeFailed : onUpdateFailedWithError");
        ThreadUtils.postInMainThread(mContext, () ->
                mUpdateUserDetails
                        .onUpdateFailedWithError(new Error(ErrorCodes.UPDATE_USER_DETAILS_ERROR, JanrainErrorEnum.getLocalizedError(mContext,ErrorCodes.UPDATE_USER_DETAILS_ERROR))));

    }

    @Override
    public boolean isJanrainInitializeRequired() {
        RLog.d(TAG, "isJanrainInitializeRequired : mJanrainInitializer.isJanrainInitializeRequired()");
        return mJanrainInitializer.isJanrainInitializeRequired();
    }

    @Override
    public void onUserUpdateSuccess() {
        performLocalUpdate();
        if (null != mUpdateUserDetails)
            RLog.d(TAG, "onUserUpdateSuccess : mUpdateUserDetails.onUpdateSuccess");
        ThreadUtils.postInMainThread(mContext, () ->
                mUpdateUserDetails.onUpdateSuccess());
    }

    @Override
    public void onUserUpdateFailed(int error) {
        RLog.e(TAG, "onUserUpdateFailed: Error " + error);
        if (error == ErrorCodes.UNKNOWN_ERROR) {
            if (null != mUpdateUserDetails) {
                ThreadUtils.postInMainThread(mContext, () ->
                        mUpdateUserDetails
                                .onUpdateFailedWithError(new Error(ErrorCodes.UNKNOWN_ERROR,JanrainErrorEnum.getLocalizedError(mContext,ErrorCodes.UNKNOWN_ERROR))));
            }
            return;
        }
        //Session Expired and refresh during this case
        if (error == ErrorCodes.JANRAIN_ACCESS_TOKEN_EXPIRED) {
            User user = new User(mContext);
            user.refreshLoginSession(this);
            return;
        }
        ThreadUtils.postInMainThread(mContext, () ->
                mUpdateUserDetails
                        .onUpdateFailedWithError(new Error(ErrorCodes.JANRAIN_ACCESS_TOKEN_EXPIRED,JanrainErrorEnum.getLocalizedError(mContext,ErrorCodes.JANRAIN_ACCESS_TOKEN_EXPIRED))));

    }

    @Override
    public void onRefreshLoginSessionSuccess() {
        performActualUpdate();
    }

    @Override
    public void onRefreshLoginSessionFailedWithError(int error) {
        if (null != mUpdateUserDetails) {
            RLog.e(TAG, "onRefreshLoginSessionFailedWithError : Error onRefreshLoginSessionFailedWithError" + error);
            ThreadUtils.postInMainThread(mContext, () ->
                    mUpdateUserDetails
                            .onUpdateFailedWithError(new Error(ErrorCodes.UPDATE_USER_DETAILS_ERROR,JanrainErrorEnum.getLocalizedError(mContext,ErrorCodes.UPDATE_USER_DETAILS_ERROR))));
        }
    }

    @Override
    public void forcedLogout() {
        if (null != mUpdateUserDetails) {
            RLog.e(TAG, "forcedLogout");
            ThreadUtils.postInMainThread(mContext, () ->
                    mUpdateUserDetails
                            .onUpdateFailedWithError(new Error(ErrorCodes.HSDP_INPUT_ERROR_1151, HSDPErrorEnum.getLocalizedError(mContext,ErrorCodes.HSDP_INPUT_ERROR_1151))));
        }
    }

    @Nullable
    protected JSONObject getCurrentUserAsJsonObject() {
        JSONObject userData = null;
        try {
            CaptureRecord capturedRecord = Jump.getSignedInUser();
            if (capturedRecord == null) {
                RLog.d(TAG, "getCurrentUserAsJsonObject : CaptureRecord loadFromDisk");
                capturedRecord = CaptureRecord.loadFromDisk(mContext);
            }
            userData = new JSONObject(capturedRecord.toString());
        } catch (JSONException e) {
            RLog.e(TAG, "getCurrentUserAsJsonObject: " + e.getMessage());
        }
        return userData;
    }
}
