package com.pim.demouapp;

import com.philips.platform.appinfra.AppInfraInterface;

public class PIMUtility {

    private final static  PIMUtility instance = new PIMUtility();
    private AppInfraInterface appInfra;

    public static PIMUtility getInstance(){
        return instance;
    }

    public void setAppInfra(AppInfraInterface appInfra){

        this.appInfra = appInfra;
    }

    public AppInfraInterface getAppInfra( ){
        return appInfra;
    }
}
