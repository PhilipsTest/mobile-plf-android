/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.platform.baseapp.screens.introscreen;

import android.support.annotation.NonNull;

import com.philips.platform.baseapp.base.AppFrameworkApplication;
import com.philips.platform.baseapp.base.UIBasePresenter;
import com.philips.platform.baseapp.screens.userregistration.UserRegistrationState;
import com.philips.platform.baseapp.screens.utility.Constants;
import com.philips.platform.uappframework.launcher.FragmentLauncher;

import philips.appframeworklibrary.flowmanager.base.BaseFlowManager;
import philips.appframeworklibrary.flowmanager.base.BaseState;
import philips.appframeworklibrary.flowmanager.base.UIStateListener;
import philips.appframeworklibrary.flowmanager.exceptions.NoEventFoundException;
import philips.appframeworklibrary.flowmanager.exceptions.NoStateException;

/**
 * Welcome presenter handles the events inside welcome fragment
 * it takes care of scenarios in which we can complete onboarding or skip it for time being
 */
public class LaunchActivityPresenter extends UIBasePresenter implements UIStateListener{

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
        String eventState = getEventState(componentID);
        fragmentLauncher = getFragmentLauncher();
        BaseFlowManager targetFlowManager = getApplicationContext().getTargetFlowManager();
        try {
            if (eventState == null)
                baseState = targetFlowManager.getNextState(targetFlowManager.getCurrentState(), eventState);
            else if (eventState.equals(LAUNCH_BACK_PRESSED))
                baseState = targetFlowManager.getBackState(targetFlowManager.getCurrentState());
        } catch (NoEventFoundException | NoStateException e) {
            e.printStackTrace();
        }
        if (baseState != null && !(baseState instanceof UserRegistrationState)) {
            baseState.setStateListener(this);
            baseState.setUiStateData(getUiStateData());
            baseState.navigate(fragmentLauncher);
        }
    }

    protected void showActionBar() {
        launchView.showActionBar();
    }

    protected String getEventState(final int componentID) {
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
    protected BaseState.UIStateData getUiStateData() {
        BaseState.UIStateData homeStateData = new BaseState.UIStateData();
        homeStateData.setFragmentLaunchType(Constants.ADD_HOME_FRAGMENT);
        return homeStateData;
    }

    protected AppFrameworkApplication getApplicationContext() {
        return (AppFrameworkApplication) launchView.getFragmentActivity().getApplicationContext();
    }

    @Override
    public void onStateComplete(BaseState baseState) {

    }
}
