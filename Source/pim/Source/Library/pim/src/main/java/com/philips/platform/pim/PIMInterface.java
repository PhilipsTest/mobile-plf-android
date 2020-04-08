/*
 * Copyright (c) Koninklijke Philips N.V. 2019
 * All rights are reserved. Reproduction or dissemination in whole or in part
 * is prohibited without the prior written consent of the copyright holder.
 */

package com.philips.platform.pim;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState;
import com.philips.platform.pim.fragment.PIMFragment;
import com.philips.platform.pim.manager.PIMConfigManager;
import com.philips.platform.pim.manager.PIMSettingManager;
import com.philips.platform.pim.manager.PIMUserManager;
import com.philips.platform.pim.migration.PIMMigrator;
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
 */
public class PIMInterface implements UappInterface {
    static final String PIM_KEY_ACTIVITY_THEME = "PIM_KEY_ACTIVITY_THEME";
    public static final String PIM_KEY_CONSENTS = "PIM_KEY_CONSENTS";
    private final String TAG = PIMInterface.class.getSimpleName();

    private Context context;

    /**
     * API to initialize PIM. Please make sure no propositions are being used before PIMInterface$init.
     *
     * @param uappDependencies pass instance of UappDependencies
     * @param uappSettings     pass instance of UappSettings
     * @since TODO: Update version
     */
    @Override
    public void init(@NonNull UappDependencies uappDependencies, @NonNull UappSettings uappSettings) {
        context = uappSettings.getContext().getApplicationContext();

        PIMInitViewModel pimInitViewModel = new PIMInitViewModel((Application) context);
        MutableLiveData<PIMInitState> livedata = pimInitViewModel.getMuatbleInitLiveData();
        livedata.observeForever(observer);
        livedata.postValue(PIMInitState.INIT_IN_PROGRESS);

        PIMSettingManager.getInstance().setPIMInitLiveData(livedata);
        PIMSettingManager.getInstance().init(uappDependencies);

        PIMUserManager pimUserManager = new PIMUserManager();
        PIMSettingManager.getInstance().setPimUserManager(pimUserManager);
        pimUserManager.init(context, uappDependencies.getAppInfra());
        PIMConfigManager pimConfigManager = new PIMConfigManager(pimUserManager);
        pimConfigManager.init(uappSettings.getContext(), uappDependencies.getAppInfra().getServiceDiscovery());

        PIMSettingManager.getInstance().getLoggingInterface().log(DEBUG, TAG, "PIMInterface init called.");
    }

    private Observer<PIMInitState> observer = new Observer<PIMInitState>() {
        @Override
        public void onChanged(@Nullable PIMInitState pimInitState) {
            if (pimInitState == PIMInitState.INIT_SUCCESS) {
                if (PIMSettingManager.getInstance().getPimUserManager().getUserLoggedInState() == UserLoggedInState.USER_NOT_LOGGED_IN) {
                    PIMMigrator pimMigrator = new PIMMigrator(context);
                    pimMigrator.migrateUSRToPIM();
                } else {
                    PIMSettingManager.getInstance().getLoggingInterface().log(DEBUG, TAG, "User is already logged in");
                }
                PIMSettingManager.getInstance().getPimInitLiveData().removeObserver(observer);
            } else if (pimInitState == PIMInitState.INIT_FAILED) {
                PIMSettingManager.getInstance().getPimInitLiveData().removeObserver(observer);
            }
        }
    };

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
            launchAsActivity(((ActivityLauncher) uiLauncher), (PIMLaunchInput) uappLaunchInput);
            PIMSettingManager.getInstance().getLoggingInterface().log(DEBUG, TAG, "Launch : Launched as activity");
        } else if (uiLauncher instanceof FragmentLauncher) {
            launchAsFragment((FragmentLauncher) uiLauncher, (PIMLaunchInput) uappLaunchInput);
            PIMSettingManager.getInstance().getLoggingInterface().log(DEBUG, TAG, "Launch : Launched as fragment");
        }
    }

    private void launchAsFragment(FragmentLauncher uiLauncher, PIMLaunchInput pimLaunchInput) {
        PIMFragment pimFragment = new PIMFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(PIM_KEY_CONSENTS, pimLaunchInput.getParameterToLaunch());
        pimFragment.setArguments(bundle);
        pimFragment.setActionbarListener(uiLauncher.getActionbarListener(), pimLaunchInput.getUserLoginListener());
        addFragment(uiLauncher, pimFragment);
    }

    private void addFragment(FragmentLauncher uiLauncher, Fragment fragment) {
        uiLauncher.getFragmentActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(uiLauncher.getParentContainerResourceID(), fragment, fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    private void launchAsActivity(ActivityLauncher uiLauncher, PIMLaunchInput pimLaunchInput) {
        if (null != pimLaunchInput) {
            Intent intent = new Intent(uiLauncher.getActivityContext(), PIMActivity.class);
            intent.putExtra(PIM_KEY_ACTIVITY_THEME, uiLauncher.getUiKitTheme());
            intent.putExtra(PIM_KEY_CONSENTS, pimLaunchInput.getParameterToLaunch());
            PIMSettingManager.getInstance().setUserLoginInerface(pimLaunchInput.getUserLoginListener());

            uiLauncher.getActivityContext().startActivity(intent);
        }
    }

    /**
     * Get the User Data Interface
     *
     * @since TODO: Update PIM version
     */
    public UserDataInterface getUserDataInterface() {
        if (context == null) {
            PIMSettingManager.getInstance().getLoggingInterface().log(DEBUG, TAG, "getUserDataInterface: Context is null");
            return null;
        }
        return new PIMDataImplementation(context, PIMSettingManager.getInstance().getPimUserManager());
    }
}
