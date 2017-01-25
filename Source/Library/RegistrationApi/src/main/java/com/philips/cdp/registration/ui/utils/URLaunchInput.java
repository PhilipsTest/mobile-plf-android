package com.philips.cdp.registration.ui.utils;

import com.philips.cdp.registration.listener.UserRegistrationUIEventListener;
import com.philips.cdp.registration.settings.RegistrationFunction;
import com.philips.platform.uappframework.uappinput.UappLaunchInput;


public class URLaunchInput extends UappLaunchInput {

    private String registrationLaunchMode;

    public boolean isAddtoBackStack() {
        return isAddToBackStack;
    }

    public void enableAddtoBackStack(boolean isAddToBackStack) {
        this.isAddToBackStack = isAddToBackStack;
    }

    private boolean isAddToBackStack;

    private RegistrationFunction registrationFunction;

    private UserRegistrationUIEventListener userRegistrationListener;

    public RegistrationFunction getRegistrationFunction() {
        return registrationFunction;
    }

    public void setRegistrationFunction(RegistrationFunction registrationFunction) {
        this.registrationFunction = registrationFunction;
    }

    public void setUserRegistrationUIEventListener(UserRegistrationUIEventListener
                                                           userRegistrationListener) {
        this.userRegistrationListener = userRegistrationListener;
    }

    public UserRegistrationUIEventListener getUserRegistrationUIEventListener() {
        return this.userRegistrationListener;
    }

    public String getRegistrationLaunchMode() {
        return registrationLaunchMode;
    }

    public void setRegistrationLaunchMode(String registrationLaunchMode) {
        this.registrationLaunchMode = registrationLaunchMode;
    }
}
