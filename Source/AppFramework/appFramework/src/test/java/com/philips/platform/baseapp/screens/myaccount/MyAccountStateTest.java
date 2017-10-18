/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.platform.baseapp.screens.myaccount;

import android.support.v4.app.FragmentManager;

import com.philips.platform.CustomRobolectricRunner;
import com.philips.platform.TestActivity;
import com.philips.platform.TestAppFrameworkApplication;
import com.philips.platform.appframework.flowmanager.base.UIStateData;
import com.philips.platform.appframework.homescreen.HamburgerActivity;
import com.philips.platform.baseapp.screens.utility.Constants;
import com.philips.platform.uappframework.launcher.FragmentLauncher;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import static org.robolectric.RuntimeEnvironment.application;

@RunWith(CustomRobolectricRunner.class)
@Config(application = TestAppFrameworkApplication.class)
public class MyAccountStateTest extends TestCase {
    private MyAccountState myAccountState;
    private FragmentLauncher fragmentLauncher;
    private HamburgerActivity hamburgerActivity;
    private ActivityController<TestActivity> activityController;

    @After
    public void tearDown(){
        activityController.pause().stop().destroy();
    }
    @Before
    public void setUp() throws Exception{
        super.setUp();
        myAccountState = new MyAccountState();
        UIStateData supportStateData = new UIStateData();
        supportStateData.setFragmentLaunchType(Constants.ADD_FROM_HAMBURGER);
        myAccountState.setUiStateData(supportStateData);

        activityController= Robolectric.buildActivity(TestActivity.class);
        hamburgerActivity=activityController.create().start().get();
        fragmentLauncher = new FragmentLauncher(hamburgerActivity, R.id.frame_container, hamburgerActivity);
    }

    @Test
    public void launchSupportState(){
        myAccountState.init(application);
        myAccountState.navigate(fragmentLauncher);
        FragmentManager fragmentManager = hamburgerActivity.getSupportFragmentManager();
        int fragmentCount = fragmentManager.getBackStackEntryCount();
        assertEquals(0,fragmentCount);
    }
}
