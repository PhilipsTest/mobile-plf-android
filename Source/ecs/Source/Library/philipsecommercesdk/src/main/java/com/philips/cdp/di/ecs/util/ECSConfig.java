package com.philips.cdp.di.ecs.util;

import com.philips.cdp.di.ecs.model.response.HybrisConfigResponse;
import com.philips.platform.appinfra.AppInfra;

public enum ECSConfig {

    INSTANCE;


    String  propositionID;
    AppInfra appInfra;
    HybrisConfigResponse config;

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

    public boolean isAppConfigured() {
        return rootCategory!=null && siteId!=null;
    }


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



    public AppInfra getAppInfra() {
        return appInfra;
    }

    public void setAppInfra(AppInfra appInfra) {
        this.appInfra = appInfra;
    }


    public  boolean isHybrisFlow(){
        return this.baseURL!=null;
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
}
