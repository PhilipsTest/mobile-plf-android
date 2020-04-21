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
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.rest.RestInterface;

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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@RunWith(RobolectricTestRunner.class)
public class GetShoppingCartTest {
    private Context mContext;


    MockECSServices mockECSServices;
    ECSServices ecsServices;


    private AppInfra appInfra;


    @Mock
    RestInterface mockRestInterface;

    MockGetECSShoppingCartsRequest mockGetECSShoppingCartsRequest;

    ECSCallback<ECSShoppingCart, Exception> ecsCallback;
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

        ecsCallback = new ECSCallback<ECSShoppingCart, Exception>() {
            @Override
            public void onResponse(ECSShoppingCart result) {

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {

            }
        };

        mockGetECSShoppingCartsRequest = new MockGetECSShoppingCartsRequest("ShoppingCartSuccess.json",ecsCallback);

    }

    @Test
    public void getCartSuccess(){
        mockInputValidator.setJsonFileName("ShoppingCartSuccess.json");
        mockECSServices.fetchShoppingCart(new ECSCallback<ECSShoppingCart, Exception>() {
            @Override
            public void onResponse(ECSShoppingCart result) {
                assertNotNull(result);
                assertNotNull(result.getGuid());
                // test case passed
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertEquals(7999, ecsError);
                // test case failed
            }
        });
    }

    @Test
    public void getCartWithoutGUID(){
        mockInputValidator.setJsonFileName("ShoppingCartWithoutGuid.json");
        mockECSServices.fetchShoppingCart(new ECSCallback<ECSShoppingCart, Exception>() {
            @Override
            public void onResponse(ECSShoppingCart result) {
                assertTrue(false);
                // test case failed
            }
            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertEquals("No cart created yet",error.getMessage());

                // test case passed
            }
        });
    }

    @Test
    public void getCartAuthFailureResponse(){
        mockInputValidator.setJsonFileName("ShoppingCartAuthError.json");
        mockECSServices.fetchShoppingCart(new ECSCallback<ECSShoppingCart, Exception>() {
            @Override
            public void onResponse(ECSShoppingCart result) {
                assertNotNull(result);
                assertNotNull(result.getGuid());
                // test case passed
            }
            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertEquals("No cart created yet",error.getMessage());

                // test case failed
            }
        });
    }

    @Test
    public void getCartEmptyResponse(){
        mockInputValidator.setJsonFileName("EmptyJson.json");
        mockECSServices.fetchShoppingCart(new ECSCallback<ECSShoppingCart, Exception>() {
            @Override
            public void onResponse(ECSShoppingCart result) {
                assertNotNull(result);
                assertNotNull(result.getGuid());
                // test case passed
            }
            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertTrue(true);

                // test case failed
            }
        });
    }


    @Test
    public void isValidURL() {
        System.out.println("print the URL"+mockGetECSShoppingCartsRequest.getURL());
        //acc.us.pil.shop.philips.com/pilcommercewebservices/v2/US_Tuscany/users/current/carts/current?lang=en_US
        String excepted = StaticBlock.getBaseURL()+"pilcommercewebservices"+"/v2/"+StaticBlock.getSiteID()+"/users/current/carts/current?lang="+StaticBlock.getLocale();
        assertEquals(excepted,mockGetECSShoppingCartsRequest.getURL());
    }

    @Test
    public void isValidGetRequest() {
        assertEquals(0,mockGetECSShoppingCartsRequest.getMethod());
    }

    @Test
    public void isValidHeader() {

        Map<String, String> expectedMap = new HashMap<String, String>();
        expectedMap.put("Authorization", "Bearer " + "acceesstoken");

        Map<String, String> actual = mockGetECSShoppingCartsRequest.getHeader();

        assertTrue(expectedMap.equals(actual));
    }

    @Test
    public void verifyOnResponseError() {
        ECSCallback<ECSShoppingCart, Exception> spy1 = Mockito.spy(ecsCallback);
        mockGetECSShoppingCartsRequest = new MockGetECSShoppingCartsRequest("ShoppingCartSuccess.json",spy1);
        VolleyError volleyError = new NoConnectionError();
        mockGetECSShoppingCartsRequest.onErrorResponse(volleyError);
        Mockito.verify(spy1).onFailure(any(Exception.class),any(ECSError.class));

    }


    @Test
    public void assertResponseSuccessListenerNotNull() {
        assertNotNull(mockGetECSShoppingCartsRequest.getJSONSuccessResponseListener());
    }

}
