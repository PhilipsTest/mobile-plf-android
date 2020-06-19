package com.pim.demouapp;

import com.philips.platform.uappframework.uappinput.UappLaunchInput;

/**
 * Created by philips on 6/16/17.
 */

public class PIMDemoUAppLaunchInput extends UappLaunchInput {

    public PIMDemoUAppLaunchInput() {
    }

    public boolean isRedirectedToClosedApp() {
        return isRedirectedToClosedApp;
    }

    public void setRedirectedToClosedApp(boolean redirectedToClosedApp) {
        isRedirectedToClosedApp = redirectedToClosedApp;
    }

    public RegistrationLib getComponent_type() {
        return component_type;
    }

    public void setComponent_type(RegistrationLib component_type) {
        this.component_type = component_type;
    }

    public enum RegistrationLib { USR , UDI};

    private boolean isRedirectedToClosedApp;
    private RegistrationLib component_type;

}
