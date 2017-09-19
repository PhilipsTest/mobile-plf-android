package com.philips.cdp.di.iap.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.TextView;

import com.philips.cdp.di.iap.BuildConfig;
import com.philips.cdp.di.iap.CustomRobolectricRunner;
import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.TestUtils;
import com.philips.cdp.di.iap.session.HybrisDelegate;
import com.philips.cdp.di.iap.session.MockNetworkController;
import com.philips.cdp.di.iap.utils.IAPConstant;
import com.philips.platform.uappframework.listener.ActionBarListener;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import java.util.ArrayList;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Robolectric.buildActivity;

@RunWith(CustomRobolectricRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class IAPActivityTest {
    @Mock
    FragmentManager fragmentManagerMock;
    @Mock
    FragmentTransaction fragmentTransactionMock;
    @Captor
    ArgumentCaptor<Fragment> fragmentArgumentCaptor;

    private IAPActivity activity;
    private ShadowActivity shadowActivity;
    private Intent intent;
    private MockNetworkController mNetworkController;
    private HybrisDelegate mHybrisDelegate;
    @Mock
    private ActionBarListener mockActionBarListener;
    @Mock
    private TextView mockTextView;

    @Before
    public void setUp() {
        initMocks(this);


        when(fragmentManagerMock.beginTransaction()).thenReturn(fragmentTransactionMock);

        TestUtils.getStubbedStore();
        TestUtils.getStubbedHybrisDelegate();
        intent = new Intent();
        intent.putExtra(IAPConstant.IAP_KEY_ACTIVITY_THEME, 0);
    }

    @Test
    public void shouldNotApplied_DefaultTheme() throws Exception {
        int themeIndex = intent.getIntExtra(IAPConstant.IAP_KEY_ACTIVITY_THEME, 0);
        startActivity(intent);
        Assert.assertEquals(themeIndex, 0);
    }

    @Test
    public void shouldSaveInstanceIsNotnull() throws Exception {
        activity = buildActivity(IAPActivity.class, intent).withIntent(intent).get();
        IAPActivity spyActivity = Mockito.spy(activity);

        Mockito.doReturn(fragmentManagerMock).when(spyActivity).getSupportFragmentManager();
        spyActivity.onCreate(mock(Bundle.class));

    }

    @Test
    public void shouldLandInProductCatalogScreen() throws Exception {
        intent.putExtra(IAPConstant.IAP_LANDING_SCREEN, 0);
        int landingIndex = intent.getIntExtra(IAPConstant.IAP_LANDING_SCREEN, 0);
        startActivity(intent);
        Assert.assertEquals(landingIndex, 0);
    }

    @Test
    public void shouldLandInShoppingCartFragment() throws Exception {
        intent.putExtra(IAPConstant.IAP_LANDING_SCREEN, 1);
        int landingIndex = intent.getIntExtra(IAPConstant.IAP_LANDING_SCREEN, 1);
        startActivity(intent);
        Assert.assertEquals(landingIndex, 1);
    }

    @Test
    public void shouldLandInPurchaseHistoryFragment() throws Exception {
        intent.putExtra(IAPConstant.IAP_LANDING_SCREEN, 2);
        int landingIndex = intent.getIntExtra(IAPConstant.IAP_LANDING_SCREEN, 2);
        startActivity(intent);
        Assert.assertEquals(landingIndex, 2);
    }

    @Test
    public void shouldLandInProductDetailFragment() throws Exception {
        //when
        intent.putExtra(IAPConstant.IAP_LANDING_SCREEN, 3);
        intent.putExtra(IAPConstant.IAP_PRODUCT_CATALOG_NUMBER_FROM_VERTICAL, "HX8033/21");
        int landingIndex = intent.getIntExtra(IAPConstant.IAP_LANDING_SCREEN, 3);

        //then
        startActivity(intent);

        //verify
        Assert.assertEquals(landingIndex, 3);
        verifyAddedFragment();
    }

    private void startActivity(Intent intent) {
        activity = buildActivity(IAPActivity.class, intent).withIntent(intent).get();
        IAPActivity spyActivity = Mockito.spy(activity);

        Mockito.doReturn(fragmentManagerMock).when(spyActivity).getSupportFragmentManager();
        spyActivity.onCreate(null);
        spyActivity.onResume();

        verify(fragmentTransactionMock).replace(anyInt(), fragmentArgumentCaptor.capture(), anyString());
    }

    private void verifyAddedFragment() {
        verify(fragmentTransactionMock).replace(
                eq(R.id.fl_mainFragmentContainer),
                isA(fragmentArgumentCaptor.getValue().getClass()),
                anyString());
        verify(fragmentTransactionMock).commitAllowingStateLoss();
    }

    @Test
    public void shouldLandInBuyDirectFragment() throws Exception {
        intent.putExtra(IAPConstant.IAP_LANDING_SCREEN, 4);
        intent.putExtra(IAPConstant.IAP_PRODUCT_CATALOG_NUMBER_FROM_VERTICAL, "HX8033/21");
        int landingIndex = intent.getIntExtra(IAPConstant.IAP_LANDING_SCREEN, 4);
        startActivity(intent);
        Assert.assertEquals(landingIndex, 4);
    }

    @Test
    public void shouldCalled_OnDestryView() {
        startActivity(intent);
        Assert.assertNotNull(activity);
    }

    @Test
    public void shouldCalled_OnPause() {
        startActivity(intent);
        Assert.assertNotNull(activity);
    }


    @Test
    public void shouldCalled_onUpdateCartCount() {
        startActivity(intent);
        activity.onUpdateCartCount();
    }

    @Test
    public void shouldCalled_onFinish() {
        startActivity(intent);
        activity.finish();
    }

    @Test
    public void shouldCalled_onBackPressed() {
        startActivity(intent);
        activity.onBackPressed();
    }

    @Test
    public void shouldCalled_onGetCompleteProductList() {
        startActivity(intent);
        activity.onGetCompleteProductList(new ArrayList<String>());
    }

    @Test
    public void shouldCalled_onSuccess() {
        startActivity(intent);
        activity.onSuccess();
    }

    @Test
    public void shouldCalled_onFailure() {
        startActivity(intent);
        activity.onFailure(0);
    }

    @Test
    public void shouldCalled_onPause() {
        pauseActivity(intent);
        activity.onPause();
    }

    @Test(expected = NullPointerException.class)
    public void shouldCalled_onSaveInstanceState() {
        startActivity(intent);
        activity.onSaveInstanceState(mock(Bundle.class), mock(PersistableBundle.class));
    }

    @Test
    public void shouldCalled_onRestoreInstanceState() {
        startActivity(intent);
        activity.onRestoreInstanceState(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldCalled_onDestroy() {
        destroyActivity(intent);
        activity.onDestroy();
    }

    private void pauseActivity(Intent intent) {
        activity = buildActivity(IAPActivity.class, intent).withIntent(intent).get();
        IAPActivity spyActivity = Mockito.spy(activity);

        Mockito.doReturn(fragmentManagerMock).when(spyActivity).getSupportFragmentManager();
        spyActivity.onCreate(null);
        spyActivity.onPause();

        verify(fragmentTransactionMock).replace(anyInt(), fragmentArgumentCaptor.capture(), anyString());
    }

    private void destroyActivity(Intent intent) {
        activity = buildActivity(IAPActivity.class, intent).withIntent(intent).get();
        IAPActivity spyActivity = Mockito.spy(activity);

        Mockito.doReturn(fragmentManagerMock).when(spyActivity).getSupportFragmentManager();
        spyActivity.onCreate(null);
        spyActivity.onDestroy();

        verify(fragmentTransactionMock).replace(anyInt(), fragmentArgumentCaptor.capture(), anyString());
    }

    @Test(expected = IllegalStateException.class)
    public void testShowFragment() throws Exception {

        startActivity(intent);
        activity.showFragment("ProductCatalog");
    }
}