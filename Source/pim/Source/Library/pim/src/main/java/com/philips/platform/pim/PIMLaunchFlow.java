package com.philips.platform.pim;

public enum PIMLaunchFlow {
    LOGIN("login"),
    CREATE("create");

    public String pimLaunchFlow;

    PIMLaunchFlow(String pimLaunchFlow){
        this.pimLaunchFlow = pimLaunchFlow;
    }
}
