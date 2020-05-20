/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.util;

import com.android.volley.DefaultRetryPolicy;
import com.philips.platform.ecs.model.oauth.ECSOAuthData;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.logging.LoggingInterface;

public enum ECSConfiguration {

    INSTANCE;


    String  propositionID;
    AppInfra appInfra;

    private LoggingInterface ecsLoggingInterface;

    private ECSOAuthData oAuthResponse;

    public String getAccessToken() {
        return accessToken;
    }

    String accessToken;

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    String baseURL;

    public String getLocale() {
        return locale;
    }

    String locale;

    public String getRootCategory() {
        return rootCategory;
    }

    public void setRootCategory(String rootCategory) {
        this.rootCategory = rootCategory;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    String rootCategory;
    String siteId;
    DefaultRetryPolicy defaultRetryPolicy;

    public DefaultRetryPolicy getDefaultRetryPolicy() {
        return defaultRetryPolicy;
    }

    public void setDefaultRetryPolicy(DefaultRetryPolicy defaultRetryPolicy) {
        this.defaultRetryPolicy = defaultRetryPolicy;
    }
    
    public AppInfra getAppInfra() {
        return appInfra;
    }

    public void setAppInfra(AppInfra appInfra) {
        this.appInfra = appInfra;
    }


    public String getPropositionID() {
        return propositionID;
    }

    public void setPropositionID(String propositionID) {
        this.propositionID = propositionID;
    }

    public void setAuthToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getCountry() {
        return getCountryFromLocale(ECSConfiguration.INSTANCE.getLocale());
    }

    public ECSOAuthData getAuthResponse() {
        return oAuthResponse;
    }

    public void setAuthResponse(ECSOAuthData oAuthResponse) {
        this.oAuthResponse = oAuthResponse;
    }

    public LoggingInterface getEcsLogging() {
        return ecsLoggingInterface;
    }

    public void setEcsLogging(LoggingInterface ecsLoggingInterface) {
        this.ecsLoggingInterface = ecsLoggingInterface;
    }

    private String getCountryFromLocale(String locale) {
        if(locale == null) return "UNKNOWN";
        String[] localeArray;
        localeArray = locale.split("_");
        return localeArray[1];
    }
}
