/* Copyright (c) Koninklijke Philips N.V. 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.appinfra.tagging;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.adobe.mobile.Analytics;
import com.adobe.mobile.Config;
import com.adobe.mobile.MobilePrivacyStatus;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.AppInfraLogEventID;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.securestorage.SecureStorageInterface;
import com.philips.platform.appinfra.timesync.TimeInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static com.adobe.mobile.Analytics.getTrackingIdentifier;
import static com.adobe.mobile.MobilePrivacyStatus.MOBILE_PRIVACY_STATUS_OPT_IN;
import static com.adobe.mobile.MobilePrivacyStatus.MOBILE_PRIVACY_STATUS_UNKNOWN;
import static com.philips.platform.appinfra.tagging.AppTagging.ACTION_NAME;
import static com.philips.platform.appinfra.tagging.AppTagging.ACTION_TAGGING_DATA;
import static com.philips.platform.appinfra.tagging.AppTagging.AIL_PRIVACY_CONSENT;
import static com.philips.platform.appinfra.tagging.AppTagging.EXTRA_TAGGING_DATA;
import static com.philips.platform.appinfra.tagging.AppTagging.PAGE_NAME;
import static com.philips.platform.appinfra.tagging.AppTaggingInterface.PrivacyStatus.OPTIN;
import static com.philips.platform.appinfra.tagging.AppTaggingInterface.PrivacyStatus.OPTOUT;
import static com.philips.platform.appinfra.tagging.AppTaggingInterface.PrivacyStatus.UNKNOWN;
import static java.lang.Enum.valueOf;


/**
 * A Wrapper class forAppTaggingHandler.
 */
public class AppTaggingHandler {
    private static String prevPage;
    private final AppInfraInterface mAppInfra;
    private String mComponentID;
    private String mComponentVersion;
    private String ADB_PRIVACY_STATUS = "ail_adb_status";
    private JSONObject masterAdbMobileConfig;

    public AppTaggingHandler(AppInfraInterface aAppInfra) {
        mAppInfra = aAppInfra;
        AppTaggingInterface.PrivacyStatus privacyStatus = null;

        // do not invoke Adobe get privacy status API as it blocks tagging on first launch
        if (PrivacyStatusCache.getPrivacyStatus() != null)
            privacyStatus = PrivacyStatusCache.getPrivacyStatus();
        else if (!TextUtils.isEmpty(mAppInfra.getSecureStorage().fetchValueForKey(ADB_PRIVACY_STATUS, getSecureStorageError()))) {
            privacyStatus = valueOf(AppTaggingInterface.PrivacyStatus.class, mAppInfra.getSecureStorage().fetchValueForKey(ADB_PRIVACY_STATUS, getSecureStorageError()));
        }
        if (privacyStatus != null) {
            PrivacyStatusCache.setPrivacyStatus(privacyStatus);
            mAppInfra.getSecureStorage().storeValueForKey(ADB_PRIVACY_STATUS, privacyStatus.name(), getSecureStorageError());
            if (privacyStatus == OPTOUT) {
                Config.setPrivacyStatus(MOBILE_PRIVACY_STATUS_OPT_IN);
            }
        }
    }

    @NonNull
    SecureStorageInterface.SecureStorageError getSecureStorageError() {
        return new SecureStorageInterface.SecureStorageError();
    }

    /*
     * Checks for SSL connection value from Adobe json
     * */
    protected boolean checkForSslConnection() {
        boolean sslValue = false;
        final JSONObject jSONObject = getMasterADBMobileConfig();
        try {
            if (jSONObject != null) {
                sslValue = jSONObject.getJSONObject("analytics").optBoolean("ssl");
                if (sslValue) {
                    ((AppInfra) mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG, AppInfraLogEventID.AI_TAGGING, "ssl value true");
                    return sslValue;
                } else if (!checkForProductionState()) {
                    throw new AssertionError("ssl value in ADBMobileConfig.json should be true");
                }
            }
        } catch (JSONException e) {
            ((AppInfra) mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, AppInfraLogEventID.AI_TAGGING, "AdobeMobile Configuration exception" +
                    Log.getStackTraceString(e));
        }

        return sslValue;
    }

    /**
     * Reading from Adobe json
     */
    protected JSONObject getMasterADBMobileConfig() {
        if (masterAdbMobileConfig != null)
            return masterAdbMobileConfig;

        try {
            final InputStream mInputStream = mAppInfra.getAppInfraContext().getAssets().open("ADBMobileConfig.json");
            final BufferedReader mBufferedReader = new BufferedReader(new InputStreamReader(mInputStream));
            final StringBuilder mStringBuilder = new StringBuilder();
            String line;
            while ((line = mBufferedReader.readLine()) != null) {
                mStringBuilder.append(line).append('\n');
            }
            masterAdbMobileConfig = new JSONObject(mStringBuilder.toString());
           /* ((AppInfra)mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG, AppInfraLogEventID.AI_TAGGING, "Master ADB Mobile Config Json" +
                    masterAdbMobileConfig.toString());*/
        } catch (Exception e) {
            ((AppInfra) mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, AppInfraLogEventID.AI_TAGGING, "Tagging ADBMobileConfig file reading exception" +
                    Log.getStackTraceString(e));
        }
        return masterAdbMobileConfig;
    }

    /**
     * Constructing default Object data
     **/
    private Map<String, Object> addAnalyticsDataObject() {
        final Map<String, Object> contextData = new HashMap<>();
        contextData.put(AppTaggingConstants.LANGUAGE_KEY, getLanguage());
        contextData.put(AppTaggingConstants.APPSID_KEY, getTrackingIdentifier());
        if (getComponentId() != null) {
            contextData.put(AppTaggingConstants.COMPONENT_ID, getComponentId());
        }
        if (getComponentVersionVersionValue() != null) {
            contextData.put(AppTaggingConstants.COMPONENT_VERSION, getComponentVersionVersionValue());
        }
        contextData.put(AppTaggingConstants.LOCAL_TIMESTAMP_KEY, getLocalTimestamp());
        contextData.put(AppTaggingConstants.UTC_TIMESTAMP_KEY, getUTCTimestamp());
        contextData.put(AppTaggingConstants.BUNDLE_ID, getAppStateFromConfig());
        // Removes Sensitive data from Context object taking Consent
        return removeSensitiveData(contextData);
    }

    /**
     * Removes Sensitive data declared in Adobe json.
     **/
    private Map<String, Object> removeSensitiveData(Map<String, Object> data) {
        final AppConfigurationInterface.AppConfigurationError configError = new AppConfigurationInterface
                .AppConfigurationError();
        if (getPrivacyConsentSensitiveData() && mAppInfra.getConfigInterface() != null) {
            try {
                final Object object = mAppInfra.getConfigInterface().getPropertyForKey
                        ("tagging.sensitiveData", "appinfra", configError);
                if (object instanceof ArrayList<?>) {
                    final ArrayList<?> taggingSensitiveData = (ArrayList<?>) object;
                    if (taggingSensitiveData.size() > 0) {
                        data.keySet().removeAll(taggingSensitiveData);
                    }
                }
            } catch (Exception e) {
                ((AppInfra) mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR,
                        AppInfraLogEventID.AI_TAGGING, "Tagging" + e);
            }
        }
        return data;
    }


    private String getAppStateFromConfig() {
        if (mAppInfra != null && mAppInfra.getAppIdentity() != null) {
            try {
                return mAppInfra.getAppIdentity().
                        getAppState().toString();
            } catch (Exception e) {
                ((AppInfra) mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR,
                        AppInfraLogEventID.AI_TAGGING, "Tagging" + e);
            }
        }
        return null;
    }

    private String getLanguage() {
        String mLanguage = null;
        if (mAppInfra != null) {
            final String uiLocale = mAppInfra.getInternationalization().getUILocaleString();
            mLanguage = uiLocale.substring(0, 2);
            ((AppInfra) mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG,
                    AppInfraLogEventID.AI_TAGGING, "Tagging" + mLanguage);
        }
        return mLanguage;
    }

    private String getUTCTimestamp() {
        String mUTCTimestamp = null;
        if (mAppInfra != null && mAppInfra.getTime() != null) {
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z", Locale.ENGLISH);
            dateFormat.setTimeZone(TimeZone.getTimeZone(TimeInterface.UTC));
            mUTCTimestamp = dateFormat.format(mAppInfra.getTime().getUTCTime());
            ((AppInfra) mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG,
                    AppInfraLogEventID.AI_TAGGING, "Tagging" + mUTCTimestamp);
        }
        return mUTCTimestamp;
    }

    private String getLocalTimestamp() {
        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z", Locale.ENGLISH);
        return dateFormat.format(calendar.getTime());
    }

    AppTaggingInterface.PrivacyStatus getMobilePrivacyStatus() {
        if (PrivacyStatusCache.getPrivacyStatus() != null)
            return PrivacyStatusCache.getPrivacyStatus();
        else if (!TextUtils.isEmpty(mAppInfra.getSecureStorage().fetchValueForKey(ADB_PRIVACY_STATUS, getSecureStorageError()))) {
            return valueOf(AppTaggingInterface.PrivacyStatus.class, mAppInfra.getSecureStorage().fetchValueForKey(ADB_PRIVACY_STATUS, getSecureStorageError()));
        } else {
            return getAdobePrivacyStatus();
        }
    }

    private AppTaggingInterface.PrivacyStatus getAdobePrivacyStatus() {
        final MobilePrivacyStatus mMobilePrivacyStatus = Config.getPrivacyStatus();
        AppTaggingInterface.PrivacyStatus mPrivacyStatus = null;
        switch (mMobilePrivacyStatus) {
            case MOBILE_PRIVACY_STATUS_OPT_IN:
                mPrivacyStatus = OPTIN;
                break;
            case MOBILE_PRIVACY_STATUS_OPT_OUT:
                mPrivacyStatus = OPTOUT;
                break;
            case MOBILE_PRIVACY_STATUS_UNKNOWN:
                mPrivacyStatus = UNKNOWN;
                break;
        }
        return mPrivacyStatus;
    }

    void setPrivacyStatus(AppTaggingInterface.PrivacyStatus privacyStatus) {
        PrivacyStatusCache.setPrivacyStatus(privacyStatus);
        switch (privacyStatus) {
            case OPTIN:
                mAppInfra.getSecureStorage().storeValueForKey(ADB_PRIVACY_STATUS, privacyStatus.name(), getSecureStorageError());
                Analytics.trackAction("analyticsOptIn", null);
                Config.setPrivacyStatus(MOBILE_PRIVACY_STATUS_OPT_IN);
                break;
            case OPTOUT:
                mAppInfra.getSecureStorage().storeValueForKey(ADB_PRIVACY_STATUS, privacyStatus.name(), getSecureStorageError());
                Analytics.trackAction("analyticsOptOut", null);
                Config.setPrivacyStatus(MOBILE_PRIVACY_STATUS_OPT_IN);
                break;
            case UNKNOWN:
                mAppInfra.getSecureStorage().storeValueForKey(ADB_PRIVACY_STATUS, privacyStatus.name(), getSecureStorageError());
                Analytics.trackAction("analyticsUnknown", null);
                Config.setPrivacyStatus(MOBILE_PRIVACY_STATUS_UNKNOWN);
                break;
        }
    }

    void track(String pageName, Map<String, String> paramMap, boolean isTrackPage) {
        if (PrivacyStatusCache.shouldTrack() && (checkForSslConnection() || checkForProductionState())) {
            trackData(pageName, paramMap, isTrackPage);
        }
    }

    protected boolean checkForProductionState() {
        if (mAppInfra.getAppIdentity() != null) {
            try {
                return !mAppInfra.getAppIdentity().
                        getAppState().toString().equalsIgnoreCase("Production");
            } catch (Exception e) {
                ((AppInfra) mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR,
                        AppInfraLogEventID.AI_TAGGING, "Tagging" + e);
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private void trackData(String pageName, Map<String, String> paramMap, boolean isTrackPage) {
        Map contextData = addAnalyticsDataObject();
        if (paramMap != null) {
            paramMap.putAll(contextData);
            contextData = removeSensitiveData((HashMap) paramMap);
        }
        if (null != prevPage && isTrackPage) {
            contextData.put(AppTaggingConstants.PREVIOUS_PAGE_NAME, prevPage);
        }
        if (isTrackPage) {
            if (pageName != null && !pageName.isEmpty()) {
                if (pageName.getBytes().length > 100) {
                    ((AppInfra) mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG, AppInfraLogEventID.AI_TAGGING, "Page name exceeds 100 bytes in length");
                }
                if (pageName.equalsIgnoreCase(prevPage)) {
                    ((AppInfra) mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG, AppInfraLogEventID.AI_TAGGING, "Page name and previous page name shouldn't be same");
                }
                Analytics.trackState(pageName, contextData);
            } else {
                ((AppInfra) mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG, AppInfraLogEventID.AI_TAGGING, "Page name should not  be empty ");
            }
            contextData.put(PAGE_NAME, pageName);
            prevPage = pageName;
        } else {
            final String event = pageName.replaceAll("\\s+", "");
            if (event != null && !event.isEmpty()) {
                if (event.getBytes().length > 255) {
                    ((AppInfra) mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG, AppInfraLogEventID.AI_TAGGING, "Event  exceeds 255 bytes in length");
                }
                Analytics.trackAction(event, contextData);
            } else {
                ((AppInfra) mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG, AppInfraLogEventID.AI_TAGGING, "Event  is null ");
            }
            contextData.put(ACTION_NAME, event);
        }
        sendBroadcast(contextData);  // sending broadcast
    }


    void timeActionStart(String actionStart, Map<String, Object> contextData) {
        if (PrivacyStatusCache.shouldTrack() && (checkForSslConnection() || checkForProductionState())) {
            Map<String, Object> analyticsDefaultParameters = addAnalyticsDataObject();
            if (contextData != null) analyticsDefaultParameters.putAll(contextData);
            Analytics.trackTimedActionStart(actionStart, analyticsDefaultParameters);
        }
    }

    void timeActionEnd(String actionEnd, Analytics.TimedActionBlock<Boolean> logic) {
        if (PrivacyStatusCache.shouldTrack() && (checkForSslConnection() || checkForProductionState())) {
            Analytics.trackTimedActionEnd(actionEnd, logic);
        }
    }

    boolean getPrivacyConsentSensitiveData() {
        if (mAppInfra != null) {
            final String consentValueString = mAppInfra.getSecureStorage().fetchValueForKey(AIL_PRIVACY_CONSENT, getSecureStorageErrorValue());
            final boolean consentValue = consentValueString != null && consentValueString.equalsIgnoreCase("true");
            ((AppInfra) mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG,
                    AppInfraLogEventID.AI_TAGGING, "Tagging-consentValue" + consentValue);
            return consentValue;
        }
        return false;
    }

    void setPrivacyConsentSensitiveData(boolean valueContent) {
        mAppInfra.getSecureStorage().storeValueForKey(AIL_PRIVACY_CONSENT, String.valueOf(valueContent), getSecureStorageErrorValue());
    }

    private SecureStorageInterface.SecureStorageError getSecureStorageErrorValue() {
        return new SecureStorageInterface.SecureStorageError();
    }

    void trackWithInfo(String pageName, String key, String value, boolean isTrackPage) {
        final HashMap<String, String> trackMap = new HashMap<>();
        trackMap.put(key, value);
        track(pageName, trackMap, isTrackPage);
    }

    /**
     * Sending the broadcast event .
     *
     * @param data Map consists of Tagging Data
     */
    @SuppressWarnings("unchecked")
    private void sendBroadcast(final Map data) {
        final Intent intent = new Intent(ACTION_TAGGING_DATA);
        intent.putExtra(EXTRA_TAGGING_DATA, (Serializable) data);
        LocalBroadcastManager.getInstance(mAppInfra.getAppInfraContext())
                .sendBroadcast(intent);
    }

    void taggingDataUnregister(final BroadcastReceiver receiver) {
        if (receiver != null && mAppInfra.getAppInfraContext() != null) {
            LocalBroadcastManager.getInstance(mAppInfra.getAppInfraContext())
                    .unregisterReceiver(receiver);
        } else {
            ((AppInfra) mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG,
                    AppInfraLogEventID.AI_TAGGING, "unregisterTaggingData" + "context is null");
        }
    }

    void taggingDataRegister(final BroadcastReceiver receiver) {
        if (receiver != null && mAppInfra.getAppInfraContext() != null) {
            LocalBroadcastManager.getInstance(mAppInfra.getAppInfraContext())
                    .registerReceiver(receiver, new IntentFilter(ACTION_TAGGING_DATA));
        } else {
            ((AppInfra) mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG,
                    AppInfraLogEventID.AI_TAGGING, "registerTaggingData" + "context is null");
        }
    }

    private String getComponentId() {
        return mComponentID;
    }

    private String getComponentVersionVersionValue() {
        return mComponentVersion;
    }

    void setComponentIdVersion(String mComponentID, String mComponentVersion) {
        this.mComponentID = mComponentID;
        this.mComponentVersion = mComponentVersion;
    }

    void setPrevPage(String prevPage) {
        AppTaggingHandler.prevPage = prevPage;
    }

    boolean enableAdobeLogs() {
        final AppConfigurationInterface.AppConfigurationError configError = new AppConfigurationInterface
                .AppConfigurationError();
        /*if (mAppInfra.getConfigInterface() != null) {*/
        try {
            final Object loglevelEnabled = mAppInfra.getConfigInterface().getPropertyForKey
                    ("enableAdobeLogs", "appinfra", configError);

            if (loglevelEnabled != null && loglevelEnabled instanceof Boolean) {
                final Boolean adobeLogLevelEnabled = (Boolean) loglevelEnabled;
                if (adobeLogLevelEnabled) {
                    Config.setDebugLogging(true);
                    return true;
                } else {
                    Config.setDebugLogging(false);
                    return false;
                }

            }

        } catch (Exception e) {
            if (mAppInfra != null) {
                ((AppInfra) mAppInfra).getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR,
                        AppInfraLogEventID.AI_TAGGING, "Error in Enable Adobe Log" + e.getMessage());
            }
        }
        return false;
    }

    // TODO for remove pagname and put internal usage
    void trackErrorActionWithCategory(@NonNull ErrorCategory errorCategory, Map trackMap, @NonNull TaggingError taggingErrorInstance, boolean isTrackPage) {
        String value;
        if (taggingErrorInstance.getErrorType() == null && taggingErrorInstance.getServerName() == null && taggingErrorInstance.getErrorCode() == null) {
            value = getComponentId()
                    + ":" + taggingErrorInstance.getErrorMsg();
        } else {
            value = getComponentId()
                    + ":" + taggingErrorInstance.getErrorType()
                    + ":" + taggingErrorInstance.getServerName()
                    + ":" + taggingErrorInstance.getErrorCode()
                    + ":" + taggingErrorInstance.getErrorMsg();
        }
        if (trackMap == null) {
            trackMap = new HashMap();
            trackMap.put(errorCategory.getValue(), value);

        } else
            trackMap.put(errorCategory.getValue(), value);
        track("sendData", trackMap, isTrackPage);
    }


    private static class PrivacyStatusCache {
        private static AppTaggingInterface.PrivacyStatus privacyStatus;

        static AppTaggingInterface.PrivacyStatus getPrivacyStatus() {
            return privacyStatus;
        }

        static void setPrivacyStatus(AppTaggingInterface.PrivacyStatus privacyStatus) {
            PrivacyStatusCache.privacyStatus = privacyStatus;
        }

        static boolean shouldTrack() {
            return privacyStatus != OPTOUT;
        }
    }
}
