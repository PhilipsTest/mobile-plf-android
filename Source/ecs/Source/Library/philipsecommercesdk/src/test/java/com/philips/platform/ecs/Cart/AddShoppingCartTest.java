package com.philips.platform.ecs.Cart;

import android.content.Context;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.philips.platform.ecs.ECSServices;
import com.philips.platform.ecs.MockECSServices;
import com.philips.platform.ecs.MockInputValidator;
import com.philips.platform.ecs.StaticBlock;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.cart.ECSShoppingCart;
import com.philips.platform.ecs.model.products.ECSProduct;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.rest.RestInterface;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;
import java.util.Map;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@RunWith(RobolectricTestRunner.class)
public class AddShoppingCartTest {
    private Context mContext;


    MockECSServices mockECSServices;
    ECSServices ecsServices;


    private AppInfra appInfra;


    @Mock
    RestInterface mockRestInterface;

    MockAddProductToECSShoppingCartRequest mockAddProductToECSShoppingCartRequest;

    ECSCallback ecsCallback;


    ECSProduct product ;
    private MockInputValidator mockInputValidator;

    @Before
    public void setUp() throws Exception {


        mContext = getInstrumentation().getContext();
        appInfra = new AppInfra.Builder().setRestInterface(mockRestInterface).build(mContext);
        appInfra.getServiceDiscovery().setHomeCountry("DE");


        mockECSServices = new MockECSServices("", appInfra);
        ecsServices = new ECSServices("",appInfra);

        mockInputValidator = new MockInputValidator();

        StaticBlock.initialize();

        product = new ECSProduct();

        ecsCallback = new ECSCallback<ECSShoppingCart, Exception>() {
            @Override
            public void onResponse(ECSShoppingCart result) {

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {

            }
        };

        mockAddProductToECSShoppingCartRequest = new MockAddProductToECSShoppingCartRequest("AddShoppingCartSuccess.json","1234",ecsCallback);
    }

    @Test
    public void addShoppingCartSuccess(){
        mockInputValidator.setJsonFileName("AddShoppingCartSuccess.json");
        ECSProduct product = new ECSProduct();
        mockECSServices.addProductToShoppingCart(product, new ECSCallback<ECSShoppingCart, Exception>() {
            @Override
            public void onResponse(ECSShoppingCart result) {
                assertNotNull(result);
                assertNotNull(result.getGuid());

            // test case passed
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
               // assertEquals(ECSErrorEnum.ECSInvalidProductError.toString(), ecsError.getErrorType());
                // test case failed
            }
        });

    }

    @Test
    public void addShoppingCartFailure(){
        mockInputValidator.setJsonFileName("EmptyString.json");
        ECSProduct product = new ECSProduct();
        mockECSServices.addProductToShoppingCart(product, new ECSCallback<ECSShoppingCart, Exception>() {
            @Override
            public void onResponse(ECSShoppingCart result) {
                assertNotNull(result);
                assertNotNull(result.getGuid());
            // test case failed
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertTrue(true);
                // test case passed
            }
        });

    }

    @Test
    public void isValidURL() {
        System.out.println("print the URL"+mockAddProductToECSShoppingCartRequest.getURL());
        String excepted = StaticBlock.getBaseURL()+"pilcommercewebservices"+"/v2/"+StaticBlock.getSiteID()+"/users/current/carts/current/entries?fields=FULL&lang="+StaticBlock.getLocale();
        Assert.assertEquals(excepted,mockAddProductToECSShoppingCartRequest.getURL());
    }

    @Test
    public void isValidPostRequest() {
        Assert.assertEquals(1,mockAddProductToECSShoppingCartRequest.getMethod());
    }

    @Test
    public void isValidHeader() {

        Map<String, String> expectedMap = new HashMap<String, String>();
        expectedMap.put("Content-Type", "application/x-www-form-urlencoded");
        expectedMap.put("Authorization", "Bearer " + "acceesstoken");

        Map<String, String> actual = mockAddProductToECSShoppingCartRequest.getHeader();

        assertTrue(expectedMap.equals(actual));
    }

    @Test
    public void isValidParam() {

        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("code", "1234");


        assertNotNull(mockAddProductToECSShoppingCartRequest.getParams());
        assertTrue(expectedMap.equals(mockAddProductToECSShoppingCartRequest.getParams()));
    }




    @Test
    public void verifyOnResponseError() {
        ECSCallback<Boolean, Exception> spy1 = Mockito.spy(ecsCallback);
        mockAddProductToECSShoppingCartRequest = new MockAddProductToECSShoppingCartRequest("EmptyString.json","1234" , spy1);
        VolleyError volleyError = new NoConnectionError();
        mockAddProductToECSShoppingCartRequest.onErrorResponse(volleyError);
        Mockito.verify(spy1).onFailure(any(Exception.class),any(ECSError.class));

    }

    @Test
    public void verifyOnResponseSuccessError() {

        ECSCallback  ecsCallback = new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {

            }
        };

        ECSCallback<Boolean, Exception> spy1 = Mockito.spy(ecsCallback);
        mockAddProductToECSShoppingCartRequest = new MockAddProductToECSShoppingCartRequest("EmptyString.json","1234" , spy1);

        mockAddProductToECSShoppingCartRequest.onResponse(null);
        Mockito.verify(spy1).onResponse(false);

    }


    @Test
    public void assertResponseSuccessListenerNotNull() {
        assertNotNull(mockAddProductToECSShoppingCartRequest.getStringSuccessResponseListener());
    }

}
