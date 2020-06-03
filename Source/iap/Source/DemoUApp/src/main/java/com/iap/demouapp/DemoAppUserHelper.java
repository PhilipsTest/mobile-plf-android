package com.iap.demouapp;

import android.app.Activity;
import android.content.Context;

import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.listener.UserRegistrationUIEventListener;
import com.philips.cdp.registration.settings.RegistrationFunction;
import com.philips.cdp.registration.ui.utils.RegistrationContentConfiguration;
import com.philips.cdp.registration.ui.utils.URInterface;
import com.philips.cdp.registration.ui.utils.URLaunchInput;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pim.PIMInterface;
import com.philips.platform.pim.PIMLaunchInput;
import com.philips.platform.pim.listeners.UserLoginListener;
import com.philips.platform.pim.manager.PIMSettingManager;
import com.philips.platform.uappframework.launcher.ActivityLauncher;

public class DemoAppUserHelper implements UserRegistrationUIEventListener, UserLoginListener {

    private static final DemoAppUserHelper insttance = new DemoAppUserHelper();
    private AppInfraInterface appInfraInterface;
    private UserDataInterface userDataInterface;
    private URInterface urInterface;
    private PIMInterface pimInterface;
    private UserLoginListener userLoginListener;

    public static DemoAppUserHelper getInstance(){
        return insttance;
    }

    private DemoAppUserHelper(){
        pimInterface = PIMInterface.getPIMInterface();
    }

    public UserDataInterface getUserDataInterface(Context context){
        if(isUDIComponent()){
            userDataInterface = pimInterface.getUserDataInterface();
        }else {
            initUSR(context);
            userDataInterface = urInterface.getUserDataInterface();
        }
        return userDataInterface;
    }

    public void launchRegistrationComponent(Context context, UserLoginListener userLoginListener){
        this.userLoginListener = userLoginListener;
        if(isUDIComponent()){
            launchUDI(context);
        }else {
            launchUSR(context);
        }

    }

    public boolean isUDIComponent(){
        String homeCountry = appInfraInterface.getServiceDiscovery().getHomeCountry();
        if(homeCountry != null && (homeCountry.equalsIgnoreCase("CN") || homeCountry.equalsIgnoreCase("IN")))
            return false;
        else
            return true;
    }

    public void setAppInfraInterface(AppInfraInterface appInfraInterface){
        this.appInfraInterface = appInfraInterface;
    }

    private void initUSR(Context context){
        urInterface = new URInterface();
        urInterface.init(new IapDemoUAppDependencies(appInfraInterface), new IapDemoAppSettings(context.getApplicationContext()));
    }

    private void launchUSR(Context context){
        URLaunchInput urLaunchInput = new URLaunchInput();
        urLaunchInput.setUserRegistrationUIEventListener(this);
        urLaunchInput.enableAddtoBackStack(true);
        RegistrationContentConfiguration contentConfiguration = new RegistrationContentConfiguration();
        contentConfiguration.enableLastName(true);
        contentConfiguration.enableContinueWithouAccount(true);
        RegistrationConfiguration.getInstance().setPrioritisedFunction(RegistrationFunction.Registration);
        urLaunchInput.setRegistrationContentConfiguration(contentConfiguration);
        urLaunchInput.setRegistrationFunction(RegistrationFunction.Registration);


        ActivityLauncher activityLauncher = new ActivityLauncher(context, ActivityLauncher.
                ActivityOrientation.SCREEN_ORIENTATION_SENSOR, null, 0, null);
        urInterface.launch(activityLauncher, urLaunchInput);
    }

    private void launchUDI(Context context){
        PIMLaunchInput launchInput = new PIMLaunchInput();
        ActivityLauncher activityLauncher = new ActivityLauncher(context, ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_SENSOR, null, 0, null);
        pimInterface.setLoginListener(this);
        PIMInterface.getPIMInterface().launch(activityLauncher, launchInput);
    }

    @Override
    public void onUserRegistrationComplete(Activity activity) {
        activity.finish();
        if(userLoginListener != null)
            userLoginListener.onLoginSuccess();
    }

    @Override
    public void onPrivacyPolicyClick(Activity activity) {

    }

    @Override
    public void onTermsAndConditionClick(Activity activity) {

    }

    @Override
    public void onPersonalConsentClick(Activity activity) {

    }

    @Override
    public void onLoginSuccess() {
        if(userLoginListener != null)
            userLoginListener.onLoginSuccess();
    }

    @Override
    public void onLoginFailed(Error error) {

    }
}
