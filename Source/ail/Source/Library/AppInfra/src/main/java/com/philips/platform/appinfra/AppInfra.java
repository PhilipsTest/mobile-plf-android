/* Copyright (c) Koninklijke Philips N.V. 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.appinfra;

import android.content.Context;
import android.os.Build;

import com.philips.platform.appinfra.abtestclient.ABTestClientInterface;
import com.philips.platform.appinfra.aikm.AIKMInterface;
import com.philips.platform.appinfra.aikm.AIKManager;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationManager;
import com.philips.platform.appinfra.appidentity.AppIdentityInterface;
import com.philips.platform.appinfra.appidentity.AppIdentityManager;
import com.philips.platform.appinfra.appupdate.AppUpdateInterface;
import com.philips.platform.appinfra.appupdate.AppUpdateManager;
import com.philips.platform.appinfra.consentmanager.ConsentManager;
import com.philips.platform.appinfra.consentmanager.ConsentManagerInterface;
import com.philips.platform.appinfra.consentmanager.consenthandler.DeviceStoredConsentHandler;
import com.philips.platform.appinfra.internationalization.InternationalizationInterface;
import com.philips.platform.appinfra.internationalization.InternationalizationManager;
import com.philips.platform.appinfra.languagepack.LanguagePackInterface;
import com.philips.platform.appinfra.languagepack.LanguagePackManager;
import com.philips.platform.appinfra.logging.AppInfraLogging;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.rest.RestInterface;
import com.philips.platform.appinfra.rest.RestManager;
import com.philips.platform.appinfra.securestorage.SecureStorage;
import com.philips.platform.appinfra.securestorage.SecureStorageInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryManager;
import com.philips.platform.appinfra.tagging.AppTagging;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;
import com.philips.platform.appinfra.timesync.TimeInterface;
import com.philips.platform.appinfra.timesync.TimeSyncSntpClient;

import java.io.Serializable;


/**
 * The AppInfra Base class, here using builder design pattern to create object .
 */
public class AppInfra implements AppInfraInterface, ComponentVersionInfo, Serializable {

    private static final long serialVersionUID = -5261016522164936691L;
    private SecureStorageInterface secureStorage;

    private LoggingInterface logger;
    private DeviceStoredConsentHandler deviceStoredConsentHandler;
    private AppTaggingInterface tagging;
    private LoggingInterface appInfraLogger;
    private AppTaggingInterface appInfraTagging;
    private AppIdentityInterface appIdentity;
    private InternationalizationInterface local;
    private ServiceDiscoveryInterface mServiceDiscoveryInterface;
    private TimeInterface mTimeSyncInterface;
    private AppConfigurationInterface configInterface;
    private RestInterface mRestInterface;
    private ABTestClientInterface mAbtesting;
    private AppUpdateInterface mAppupdateInterface;
    private ConsentManagerInterface consentManager;
    private RxBus rxBus = null;


    /**
     * The App infra context. This MUST be Application context
     */
    private Context appInfraContext;
    private LanguagePackInterface mLanguagePackInterface;
    private AIKMInterface aikmInterface;


    private AppInfra(Context pContext) {
        appInfraContext = pContext;
    }

    private static void postLog(AppInfra ai, long startTime, String message) {
        long endTime = System.currentTimeMillis();
        long methodDuration = (endTime - startTime);
        ai.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, AppInfraLogEventID.AI_APPINFRA,
                message + methodDuration);
    }

    @Override
    public Context getAppInfraContext() {
        return appInfraContext;
    }

    @Override
    public TimeInterface getTime() {
        return mTimeSyncInterface;
    }

    private void setTime(TimeInterface mTimeSyncInterface) {
        this.mTimeSyncInterface = mTimeSyncInterface;
    }

    @Override
    public SecureStorageInterface getSecureStorage() {
        return secureStorage;
    }

    private void setSecureStorage(SecureStorageInterface sec) {
        secureStorage = sec;
    }

    @Override
    public AppIdentityInterface getAppIdentity() {
        return appIdentity;
    }

    private void setAppIdentity(AppIdentityInterface identity) {
        appIdentity = identity;

    }

    private void setDeviceStoredConsentHandler(DeviceStoredConsentHandler deviceStoredConsentHandler) {
        this.deviceStoredConsentHandler = deviceStoredConsentHandler;

    }

    @Override
    public InternationalizationInterface getInternationalization() {
        return local;
    }

    @Override
    public LoggingInterface getLogging() {
        return logger;
    }

    private void setLogging(LoggingInterface log) {
        logger = log;
        appInfraLogger = logger.createInstanceForComponent(getComponentId(),
                getVersion());
    }

    @Override
    public ServiceDiscoveryInterface getServiceDiscovery() {
        return mServiceDiscoveryInterface;
    }

    @Override
    public AppConfigurationInterface getConfigInterface() {
        return configInterface;
    }

    public void setConfigInterface(AppConfigurationInterface configInterface) {
        this.configInterface = configInterface;
    }

    @Override
    public RestInterface getRestClient() {
        return mRestInterface;
    }

    @Override
    public ABTestClientInterface getAbTesting() {
        return mAbtesting;
    }

    void setAbTesting(ABTestClientInterface abTesting) {
        mAbtesting = abTesting;
    }

    @Override
    public LanguagePackInterface getLanguagePack() {
        return mLanguagePackInterface;
    }

    @Override
    public AppUpdateInterface getAppUpdate() {
        return mAppupdateInterface;
    }

    void setLanguagePackInterface(LanguagePackInterface languagePackInterface) {
        this.mLanguagePackInterface = languagePackInterface;
    }

    private void setAiKmInterface(AIKMInterface aikmInterface) {
        this.aikmInterface = aikmInterface;
    }

    private void setRestInterface(RestInterface restInterface) {
        mRestInterface = restInterface;
    }

    private void setLocal(InternationalizationInterface locale) {
        local = locale;

    }

    private void setServiceDiscoveryInterface(ServiceDiscoveryInterface mServiceDiscoveryInterfac) {
        mServiceDiscoveryInterface = mServiceDiscoveryInterfac;
    }

    void setAppupdateInterface(AppUpdateInterface appupdateInterface) {
        this.mAppupdateInterface = appupdateInterface;
    }

    public AppTaggingInterface getTagging() {
        return tagging;
    }

    private void setTagging(AppTaggingInterface tagg) {
        tagging = tagg;
        appInfraTagging = tagging.createInstanceForComponent(getComponentId(),
                getVersion());
    }

    public LoggingInterface getAppInfraLogInstance() { // this log should be used withing App Infra library
        return appInfraLogger;
    }


    /**
     * This Api is used to tag only App-infra based events
     *
     * @return - returns AppTaggingInterface instance
     */
    public AppTaggingInterface getAppInfraTaggingInstance() { // this tag should be used withing App Infra library
        return appInfraTagging;
    }

    @Override
    public String getComponentId() {
        return "ail";
    }

    @Override
    public String getVersion() {
        return BuildConfig.VERSION_NAME;
    }

    @Override
    public AIKMInterface getAiKmInterface() {
        return aikmInterface;
    }

    @Override
    public ConsentManagerInterface getConsentManager() {
        return consentManager;
    }

    @Override
    public DeviceStoredConsentHandler getDeviceStoredConsentHandler() {
        return deviceStoredConsentHandler;
    }

    public void setConsentManager(ConsentManagerInterface consentMgr) {
        consentManager = consentMgr;
    }

    /**
     * The type Builder,to enable an application developer to create his own implementation for specific App Infra modules and have all components integrated in the app use that alternative module implementation;
     * App Infra supports a builder pattern. By the use of the builder pattern, it is possible to create an instance of App Infra with alternative module implementations that overwrite one or more of the default module implementations.
     * The most common use case for providing alternative implementations is for testing purposes where a (component test-) app wants to test its functionality in isolation without having to implicitly test the App Infra implementation or any cloud services abstracted by App Infra.
     * In such a case, the app developer can create an App Infra instance with dummy implementations.Another use case for implementation replacement is to provide the ability to maintain compatibility with another cloud back-end (version).
     */
    public static class Builder {

        private SecureStorageInterface secStor;
        private LoggingInterface logger; // builder logger
        //   private LoggingInterface aiLogger; // app infra logger
        private AppTaggingInterface tagging;
        private AppIdentityInterface appIdentity;
        private InternationalizationInterface local;
        private ServiceDiscoveryInterface mServiceDiscoveryInterface;
        private TimeInterface mTimeSyncInterfaceBuilder;
        private ABTestClientInterface abtesting;


        private AppConfigurationInterface configInterface;
        private RestInterface mRestInterface;
        private LanguagePackInterface languagePack;
        private AppUpdateInterface appupdateInterface;
        private AIKMInterface aikmInterface;
        private ConsentManagerInterface consentManager;
        private DeviceStoredConsentHandler deviceStoredConsentHandler;


        /**
         * Instantiates a new Builder.
         * It can be configured with Builder setter methods
         */
        public Builder() {
            secStor = null;
            logger = null;
            //aiLogger = null;
            tagging = null;
            appIdentity = null;
            local = null;
            mServiceDiscoveryInterface = null;
            mTimeSyncInterfaceBuilder = null;
            configInterface = null;
            mRestInterface = null;
            languagePack = null;
            aikmInterface = null;
            abtesting = null;
            consentManager = null;
            deviceStoredConsentHandler=null;
        }


        /**
         * Sets config.
         *
         * @param abTestClient the config
         * @return the config
         */
        public Builder setAbTesting(ABTestClientInterface abTestClient) {
            this.abtesting = abTestClient;
            return this;
        }

        /**
         * Sets config.
         *
         * @param config the config
         * @return the config
         */
        public Builder setConfig(AppConfigurationInterface config) {
            configInterface = config;
            return this;
        }

        /**
         * Sets REST.
         *
         * @param rest the config
         * @return the config
         */
        public Builder setRestInterface(RestInterface rest) {
            mRestInterface = rest;
            return this;
        }

        /**
         * Sets Builder logging overriding the default implementation.
         *
         * @param log the log
         * @return the logging
         */
        public Builder setLogging(LoggingInterface log) {
            logger = log;
            return this;
        }

        /**
         * Sets Builder secure storage overriding the default implementation.
         *
         * @param secureStorage the secure storage
         * @return the secure storage
         */
        public Builder setSecureStorage(SecureStorageInterface secureStorage) {
            secStor = secureStorage;
            return this;
        }


        /**
         * Sets Builder service discovery overriding the default implementation.
         *
         * @param serviceDiscoveryInterface the service discovery interface
         * @return the service discovery
         */
        public Builder setServiceDiscovery(ServiceDiscoveryInterface serviceDiscoveryInterface) {
            mServiceDiscoveryInterface = serviceDiscoveryInterface;
            return this;
        }

        /**
         * Sets Builder tagging overriding the default implementation.
         *
         * @param aIAppTaggingInterface the a i app tagging interface
         * @return the tagging
         */
        public Builder setTagging(AppTaggingInterface aIAppTaggingInterface) {
            tagging = aIAppTaggingInterface;
            return this;
        }


        /**
         * Sets Builder time sync overriding the default implementation.
         *
         * @param timeSyncSntpClient the time sync sntp client
         * @return the time sync
         */
        public Builder setTimeSync(TimeInterface timeSyncSntpClient) {
            mTimeSyncInterfaceBuilder = timeSyncSntpClient;
            return this;
        }

        public void setDeviceStoredConsentHandler(DeviceStoredConsentHandler deviceStoredConsentHandler) {
            this.deviceStoredConsentHandler = deviceStoredConsentHandler;
        }

        /**
         * Sets Builder aiKm Service overriding the default implementation.
         *
         * @param aikmInterface aiKm service interface
         */
        public void setAiKmInterface(AIKMInterface aikmInterface) {
            this.aikmInterface = aikmInterface;
        }

        public Builder setConsentManager(ConsentManagerInterface consentMgr) {
            this.consentManager = consentMgr;
            return this;
        }

        /**
         * Actual AppInfra object is created here.Once build is called AppInfra is created in memory and cannot be modified during runtime.
         *
         * @param pContext Application Context
         * @return the app infra instance
         */
        public AppInfra build(Context pContext) {
            long startTime = System.currentTimeMillis();
            final AppInfra ai = new AppInfra(pContext);
            final AppConfigurationManager appConfigurationManager = new AppConfigurationManager(ai);
            ai.setConfigInterface(configInterface == null ? appConfigurationManager : configInterface);

            ai.setTime(mTimeSyncInterfaceBuilder == null ? new TimeSyncSntpClient(ai) : mTimeSyncInterfaceBuilder);

            ai.setSecureStorage(secStor == null ? new SecureStorage(ai) : secStor);
            ai.setLogging(logger == null ? new AppInfraLogging(ai) : logger);

            ai.setAppIdentity(appIdentity == null ? new AppIdentityManager(ai) : appIdentity);
            ai.setLocal(local == null ? new InternationalizationManager(ai) : local);

            ai.setTagging(tagging == null ? new AppTagging(ai) : tagging);

            ai.setServiceDiscoveryInterface(mServiceDiscoveryInterface == null ?
                    new ServiceDiscoveryManager(ai) : mServiceDiscoveryInterface);

            ai.setRestInterface(mRestInterface == null ? new RestManager(ai) : mRestInterface);

            ai.setConsentManager(consentManager == null ? new ConsentManager(ai) : consentManager);

            ai.getTagging().registerClickStreamHandler(ai.getConsentManager());

            new Thread(new Runnable() {
                @Override
                public void run() {

                    if (abtesting != null) {
                        ai.setAbTesting(abtesting);
                    }

                   /* final Object abTestConfig = ABTestClientManager.getAbtestConfig(appConfigurationManager, ai);
                    if (abTestConfig != null) {
                        ai.setAbTesting(aIabtesting == null ? new ABTestClientManager(ai) : aIabtesting);
                    } else {
                        ai.getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG,
                                AppInfraLogEventID.AI_APPINFRA, "Please add the Abtest Config Values " +
                                        "to use Abtesting");
                    }
                    ai.getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG, AppInfraLogEventID.AI_APPINFRA,
                            "Device name:" + Build.MANUFACTURER + " " + Build.MODEL + " " + " OS version:" + Build.VERSION.RELEASE);

                    if (ai.getAppIdentity() != null) {
                        initializeLogs(ai);
                    }*/

//                    appConfigurationManager.migrateDynamicData();
                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String languagePackConfig = LanguagePackManager.getLanguagePackConfig(appConfigurationManager, ai);
                    if (languagePackConfig != null) {
                        ai.setLanguagePackInterface(languagePack == null ? new LanguagePackManager(ai) : languagePack);
                    } else {
                        ai.getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG,
                                AppInfraLogEventID.AI_APPINFRA, "Please add the LanguagePack Config Values " +
                                        "to use Language Pack");
                    }
                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Object appUpdateConfig = AppUpdateManager.getAutoRefreshValue(appConfigurationManager, ai);
                    if (appUpdateConfig != null) {
                        final AppUpdateManager appUpdateManager = new AppUpdateManager(ai);
                        ai.setAppupdateInterface(appupdateInterface == null ? appUpdateManager : appupdateInterface);
                        appUpdateManager.appInfraRefresh();
                    } else {
                        ai.getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG,
                                AppInfraLogEventID.AI_APPINFRA, "Please add the AppUpdate Config Values " +
                                        "to use AppUpdate Feature ");
                    }
                }
            }).start();

//            if (AIKManager.isAiKmServiceEnabled(appConfigurationManager, ai)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final AIKManager aikManager;
                    aikManager = new AIKManager(ai);
                    ai.setAiKmInterface(aikmInterface == null ? aikManager : aikmInterface);
                }
            }).start();
//            }

            ai.setDeviceStoredConsentHandler(deviceStoredConsentHandler==null?new DeviceStoredConsentHandler(ai):deviceStoredConsentHandler);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (ai.getLogging() instanceof AppInfraLogging) {
                        ((AppInfraLogging) ai.getLogging()).onAppInfraInitialised(ai);
                    }
                }
            }).start();


            postLog(ai, startTime, "App-infra initialization ends with ");
            return ai;
        }
    }


    private static void initializeLogs(AppInfra ai) {
        final StringBuilder appInfraLogStatement = new StringBuilder();

        try {
            appInfraLogStatement.append("AppInfra initialized for application \"");
            appInfraLogStatement.append(ai.getAppIdentity().getAppName());
            appInfraLogStatement.append("\" version \"");
            appInfraLogStatement.append(ai.getAppIdentity().getAppVersion());
            appInfraLogStatement.append("\" in state \"");
            appInfraLogStatement.append(ai.getAppIdentity().getAppState());

        } catch (IllegalArgumentException e) {
            ai.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR,
                    AppInfraLogEventID.AI_APPINFRA, "IllegalArgumentException in InitializeLogs " + e.getMessage());
        }
        appInfraLogStatement.append("\"");
        ai.getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG,
                AppInfraLogEventID.AI_APPINFRA, "AppInfra initialized " + appInfraLogStatement.toString());
    }

    public RxBus getRxBus() {
        if (rxBus == null) {
            rxBus = new RxBus();
        }

        return rxBus;
    }
}
