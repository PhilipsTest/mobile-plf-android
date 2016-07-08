package com.philips.cdp.di.iap.model;

import android.content.Context;

import com.android.volley.Request;
import com.philips.cdp.di.iap.TestUtils;
import com.philips.cdp.di.iap.core.StoreSpec;
import com.philips.cdp.di.iap.response.products.ProductDetailEntity;
import com.philips.cdp.di.iap.store.IAPUser;
import com.philips.cdp.di.iap.store.MockStore;
import com.philips.cdp.di.iap.store.NetworkURLConstants;
import com.philips.cdp.di.iap.utils.ModelConstants;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.*;

public class ProductDetailRequestTest {

    @Mock
    Context mContext;
    @Mock
    IAPUser mUser;
    private StoreSpec mStore;
    private AbstractModel mModel;

    @Before
    public void setUp() {
        mStore = (new MockStore(mContext, mUser)).getStore();
        mStore.initStoreConfig("en","US", null);
        mModel = new ProductDetailRequest(mStore, null, null);
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

    @Test
    public void productDetaiURL() {
        HashMap<String, String> query = new HashMap<>();
        query.put(ModelConstants.PRODUCT_CODE, NetworkURLConstants.DUMMY_PRODUCT_ID);
        ProductDetailRequest request = new ProductDetailRequest(mStore, query, null);
        assertEquals(NetworkURLConstants.PRODUCT_DETAIL_URL, request.getUrl());
    }

    @Test(expected = RuntimeException.class)
    public void testProductDetailURLWhenParamsIsNull() throws Exception {
        assertNotEquals(NetworkURLConstants.PRODUCT_DETAIL_URL, mModel.getUrl());
    }

    @Test
    public void parseResponseShouldBeOfGetShippingAddressDataType() {
        String oneAddress = TestUtils.readFile(ProductDetailRequestTest.class, "product_detail.txt");
        Object response = mModel.parseResponse(oneAddress);
        assertEquals(response.getClass(), ProductDetailEntity.class);
    }
}