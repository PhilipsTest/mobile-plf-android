package com.philips.cdp.digitalcare.contactus.fragments;

import android.content.res.Configuration;
import android.view.View;

import com.philips.cdp.digitalcare.DigitalCareConfigManager;
import com.philips.cdp.digitalcare.R;
import com.philips.cdp.digitalcare.homefragment.DigitalCareBaseFragment;
import com.philips.cdp.digitalcare.util.CustomRobolectricRunnerCC;
import com.philips.cdp.digitalcare.util.DigitalCareTestMock;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.configuration.MockitoConfiguration;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.Robolectric;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(CustomRobolectricRunnerCC.class)
@PrepareForTest(DigitalCareConfigManager.class)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*", "org.apache.xerces", "javax.xml.*", "org.xml.sax.*", "org.w3c.dom.*",  "org.springframework.context.*", "org.apache.log4j.*"})
public class ChatFragmentTest extends MockitoConfiguration{

    private ChatFragment fragment;

    private View rootView;

    @Rule
    public PowerMockRule powerMockRule=new PowerMockRule();

    @Mock
    private DigitalCareConfigManager mockDigitalCareConfigManager;

    @Mock
    private AppTaggingInterface mockAppTaggingInterface;

    private DigitalCareBaseFragment digitalCareBaseFragmentspy;

    @Before
    public void setUp(){
        Robolectric.buildActivity(DigitalCareTestMock.class).create().get();
        MockitoAnnotations.initMocks(this);
        fragment = new ChatFragment();
        PowerMockito.mockStatic(DigitalCareConfigManager.class);
        when(DigitalCareConfigManager.getInstance()).thenReturn(mockDigitalCareConfigManager);
        when(DigitalCareConfigManager.getInstance().getTaggingInterface()).thenReturn(mockAppTaggingInterface);
        digitalCareBaseFragmentspy=spy(fragment);
        SupportFragmentTestUtil.startFragment(fragment,DigitalCareTestMock.class);
        rootView=fragment.getView();
    }

    @After
    public void tearDown() throws Exception {
        fragment = null;
        digitalCareBaseFragmentspy = null;
        mockDigitalCareConfigManager = null;
        mockAppTaggingInterface = null;
        rootView = null;
    }

    @Test
    public void testViewNotNull() throws Exception {
        Assert.assertNotNull(rootView);
    }

    @Test
    public void tesChatNow(){
        SupportFragmentTestUtil.startFragment(digitalCareBaseFragmentspy, DigitalCareTestMock.class);
        digitalCareBaseFragmentspy=spy(fragment);
        digitalCareBaseFragmentspy.isInternetAvailable=true;
        digitalCareBaseFragmentspy.getView().findViewById(R.id.chatNow).performClick();
    }

    @Test
    public void testGetActionbarTitle(){
        String title = fragment.getActionbarTitle();
        Assert.assertNotNull(title);
    }

    @Test
    public void testSetPreviousPageName(){
        String previousPageName = fragment.setPreviousPageName();
        Assert.assertNotNull(previousPageName);
    }

    @Test
    public void testOnConfigurationChanged(){
        Configuration configuration = new Configuration();
        fragment.onConfigurationChanged(configuration);
    }

    @Test
    public void testDestroyMethod(){
        fragment.onDestroy();
    }
}