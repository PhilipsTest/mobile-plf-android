package com.philips.platform.csw;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.philips.platform.catk.CatkConstants;
import com.philips.platform.catk.CatkInputs;
import com.philips.platform.catk.ConsentAccessToolKit;
import com.philips.platform.catk.injection.CatkComponentFactory;
import com.philips.platform.catk.provider.ComponentProvider;
import com.philips.platform.csw.injection.AppInfraModule;
import com.philips.platform.csw.injection.CswComponent;
import com.philips.platform.csw.injection.CswModule;
import com.philips.platform.csw.injection.DaggerCswComponent;
import com.philips.platform.csw.utils.CswLogger;
import com.philips.platform.uappframework.UappInterface;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;
import com.philips.platform.uappframework.uappinput.UappDependencies;
import com.philips.platform.uappframework.uappinput.UappLaunchInput;
import com.philips.platform.uappframework.uappinput.UappSettings;

public class CswInterface implements UappInterface {

    private UiLauncher uiLauncher;
    private CswLaunchInput cswLaunchInput;

    /**
     * Launches the CswInterface interface. The component can be launched either with an ActivityLauncher or a FragmentLauncher.
     *
     * @param uiLauncher      - ActivityLauncher or FragmentLauncher
     * @param uappLaunchInput - CswLaunchInput
     */
    @Override
    public void launch(UiLauncher uiLauncher, UappLaunchInput uappLaunchInput) {
        this.uiLauncher = uiLauncher;
        this.cswLaunchInput = (CswLaunchInput)uappLaunchInput;
        launch();
    }

    private void launch() {
        if (uiLauncher instanceof ActivityLauncher) {
            launchAsActivity(((ActivityLauncher) uiLauncher), cswLaunchInput);
        } else if (uiLauncher instanceof FragmentLauncher) {
            launchAsFragment((FragmentLauncher) uiLauncher, cswLaunchInput);
        }
    }

    private void launchAsFragment(FragmentLauncher fragmentLauncher, CswLaunchInput uappLaunchInput) {
        try {
            FragmentManager mFragmentManager = fragmentLauncher.getFragmentActivity().
                    getSupportFragmentManager();
            CswFragment cswFragment = new CswFragment();
            cswFragment.setOnUpdateTitleListener(fragmentLauncher.
                    getActionbarListener());

            Bundle fragmentConfig = uappLaunchInput.getConfig().toBundle();
            fragmentConfig.putBoolean(CatkConstants.BUNDLE_KEY_ADDTOBACKSTACK, uappLaunchInput.isAddtoBackStack());
            cswFragment.setArguments(fragmentConfig);

            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

            if (uappLaunchInput.isAddtoBackStack()) {
                fragmentTransaction.addToBackStack(CswConstants.CSWFRAGMENT);
            }

            fragmentTransaction.replace(fragmentLauncher.getParentContainerResourceID(),
                    cswFragment,
                    CswConstants.CSWFRAGMENT);
            fragmentTransaction.commitAllowingStateLoss();
        } catch (IllegalStateException ignore) {

        }
    }

    private void launchAsActivity(ActivityLauncher uiLauncher, CswLaunchInput uappLaunchInput) {
        if (null != uiLauncher && uappLaunchInput != null) {
            Intent cswIntent = new Intent(uappLaunchInput.getContext(), CswActivity.class);
            cswIntent.putExtras(uappLaunchInput.getConfig().toBundle());

            cswIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            uappLaunchInput.getContext().startActivity(cswIntent);
        }
    }

    /**
     * Entry point for User registration. Please make sure no User registration components are being used before CswInterface$init.
     *
     * @param uappDependencies - With an AppInfraInterface instance.
     * @param uappSettings     - With an application provideAppContext.
     */
    @Override
    public void init(UappDependencies uappDependencies, UappSettings uappSettings) {
        ConsentAccessToolKit.getInstance().init(initConsentToolKit(uappDependencies, uappSettings));
        cswComponent = initDaggerComponents(uappDependencies, uappSettings);
        CswLogger.init();
        CswLogger.enableLogging();
    }

    public CatkInputs initConsentToolKit(UappDependencies uappDependencies, UappSettings uappSettings) {
        CatkInputs catkInputs = new CatkInputs();
        catkInputs.setContext(uappSettings.getContext());
        catkInputs.setAppInfra(uappDependencies.getAppInfra());
        catkInputs.setApplicationName(((CswDependencies) uappDependencies).getApplicationName());
        catkInputs.setPropositionName(((CswDependencies) uappDependencies).getPropositionName());
        return catkInputs;
    }

    private CswComponent initDaggerComponents(UappDependencies uappDependencies, UappSettings uappSettings) {
        return DaggerCswComponent.builder()
                .cswModule(new CswModule(uappSettings.getContext()))
                .appInfraModule(new AppInfraModule(uappDependencies.getAppInfra())).build();
    }

    public static CswComponent getCswComponent() {
        return cswComponent;
    }

    private static CswComponent cswComponent;

}
