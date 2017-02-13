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

    private AppInfra mAppInfra;
    private Context mContext;
    private JSONObject dynamicConfigJsonCache;
    private JSONObject cloudConfigJsonCache;
    private JSONObject staticConfigJsonCache;
    private static final String mAppConfig_SecureStoreKey = "ail.app_config";
    private static final String mAppConfig_SecureStoreKey_new = "ailNew.app_config";
    private static final String CLOUD_APP_CONFIG_FILE = "CloudConfig";
    private static final String CLOUD_APP_CONFIG_JSON = "cloudConfigJson";
    private static final String CLOUD_APP_CONFIG_URL = "cloudConfigUrl";

    private SecureStorageInterface ssi;

    public AppConfigurationManager(AppInfra appInfra) {
        mAppInfra = appInfra;
        mContext = appInfra.getAppInfraContext();
    }

    protected JSONObject getMasterConfigFromApp() {
        JSONObject result = null;
        try {
            InputStream mInputStream = mContext.getAssets().open("AppConfig.json");
            BufferedReader r = new BufferedReader(new InputStreamReader(mInputStream));
            StringBuilder total = new StringBuilder();
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
        ssi = mAppInfra.getSecureStorage();
        JSONObject jObj = null;
        SecureStorageInterface.SecureStorageError sse = new SecureStorageInterface.SecureStorageError();
        String jsonString = ssi.fetchValueForKey(mAppConfig_SecureStoreKey_new, sse);
        if (null == jsonString || null == sse) {
            //jObj = getMasterConfigFromApp();// reads from Application asset
        } else {

            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG, "uAPP_CONFIG", jsonString);
            try {
                jObj = new JSONObject(jsonString);
                jObj = makeKeyUppercase(jObj); // converting all Group and child key Uppercase
            } catch (Exception e) {
                mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, "AppConfiguration exception",
                        Log.getStackTraceString(e));
            }
        }
        return jObj;
    }

    private JSONObject getCloudConfigJsonCache() {
        if (cloudConfigJsonCache == null) {
            cloudConfigJsonCache = getCloudJSONFromDevice();
        }
        return cloudConfigJsonCache;
    }

    private JSONObject getCloudJSONFromDevice() {
        JSONObject cloudConfigJsonObj = null;
        SharedPreferences sharedPreferences = getCloudConfigSharedPreferences();
        if (null != sharedPreferences && sharedPreferences.contains(CLOUD_APP_CONFIG_JSON)) {
            final String savedCloudConfigJson = sharedPreferences.getString(CLOUD_APP_CONFIG_JSON, null);
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
        if (null == group || null == key || group.isEmpty() || key.isEmpty() || !group.matches("[a-zA-Z0-9_.-]+") || !key.matches("[a-zA-Z0-9_.-]+")) {
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
                boolean isCocoPresent = dynamicConfigJsonCache.has(group);
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
                        if (((ArrayList) object).get(0) instanceof ArrayList) {
                            throw new IllegalArgumentException("Invalid Argument Exception");
                        } else if (((ArrayList) object).get(0) instanceof Integer || ((ArrayList) object).get(0) instanceof String) {

                            JSONArray jsonArray = new JSONArray(((ArrayList) object).toArray());
                            cocoJSONobject.put(key, jsonArray);

                        } else {
                            throw new IllegalArgumentException("Invalid Argument Exception");
                        }
                    } else if (object instanceof HashMap) { // if object is MAP
                        Map<?, ?> map = (Map) object;
                        Set<?> keyset = map.keySet();
                        Iterator<?> keyItr = keyset.iterator();
                        Object objectKey = keyItr.next();
                        Object value = map.get(objectKey); // value for key:objectKey
                        if (null == value) {
                            throw new IllegalArgumentException("Invalid Argument Exception");
                        } else {

                            if (objectKey instanceof String) { // if keys are String
                                if (value instanceof String || value instanceof Integer) { // if value are Integer OR String
                                    JSONObject jsonObject = new JSONObject(object.toString());
                                    cocoJSONobject.put(key, jsonObject);
                                } else {
                                    throw new IllegalArgumentException("Invalid Argument Exception");
                                }
                            } else {
                                throw new IllegalArgumentException("Invalid Argument Exception");
                            }
                        }
                    } else if (object instanceof Integer || object instanceof String || null == object) {

                        cocoJSONobject.put(key, object);
                    } else {
                        throw new IllegalArgumentException("Invalid Argument Exception");
                    }
                    SecureStorageInterface.SecureStorageError sse = new SecureStorageInterface.SecureStorageError();
                    ssi.storeValueForKey(mAppConfig_SecureStoreKey_new, dynamicConfigJsonCache.toString(), sse);
                    if (null == sse.getErrorCode()) {
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
        if (null == group || null == key || group.isEmpty() || key.isEmpty() || !group.matches("[a-zA-Z0-9_.-]+") || !key.matches("[a-zA-Z0-9_.-]+")) {
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
            boolean isCocoPresent = jsonObject.has(group);
            if (!isCocoPresent) { // if request coco does not exist
                configError.setErrorCode(AppConfigurationError.AppConfigErrorEnum.GroupNotExists);
            } else {
                JSONObject cocoJSONobject = jsonObject.optJSONObject(group);
                if (null == cocoJSONobject) { // invalid Coco JSON
                    configError.setErrorCode(AppConfigurationError.AppConfigErrorEnum.FatalError);
                } else {
                    boolean isKeyPresent = cocoJSONobject.has(key);
                    if (!isKeyPresent) { // if key is not found inside coco
                        configError.setErrorCode(AppConfigurationError.AppConfigErrorEnum.KeyNotExists);
                    } else {
                        object = cocoJSONobject.opt(key); // Returns the value mapped by name, or null if no such mapping exists
                        if (null == object) {

                        } else {
                            //  KEY FOUND SUCCESS
                            configError.setErrorCode(AppConfigurationError.AppConfigErrorEnum.NoError);
                            if (cocoJSONobject.opt(key) instanceof JSONArray) {
                                JSONArray jsonArray = cocoJSONobject.optJSONArray(key);
                                List<Object> list = new ArrayList<Object>();
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
        HashMap<String, Object> map = new HashMap<String, Object>();
        JSONObject jObject = new JSONObject(JSON.toString());
        Iterator<?> keys = jObject.keys();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            Object value = jObject.get(key);
            map.put(key, value);
        }
        return map;
    }

    private JSONObject makeKeyUppercase(JSONObject json) {
        JSONObject newJsonGroup = new JSONObject();
        Iterator<String> iteratorGroup = json.keys();
        while (iteratorGroup.hasNext()) {
            String keyGroup = iteratorGroup.next();
            try {
                JSONObject objectGroup = json.optJSONObject(keyGroup);


                JSONObject newJsonChildObject = new JSONObject();
                Iterator<String> iteratorKey = objectGroup.keys();
                while (iteratorKey.hasNext()) {
                    String key = iteratorKey.next();
                    try {
                        Object objectKey = objectGroup.opt(key);
                        newJsonChildObject.put(key.toUpperCase(), objectKey);
                    } catch (JSONException e) {
                        // Something went wrong!
                    }

                }
                newJsonGroup.put(keyGroup.toUpperCase(), newJsonChildObject);

            } catch (JSONException e) {
                // Something went wrong!
            }
        }
        return newJsonGroup;
    }

    @Override
    public void refreshCloudConfig(final OnRefreshListener onRefreshListener) {
        AppConfigurationError configError = new AppConfigurationError();
        String cloudServiceId = (String) getPropertyForKey("appconfig.cloudServiceId", "APPINFRA", configError);
        ServiceDiscoveryInterface serviceDiscoveryInterface = mAppInfra.getServiceDiscovery();
        serviceDiscoveryInterface.getServiceUrlWithCountryPreference(cloudServiceId, new ServiceDiscoveryInterface.OnGetServiceUrlListener() {
            @Override
            public void onSuccess(URL url) {
                SharedPreferences sharedPreferences = getCloudConfigSharedPreferences();
                if (null != sharedPreferences && sharedPreferences.contains(CLOUD_APP_CONFIG_URL)) {
                    final String savedURL = sharedPreferences.getString(CLOUD_APP_CONFIG_URL, null);
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
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
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
        mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG, "uAPP_CONFIG", "Cloud config "+cloudConfig);
        SharedPreferences sharedPreferences = getCloudConfigSharedPreferences();
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        prefEditor.putString(CLOUD_APP_CONFIG_JSON, cloudConfig.toString());
        prefEditor.putString(CLOUD_APP_CONFIG_URL, url);
        prefEditor.commit();
    }

    void clearCloudConfigFile() {
        SharedPreferences prefs = getCloudConfigSharedPreferences();
        SharedPreferences.Editor prefEditor = prefs.edit();
        prefEditor.clear();
        prefEditor.commit();
    }

    private SharedPreferences getCloudConfigSharedPreferences() {
        return mContext.getSharedPreferences(CLOUD_APP_CONFIG_FILE, Context.MODE_PRIVATE);
    }

    public void migrateDynamicData() {
        AppConfigurationInterface.AppConfigurationError configError = new AppConfigurationInterface.AppConfigurationError();
        ssi = mAppInfra.getSecureStorage();
        JSONObject oldDynamicConfigJson = null;
        SecureStorageInterface.SecureStorageError sse = new SecureStorageInterface.SecureStorageError();
        String jsonString = ssi.fetchValueForKey(mAppConfig_SecureStoreKey, sse);
        if (sse.getErrorCode() != SecureStorageInterface.SecureStorageError.secureStorageError.UnknownKey && null != jsonString) {
           mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG, "uAPP_CONFIG", "Migration starts for old dyanmic data > " + jsonString);
            dynamicConfigJsonCache =  null;// reset cache
            try {
                oldDynamicConfigJson = new JSONObject(jsonString);
                oldDynamicConfigJson = makeKeyUppercase(oldDynamicConfigJson); // converting all Group and child key Uppercase
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Iterator<String> iteratorGroup = oldDynamicConfigJson.keys();
            while (iteratorGroup.hasNext()) {
                String keyGroup = iteratorGroup.next();
                try {
                    JSONObject objectGroup = oldDynamicConfigJson.optJSONObject(keyGroup);
                    Iterator<String> iteratorKey = objectGroup.keys();
                    while (iteratorKey.hasNext()) {
                        String key = iteratorKey.next();
                           Object value= getDefaultPropertyForKey(key, keyGroup,configError );
                           if(null!=value && configError.getErrorCode()== AppConfigurationInterface.AppConfigurationError.AppConfigErrorEnum.NoError ){
                               Object dynamicValue= objectGroup.opt(key);
                               if(!value.equals(dynamicValue)){ // check if values are NOT equal
                                   AppConfigurationInterface.AppConfigurationError configErrorForNewKey = new AppConfigurationInterface.AppConfigurationError();
                                   setPropertyForKey(key.toUpperCase(),keyGroup, dynamicValue,configErrorForNewKey); // add only changed value to dynamic migrated json
                               }
                           }
                    }

                } catch (Exception e) {
                    // Something went wrong!
                }
            }
            ssi.removeValueForKey(mAppConfig_SecureStoreKey);
            String migratedDynamicData = ssi.fetchValueForKey(mAppConfig_SecureStoreKey_new,sse);
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG, "uAPP_CONFIG", "Dynamic data  > " + migratedDynamicData);
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG, "uAPP_CONFIG", "Migration completes for  > " + jsonString);
        }else{
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG, "uAPP_CONFIG", "Migration not required");
            //Log.v("uAPP_CONFIG","Migration not required" );
        }

    }
}
