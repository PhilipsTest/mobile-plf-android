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
import com.philips.cdp.di.ecs.model.orders.ECSOrderHistory;
import com.philips.cdp.di.ecs.model.orders.ECSOrders;
import com.philips.cdp.di.ecs.model.payment.ECSPayment;
import com.philips.cdp.di.ecs.model.payment.PaymentMethods;
import com.philips.cdp.di.ecs.model.products.ECSProduct;
import com.philips.cdp.di.ecs.model.retailers.ECSRetailerList;
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
import static com.philips.cdp.di.ecs.error.ECSErrorEnum.ECSCtnNotProvided;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;

@RunWith(RobolectricTestRunner.class)
public class GetPaymentsTest {

    private Context mContext;


    MockECSServices mockECSServices;
    ECSServices ecsServices;


    private AppInfra appInfra;


    @Mock
    RestInterface mockRestInterface;

    MockGetPaymentsRequest mockGetPaymentsRequest;

    ECSCallback<List<ECSPayment>, Exception> ecsCallback;
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

        ecsCallback = new ECSCallback<List<ECSPayment>, Exception>() {
            @Override
            public void onResponse(List<ECSPayment> result) {

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {

            }
        };

        mockGetPaymentsRequest = new MockGetPaymentsRequest("GetPaymentsSuccess.json",ecsCallback);

    }



    @Test
    public void getPaymentSuccess() {
        mockInputValidator.setJsonFileName("GetPaymentsSuccess.json");
        mockECSServices.fetchPaymentsDetails(new ECSCallback<List<ECSPayment>, Exception>() {
            @Override
            public void onResponse(List<ECSPayment> result) {
                assertNotNull(result);
                assertNotNull(result.size()>0);
                //test passed

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertTrue(false);
                //test failed
            }
        });
    }

    //doing for kotlin order detail file

    @Test
    public void getOrderHistrorySuccess() {
        mockInputValidator.setJsonFileName("GetOrderHistorySuccess.json");
        mockECSServices.fetchOrderHistory(1, 0, new ECSCallback<ECSOrderHistory, Exception>() {
            @Override
            public void onResponse(ECSOrderHistory result) {
                assertNotNull(result);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertTrue(false);
            }
        });
    }

    @Test
    public void getOrderDetailSuccess() {
        mockInputValidator.setJsonFileName("GetOrderDetailSuccess.json");
        ECSOrders orders = new ECSOrders();
        mockECSServices.fetchOrderDetail(orders, new ECSCallback<ECSOrders, Exception>() {
            @Override
            public void onResponse(ECSOrders result) {
                assertEquals("US",result.getOrderDetail().getDeliveryAddress().getCountry().getName());
                assertNotNull(result);
                assertEquals("US",result.getOrderDetail().getDeliveryAddress().getCountry().getName());
                assertEquals("US",result.getOrderDetail().getDeliveryAddress().getRegion().getCountryIso());
                assertEquals("US-IL",result.getOrderDetail().getDeliveryAddress().getRegion().getIsocode());
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertTrue(true);
                assertEquals("Please provide valid order",error.getMessage());
                assertEquals(5060,ecsError.getErrorcode());
            }
        });
    }

    @Test
    public void getRetailerSuccess() {
        mockInputValidator.setJsonFileName("GetRetailerInfoSuccess.json");
        ECSProduct product = new ECSProduct();
        product.setCode("1234");
        mockECSServices.fetchRetailers(product, new ECSCallback<ECSRetailerList, Exception>() {
            @Override
            public void onResponse(ECSRetailerList result) {
                assertNotNull(result);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertTrue(false);
            }
        });
    }

    @Test
    public void getRetailerSuccessError() {
        mockInputValidator.setJsonFileName("GetRetailerInfoSuccess.json");
        ECSProduct product = new ECSProduct();
        mockECSServices.fetchRetailers(product, new ECSCallback<ECSRetailerList, Exception>() {
            @Override
            public void onResponse(ECSRetailerList result) {
                assertNotNull(result);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertTrue(true);
                assertEquals(ECSCtnNotProvided.toString(), ecsError.getErrorType());
            }
        });
    }


    @Test
    public void getPaymentFailure() {
        mockInputValidator.setJsonFileName("EmptyJson.json");
        mockECSServices.fetchPaymentsDetails(new ECSCallback<List<ECSPayment>, Exception>() {
            @Override
            public void onResponse(List<ECSPayment> result) {
                assertTrue(true);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertEquals(12999, ecsError);
            }
        });
    }

    @Test
    public void isValidURL() {
        System.out.println("print the URL"+mockGetPaymentsRequest.getURL());
        String excepted = StaticBlock.getBaseURL()+"pilcommercewebservices"+"/v2/"+StaticBlock.getSiteID()+"/users/current/paymentdetails?fields=FULL&lang="+StaticBlock.getLocale();
        Assert.assertEquals(excepted,mockGetPaymentsRequest.getURL());
    }


    @Test
    public void isValidGetRequest() {
        Assert.assertEquals(0,mockGetPaymentsRequest.getMethod());
    }

    @Test
    public void isValidHeader() {

        Map<String, String> expectedMap = new HashMap<String, String>();
        expectedMap.put("Authorization", "Bearer " + "acceesstoken");

        Map<String, String> actual = mockGetPaymentsRequest.getHeader();

        assertTrue(expectedMap.equals(actual));
    }

    @Test
    public void isValidParam() {
        assertNull(mockGetPaymentsRequest.getParams());
    }


    @Test
    public void verifyOnResponseError() {
        ECSCallback<List<ECSPayment>, Exception> spy1 = Mockito.spy(ecsCallback);
        mockGetPaymentsRequest = new MockGetPaymentsRequest("GetPaymentsSuccess.json",spy1);
        VolleyError volleyError = new NoConnectionError();
        mockGetPaymentsRequest.onErrorResponse(volleyError);
        Mockito.verify(spy1).onFailure(any(Exception.class),any(ECSError.class));

    }

    @Test
    public void verifyOnResponseSuccess() {

        ECSCallback<List<ECSPayment>, Exception> spy1 = Mockito.spy(ecsCallback);
        mockGetPaymentsRequest = new MockGetPaymentsRequest("GetPaymentsSuccess.json",spy1);

        JSONObject jsonObject = getJsonObject("GetPaymentsSuccess.json");

        mockGetPaymentsRequest.onResponse(jsonObject);

        Mockito.verify(spy1).onResponse(anyList());

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
        assertNotNull(mockGetPaymentsRequest.getJSONSuccessResponseListener());
    }
}
