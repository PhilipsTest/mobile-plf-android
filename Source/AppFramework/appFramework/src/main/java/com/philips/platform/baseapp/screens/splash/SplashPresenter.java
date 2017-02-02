/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.platform.baseapp.screens.splash;

import android.util.Log;
import android.widget.Toast;

import com.philips.platform.appframework.R;
import com.philips.platform.appframework.flowmanager.AppStates;
import com.philips.platform.appframework.flowmanager.base.BaseFlowManager;
import com.philips.platform.appframework.flowmanager.base.BaseState;
import com.philips.platform.appframework.flowmanager.base.UIStateListener;
import com.philips.platform.appframework.flowmanager.exceptions.ConditionIdNotSetException;
import com.philips.platform.appframework.flowmanager.exceptions.NoConditionFoundException;
import com.philips.platform.appframework.flowmanager.exceptions.NoEventFoundException;
import com.philips.platform.appframework.flowmanager.exceptions.NoStateException;
import com.philips.platform.appframework.flowmanager.exceptions.StateIdNotSetException;
import com.philips.platform.baseapp.base.AppFrameworkApplication;
import com.philips.platform.baseapp.base.UIBasePresenter;
import com.philips.platform.baseapp.screens.introscreen.LaunchView;
import com.philips.platform.baseapp.screens.userregistration.UserRegistrationState;
import com.philips.platform.uappframework.launcher.FragmentLauncher;

/**
 * Splash presenter loads the splash screen and sets the next state after splash
 * The wait timer for splash screen is 3 secs ( configurable by verticals)
 */
public class SplashPresenter extends UIBasePresenter implements UIStateListener {
    private final LaunchView uiView;
    private String APP_START = "onSplashTimeOut";

    public SplashPresenter(LaunchView uiView) {
        super(uiView);
        this.uiView = uiView;
        setState(AppStates.SPLASH);
    }

    /**
     * The methods takes decision to load which next state needs to be loaded after splash screen
     * Depending upon the User registration is compelted on not state will change
     *
     */
    @Override
    public void onEvent(int componentID) {
        try {
            BaseFlowManager targetFlowManager = getApplicationContext().getTargetFlowManager();
            final BaseState splashState = targetFlowManager.getState(AppStates.SPLASH);
            BaseState baseState = targetFlowManager.getNextState(splashState, APP_START);
            if (null != baseState) {
                baseState.setUiStateData(setStateData(baseState.getStateID()));
                baseState.setStateListener(this);
                if (baseState instanceof UserRegistrationState) {
                    uiView.showActionBar();
                }
                baseState.navigate(getFragmentLauncher());
            }
        } catch (NoEventFoundException | NoStateException | NoConditionFoundException | StateIdNotSetException | ConditionIdNotSetException
                e) {
            Log.d(getClass() + "", e.getMessage());
            Toast.makeText(uiView.getFragmentActivity(), uiView.getFragmentActivity().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
        }
    }

    protected FragmentLauncher getFragmentLauncher() {
        return new FragmentLauncher(uiView.getFragmentActivity(), uiView.getContainerId(), uiView.getActionBarListener());
    }
    protected AppFrameworkApplication getApplicationContext() {
        return (AppFrameworkApplication) uiView.getFragmentActivity().getApplicationContext();
    }
    protected void finishActivity() {
        uiView.finishActivityAffinity();
    }

    @Override
    public void onStateComplete(BaseState baseState) {

    }
}
