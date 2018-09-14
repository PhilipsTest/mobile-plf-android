
/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.controller;

import android.content.Context;
import android.support.annotation.Nullable;

import com.janrain.android.Jump;
import com.janrain.android.capture.Capture;
import com.janrain.android.capture.Capture.InvalidApidChangeException;
import com.janrain.android.capture.CaptureApiError;
import com.janrain.android.capture.CaptureRecord;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.handlers.UpdateUserRecordHandler;
import com.philips.cdp.registration.settings.RegistrationSettings;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.ntputils.ServerTime;
import com.philips.ntputils.constants.ServerTimeConstants;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.timesync.TimeInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import javax.inject.Inject;

public class UpdateUserRecord implements UpdateUserRecordHandler {

    @Inject
    ServiceDiscoveryInterface serviceDiscoveryInterface;

    @Inject
    TimeInterface timeInterface;

    private String CONSUMER_TIMESTAMP = "timestamp";

    private String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private String CONSUMER_VISITED_MICROSITE_IDS = "visitedMicroSites";

    private String OLDER_THAN_AGE_LIMIT = "olderThanAgeLimit";

    private Context mContext;

    private String CONSUMER_ROLE = "role";

    private String CONSUMER_ROLES = "roles";

    private String CONSUMER_ROLE_ASSIGNED = "role_assigned";

    private String CONSUMER_COUNTRY = "country";


    private String CONSUMER_NAME = "consumer";

    private String CONSUMER_PREFERED_LANGUAGE = "preferredLanguage";

    private String CONSUMER_PRIMARY_ADDRESS = "primaryAddress";

    private final static String TAG = "UpdateUserRecord";

    public UpdateUserRecord(Context context) {
        RegistrationConfiguration.getInstance().getComponent().inject(this);
        mContext = context;
    }

    @Override
    public void updateUserRecordRegister() {

        if (Jump.getSignedInUser() != null) {
            serviceDiscoveryInterface.getHomeCountry(new ServiceDiscoveryInterface.OnGetHomeCountryListener() {
                @Override
                public void onSuccess(String s, SOURCE source) {
                    RLog.d(TAG, " ServiceDiscoveryInterface :onSuccess: Country Sucess :");
                    CaptureRecord updatedUser = Jump.getSignedInUser();
                    JSONObject originalUserInfo = getCurrentUserAsJsonObject();
                    String microSiteId = RegistrationConfiguration.getInstance().getMicrositeId();

                    try {
                        ServerTime.init(timeInterface);
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
                        primaryAddressObject.put(CONSUMER_COUNTRY,s);
                        RLog.e(TAG,"ServiceDiscoveryInterface :onSuccess  : "+s);
                        JSONArray primaryAddressArray = new JSONArray();
                        primaryAddressArray.put(primaryAddressObject);

                        updatedUser.put(CONSUMER_VISITED_MICROSITE_IDS, visitedMicroSitesArray);
                        updatedUser.put(CONSUMER_ROLES, rolesArray);
                        updatedUser.put(CONSUMER_PREFERED_LANGUAGE, Locale.getDefault().getLanguage());
                        RLog.e(TAG,"ServiceDiscoveryInterface :onSuccess : "+ Locale.getDefault().getLanguage());
                        updatedUser.put(CONSUMER_PRIMARY_ADDRESS, primaryAddressObject);
                        if (!(originalUserInfo.getBoolean(OLDER_THAN_AGE_LIMIT) && updatedUser.getBoolean(OLDER_THAN_AGE_LIMIT))) {
                            updatedUser.put(OLDER_THAN_AGE_LIMIT, true);
                        }

                        updateUserRecord(updatedUser, originalUserInfo);

                    } catch (JSONException e) {
                        RLog.e(TAG, "updateUserRecordRegister :On success JSON Exception" + e.getMessage());
                    }
                }
                @Override
                public void onError(ERRORVALUES errorvalues, String s) {
                    RLog.e(TAG, " Country Error :" + s);
                }
            });

        }
    }

    private void updateUserRecord(CaptureRecord user, final JSONObject originalUserInfo) {
        try {
            user.synchronize(new Capture.CaptureApiRequestCallback() {

                @Override
                public void onSuccess() {
                    Jump.saveToDisk(mContext);
                }

                @Override
                public void onFailure(CaptureApiError e) {
                    RLog.e(TAG, "updateUserRecord : onFailure : error " + e.raw_response);
                }
            }, originalUserInfo);

        } catch (InvalidApidChangeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateUserRecordLogin() {

        if (Jump.getSignedInUser() != null) {
            CaptureRecord updatedUser = Jump.getSignedInUser();
            JSONObject originalUserInfo = getCurrentUserAsJsonObject();
            String microSiteId = RegistrationConfiguration.getInstance().getMicrositeId();
            try {
                ServerTime.init(timeInterface);
                String currentDate = ServerTime.getCurrentUTCTimeWithFormat(ServerTimeConstants.DATE_FORMAT_FOR_JUMP);

                JSONObject visitedMicroSitesObject = new JSONObject();
                visitedMicroSitesObject.put(RegistrationSettings.MICROSITE_ID, microSiteId);
                visitedMicroSitesObject.put(CONSUMER_TIMESTAMP, currentDate);
                JSONArray visitedMicroSitesArray = (JSONArray) updatedUser.get(CONSUMER_VISITED_MICROSITE_IDS);
                RLog.d(TAG, "updateUserRecordLogin : Visited microsite ids = " + visitedMicroSitesArray);
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
                RLog.e(TAG, "updateUserRecordLogin: JSON Exception"+e.getMessage());
            }
        }
    }

    @Nullable
    private JSONObject getCurrentUserAsJsonObject() {
        JSONObject userData = null;
        try {
            userData = new JSONObject(Jump.getSignedInUser().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userData;
    }
}
