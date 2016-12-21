/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.platform.baseapp.screens.introscreen;

import android.support.annotation.NonNull;

import com.philips.platform.appframework.flowmanager.base.BaseFlowManager;
import com.philips.platform.appframework.flowmanager.base.BaseState;
import com.philips.platform.appframework.flowmanager.exceptions.NoEventFoundException;
import com.philips.platform.appframework.flowmanager.exceptions.NoStateException;
import com.philips.platform.baseapp.base.AppFrameworkApplication;
import com.philips.platform.baseapp.base.UIBasePresenter;
import com.philips.platform.baseapp.base.UIStateData;
import com.philips.platform.baseapp.screens.userregistration.UserRegistrationState;
import com.philips.platform.baseapp.screens.utility.Constants;
import com.philips.platform.uappframework.launcher.FragmentLauncher;

/**
 * Welcome presenter handles the events inside welcome fragment
 * it takes care of scenarios in which we can complete onboarding or skip it for time being
 */
public class LaunchActivityPresenter extends UIBasePresenter{

    public static final int APP_LAUNCH_STATE = 890;
   // private static final int USER_REGISTRATION_STATE = 889;
    private LaunchView launchView;
    private BaseState baseState;
    private FragmentLauncher fragmentLauncher;
    private String LAUNCH_BACK_PRESSED = "back";
    private String WELCOME_REGISTRATION = "welcome_registration";
    private String APP_LAUNCH = "onAppLaunch";

    public LaunchActivityPresenter(LaunchView launchView) {
        super(launchView);
        this.launchView = launchView;
    }

    /**
     * Handles the onclick of Welcome Skip and Done button
     *
     * @param componentID : takes compenent Id
     */
    @Override
    public void onEvent(int componentID) {
        showActionBar();
        String event = getEvent(componentID);
        fragmentLauncher = getFragmentLauncher();
        BaseFlowManager targetFlowManager = getApplicationContext().getTargetFlowManager();
        try {
            if (event == null)
                baseState = targetFlowManager.getNextState(targetFlowManager.getCurrentState(), event);
            else if (event.equals(LAUNCH_BACK_PRESSED))
                baseState = targetFlowManager.getBackState(targetFlowManager.getCurrentState());
        } catch (NoEventFoundException | NoStateException e) {
            e.printStackTrace();
        }
        // TODO: Deepthi please remove the code here and move the data within state and make sure presenter is passed via standard interface after split
        // TODO: Deepthi what if its another state or state returned from FM is null
        if (baseState != null && !(baseState instanceof UserRegistrationState)) {
            baseState.setStateListener(this);
            baseState.setUiStateData(getUiStateData());
            baseState.navigate(fragmentLauncher);
        }
    }

    protected void showActionBar() {
        launchView.showActionBar();
    }

    // TODO: Deepthi  can we make it abstract if its common
    protected String getEvent(final int componentID) {
        switch (componentID) {
            case Constants.BACK_BUTTON_CLICK_CONSTANT:
                return LAUNCH_BACK_PRESSED;
            default:return null;
        }
    }

    protected FragmentLauncher getFragmentLauncher() {
        fragmentLauncher = new FragmentLauncher(launchView.getFragmentActivity(), launchView.getContainerId(), launchView.getActionBarListener());
        return fragmentLauncher;
    }

    @NonNull
    protected UIStateData getUiStateData() {
        UIStateData homeStateData = new UIStateData();
        homeStateData.setFragmentLaunchType(Constants.ADD_HOME_FRAGMENT);
        return homeStateData;
    }

    protected AppFrameworkApplication getApplicationContext() {
        return (AppFrameworkApplication) launchView.getFragmentActivity().getApplicationContext();
    }
}
