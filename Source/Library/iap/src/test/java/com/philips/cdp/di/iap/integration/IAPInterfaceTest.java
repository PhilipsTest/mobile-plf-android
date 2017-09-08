package com.philips.cdp.di.iap.integration;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.TestUtils;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.listener.ActionBarListener;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;

@RunWith(RobolectricTestRunner.class)
public class IAPInterfaceTest {

    @Mock
    Context mContext;

    @Mock
    private IAPDependencies mIAPDependencies;

    @Mock
    private IAPLaunchInput mIapLaunchInput;


    private IAPSettings mIAPSettings;
    private IAPInterface mIapInterface;
    @Mock
    private IAPHandler mockHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mIAPSettings = new IAPSettings(mContext);
        mIapInterface = new MockIAPInterface();
        mIapLaunchInput.setIapListener(Mockito.mock(IAPListener.class));
    }

    @Test
    public void testInit() {
        mIapInterface.init(mIAPDependencies, mIAPSettings);
        Assert.assertNotNull(mIAPDependencies);
        Assert.assertNotNull(mIAPSettings);
    }

    @Test
    public void testLaunch() {
        TestUtils.getStubbedHybrisDelegate();
        FragmentActivity activity = Robolectric.setupActivity(FragmentActivity.class);
        ArrayList<String> blackListedRetailer = new ArrayList<>();
        IAPFlowInput input = new IAPFlowInput("HX9043/64");
        IAPLaunchInput iapLaunchInput = new IAPLaunchInput();
        iapLaunchInput.setIAPFlow(IAPLaunchInput.IAPFlows.IAP_PRODUCT_DETAIL_VIEW, input, blackListedRetailer);
        mIAPSettings.setUseLocalData(true);
        mIapInterface.init(mIAPDependencies, mIAPSettings);
        mIapInterface.launch(new FragmentLauncher(activity, R.id.cart_container, Mockito.mock(ActionBarListener.class)), iapLaunchInput);
    }

    @Test
    public void testGetProductCount() {
        IAPListener listener = Mockito.mock(IAPListener.class);
        mIapInterface.getProductCartCount(listener);
        Assert.assertNotNull(listener);

    }

    @Test
    public void getCompleteProductList() {
        IAPListener listener = Mockito.mock(IAPListener.class);
        mIapInterface.getCompleteProductList(listener);
        Assert.assertNotNull(listener);
    }


    @Test
    public void isCartVisible() {
        IAPListener listener = Mockito.mock(IAPListener.class);
        mIapInterface.init(mIAPDependencies, mIAPSettings);
        mIapInterface.isCartVisible(listener);
        Assert.assertNotNull(listener);
    }

}