package com.philips.cdp.prodreg.tagging;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.adobe.mobile.Analytics;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.tagging.AIAppTaggingInterface;

import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class TaggingSupport {

    private static TaggingSupport taggingSupport;
    private static AIAppTaggingInterface aiAppTaggingInterface;
    private static Context context;

    private TaggingSupport() {
    }

    public static TaggingSupport getInstance(Context context) {
        if (taggingSupport == null) {
            TaggingSupport.context = context;
            taggingSupport = new TaggingSupport();
            aiAppTaggingInterface = new AppInfra.Builder().build(context).getTagging();
        }
        return taggingSupport;
    }

    private static String getCurrency() {
        Currency currency = Currency.getInstance(Locale.getDefault());
        String currencyCode = currency.getCurrencyCode();
        if (currencyCode == null)
            currencyCode = AnalyticsConstants.KEY_CURRENCY;
        return currencyCode;
    }

    private static int getAppVersion() {
        int appVersion;
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            appVersion = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
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

    public AIAppTaggingInterface getAiAppTaggingInterface() {
        return aiAppTaggingInterface;
    }

    /*
     * This context data will be called for every page track.
     */
    public Map<String, String> getCommonGoalsMap() {
        Map<String, String> contextData = new HashMap<>();
        contextData.put(AnalyticsConstants.KEY_APP_NAME,
                AnalyticsConstants.ACTION_VALUE_APP_NAME);
        contextData.put(AnalyticsConstants.KEY_VERSION, String.valueOf(getAppVersion()));
        contextData
                .put(AnalyticsConstants.KEY_OS,
                        AnalyticsConstants.ACTION_VALUE_ANDROID
                                + Build.VERSION.RELEASE);
        contextData.put(AnalyticsConstants.KEY_COUNTRY,
                Locale.getDefault().getCountry());
        contextData.put(AnalyticsConstants.KEY_LANGUAGE,
                Locale.getDefault().getLanguage());
        contextData.put(AnalyticsConstants.KEY_CURRENCY, getCurrency());
        contextData.put(AnalyticsConstants.KEY_TIME_STAMP, getTimestamp());
        contextData.put(AnalyticsConstants.KEY_APP_ID,
                Analytics.getTrackingIdentifier());
        contextData.put(AnalyticsConstants.APP_SOURCE,
                AnalyticsConstants.APP_SOURCE_VALUE);
        return contextData;
    }

    public void trackPageWithCommonGoals(String pageName, Map<String, String> trackInfo) {
        final Map<String, String> commonGoalsMap = getCommonGoalsMap();
        commonGoalsMap.putAll(trackInfo);
        getAiAppTaggingInterface().trackPageWithInfo(pageName, commonGoalsMap);
    }

    public void trackPageWithCommonGoals(String pageName, String key, String value) {
        final Map<String, String> commonGoalsMap = getCommonGoalsMap();
        commonGoalsMap.put(key, value);
        getAiAppTaggingInterface().trackPageWithInfo(pageName, commonGoalsMap);
    }

    public void trackOnlyPage(String pageName, Map<String, String> trackInfo) {
        getAiAppTaggingInterface().trackPageWithInfo(pageName, trackInfo);
    }

    public void trackOnlyPage(String pageName, String key, String value) {
        final Map<String, String> hashMap = new HashMap<>();
        hashMap.put(key, value);
        getAiAppTaggingInterface().trackPageWithInfo(pageName, hashMap);
    }
}
