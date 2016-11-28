/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/

package com.philips.platform.modularui.statecontroller;

import android.support.v4.app.FragmentActivity;

import com.philips.platform.appframework.flowmanager.AppConditions;
import com.philips.platform.appframework.flowmanager.AppStates;
import com.philips.platform.appframework.flowmanager.FlowManager;
import com.philips.platform.appframework.stateimpl.HamburgerActivityState;
import com.philips.platform.baseapp.base.AppFrameworkApplication;
import com.philips.platform.baseapp.base.FileUtility;
import com.philips.platform.baseapp.condition.ConditionIsLoggedIn;
import com.philips.platform.baseapp.screens.homefragment.HomeFragmentState;
import com.philips.platform.baseapp.screens.introscreen.LaunchActivityState;
import com.philips.platform.baseapp.screens.settingscreen.SettingsFragmentState;
import com.philips.platform.baseapp.screens.splash.SplashState;

import junit.framework.TestCase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Flow manager class is used for navigating from one state to other state
 */

public class FlowManagerTest extends TestCase {

    private FragmentActivity fragmentActivityMock;
    private AppFrameworkApplication appFrameworkApplication;
    private FileUtility fileUtility;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        fragmentActivityMock = mock(FragmentActivity.class);
        appFrameworkApplication = mock(AppFrameworkApplication.class);
        fileUtility = new FileUtility(appFrameworkApplication);

    }

    public void testGetNextState(){
        final SplashState splashState = mock(SplashState.class);
        final FlowManager flowManager = mock(FlowManager.class);
        final LaunchActivityState launchActivityState = mock(LaunchActivityState.class);
        when(flowManager.getState(AppStates.FIRST_STATE)).thenReturn(launchActivityState);
        when(flowManager.getNextState(launchActivityState,"onAppLaunch")).thenReturn(splashState);
        assertTrue(splashState instanceof SplashState);
    }

    public void testCurrentState(){
        final HomeFragmentState homeFragmentState = mock(HomeFragmentState.class);
        final FlowManager flowManager = mock(FlowManager.class);
        final HamburgerActivityState hamburgerActivityState = mock(HamburgerActivityState.class);
        when(flowManager.getState(AppStates.HAMBURGER_HOME)).thenReturn(hamburgerActivityState);
        when(flowManager.getNextState(hamburgerActivityState,"home_fragment")).thenReturn(homeFragmentState);
        when(flowManager.getCurrentState()).thenReturn(homeFragmentState);
        assertEquals(flowManager.getCurrentState(),homeFragmentState);
    }

    public void testGetState(){
        final FlowManager flowManager = mock(FlowManager.class);
        final SettingsFragmentState settingsFragmentState = mock(SettingsFragmentState.class);
        when(flowManager.getState(AppStates.SETTINGS)).thenReturn(settingsFragmentState);
        assertEquals(settingsFragmentState,flowManager.getState(AppStates.SETTINGS));
    }

    public void testGetCondition(){
        final FlowManager flowManager = mock(FlowManager.class);
        final ConditionIsLoggedIn conditionIsLoggedIn = mock(ConditionIsLoggedIn.class);
        when(flowManager.getCondition(AppConditions.IS_LOGGED_IN)).thenReturn(conditionIsLoggedIn);
        assertEquals(conditionIsLoggedIn,flowManager.getCondition(AppConditions.IS_LOGGED_IN));
    }
}

