/* Copyright (c) Koninklijke Philips N.V. 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.appinfra.logging;

import java.io.Serializable;
import java.util.Map;

/**
 * The Logging Interface
 */
public interface LoggingInterface extends Serializable {
    /**
     * The enum Log level.
     */
    enum LogLevel {
        VERBOSE, DEBUG, INFO, WARNING, ERROR
    }

    /**
     * <p>
     * Create instance for component logging interface.
     * This method to be used by all component to get their respective logging
     *
     * @param componentId      the base package name of component
     * @param componentVersion the component version
     * @return the logging interface
     * @since 1.0.0
     */
    LoggingInterface createInstanceForComponent(String componentId, String componentVersion);

    /**
     * Logs message on console and file .
     *
     * @param level   the level {VERBOSE, DEBUG, INFO, WARNING, ERROR}
     * @param eventId the Event name or Tag
     * @param message the message
     * @since 1.0.0
     */
    void log(LogLevel level, String eventId, String message);

    /**
     * Logs message on console and file .
     *
     * @param level   the level {VERBOSE, DEBUG, INFO, WARNING, ERROR}
     * @param eventId the Event name or Tag
     * @param message the message
     * @param map     the dictionary
     * @since 1.0.0
     */
    void log(LogLevel level, String eventId, String message, Map<String, ?> map);

    /**
     * To identify log originated from which user set/reset when user login/logout can be empty (will not be able to track based on user)
     * @param userUUID
     * @since 1802.0.0
     * Not used in PIm
     */
    void setHSDPUserUUID(String userUUID);
    /**
     *
     * @return This method returns the Cloud Logging consent identifier
     * @since 1802.0
     * Not used in PIM
     */
    String getCloudLoggingConsentIdentifier();


}
