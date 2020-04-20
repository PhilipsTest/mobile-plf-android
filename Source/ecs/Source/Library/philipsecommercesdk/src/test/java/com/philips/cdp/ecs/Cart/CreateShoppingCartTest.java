package com.philips.cdp.ecs.Cart;

import android.content.Context;


import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.philips.platform.ecs.ECSServices;
import com.philips.cdp.ecs.MockECSServices;
import com.philips.cdp.ecs.MockInputValidator;
import com.philips.cdp.ecs.StaticBlock;
import com.philips.cdp.ecs.TestUtil;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.cart.AppliedVoucherEntity;
import com.philips.platform.ecs.model.cart.ECSShoppingCart;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.rest.RestInterface;


import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@RunWith(RobolectricTestRunner.class)
public class CreateShoppingCartTest {

    private Context mContext;


    MockECSServices mockECSServices;
    ECSServices ecsServices;


    private AppInfra appInfra;

    @Mock
    List<AppliedVoucherEntity> list;


    @Mock
    RestInterface mockRestInterface;

    MockCreateECSShoppingCartRequest mockCreateECSShoppingCartRequest;

    ECSCallback<ECSShoppingCart, Exception>  ecsCallback;
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

        mockCreateECSShoppingCartRequest = new MockCreateECSShoppingCartRequest("ShoppingCartSuccess.json",ecsCallback);

    }

    @Test
    public void createCartSuccess(){
        mockInputValidator.setJsonFileName("ShoppingCartSuccess.json");
        mockECSServices.createShoppingCart(new ECSCallback<ECSShoppingCart, Exception>() {
            @Override
            public void onResponse(ECSShoppingCart result) {
                assertNotNull(result);
                assertNotNull(result.getGuid());
                // test case passed
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assert true;
                // test case failed
            }
        });
    }

    @Test
    public void createCartFailure(){
        mockInputValidator.setJsonFileName("ShoppingCartWithoutGuid.json");
        mockECSServices.createShoppingCart(new ECSCallback<ECSShoppingCart, Exception>() {
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
    public void createCartEmptyResponse(){
        mockInputValidator.setJsonFileName("EmptyJson.json");
        mockECSServices.createShoppingCart(new ECSCallback<ECSShoppingCart, Exception>() {
            @Override
            public void onResponse(ECSShoppingCart result) {
                assertTrue(false);
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
        System.out.println("print the URL"+mockCreateECSShoppingCartRequest.getURL());
        String excepted = StaticBlock.getBaseURL()+"pilcommercewebservices"+"/v2/"+StaticBlock.getSiteID()+"/users/current/carts?lang="+StaticBlock.getLocale();
        Assert.assertEquals(excepted,mockCreateECSShoppingCartRequest.getURL());
    }

    @Test
    public void isValidPostRequest() {
        Assert.assertEquals(1,mockCreateECSShoppingCartRequest.getMethod());
    }

    @Test
    public void isValidHeader() {

        Map<String, String> expectedMap = new HashMap<String, String>();
        expectedMap.put("Authorization", "Bearer " + "acceesstoken");

        Map<String, String> actual = mockCreateECSShoppingCartRequest.getHeader();

        assertTrue(expectedMap.equals(actual));
    }

    @Test
    public void verifyOnResponseError() {
        ECSCallback<ECSShoppingCart, Exception> spy1 = Mockito.spy(ecsCallback);
        mockCreateECSShoppingCartRequest = new MockCreateECSShoppingCartRequest("ShoppingCartSuccess.json",spy1);
        VolleyError volleyError = new NoConnectionError();
        mockCreateECSShoppingCartRequest.onErrorResponse(volleyError);
        Mockito.verify(spy1).onFailure(any(Exception.class),any(ECSError.class));

    }

    @Test
    public void verifyOnResponseSuccess() {
        ECSCallback<ECSShoppingCart, Exception> spy1 = Mockito.spy(ecsCallback);
        mockCreateECSShoppingCartRequest = new MockCreateECSShoppingCartRequest("ShoppingCartSuccess.json",spy1);
        mockCreateECSShoppingCartRequest.onResponse(getJsonObject("ShoppingCartSuccess.json"));

        ECSShoppingCart ecsShoppingCart = new Gson().fromJson(getJsonObject("ShoppingCartSuccess.json").toString(),
                ECSShoppingCart.class);

        assertNotNull(ecsShoppingCart.getGuid());
        assertNotNull(ecsShoppingCart.getCode());
        assertNotNull(ecsShoppingCart.getTotalItems());
        assertNotNull(ecsShoppingCart.getStore());
        assertNotNull(ecsShoppingCart.getDeliveryItemsQuantity());
        assertNotNull(ecsShoppingCart.getOrderDiscounts());
        assertNotNull(ecsShoppingCart.getProductDiscounts());
        assertNotNull(ecsShoppingCart.getSubTotal());
        assertNotNull(ecsShoppingCart.getTotalPrice());
        assertNotNull(ecsShoppingCart.getTotalPriceWithTax());
        assertNotNull(ecsShoppingCart.getTotalTax());
        assertNotNull(ecsShoppingCart.getTotalDiscounts());
        assertNull(ecsShoppingCart.getDeliveryAddress());
        assertNull(ecsShoppingCart.getDeliveryCost());
        assertNull(ecsShoppingCart.getDeliveryMode());
        assertNull(ecsShoppingCart.getDeliveryOrderGroups());
        assertEquals(false,ecsShoppingCart.isCalculated());

        assertNotNull(ecsShoppingCart.getTotalDiscounts().getCurrencyIso());
        assertNotNull(ecsShoppingCart.getTotalDiscounts().getFormattedValue());
        assertNotNull(ecsShoppingCart.getTotalDiscounts().getPriceType());
        assertNotNull(ecsShoppingCart.getTotalDiscounts().getValue());

        assertNotNull(ecsShoppingCart.getTotalTax().getCurrencyIso());
        assertNotNull(ecsShoppingCart.getTotalTax().getFormattedValue());
        assertNotNull(ecsShoppingCart.getTotalTax().getPriceType());
        assertNotNull(ecsShoppingCart.getTotalTax().getValue());

        assertNotNull(ecsShoppingCart.getTotalPriceWithTax().getCurrencyIso());
        assertNotNull(ecsShoppingCart.getTotalPriceWithTax().getFormattedValue());
        assertNotNull(ecsShoppingCart.getTotalPriceWithTax().getPriceType());
        assertNotNull(ecsShoppingCart.getTotalPriceWithTax().getValue());

        assertNotNull(ecsShoppingCart.getTotalPrice().getCurrencyIso());
        assertNotNull(ecsShoppingCart.getTotalPrice().getFormattedValue());
        assertNotNull(ecsShoppingCart.getTotalPrice().getPriceType());
        assertNotNull(ecsShoppingCart.getTotalPrice().getValue());
        assertNotNull(ecsShoppingCart.getSubTotal().getCurrencyIso());
        assertNotNull(ecsShoppingCart.getSubTotal().getFormattedValue());
        assertNotNull(ecsShoppingCart.getSubTotal().getPriceType());
        assertNotNull(ecsShoppingCart.getSubTotal().getValue());
        assertNotNull(ecsShoppingCart.getProductDiscounts().getCurrencyIso());
        assertNotNull(ecsShoppingCart.getProductDiscounts().getFormattedValue());
        assertNotNull(ecsShoppingCart.getProductDiscounts().getPriceType());
        assertNotNull(ecsShoppingCart.getProductDiscounts().getValue());
        assertNotNull(ecsShoppingCart.getOrderDiscounts().getCurrencyIso());
        assertNotNull(ecsShoppingCart.getOrderDiscounts().getFormattedValue());
        assertNotNull(ecsShoppingCart.getOrderDiscounts().getPriceType());
        assertNotNull(ecsShoppingCart.getOrderDiscounts().getValue());
        assertEquals("DE_Pub",ecsShoppingCart.getSite());
        assertEquals(false,ecsShoppingCart.isNet());
        assertEquals(0,ecsShoppingCart.getPickupItemsQuantity());
        assertEquals(0,ecsShoppingCart.getTotalUnitCount());


        Mockito.verify(spy1).onResponse(any(ECSShoppingCart.class));

    }

    @Test
    public void assertResponseSuccessListenerNotNull() {
        assertNotNull(mockCreateECSShoppingCartRequest.getJSONSuccessResponseListener());
    }

    JSONObject getJsonObject(String jsonfileName){

        JSONObject result = null;
        InputStream in = getClass().getClassLoader().getResourceAsStream(jsonfileName);
        String jsonString = TestUtil.loadJSONFromFile(in);
        try {
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            return null;
        }
    }

}
