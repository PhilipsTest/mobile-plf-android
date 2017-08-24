/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
* in whole or in part is prohibited without the prior written
* consent of the copyright holder.
*/
package com.philips.platform.baseapp.screens.cocoversion;

import android.support.v4.app.FragmentManager;

import com.philips.platform.CustomRobolectricRunner;
import com.philips.platform.TestActivity;
import com.philips.platform.TestAppFrameworkApplication;
import com.philips.platform.appframework.BuildConfig;
import com.philips.platform.appframework.R;
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
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

@RunWith(CustomRobolectricRunner.class)
@Config(application = TestAppFrameworkApplication.class)
public class CocoVersionStateTest extends TestCase {

/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
    private CocoVersionState cocoVersionState;
    private FragmentLauncher fragmentLauncher;
    private HamburgerActivity launchActivity;
    private ActivityController<TestActivity> activityController;

    @After
    public void tearDown(){
        activityController.pause().stop().destroy();
        cocoVersionState=null;
        launchActivity=null;
    }
    @Before
    public void setUp() throws Exception{
        super.setUp();
        cocoVersionState = new CocoVersionState();
        UIStateData iapStateData = new UIStateData();
        iapStateData.setFragmentLaunchType(Constants.CLEAR_TILL_HOME);
        cocoVersionState.setUiStateData(iapStateData);

        activityController=Robolectric.buildActivity(TestActivity.class);
        launchActivity=activityController.create().start().get();
        fragmentLauncher = new FragmentLauncher(launchActivity, R.id.frame_container, launchActivity);
    }

    @Test
    public void launchCocoVersionState(){
        cocoVersionState.navigate(fragmentLauncher);
        FragmentManager fragmentManager = launchActivity.getSupportFragmentManager();
        int fragmentCount = fragmentManager.getBackStackEntryCount();
        assertEquals(1,fragmentCount);
    }
}
