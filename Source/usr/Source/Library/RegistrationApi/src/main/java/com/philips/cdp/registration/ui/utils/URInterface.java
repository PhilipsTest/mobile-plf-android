package com.philips.cdp.registration.ui.utils;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.*;
import android.support.v4.app.*;

import com.janrain.android.Jump;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.configuration.*;
import com.philips.cdp.registration.injection.*;
import com.philips.cdp.registration.myaccount.MyaDetailsFragment;
import com.philips.cdp.registration.settings.*;
import com.philips.cdp.registration.ui.traditional.*;
import com.philips.platform.uappframework.UappInterface;
import com.philips.platform.uappframework.launcher.*;
import com.philips.platform.uappframework.uappinput.*;
import com.philips.platform.uid.thememanager.*;

/**
 * It is used to initialize and launch USR
 * @since 1.0.0
 */

public class URInterface implements UappInterface {

    private static RegistrationComponent component;

    /**
     * Launches the USR user interface. The component can be launched either with an ActivityLauncher or a FragmentLauncher.
     * @param uiLauncher  pass ActivityLauncher or FragmentLauncher
     * @param uappLaunchInput pass instance of  URLaunchInput
     * @since 1.0.0
     */
    @Override
    public void launch(UiLauncher uiLauncher, UappLaunchInput uappLaunchInput) {
        if (uiLauncher instanceof ActivityLauncher) {
                launchAsActivity(((ActivityLauncher) uiLauncher), uappLaunchInput);

        } else if (uiLauncher instanceof FragmentLauncher) {

            if(isUserSignedIn(RegistrationHelper.getInstance().
                    getUrSettings().getContext())){
                launchMyAccountDetails((FragmentLauncher) uiLauncher, uappLaunchInput);
            }else {
                launchAsFragment((FragmentLauncher) uiLauncher, uappLaunchInput);
            }

        }
    }

    private boolean isUserSignedIn(Context mContext){
        User user = new User(mContext);
        return user.isUserSignIn();
    }

    private void launchMyAccountDetails(FragmentLauncher fragmentLauncher, UappLaunchInput uappLaunchInput){

        MyaDetailsFragment  myaDetailsFragment=new MyaDetailsFragment();
        FragmentManager mFragmentManager = fragmentLauncher.getFragmentActivity().
                getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(fragmentLauncher.getParentContainerResourceID(),
                myaDetailsFragment,
                RegConstants.REGISTRATION_FRAGMENT_TAG);
        if (((URLaunchInput)
                uappLaunchInput).isAddtoBackStack()) {
            fragmentTransaction.addToBackStack(RegConstants.REGISTRATION_FRAGMENT_TAG);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }
    /**
     * It is used to launch USR as a fragment
     * @param fragmentLauncher  pass instance of FragmentLauncher
     * @param uappLaunchInput   pass instance of UappLaunchInput
     *                        @since 1.0.0
     */
    private void launchAsFragment(FragmentLauncher fragmentLauncher,
                                  UappLaunchInput uappLaunchInput) {
        try {
            FragmentManager mFragmentManager = fragmentLauncher.getFragmentActivity().
                    getSupportFragmentManager();
            MyaDetailsFragment registrationFragment = new MyaDetailsFragment();
            Bundle bundle = new Bundle();

            RegistrationLaunchMode registrationLaunchMode =  ((URLaunchInput)uappLaunchInput).getEndPointScreen();
            UIFlow uiFlow =((URLaunchInput) uappLaunchInput).getUIflow();
            RegUtility.setUiFlow(uiFlow);


            RegistrationContentConfiguration registrationContentConfiguration = ((URLaunchInput) uappLaunchInput).
                    getRegistrationContentConfiguration();
            bundle.putSerializable(RegConstants.REGISTRATION_CONTENT_CONFIG, registrationContentConfiguration);


            bundle.putSerializable(RegConstants.REGISTRATION_LAUNCH_MODE, registrationLaunchMode);
            registrationFragment.setArguments(bundle);
            registrationFragment.setOnUpdateTitleListener(fragmentLauncher.
                    getActionbarListener());

            if (null != uappLaunchInput && null != ((URLaunchInput) uappLaunchInput).
                    getUserRegistrationUIEventListener()) {
                RegistrationConfiguration.getInstance().setUserRegistrationUIEventListener
                        (((URLaunchInput) uappLaunchInput).
                                getUserRegistrationUIEventListener());
                ((URLaunchInput) uappLaunchInput).setUserRegistrationUIEventListener(null);
            }

            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.replace(fragmentLauncher.getParentContainerResourceID(),
                    registrationFragment,
                    RegConstants.REGISTRATION_FRAGMENT_TAG);
            if (((URLaunchInput)
                    uappLaunchInput).isAddtoBackStack()) {
                fragmentTransaction.addToBackStack(RegConstants.REGISTRATION_FRAGMENT_TAG);
            }
            fragmentTransaction.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            RLog.e(RLog.EXCEPTION,
                    "RegistrationActivity :FragmentTransaction Exception occured in addFragment  :"
                            + e.getMessage());
        }

    }

    /**
     * It is used to launch USR as a activity
     * @param uiLauncher pass instance of ActivityLauncher
     * @param uappLaunchInput pass instance of  UappLaunchInput
     *                        @since 1.0.0
     */
    private void launchAsActivity(ActivityLauncher uiLauncher, UappLaunchInput uappLaunchInput) {

        if (null != uappLaunchInput) {
            RegistrationFunction registrationFunction = ((URLaunchInput) uappLaunchInput).
                    getRegistrationFunction();
            if (null != registrationFunction) {
                RegistrationConfiguration.getInstance().setPrioritisedFunction
                        (registrationFunction);
            }
            ThemeConfiguration themeConfiguration = uiLauncher.getDlsThemeConfiguration();
            if(themeConfiguration != null) {
                RegistrationHelper.getInstance().setThemeConfiguration(themeConfiguration);
            }
            int themeResId = uiLauncher.getUiKitTheme();
            RegistrationHelper.getInstance().setTheme(themeResId);
            RegistrationContentConfiguration registrationContentConfiguration = ((URLaunchInput) uappLaunchInput).
                    getRegistrationContentConfiguration();

            UIFlow uiFlow =((URLaunchInput) uappLaunchInput).getUIflow();
            RegUtility.setUiFlow(uiFlow);


            RegistrationConfiguration.getInstance().setUserRegistrationUIEventListener(((URLaunchInput) uappLaunchInput).
                    getUserRegistrationUIEventListener());
            ((URLaunchInput) uappLaunchInput).setUserRegistrationUIEventListener(null);
            Intent registrationIntent = new Intent(RegistrationHelper.getInstance().
                    getUrSettings().getContext(), RegistrationActivity.class);
            Bundle bundle = new Bundle();

            RegistrationLaunchMode registrationLaunchMode =  RegistrationLaunchMode.DEFAULT;

            if(((URLaunchInput)uappLaunchInput).getEndPointScreen()!=null){
                registrationLaunchMode = ((URLaunchInput)uappLaunchInput).getEndPointScreen();
            }

            bundle.putSerializable(RegConstants.REGISTRATION_UI_FLOW, uiFlow);
            bundle.putSerializable(RegConstants.REGISTRATION_LAUNCH_MODE, registrationLaunchMode);
            bundle.putSerializable(RegConstants.REGISTRATION_CONTENT_CONFIG, registrationContentConfiguration);
            bundle.putInt(RegConstants.ORIENTAION, uiLauncher.getScreenOrientation().
                    getOrientationValue());

            registrationIntent.putExtras(bundle);
            registrationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            RegistrationHelper.getInstance().
                    getUrSettings().getContext().startActivity(registrationIntent);
        }
    }

    /**
     * Entry point for USR. Please make sure no propositions are being used before URInterface$init.
     * @param uappDependencies pass instance of UappDependencies
     * @param uappSettings pass instance of UappSettings
     *                     @since 1.0.0
     */
    @Override
    public void init(UappDependencies uappDependencies, UappSettings uappSettings) {
        component = initDaggerComponents(uappDependencies, uappSettings);
        RegistrationConfiguration.getInstance().setComponent(component);
        Jump.init(uappSettings.getContext(), uappDependencies.getAppInfra().getSecureStorage());
        RegistrationHelper.getInstance().setUrSettings(uappSettings);
        RegistrationHelper.getInstance().initializeUserRegistration(uappSettings.getContext());
    }

    @NonNull
    private RegistrationComponent initDaggerComponents(UappDependencies uappDependencies, UappSettings uappSettings) {
        return DaggerRegistrationComponent.builder()
                    .networkModule(new NetworkModule(uappSettings.getContext()))
                    .appInfraModule(new AppInfraModule(uappDependencies.getAppInfra()))
                    .registrationModule(new RegistrationModule(uappSettings.getContext()))
                    .build();
    }
}
