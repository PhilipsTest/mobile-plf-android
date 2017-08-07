package com.philips.cdp.registration.ui.utils;

import com.philips.cdp.registration.configuration.RegistrationLaunchMode;
import com.philips.cdp.registration.listener.UserRegistrationUIEventListener;
import com.philips.cdp.registration.settings.RegistrationFunction;
import com.philips.platform.uappframework.uappinput.UappLaunchInput;

/**
 * This class is used to provide input parameters and customizations for User registration.
 */

public class URLaunchInput extends UappLaunchInput {

    private RegistrationLaunchMode registrationLaunchMode;

    private RegistrationContentConfiguration registrationContentConfiguration;

    /**
     * Get status of is current fragment need to add to backstack or no.
     *
     * @return true if need to add to fragment back stack
     */
    public boolean isAddtoBackStack() {
        return isAddToBackStack;
    }

    @Deprecated
    private boolean isAccountSettings;

    /**
     * Enable  add to back stack for current fragment.
     *
     * @param isAddToBackStack
     */
    public void enableAddtoBackStack(boolean isAddToBackStack) {
        this.isAddToBackStack = isAddToBackStack;
    }

    private boolean isAddToBackStack;

    private RegistrationFunction registrationFunction;

    private UserRegistrationUIEventListener userRegistrationListener;

    /**
     * Get Registration function.
     * @return Registration function  RegistrationFunction
     */
    public RegistrationFunction getRegistrationFunction() {
        return registrationFunction;
    }

    /**
     * RegistrationFunction is used to prioritize  between Create account and Sign in.
     * RegistrationFunction.Registration - Will display the Create account option on top
     * RegistrationFunction.SignIn - Will display the Sign in option on top.
     *
     * @param registrationFunction
     */
    public void setRegistrationFunction(RegistrationFunction registrationFunction) {
        this.registrationFunction = registrationFunction;
    }

    /**
     * Set a UserRegistrationUIEventListener to provide custom implementations of
     * Terms and conditions, Privacy policy and know about user registration completion.
     *
     * @param userRegistrationListener
     */
    public void setUserRegistrationUIEventListener(UserRegistrationUIEventListener
                                                           userRegistrationListener) {
        this.userRegistrationListener = userRegistrationListener;
    }

    public UserRegistrationUIEventListener getUserRegistrationUIEventListener() {
        return this.userRegistrationListener;
    }

    public RegistrationLaunchMode getEndPointScreen() {
        return registrationLaunchMode;
    }

    public void setEndPointScreen(RegistrationLaunchMode registrationLaunchMode) {
        this.registrationLaunchMode = registrationLaunchMode;
    }

    /**
     * Used to set custom content on the marketing opt in page and home page.
     * Please see RegistrationContentConfiguration class for more details.
     *
     * @param registrationContentConfiguration
     */
    public void setRegistrationContentConfiguration(RegistrationContentConfiguration registrationContentConfiguration) {
        this.registrationContentConfiguration = registrationContentConfiguration;
    }

    public RegistrationContentConfiguration getRegistrationContentConfiguration() {
        return this.registrationContentConfiguration;
    }

    UIFlow uiFlow;

    /**
     * Used to override the UI flow. Setting this will disable any server side A/B testing.
     * Advised not to use for normal use-cases.
     *
     * @param uiFlow - Any one of the UIFlow enum values.
     */
    public void setUIFlow(UIFlow uiFlow) {
        this.uiFlow = uiFlow;
    }

    public UIFlow getUIflow() {
        return uiFlow;
    }

    @Deprecated
    public void setAccountSettings(boolean isAccountSettings) {
        this.isAccountSettings = isAccountSettings;
    }

    @Deprecated
    public boolean isAccountSettings() {
        return isAccountSettings;
    }

}
