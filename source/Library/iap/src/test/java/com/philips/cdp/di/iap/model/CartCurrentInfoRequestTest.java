package com.philips.cdp.di.iap.model;

import android.content.Context;

import com.android.volley.Request;
import com.philips.cdp.di.iap.TestUtils;
import com.philips.cdp.di.iap.response.carts.Carts;
import com.philips.cdp.di.iap.store.IAPUser;
import com.philips.cdp.di.iap.store.MockStore;
import com.philips.cdp.di.iap.store.NetworkURLConstants;
import com.philips.cdp.di.iap.store.Store;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;

/**
 * Created by 310164421 on 3/8/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class CartCurrentInfoRequestTest extends TestCase {
    @Mock
    private Store mStore;

    @Before
    public void setUP() {
        mStore = new MockStore(mock(Context.class), mock(IAPUser.class)).getStore();
        mStore.initStoreConfig("en", "us", null);
    }

    @Test
    public void matchCartCreateRequestURL() {
        CartCurrentInfoRequest request = new CartCurrentInfoRequest(mStore, null, null);
        assertEquals(NetworkURLConstants.CART_DETAIL_URL, request.getUrl());
    }

    @Test
    public void testRequestMethodIsGET() {
        CartCurrentInfoRequest request = new CartCurrentInfoRequest(mStore, null, null);
        assertEquals(Request.Method.GET, request.getMethod());
    }

    @Test
    public void testQueryParamsIsNull() {
        CartCurrentInfoRequest request = new CartCurrentInfoRequest(mStore, null, null);
        assertNull(request.requestBody());
    }

    @Test
    public void parseResponseShouldBeOfGetShippingAddressDataType() {
        CartCurrentInfoRequest request = new CartCurrentInfoRequest(mStore, null, null);
        String oneAddress = TestUtils.readFile(CartCurrentInfoRequestTest.class, "create_cart.txt");
        Object response = request.parseResponse(oneAddress);
        assertEquals(response.getClass(), Carts.class);
    }
}