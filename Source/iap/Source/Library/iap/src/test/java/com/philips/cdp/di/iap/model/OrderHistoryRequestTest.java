package com.philips.cdp.di.iap.model;

import android.content.Context;

import com.android.volley.Request;
import com.philips.cdp.di.iap.integration.MockIAPSetting;
import com.philips.cdp.di.iap.store.IAPUser;
import com.philips.cdp.di.iap.store.MockStore;
import com.philips.cdp.di.iap.store.NetworkURLConstants;
import com.philips.cdp.di.iap.store.StoreListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
public class OrderHistoryRequestTest {
    private Context mContext;

    @Mock
    private IAPUser mUser;
    @Mock
    private MockIAPSetting mIAPSetting;

    private AbstractModel mModel;

    @Before
    public void setUp() {
        mContext = getInstrumentation().getContext();
        mIAPSetting = new MockIAPSetting(mContext);
        StoreListener mStore = (new MockStore(mContext, mUser)).getStore(mIAPSetting);
        mStore.initStoreConfig(/*"en", "US",*/ null);
        mModel = new OrderHistoryRequest(mStore, null, null);
    }

    @Test
    public void testRequestMethodIsGET() {
        assertEquals(Request.Method.GET, mModel.getMethod());
    }

    @Test
    public void testQueryParamsIsNull() {
        assertNull(mModel.requestBody());
    }

    @Test
    public void testStoreIsNotNull() {
        assertNotNull(mModel.getStore());
    }

    @Test(expected = RuntimeException.class)
    public void orderHistoryURL() {
        assertEquals(NetworkURLConstants.PLACE_ORDER_URL, mModel.getUrl());
    }

//    @Test
//    public void isValidResponse() {
//        String validAddress = TestUtils.readFile(OrderHistoryRequestTest.class, "Orders.txt");
//        Object response = mModel.parseResponse(validAddress);
//        assertEquals(response.getClass(), OrdersData.class);
//    }
}