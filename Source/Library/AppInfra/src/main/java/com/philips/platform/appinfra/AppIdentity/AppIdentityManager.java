/* Copyright (c) Koninklijke Philips N.V. 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.appinfra.appidentity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.R;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


/**
 * The type App identity manager.
 */
public class AppIdentityManager implements AppIdentityInterface {

    AppInfra mAppInfra;
    Context context;

    public String mAppName;
    public String mAppVersion;
    public String mServiceDiscoveryEnvironment;
    public String mLocalizedAppName;
    public String micrositeId;
    public String sector;
    public String mAppState;

    List<String> mSectorValues = Arrays.asList("b2b", "b2c", "b2b_Li", "b2b_HC");
    List<String> mServiceDiscoveryEnv = Arrays.asList("DEVELOPMENT", "TEST", "STAGING", "ACCEPTANCE", "PRODUCTION");
    List<String> mAppStateValues = Arrays.asList("DEVELOPMENT", "TEST", "STAGING", "ACCEPTANCE", "PRODUCTION");
    Set<String> set = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);


    @Override
    public String getAppName() {
        return mAppName;
    }


    @Override
    public String getAppVersion() {
        return mAppVersion;
    }

    @Override
    public AppState getAppState() {
        AppState mAppStateEnum = null;
        if (mAppState.equalsIgnoreCase("DEVELOPMENT")) {
            mAppStateEnum = AppState.DEVELOPMENT;
        }
        if (mAppState.equalsIgnoreCase("TEST")) {
            mAppStateEnum = AppState.TEST;
        }
        if (mAppState.equalsIgnoreCase("STAGING")) {
            mAppStateEnum = AppState.STAGING;
        }
        if (mAppState.equalsIgnoreCase("ACCEPTANCE")) {
            mAppStateEnum = AppState.ACCEPTANCE;
        }
        if (mAppState.equalsIgnoreCase("PRODUCTION")) {
            mAppStateEnum = AppState.PRODUCTION;
        }
        if (mAppStateEnum != null) {
            return mAppStateEnum;
        }

        return mAppStateEnum;
    }

    @Override
    public String getServiceDiscoveryEnvironment() {
        return mServiceDiscoveryEnvironment;
    }


    @Override
    public String getLocalizedAppName() {
        return mLocalizedAppName;
    }


    @Override
    public String getMicrositeId() {
        return micrositeId;
    }


    @Override
    public String getSector() {
        return sector;
    }

    public AppIdentityManager(AppInfra aAppInfra) {
        mAppInfra = aAppInfra;
        context = mAppInfra.getAppInfraContext();
        // Class shall not presume appInfra to be completely initialized at this point.
        // At any call after the constructor, appInfra can be presumed to be complete.

        // Method Loads the json data and can be access through getters
        loadJSONFromAsset();

    }

    // Refactored to support unit test
    protected String getJsonStringFromAsset() {
        String json = null;
        InputStream is = null;
        try {
            is = context.getAssets().open("AppIdentity.json");
            int size = is.available();
            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public String loadJSONFromAsset() {
        String json;
        try {
            json = getJsonStringFromAsset();
            if (json != null) {
                try {
                    JSONObject obj = new JSONObject(json);
                    validateAppIdentity(obj);


                    mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "micrositeId", "" + getMicrositeId());
                    mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "sector", "" + getSector());
                    mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "AppState", "" + getAppState());
                    mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "AppName", "" + getAppName());
                    mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "AppVersion", "" + getAppVersion());
                    mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "AppLocalizedNAme", "" + getLocalizedAppName());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    public void validateAppIdentity(JSONObject jsonObject) throws JSONException, InvalidArgumentException {
        micrositeId = jsonObject.getString("micrositeId");
        sector = jsonObject.getString("sector");
        mServiceDiscoveryEnvironment = jsonObject.getString("ServiceDiscoveryEnvironment");
        mAppState = jsonObject.getString("AppState");

        if (micrositeId != null && !micrositeId.isEmpty()) {
            if (!micrositeId.matches("[a-zA-Z0-9_.-]+")) {
                micrositeId = null;
                throw new InvalidArgumentException("micrositeId must not contain special charectors in appIdentityConfig json file");
            }
        } else {
            throw new InvalidArgumentException("micrositeId cannot be empty in appIdentityConfig  file ");
        }


        if (sector != null && !sector.isEmpty()) {
            set.addAll(mSectorValues);
            if (!set.contains(sector)) {
                throw new InvalidArgumentException("Sector in appIdentityConfig  file must match one of the following values" +
                        " \\n b2b,\\n b2c,\\n b2b_Li, \\n b2b_HC");
            }
        } else {
            throw new InvalidArgumentException("App Sector cannot be empty in appIdentityConfig json file");
        }

        if (mServiceDiscoveryEnvironment != null && !mServiceDiscoveryEnvironment.isEmpty()) {
            set.addAll(mServiceDiscoveryEnv);
            if (!set.contains(mServiceDiscoveryEnvironment)) {
                throw new InvalidArgumentException("servicediscoveryENV in appIdentityConfig  file must match " +
                        "one of the following values \n TEST,\n DEVELOPMENT,\n STAGING, \n ACCEPTANCE, \n PRODUCTION");
            }
        }

        if (mAppState != null && !mAppState.isEmpty()) {
            set.addAll(mAppStateValues);
            if (!set.contains(mAppState)) {
                throw new InvalidArgumentException("App State in appIdentityConfig  file must match" +
                        " one of the following values \\n TEST,\\n DEVELOPMENT,\\n STAGING, \\n ACCEPTANCE, \\n PRODUCTION");
            }

        }


        try {
            PackageInfo pInfo;
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            mAppName = context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();

            System.out.println("APPNAME " + " " + mAppName);
                        /* Vertical App should have this string defined for all supported language files
                        *  default <string name="localized_commercial_app_name">AppInfra DemoApp localized</string>
                        * */
            mLocalizedAppName = context.getResources().getString(R.string.localized_commercial_app_name);

            mAppVersion = String.valueOf(pInfo.versionName);
            if (mAppVersion != null && !mAppVersion.isEmpty()) {
                if (!mAppVersion.matches("[0-9]+\\.[0-9]+\\.[0-9]+([_-].*)]")) {
                    throw new InvalidArgumentException("AppVersion should be in proper format");
                }
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


    }

}
