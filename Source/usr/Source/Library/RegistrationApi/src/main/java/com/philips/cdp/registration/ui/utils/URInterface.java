package com.philips.cdp.registration.ui.utils;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.janrain.android.Jump;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.configuration.RegistrationLaunchMode;
import com.philips.cdp.registration.consents.MarketingConsentHandler;
import com.philips.cdp.registration.consents.URConsentProvider;
import com.philips.cdp.registration.dao.UserDataProvider;
import com.philips.cdp.registration.injection.AppInfraModule;
import com.philips.cdp.registration.injection.DaggerRegistrationComponent;
import com.philips.cdp.registration.injection.NetworkModule;
import com.philips.cdp.registration.injection.RegistrationComponent;
import com.philips.cdp.registration.injection.RegistrationModule;
import com.philips.cdp.registration.settings.RegistrationFunction;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.ui.traditional.RegistrationActivity;
import com.philips.cdp.registration.ui.traditional.RegistrationFragment;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.uappframework.UappInterface;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;
import com.philips.platform.uappframework.uappinput.UappDependencies;
import com.philips.platform.uappframework.uappinput.UappLaunchInput;
import com.philips.platform.uappframework.uappinput.UappSettings;
import com.philips.platform.uid.thememanager.ThemeConfiguration;

import java.util.Collections;

import static com.janrain.android.utils.AndroidUtils.TAG;

/**
 * It is used to initialize and launch USR
 *
 * @since 1.0.0
 */

public class URInterface implements UappInterface {

    private static RegistrationComponent component;
    private Context context;

    private static final long serialVersionUID = 1128016096756071381L;


    /**
     * Launches the USR user interface. The component can be launched either with an ActivityLauncher or a FragmentLauncher.
     *
     * @param uiLauncher      pass ActivityLauncher or FragmentLauncher
     * @param uappLaunchInput pass instance of  URLaunchInput
     * @since 1.0.0
     */
    @Override
    public void launch(UiLauncher uiLauncher, UappLaunchInput uappLaunchInput) {
        if (uiLauncher instanceof ActivityLauncher) {
            launchAsActivity(((ActivityLauncher) uiLauncher), uappLaunchInput);
        } else if (uiLauncher instanceof FragmentLauncher) {
            launchAsFragment((FragmentLauncher) uiLauncher, uappLaunchInput);
        }
    }

    /**
     * It is used to launch USR as a fragment
     *
     * @param fragmentLauncher pass instance of FragmentLauncher
     * @param uappLaunchInput  pass instance of UappLaunchInput
     * @since 1.0.0
     */
    private void launchAsFragment(FragmentLauncher fragmentLauncher,
                                  UappLaunchInput uappLaunchInput) {
        try {
            RegistrationFunction registrationFunction = ((URLaunchInput) uappLaunchInput).
                    getRegistrationFunction();
            if (null != registrationFunction) {
                RegistrationConfiguration.getInstance().setPrioritisedFunction
                        (registrationFunction);
            }
            FragmentManager mFragmentManager = fragmentLauncher.getFragmentActivity().
                    getSupportFragmentManager();
            RegistrationFragment registrationFragment = new RegistrationFragment();
            Bundle bundle = new Bundle();

            RegistrationLaunchMode registrationLaunchMode = ((URLaunchInput) uappLaunchInput).getEndPointScreen();
            UIFlow uiFlow = ((URLaunchInput) uappLaunchInput).getUIflow();
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
     *
     * @param uiLauncher      pass instance of ActivityLauncher
     * @param uappLaunchInput pass instance of  UappLaunchInput
     * @since 1.0.0
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
            if (themeConfiguration != null) {
                RegistrationHelper.getInstance().setThemeConfiguration(themeConfiguration);
            }
            int themeResId = uiLauncher.getUiKitTheme();
            RegistrationHelper.getInstance().setTheme(themeResId);
            RegistrationContentConfiguration registrationContentConfiguration = ((URLaunchInput) uappLaunchInput).
                    getRegistrationContentConfiguration();

            UIFlow uiFlow = ((URLaunchInput) uappLaunchInput).getUIflow();
            RegUtility.setUiFlow(uiFlow);


            RegistrationConfiguration.getInstance().setUserRegistrationUIEventListener(((URLaunchInput) uappLaunchInput).
                    getUserRegistrationUIEventListener());
            ((URLaunchInput) uappLaunchInput).setUserRegistrationUIEventListener(null);
            Intent registrationIntent = new Intent(RegistrationHelper.getInstance().
                    getUrSettings().getContext(), RegistrationActivity.class);
            Bundle bundle = new Bundle();

            RegistrationLaunchMode registrationLaunchMode = ((URLaunchInput) uappLaunchInput).getEndPointScreen();

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
     *
     * @param uappDependencies pass instance of UappDependencies
     * @param uappSettings     pass instance of UappSettings
     * @since 1.0.0
     */
    @Override
    public void init(UappDependencies uappDependencies, UappSettings uappSettings) {
        component = initDaggerComponents(uappDependencies, uappSettings);
        context = uappSettings.getContext();
        RegistrationConfiguration.getInstance().setComponent(component);
        Jump.init(uappSettings.getContext(), uappDependencies.getAppInfra().getSecureStorage());
        RegistrationHelper.getInstance().setUrSettings(uappSettings);
        RegistrationHelper.getInstance().initializeUserRegistration(uappSettings.getContext());
        uappDependencies.getAppInfra().getConsentManager().register(Collections.singletonList(URConsentProvider.USR_MARKETING_CONSENT), new MarketingConsentHandler(context));
    }

    @NonNull
    private RegistrationComponent initDaggerComponents(UappDependencies uappDependencies, UappSettings uappSettings) {
        return DaggerRegistrationComponent.builder()
                .networkModule(new NetworkModule(uappSettings.getContext()))
                .appInfraModule(new AppInfraModule(uappDependencies.getAppInfra()))
                .registrationModule(new RegistrationModule(uappSettings.getContext()))
                .build();
    }

    /**
     * Get the User Data Interface
     * @since 2018.1.0
     *
     */
    public UserDataInterface getUserDataInterface(){
        if(context == null) {
            RLog.d(TAG, "getUserDataInterface: Please call init API before fetching data interface");
            return null;
        }
        else {

            return new UserDataProvider(context);
        }

    }
}
