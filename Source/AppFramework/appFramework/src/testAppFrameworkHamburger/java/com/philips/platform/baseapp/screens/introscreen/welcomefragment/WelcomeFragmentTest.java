/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.platform.baseapp.screens.introscreen.welcomefragment;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.philips.platform.TestAppFrameworkApplication;
import com.philips.platform.appframework.BuildConfig;
import com.philips.platform.appframework.R;
import com.philips.platform.baseapp.screens.introscreen.pager.WelcomePagerAdapter;
import com.philips.platform.baseapp.screens.splash.SplashFragmentTest;
import com.shamanland.fonticon.FontIconView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowLooper;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE,constants = BuildConfig.class, application = TestAppFrameworkApplication.class, sdk = 25)
public class WelcomeFragmentTest {
    private SplashFragmentTest.LaunchActivityMockAbstract launchActivity;
    private ImageView logo;
    private WelcomeFragmentMockAbstract welcomeFragment;
    private ViewPager pager;
    private ImageView rightArrow;
    private ActivityController<SplashFragmentTest.LaunchActivityMockAbstract> activityController;

    @After
    public void tearDown(){
        activityController.pause().stop().destroy();
        welcomeFragment=null;
        pager=null;
        launchActivity=null;
    }
    @Before
    public void setUp(){
        activityController=Robolectric.buildActivity(SplashFragmentTest.LaunchActivityMockAbstract.class);
        launchActivity=activityController.create().start().get();
//        launchActivity = Robolectric.buildActivity(SplashFragmentTest.LaunchActivityMockAbstract.class).create().start().get();
        welcomeFragment =  new WelcomeFragmentMockAbstract();
        launchActivity.getSupportFragmentManager().beginTransaction().add(welcomeFragment,null).commit();
        pager = (ViewPager) welcomeFragment.getView().findViewById(R.id.welcome_pager);
    }

    @Test
    public void testArrowClicks(){
        pager.setCurrentItem(1);
        rightArrow = (ImageView) welcomeFragment.getView().findViewById(R.id.welcome_rightarrow);
        rightArrow.performClick();
        assertEquals(FontIconView.VISIBLE,rightArrow.getVisibility());

    }

    @Test
    public void testBackPressed(){
        pager.setCurrentItem(0);
        boolean handleBack = welcomeFragment.handleBackEvent();
        assertEquals(true,handleBack);
    }

    @Test
    public void testDoneVisible() {
        pager.setCurrentItem(8);
        rightArrow = (ImageView) welcomeFragment.getView().findViewById(R.id.welcome_rightarrow);
        assertFalse(View.VISIBLE == rightArrow.getVisibility());
    }

    @Test
    public void testClearAdapter() {
        welcomeFragment.clearAdapter();
        assertNull(pager.getAdapter());
    }

    public static class WelcomeFragmentMockAbstract extends WelcomeFragment {
        View view;
        @Override
        protected void startLogging() {

        }
    }


}
