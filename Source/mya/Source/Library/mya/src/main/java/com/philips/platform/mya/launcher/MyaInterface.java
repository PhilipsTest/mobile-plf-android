/*
 * Copyright (c) 2017 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.mya.launcher;

import android.content.Intent;
import android.os.Bundle;

import com.philips.cdp.registration.UserLoginState;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;
import com.philips.platform.mya.MyaHelper;
import com.philips.platform.mya.activity.MyaActivity;
import com.philips.platform.mya.error.MyaError;
import com.philips.platform.mya.tabs.MyaTabFragment;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.uappframework.UappInterface;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;
import com.philips.platform.uappframework.uappinput.UappDependencies;
import com.philips.platform.uappframework.uappinput.UappLaunchInput;
import com.philips.platform.uappframework.uappinput.UappSettings;

import static com.philips.platform.mya.activity.MyaActivity.MYA_DLS_THEME;

/**
 * This class is used to launch My Account either as fragment or activity.
 *
 * @since 2018.1.0
 */
public class MyaInterface implements UappInterface {
    private static final long serialVersionUID = -1294841605213614728L;
    public static String USER_PLUGIN = "user_plugin";

    /**
     * API to initialize My Account
     *
     * @param uappDependencies - With an AppInfraInterface instance.
     * @param uappSettings     - With an application provideAppContext.
     * @since 2018.1.0
     */
    @Override
    public void init(UappDependencies uappDependencies, UappSettings uappSettings) {
        if (!(uappDependencies instanceof MyaDependencies)) {
            throw new IllegalArgumentException("uappDependencies must be an instance of MyaDependencies.");
        }
        MyaDependencies myaDependencies = (MyaDependencies) uappDependencies;
        MyaHelper.getInstance().setAppInfra(myaDependencies.getAppInfra());
        MyaHelper.getInstance().setMyaLogger(myaDependencies.getAppInfra().getLogging().createInstanceForComponent(MyaHelper.MYA_TLA, com.philips.platform.mya.BuildConfig.VERSION_NAME));
        MyaHelper.getInstance().setAppTaggingInterface(getTaggingInterface(myaDependencies));
    }

    /**
     * Launches the My Account interface. The component can be launched either with an ActivityLauncher or a FragmentLauncher.
     *
     * @param uiLauncher      - ActivityLauncher or FragmentLauncher
     * @param uappLaunchInput - MyaLaunchInput
     * @since 2018.1.0
     */
    @Override
    public void launch(UiLauncher uiLauncher, UappLaunchInput uappLaunchInput) {
        MyaLaunchInput myaLaunchInput = (MyaLaunchInput) uappLaunchInput;
        MyaHelper.getInstance().setUserDataInterface(myaLaunchInput.getUserDataInterface());
        if (myaLaunchInput.getMyaListener() == null)
            MyaHelper.getInstance().getMyaLogger().log(LoggingInterface.LogLevel.DEBUG, MyaHelper.MYA_TLA, "Mya Listener not set");

        UserDataInterface userDataInterface = getUserDataInterface();
        if (userDataInterface.getUserLoggedInState().ordinal() < UserLoginState.PENDING_HSDP_LOGIN.ordinal()) {
            myaLaunchInput.getMyaListener().onError(MyaError.USERNOTLOGGEDIN);
            return;
        }
        MyaHelper.getInstance().setMyaLaunchInput(myaLaunchInput);
        MyaHelper.getInstance().setMyaListener(myaLaunchInput.getMyaListener());

        Bundle bundle = new Bundle();
        bundle.putSerializable(USER_PLUGIN, userDataInterface);


        if (uiLauncher instanceof ActivityLauncher) {
            ActivityLauncher activityLauncher = (ActivityLauncher) uiLauncher;
            MyaHelper.getInstance().setThemeConfiguration(activityLauncher.getDlsThemeConfiguration());
            launchAsActivity(activityLauncher, myaLaunchInput);
        } else if (uiLauncher instanceof FragmentLauncher) {
            launchAsFragment((FragmentLauncher) uiLauncher, bundle);
        }
    }

    private void launchAsFragment(FragmentLauncher fragmentLauncher, Bundle arguments) {
        MyaTabFragment myaTabFragment = new MyaTabFragment();
        myaTabFragment.setArguments(arguments);
        myaTabFragment.setFragmentLauncher(fragmentLauncher);
        myaTabFragment.setActionbarUpdateListener(fragmentLauncher.getActionbarListener());
        myaTabFragment.showFragment(myaTabFragment);
    }

    private void launchAsActivity(ActivityLauncher uiLauncher, MyaLaunchInput myaLaunchInput) {
        if (null != uiLauncher && myaLaunchInput != null) {
            Intent myAccountIntent = new Intent(myaLaunchInput.getContext(), MyaActivity.class);
            myAccountIntent.putExtra(MYA_DLS_THEME, uiLauncher.getUiKitTheme());
            myaLaunchInput.getContext().startActivity(myAccountIntent);
        }
    }

    protected UserDataInterface getUserDataInterface() {
        return MyaHelper.getInstance().getUserDataInterface();
    }

    protected AppTaggingInterface getTaggingInterface(MyaDependencies myaDependencies) {
        return myaDependencies.getAppInfra().getTagging().createInstanceForComponent(MyaHelper.MYA_TLA, com.philips.platform.mya.BuildConfig.VERSION_NAME);
    }
}
