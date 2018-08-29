/**
 * Copyright (c) Koninklijke Philips N.V., 2017.
 * All rights reserved.
 */
package com.philips.platform.ews.microapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.philips.platform.ews.EWSActivity;
import com.philips.platform.ews.R;
import com.philips.platform.ews.configuration.ContentConfiguration;
import com.philips.platform.ews.injections.DaggerEWSComponent;
import com.philips.platform.ews.injections.DependencyHelper;
import com.philips.platform.ews.injections.EWSConfigurationModule;
import com.philips.platform.ews.injections.EWSDependencyProviderModule;
import com.philips.platform.ews.injections.EWSModule;
import com.philips.platform.ews.navigation.Navigator;
import com.philips.platform.uappframework.UappInterface;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;
import com.philips.platform.uappframework.uappinput.UappDependencies;
import com.philips.platform.uappframework.uappinput.UappLaunchInput;
import com.philips.platform.uappframework.uappinput.UappSettings;

/*
 * EWSInterface is an entry point for EWS launching,
 * All the initialisation for EWS should be done using it.
 */
@SuppressWarnings("WeakerAccess")
public class EWSUapp implements UappInterface {

    public static final String ERROR_MSG_INVALID_CALL = "Please call \"init\" method, before calling launching ews with valid params";
    public static final String SCREEN_ORIENTATION = "screen.orientation";
    public static final String PRODUCT_NAME = "productName";
    private static final String TAG = "EWSInterface";
    @NonNull
    private Navigator navigator;

    @NonNull
    private Context context;

    private DependencyHelper dependencyHelper;

    /**
     * Entry point for EWS. Please make sure no EWS components are being used before EWSInterface$init.
     *
     * @param uappDependencies - With an AppInfraInterface instance.
     * @param uappSettings     - With an application context.
     */
    @Override
    public void init(@NonNull final UappDependencies uappDependencies, @NonNull final UappSettings uappSettings) {
        EWSDependencies ewsDependencies = (EWSDependencies) uappDependencies;
        ContentConfiguration contentConfiguration = ewsDependencies.getContentConfiguration();
        if (contentConfiguration == null) {
            contentConfiguration = new ContentConfiguration();
        }
        dependencyHelper = new DependencyHelper(uappDependencies.getAppInfra(), ewsDependencies.getCommCentral(), ewsDependencies.getProductKeyMap(), contentConfiguration);
        context = uappSettings.getContext();
    }

    /**
     * Launches the EWS user interface. The component can be launched either with an ActivityLauncher or a FragmentLauncher.
     *
     * @param uiLauncher      - ActivityLauncher or FragmentLauncher
     * @param uappLaunchInput - URLaunchInput
     */
    @Override
    public void launch(@NonNull final UiLauncher uiLauncher, @NonNull final UappLaunchInput uappLaunchInput) {
        if (!DependencyHelper.areDependenciesInitialized()) {
            throw new UnsupportedOperationException(ERROR_MSG_INVALID_CALL);
        }

        if (uiLauncher instanceof FragmentLauncher) {
            launchAsFragment((FragmentLauncher) uiLauncher, uappLaunchInput);
        } else if (uiLauncher instanceof ActivityLauncher) {
            dependencyHelper.setThemeConfiguration(((ActivityLauncher) uiLauncher).getDlsThemeConfiguration());
            launchAsActivity(uappLaunchInput);
        }
    }

    @VisibleForTesting
    void launchAsFragment(@NonNull final FragmentLauncher fragmentLauncher, @NonNull final UappLaunchInput uappLaunchInput) {
        EWSDependencyProviderModule ewsDependencyProviderModule = new EWSDependencyProviderModule(DependencyHelper.getAppInfraInterface(), DependencyHelper.getProductKeyMap());
        try {
            EWSModule ewsModule = new EWSModule(fragmentLauncher.getFragmentActivity(),
                    fragmentLauncher.getFragmentActivity().getSupportFragmentManager(),
                    fragmentLauncher.getParentContainerResourceID(), DependencyHelper.getCommCentral());

            DaggerEWSComponent.builder()
                    .eWSModule(ewsModule)
                    .eWSConfigurationModule(new EWSConfigurationModule(fragmentLauncher.getFragmentActivity(), DependencyHelper.getContentConfiguration()))
                    .eWSDependencyProviderModule(ewsDependencyProviderModule)
                    .build();
            navigator = ewsModule.provideNavigator();

            ((EWSLauncherInput) uappLaunchInput).setContainerFrameId(fragmentLauncher.getParentContainerResourceID());
            ((EWSLauncherInput) uappLaunchInput).setFragmentManager(fragmentLauncher.getFragmentActivity().getSupportFragmentManager());
            navigator.navigateToGettingStartedScreen();
            ewsDependencyProviderModule.provideEWSTagger().collectLifecycleInfo(fragmentLauncher.getFragmentActivity());
        } catch (Exception e) {
            ewsDependencyProviderModule.provideEWSLogger().e(TAG,
                    "RegistrationActivity :FragmentTransaction Exception occured in addFragment  :"
                            + e.getMessage());
        }
    }

    private void launchAsActivity(@NonNull final UappLaunchInput uappLaunchInput) {
        ((EWSLauncherInput) uappLaunchInput).setContainerFrameId(R.id.contentFrame);
        Intent intent = new Intent(context, EWSActivity.class);
        intent.putExtra(SCREEN_ORIENTATION, ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_PORTRAIT);
        intent.putExtra(EWSActivity.KEY_CONTENT_CONFIGURATION, DependencyHelper.getContentConfiguration());
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, EwsResultListener.EWS_RESULT_SUCCESS);
        } else {
            context.startActivity(intent);
        }
    }

}