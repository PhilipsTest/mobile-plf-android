/* Copyright (c) Koninklijke Philips N.V., 2017
* All rights are reserved. Reproduction or dissemination
* in whole or in part is prohibited without the prior written
* consent of the copyright holder.
*/
package com.philips.platform.baseapp.screens.devicepairing;

import android.support.v4.app.FragmentManager;

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

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, constants = BuildConfig.class, application = TestAppFrameworkApplication.class, sdk = 25)

public class DevicePairingStateTest extends TestCase {
    private DevicePairingState devicePairingState;
    private FragmentLauncher fragmentLauncher;
    private HamburgerActivity hamburgerActivity;
    private ActivityController<TestActivity> activityController;

    @After
    public void tearDown() {
        activityController.pause().stop().destroy();
        devicePairingState = null;
        hamburgerActivity = null;
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        devicePairingState = new DevicePairingState();

        UIStateData devicePairingStateData = new UIStateData();
        devicePairingStateData.setFragmentLaunchType(Constants.CLEAR_TILL_HOME);
        devicePairingState.setUiStateData(devicePairingStateData);

        activityController = Robolectric.buildActivity(TestActivity.class);
        hamburgerActivity = activityController.create().start().get();
        fragmentLauncher = new FragmentLauncher(hamburgerActivity, R.id.frame_container, hamburgerActivity);
    }

    @Test(expected = UnsatisfiedLinkError.class)
    public void launchDevicePairingState() {
        devicePairingState.navigate(fragmentLauncher);
        FragmentManager fragmentManager = hamburgerActivity.getSupportFragmentManager();
        int fragmentCount = fragmentManager.getBackStackEntryCount();
        assertEquals(1, fragmentCount);
    }
}
