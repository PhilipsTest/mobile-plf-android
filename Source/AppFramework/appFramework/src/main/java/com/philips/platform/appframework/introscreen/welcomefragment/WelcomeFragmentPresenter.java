package com.philips.platform.appframework.introscreen.welcomefragment;

import android.support.annotation.NonNull;

import com.philips.platform.appframework.AppFrameworkApplication;
import com.philips.platform.appframework.R;
import com.philips.platform.appframework.flowmanager.FlowManager;
import com.philips.platform.appframework.homescreen.HomeActivityPresenter;
import com.philips.platform.appframework.utility.Constants;
import com.philips.platform.appframework.utility.SharedPreferenceUtility;
import com.philips.platform.flowmanager.jsonstates.AppStates;
import com.philips.platform.flowmanager.jsonstates.EventStates;
import com.philips.platform.modularui.statecontroller.UIBasePresenter;
import com.philips.platform.modularui.statecontroller.UIState;
import com.philips.platform.modularui.stateimpl.HomeActivityState;
import com.philips.platform.modularui.stateimpl.URStateListener;
import com.philips.platform.modularui.stateimpl.UserRegistrationState;
import com.philips.platform.uappframework.launcher.FragmentLauncher;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class WelcomeFragmentPresenter extends UIBasePresenter implements URStateListener {

    private AppFrameworkApplication appFrameworkApplication;
    private SharedPreferenceUtility sharedPreferenceUtility;
    private UIState uiState;
    private FragmentLauncher fragmentLauncher;
    private WelcomeFragmentView welcomeFragmentView;

    public WelcomeFragmentPresenter(WelcomeFragmentView welcomeFragmentView) {
        super(welcomeFragmentView);
        this.welcomeFragmentView = welcomeFragmentView;
    }

    @Override
    public void onClick(final int componentID) {
        appFrameworkApplication = (AppFrameworkApplication) welcomeFragmentView.getFragmentActivity().getApplicationContext();
        welcomeFragmentView.showActionBar();
        EventStates eventId = getEventID(componentID);
        uiState = FlowManager.getInstance(appFrameworkApplication).getNextState(AppStates.WELCOME, eventId);
        uiState.setPresenter(this);
        fragmentLauncher = getFragmentLauncher();
        appFrameworkApplication.getFlowManager().setCurrentState(uiState);
        uiState.navigate(fragmentLauncher);
    }

    @NonNull
    protected FragmentLauncher getFragmentLauncher() {
        return new FragmentLauncher(welcomeFragmentView.getFragmentActivity(), welcomeFragmentView.getContainerId(), welcomeFragmentView.getActionBarListener());
    }

    protected UIState getUiState(final int componentID) {
        switch (componentID) {
            case R.id.welcome_skip_button:
                uiState = new UserRegistrationState();
                uiState.setPresenter(this);
                ((UserRegistrationState) uiState).registerUIStateListener(this);
                break;
            case R.id.welcome_start_registration_button:
                sharedPreferenceUtility = new SharedPreferenceUtility(welcomeFragmentView.getFragmentActivity());
                sharedPreferenceUtility.writePreferenceBoolean(Constants.DONE_PRESSED, true);
                uiState = new UserRegistrationState();
                uiState.setPresenter(this);
                ((UserRegistrationState) uiState).registerUIStateListener(this);
                break;
            case HomeActivityPresenter.MENU_OPTION_HOME:
                uiState = new HomeActivityState();
        }
        return uiState;
    }

    private EventStates getEventID(final int componentID) {
        switch (componentID) {
            case R.id.welcome_skip_button:
                return EventStates.WELCOME_SKIP;
            case R.id.welcome_start_registration_button:
                sharedPreferenceUtility = new SharedPreferenceUtility(welcomeFragmentView.getFragmentActivity());
                sharedPreferenceUtility.writePreferenceBoolean(Constants.DONE_PRESSED, true);
                return EventStates.WELCOME_DONE;
            case HomeActivityPresenter.MENU_OPTION_HOME:
                return EventStates.WELCOME_HOME;
        }
        return null;
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onStateComplete(final UIState uiState) {
        appFrameworkApplication = (AppFrameworkApplication) welcomeFragmentView.getFragmentActivity().getApplicationContext();
        EventStates eventId = getEventID(HomeActivityPresenter.MENU_OPTION_HOME);
        this.uiState = FlowManager.getInstance(appFrameworkApplication).getNextState(AppStates.WELCOME, eventId);
        fragmentLauncher = getFragmentLauncher();
        this.uiState.setPresenter(this);
        welcomeFragmentView.finishActivityAffinity();
        uiState.navigate(fragmentLauncher);
    }

    @Override
    public void onLogoutSuccess() {

    }

    @Override
    public void onLogoutFailure() {

    }
}
