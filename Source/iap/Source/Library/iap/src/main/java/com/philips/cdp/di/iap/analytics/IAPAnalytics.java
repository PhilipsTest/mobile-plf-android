/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.analytics;

import android.app.Activity;

import com.philips.cdp.di.iap.integration.IAPDependencies;
import com.philips.cdp.di.iap.utils.IAPLog;
import com.philips.platform.appinfra.BuildConfig;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;

import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant.IAP_COUNTRY;
import static com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant.IAP_CURRENCY;

public class IAPAnalytics {
    public static AppTaggingInterface sAppTaggingInterface;
    public static String mCountryCode="";
    public static String mCurrencyCode="";
    public static String mCategory="";

    private IAPAnalytics(){

    }

    public static void initIAPAnalytics(IAPDependencies dependencies) {
        sAppTaggingInterface =
                dependencies.getAppInfra().getTagging().
                        createInstanceForComponent(IAPAnalyticsConstant.COMPONENT_NAME, BuildConfig.VERSION_NAME);
        mCountryCode=dependencies.getAppInfra().getServiceDiscovery().getHomeCountry();

    }

    public static void trackPage(String currentPage) {
        if (sAppTaggingInterface != null) {
            Map<String, String> map = new HashMap<>();
            sAppTaggingInterface.trackPageWithInfo(currentPage, addCountryAndCurrency(map));
        }
    }

    public static void trackAction(String state, String key, Object value) {
        String valueObject = (String) value;
        IAPLog.i(IAPLog.LOG, "trackAction" + valueObject);
        if (sAppTaggingInterface != null)
            sAppTaggingInterface.
                    trackActionWithInfo(state, key, valueObject);
    }

    public static void trackErrorWithPrefix(String state, String key, Object value) {
        String valueObject = "IAP:".concat((String) value);
        IAPLog.i(IAPLog.LOG, "trackAction" + valueObject);
        if (sAppTaggingInterface != null)
            sAppTaggingInterface.
                    trackActionWithInfo(state, key, valueObject);
    }


    public static void trackMultipleActions(String state, Map<String, String> map) {
        if (sAppTaggingInterface != null)
            sAppTaggingInterface.
                    trackActionWithInfo(state, addCountryAndCurrency(map));
    }

    public static void pauseCollectingLifecycleData() {
        if (sAppTaggingInterface != null)
            sAppTaggingInterface.pauseLifecycleInfo();
    }

    public static void collectLifecycleData(Activity activity) {
        if (sAppTaggingInterface != null)
            sAppTaggingInterface.collectLifecycleInfo(activity);
    }

    public static void clearAppTaggingInterface() {
        sAppTaggingInterface = null;
    }

    public static Map<String,String> addCountryAndCurrency(Map<String,String> map) {
        map.put(IAP_COUNTRY,mCountryCode);
        map.put(IAP_CURRENCY,mCurrencyCode);
        return map;

    }

    public static void setCurrencyString(String localeString){
        try {
            String[] localeArray = localeString.split("_");
            Locale locale = new Locale(localeArray[0], localeArray[1]);
            Currency currency = Currency.getInstance(locale);
            mCurrencyCode = currency.getCurrencyCode();
        }catch(Exception e){

        }

    }


}
