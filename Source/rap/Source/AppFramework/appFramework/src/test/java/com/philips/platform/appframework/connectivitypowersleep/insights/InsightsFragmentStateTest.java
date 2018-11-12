/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */
package com.philips.platform.appframework.connectivitypowersleep.insights;

import android.support.v4.app.FragmentManager;

import com.philips.platform.TestActivity;
import com.philips.platform.TestAppFrameworkApplication;
import com.philips.platform.appframework.R;
import com.philips.platform.appframework.homescreen.HamburgerActivity;
import com.philips.platform.uappframework.launcher.FragmentLauncher;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(application = TestAppFrameworkApplication.class)
public class InsightsFragmentStateTest {

    private InsightsFragmentState insightsFragmentState;
    private FragmentLauncher fragmentLauncher;
    private HamburgerActivity launchActivity;
    private ActivityController<TestActivity> activityController;


    @Before
    public void setUp() throws Exception{
        insightsFragmentState = new InsightsFragmentState();
        activityController= Robolectric.buildActivity(TestActivity.class);
        launchActivity=activityController.create().start().get();
        insightsFragmentState.init(launchActivity);
        insightsFragmentState.updateDataModel();
        fragmentLauncher = new FragmentLauncher(launchActivity, R.id.frame_container, launchActivity);
    }

    @Test
    public void launchCocoVersionState(){
        insightsFragmentState.navigate(fragmentLauncher);
        FragmentManager fragmentManager = launchActivity.getSupportFragmentManager();
        int fragmentCount = fragmentManager.getBackStackEntryCount();
        assertEquals(1,fragmentCount);
    }

    @After
    public void tearDown(){
        activityController.pause().stop().destroy();
        insightsFragmentState =null;
        launchActivity=null;
    }
}

