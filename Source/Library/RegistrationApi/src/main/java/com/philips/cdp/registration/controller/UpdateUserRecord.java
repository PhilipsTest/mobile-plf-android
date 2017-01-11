
/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.controller;

import android.content.Context;
import android.content.SharedPreferences;

import com.janrain.android.Jump;
import com.janrain.android.capture.Capture;
import com.janrain.android.capture.Capture.InvalidApidChangeException;
import com.janrain.android.capture.CaptureApiError;
import com.janrain.android.capture.CaptureRecord;
import com.philips.cdp.registration.handlers.UpdateUserRecordHandler;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.settings.RegistrationSettings;
import com.philips.cdp.registration.settings.UserRegistrationInitializer;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.ntputils.ServerTime;
import com.philips.ntputils.constants.ServerTimeConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UpdateUserRecord implements UpdateUserRecordHandler {

    private String CONSUMER_TIMESTAMP = "timestamp";

    private String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private String CONSUMER_VISITED_MICROSITE_IDS = "visitedMicroSites";

    private String OLDER_THAN_AGE_LIMIT = "olderThanAgeLimit";

    private Context mContext;

    private String CONSUMER_ROLE = "role";

    private String CONSUMER_ROLES = "roles";

    private String CONSUMER_ROLE_ASSIGNED = "role_assigned";

    private String CONSUMER_COUNTRY = "country";

    private String CONSUMER_ADDRESS1 = "address1";

    private String CONSUMER_ADDRESS2 = "address2";

    private String CONSUMER_ADDRESS3 = "address3";

    private String CONSUMER_CITY = "city";

    private String CONSUMER_COMPANY = "company";

    private String CONSUMER_PHONE_NUMBER = "dayTimePhoneNumber";

    private String CONSUMER_HOUSE_NUMBER = "houseNumber";

    private String CONSUMER_MOBILE = "mobile";

    private String CONSUMER_PHONE = "phone";

    private String CONSUMER_STATE = "state";

    private String CONSUMER_ZIP = "zip";

    private String CONSUMER_NAME = "consumer";

    private String CONSUMER_ZIP_PLUS = "zipPlus4";

    private String CONSUMER_PREFERED_LANGUAGE = "preferredLanguage";

    private String CONSUMER_PRIMARY_ADDRESS = "primaryAddress";

    private String LOG_TAG = "RegisterSocial";

    public UpdateUserRecord(Context context) {
        mContext = context;
    }

    @Override
    public void updateUserRecordRegister() {

        if (Jump.getSignedInUser() != null) {
            CaptureRecord updatedUser = CaptureRecord.loadFromDisk(mContext);
            JSONObject originalUserInfo = CaptureRecord.loadFromDisk(mContext);
            SharedPreferences myPrefs = mContext.getSharedPreferences(
                    RegistrationSettings.REGISTRATION_API_PREFERENCE, 0);
            String microSiteId = myPrefs.getString(RegistrationSettings.MICROSITE_ID, null);

            RegistrationHelper userSettings = RegistrationHelper.getInstance();
            // visitedMicroSites
            try {

                ServerTime.init(RegistrationHelper.getInstance().getAppInfraInstance().getTime());
                String currentDate = ServerTime.getCurrentUTCTimeWithFormat(DATE_FORMAT);
                JSONObject visitedMicroSitesObject = new JSONObject();
                visitedMicroSitesObject.put(RegistrationSettings.MICROSITE_ID, microSiteId);
                visitedMicroSitesObject.put(CONSUMER_TIMESTAMP, currentDate);
                JSONArray visitedMicroSitesArray = new JSONArray();
                visitedMicroSitesArray.put(visitedMicroSitesObject);
                // roles
                JSONObject rolesObject = new JSONObject();
                rolesObject.put(CONSUMER_ROLE, CONSUMER_NAME);
                rolesObject.put(CONSUMER_ROLE_ASSIGNED, currentDate);
                JSONArray rolesArray = new JSONArray();
                rolesArray.put(rolesObject);

                // PrimaryAddress
                JSONObject primaryAddressObject = new JSONObject();

                primaryAddressObject.put(CONSUMER_COUNTRY, UserRegistrationInitializer.getInstance().getRegistrationSettings()
                        .getPreferredCountryCode());
                JSONArray primaryAddressArray = new JSONArray();
                primaryAddressArray.put(primaryAddressObject);

                updatedUser.put(CONSUMER_VISITED_MICROSITE_IDS, visitedMicroSitesArray);
                updatedUser.put(CONSUMER_ROLES, rolesArray);
                updatedUser.put(CONSUMER_PREFERED_LANGUAGE, UserRegistrationInitializer.getInstance().getRegistrationSettings()
                        .getPreferredLangCode());
                updatedUser.put(CONSUMER_PRIMARY_ADDRESS, primaryAddressObject);
                if (!(originalUserInfo.getBoolean(OLDER_THAN_AGE_LIMIT) && updatedUser.getBoolean(OLDER_THAN_AGE_LIMIT))) {
                    updatedUser.put(OLDER_THAN_AGE_LIMIT, true);
                }

                updateUserRecord(updatedUser, originalUserInfo);

            } catch (JSONException e) {
                RLog.e(LOG_TAG, "On success, Caught JSON Exception");
            }
        }
    }

    private void updateUserRecord(CaptureRecord user, final JSONObject originalUserInfo) {
        RLog.d(LOG_TAG, "******** NEW updateUserRecord :"+user.toString());
        try {
            user.synchronize(new Capture.CaptureApiRequestCallback() {

                @Override
                public void onSuccess() {
                    Jump.saveToDisk(mContext);
                }

                @Override
                public void onFailure(CaptureApiError e) {
                }
            }, originalUserInfo);

        } catch (InvalidApidChangeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateUserRecordLogin() {

        if (Jump.getSignedInUser() != null) {
            CaptureRecord updatedUser = CaptureRecord.loadFromDisk(mContext);
            JSONObject originalUserInfo = CaptureRecord.loadFromDisk(mContext);
            SharedPreferences myPrefs = mContext.getSharedPreferences(
                    RegistrationSettings.REGISTRATION_API_PREFERENCE, 0);
            String microSiteId = myPrefs.getString(RegistrationSettings.MICROSITE_ID, null);
            try {
                ServerTime.init(RegistrationHelper.getInstance().getAppInfraInstance().getTime());
                String currentDate = ServerTime.getCurrentUTCTimeWithFormat(ServerTimeConstants.DATE_FORMAT_FOR_JUMP);

                JSONObject visitedMicroSitesObject = new JSONObject();
                visitedMicroSitesObject.put(RegistrationSettings.MICROSITE_ID, microSiteId);
                visitedMicroSitesObject.put(CONSUMER_TIMESTAMP, currentDate);
                JSONArray visitedMicroSitesArray = (JSONArray) updatedUser.get(CONSUMER_VISITED_MICROSITE_IDS);
                RLog.d(LOG_TAG, "Visited microsite ids = " + visitedMicroSitesArray);
                if (null == visitedMicroSitesArray) {
                    visitedMicroSitesArray = new JSONArray();
                }
                visitedMicroSitesArray.put(visitedMicroSitesObject);
                updatedUser.put(CONSUMER_VISITED_MICROSITE_IDS, visitedMicroSitesArray);

                if (!(originalUserInfo.getBoolean(OLDER_THAN_AGE_LIMIT) && updatedUser.getBoolean(OLDER_THAN_AGE_LIMIT))) {
                    updatedUser.put(OLDER_THAN_AGE_LIMIT, true);
                }
                updateUserRecord(updatedUser, originalUserInfo);
            } catch (JSONException e) {
                RLog.e(LOG_TAG, "On success, Caught JSON Exception");
            }
        }
    }
}
