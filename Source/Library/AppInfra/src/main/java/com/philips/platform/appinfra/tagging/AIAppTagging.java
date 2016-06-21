/* Copyright (c) Koninklijke Philips N.V. 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.appinfra.tagging;

import android.content.Context;

import com.adobe.mobile.Analytics;
import com.adobe.mobile.Config;
import com.adobe.mobile.MobilePrivacyStatus;
import com.philips.platform.appinfra.AppInfra;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class AIAppTagging implements AIAppTaggingInterface {
    private static String componentVersionKey;

    private static String newFieldKey;

    private static String newFieldValue;

    private static String componentVersionVersionValue ;
    private static String mLanguage;
//    private static String mCountry;
    private static String mAppsIdkey;
    private static String mLocalTimestamp;
    private static String mUTCTimestamp;
    private static String prevPage;


    private AppInfra mAppInfra;
    protected String mComponentID;
    protected String mComponentVersion;

    private static String[] defaultValues = {
            AIAppTaggingConstants.LANGUAGE_KEY,
            AIAppTaggingConstants.APPSID_KEY,
            AIAppTaggingConstants.COMPONENT_ID,
            AIAppTaggingConstants.COMPONENT_VERSION,

            AIAppTaggingConstants.UTC_TIMESTAMP_KEY


    };


//    private static String mCurruncy;

    private static Locale mlocale;

    private static Context mcontext;

    private static String mAppName;
    private static Map<String, Object> contextData;

    public AIAppTagging(AppInfra aAppInfra) {
        mAppInfra = aAppInfra;
        // Class shall not presume appInfra to be completely initialized at this point.
        // At any call after the constructor, appInfra can be presumed to be complete.

    }

    public static void init(Locale locale, Context context,String appName){
        mlocale = locale;
        mcontext = context;
        prevPage = appName;
        Config.setContext(context);
//        contextData = addAnalyticsDataObject();

        if(appName == null){
            throw new RuntimeException("Please set app name for tagging library");
        }
    }



    public static void setDebuggable(final boolean enable){
        Config.setDebugLogging(enable);
    }


    public static void getPrivacyStatus(){
        Config.getPrivacyStatus();
    }



    private static Map<String, Object> addAnalyticsDataObject() {
        Map<String, Object> contextData = new HashMap<String, Object>();

        contextData.put(AIAppTaggingConstants.LANGUAGE_KEY, getLanguage());
//        contextData.put(AIAppTaggingConstants.CURRENCY_KEY, getCurrency());

        contextData.put(AIAppTaggingConstants.APPSID_KEY, getAppsId());
        contextData.put(AIAppTaggingConstants.COMPONENT_ID, getComponentId());
        contextData.put(AIAppTaggingConstants.COMPONENT_VERSION, getComponentVersionVersionValue());
        contextData.put(AIAppTaggingConstants.LOCAL_TIMESTAMP_KEY, getLocalTimestamp());
        contextData.put(AIAppTaggingConstants.UTC_TIMESTAMP_KEY, getUTCTimestamp());
        if (null != getNewKey() && null != getNewValue()) {
//            contextData.put(getComponentVersionKey(), getComponentVersionVersionValue());

            if(getNewKey().contains(",") && getNewValue().contains(",") ){

            }else{
                contextData.put(getNewKey(), getNewValue());
            }

        }

        return contextData;
    }
    private static String getAppsId(){
        if(mAppsIdkey == null){
            mAppsIdkey= Analytics.getTrackingIdentifier();
        }

        return mAppsIdkey;
    }

    private static void setNewKey(String newFieldkey) {
        AIAppTagging.newFieldKey = newFieldkey;

    }
    private static void setNewValue(String newFieldvalue) {
        AIAppTagging.newFieldValue = newFieldvalue;
    }
    private static String getNewKey(){
        return newFieldKey;
    }
    private static String getNewValue(){
        return newFieldValue;
    }

    private static String getLanguage(){
        if(mLanguage == null){
            mLanguage = mlocale.getLanguage();
        }
        return mLanguage;

    }

    private static void setAppsIdkeyOverridden(String appsIdkey) {
        AIAppTagging.mAppsIdkey = appsIdkey;
    }

    private static String getUTCTimestamp() {

        if(mLocalTimestamp == null){
            DateFormat df = DateFormat.getTimeInstance();
            df.setTimeZone(TimeZone.getTimeZone("gmt"));
            String utcTime = df.format(new Date());
            mLocalTimestamp = utcTime;
        }


        return mLocalTimestamp;
    }

    private static String getLocalTimestamp() {


        if(mUTCTimestamp == null){
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = df.format(c.getTime());
            mUTCTimestamp = formattedDate;
        }


        return mUTCTimestamp;
    }

    public static String getComponentId() {
        if(componentVersionKey == null){
            componentVersionKey = "DefaultText";
        }
        return componentVersionKey;
    }

    public static void setComponentID(String componentID) {
        AIAppTagging.componentVersionKey = componentID;
    }

    public static String getComponentVersionVersionValue() {
        if(componentVersionVersionValue == null){
            componentVersionVersionValue = "DefalutValue";
        }
        return componentVersionVersionValue;
    }

    public static void setComponentVersionVersionValue(String componentVersionVersionValue) {
        AIAppTagging.componentVersionVersionValue = componentVersionVersionValue;
    }

    /**
     * Create instance for component tagging interface.
     * This method to be used by all component to get their respective tagging
     * @param componentId      the component id
     * @param componentVersion the component version
     * @return the appinfra app tagging interface
     */
    @Override
    public AIAppTaggingInterface createInstanceForComponent(String componentId, String componentVersion) {
        return new AIAppTaggingWrapper(mAppInfra, componentId, componentVersion);
    }

     /**
     * Sets privacy consent.
     *
     * @param privacyStatus the privacy status
     */
    @Override
    public void setPrivacyConsent(PrivacyStatus privacyStatus) {
        switch (privacyStatus) {
            case OPTIN:
                Config.setPrivacyStatus(MobilePrivacyStatus.MOBILE_PRIVACY_STATUS_OPT_IN);
                break;
            case OPTOUT:
                Config.setPrivacyStatus(MobilePrivacyStatus.MOBILE_PRIVACY_STATUS_OPT_OUT);

                break;
            case UNKNOWN:
                Config.setPrivacyStatus(MobilePrivacyStatus.MOBILE_PRIVACY_STATUS_UNKNOWN);
                break;

        }


    }

    /**
     * Gets privacy consent.
     *
     * @return the privacy consent
     */
    @Override
    public PrivacyStatus getPrivacyConsent() {
        return null;
    }

    /**
     * Track page with info with single key value.
     *
     * @param pageName the page name
     * @param key      the key
     * @param value    the value
     */
    @Override
    public void trackPageWithInfo(String pageName, String key, String value) {
        contextData = addAnalyticsDataObject();

        if(Arrays.asList(defaultValues).contains(key)){

            switch (key){

                case AIAppTaggingConstants.COMPONENT_ID:

                    contextData.put(AIAppTaggingConstants.COMPONENT_ID, value);
                    setComponentID(value);
                    break;
                case AIAppTaggingConstants.COMPONENT_VERSION:
                    contextData.put(AIAppTaggingConstants.COMPONENT_VERSION, value);
                    setComponentVersionVersionValue(value);
                    break;

            }


        }else{
            setNewKey(key);
            setNewValue(value);
            contextData = addAnalyticsDataObject();
        }
        if (null != prevPage) {
            contextData.put(AIAppTaggingConstants.PREVIOUS_PAGE_NAME, prevPage);
        }
        Analytics.trackState(pageName, contextData);

        prevPage = pageName;

    }

    /**
     * Track page with info with multiple key value.
     *
     * @param pageName  the page name
     * @param paramDict the param dict
     */
    @Override
    public void trackPageWithInfo(String pageName, Map<String, String> paramMap) {
        Map<String, Object> contextData = addAnalyticsDataObject();
        contextData.putAll(paramMap);
        if (null != prevPage) {
            contextData.put(AIAppTaggingConstants.PREVIOUS_PAGE_NAME, prevPage);
        }
        Analytics.trackState(pageName, contextData);

        prevPage = pageName;

    }

    /**
     * Track action with info with single key value.
     *
     * @param pageName the page name
     * @param key      the key
     * @param value    the value
     */
    @Override
    public void trackActionWithInfo(String pageName, String key, String value) {
        Map<String, Object> contextData = addAnalyticsDataObject();
        if (null != key) {

            contextData.put(key, value);
        }
        if (null != prevPage) {
            contextData.put(AIAppTaggingConstants.PREVIOUS_PAGE_NAME, prevPage);
        }
        Analytics.trackAction(pageName, contextData);

        prevPage = pageName;
    }

    /**
     * Track action with info with multiple key value.
     *
     * @param pageName  the page name
     * @param paramDict the param dict
     */
    @Override
    public void trackActionWithInfo(String pageName, Map<String, String> paramMap) {
        Map<String, Object> contextData = addAnalyticsDataObject();

        if(null!=paramMap) {
            try {
                Map<String, Object> tmp = new HashMap<String, Object>(paramMap);
                tmp.keySet().removeAll(contextData.keySet());
                contextData.putAll(paramMap);

                if (null != prevPage) {
                    contextData.put(AIAppTaggingConstants.PREVIOUS_PAGE_NAME, prevPage);
                }
                Analytics.trackAction(pageName, contextData);
                prevPage = pageName;
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

}
