/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.appframework.stateimpl;

import android.app.Activity;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.baseapp.base.AppFrameworkApplication;
import com.philips.platform.myapplication.DemoMicroAppApplicationuAppInterface;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uappframework.uappinput.UappLaunchInput;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DemoThsStateTest {

    DemoThsState mDemoThsState;

    @Mock
    Activity application;

    @Mock
    AppFrameworkApplication appFrameworkApplication;

    @Mock
    AppInfraInterface appInfraInterface;


    @Mock
    DemoMicroAppApplicationuAppInterface uappDemoInterface;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mDemoThsState = new DemoThsState();
        mDemoThsState.mDemoMicroAppApplicationuAppInterface = uappDemoInterface;
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testThsState() throws Exception {
        when(application.getApplicationContext()).thenReturn(appFrameworkApplication);
        when(appFrameworkApplication.getAppInfra()).thenReturn(appInfraInterface);
        mDemoThsState.updateDataModel();
        mDemoThsState.init(application);
        mDemoThsState.navigate(null);
        verify(uappDemoInterface).launch(any(ActivityLauncher.class),(UappLaunchInput)isNull());
    }

}