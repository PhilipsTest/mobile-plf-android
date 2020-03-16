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
import com.philips.cdp.registration.errors.ErrorCodes;
import com.philips.cdp.registration.errors.JanrainErrorEnum;
import com.philips.cdp.registration.settings.JanrainInitializer;
import com.philips.cdp.registration.ui.utils.Gender;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.ThreadUtils;
import com.philips.cdp.registration.update.UpdateUser;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pif.DataInterface.USR.listeners.UpdateUserDetailsHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateGender extends UpdateUserDetailsBase {

    public final static String USER_GENDER = "gender";

    private static final String TAG =  "UpdateGender";

    private Gender mGender;

    public UpdateGender(Context context) {
        super(context);
        mJanrainInitializer = new JanrainInitializer();
        mContext = context;
    }

    public void updateGender(final UpdateUserDetailsHandler
                                     updateUserDetailsHandler,
                             final Gender gender) {
        RLog.d(TAG,"updateGender : is called");
        mUpdateUserDetails = updateUserDetailsHandler;
        mGender = gender;
        if (isJanrainInitializeRequired()) {
            mJanrainInitializer.initializeJanrain(mContext);
            return;
        }
        performActualUpdate();
    }

    protected void performActualUpdate() {
        RLog.d(TAG,"performActualUpdate : is called");
        JSONObject userData = getCurrentUserAsJsonObject();
        mUpdatedUserdata = Jump.getSignedInUser();
        try {
            if (null != mUpdatedUserdata) {
                mUpdatedUserdata.put(USER_GENDER, mGender.toString());
                UpdateUser updateUser = new UpdateUser();
                updateUser.update(mUpdatedUserdata, userData, this);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            if (null != mUpdateUserDetails)
                ThreadUtils.postInMainThread(mContext,()->
                mUpdateUserDetails.
                        onUpdateFailedWithError(new Error(ErrorCodes.UPDATE_USER_DETAILS_ERROR, JanrainErrorEnum.getLocalizedError(mContext,ErrorCodes.UPDATE_USER_DETAILS_ERROR))));
        }
    }

    protected void performLocalUpdate() {
        RLog.d(TAG,"performLocalUpdate : is called");
        if (null != mUpdatedUserdata)
            try {
                mUpdatedUserdata.put(USER_GENDER, mGender.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        mUpdatedUserdata.saveToDisk(mContext);
    }
}
