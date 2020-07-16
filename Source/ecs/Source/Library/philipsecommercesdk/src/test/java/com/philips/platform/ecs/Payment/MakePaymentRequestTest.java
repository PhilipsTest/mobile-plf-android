package com.philips.platform.ecs.Payment;


import android.content.Context;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.philips.platform.ecs.ECSServices;
import com.philips.platform.ecs.MockECSServices;
import com.philips.platform.ecs.MockInputValidator;
import com.philips.platform.ecs.StaticBlock;
import com.philips.platform.ecs.TestUtil;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.address.ECSAddress;
import com.philips.platform.ecs.model.orders.ECSOrderDetail;
import com.philips.platform.ecs.model.payment.ECSPaymentProvider;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.rest.RestInterface;

import org.json.JSONException;
import org.json.JSONObject;
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
import static com.philips.platform.ecs.error.ECSErrorEnum.ECSorderIdNil;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@RunWith(RobolectricTestRunner.class)

public class MakePaymentRequestTest {

    private Context mContext;


    MockECSServices mockECSServices;
    ECSServices ecsServices;


    private AppInfra appInfra;

    MockMakePaymentRequest mockMakePaymentRequest;

    ECSOrderDetail orderDetail;
    ECSAddress addresses;

    ECSCallback<ECSPaymentProvider, Exception> ecsCallback;


    @Mock
    RestInterface mockRestInterface;
    private MockInputValidator mockInputValidator;

    @Before
    public void setUp() throws Exception {


        mContext = getInstrumentation().getContext();
        appInfra = new AppInfra.Builder().setRestInterface(mockRestInterface).build(mContext);
        appInfra.getServiceDiscovery().setHomeCountry("DE");


        mockECSServices = new MockECSServices(appInfra);
        ecsServices = new ECSServices(appInfra);

        StaticBlock.initialize();

        mockInputValidator = new MockInputValidator();
        addresses = StaticBlock.getAddressesObject();
        ecsCallback = new ECSCallback<ECSPaymentProvider, Exception>() {
            @Override
            public void onResponse(ECSPaymentProvider result) {

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {

            }
        };
        orderDetail = new ECSOrderDetail();
        orderDetail.setCode("123");
        mockMakePaymentRequest = new MockMakePaymentRequest("MakePaymentSuccess.json",orderDetail,addresses,ecsCallback);

    }

    @Test
    public void makePaymentSuccess() {
        mockInputValidator.setJsonFileName("MakePaymentSuccess.json");
        ECSOrderDetail orderDetail = new ECSOrderDetail();
        ECSAddress addresses = new ECSAddress();
        mockECSServices.makePayment(orderDetail,addresses,new ECSCallback<ECSPaymentProvider, Exception>() {
            @Override
            public void onResponse(ECSPaymentProvider result) {
                assertNotNull(result);
                assertNotNull(result.getWorldpayUrl());
                //test passed

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertEquals(ECSorderIdNil.toString(),ecsError.getErrorType());
                //test failed
            }
        });
    }

    @Test
    public void isValidURL() {
        System.out.println("print the URL"+mockMakePaymentRequest.getURL());
        String excepted = StaticBlock.getBaseURL()+"pilcommercewebservices"+"/v2/"+StaticBlock.getSiteID()+"/users/current/orders/123/pay";
        assertEquals(excepted,mockMakePaymentRequest.getURL());
    }

    @Test
    public void isValidPostRequest() {
        assertEquals(1,mockMakePaymentRequest.getMethod());
    }

    @Test
    public void isValidHeader() {

        Map<String, String> expectedMap = new HashMap<String, String>();
        expectedMap.put("Content-Type", "application/x-www-form-urlencoded");
        expectedMap.put("Authorization", "Bearer " + "acceesstoken");

        Map<String, String> actual = mockMakePaymentRequest.getHeader();

        assertTrue(expectedMap.equals(actual));
    }

    @Test
    public void isValidParam() {
        assertNotNull(mockMakePaymentRequest.getParams());
        assertNotEquals(0,mockMakePaymentRequest.getParams().size());
    }


    @Test
    public void verifyOnResponseError() {
        ECSCallback<ECSPaymentProvider, Exception> spy1 = Mockito.spy(ecsCallback);
        mockMakePaymentRequest = new MockMakePaymentRequest("MakePaymentSuccess.json", orderDetail, addresses, spy1);
        VolleyError volleyError = new NoConnectionError();
        mockMakePaymentRequest.onErrorResponse(volleyError);
        Mockito.verify(spy1).onFailure(any(Exception.class),any(ECSError.class));

    }

    @Test
    public void verifyOnResponseSuccess() {

        ECSCallback<ECSPaymentProvider, Exception> spy1 = Mockito.spy(ecsCallback);
        mockMakePaymentRequest = new MockMakePaymentRequest("MakePaymentSuccess.json",orderDetail, addresses,spy1);

        JSONObject jsonObject = getJsonObject("MakePaymentSuccess.json");

        mockMakePaymentRequest.onResponse(String.valueOf(jsonObject));

        Mockito.verify(spy1).onResponse(any(ECSPaymentProvider.class));

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
        assertNotNull(mockMakePaymentRequest.getStringSuccessResponseListener());
    }

}
