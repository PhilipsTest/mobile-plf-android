
package com.philips.cdp.registration.analytics;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.adobe.mobile.Analytics;
import com.philips.cdp.registration.settings.RegistrationHelper;

public class AnalyticsUtils {

    private static String prevPage = null;

    public static void trackPage(String currPage) {
        if(!RegistrationHelper.getInstance().isTagginEnabled()){
            return;
        }
        Map<String, Object> contextData = addAnalyticsDataObject();
        if (null != prevPage && currPage.toString()!=AnalyticsPages.USER_PROFILE) {
            contextData.put(AnalyticsConstants.PREVIOUS_PAGE_NAME, prevPage);
        }
        Analytics.trackState(currPage, contextData);
        prevPage = currPage;
    }

    public static void trackFirstPage(String currPage) {
            if(!RegistrationHelper.getInstance().isTagginEnabled()){
            return;
        }
        Map<String, Object> contextData = addAnalyticsDataObject();
        Analytics.trackState(currPage, contextData);
        prevPage = currPage;
    }

    public static void trackAction(String state, String key, Object value) {
        if(!RegistrationHelper.getInstance().isTagginEnabled()){
            return;
        }
        Map<String, Object> contextData = addAnalyticsDataObject();
        if (null != key) {
            contextData.put(key, value);
        }
        Analytics.trackAction(state, contextData);
    }

    public static void trackMultipleActions(String state, Map<String, Object> map) {
        if(!RegistrationHelper.getInstance().isTagginEnabled()){
            return;
        }
        Map<String, Object> contextData = addAnalyticsDataObject();
        contextData.putAll(map);
        Analytics.trackAction(state, contextData);
    }

    private static Map<String, Object> addAnalyticsDataObject() {

        Map<String, Object> contextData = new HashMap<String, Object>();
        contextData.put(AnalyticsConstants.CP_KEY, AnalyticsConstants.CP_VALUE);
        contextData.put(AnalyticsConstants.APPNAME_KEY, AnalyticsConstants.APPNAME_VALUE);
        contextData.put(AnalyticsConstants.VERSION_KEY, RegistrationHelper.getInstance()
                .getAppVersion());
        contextData.put(AnalyticsConstants.OS_KEY, AnalyticsConstants.OS_ANDROID);
        contextData.put(AnalyticsConstants.COUNTRY_KEY, RegistrationHelper.getInstance()
                .getLocale().getCountry());
        contextData.put(AnalyticsConstants.LANGUAGE_KEY, RegistrationHelper.getInstance()
                .getLocale().getLanguage());
        contextData.put(AnalyticsConstants.CURRENCY_KEY, getCurrency());
        contextData.put(AnalyticsConstants.APPSID_KEY, RegistrationHelper.getInstance().getAnalyticsAppId());
        contextData.put(AnalyticsConstants.TIMESTAMP_KEY, getTimestamp());

        return contextData;
    }

    private static String getCurrency() {
        Currency currency = Currency.getInstance(Locale.getDefault());
        String currencyCode = currency.getCurrencyCode();
        if (currencyCode == null)
            currencyCode = AnalyticsConstants.DEFAULT_CURRENCY;
        return currencyCode;
}

    private static String getTimestamp() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }
}
