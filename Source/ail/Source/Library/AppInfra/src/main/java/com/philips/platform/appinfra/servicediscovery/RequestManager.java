package com.philips.platform.appinfra.servicediscovery;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.LocaleList;
import android.support.annotation.VisibleForTesting;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.RequestFuture;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.AppInfraLogEventID;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.rest.request.JsonObjectRequest;
import com.philips.platform.appinfra.servicediscovery.model.AISDResponse;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscovery;
import com.philips.platform.appinfra.tagging.AppInfraTaggingUtil;

import org.json.JSONObject;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.philips.platform.appinfra.tagging.AppInfraTaggingUtil.SD_CLEAR_DATA;
import static com.philips.platform.appinfra.tagging.AppInfraTaggingUtil.SD_STORE_FAILED;
import static com.philips.platform.appinfra.tagging.AppInfraTaggingUtil.SERVICE_DISCOVERY;

/**
 * The RequestManager class for service discovery.
 */
public class RequestManager {

    //    RequestQueue mRequestQueue;
    private static final String TAG = "RequestManager";//this.class.getSimpleName();
    private final AppInfraInterface mAppInfra;
    private static final String SERVICE_DISCOVERY_CACHE_FILE = "SDCacheFile";
    private final Context mContext;
    private SharedPreferences mSharedPreference;
    private SharedPreferences.Editor mPrefEditor;
    private AppInfraTaggingUtil appInfraTaggingUtil;

    public RequestManager(Context context, AppInfraInterface appInfra) {
        mContext = context;
        mAppInfra = appInfra;
        VolleyLog.DEBUG = false;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    RequestManager(Context context, AppInfraInterface appInfra, AppInfraTaggingUtil appInfraTaggingUtil) {
        mContext = context;
        mAppInfra = appInfra;
        this.appInfraTaggingUtil = appInfraTaggingUtil;
    }

    public ServiceDiscovery execute(final String url, ServiceDiscoveryManager.AISDURLType urlType) {
        final RequestFuture<JSONObject> future = RequestFuture.newFuture();
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, future, future, null, null, null);
        request.setShouldCache(true);
        mAppInfra.getRestClient().getRequestQueue().add(request);

        final ServiceDiscovery result = new ServiceDiscovery(mAppInfra);
        try {
            //ToDO: Changed timeout to 30 sec, need to monitor analytics after one week
            final JSONObject response = future.get(30, TimeUnit.SECONDS);
            cacheServiceDiscovery(response, url, urlType);
            return parseResponse(response);
        } catch (InterruptedException | TimeoutException e) {
            final ServiceDiscovery.Error err = new ServiceDiscovery.Error(ServiceDiscoveryInterface
                    .OnErrorListener.ERRORVALUES.CONNECTION_TIMEOUT, "Timed out or interrupted");
            result.setError(err);
            result.setSuccess(false);
        } catch (ExecutionException e) {
            final Throwable error = e.getCause();
            ServiceDiscovery.Error volleyError;
            if (error instanceof TimeoutError) {
                ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, AppInfraLogEventID.AI_SERVICE_DISCOVERY, "ServiceDiscovery error");
                volleyError = new ServiceDiscovery.Error(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.CONNECTION_TIMEOUT, "TimeoutORNoConnection");
            } else if (error instanceof NoConnectionError) {
                ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, AppInfraLogEventID.AI_SERVICE_DISCOVERY, "ServiceDiscovery error");
                volleyError = new ServiceDiscovery.Error(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.NO_NETWORK, "NoConnectionError");
            } else if (error instanceof AuthFailureError) {
                ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, AppInfraLogEventID.AI_SERVICE_DISCOVERY, "ServiceDiscovery error");
                volleyError = new ServiceDiscovery.Error(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.SERVER_ERROR, "AuthFailureError");
            } else if (error instanceof ServerError) {
                ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, AppInfraLogEventID.AI_SERVICE_DISCOVERY, "ServiceDiscovery error" +
                        error.toString());
                volleyError = new ServiceDiscovery.Error(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.SERVER_ERROR, "ServerError");
            } else if (error instanceof NetworkError) {
                ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, AppInfraLogEventID.AI_SERVICE_DISCOVERY, "ServiceDiscovery error" +
                        error.toString());
                volleyError = new ServiceDiscovery.Error(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.SERVER_ERROR, "NetworkError");
            } else if (error instanceof ParseError) {
                ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, AppInfraLogEventID.AI_SERVICE_DISCOVERY, "ServiceDiscovery error");
                volleyError = new ServiceDiscovery.Error(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.SERVER_ERROR, "ServerError");
            } else {
                volleyError = new ServiceDiscovery.Error(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.UNKNOWN_ERROR, "error while execute");
            }
            result.setError(volleyError);
        }
        return result;
    }

    private ServiceDiscovery parseResponse(JSONObject response) {
        final ServiceDiscovery result = new ServiceDiscovery(mAppInfra);
        result.setSuccess(response.optBoolean("success"));
        if (!result.isSuccess()) {
            final ServiceDiscovery.Error err = new ServiceDiscovery.Error(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.SERVER_ERROR, "Server reports failure");
            result.setError(err);
        } else { // no sense in further processing if server reports error
            // START setting match by country
            result.parseResponse(mContext, mAppInfra, response);
        }

        return result;
    }


    public String getLocaleList() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return LocaleList.getDefault().toString();
        } else {
            return mAppInfra.getInternationalization().getUILocaleString();
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    void cacheServiceDiscovery(JSONObject serviceDiscovery, String url, ServiceDiscoveryManager.AISDURLType urlType) {
        try {
            mSharedPreference = getServiceDiscoverySharedPreferences();
            mPrefEditor = mSharedPreference.edit();
            final Date currentDate = new Date();
            final long refreshTimeExpiry = currentDate.getTime() + 24 * 3600 * 1000;  // current time + 24 hour
            switch (urlType) {
                case AISDURLTypeProposition:
                    mPrefEditor.putString("SDPROPOSITION", serviceDiscovery.toString());
                    mPrefEditor.putString("SDPROPOSITIONURL", url);
                    break;
                case AISDURLTypePlatform:
                    mPrefEditor.putString("SDPLATFORM", serviceDiscovery.toString());
                    mPrefEditor.putString("SDPLATFORMURL", url);
                    break;
            }
            mPrefEditor.putLong("SDrefreshTime", refreshTimeExpiry);
            mPrefEditor.apply();
            if (urlType == ServiceDiscoveryManager.AISDURLType.AISDURLTypePlatform) {
                ((AppInfra)mAppInfra).getRxBus().send(new ServiceDiscoveryDownloadEvent());
            }
        } catch (Exception e) {
            appInfraTaggingUtil.trackErrorAction(SERVICE_DISCOVERY, SD_STORE_FAILED);
        }
    }


    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    AISDResponse getCachedData() {
        AISDResponse cachedResponse = null;
        mSharedPreference = getServiceDiscoverySharedPreferences();
        if (mSharedPreference != null) {
            if (getPropositionEnabled(mAppInfra)) {
                try {
                    final String platformCache = mSharedPreference.getString("SDPLATFORM", null);
                    if (platformCache != null) {

                        final JSONObject platformObject = new JSONObject(platformCache);
                        final ServiceDiscovery platformService = parseResponse(platformObject);
                        cachedResponse = new AISDResponse(mAppInfra);
                        cachedResponse.setPlatformURLs(platformService);
                        return cachedResponse;
                    }
                } catch (Exception exception) {
                    ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR,
                            AppInfraLogEventID.AI_SERVICE_DISCOVERY, "while getting cached data" + exception.getMessage());
                }
            } else {
                try {
                    final String propositionCache = mSharedPreference.getString("SDPROPOSITION", null);
                    final String platformCache = mSharedPreference.getString("SDPLATFORM", null);
                    if (propositionCache != null && platformCache != null) {
                        final JSONObject propositionObject = new JSONObject(propositionCache);
                        final ServiceDiscovery propostionService = parseResponse(propositionObject);

                        final JSONObject platformObject = new JSONObject(platformCache);
                        final ServiceDiscovery platformService = parseResponse(platformObject);
                        cachedResponse = new AISDResponse(mAppInfra);
                        cachedResponse.setPropositionURLs(propostionService);
                        cachedResponse.setPlatformURLs(platformService);
                        return cachedResponse;
                    }
                } catch (Exception exception) {
                    ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR,
                            AppInfraLogEventID.AI_SERVICE_DISCOVERY, "while getting cached data" + exception.getMessage());
                }
            }
        }
        return cachedResponse;
    }

    String getUrlProposition() {
        mSharedPreference = getServiceDiscoverySharedPreferences();
        if (mSharedPreference != null) {
            return mSharedPreference.getString("SDPROPOSITIONURL", null);
        }
        return null;
    }

    String getUrlPlatform() {
        mSharedPreference = getServiceDiscoverySharedPreferences();
        if (mSharedPreference != null) {
            return mSharedPreference.getString("SDPLATFORMURL", null);
        }
        return null;
    }

    boolean isServiceDiscoveryDataExpired() {
        mSharedPreference = getServiceDiscoverySharedPreferences();
        if (mSharedPreference != null) {
            final long refreshTimeExpiry = mSharedPreference.getLong("SDrefreshTime", 0);
            final Date currentDate = new Date();
            long currentDateLong = currentDate.getTime();
            return currentDateLong >= refreshTimeExpiry;
        }
        return false;
    }


    void clearCacheServiceDiscovery() {
        mSharedPreference = getServiceDiscoverySharedPreferences();
        mPrefEditor = mSharedPreference.edit();
        mPrefEditor.clear();
        mPrefEditor.apply();
        ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG, AppInfraLogEventID.AI_SERVICE_DISCOVERY,  SD_CLEAR_DATA);
    }

    SharedPreferences getServiceDiscoverySharedPreferences() {
        return mContext.getSharedPreferences(SERVICE_DISCOVERY_CACHE_FILE, Context.MODE_PRIVATE);
    }

    //TODO - need to change api name as it work in opposite way
    boolean getPropositionEnabled(AppInfraInterface appInfra) {
        final AppConfigurationInterface.AppConfigurationError appConfigurationError = new AppConfigurationInterface
                .AppConfigurationError();

        try {
            final Object propositionEnabledObject = appInfra.getConfigInterface()
                    .getPropertyForKey("servicediscovery.propositionEnabled", "appinfra",
                            appConfigurationError);
            if (propositionEnabledObject != null) {
                if (propositionEnabledObject instanceof Boolean) {
                    final Boolean propositionEnabled = (Boolean) propositionEnabledObject;
                    if (appConfigurationError.getErrorCode() == AppConfigurationInterface.AppConfigurationError.AppConfigErrorEnum.NoError && !propositionEnabled) {
                        return true;
                    }
                } else {
                    ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.VERBOSE, AppInfraLogEventID.AI_SERVICE_DISCOVERY, "servicediscovery.propositionEnabled instance should be boolean value true or false");
                }
            }
        } catch (IllegalArgumentException illegalArgumentException) {
            ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, AppInfraLogEventID.AI_SERVICE_DISCOVERY, "IllegalArgumentException while getPropositionEnabled");
        }
        return false;
    }

}
