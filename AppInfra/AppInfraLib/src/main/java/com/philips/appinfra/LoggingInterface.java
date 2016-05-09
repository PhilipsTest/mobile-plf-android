/* Copyright (c) Koninklijke Philips N.V. 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.appinfra;

/**
 * Created by 310209604 on 2016-05-04.
 */
public interface LoggingInterface {
    public  enum  LogLevel {VERBOSE, DEBUG, INFO, WARNING, ERROR};

    public LoggingInterface createInstanceForComponent(String componentId, String componentVersion);
    public void log(LogLevel level, String eventId, String message);
}
