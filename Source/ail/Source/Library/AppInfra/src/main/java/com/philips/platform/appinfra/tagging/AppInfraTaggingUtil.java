/* Copyright (c) Koninklijke Philips N.V. 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.appinfra.tagging;


import android.text.TextUtils;

import com.philips.platform.appinfra.AppInfraLogEventID;
import com.philips.platform.appinfra.logging.LoggingInterface;

import java.io.Serializable;

public class AppInfraTaggingUtil implements Serializable {

    private static final long serialVersionUID = -7930120706891543524L;
    private AppTaggingInterface appTagging;
    private LoggingInterface appInfraLogging;

    public static final String MALFORMED_URL = "malformed url after applying url parameters";
    public static final String NO_NETWORK = "Internet is not reachable";

    ////Technical Errors
    public static final String SD_URL_MISMATCH = " SD data refresh due to URL mismatch ";
    public static final String SD_SET_INVALID_COUNTRY_CODE = " setHomeCountry invalid country - ";
    public static final String SD_SET_HOME_COUNTRY_STORE_FAILED = " setHomeCountry save failed";
    public static final String SD_SET_HOME_COUNTRY_FETCH_FAILED = " setHomeCountry fetch failed ";
    public static final String SD_STORE_FAILED = " error while saving SD data ";

    // AppInfra Tagging Categories
    public static final String SERVICE_DISCOVERY = "ServiceDiscovery";

    ////AppInfra success
    public static final String DOWNLOAD_PLATFORM_SERVICES_INVOKED = " Downloading platform services -";
    public static final String DOWNLOAD_PREPOSITION_SERVICES_INVOKED = " Downloading preposition services -";
    public static final String SD_SUCCESS = " SD download success ";
    public static final String SD_LOCAL_CACHE_DATA_SUCCESS = " SD fetched local cached data";
    public static final String GET_HOME_COUNTRY_SIM_SUCCESS = " Fetched country code from sim - ";
    public static final String GET_HOME_COUNTRY_GEOIP_SUCCESS = " Fetched country code  from GEOIP - ";
    public static final String SET_HOME_COUNTRY_SUCCESS = " Successfully setHomeCountry - ";
    public static final String SD_FORCE_REFRESH_CALLED = "SD force refreshed called";
    public static final String SD_CLEAR_DATA = "Clearing SD data";

    //keys
    public static final String SEND_DATA = "sendData";


    //Actions
    public static final String TECHNICAL_ERROR = "TechnicalError";
    static final String SUCCESS_MESSAGE = "appInfraSuccessMessage";


    public AppInfraTaggingUtil(AppTaggingInterface appTagging, LoggingInterface appInfraLogInstance) {
        this.appTagging = appTagging;
        this.appInfraLogging = appInfraLogInstance;
    }

    public void trackErrorAction(String category, String message) {
        if (!TextUtils.isEmpty(category) && !TextUtils.isEmpty(message)) {
            appTagging.trackActionWithInfo(SEND_DATA, TECHNICAL_ERROR, "AIL:".concat(category).concat(":").concat(message));
            appInfraLogging.log(LoggingInterface.LogLevel.DEBUG, AppInfraLogEventID.AI_SERVICE_DISCOVERY, "AIL:".concat(category).concat(":").concat(message));
        }
    }
}
