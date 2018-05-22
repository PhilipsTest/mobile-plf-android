/* Copyright (c) Koninklijke Philips N.V. 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.appinfra.logging;


import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.consentmanager.consenthandler.DeviceStoredConsentHandler;
import com.philips.platform.appinfra.logging.model.AILCloudLogMetaData;
import com.philips.platform.appinfra.logging.sync.CloudLogSyncManager;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AppInfraLogging implements LoggingInterface {


    private static final long serialVersionUID = -4898715486015827285L;
    private AppInfra mAppInfra;
    private transient Logger mJavaLogger;
    private String componentId, componentVersion;

    private AILCloudLogMetaData ailCloudLogMetaData = new AILCloudLogMetaData();

    private LoggingConfiguration loggingConfiguration;

    public AILCloudLogMetaData getAilCloudLogMetaData() {
        return ailCloudLogMetaData;
    }


    public AppInfraLogging(AppInfra aAppInfra) {
        this(aAppInfra, "", "");
    }

    public AppInfraLogging(AppInfra aAppInfra, String componentId, String componentVersion) {
        mAppInfra = aAppInfra;
        mJavaLogger = getJavaLogger(componentId, componentVersion);
        this.componentId = componentId;
        this.componentVersion = componentVersion;
    }

    @Override
    public LoggingInterface createInstanceForComponent(String componentId, String componentVersion) {
        return new AppInfraLogging(mAppInfra, componentId, componentVersion);
    }


    @Override
    public void log(LogLevel level, String eventId, String message) {
        log(level, eventId, message, null);
        if (TextUtils.isEmpty(componentId)) {
            componentId = mAppInfra.getAppIdentity().getAppName();
        }
        if (TextUtils.isEmpty(componentVersion)) {
            componentVersion = mAppInfra.getAppIdentity().getAppVersion();
        }
    }

    /**
     * Logs message on console and file .
     *
     * @param level   the level {VERBOSE, DEBUG, INFO, WARNING, ERROR}
     * @param eventId the Event name or Tag
     * @param message the message
     * @param map     the dictionary
     * @since 1.0.0
     */
    @Override
    public void log(LogLevel level, String eventId, String message, Map<String, ?> map) {
        if (loggingConfiguration.checkIfComponentLogCriteriaMet(componentId)) {
            Object[] params = getParamObjects();
            if (null != mJavaLogger) {
                params[0] = message;
                params[1] = componentId;
                params[2] = componentVersion;
                params[3] = map;

                switch (level) {
                    case ERROR:
                        mJavaLogger.log(Level.SEVERE, eventId, params);
                        break;
                    case WARNING:
                        mJavaLogger.log(Level.WARNING, eventId, params);
                        break;
                    case INFO:
                        mJavaLogger.log(Level.INFO, eventId, params);
                        break;
                    case DEBUG:
                        mJavaLogger.log(Level.CONFIG, eventId, params);
                        break;
                    case VERBOSE:
                        mJavaLogger.log(Level.FINE, eventId, params);
                        break;
                }
            }
        }
    }

    public void updateMetadata(AppInfra appInfra) {
        try {
            if (appInfra.getAppIdentity() != null) {
                ailCloudLogMetaData.setAppName(appInfra.getAppIdentity().getAppName());
                ailCloudLogMetaData.setAppState(appInfra.getAppIdentity().getAppState().toString());
                ailCloudLogMetaData.setAppVersion(appInfra.getAppIdentity().getAppVersion());
            }
            if (appInfra.getTagging() != null) {
                ailCloudLogMetaData.setAppId(appInfra.getTagging().getTrackingIdentifier());
            }
            if (appInfra.getInternationalization() != null) {
                ailCloudLogMetaData.setLocale(appInfra.getInternationalization().getUILocaleString());
            }
            if (appInfra.getServiceDiscovery() != null) {
                ailCloudLogMetaData.setHomeCountry(appInfra.getServiceDiscovery().getHomeCountry());
            }
            if (loggingConfiguration != null && loggingConfiguration.isCloudLogEnabled()) {
                registerCloudHandler();
            }
        } catch (IllegalArgumentException e) {

        }

    }

    public void registerCloudHandler() {
        DeviceStoredConsentHandler deviceStoredConsentHandler = new DeviceStoredConsentHandler(mAppInfra);
        deviceStoredConsentHandler.registerConsentChangeListener(CloudLogSyncManager.getInstance(mAppInfra, loggingConfiguration));
        CloudConsentProvider cloudConsentProvider = new CloudConsentProvider(deviceStoredConsentHandler);
        cloudConsentProvider.registerConsentHandler(mAppInfra.getConsentManager());
    }

    @Override
    public void setUserUUID(String userUUID) {
        ailCloudLogMetaData.setUserUUID(userUUID);
    }

    protected Logger getJavaLogger(String componentId, String componentVersion) {
        loggingConfiguration = new LoggingConfiguration(mAppInfra, componentId, componentVersion);
        return LoggerFactory.getLoggerInstance(mAppInfra, new LoggingConfiguration(mAppInfra, componentId, componentVersion));
    }

    @NonNull
    Object[] getParamObjects() {
        return new Object[4];
    }
}
