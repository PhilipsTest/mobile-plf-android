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
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState;
import com.philips.platform.pim.listeners.UserLoginListener;
import com.philips.platform.pim.listeners.UserMigrationListener;
import com.philips.platform.pim.errors.PIMErrorEnums;
import com.philips.platform.pim.fragment.PIMFragment;
import com.philips.platform.pim.listeners.PIMLoginListener;
import com.philips.platform.pim.manager.PIMConfigManager;
import com.philips.platform.pim.manager.PIMLoginManager;
import com.philips.platform.pim.manager.PIMSettingManager;
import com.philips.platform.pim.manager.PIMUserManager;
import com.philips.platform.pim.migration.PIMMigrator;
import com.philips.platform.pim.models.PIMInitViewModel;
import com.philips.platform.pim.utilities.PIMInitState;
import com.philips.platform.pim.utilities.PIMSecureStorageHelper;
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
public class PIMInterface implements UappInterface, UserMigrationListener, PIMLoginListener {
    static final String PIM_KEY_ACTIVITY_THEME = "PIM_KEY_ACTIVITY_THEME";
    public static final String PIM_KEY_CONSENTS = "PIM_KEY_CONSENTS";
    private static final long serialVersionUID = 4160247155579172330L;
    private final String TAG = PIMInterface.class.getSimpleName();

    private Context context;
    private PIMMigrator pimMigrator;
    private UserMigrationListener userMigrationListener;
    private UserLoginListener userLoginListener;

    /**
     * API to initialize PIM. Please make sure no propositions are being used before PIMInterface$init.
     *
     * @param uappDependencies pass instance of UappDependencies
     * @param uappSettings     pass instance of UappSettings
     * @since TODO: Update version
     * 
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

        pimMigrator = new PIMMigrator(context, this);

        PIMSettingManager.getInstance().getLoggingInterface().log(DEBUG, TAG, "PIMInterface init called.");
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
            launchAsActivity(((ActivityLauncher) uiLauncher), (PIMLaunchInput) uappLaunchInput);
            PIMSettingManager.getInstance().getLoggingInterface().log(DEBUG, TAG, "Launch : Launched as activity");
        } else if (uiLauncher instanceof FragmentLauncher) {
            launchAsFragment((FragmentLauncher) uiLauncher, (PIMLaunchInput) uappLaunchInput);
            if (((PIMLaunchInput) uappLaunchInput).getUserLoginListener() != null)
                userLoginListener = ((PIMLaunchInput) uappLaunchInput).getUserLoginListener();
            PIMSettingManager.getInstance().getLoggingInterface().log(DEBUG, TAG, "Launch : Launched as fragment");
        }
    }

    /**
     * Get the User Data Interface
     */
    public UserDataInterface getUserDataInterface() {
        if (context == null) {
            PIMSettingManager.getInstance().getLoggingInterface().log(DEBUG, TAG, "getUserDataInterface: Context is null");
            return null;
        }
        return new PIMDataImplementation(context, PIMSettingManager.getInstance().getPimUserManager());
    }

    /**
     * To migrate user from USR to PIM
     *
     * @param userMigrationListener listener for migration
     */
    public void migrateJanrainUserToPIM(UserMigrationListener userMigrationListener) {
        PIMSettingManager.getInstance().getLoggingInterface().log(DEBUG, TAG, "migrateJanrainUserToPIM called : " + pimMigrator.isMigrationInProgress());
        final PIMUserManager pimUserManager = PIMSettingManager.getInstance().getPimUserManager();
        if (pimUserManager == null) {
            PIMSettingManager.getInstance().getLoggingInterface().log(DEBUG, TAG, "pimUserManager null : " + pimMigrator.isMigrationInProgress());
            userMigrationListener.onUserMigrationFailed(new Error(PIMErrorEnums.MIGRATION_FAILED.errorCode, PIMErrorEnums.getLocalisedErrorDesc(context, PIMErrorEnums.MIGRATION_FAILED.errorCode)));
            return;
        }
        if (pimUserManager.getUserLoggedInState() == UserLoggedInState.USER_LOGGED_IN) {
            PIMSettingManager.getInstance().getLoggingInterface().log(DEBUG, TAG, "USER_LOGGED_IN : " + pimMigrator.isMigrationInProgress());
            userMigrationListener.onUserMigrationSuccess();
            return;
        }
        this.userMigrationListener = userMigrationListener;

        MutableLiveData<PIMInitState> pimInitLiveData = PIMSettingManager.getInstance().getPimInitLiveData();
        PIMSettingManager.getInstance().getLoggingInterface().log(DEBUG, TAG, "After pimInitLiveData : " + pimMigrator.isMigrationInProgress());

        pimInitLiveData.observeForever(new Observer<PIMInitState>() {
            @Override
            public void onChanged(@Nullable PIMInitState pimInitState) {
                if (pimInitState == PIMInitState.INIT_SUCCESS) {
                    PIMSettingManager.getInstance().getLoggingInterface().log(DEBUG, TAG, "PIMInterface migrate init success : " + pimMigrator.isMigrationInProgress());
                    pimInitLiveData.removeObserver(this);
                    if (!pimMigrator.isMigrationInProgress())
                        pimMigrator.migrateUSRToPIM();
                    else
                        PIMSettingManager.getInstance().getLoggingInterface().log(DEBUG, TAG, "PIMMigration is in Progress : " + pimMigrator.isMigrationInProgress());
                } else if (pimInitState == PIMInitState.INIT_FAILED) {
                    PIMSettingManager.getInstance().getLoggingInterface().log(DEBUG, TAG, "PIMInterface migrate init failed : " + pimMigrator.isMigrationInProgress());
                    pimInitLiveData.removeObserver(this);
                    userMigrationListener.onUserMigrationFailed(new Error(PIMErrorEnums.MIGRATION_FAILED.errorCode, PIMErrorEnums.getLocalisedErrorDesc(context, PIMErrorEnums.MIGRATION_FAILED.errorCode)));
                }
            }
        });
    }

    public void setLoginListener(UserLoginListener userLoginListener) {
        this.userLoginListener = userLoginListener;
    }

    private final Observer<PIMInitState> observer = new Observer<PIMInitState>() {
        @Override
        public void onChanged(@Nullable PIMInitState pimInitState) {
            PIMSecureStorageHelper pimSecureStorageHelper = new PIMSecureStorageHelper(PIMSettingManager.getInstance().getAppInfraInterface());
            if (pimInitState == PIMInitState.INIT_SUCCESS) {
                PIMSettingManager.getInstance().getLoggingInterface().log(DEBUG, TAG, "PIMInterface main init success : " + pimMigrator.isMigrationInProgress());
                if (PIMSettingManager.getInstance().getPimUserManager().getUserLoggedInState() == UserLoggedInState.USER_LOGGED_IN) {
                    PIMSettingManager.getInstance().getLoggingInterface().log(DEBUG, TAG, "User is already logged in");
                } else if (pimSecureStorageHelper.getAuthorizationResponse() != null) {
                    loginRedirectToClosedApp();
                } else if (pimMigrator.isMigrationRequired() && !pimMigrator.isMigrationInProgress()) {
                    PIMSettingManager.getInstance().getLoggingInterface().log(DEBUG, TAG, "before silent migration called : " + pimMigrator.isMigrationInProgress());
                    pimMigrator.migrateUSRToPIM();
                    PIMSettingManager.getInstance().getLoggingInterface().log(DEBUG, TAG, "after silent migration called : " + pimMigrator.isMigrationInProgress());
                }
                PIMSettingManager.getInstance().getPimInitLiveData().removeObserver(observer);
            } else if (pimInitState == PIMInitState.INIT_FAILED) {
                PIMSettingManager.getInstance().getLoggingInterface().log(DEBUG, TAG, "after silent migration called : " + pimMigrator.isMigrationInProgress());
                PIMSettingManager.getInstance().getPimInitLiveData().removeObserver(observer);
            }
        }
    };

    private void loginRedirectToClosedApp() {
        PIMLoginManager pimLoginManager = new PIMLoginManager(context, PIMSettingManager.getInstance().getPimOidcConfigration(), null);
        pimLoginManager.exchangeCodeOnEmailVerify(this);
    }

    private void launchAsFragment(FragmentLauncher uiLauncher, PIMLaunchInput pimLaunchInput) {
        PIMFragment pimFragment = new PIMFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(PIM_KEY_CONSENTS, pimLaunchInput.getParameterToLaunch());
        pimFragment.setArguments(bundle);
        pimFragment.setActionbarListener(uiLauncher.getActionbarListener(), this);
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
            PIMSettingManager.getInstance().setUserLoginInerface(this);
            uiLauncher.getActivityContext().startActivity(intent);
        }
    }

    @Override
    public void onUserMigrationSuccess() {
        PIMSettingManager.getInstance().getLoggingInterface().log(DEBUG, TAG, "onUserMigrationSuccess called : " + pimMigrator.isMigrationInProgress());
        if (userMigrationListener != null)
            userMigrationListener.onUserMigrationSuccess();
    }

    @Override
    public void onUserMigrationFailed(Error error) {
        PIMSettingManager.getInstance().getLoggingInterface().log(DEBUG, TAG, "onUserMigrationFailed : " + pimMigrator.isMigrationInProgress());
        if (userMigrationListener != null)
            userMigrationListener.onUserMigrationFailed(error);
    }

    @Override
    public void onLoginSuccess() {
        if (userLoginListener != null)
            userLoginListener.onLoginSuccess();
    }

    @Override
    public void onLoginFailed(Error error) {
        if (userLoginListener != null)
            userLoginListener.onLoginFailed(error);
    }
}
