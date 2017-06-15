/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.platform.baseapp.screens.splash;

import android.os.Bundle;
import android.widget.ImageView;

import com.philips.platform.TestAppFrameworkApplication;
import com.philips.platform.appframework.BuildConfig;
import com.philips.platform.appframework.R;
import com.philips.platform.baseapp.screens.introscreen.LaunchActivityAbstract;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE,constants = BuildConfig.class, application = TestAppFrameworkApplication.class, sdk = 25)
public class SplashFragmentTest {
    private LaunchActivityMockAbstract launchActivity;
    private SplashFragmentAbstract splashFragment;
    private ImageView logo;

    @Before
    public void setUp(){
        launchActivity = Robolectric.buildActivity(LaunchActivityMockAbstract.class).create().start().get();
        splashFragment =  new SplashFragmentAbstract();
        launchActivity.getSupportFragmentManager().beginTransaction().add(splashFragment,null).commit();

    }

    @Test
    public void testSplashFragmentLaunch(){
        assertNotNull(splashFragment);
    }

    @Test
    public void testSplashLogo(){
        logo = (ImageView) splashFragment.getView().findViewById(R.id.splash_logo);
        assertNotNull(logo);
    }

    public static class LaunchActivityMockAbstract extends LaunchActivityAbstract {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            setTheme(R.style.Theme_Philips_DarkBlue_Gradient_NoActionBar);
            super.onCreate(savedInstanceState);
        }
    }

}
