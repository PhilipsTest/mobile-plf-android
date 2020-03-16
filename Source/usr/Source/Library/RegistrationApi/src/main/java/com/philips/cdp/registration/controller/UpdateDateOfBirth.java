/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.controller;

import android.content.Context;
import androidx.annotation.NonNull;

import com.janrain.android.Jump;
import com.philips.cdp.registration.errors.ErrorCodes;
import com.philips.cdp.registration.errors.JanrainErrorEnum;
import com.philips.cdp.registration.settings.JanrainInitializer;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.ServerTime;
import com.philips.cdp.registration.ui.utils.ThreadUtils;
import com.philips.cdp.registration.update.UpdateUser;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pif.DataInterface.USR.listeners.UpdateUserDetailsHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UpdateDateOfBirth extends UpdateUserDetailsBase {

    public final static String USER_DATE_OF_BIRTH = "birthday";

    public static final String DATE_FORMAT_FOR_DOB = "yyyy-MM-dd";

    private String mBirthDate;

    private final String TAG = "UpdateDateOfBirth";

    public UpdateDateOfBirth(Context context) {
        super(context);
        mJanrainInitializer = new JanrainInitializer();
        mContext = context;
    }

    public void updateDateOfBirth(@NonNull final UpdateUserDetailsHandler
                                          updateUserDetailsHandler,
                                  @NonNull final Date date) {
        RLog.d(TAG, "updateDateOfBirth is called");
        mUpdateUserDetails = updateUserDetailsHandler;
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_FOR_DOB, Locale.ROOT);
        mBirthDate = sdf.format(date);
        try {
            Date firstDate = sdf.parse(mBirthDate);
            Date secondDate = sdf.parse(ServerTime.getCurrentTime());
            if (firstDate.compareTo(secondDate) > 0) {
                ThreadUtils.postInMainThread(mContext, () ->
                        mUpdateUserDetails.onUpdateFailedWithError(new Error(ErrorCodes.UPDATE_USER_DETAILS_ERROR, JanrainErrorEnum.getLocalizedError(mContext,ErrorCodes.UPDATE_USER_DETAILS_ERROR))));
                return;
            }
            if (isJanrainInitializeRequired()) {
                mJanrainInitializer.initializeJanrain(mContext);
                return;
            }
            performActualUpdate();
        } catch (ParseException e) {
            ThreadUtils.postInMainThread(mContext, () ->
                    mUpdateUserDetails.onUpdateFailedWithError(new Error(ErrorCodes.UPDATE_USER_DETAILS_ERROR,JanrainErrorEnum.getLocalizedError(mContext,ErrorCodes.UPDATE_USER_DETAILS_ERROR))));
        }
    }


    protected void performActualUpdate() {
        RLog.d(TAG, "performActualUpdate is called");
        JSONObject userData = getCurrentUserAsJsonObject();
        mUpdatedUserdata = Jump.getSignedInUser();
        try {
            if (null != mUpdatedUserdata) {
                mUpdatedUserdata.put(USER_DATE_OF_BIRTH, mBirthDate);
                UpdateUser updateUser = new UpdateUser();
                updateUser.update(mUpdatedUserdata, userData, this);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            if (null != mUpdateUserDetails)
                ThreadUtils.postInMainThread(mContext, () ->
                        mUpdateUserDetails.
                                onUpdateFailedWithError(new Error(ErrorCodes.UPDATE_USER_DETAILS_ERROR,JanrainErrorEnum.getLocalizedError(mContext,ErrorCodes.UPDATE_USER_DETAILS_ERROR))));
        }
    }

    protected void performLocalUpdate() {
        RLog.d(TAG, "performLocalUpdate is called");
        if (null != mUpdatedUserdata)
            try {
                mUpdatedUserdata.put(USER_DATE_OF_BIRTH, mBirthDate);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        mUpdatedUserdata.saveToDisk(mContext);
    }
}
