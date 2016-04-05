package com.philips.cdp.di.iap.shoppingcart;

import android.content.Context;

import com.philips.cdp.di.iap.shoppingCart.PRXProductAssetBuilder;
import com.philips.cdp.prxclient.prxdatabuilder.ProductAssetBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(MockitoJUnitRunner.class)
public class PRXProductAssetBuilderTest {

    @Mock
    private Context context;
    @Mock
    private PRXProductAssetBuilder mPrxProductAssetBuilder;
    @Mock
    private ProductAssetBuilder mProductAssetBuilder;



    @Before
    public void setUP() {
        mPrxProductAssetBuilder = Mockito.mock(PRXProductAssetBuilder.class);
    }

    @Test
    public void executeRequest(){
        mPrxProductAssetBuilder.executeRequest(mProductAssetBuilder);
        verify(mPrxProductAssetBuilder).executeRequest(mProductAssetBuilder);
    }

}