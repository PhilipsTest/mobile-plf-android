package com.philips.cdp.di.ecs.Payment;

import android.content.Context;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.philips.cdp.di.ecs.ECSServices;
import com.philips.cdp.di.ecs.MockECSServices;
import com.philips.cdp.di.ecs.MockInputValidator;
import com.philips.cdp.di.ecs.StaticBlock;
import com.philips.cdp.di.ecs.TestUtil;
import com.philips.cdp.di.ecs.error.ECSError;
import com.philips.cdp.di.ecs.integration.ECSCallback;
import com.philips.cdp.di.ecs.model.orders.ECSOrderDetail;
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
import java.util.Map;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@RunWith(RobolectricTestRunner.class)
public class PlaceOrderTest {

    private Context mContext;


    MockECSServices mockECSServices;
    ECSServices ecsServices;


    private AppInfra appInfra;

    MockPlaceOrderRequest mockPlaceOrderRequest;

    ECSCallback<ECSOrderDetail, Exception> ecsCallback;


    @Mock
    RestInterface mockRestInterface;
    private MockInputValidator mockInputValidator;

    @Before
    public void setUp() throws Exception {


        mContext = getInstrumentation().getContext();
        appInfra = new AppInfra.Builder().setRestInterface(mockRestInterface).build(mContext);
        appInfra.getServiceDiscovery().setHomeCountry("DE");


        mockECSServices = new MockECSServices("", appInfra);
        ecsServices = new ECSServices("",appInfra);

        StaticBlock.initialize();

        mockInputValidator = new MockInputValidator();
        ecsCallback = new ECSCallback<ECSOrderDetail, Exception>() {
            @Override
            public void onResponse(ECSOrderDetail result) {

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {

            }
        };
        mockPlaceOrderRequest = new MockPlaceOrderRequest("SubmitOrderSuccess.json","", ecsCallback);
    }

    @Test
    public void placeOrderSuccess() {
        mockInputValidator.setJsonFileName("SubmitOrderSuccess.json");
        mockECSServices.submitOrder("123",new ECSCallback<ECSOrderDetail, Exception>() {
            @Override
            public void onResponse(ECSOrderDetail result) {
                assertNotNull(result);
                //test passed

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertTrue(false);
                //test failed
            }
        });
    }

    @Test
    public void isValidURL() {
        System.out.println("print the URL"+mockPlaceOrderRequest.getURL());
        String excepted = StaticBlock.getBaseURL()+"pilcommercewebservices"+"/v2/"+StaticBlock.getSiteID()+"/users/current/orders";
        Assert.assertEquals(excepted,mockPlaceOrderRequest.getURL());
    }

    @Test
    public void isValidPostRequest() {
        Assert.assertEquals(1,mockPlaceOrderRequest.getMethod());
    }

    @Test
    public void isValidHeader() {

        Map<String, String> expectedMap = new HashMap<String, String>();
        expectedMap.put("Authorization", "Bearer " + "acceesstoken");

        Map<String, String> actual = mockPlaceOrderRequest.getHeader();

        assertTrue(expectedMap.equals(actual));
    }

    @Test
    public void isValidParam() {
        assertNotNull(mockPlaceOrderRequest.getParams());
        assertNotEquals(0,mockPlaceOrderRequest.getParams().size());
    }


    @Test
    public void verifyOnResponseError() {
        ECSCallback<ECSOrderDetail, Exception> spy1 = Mockito.spy(ecsCallback);
        mockPlaceOrderRequest = new MockPlaceOrderRequest("SubmitOrderSuccess.json","", spy1);
        VolleyError volleyError = new NoConnectionError();
        mockPlaceOrderRequest.onErrorResponse(volleyError);
        Mockito.verify(spy1).onFailure(any(Exception.class),any(ECSError.class));

    }

    @Test
    public void verifyOnResponseSuccess() {

        ECSCallback<ECSOrderDetail, Exception> spy1 = Mockito.spy(ecsCallback);
        mockPlaceOrderRequest = new MockPlaceOrderRequest("SubmitOrderSuccess.json","",spy1);

        JSONObject jsonObject = getJsonObject("SubmitOrderSuccess.json");

        mockPlaceOrderRequest.onResponse(String.valueOf(jsonObject));

        Mockito.verify(spy1).onResponse(any(ECSOrderDetail.class));

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

    @Test
    public void assertResponseSuccessListenerNotNull() {
        assertNotNull(mockPlaceOrderRequest.getStringSuccessResponseListener());
    }
}
