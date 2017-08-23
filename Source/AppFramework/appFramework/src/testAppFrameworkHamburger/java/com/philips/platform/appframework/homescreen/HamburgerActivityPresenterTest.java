/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/

package com.philips.platform.appframework.homescreen;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.philips.platform.appframework.R;
import com.philips.platform.appframework.flowmanager.AppStates;
import com.philips.platform.appframework.flowmanager.FlowManager;
import com.philips.platform.appframework.flowmanager.base.UIStateData;
import com.philips.platform.appframework.flowmanager.exceptions.NoEventFoundException;
import com.philips.platform.appframework.stateimpl.HamburgerActivityState;
import com.philips.platform.baseapp.base.AppFrameworkApplication;
import com.philips.platform.baseapp.base.FragmentView;
import com.philips.platform.appframework.stateimpl.DemoDataServicesState;
import com.philips.platform.baseapp.screens.homefragment.HomeFragmentState;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.listener.ActionBarListener;

import junit.framework.TestCase;

import java.util.ArrayList;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HamburgerActivityPresenterTest extends TestCase {

    FragmentView fragmentViewMock;
    private HamburgerActivityPresenter hamburgerActivityPresenter;
    private FragmentActivity fragmentActivityMock;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        fragmentViewMock = mock(FragmentView.class);
        Resources resourcesMock = mock(Resources.class);
        when(resourcesMock.getStringArray(R.array.productselection_ctnlist)).thenReturn(new String[]{"abcd"});
        fragmentActivityMock = mock(FragmentActivity.class);
        ActionBarListener actionBarListenerMock = mock(ActionBarListener.class);
        when(fragmentActivityMock.getResources()).thenReturn(resourcesMock);
        when(fragmentViewMock.getFragmentActivity()).thenReturn(fragmentActivityMock);
        when(fragmentViewMock.getContainerId()).thenReturn(12345);
        when(fragmentViewMock.getActionBarListener()).thenReturn(actionBarListenerMock);
        hamburgerActivityPresenter = new HamburgerActivityPresenter(fragmentViewMock) {
            @Override
            public void setState(final String stateID) {
                super.setState(AppStates.HAMBURGER_HOME);
            }

            @NonNull
            @Override
            protected ArrayList<String> getCtnList() {
                ArrayList<String> ctnList = new ArrayList<>();
                ctnList.add("HX6064/33");
                return ctnList;
            }
        };
    }

//    public void testGetUiState() {
//        final FragmentView fragmentViewMock = mock(FragmentView.class);
//        final FragmentActivity fragmentActivityMock = mock(FragmentActivity.class);
//        final Resources resourcesMock = mock(Resources.class);
//        when(fragmentViewMock.getFragmentActivity()).thenReturn(fragmentActivityMock);
//        when(fragmentActivityMock.getResources()).thenReturn(resourcesMock);
//        assertEquals(true, hamburgerActivityPresenter.setStateData(0) instanceof UIStateData);
//        assertEquals(true, hamburgerActivityPresenter.setStateData(1) instanceof UIStateData);
//        assertEquals(true, hamburgerActivityPresenter.setStateData(2) instanceof UIStateData);
//        assertEquals(true, hamburgerActivityPresenter.setStateData(3) instanceof UIStateData);
//        assertEquals(true, hamburgerActivityPresenter.setStateData(4) instanceof UIStateData);
//        assertEquals(true, hamburgerActivityPresenter.setStateData(-1) instanceof UIStateData);
//    }

    public void testOnClick() throws NoEventFoundException {
        final UIStateData uiStateData = mock(UIStateData.class);
        final FragmentLauncher fragmentLauncherMock = mock(FragmentLauncher.class);
        final HomeFragmentState homeFragmentStateMock = mock(HomeFragmentState.class);
        final HamburgerActivityState hamburgerActivityState = mock(HamburgerActivityState.class);
        final AppFrameworkApplication appFrameworkApplicationMock = mock(AppFrameworkApplication.class);
        when(fragmentActivityMock.getApplicationContext()).thenReturn(appFrameworkApplicationMock);
        hamburgerActivityPresenter = new HamburgerActivityPresenter(fragmentViewMock) {
            @Override
            public void setState(final String stateID) {
                super.setState(AppStates.HAMBURGER_HOME);
            }

            @NonNull
            @Override
            protected UIStateData setStateData(final String componentID) {
                return uiStateData;
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

        FlowManager uiFlowManager = mock(FlowManager.class);
        when(appFrameworkApplicationMock.getTargetFlowManager()).thenReturn(uiFlowManager);
        when(appFrameworkApplicationMock.getTargetFlowManager().getState(AppStates.HAMBURGER_HOME)).thenReturn(hamburgerActivityState);
        when(uiFlowManager.getNextState(hamburgerActivityState, "home_fragment")).thenReturn(homeFragmentStateMock);
        hamburgerActivityPresenter.onEvent(0);
        verify(homeFragmentStateMock, atLeastOnce()).navigate(fragmentLauncherMock);
    }

    public void testDataServicesLaunch() {
        final UIStateData uiStateData = mock(UIStateData.class);
        final FragmentLauncher fragmentLauncherMock = mock(FragmentLauncher.class);
        final DemoDataServicesState dataSyncStateMock = mock(DemoDataServicesState.class);
        final HamburgerActivityState hamburgerActivityState = mock(HamburgerActivityState.class);
        final AppFrameworkApplication appFrameworkApplicationMock = mock(AppFrameworkApplication.class);
        when(fragmentActivityMock.getApplicationContext()).thenReturn(appFrameworkApplicationMock);
        hamburgerActivityPresenter = new HamburgerActivityPresenter(fragmentViewMock) {
            @Override
            public void setState(final String stateID) {
                super.setState(AppStates.HAMBURGER_HOME);
            }

            @NonNull
            @Override
            protected UIStateData setStateData(final String componentID) {
                return uiStateData;
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

        FlowManager uiFlowManager = mock(FlowManager.class);
        when(appFrameworkApplicationMock.getTargetFlowManager()).thenReturn(uiFlowManager);
        when(appFrameworkApplicationMock.getTargetFlowManager().getState(AppStates.HAMBURGER_HOME)).thenReturn(hamburgerActivityState);
        try {
            when(uiFlowManager.getNextState(hamburgerActivityState, "data_sync")).thenReturn(dataSyncStateMock);
            when(uiFlowManager.getNextState(hamburgerActivityState, "some_event")).thenReturn(dataSyncStateMock);
        } catch (Exception e) {
            assertTrue(e instanceof NoEventFoundException);
        }
        hamburgerActivityPresenter.onEvent(5);
        verify(dataSyncStateMock, times(1)).navigate(fragmentLauncherMock);
    }
}