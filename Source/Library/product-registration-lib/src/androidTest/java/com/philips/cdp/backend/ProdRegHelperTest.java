package com.philips.cdp.backend;

import android.content.Context;
import android.support.annotation.NonNull;

import com.philips.cdp.MockitoTestCase;
import com.philips.cdp.handler.ProdRegListener;
import com.philips.cdp.prxclient.RequestManager;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */

public class ProdRegHelperTest extends MockitoTestCase {

    ProdRegHelper prodRegHelper;
    Context mContext;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        prodRegHelper = new ProdRegHelper();
        mContext = getInstrumentation().getContext();
    }

    public void testRegisterProduct() {
        final UserProduct[] userProduct = new UserProduct[1];
        ProdRegHelper prodRegHelper = new ProdRegHelper() {
            @NonNull
            @Override
            protected UserProduct getUserProduct(final Product product) {
                userProduct[0] = mock(UserProduct.class);
                return userProduct[0];
            }
        };
        Product product = mock(Product
                .class);
        ProdRegListener prodRegListener = mock(ProdRegListener.class);
        prodRegHelper.registerProduct(mContext, product, prodRegListener);
        RequestManager requestManager = mock(RequestManager.class);
        verify(userProduct[0]).registerProduct(mContext, product, prodRegListener);

    }

}