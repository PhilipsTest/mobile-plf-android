/* Copyright (c) Koninklijke Philips N.V. 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.appinfra.logging;


import android.support.annotation.NonNull;

import com.philips.platform.appinfra.AppInfra;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AppInfraLogging implements LoggingInterface {


    private static final long serialVersionUID = -4898715486015827285L;
    private AppInfra mAppInfra;
    private transient Logger mJavaLogger;

    public AppInfraLogging(AppInfra aAppInfra) {
        this(aAppInfra,"","");
    }

    public AppInfraLogging(AppInfra aAppInfra,String componentId, String componentVersion) {
        mAppInfra = aAppInfra;
        mJavaLogger=LoggerFactory.createLoggerWithLogConfiguration(mAppInfra,new LoggingConfiguration(mAppInfra, componentId, componentVersion));
        if(mJavaLogger==null){
            mJavaLogger=Logger.getLogger(componentId);
        }
    }

    @Override
    public LoggingInterface createInstanceForComponent(String componentId, String componentVersion) {
        return new AppInfraLogging(mAppInfra,componentId,componentVersion);
    }


    @Override
    public void log(LogLevel level, String eventId, String message) {
        log(level,eventId,message,null);
    }

    /**
     * Logs message on console and file .
     *
     * @param level   the level {VERBOSE, DEBUG, INFO, WARNING, ERROR}
     * @param eventId the Event name or Tag
     * @param message the message
     * @param map the dictionary
     * @since 1.0.0
     */
    @Override
    public void log(LogLevel level, String eventId, String message, Map<String, ?> map) {
        Object[] params = new Object[2];

        if (null != mJavaLogger) {
            params[0]=message;
            params[1]=map;
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

    @Override
    public void setUserUUID(String userUUID) {
        mAppInfra.getAilCloudLogMetaData().setUserUUID(userUUID);
    }

    protected Logger getJavaLogger() {
        return mJavaLogger;
    }

    @NonNull
    Object[] getParamObjects() {
        return new Object[2];
    }
}
