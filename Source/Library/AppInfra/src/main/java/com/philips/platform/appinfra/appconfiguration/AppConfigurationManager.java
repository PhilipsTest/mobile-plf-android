/* Copyright (c) Koninklijke Philips N.V. 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.appinfra.appconfiguration;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.rest.request.JsonObjectRequest;
import com.philips.platform.appinfra.securestorage.SecureStorageInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by 310238114 on 7/25/2016.
 */
public class AppConfigurationManager implements AppConfigurationInterface {

    private final AppInfra mAppInfra;
    private final Context mContext;
    private JSONObject dynamicConfigJsonCache;
    private JSONObject cloudConfigJsonCache;
    private JSONObject staticConfigJsonCache;
    private static final String  APPCONFIG_SECURE_STORAGE_KEY = "ail.app_config";
    private static final String APPCONFIG_SECURE_STORAGE_KEY_NEW = "ailNew.app_config";
    private static final String CLOUD_APP_CONFIG_FILE = "CloudConfig";
    private static final String CLOUD_APP_CONFIG_JSON = "cloudConfigJson";
    private static final String CLOUD_APP_CONFIG_URL = "cloudConfigUrl";
    private JSONObject result = null;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mPrefEditor;
    private SecureStorageInterface mSecureStorageInterface;

    public AppConfigurationManager(AppInfra appInfra) {
        mAppInfra = appInfra;
        mContext = appInfra.getAppInfraContext();
        mSharedPreferences = getCloudConfigSharedPreferences();
        mPrefEditor = mSharedPreferences.edit();
    }

    protected JSONObject getMasterConfigFromApp() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final InputStream mInputStream = mContext.getAssets().open("AppConfig.json");
                    final BufferedReader r = new BufferedReader(new InputStreamReader(mInputStream));
                    final StringBuilder total = new StringBuilder();
                    String line;
                         while ((line = r.readLine()) != null) {
                        total.append(line).append('\n');
                    }
                    result = new JSONObject(total.toString());
                    result = makeKeyUppercase(result); // converting all Group and child key Uppercase

                    mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.VERBOSE, "Json",
                            result.toString());

                } catch (Exception e) {
                    mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, "AppConfiguration exception",
                            Log.getStackTraceString(e));
                }
            }
        }).start();

        return result;
    }


    private JSONObject getDynamicConfigJsonCache() {
        if (null == dynamicConfigJsonCache) {
            dynamicConfigJsonCache = getDynamicJSONFromDevice();
        }
        return dynamicConfigJsonCache;

    }

    private JSONObject getStaticConfigJsonCache() {
        if (staticConfigJsonCache == null) {
            staticConfigJsonCache = getMasterConfigFromApp();
        }
        return staticConfigJsonCache;
    }


    private JSONObject getDynamicJSONFromDevice() {
        mSecureStorageInterface = mAppInfra.getSecureStorage();
        JSONObject mJsonObject = null;
        final SecureStorageInterface.SecureStorageError mSecureStorageError = new SecureStorageInterface.SecureStorageError();
        final String jsonString = mSecureStorageInterface.fetchValueForKey(APPCONFIG_SECURE_STORAGE_KEY_NEW, mSecureStorageError);
        if (null != jsonString ) {
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG, "uAPP_CONFIG", jsonString);
            try {
                mJsonObject = new JSONObject(jsonString);
                mJsonObject = makeKeyUppercase(mJsonObject); // converting all Group and child key Uppercase
            } catch (Exception e) {
                mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, "AppConfiguration exception",
                        Log.getStackTraceString(e));
            }
        }
        return mJsonObject;
    }

    private JSONObject getCloudConfigJsonCache() {
        if (cloudConfigJsonCache == null) {
            cloudConfigJsonCache = getCloudJSONFromDevice();
        }
        return cloudConfigJsonCache;
    }

    private JSONObject getCloudJSONFromDevice() {
        JSONObject cloudConfigJsonObj = null;
        if (null != mSharedPreferences && mSharedPreferences.contains(CLOUD_APP_CONFIG_JSON)) {
            final String savedCloudConfigJson = mSharedPreferences.getString(CLOUD_APP_CONFIG_JSON, null);
            if (null != savedCloudConfigJson) {
                try {
                    cloudConfigJsonObj = new JSONObject(savedCloudConfigJson);
                } catch (JSONException e) {
                    mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, "AppConfiguration exception",
                            Log.getStackTraceString(e));
                } catch (Exception e) {
                    mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, "AppConfiguration exception",
                            Log.getStackTraceString(e));
                }
            }
        }
        return cloudConfigJsonObj;
    }

    @Override
    public Object getPropertyForKey(String key, String group, AppConfigurationError configError)
            throws IllegalArgumentException {
        Object object = null;
        if (null == group || null == key || group.isEmpty() || key.isEmpty() ||
                !group.matches("[a-zA-Z0-9_.-]+") || !key.matches("[a-zA-Z0-9_.-]+")) {
            configError.setErrorCode(AppConfigurationError.AppConfigErrorEnum.InvalidKey);
            throw new IllegalArgumentException("Invalid Argument Exception");
        } else {
            try {
                object = getKey(key, group, configError, getDynamicConfigJsonCache());  // Level 1 search in dynamic config
                if (configError.getErrorCode() == AppConfigurationError.AppConfigErrorEnum.NoDataFoundForKey || // dynamic config does not exist
                        configError.getErrorCode() == AppConfigurationError.AppConfigErrorEnum.GroupNotExists || // Group in dynamic config does not exist
                        configError.getErrorCode() == AppConfigurationError.AppConfigErrorEnum.KeyNotExists) {   // key in dynamic config does not exist
                    configError.setErrorCode(null);// reset error code to null
                    object = getKey(key, group, configError, getCloudConfigJsonCache()); // Level 2 search in cloud config
                    if (configError.getErrorCode() == AppConfigurationError.AppConfigErrorEnum.NoDataFoundForKey || // cloud config does not exist
                            configError.getErrorCode() == AppConfigurationError.AppConfigErrorEnum.GroupNotExists || // Group in cloud config does not exist
                            configError.getErrorCode() == AppConfigurationError.AppConfigErrorEnum.KeyNotExists) {   // key in cloud config does not exist
                        configError.setErrorCode(null);// reset error code to null
                        object = getKey(key, group, configError, getStaticConfigJsonCache()); // Level 3 search in static config
                        if (configError.getErrorCode() == AppConfigurationError.AppConfigErrorEnum.NoError) { //if key is found in cloud config
                            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "uAppConfig", "key found in static config");
                        }
                    } else {
                        if (configError.getErrorCode() == AppConfigurationError.AppConfigErrorEnum.NoError) { //if key is found in cloud config
                            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "uAppConfig", "key found in cloud config");
                        }
                    }
                } else {
                    if (configError.getErrorCode() == AppConfigurationError.AppConfigErrorEnum.NoError) { //if key is found in dynamic config
                        mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "uAppConfig", "key found in dynamic config");
                    }
                }
            } catch (Exception e) {
                mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, "AppConfiguration exception",
                        Log.getStackTraceString(e));
            }
        }
        return object;
    }

    @Override
    public boolean setPropertyForKey(String key, String group, Object object, AppConfigurationError
            configError) throws IllegalArgumentException {
        boolean setOperation = false;
        if (null == key || null == group || group.isEmpty() || !group.matches("[a-zA-Z0-9_.-]+") ||
                !key.matches("[a-zA-Z0-9_.-]+")) {
            configError.setErrorCode(AppConfigurationError.AppConfigErrorEnum.InvalidKey);
            throw new IllegalArgumentException("Invalid Argument Exception");
        } else {
            if (null == getDynamicConfigJsonCache()) {
                dynamicConfigJsonCache = new JSONObject();
            }
            key = key.toUpperCase();
            group = group.toUpperCase();
            try {
                final boolean isCocoPresent = dynamicConfigJsonCache.has(group);
                JSONObject cocoJSONobject;
                if (!isCocoPresent) { // if request coco  does not exist
                    // configError.setErrorCode(ConfigError.ConfigErrorEnum.GroupNotExists);
                    cocoJSONobject = new JSONObject();
                    dynamicConfigJsonCache.put(group, cocoJSONobject);
                } else {
                    cocoJSONobject = dynamicConfigJsonCache.optJSONObject(group);
                }
                if (null == cocoJSONobject) { // invalid Coco JSON
                    configError.setErrorCode(AppConfigurationError.AppConfigErrorEnum.FatalError);
                } else {
                    // boolean isKeyPresent = cocoJSONobject.has(key);
                    if (object instanceof ArrayList) {

                         if (((ArrayList) object).get(0) instanceof Integer || ((ArrayList) object).get(0) instanceof String) {

                            final JSONArray jsonArray = new JSONArray(((ArrayList) object).toArray());
                            cocoJSONobject.put(key, jsonArray);

                        } else {
                            throw new IllegalArgumentException("Invalid Argument Exception");
                        }
                    } else if (object instanceof HashMap) { // if object is MAP
                        final Map<?, ?> map = (Map) object;
                        final Set<?> keyset = map.keySet();
                        final Iterator<?> keyItr = keyset.iterator();
                        final Object objectKey = keyItr.next();
                        final Object value = map.get(objectKey); // value for key:objectKey
                        if (null == value) {
                            throw new IllegalArgumentException("Invalid Argument Exception");
                        } else {

                            if (objectKey instanceof String && (value instanceof String || value instanceof Integer)) { // if keys are String and value are Integer OR String
                                    final JSONObject jsonObject = new JSONObject(object.toString());
                                    cocoJSONobject.put(key, jsonObject);
                            } else {
                                throw new IllegalArgumentException("Invalid Argument Exception");
                            }
                        }
                    } else if (object instanceof Integer || object instanceof String || null == object) {

                        cocoJSONobject.put(key, object);
                    } else {
                        throw new IllegalArgumentException("Invalid Argument Exception");
                    }
                    final SecureStorageInterface.SecureStorageError mSecureStorageError = new SecureStorageInterface.SecureStorageError();
                    mSecureStorageInterface.storeValueForKey(APPCONFIG_SECURE_STORAGE_KEY_NEW, dynamicConfigJsonCache.toString(), mSecureStorageError);
                    if (null == mSecureStorageError.getErrorCode()) {
                        setOperation = true;
                    } else {
                        configError.setErrorCode(AppConfigurationError.AppConfigErrorEnum.SecureStorageError);
                    }
                }
            } catch (Exception e) {
                mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, "AppConfiguration exception",
                        Log.getStackTraceString(e));
                setOperation = false;
            }
        }
        return setOperation;
    }


    @Override
    public Object getDefaultPropertyForKey(String key, String group, AppConfigurationError configError) throws IllegalArgumentException {

        Object object = null;
        if (null == group || null == key || group.isEmpty() || key.isEmpty()
                || !group.matches("[a-zA-Z0-9_.-]+") || !key.matches("[a-zA-Z0-9_.-]+")) {
            configError.setErrorCode(AppConfigurationError.AppConfigErrorEnum.InvalidKey);
            throw new IllegalArgumentException("Invalid Argument Exception");
        } else {
            //dynamicConfigJsonCache is initialized//
            getStaticConfigJsonCache();
            try {
                object = getKey(key, group, configError, staticConfigJsonCache);
            } catch (Exception e) {
                mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, "AppConfiguration exception",
                        Log.getStackTraceString(e));
            }
        }
        return object;
    }

    private Object getKey(String key, String group, AppConfigurationError configError, JSONObject jsonObject) {
        key = key.toUpperCase();
        group = group.toUpperCase();
        Object object = null;
        if (null == jsonObject) {
            configError.setErrorCode(AppConfigurationError.AppConfigErrorEnum.NoDataFoundForKey);
        } else {
            final boolean isCocoPresent = jsonObject.has(group);
            if (!isCocoPresent) { // if request coco does not exist
                configError.setErrorCode(AppConfigurationError.AppConfigErrorEnum.GroupNotExists);
            } else {
                final JSONObject cocoJSONobject = jsonObject.optJSONObject(group);
                if (null == cocoJSONobject) { // invalid Coco JSON
                    configError.setErrorCode(AppConfigurationError.AppConfigErrorEnum.FatalError);
                } else {
                    boolean isKeyPresent = cocoJSONobject.has(key);
                    if (!isKeyPresent) { // if key is not found inside coco
                        configError.setErrorCode(AppConfigurationError.AppConfigErrorEnum.KeyNotExists);
                    } else {
                        object = cocoJSONobject.opt(key); // Returns the value mapped by name, or null if no such mapping exists
                        if (null != object) {
                            //  KEY FOUND SUCCESS
                            configError.setErrorCode(AppConfigurationError.AppConfigErrorEnum.NoError);
                            if (cocoJSONobject.opt(key) instanceof JSONArray) {
                                final JSONArray jsonArray = cocoJSONobject.optJSONArray(key);
                                final List<Object> list = new ArrayList<Object>();
                                for (int iCount = 0; iCount < jsonArray.length(); iCount++) {
                                    list.add(jsonArray.opt(iCount));
                                }
                                object = list;
                            } else if (cocoJSONobject.opt(key) instanceof JSONObject) {
                                try {
                                    object = jsonToMap(cocoJSONobject.opt(key));
                                } catch (JSONException e) {
                                    mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, "AppConfiguration exception",
                                            Log.getStackTraceString(e));
                                }
                            }
                        }
                    }
                }
            }
        }
        return object;
    }

    private Map<String, ?> jsonToMap(Object JSON) throws JSONException {
        final HashMap<String, Object> map = new HashMap<String, Object>();
        final JSONObject jObject = new JSONObject(JSON.toString());
        final Iterator<?> keys = jObject.keys();

        while (keys.hasNext()) {
            final String key = (String) keys.next();
            final Object value = jObject.get(key);
            map.put(key, value);
        }
        return map;
    }

    private JSONObject makeKeyUppercase(JSONObject json) {
        final JSONObject newJsonGroup = new JSONObject();
        final Iterator<String> iteratorGroup = json.keys();
        while (iteratorGroup.hasNext()) {
            final String keyGroup = iteratorGroup.next();
            try {
                final JSONObject objectGroup = json.optJSONObject(keyGroup);
                final JSONObject newJsonChildObject = new JSONObject();
                final Iterator<String> iteratorKey = objectGroup.keys();
                while (iteratorKey.hasNext()) {
                    final String key = iteratorKey.next();
                    try {
                        final Object objectKey = objectGroup.opt(key);
                        newJsonChildObject.put(key.toUpperCase(), objectKey);
                    } catch (JSONException e) {
                        // Something went wrong!
                        throw new RuntimeException(e);
                    }

                }
                newJsonGroup.put(keyGroup.toUpperCase(), newJsonChildObject);

            } catch (JSONException e) {
                // Something went wrong!
                throw new RuntimeException(e);
            }
        }
        return newJsonGroup;
    }

    @Override
    public void refreshCloudConfig(final OnRefreshListener onRefreshListener) {
        final AppConfigurationError mAppConfigError = new AppConfigurationError();
        final String cloudServiceId = (String) getPropertyForKey("appconfig.cloudServiceId", "APPINFRA", mAppConfigError);
        final ServiceDiscoveryInterface serviceDiscoveryInterface = mAppInfra.getServiceDiscovery();
        serviceDiscoveryInterface.getServiceUrlWithCountryPreference(cloudServiceId, new ServiceDiscoveryInterface.OnGetServiceUrlListener() {
            @Override
            public void onSuccess(URL url) {
                if (null != mSharedPreferences && mSharedPreferences.contains(CLOUD_APP_CONFIG_URL)) {
                    final String savedURL = mSharedPreferences.getString(CLOUD_APP_CONFIG_URL, null);
                    if (url.toString().trim().equalsIgnoreCase(savedURL)) { // cloud config url has not changed
                        onRefreshListener.onSuccess(OnRefreshListener.REFRESH_RESULT.NO_REFRESH_REQUIRED);
                    } else { // cloud config url has  changed
                        clearCloudConfigFile(); // clear old cloud config data
                        fetchCloudConfig(url.toString(), onRefreshListener);
                    }
                } else {
                    fetchCloudConfig(url.toString(), onRefreshListener);
                }

            }

            @Override
            public void onError(ERRORVALUES error, String message) {
                onRefreshListener.onError(AppConfigurationError.AppConfigErrorEnum.ServerError, error.toString());
            }
        });


    }

    void fetchCloudConfig(final String url, final OnRefreshListener onRefreshListener) {
        try {
            final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "fetchCloudConfig", response.toString());
                    saveCloudConfig(response, url);
                    onRefreshListener.onSuccess(OnRefreshListener.REFRESH_RESULT.REFRESHED_FROM_SERVER);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "fetchCloudConfig", error.toString());
                    onRefreshListener.onError(AppConfigurationError.AppConfigErrorEnum.ServerError, error.toString());
                }
            }, null, null, null);
            request.setShouldCache(true);
            mAppInfra.getRestClient().getRequestQueue().add(request);
        } catch (Exception e) {
            onRefreshListener.onError(AppConfigurationError.AppConfigErrorEnum.ServerError, e.toString());
        }
    }

    private void saveCloudConfig(JSONObject cloudConfig, String url) {
        cloudConfig = makeKeyUppercase(cloudConfig); // converting all Group and child key to Uppercase
        mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG, "uAPP_CONFIG", "Cloud config " + cloudConfig);
        mPrefEditor.putString(CLOUD_APP_CONFIG_JSON, cloudConfig.toString());
        mPrefEditor.putString(CLOUD_APP_CONFIG_URL, url);
        mPrefEditor.commit();
    }

    void clearCloudConfigFile() {
        mPrefEditor.clear();
        mPrefEditor.commit();
    }

    private SharedPreferences getCloudConfigSharedPreferences() {
        return mContext.getSharedPreferences(CLOUD_APP_CONFIG_FILE, Context.MODE_PRIVATE);
    }

    public void migrateDynamicData() {
                final AppConfigurationInterface.AppConfigurationError configError = new AppConfigurationInterface.AppConfigurationError();
                mSecureStorageInterface = mAppInfra.getSecureStorage();
                JSONObject oldDynamicConfigJson = null;
                final SecureStorageInterface.SecureStorageError mSecureStorageError = new SecureStorageInterface.SecureStorageError();
                final String jsonString = mSecureStorageInterface.fetchValueForKey(APPCONFIG_SECURE_STORAGE_KEY, mSecureStorageError);
                if (mSecureStorageError.getErrorCode() != SecureStorageInterface.SecureStorageError.secureStorageError.UnknownKey && null != jsonString || null != dynamicConfigJsonCache) {
                    mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG, "uAPP_CONFIG", "Migration starts for old dyanmic data > " + jsonString);
                    //dynamicConfigJsonCache =  null;// reset cache
                    try {
                        if (null != jsonString) {
                            oldDynamicConfigJson = new JSONObject(jsonString);
                            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG, "uAPP_CONFIG", "Migration starts for old dyanmic data > " + jsonString);
                        } else if (null != dynamicConfigJsonCache) {
                            oldDynamicConfigJson = dynamicConfigJsonCache;
                            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG, "uAPP_CONFIG", "Migration starts for old dyanmic data > " + dynamicConfigJsonCache);
                        }
                        dynamicConfigJsonCache = null;
                        oldDynamicConfigJson = makeKeyUppercase(oldDynamicConfigJson); // converting all Group and child key Uppercase
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final Iterator<String> iteratorGroup = oldDynamicConfigJson.keys();
                    while (iteratorGroup.hasNext()) {
                        final String keyGroup = iteratorGroup.next();
                        try {
                            final JSONObject objectGroup = oldDynamicConfigJson.optJSONObject(keyGroup);
                            final Iterator<String> iteratorKey = objectGroup.keys();
                            while (iteratorKey.hasNext()) {
                                final String key = iteratorKey.next();
                                final Object value = getDefaultPropertyForKey(key, keyGroup, configError);
                                if (null != value && configError.getErrorCode() == AppConfigurationInterface.AppConfigurationError.AppConfigErrorEnum.NoError) {
                                    final Object dynamicValue = objectGroup.opt(key);
                                    if (!value.equals(dynamicValue)) { // check if values are NOT equal
                                        final AppConfigurationInterface.AppConfigurationError configErrorForNewKey = new AppConfigurationInterface.AppConfigurationError();
                                        setPropertyForKey(key.toUpperCase(), keyGroup, dynamicValue, configErrorForNewKey); // add only changed value to dynamic migrated json
                                    }
                                }
                            }

                        } catch (Exception e) {
                            // Something went wrong!
                        }
                    }
                    mSecureStorageInterface.removeValueForKey(APPCONFIG_SECURE_STORAGE_KEY);
                    final String migratedDynamicData = mSecureStorageInterface.fetchValueForKey(APPCONFIG_SECURE_STORAGE_KEY_NEW, mSecureStorageError);
                    mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG, "uAPP_CONFIG", "Dynamic data  > " + migratedDynamicData);
                    mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG, "uAPP_CONFIG", "Migration completes for  > " + jsonString);
                } else {
                    mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG, "uAPP_CONFIG", "Migration not required");
                    //Log.v("uAPP_CONFIG","Migration not required" );
                }
        }

    }

