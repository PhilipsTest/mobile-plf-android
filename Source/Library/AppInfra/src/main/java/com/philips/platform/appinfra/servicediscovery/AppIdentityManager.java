/* Copyright (c) Koninklijke Philips N.V. 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.appinfra.servicediscovery;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 310238655 on 6/1/2016.
 */
public class AppIdentityManager implements AppIdentityInterface {

    AppInfra mAppInfra;
    Context context;

    public String mAppName;
    public String mAppVersion;
    public String mAppState;
    public String mAppLocalizedNAme;
    public String micrositeId;
    public String sector;


    public String getmAppName() {
        return mAppName;
    }

    public void setmAppName(String mAppName) {
        this.mAppName = mAppName;
    }

    public String getmAppVersion() {
        return mAppVersion;
    }

    public void setmAppVersion(String mAppVersion) {
        this.mAppVersion = mAppVersion;
    }

    public String getmAppState() {
        return mAppState;
    }

    public void setmAppState(String mAppState) {
        this.mAppState = mAppState;
    }

    public String getmAppLocalizedNAme() {
        return mAppLocalizedNAme;
    }

    public void setmAppLocalizedNAme(String mAppLocalizedNAme) {
        this.mAppLocalizedNAme = mAppLocalizedNAme;
    }

    public String getMicrositeId() {
        return micrositeId;
    }

    public void setMicrositeId(String micrositeId) {
        this.micrositeId = micrositeId;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public AppIdentityManager(AppInfra aAppInfra) {
        mAppInfra = aAppInfra;
        context = mAppInfra.getAppInfraContext();
        // Class shall not presume appInfra to be completely initialized at this point.
        // At any call after the constructor, appInfra can be presumed to be complete.
//        loadJSONFromAsset();
    }

       public String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = context.getAssets().open("AppIdentity.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");
            if(json != null){
                try {
                    JSONObject obj = new JSONObject(json);
                     micrositeId= obj.getString("micrositeId");
                     sector = obj.getString("sector");
                     mAppState= obj.getString("AppState");
                    if(micrositeId!=null)
                        setMicrositeId(micrositeId);
                    if(sector!=null)
                        setSector(sector);
                    if(mAppState!=null)
                        setmAppState(mAppState);
                    PackageInfo pInfo = null;
                    try {
                        pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                         mAppName = pInfo.versionName;
                        if(mAppName!=null)
                            setmAppName(mAppName);
                        mAppVersion = String.valueOf(pInfo.versionCode);
                        if(mAppVersion!=null)
                            setmAppVersion(mAppVersion);

                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }


                    Log.i("Obj tag1", ""+micrositeId);
                    Log.i("Obj tag2", ""+sector);
                    Log.i("Obj tag1", ""+mAppState);
                    Log.i("Obj tag2", ""+mAppName);
                    Log.i("Obj tag1", ""+mAppVersion);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }




        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    @Override
    public void configureAppIdentity(String configFilePath) {

    }
}
