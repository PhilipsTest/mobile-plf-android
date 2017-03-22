package com.philips.platform.baseapp.screens.aboutscreen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.philips.cdp.uikit.hamburger.HamburgerAdapter;
import com.philips.platform.GradleRunner;
import com.philips.platform.appframework.BuildConfig;
import com.philips.platform.appframework.R;
import com.philips.platform.appframework.homescreen.HamburgerActivity;
import com.philips.platform.appframework.homescreen.TestAppFrameworkApplication;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil.*;
import static org.robolectric.util.FragmentTestUtil.startFragment;

@RunWith(GradleRunner.class)
@Config(manifest=Config.NONE, constants = BuildConfig.class, application = TestAppFrameworkApplication.class, sdk = 21)
public class AboutScreenFragmentTest {
    private HamburgerActivity hamburgerActivity = null;
    private FrameLayout hamburgerClick = null;
    private ListView drawerListView =null;
    private HamburgerAdapter adapter;
    private TestAboutScreenFragment aboutScreenFragment;

    @Before
    public void setUp(){
        hamburgerActivity = Robolectric.buildActivity(HamburgerActivity.class).create().start().get();
        View customView = LayoutInflater.from(hamburgerActivity).
                inflate(R.layout.af_action_bar_shopping_cart, null);
        hamburgerClick = (FrameLayout) customView.findViewById(R.id.af_hamburger_frame_layout);
        drawerListView = (ListView) hamburgerActivity.findViewById(R.id.hamburger_list);
        adapter = (HamburgerAdapter) drawerListView.getAdapter();
        aboutScreenFragment = new TestAboutScreenFragment();
        SupportFragmentTestUtil.startFragment(aboutScreenFragment);
    }

    @Test
    public void testAboutScreenFragment(){

        assertNotNull(aboutScreenFragment);
    }

    @Test
    public void testVersion(){
        TextView  version =(TextView)aboutScreenFragment.view.findViewById(R.id.about_version);
        assertEquals(version.getText(),hamburgerActivity.getResources().getString(R.string.RA_About_App_Version) +BuildConfig.VERSION_NAME);
    }

    @Test
    public void testDescription(){
        TextView  content =(TextView)aboutScreenFragment.view.findViewById(R.id.about_content);
        assertEquals(content.getText(),hamburgerActivity.getResources().getString(R.string.RA_About_Description));
    }

    public static class TestAboutScreenFragment extends AboutScreenFragment{
       View view;
       @Override
       protected void startAppTagging() {

       }

       @Override
       public void onResume() {
            super.onResume();
       }

       @Override
       public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
           view = super.onCreateView(inflater, container, savedInstanceState);
           return view;
       }

       @Override
       protected void updateActionBar() {

       }
   }
}
