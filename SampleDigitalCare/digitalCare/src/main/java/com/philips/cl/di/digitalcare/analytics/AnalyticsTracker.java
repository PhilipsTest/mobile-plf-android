package com.philips.cl.di.digitalcare.analytics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

import com.adobe.mobile.Analytics;
import com.adobe.mobile.Config;
import com.philips.cl.di.digitalcare.DigitalCareConfigManager;
import com.philips.cl.di.digitalcare.util.DigiCareLogger;

import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * AnalyticsTracker class is responsible for Adobe Analytics. Here all APIs has
 * been added to call for page/event tagging.
 *
 * @author: ritesh.jha@philips.com
 * @since: Mar 25, 2015
 */
public class AnalyticsTracker {

    private static final String TAG = "DigitalCare:Analytics";
    private static boolean trackMetrics = false;
    private static Context mContext = null;

    /**
     * Analytics: Tagging can be enabled disabled.
     */
    public static void isEnable(boolean enable) {
        trackMetrics = enable;
    }

    /**
     * Analytics: Initialize with the context. After initialization only
     * TAGGIN'S APIs can be used.
     */
    public static void initContext(Context context) {
        if (!trackMetrics)
            return;
        Config.setContext(context);
        mContext = context;
    }

    /**
     * Analytics: This needs to call on onResume() of every activity.
     */
    public static void startCollectLifecycleData() {
        if (!trackMetrics)
            return;
        new Thread() {
            public void run() {
                Config.collectLifecycleData();
            }

            ;
        };
    }

    /**
     * Analytics: This needs to call on onPause() of every activity.
     */
    public static void stopCollectLifecycleData() {
        if (!trackMetrics)
            return;
        new Thread() {
            public void run() {
                Config.pauseCollectingLifecycleData();
            }

            ;
        };
    }

    public static void trackPage(String pageName, String previousPageName) {
        if (!trackMetrics)
            return;
        DigiCareLogger.i(TAG, "Track page :" + pageName);
        Analytics.trackState(pageName, addPageContextData(previousPageName));
    }

    public static void trackPage(String pageName, String previousPageName, Map<String, Object> contextData) {
        if (!trackMetrics)
            return;
        DigiCareLogger.i(TAG, "Track page :" + pageName);
        Analytics.trackState(pageName, addPageContextData(previousPageName, contextData));
    }

    /**
     * Tracking action for events.
     *
     * @param actionName : Name of the action.
     * @param mapKey     : Key field in the Map.
     * @param mapValue   : Value field in the Map.
     */
    public static void trackAction(String actionName, String mapKey,
                                   String mapValue) {
        if (!trackMetrics)
            return;
        DigiCareLogger.i(TAG, "TrackAction : actionName : " + actionName);
        Analytics.trackAction(actionName,
                addActionContextData(mapKey, mapValue));
    }

    public static void trackAction(String actionName, Map<String, Object> contextData) {
        if (!trackMetrics)
            return;
        DigiCareLogger.i(TAG, "TrackAction : actionName : " + actionName);
        contextData.put(AnalyticsConstants.KEY_TIME_STAMP, getTimestamp());
        Analytics.trackAction(actionName, contextData);
    }

    private static Map<String, Object> addActionContextData(String mapKey,
                                                            String mapValue) {
        Map<String, Object> contextData = new HashMap<String, Object>();
        contextData.put(AnalyticsConstants.KEY_TIME_STAMP, getTimestamp());
        contextData.put(mapKey, mapValue);
        return contextData;
    }

    /*
     * This context data will be called for every page track.
     */
    private static Map<String, Object> addPageContextData(
            String previousPageName) {
        Map<String, Object> contextData = new HashMap<String, Object>();
        contextData.put(AnalyticsConstants.KEY_APPNAME,
                AnalyticsConstants.ACTION_VALUE_APPNAME);
        contextData.put(AnalyticsConstants.KEY_VERSION, getAppVersion());
        contextData
                .put(AnalyticsConstants.KEY_OS,
                        AnalyticsConstants.ACTION_VALUE_ANDROID
                                + Build.VERSION.RELEASE);
        contextData.put(AnalyticsConstants.KEY_COUNTRY,
                DigitalCareConfigManager.getInstance().getLocale()
                        .getCountry());
        contextData.put(AnalyticsConstants.KEY_LANGUAGE,
                DigitalCareConfigManager.getInstance().getLocale()
                        .getLanguage());
        contextData.put(AnalyticsConstants.KEY_CURRENCY, getCurrency());
        contextData.put(AnalyticsConstants.KEY_PREVIOUS_PAGENAME,
                previousPageName);
        contextData.put(AnalyticsConstants.KEY_TIME_STAMP, getTimestamp());
        contextData.put(AnalyticsConstants.KEY_APP_ID,
                Analytics.getTrackingIdentifier());
        return contextData;
    }

    /*
     * This context data will be called for every page track if additional Map object is there.
     */
    private static Map<String, Object> addPageContextData(String previousPageName, Map<String, Object> contextData) {
        Map<String, Object> contextDataNew = AnalyticsTracker.addPageContextData(previousPageName);
        contextData.putAll(contextDataNew);
        return contextData;
    }

    private static String getCurrency() {
        Currency currency = Currency.getInstance(DigitalCareConfigManager.getInstance().getLocale());
        String currencyCode = currency.getCurrencyCode();
        if (currencyCode == null)
            currencyCode = AnalyticsConstants.KEY_CURRENCY;
        return currencyCode;
    }

    private static int getAppVersion() {
        int appVersion = 0;
        try {
            PackageInfo packageInfo = mContext.getPackageManager()
                    .getPackageInfo(mContext.getPackageName(), 0);
            DigiCareLogger.i(DigiCareLogger.APPLICATION,
                    "Application version: " + packageInfo.versionName + " ("
                            + packageInfo.versionCode + ")");
            appVersion = packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
        return appVersion;
    }

    @SuppressLint("SimpleDateFormat")
    private static String getTimestamp() {
        long timeMillis = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String date = dateFormat.format(new Date(timeMillis));
        return date;
    }
}
