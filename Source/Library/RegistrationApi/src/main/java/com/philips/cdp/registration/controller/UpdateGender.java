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
import com.philips.cdp.registration.handlers.UpdateUserDetailsHandler;
import com.philips.cdp.registration.settings.JanrainInitializer;
import com.philips.cdp.registration.ui.utils.Gender;
import com.philips.cdp.registration.ui.utils.ThreadUtils;
import com.philips.cdp.registration.update.UpdateUser;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateGender extends UpdateUserDetailsBase {

    public final static String USER_GENDER = "gender";

    private Gender mGender;

    public UpdateGender(Context context) {
        mJanrainInitializer = new JanrainInitializer();
        mContext = context;
    }

    public void updateGender(final UpdateUserDetailsHandler
                                     updateUserDetailsHandler,
                             final Gender gender) {
        mUpdateUserDetails = updateUserDetailsHandler;
        mGender = gender;
        if (isJanrainInitializeRequired()) {
            mJanrainInitializer.initializeJanrain(mContext, this);
            return;
        }
        performActualUpdate();
    }

    protected void performActualUpdate() {
        JSONObject userData = getCurrentUserAsJsonObject();
        mUpdatedUserdata = Jump.getSignedInUser();
        try {
            if (null != mUpdatedUserdata) {
                mUpdatedUserdata.put(USER_GENDER, mGender.toString());

                JSONObject russianConsent = (JSONObject) mUpdatedUserdata.get("janrain");
                if(russianConsent!=null ){
                System.out.println("data"+ mUpdatedUserdata.get("janrain"));
                    JSONObject controlFields = (JSONObject)   russianConsent.get("controlFields");
                    System.out.println("data"+ controlFields);
                    if(controlFields!=null){
                        System.out.println("data"+ controlFields.get("one"));
                        controlFields.put("one","true222222");
                    }
                    russianConsent.put("controlFields",controlFields);
                    mUpdatedUserdata.put("janrain",russianConsent);
                    System.out.println("data"+ mUpdatedUserdata.get("janrain"));
                }

                JSONObject  one = new JSONObject();
                one.put("one","true");

                JSONObject  control = new JSONObject();
                control.put("controlFields",one);

                JSONObject  janrain = new JSONObject();
                janrain.put("janrain",control);
                System.out.println("data"+ janrain);
                mUpdatedUserdata.put("janrain",new JSONObject(mUpdatedUserdata.get("janrain").toString()));
                UpdateUser updateUser = new UpdateUser();
                updateUser.update(mUpdatedUserdata, userData, this);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            if (null != mUpdateUserDetails)
                ThreadUtils.postInMainThread(mContext,()->
                mUpdateUserDetails.
                        onUpdateFailedWithError(-1));
        }
    }

    protected void performLocalUpdate() {
        if (null != mUpdatedUserdata)
            try {
                mUpdatedUserdata.put(USER_GENDER, mGender.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        mUpdatedUserdata.saveToDisk(mContext);
    }
}
