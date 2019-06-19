/*
 * Copyright (c) Koninklijke Philips N.V. 2019
 * All rights are reserved. Reproduction or dissemination in whole or in part
 * is prohibited without the prior written consent of the copyright holder.
 */

package com.philips.platform.pim;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.pim.fragment.PIMFragment;
import com.philips.platform.pim.manager.PIMConfigManager;
import com.philips.platform.pim.manager.PIMSettingManager;
import com.philips.platform.pim.manager.PIMUserManager;
import com.philips.platform.pim.models.PIMInitViewModel;
import com.philips.platform.pim.utilities.PIMInitState;
import com.philips.platform.uappframework.UappInterface;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;
import com.philips.platform.uappframework.uappinput.UappDependencies;
import com.philips.platform.uappframework.uappinput.UappLaunchInput;
import com.philips.platform.uappframework.uappinput.UappSettings;

import static com.philips.platform.appinfra.logging.LoggingInterface.LogLevel.DEBUG;

/**
 * Used to initialize and launch PIM
 *
 * @since TODO:Shashi need to update pim version
 */
public class PIMInterface implements UappInterface {
    private static final String PIM_KEY_ACTIVITY_THEME = "PIM_KEY_ACTIVITY_THEME";
    private final String TAG = PIMInterface.class.getSimpleName();
    private LoggingInterface mLoggingInterface;

    private Context context;
    private PIMInitViewModel pimInitViewModel;

    /**
     * API to initialize PIM. Please make sure no propositions are being used before PIMInterface$init.
     *
     * @param uappDependencies pass instance of UappDependencies
     * @param uappSettings     pass instance of UappSettings
     * @since TODO: Update version
     */
    @Override
    public void init(@NonNull UappDependencies uappDependencies, @NonNull UappSettings uappSettings) {
        context = uappSettings.getContext();

        pimInitViewModel = ViewModelProviders.of((FragmentActivity)context).get(PIMInitViewModel.class);

        MutableLiveData<PIMInitState> livedata = pimInitViewModel.getMuatbleInitLiveData();
        livedata.setValue(PIMInitState.INIT_IN_PROGRESS);

        PIMSettingManager.getInstance().setPIMInitLiveData(livedata);
        PIMSettingManager.getInstance().init(uappDependencies);
        PIMUserManager pimUserManager = new PIMUserManager();
        PIMSettingManager.getInstance().setPimUserManager(pimUserManager);
        pimUserManager.init(context, uappDependencies.getAppInfra());
        PIMConfigManager pimConfigManager = new PIMConfigManager(pimUserManager);
        pimConfigManager.init(uappDependencies.getAppInfra().getServiceDiscovery());

        mLoggingInterface = PIMSettingManager.getInstance().getLoggingInterface();
        mLoggingInterface.log(DEBUG, TAG, "PIMInterface init called.");
    }

    /**
     * Launches the PIM user interface. The component can be launched either with an ActivityLauncher or a FragmentLauncher.
     *
     * @param uiLauncher      pass ActivityLauncher or FragmentLauncher
     * @param uappLaunchInput pass instance of  URLaunchInput
     * @since TODO: Update version
     */
    @Override
    public void launch(UiLauncher uiLauncher, UappLaunchInput uappLaunchInput) {

        if (uiLauncher instanceof ActivityLauncher) {
            launchAsActivity(((ActivityLauncher) uiLauncher), uappLaunchInput);
            mLoggingInterface.log(DEBUG, TAG, "Launch : Launched as activity");
        } else if (uiLauncher instanceof FragmentLauncher) {
            launchAsFragment((FragmentLauncher) uiLauncher, (PIMLaunchInput) uappLaunchInput);
            mLoggingInterface.log(DEBUG, TAG, "Launch : Launched as fragment");
        }
    }

    private void launchAsFragment(FragmentLauncher uiLauncher, PIMLaunchInput pimLaunchInput) {
        PIMFragment pimFragment = new PIMFragment();
        addFragment(uiLauncher, pimFragment);
    }

    private void addFragment(FragmentLauncher uiLauncher, Fragment fragment) {
        uiLauncher.getFragmentActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(uiLauncher.getParentContainerResourceID(), fragment, fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    private void launchAsActivity(ActivityLauncher uiLauncher, UappLaunchInput uappLaunchInput) {
        Intent intent = new Intent(uiLauncher.getActivityContext(), PIMActivity.class);
        intent.putExtra(PIM_KEY_ACTIVITY_THEME, uiLauncher.getUiKitTheme());
        uiLauncher.getActivityContext().startActivity(intent);
    }

    /**
     * Get the User Data Interface
     *
     * @since TODO: Update PIM version
     */
    public UserDataInterface getUserDataInterface() {
        if (context == null) {
            mLoggingInterface.log(DEBUG, TAG, "getUserDataInterface: Context is null");
            return null;
        }
        return new PIMDataImplementation(context, PIMSettingManager.getInstance().getPimUserManager());
    }
}
