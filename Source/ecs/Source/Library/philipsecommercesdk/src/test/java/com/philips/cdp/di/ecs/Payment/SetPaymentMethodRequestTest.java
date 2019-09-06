package com.philips.cdp.di.ecs.Payment;

import android.content.Context;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.philips.cdp.di.ecs.ECSServices;
import com.philips.cdp.di.ecs.MockECSServices;
import com.philips.cdp.di.ecs.StaticBlock;
import com.philips.cdp.di.ecs.error.ECSError;
import com.philips.cdp.di.ecs.integration.ECSCallback;
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
import static org.mockito.ArgumentMatchers.anyInt;

@RunWith(RobolectricTestRunner.class)
public class SetPaymentMethodRequestTest {

    private Context mContext;


    MockECSServices mockECSServices;
    ECSServices ecsServices;


    private AppInfra appInfra;


    @Mock
    RestInterface mockRestInterface;

    MockSetPaymentMethodRequest mockSetPaymentMethodRequest;

    ECSCallback<Boolean, Exception> ecsCallback;

    @Before
    public void setUp() throws Exception {

        mContext = getInstrumentation().getContext();
        appInfra = new AppInfra.Builder().setRestInterface(mockRestInterface).build(mContext);
        appInfra.getServiceDiscovery().setHomeCountry("DE");

        mockECSServices = new MockECSServices("", appInfra);
        ecsServices = new ECSServices("",appInfra);

        StaticBlock.initialize();

        ecsCallback = new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {

            }
        };
        mockSetPaymentMethodRequest = new MockSetPaymentMethodRequest("8960990117930",ecsCallback,"EmptyString.json");
    }

    @Test
    public void setPaymentMethodSuccess() {

        mockECSServices.setJsonFileName("EmptyString.json");
        mockECSServices.setPaymentMethod("8960990117930", new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {
                assertTrue(result);
                //test case passed
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertTrue(false);
                //test case failed
            }
        });
    }

    @Test
    public void setPaymentMethodFailure() {

        mockECSServices.setJsonFileName("SetPaymentFailure.json");
        mockECSServices.setPaymentMethod("8960990117930", new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {
                assertTrue(false);
                //test case failed
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertTrue(true);
                //test case passed

            }
        });
    }

    @Test
    public void isValidURL() {
        System.out.println("print the URL"+mockSetPaymentMethodRequest.getURL());
        String excepted = StaticBlock.getBaseURL()+"pilcommercewebservices"+"/v2/"+StaticBlock.getSiteID()+"/users/current/carts/current/paymentdetails?fields=FULL&lang="+StaticBlock.getLocale();
        Assert.assertEquals(excepted,mockSetPaymentMethodRequest.getURL());
    }

    @Test
    public void isValidPutRequest() {
        Assert.assertEquals(2,mockSetPaymentMethodRequest.getMethod());
    }

    @Test
    public void isValidHeader() {

        Map<String, String> expectedMap = new HashMap<String, String>();
        expectedMap.put("Content-Type", "application/x-www-form-urlencoded");
        expectedMap.put("Authorization", "Bearer " + "acceesstoken");

        Map<String, String> actual = mockSetPaymentMethodRequest.getHeader();

        assertTrue(expectedMap.equals(actual));
    }

    @Test
    public void isValidParam() {
        assertNotNull(mockSetPaymentMethodRequest.getParams());
        assertNotEquals(0,mockSetPaymentMethodRequest.getParams().size());
    }


    @Test
    public void verifyOnResponseError() {
        ECSCallback<Boolean, Exception> spy1 = Mockito.spy(ecsCallback);
        mockSetPaymentMethodRequest = new MockSetPaymentMethodRequest("8960990117930", spy1, "EmptyString.json");
        VolleyError volleyError = new NoConnectionError();
        mockSetPaymentMethodRequest.onErrorResponse(volleyError);
        Mockito.verify(spy1).onFailure(any(Exception.class),any(ECSError.class));

    }

    @Test
    public void assertResponseSuccessListenerNotNull() {
        assertNotNull(mockSetPaymentMethodRequest.getStringSuccessResponseListener());
    }
}
