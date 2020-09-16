package com.philips.platform.appinfra;

import android.content.Context;

import com.philips.platform.appinfra.abtestclient.ABTestClientInterface;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.appinfra.appidentity.AppIdentityInterface;
import com.philips.platform.appinfra.appupdate.AppUpdateInterface;
import com.philips.platform.appinfra.consentmanager.ConsentManagerInterface;
import com.philips.platform.appinfra.consentmanager.consenthandler.DeviceStoredConsentHandler;
import com.philips.platform.appinfra.internationalization.InternationalizationInterface;
import com.philips.platform.appinfra.languagepack.LanguagePackInterface;
import com.philips.platform.appinfra.logging.CloudLoggingInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.rest.RestInterface;
import com.philips.platform.appinfra.securestorage.SecureStorageInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.tagging.AnalyticsInterface;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;
import com.philips.platform.appinfra.timesync.TimeInterface;

import java.io.Serializable;

/**
 * App Infra provides a range of modules that are the basis for any mobile application
 * App Infra provides various utility services for the application such as Logging, Tagging, Service discovery etc..
 */
public interface AppInfraInterface extends Serializable {


    /**
     * Gets secure storage.
     *
     * @return the secure storage
     */
    SecureStorageInterface getSecureStorage();

    /**
     * Gets app identity.
     *
     * @return the app identity
     */
    AppIdentityInterface getAppIdentity();

    /**
     * Gets local.
     *
     * @return the local
     */
    InternationalizationInterface getInternationalization();

    /**
     * Gets logging.
     *
     * @return the logging
     */
    LoggingInterface getLogging();

    /**
     * Gets cloud logging.
     *
     * @return the  cloud logging
     */
    CloudLoggingInterface getCloudLogging();

    /**
     * Gets service discovery interface.
     *
     * @return the service discovery interface
     */
    ServiceDiscoveryInterface getServiceDiscovery();

    /**
     * Gets tagging.
     *
     * @return the tagging
     */
    AppTaggingInterface getTagging();

    /**
     * Gets tagging.
     *
     * @return the tagging
     */

    /**
     * Gets time sync.
     *
     * @return the time sync
     */
    TimeInterface getTime();

    /**
     * Gets time sync.
     *
     * @return the config
     */
    AppConfigurationInterface getConfigInterface();

    /**
     * Gets REST API Manager.
     *
     * @return the config
     */
    RestInterface getRestClient();

    /**
     * Gets the A/B testing Manager.
     *
     * @return the abTesting
     */
    ABTestClientInterface getAbTesting();

    /**
     * Gets the language pack Manager.
     *
     * @return the language pack
     */
    LanguagePackInterface getLanguagePack();

    /**
     * Gets the appupdate interface.
     *
     * @return appupdate interface
     */
    AppUpdateInterface getAppUpdate();



    /**
     * Gets ConsentManager.
     *
     * @return the ConsentManager
     */
    ConsentManagerInterface getConsentManager();

    /**
     * Get device storage consent handler
     * @return the DeviceStoredConsentHandler
     * @since 1802.0
     */
    DeviceStoredConsentHandler getDeviceStoredConsentHandler();

    /**
     * Get Context of Appinfra
     * @return - the context
     * @since 1804.0
     */
    Context getAppInfraContext();

    AnalyticsInterface getAnalytics();

    void  setAnalytics(AnalyticsInterface analytics);

}
