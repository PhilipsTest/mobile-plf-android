/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */
package com.philips.platform.appinfra.tagging;

import android.app.Activity;
import android.content.BroadcastReceiver;

import com.adobe.mobile.Analytics;
import com.philips.platform.appinfra.consentmanager.ConsentManagerInterface;
import com.philips.platform.pif.chi.ConsentHandlerInterface;

import java.io.Serializable;
import java.util.Map;

/**
 * The interface Ai app tagging interface.
 */
public interface AppTaggingInterface extends Serializable {

    /**
     * Create instance for component ai app tagging interface.
     * This method to be used by all component to get their respective tagging
     *
     * @param componentId      the component id
     * @param componentVersion the component version
     * @return the appinfra app tagging interface
     * @since 1.0.0
     */
    AppTaggingInterface createInstanceForComponent(String componentId,
                                                   String componentVersion);

    /**
     * Sets previous page.
     *
     * @param previousPage the previous page
     * @since 1.0.0
     */
    void setPreviousPage(String previousPage);

    /**
     * Gets privacy consent.
     *
     * @return the privacy consent
     * @since 1.0.0
     */
    PrivacyStatus getPrivacyConsent();

    /**
     * Sets privacy consent.
     * OptIn - on setting OptIn hits are sent immediately
     * OptOut - on setting OptOut hits are discarded. Only App Lifecycle data is tracked
     * UnKnown - on setting UnKnown, If your report suite is timestamp-enabled,
     * hits are saved until the privacy status changes to opt-in (then hits are sent)
     * or opt-out (then hits are discarded). If your report suite is not timestamp-enabled,
     * hits are discarded until the privacy status changes to opt in
     *
     * @param privacyStatus the privacy status
     * @since 1.0.0
     */
    void setPrivacyConsent(PrivacyStatus privacyStatus);

    /**
     * Track page with info.
     *
     * @param pageName the page name
     * @param key      the key
     * @param value    the value
     * @since 1.0.0
     */
    void trackPageWithInfo(String pageName, String key, String value);

    /**
     * Track page with info.
     *
     * @param pageName  the page name
     * @param paramDict set of key/value pairs to be added to the tracking entry
     * @since 1.0.0
     */
    void trackPageWithInfo(String pageName, Map<String, String> paramDict);

    /**
     * Track action with info.
     *
     * @param pageName the page name
     * @param key      the key
     * @param value    the value
     * @since 1.0.0
     */
    void trackActionWithInfo(String pageName, String key, String value);

    /**
     * Track action with info.
     *
     * @param pageName  the page name
     * @param paramDict set of key/value pairs to be added to the tracking entry
     * @since 1.0.0
     */
    void trackActionWithInfo(String pageName, Map<String, String> paramDict);

    /**
     * Track error action with info.
     *
     * @param taggingErrorInstance the tagging error instance
     * @since 1.0.0
     */
//    void trackErrorAction(TaggingError taggingErrorInstance);

    /**
     * Track error action with info.
     *
     * @param errorCategory        the tagging error category
     * @param taggingErrorInstance the tagging error instance
     * @since 1.0.0
     */
    void trackErrorAction(ErrorCategory errorCategory, Map trackMap, TaggingError taggingErrorInstance);


    /**
     * Collect LifeCycle info.
     *
     * @param context   the page name
     * @param paramDict set of key/value pairs to be added to the tracking entry
     * @since 1.0.0
     */
    void collectLifecycleInfo(Activity context, Map<String, Object> paramDict);

    /**
     * Collect LifeCycle info.
     *
     * @param context the page name
     * @since 1.0.0
     */
    void collectLifecycleInfo(Activity context);

    /**
     * Pause LifeCycle info.
     *
     * @since 1.0.0
     */
    void pauseLifecycleInfo();

    /**
     * Track Video started with a videoName.
     *
     * @param videoName user friendly name of video being played.
     * @since 1.0.0
     */
    void trackVideoStart(String videoName);

    /**
     * Track Video End with a videoName.
     *
     * @param videoName user friendly name of video being ended.
     * @since 1.0.0
     */
    void trackVideoEnd(String videoName);

    /**
     * Track social sharing with social media like facebook, twitter, mail etc
     *
     * @param medium     SocialMedium=enum:{Facebook, Twitter, Mail, AirDrop}
     * @param sharedItem sharedItem is the object being shared
     * @since 1.0.0
     */
    void trackSocialSharing(SocialMedium medium, String sharedItem);

    /**
     * Track URL destination
     *
     * @param url url destination.
     * @since 1.0.0
     */
    void trackLinkExternal(String url);

    /**
     * Track file being downloaded.
     *
     * @param filename String filename.
     * @since 1.0.0
     */
    void trackFileDownload(String filename);

    /**
     * Track Timed Action Start.
     *
     * @param actionStart String filename.
     * @since 1.0.0
     */
    void trackTimedActionStart(String actionStart);

    /**
     * Track Timed Action Start.
     *
     * @param actionStart String filename.
     * @param contextData set of key/value pairs to be added to the tracking entry
     * @since 18.2.0
     */
    void trackTimedActionStart(String actionStart, Map<String, Object> contextData);

    /**
     * Track Timed Action end.
     *
     * @param actionEnd String filename.
     * @since 1.0.0
     */
    void trackTimedActionEnd(String actionEnd);

    /**
     * Track Timed Action end.
     *
     * @param actionEnd String filename.
     * @param logic     - Pass analytics timed action block
     * @since 18.2.0
     */

    void trackTimedActionEnd(String actionEnd, Analytics.TimedActionBlock<Boolean> logic);

    /**
     * get Privacy Consent For SensitiveData.
     *
     * @return returns consent value true or false
     * @since 1.0.0
     */
    boolean getPrivacyConsentForSensitiveData();

    /**
     * sets Privacy Consent For SensitiveData.
     *
     * @param valueContent String filename.
     * @since 1.0.0
     */
    void setPrivacyConsentForSensitiveData(boolean valueContent);

    /**
     * @return an String value containing the tracking identifier
     * @brief Retrieves the analytics tracking identifier
     * @note This method can cause a blocking network call and should not be used from a UI thread.
     * @since 1.0.0
     */
    String getTrackingIdentifier();

    /**
     * @return an String value containing the Visitor identifier to append with given URL
     * @brief Retrieves the analytics Visitor identifier
     * @note This method can cause a blocking network call and should not be used from a UI thread.
     * @since 1904
     */
    String getVisitorIDAppendToURL(String url);

    /**
     * UnRegister for the Tagging data .
     *
     * @param receiver BroadcastReceiver
     * @since 1.0.0
     */
    void unregisterTaggingData(BroadcastReceiver receiver);

    /**
     * Register for the Tagging data.
     *
     * @param receiver BroadcastReceiver
     * @since 1.0.0
     */
    void registerTaggingData(BroadcastReceiver receiver);

    /**
     * The enum Privacy status.
     */
    enum PrivacyStatus {
        OPTIN, OPTOUT, UNKNOWN
    }

    /**
     * Retrieves the handler which handles Click Stream Consent
     * ClickStreamConsentHandler is used for storing/fetching Tagging Consents in Adobe
     *
     * @return This method returns the Click Stream Consent Handler
     * @since 1801.0
     */
    ConsentHandlerInterface getClickStreamConsentHandler();

    /**
     * This gives the Click Stream key without which ClickStreamConsentHandler will not function.
     * Only Consent Definitions containing this key will be considered for Click Stream Handling.
     * If no such key is found then the app will crash.
     *
     * @return This method returns the Click Stream Consent Identifier
     * @since 1801.0
     */
    String getClickStreamConsentIdentifier();

    /**
     * Register ClickStreamHandler.
     *
     * @param consentManager ConsentManager
     * @since 1801.0
     */
    void registerClickStreamHandler(ConsentManagerInterface consentManager);

    enum SocialMedium {
        Facebook("facebook"),
        Twitter("twitter"),
        Mail("mail"),
        AirDrop("airdrop");
        private final String socialMedium;

        SocialMedium(String socialMedium) {
            this.socialMedium = socialMedium;
        }

        public String toString() {
            return this.socialMedium;
        }
    }
}


