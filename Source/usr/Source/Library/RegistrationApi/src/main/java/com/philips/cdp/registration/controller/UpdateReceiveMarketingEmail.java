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
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.ServerTime;
import com.philips.cdp.registration.ui.utils.ThreadUtils;
import com.philips.cdp.registration.update.UpdateUser;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pif.DataInterface.USR.listeners.UpdateUserDetailsHandler;

import org.json.JSONException;
import org.json.JSONObject;


public class UpdateReceiveMarketingEmail extends UpdateUserDetailsBase {

    private final static String USER_RECEIVE_MARKETING_EMAIL = "receiveMarketingEmail";
    private final static String LOCALE = "locale";
    private final static String TIMESTAMP = "timestamp";
    private final static String MARKETING_OPT_IN = "marketingOptIn";
    private String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private boolean mReceiveMarketingEmail;

    private static final String TAG = "UpdateReceiveMarketingEmail";

    public UpdateReceiveMarketingEmail(Context context) {
        super(context);
        mJanrainInitializer = new JanrainInitializer();
        mContext = context;
    }

    public void updateMarketingEmailStatus(final UpdateUserDetailsHandler
                                                   updateUserDetailsHandler,
                                           final boolean receiveMarketingEmail) {
        RLog. i(TAG, "updateMarketingEmailStatus : is called");
        mUpdateUserDetails = updateUserDetailsHandler;
        mReceiveMarketingEmail = receiveMarketingEmail;
        if (isJanrainInitializeRequired()) {
            mJanrainInitializer.initializeJanrain(mContext);
            return;
        }
        performActualUpdate();
    }

    @Override
    protected void performActualUpdate() {
        JSONObject userData = getCurrentUserAsJsonObject();
        mUpdatedUserdata = Jump.getSignedInUser();
        try {
            if (null != mUpdatedUserdata) {
                mUpdatedUserdata.put(USER_RECEIVE_MARKETING_EMAIL, mReceiveMarketingEmail);

                JSONObject marketingOptIn = new JSONObject();
                marketingOptIn.put(LOCALE, RegistrationHelper.getInstance().getLocale().toString());
                marketingOptIn.put(TIMESTAMP, ServerTime.getCurrentUTCTimeWithFormat(DATE_FORMAT));
                mUpdatedUserdata.put(MARKETING_OPT_IN, marketingOptIn);
                UpdateUser updateUser = new UpdateUser();
                updateUser.update(mUpdatedUserdata, userData, this);
                RLog.d(TAG, "performActualUpdate : updateUser.update is called");
            }
        } catch (JSONException e) {
            if (null != mUpdateUserDetails)
                ThreadUtils.postInMainThread(mContext, () ->
                        mUpdateUserDetails.
                                onUpdateFailedWithError(new Error(ErrorCodes.UPDATE_USER_DETAILS_ERROR, JanrainErrorEnum.getLocalizedError(mContext,ErrorCodes.UPDATE_USER_DETAILS_ERROR))));
            RLog.e(TAG, "performActualUpdate: JSONException" + e.getMessage());
        }
    }

    @Override
    protected void performLocalUpdate() {
        if (null != mUpdatedUserdata) {
            try {
                mUpdatedUserdata.put(USER_RECEIVE_MARKETING_EMAIL, mReceiveMarketingEmail);
                JSONObject marketingOptIn = new JSONObject();
                marketingOptIn.put(LOCALE, RegistrationHelper.getInstance().getLocale().toString());
                marketingOptIn.put(TIMESTAMP, ServerTime.getCurrentUTCTimeWithFormat(DATE_FORMAT));

                mUpdatedUserdata.put(MARKETING_OPT_IN, marketingOptIn);
                RLog.d(TAG, "performLocalUpdate : saveToDisk");
                mUpdatedUserdata.saveToDisk(mContext);
            } catch (JSONException e) {
                RLog.e(TAG,"performLocalUpdate: JJSONException"+ e.getMessage());
            }
        }
    }
}
