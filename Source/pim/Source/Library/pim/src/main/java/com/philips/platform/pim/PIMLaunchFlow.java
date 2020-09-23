package com.philips.platform.pim;

public enum PIMLaunchFlow {
    LOGIN("login"),
    CREATE("create"),
    NO_PROMPT("noPrompt");

    public String pimLaunchFlow;

    PIMLaunchFlow(String pimLaunchFlow){
        this.pimLaunchFlow = pimLaunchFlow;
    }
}
