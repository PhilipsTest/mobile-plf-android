/*
 * Copyright (c) 2017 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.csw;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.philips.platform.csw.injection.AppInfraModule;
import com.philips.platform.csw.injection.CswComponent;
import com.philips.platform.csw.injection.CswModule;
import com.philips.platform.csw.injection.DaggerCswComponent;
import com.philips.platform.csw.permission.PermissionFragment;
import com.philips.platform.csw.utils.CswLogger;
import com.philips.platform.uappframework.UappInterface;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;
import com.philips.platform.uappframework.uappinput.UappDependencies;
import com.philips.platform.uappframework.uappinput.UappLaunchInput;
import com.philips.platform.uappframework.uappinput.UappSettings;

import java.io.Serializable;

public class CswInterface implements UappInterface {
    private static final String TAG = "CswInterface";

    private static final CswInterface reference = new CswInterface();
    private static CswComponent cswComponent;

    private UiLauncher uiLauncher;
    private CswLaunchInput cswLaunchInput;
    private CswDependencies uappDependencies;

    public static CswInterface get() {
        return reference;
    }

    public static CswComponent getCswComponent() {
        return cswComponent;
    }

    /**
     * Entry point for User registration. Please make sure no User registration components are being used before CswInterface$init.
     *
     * @param uappDependencies - With an AppInfraInterface instance.
     * @param uappSettings     - With an application provideAppContext.
     */
    @Override
    public void init(UappDependencies uappDependencies, UappSettings uappSettings) {
        if (!(uappDependencies instanceof CswDependencies)) {
            throw new IllegalStateException("Illegal state! Must be instance of CswDependencies");
        }

        CswDependencies ourDeps = (CswDependencies) uappDependencies;

        cswComponent = initDaggerComponents(uappDependencies, uappSettings);
        this.uappDependencies = (CswDependencies) uappDependencies;

        reference.uappDependencies = ourDeps;

        CswLogger.init();
        CswLogger.enableLogging();
    }

    /**
     * Launches the CswInterface interface. The component can be launched either with an ActivityLauncher or a FragmentLauncher.
     *
     * @param uiLauncher      - ActivityLauncher or FragmentLauncher
     * @param uappLaunchInput - CswLaunchInput
     */
    @Override
    public void launch(UiLauncher uiLauncher, UappLaunchInput uappLaunchInput) {
        this.uiLauncher = uiLauncher;
        this.cswLaunchInput = (CswLaunchInput) uappLaunchInput;
        launch();
    }

    public CswDependencies getDependencies() {
        return uappDependencies;
    }

    private void launch() {
        if (uiLauncher instanceof ActivityLauncher) {
            launchAsActivity(((ActivityLauncher) uiLauncher), cswLaunchInput);
        } else if (uiLauncher instanceof FragmentLauncher) {
            launchAsFragment((FragmentLauncher) uiLauncher, cswLaunchInput);
        }
    }

    private void launchAsFragment(FragmentLauncher fragmentLauncher, CswLaunchInput cswLaunchInput) {
        try {
            FragmentManager mFragmentManager = fragmentLauncher.getFragmentActivity().getSupportFragmentManager();
            PermissionFragment permissionFragment = new PermissionFragment();
            Bundle args = new Bundle();
            args.putSerializable(CswConstants.CONSENT_DEFINITIONS, (Serializable) cswLaunchInput.getConsentDefinitionList());
            permissionFragment.setArguments(args);
            permissionFragment.setUpdateTitleListener(fragmentLauncher.getActionbarListener());
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.addToBackStack(PermissionFragment.TAG);
            fragmentTransaction.replace(fragmentLauncher.getParentContainerResourceID(), permissionFragment, PermissionFragment.TAG);
            fragmentTransaction.commitAllowingStateLoss();
        } catch (IllegalStateException exception) {
            Log.e(TAG, "Could not launch MYA ConsentWidgets as Fragment. See stacktrace:", exception);
        }
    }

    private void launchAsActivity(ActivityLauncher uiLauncher, CswLaunchInput uappLaunchInput) {
        if (null != uiLauncher && uappLaunchInput != null) {
            Intent cswIntent = new Intent(uappLaunchInput.getContext(), CswActivity.class);
            cswIntent.putExtra(CswConstants.DLS_THEME, uiLauncher.getUiKitTheme());
            cswIntent.putExtra(CswConstants.CONSENT_DEFINITIONS, (Serializable) uappLaunchInput.getConsentDefinitionList());
            cswIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            uappLaunchInput.getContext().startActivity(cswIntent);
            reference.cswLaunchInput = uappLaunchInput;
        }
    }

    private CswComponent initDaggerComponents(UappDependencies uappDependencies, UappSettings uappSettings) {
        return DaggerCswComponent.builder()
                .cswModule(new CswModule(uappSettings.getContext()))
                .appInfraModule(new AppInfraModule(uappDependencies.getAppInfra())).build();
    }
}
