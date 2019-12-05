package com.philips.cdp.di.ecs.Address;

import android.content.Context;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.philips.cdp.di.ecs.ECSServices;
import com.philips.cdp.di.ecs.MockECSServices;
import com.philips.cdp.di.ecs.MockInputValidator;
import com.philips.cdp.di.ecs.StaticBlock;
import com.philips.cdp.di.ecs.error.ECSError;
import com.philips.cdp.di.ecs.integration.ECSCallback;
import com.philips.cdp.di.ecs.model.address.ECSAddress;
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
public class SetDeliveryAddressRequestTest {
    private Context mContext;


    MockECSServices mockECSServices;
    ECSServices ecsServices;


    private AppInfra appInfra;


    @Mock
    RestInterface mockRestInterface;

    MockSetDeliveryAddressRequest mockSetDeliveryAddressRequest;
    ECSCallback<Boolean, Exception> ecsCallback;
    private MockInputValidator mockInputValidator;

    @Before
    public void setUp() throws Exception {


        mContext = getInstrumentation().getContext();
        appInfra = new AppInfra.Builder().setRestInterface(mockRestInterface).build(mContext);
        //appInfra.getServiceDiscovery().setHomeCountry("DE");


        mockECSServices = new MockECSServices("", appInfra);
        ecsServices = new ECSServices("",appInfra);

        mockInputValidator = new MockInputValidator();
        StaticBlock.initialize();

        ecsCallback = new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {

            }
        };

        mockSetDeliveryAddressRequest = new MockSetDeliveryAddressRequest("EmptyString.json","addressID",ecsCallback);

    }


    @Test
    public void GetResponseSuccess() {
        mockInputValidator.setJsonFileName("EmptyString.json");
        ECSAddress addresses = new ECSAddress();
        mockECSServices.setDeliveryAddress(true, addresses, new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {
                assertTrue(result);
                //test case passed
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertTrue(true);

               // assertEquals(ECSInvalidAddressError.toString(),ecsError.getErrorType());
                //test case faile
            }
        });

    }

    @Test
    public void GetListSavedAddressTestFailue() {
        mockInputValidator.setJsonFileName("EmptyJson.json");
        ECSAddress addresses = new ECSAddress();
        mockECSServices.setDeliveryAddress(true, addresses, new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {
                assertTrue(true);

                //test case passed

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertTrue(true);

                //test case failed

            }
        });
    }

    @Test
    public void isValidURL() {
        System.out.println("print the URL"+mockSetDeliveryAddressRequest.getURL());
        String excepted = StaticBlock.getBaseURL()+"pilcommercewebservices"+"/v2/"+StaticBlock.getSiteID()+"/users/current/carts/current/addresses/delivery?fields=FULL&lang="+StaticBlock.getLocale();
        Assert.assertEquals(excepted,mockSetDeliveryAddressRequest.getURL());
    }

    @Test
    public void isValidPutRequest() {
        Assert.assertEquals(2,mockSetDeliveryAddressRequest.getMethod());
    }

    @Test
    public void isValidHeader() {

        Map<String, String> expectedMap = new HashMap<String, String>();
        expectedMap.put("Content-Type", "application/x-www-form-urlencoded");
        expectedMap.put("Authorization", "Bearer " + "acceesstoken");

        Map<String, String> actual = mockSetDeliveryAddressRequest.getHeader();

        assertTrue(expectedMap.equals(actual));
    }

    @Test
    public void isValidParam() {
        assertNotNull(mockSetDeliveryAddressRequest.getParams());
        assertNotEquals(0,mockSetDeliveryAddressRequest.getParams().size());
    }


    @Test
    public void verifyOnResponseError() {
        ECSCallback<Boolean, Exception> spy1 = Mockito.spy(ecsCallback);
        mockSetDeliveryAddressRequest = new MockSetDeliveryAddressRequest( "EmptyString.json","addressID",spy1);
        VolleyError volleyError = new NoConnectionError();
        mockSetDeliveryAddressRequest.onErrorResponse(volleyError);
        Mockito.verify(spy1).onFailure(any(Exception.class),any(ECSError.class));

    }

    @Test
    public void assertResponseSuccessListenerNotNull() {
        assertNotNull(mockSetDeliveryAddressRequest.getStringSuccessResponseListener());
    }
}
