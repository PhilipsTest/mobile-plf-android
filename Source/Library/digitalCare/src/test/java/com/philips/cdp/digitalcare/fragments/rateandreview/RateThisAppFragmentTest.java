package com.philips.cdp.digitalcare.fragments.rateandreview;

import android.content.res.Configuration;
import android.view.View;
import android.widget.Button;

import com.philips.cdp.digitalcare.DigitalCareConfigManager;
import com.philips.cdp.digitalcare.R;
import com.philips.cdp.digitalcare.fragments.rateandreview.fragments.RateThisAppFragmentPresenter;
import com.philips.cdp.digitalcare.homefragment.DigitalCareBaseFragment;
import com.philips.cdp.digitalcare.productdetails.model.ViewProductDetailsModel;
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

/**
 * Created by philips on 7/10/17.
 */
@RunWith(CustomRobolectricRunnerCC.class)
@PrepareForTest(DigitalCareConfigManager.class)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
public class RateThisAppFragmentTest extends MockitoConfiguration {

    @Mock
    private DigitalCareConfigManager mockDigitalCareConfigManager;

    @Rule
    public PowerMockRule powerMockRule=new PowerMockRule();

    private RateThisAppFragment fragment;

    @Mock
    private DigitalCareBaseFragment mockDigitalCareBaseFragment;

    @Mock
    RateThisAppFragmentPresenter rateThisAppFragmentPresenter;

    @Mock
    AppTaggingInterface mockAppTaggingInterface;

    private ViewProductDetailsModel viewProductDetailsModel;

    private DigitalCareBaseFragment digitalCareBaseFragmentspy;


    View rootView;
    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        fragment = new RateThisAppFragment();
        viewProductDetailsModel = new ViewProductDetailsModel();
        viewProductDetailsModel.setProductName("AqvaShaver");
        viewProductDetailsModel.setCtnName("Shaver_2234");
        viewProductDetailsModel.setProductImage("http://www.philips.com");
        viewProductDetailsModel.setProductInfoLink("http://www.philips.com");
        viewProductDetailsModel.setDomain("Philips");

        PowerMockito.mockStatic(DigitalCareConfigManager.class);
        when(DigitalCareConfigManager.getInstance()).thenReturn(mockDigitalCareConfigManager);
        when(mockDigitalCareConfigManager.getViewProductDetailsData()).thenReturn(viewProductDetailsModel);
        digitalCareBaseFragmentspy=spy(fragment);

        SupportFragmentTestUtil.startFragment(fragment,DigitalCareTestMock.class);
        rootView=fragment.getView();
        Robolectric.buildActivity(DigitalCareTestMock.class).create().get();
    }

    @After
    public void tearDown() throws Exception{
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
    public void testPhilipsReviewButtonVisibilityGone(){
        ViewProductDetailsModel viewProductDetailsModel=new ViewProductDetailsModel();
        viewProductDetailsModel.setProductInfoLink(null);
        fragment.onPRXProductPageReceived(viewProductDetailsModel);
        Button mRatePhilipsBtn=(Button) rootView.findViewById(
                R.id.tellus_PhilipsReviewButton);
        Assert.assertEquals(View.GONE,mRatePhilipsBtn.getVisibility());
    }

    @Test
    public void testPhilipsReviewButtonVisibilityVisible(){
        ViewProductDetailsModel viewProductDetailsModel=new ViewProductDetailsModel();
        viewProductDetailsModel.setProductInfoLink("http://www.philips.com");
        fragment.onPRXProductPageReceived(viewProductDetailsModel);

        Button mRatePhilipsBtn=(Button) rootView.findViewById(
                R.id.tellus_PhilipsReviewButton);
        Assert.assertEquals(View.VISIBLE,mRatePhilipsBtn.getVisibility());
    }

    @Test
    public void testPerformTellusPhilipsReviewButton(){
        SupportFragmentTestUtil.startFragment(digitalCareBaseFragmentspy, DigitalCareTestMock.class);
        PowerMockito.mockStatic(DigitalCareConfigManager.class);
        when(DigitalCareConfigManager.getInstance()).thenReturn(mockDigitalCareConfigManager);
        when(DigitalCareConfigManager.getInstance().getTaggingInterface()).thenReturn(mockAppTaggingInterface);
        digitalCareBaseFragmentspy=spy(fragment);
        digitalCareBaseFragmentspy.isInternetAvailable=true;
        digitalCareBaseFragmentspy.getView().findViewById(R.id.tellus_PhilipsReviewButton).performClick();
    }

    @Test
    public void testPerformPlayStoreReviewButton(){
        SupportFragmentTestUtil.startFragment(digitalCareBaseFragmentspy, DigitalCareTestMock.class);
        //  Mockito.doNothing().when(digitalCareBaseFragmentspy).showFragment(fragment);
        PowerMockito.mockStatic(DigitalCareConfigManager.class);
        when(DigitalCareConfigManager.getInstance()).thenReturn(mockDigitalCareConfigManager);
        when(DigitalCareConfigManager.getInstance().getTaggingInterface()).thenReturn(mockAppTaggingInterface);
        digitalCareBaseFragmentspy=spy(fragment);
        digitalCareBaseFragmentspy.isInternetAvailable=true;
        digitalCareBaseFragmentspy.getView().findViewById(R.id.tellus_PlayStoreReviewButton).performClick();
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
    public void testonConfigurationChanged(){
        Configuration configuration = new Configuration();
        fragment.onConfigurationChanged(configuration);
    }

    @Test
    public void testDestroyMethod(){
        fragment.onDestroy();
    }
}