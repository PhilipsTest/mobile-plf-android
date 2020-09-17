/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.shoppingcart;

import android.content.Context;

import com.philips.cdp.di.iap.prx.PRXAssetExecutor;
import com.philips.cdp.prxclient.request.ProductAssetRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PRXProductAssetBuilderTest {

    @Mock
    private Context context;
    @Mock
    private PRXAssetExecutor mPrxProductAssetBuilder;
    @Mock
    private ProductAssetRequest mProductAssetBuilder;

    @Before
    public void setUP() {
        mPrxProductAssetBuilder = Mockito.mock(PRXAssetExecutor.class);
    }

    @Test
    public void executeRequest() {
        mPrxProductAssetBuilder.executeRequest(mProductAssetBuilder);
        verify(mPrxProductAssetBuilder).executeRequest(mProductAssetBuilder);
    }
}