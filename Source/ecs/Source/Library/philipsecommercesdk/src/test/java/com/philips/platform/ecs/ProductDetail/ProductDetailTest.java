package com.philips.platform.ecs.ProductDetail;

import android.content.Context;

import com.philips.platform.ecs.ECSServices;
import com.philips.platform.ecs.MockECSServices;
import com.philips.platform.ecs.MockInputValidator;
import com.philips.platform.ecs.StaticBlock;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.products.ECSProduct;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.rest.RestInterface;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class ProductDetailTest {

    private Context mContext;


    MockECSServices mockECSServices;
    ECSServices ecsServices;


    private AppInfra appInfra;


    @Mock
    RestInterface mockRestInterface;
    private MockInputValidator mockInputValidator;


    @Before
    public void setUp() throws Exception {


        mContext = getInstrumentation().getContext();
        appInfra = new AppInfra.Builder().setRestInterface(mockRestInterface).build(mContext);
        appInfra.getServiceDiscovery().setHomeCountry("DE");

        StaticBlock.initialize();
        mockInputValidator = new MockInputValidator();

        mockECSServices = new MockECSServices("", appInfra);
        ecsServices = new ECSServices("",appInfra);

    }


    // This will fetch Product detail:  Asset and Disclaimer
    @Test
    public void getProductDetailSuccess() {
        ECSProduct product = new ECSProduct();
        product.setCode("HX2345/01");
        mockInputValidator.setJsonFileName("PRXProductAssets.json");
        mockECSServices.fetchProductDetails(product, new ECSCallback<ECSProduct, Exception>() {
            @Override
            public void onResponse(ECSProduct product) {
                assertNotNull(product);
                assertNotNull(product.getAssets());
                assertNotNull(product.getDisclaimers());
                // test case passed
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertFalse(true);
                // test case failed
            }
        });
    }


    @Test
    public void getProductDetailAssetFailure() {
        ECSProduct product = new ECSProduct();
        product.setCode("HX2345/01");
        mockInputValidator.setJsonFileName("EmptyJson.json");
        mockECSServices.fetchProductDetails(product, new ECSCallback<ECSProduct, Exception>() {
            @Override
            public void onResponse(ECSProduct product) {
                assertTrue(true);
                // test case failed
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertEquals(5999, ecsError); // error code for Product List
                // test case passed
            }
        });
    }
}
