/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */
package com.philips.platform.appinfra.tagging;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;

import com.adobe.mobile.Analytics;
import com.adobe.mobile.Config;
import com.adobe.mobile.Visitor;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.appinfra.consentmanager.ConsentManagerInterface;
import com.philips.platform.pif.chi.ConsentHandlerInterface;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * App Tagging classes .
 */
public class AppTagging implements AppTaggingInterface {

    private static final long serialVersionUID = 4532714074636578025L;
    static final String PAGE_NAME = "ailPageName";
    static final String ACTION_NAME = "ailActionName";
    static final String ACTION_TAGGING_DATA = "ACTION_TAGGING_DATA";
    static final String EXTRA_TAGGING_DATA = "TAGGING_DATA";
    static final String AIL_PRIVACY_CONSENT = "ailPrivacyConsentForSensitiveData";
    static final String CLICKSTREAM_CONSENT_TYPE = "AIL_ClickStream";
    private static String prevPage;
    private final AppInfraInterface mAppInfra;
    private final AnalyticsInterface analytics;
    private final Boolean isGoogleAnalyticsEnabled;
    private final Boolean isAdobeAnalyticsEnabled;
    protected String mComponentID;
    protected String mComponentVersion;
    private transient AppTaggingHandler appTaggingHandle;

    private ClickStreamConsentHandler mConsentHandler;


    public AppTagging(AppInfraInterface aAppInfra) {
        mAppInfra = aAppInfra;
        init(mAppInfra.getAppInfraContext());
        analytics = aAppInfra.getAnalytics();
        AppConfigurationInterface.AppConfigurationError configError = new AppConfigurationInterface.AppConfigurationError();
        isGoogleAnalyticsEnabled = (Boolean) aAppInfra.getConfigInterface().getPropertyForKey("GoogleAnalyticsEnabled", "analytics", configError);
        isAdobeAnalyticsEnabled = (Boolean) aAppInfra.getConfigInterface().getPropertyForKey("AdobeAnalyticsEnabled", "analytics", configError);
        // Class shall not presume appInfra to be completely initialized at this point.
        // At any call after the constructor, appInfra can be presumed to be complete.
    }

    private void init(Context context) {
        getAppTaggingHandler();
        Config.setContext(context);
        getAppTaggingHandler().enableAdobeLogs();
    }

    AppTaggingHandler getAppTaggingHandler() {
        if (appTaggingHandle == null) {
            return appTaggingHandle = new AppTaggingHandler(mAppInfra);
        }
        return appTaggingHandle;
    }


    @Override
    public String getTrackingIdentifier() {
        return Analytics.getTrackingIdentifier();
    }

    @Override
    public String getVisitorIDAppendToURL(String url) {
        return Visitor.appendToURL(url);
    }


    @Override
    public AppTaggingInterface createInstanceForComponent(String componentId, String componentVersion) {
       // if(isGoogleAnalyticsEnabled) return analytics.createInstanceForComponent(componentId,componentVersion);
        if(isAdobeAnalyticsEnabled) return new AppTaggingWrapper(mAppInfra, componentId, componentVersion);
        return new AppTaggingWrapper(mAppInfra, componentId, componentVersion);
    }

    @Override
    public PrivacyStatus getPrivacyConsent() {
        return getAppTaggingHandler().getMobilePrivacyStatus();
    }

    @Override
    public void setPrivacyConsent(PrivacyStatus privacyStatus) {
        getAppTaggingHandler().setPrivacyStatus(privacyStatus);
    }

    @Override
    public void trackTimedActionStart(String actionStart) {
        getAppTaggingHandler().timeActionStart(actionStart, null);
    }

    @Override
    public void trackTimedActionStart(String actionStart, Map<String, Object> contextData) {
        getAppTaggingHandler().timeActionStart(actionStart, contextData);
    }

    @Override
    public void trackTimedActionEnd(String actionEnd) {
        getAppTaggingHandler().timeActionEnd(actionEnd, null);
    }

    @Override
    public void trackTimedActionEnd(String actionEnd, Analytics.TimedActionBlock<Boolean> logic) {
        getAppTaggingHandler().timeActionEnd(actionEnd, logic);
    }

    @Override
    public boolean getPrivacyConsentForSensitiveData() {
        return getAppTaggingHandler().getPrivacyConsentSensitiveData();
    }

    // Sets the value of Privacy Consent For Sensitive Data and stores in preferences
    @Override
    public void setPrivacyConsentForSensitiveData(boolean valueContent) {
        getAppTaggingHandler().setPrivacyConsentSensitiveData(valueContent);
    }

    @Override
    public void trackPageWithInfo(String pageName, String key, String value) {
        if(isGoogleAnalyticsEnabled) analytics.trackPageWithInfo(pageName,key,value);
        if(isAdobeAnalyticsEnabled) getAppTaggingHandler().trackWithInfo(pageName, key, value, true);
    }

    @Override
    public void trackPageWithInfo(String pageName, Map<String, String> paramMap) {
        if(isGoogleAnalyticsEnabled) analytics.trackPageWithInfo(pageName,paramMap);
        if(isAdobeAnalyticsEnabled) getAppTaggingHandler().track(pageName, paramMap, true);
    }

    @Override
    public void trackActionWithInfo(String pageName, String key, String value) {
        if(isGoogleAnalyticsEnabled) analytics.trackActionWithInfo(pageName,key,value);
        if(isAdobeAnalyticsEnabled) getAppTaggingHandler().trackWithInfo(pageName, key, value, false);
    }

    @Override
    public void trackActionWithInfo(String pageName, Map<String, String> paramMap) {
        if(isGoogleAnalyticsEnabled) analytics.trackActionWithInfo(pageName,paramMap);
        if(isAdobeAnalyticsEnabled) getAppTaggingHandler().track(pageName, paramMap, false);
    }

    @Override
    public void collectLifecycleInfo(Activity context, Map<String, Object> paramDict) {
        Config.collectLifecycleData(context, paramDict);
    }

    @Override
    public void collectLifecycleInfo(Activity context) {
        Config.collectLifecycleData(context);
    }

    @Override
    public void pauseLifecycleInfo() {
        Config.pauseCollectingLifecycleData();
    }

    @Override
    public void trackVideoStart(String videoName) {
        trackActionWithInfo("videoStart", "videoName", videoName);
    }

    @Override
    public void trackVideoEnd(String videoName) {
        trackActionWithInfo("videoEnd", "videoName", videoName);
    }

    @Override
    public void trackSocialSharing(SocialMedium medium, String sharedItem) {
        socialSharing(medium, sharedItem);
    }

    @Override
    public void trackLinkExternal(String url) {
        trackActionWithInfo("sendData", "exitLinkName", url);
    }

    @Override
    public void trackFileDownload(String filename) {
        trackActionWithInfo("sendData", "fileName", filename);
    }

    @Override
    public void unregisterTaggingData(final BroadcastReceiver receiver) {
        getAppTaggingHandler().taggingDataUnregister(receiver);
    }

    @Override
    public void registerTaggingData(final BroadcastReceiver receiver) {
        getAppTaggingHandler().taggingDataRegister(receiver);
    }

    @Override
    public ConsentHandlerInterface getClickStreamConsentHandler() {
        if (mConsentHandler == null)
            mConsentHandler = new ClickStreamConsentHandler(mAppInfra);
        return mConsentHandler;
    }

    @Override
    public String getClickStreamConsentIdentifier() {
        return CLICKSTREAM_CONSENT_TYPE;
    }

    @Override
    public void registerClickStreamHandler(ConsentManagerInterface consentManager) {
        consentManager.deregisterHandler(Collections.singletonList(CLICKSTREAM_CONSENT_TYPE));
        consentManager.registerHandler(Collections.singletonList(CLICKSTREAM_CONSENT_TYPE), getClickStreamConsentHandler());
    }

    private void socialSharing(AppTaggingInterface.SocialMedium medium, String sharedItem) {
        final HashMap<String, String> trackMap = new HashMap<>();
        trackMap.put("socialItem", sharedItem);
        trackMap.put("socialType", medium.toString());
        trackActionWithInfo("socialShare", trackMap);
    }

    public void setComponentIdAndVersion(String mComponentID, String mComponentVersion) {
        getAppTaggingHandler().setComponentIdVersion(mComponentID, mComponentVersion);
    }


    @Override
    public void setPreviousPage(String previousPage) {
        prevPage = previousPage;
        getAppTaggingHandler().setPrevPage(previousPage);

    }
}
