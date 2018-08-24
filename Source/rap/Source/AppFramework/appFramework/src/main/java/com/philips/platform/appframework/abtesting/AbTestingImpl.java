package com.philips.platform.appframework.abtesting;

import android.support.annotation.NonNull;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.AppInfraLogEventID;
import com.philips.platform.appinfra.abtestclient.ABTestClientInterface;
import com.philips.platform.appinfra.abtestclient.CacheModel;
import com.philips.platform.appinfra.logging.LoggingInterface;

import java.util.HashMap;
import java.util.Map;

public class AbTestingImpl implements ABTestClientInterface {

    interface FetchDataHandler {
        void fetchData(Map<String, CacheModel.ValueModel> data);
        void updateCacheStatus(ABTestClientInterface.CACHESTATUS CACHESTATUS);
    }

    private FireBaseWrapper fireBaseWrapper;
    private AbTestingLocalCache abTestingLocalCache;
    private Map<String, CacheModel.ValueModel> inMemoryCache;
    private AppInfraInterface appInfraInterface;
    private CACHESTATUS CACHESTATUSVALUE = CACHESTATUS.EXPERIENCE_NOT_UPDATED;

    /**
     * invoke this api to initialise FireBase remote configuration
     */
    public void initFireBase() {
        fireBaseWrapper = getFireBaseWrapper();
    }

    @NonNull
    FireBaseWrapper getFireBaseWrapper() {
        return new FireBaseWrapper(FirebaseRemoteConfig.getInstance());
    }

    /**
     * To be invoked first before calling any api of this class
     * @param appInfraInterface - pass appInfra reference
     */
    public void initAbTesting(AppInfraInterface appInfraInterface) {
        this.appInfraInterface = appInfraInterface;
        abTestingLocalCache = getAbTestingLocalCache(appInfraInterface);
        abTestingLocalCache.initAppInfra(appInfraInterface);
        fireBaseWrapper.initAppInfra(appInfraInterface);
        inMemoryCache = new HashMap<>();
        // Syncing in-memory cache with disk memory if available
        if (abTestingLocalCache.getCacheFromPreference().getTestValues() != null)
            syncInMemoryCache(inMemoryCache, abTestingLocalCache.getCacheFromPreference().getTestValues());
        appInfraInterface.getLogging().log(LoggingInterface.LogLevel.DEBUG, AppInfraLogEventID.AI_ABTEST_CLIENT, " in-memory cache size " + inMemoryCache.size() + "");
    }

    @NonNull
    AbTestingLocalCache getAbTestingLocalCache(AppInfraInterface appInfraInterface) {
        return new AbTestingLocalCache(appInfraInterface.getAppInfraContext(), new Gson());
    }

    @Override
    public CACHESTATUS getCacheStatus() {
        return CACHESTATUSVALUE;
    }

    @Override
    public String getTestValue(@NonNull String requestNameKey, @NonNull String defaultValue, UPDATETYPE updateType) {
        if (requestNameKey.isEmpty())
            return null;
        //fetching data from in-memory cache
        CacheModel.ValueModel valueModel = inMemoryCache.get(requestNameKey);
        String testValue;
        if (valueModel == null || valueModel.getTestValue() == null) {
            testValue = defaultValue;
        } else
            testValue = valueModel.getTestValue();
        // once the value is given from in-memory then we check if the type is restart or update
        // then if it is update then we store the value in the disk else
        // in memory(update of in-memory only happens if the key is not defined in server)
        updateCachesForTestName(requestNameKey, testValue, updateType);
        return testValue;
    }

    @Override
    public void updateCache(OnRefreshListener onRefreshListener) {
        if(!appInfraInterface.getRestClient().isInternetReachable()) {
            // throw error callback if no network available
            onRefreshListener.onError(OnRefreshListener.ERRORVALUE.NO_NETWORK);
            return;
        }
        // fetching data from FireBase Server
        fireBaseWrapper.fetchDataFromFireBase(getFetchDataHandler(), onRefreshListener);
    }

    @NonNull
    FetchDataHandler getFetchDataHandler() {
        return new FetchDataHandler() {
            @Override
            public void fetchData(Map<String, CacheModel.ValueModel> data) {
                // Syncing in-memory cache with FireBase data if required
                syncInMemoryCache(inMemoryCache, data);
            }

            @Override
            public void updateCacheStatus(CACHESTATUS CACHESTATUS) {
                // update cache status
                AbTestingImpl.this.CACHESTATUSVALUE = CACHESTATUS;
            }
        };
    }

    @Override
    public void enableDeveloperMode(boolean state) {
        // enable/dis-able FireBase developer mode
        fireBaseWrapper.enableDeveloperMode(state);
    }

    private void updateCachesForTestName(String requestNameKey, String testValue, UPDATETYPE updateType) {
        if (inMemoryCache.containsKey(requestNameKey)) {
            final CacheModel.ValueModel val = inMemoryCache.get(requestNameKey);
            if (val.getTestValue() == null || !updateType.name().equalsIgnoreCase(UPDATETYPE.APP_RESTART.name())) {
                updateInMemoryCache(requestNameKey, testValue, updateType.name());
            }
            //else value is already there in cache ignoring the new value
        }
        if (updateType.equals(UPDATETYPE.APP_RESTART)) {
            // remove testValue from disk if update type is app-start
            abTestingLocalCache.removeFromDisk(requestNameKey);
        } else if (updateType.name().equals
                (UPDATETYPE.APP_UPDATE.name())) {
            // saving test value to disk cache
            abTestingLocalCache.updatePreferenceCacheModel(requestNameKey, inMemoryCache.get(requestNameKey));
            abTestingLocalCache.saveCacheToDisk();
        }
        appInfraInterface.getLogging().log(LoggingInterface.LogLevel.INFO, AppInfraLogEventID.AI_ABTEST_CLIENT,
                "testValue " + testValue);
    }

    private void updateInMemoryCache(String requestNameKey, String testValue, String updateType) {
        final CacheModel.ValueModel valueModel = new CacheModel.ValueModel();
        valueModel.setTestValue(testValue);
        valueModel.setUpdateType(updateType);
        valueModel.setAppVersion(appInfraInterface.getAppIdentity().getAppVersion());
        inMemoryCache.put(requestNameKey, valueModel);
    }

    private void syncInMemoryCache(Map<String, CacheModel.ValueModel> inMemoryCache, Map<String, CacheModel.ValueModel> cacheModel) {
        for (Map.Entry<String, CacheModel.ValueModel> entry : cacheModel.entrySet()) {
            appInfraInterface.getLogging().log(LoggingInterface.LogLevel.DEBUG, AppInfraLogEventID.AI_ABTEST_CLIENT, " abtest data "+entry.getKey() +" --- "+ entry.getValue().getTestValue());
            if (inMemoryCache.containsKey(entry.getKey())) {
                CacheModel.ValueModel valueModel = inMemoryCache.get(entry.getKey());
                if (valueModel.getUpdateType() != null && valueModel.getUpdateType().equals(UPDATETYPE.APP_RESTART.name())) {
                    valueModel.setTestValue(entry.getValue().getTestValue());
                } else if (valueModel.getUpdateType() != null && valueModel.getUpdateType().equals(UPDATETYPE.APP_UPDATE.name())) {
                    if (isAppUpdated(valueModel)) {
                        valueModel.setTestValue(entry.getValue().getTestValue());
                        valueModel.setAppVersion(entry.getValue().getAppVersion());
                    }
                }
            } else
                inMemoryCache.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     *
     * @param valueModel - to get version from disk
     * @return - will verify is application version updated
     */
    private boolean isAppUpdated(CacheModel.ValueModel valueModel) {
        return valueModel.getAppVersion() == null || valueModel.getAppVersion().isEmpty() || !valueModel.getAppVersion().equalsIgnoreCase(appInfraInterface.getAppIdentity().getAppVersion());
    }

    Map<String, CacheModel.ValueModel> getInMemoryCache() {
        return inMemoryCache;
    }
}
