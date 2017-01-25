/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.platform.baseapp.screens.introscreen.welcomefragment;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.philips.platform.appframework.R;
import com.philips.platform.appframework.flowmanager.AppStates;
import com.philips.platform.appframework.flowmanager.FlowManager;
import com.philips.platform.appframework.stateimpl.HomeTabbedActivityState;
import com.philips.platform.baseapp.base.AppFrameworkApplication;
import com.philips.platform.baseapp.screens.userregistration.UserRegistrationState;
import com.philips.platform.uappframework.launcher.FragmentLauncher;

import junit.framework.TestCase;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */

public class WelcomeFragmentPresenterTest extends TestCase {

    private WelcomeFragmentPresenter welcomeFragmentPresenter;
    private WelcomeFragmentView welcomeFragmentViewMock;
    private FragmentActivity fragmentActivityMock;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        welcomeFragmentViewMock = mock(WelcomeFragmentView.class);
        fragmentActivityMock = mock(FragmentActivity.class);
        when(welcomeFragmentViewMock.getFragmentActivity()).thenReturn(fragmentActivityMock);
        welcomeFragmentPresenter = new WelcomeFragmentPresenter(welcomeFragmentViewMock);
    }

    public void testOnClick() throws Exception {
        final UserRegistrationState userRegStateMock = mock(UserRegistrationState.class);
        final FragmentLauncher fragmentLauncherMock = mock(FragmentLauncher.class);
        final AppFrameworkApplication appFrameworkApplicationMock = mock(AppFrameworkApplication.class);
        when(fragmentActivityMock.getApplicationContext()).thenReturn(appFrameworkApplicationMock);
        welcomeFragmentPresenter = new WelcomeFragmentPresenter(welcomeFragmentViewMock) {
            @Override
            public void setState(final String stateID) {
                super.setState(AppStates.TAB_HOME);
            }

            @Override
            protected FragmentLauncher getFragmentLauncher() {
                return fragmentLauncherMock;
            }

            @Override
            protected AppFrameworkApplication getApplicationContext() {
                return appFrameworkApplicationMock;
            }
        };
        FlowManager uiFlowManagerMock = mock(FlowManager.class);
        when(appFrameworkApplicationMock.getTargetFlowManager()).thenReturn(uiFlowManagerMock);
        when(uiFlowManagerMock.getNextState("welcome_skip")).thenReturn(userRegStateMock);
        welcomeFragmentPresenter.onEvent(R.id.welcome_skip_button);
        verify(welcomeFragmentViewMock).showActionBar();
        verify(userRegStateMock, atLeastOnce()).navigate(fragmentLauncherMock);
    }

    public void testOnBackPress() throws Exception {
        final HomeTabbedActivityState homeTabbedActivityStateMock = mock(HomeTabbedActivityState.class);
        final FragmentLauncher fragmentLauncherMock = mock(FragmentLauncher.class);
        final AppFrameworkApplication appFrameworkApplicationMock = mock(AppFrameworkApplication.class);
        when(fragmentActivityMock.getApplicationContext()).thenReturn(appFrameworkApplicationMock);
        welcomeFragmentPresenter = new WelcomeFragmentPresenter(welcomeFragmentViewMock) {
            @Override
            public void setState(final String stateID) {
                super.setState(AppStates.WELCOME);
            }

            @NonNull
            @Override
            protected FragmentLauncher getFragmentLauncher() {
                return fragmentLauncherMock;
            }

            @Override
            protected AppFrameworkApplication getApplicationContext() {
                return appFrameworkApplicationMock;
            }
        };
        FlowManager uiFlowManagerMock = mock(FlowManager.class);
        when(appFrameworkApplicationMock.getTargetFlowManager()).thenReturn(uiFlowManagerMock);
        when(uiFlowManagerMock.getNextState("welcome_home")).thenReturn(homeTabbedActivityStateMock);
        welcomeFragmentPresenter.onEvent(0);
        verify(homeTabbedActivityStateMock, atLeastOnce()).navigate(fragmentLauncherMock);
    }

    public void testGetUiState() {
        assertEquals("welcome_done", welcomeFragmentPresenter.getEventState(R.id.welcome_start_registration_button));
    }
}
