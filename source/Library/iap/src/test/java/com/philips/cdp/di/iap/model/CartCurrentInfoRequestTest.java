package com.philips.cdp.di.iap.model;

import android.content.Context;
import android.os.Message;

import com.android.volley.Request;
import com.philips.cdp.di.iap.TestUtils;
import com.philips.cdp.di.iap.core.StoreSpec;
import com.philips.cdp.di.iap.response.carts.Carts;
import com.philips.cdp.di.iap.store.IAPUser;
import com.philips.cdp.di.iap.store.MockStore;
import com.philips.cdp.di.iap.store.NetworkURLConstants;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CartCurrentInfoRequestTest {
    @Mock
    private StoreSpec mStore;

    @Before
    public void setUP() {
        mStore = new MockStore(mock(Context.class), mock(IAPUser.class)).getStore();
        mStore.initStoreConfig("en", "us", null);
    }

    @Test
    public void matchCartCreateRequestURL() {
        GetCartDetailsRequest request = new GetCartDetailsRequest(mStore, null, null);
        Assert.assertEquals(NetworkURLConstants.CART_DETAIL_URL, request.getUrl());
    }

    @Test
    public void testRequestMethodIsGET() {
        GetCartDetailsRequest request = new GetCartDetailsRequest(mStore, null, null);
        Assert.assertEquals(Request.Method.GET, request.getMethod());
    }

    @Test
    public void testQueryParamsIsNull() {
        GetCartDetailsRequest request = new GetCartDetailsRequest(mStore, null, null);
        Assert.assertNull(request.requestBody());
    }

    @Test
    public void parseResponseShouldBeOfCartCurrentInfoDataType() {
        GetCartDetailsRequest request = new GetCartDetailsRequest(mStore, null, null);
        String oneAddress = TestUtils.readFile(CartCurrentInfoRequestTest.class, "create_cart.txt");
        Object response = request.parseResponse(oneAddress);
        Assert.assertEquals(response.getClass(), Carts.class);
    }

    @Test
    public void testonPostSuccess() {
        AbstractModel.DataLoadListener listener = Mockito.mock(AbstractModel.DataLoadListener.class);
        Message msg = Mockito.mock(Message.class);
        GetCartDetailsRequest request = new GetCartDetailsRequest(mStore, null, listener);
        GetCartDetailsRequest mockrequest = Mockito.spy(request);//new GetCartDetailsRequest(mStore, null, listener);
        mockrequest.onPostSuccess(msg);
        verify(listener, Mockito.atLeast(1)).onModelDataLoadFinished(msg);
    }
}