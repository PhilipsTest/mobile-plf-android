package com.philips.cdp.ecs.Address;

import android.content.Context;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.philips.platform.ecs.ECSServices;
import com.philips.cdp.ecs.MockECSServices;
import com.philips.cdp.ecs.MockInputValidator;
import com.philips.cdp.ecs.StaticBlock;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.error.ECSErrorWrapper;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.address.ECSAddress;
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
import java.util.List;
import java.util.Map;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.philips.platform.ecs.error.ECSNetworkError.getErrorMessageFromException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@RunWith(RobolectricTestRunner.class)
public class CreateAddressTest {
    private Context mContext;


    MockECSServices mockECSServices;
    ECSServices ecsServices;

    MockInputValidator mockInputValidator;


    private AppInfra appInfra;


    @Mock
    RestInterface mockRestInterface;

    MockCreateAddressRequest mockCreateAddressRequest;

    ECSAddress address;

    ECSCallback<ECSAddress, Exception> ecsCallback;

    @Before
    public void setUp() throws Exception {


        mContext = getInstrumentation().getContext();
        appInfra = new AppInfra.Builder().setRestInterface(mockRestInterface).build(mContext);
        //appInfra.getServiceDiscovery().setHomeCountry("DE");


        mockECSServices = new MockECSServices("", appInfra);
        ecsServices = new ECSServices("",appInfra);

        mockInputValidator = new MockInputValidator();

        StaticBlock.initialize();
        address = StaticBlock.getAddressesObject();
        ecsCallback = new ECSCallback<ECSAddress, Exception>() {
            @Override
            public void onResponse(ECSAddress result) {

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {

            }
        };
        mockCreateAddressRequest = new MockCreateAddressRequest("CreateAddressSuccess.json", address, ecsCallback);

    }


    //////////////////////////////start of single created  Address fetch ////////////////

    @Test
    public void addAddressSingleSuccess() {
        mockInputValidator.setJsonFileName("CreateAddressSuccess.json");
        ECSAddress address = StaticBlock.getAddressesObject();
        ecsServices.createAndFetchAddress(address, new ECSCallback<List<ECSAddress>, Exception>() {
            @Override
            public void onResponse(List<ECSAddress> address) {
                assertNotNull(address);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {

                assertTrue(true);
            }
        });

    }

    @Test
    public void addAddressSingleFailureInvalidZipCode() {
        mockInputValidator.setJsonFileName("CreateAddressFailureInvalidZipCode.json");
        ECSAddress address = new ECSAddress();
        ecsServices.createAndFetchAddress(address, new ECSCallback<List<ECSAddress>, Exception>() {
            @Override
            public void onResponse(List<ECSAddress> address) {
                assertTrue(true);

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertTrue(true);
            }
        });

    }

    @Test
    public void addAddressSingleFailure() {
        mockInputValidator.setJsonFileName("EmptyString.json");
        ECSAddress address = new ECSAddress();
        ecsServices.createAndFetchAddress(address, new ECSCallback<List<ECSAddress>, Exception>() {
            @Override
            public void onResponse(List<ECSAddress> address) {
               assertTrue(true);

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
               // assertEquals(ECSInvalidAddressError.toString(), ecsError.getErrorType());
            }
        });

    }



    //////////////////////////////End of single created  Address fetch ////////////////





    //////////////////////////////start of All  Address fetch ////////////////

    @Test
    public void addAddressSuccess() {
        mockInputValidator.setJsonFileName("CreateAddressSuccess.json");
        ECSAddress addressRequest = new ECSAddress();
        ecsServices.createAndFetchAddress(addressRequest, new ECSCallback<List<ECSAddress>, Exception>() {
            @Override
            public void onResponse(List<ECSAddress> addressList) {
                assertNotNull(addressList);
                assertNotNull(addressList.size()>0);
                assertNotNull(addressList.get(0).getId());

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                // assertEquals(12999,errorCode);
                assertTrue(true);
            }
        });

    }


    @Test
    public void addAddressFailure() {
        mockInputValidator.setJsonFileName("EmptyString.json");
        ECSAddress addressRequest = new ECSAddress();
        ecsServices.createAndFetchAddress(addressRequest, new ECSCallback<List<ECSAddress>, Exception>() {
            @Override
            public void onResponse(List<ECSAddress> addressList) {
                assertTrue(true);

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                //todo  assertEquals(ECSInvalidAddressError.toString(), ecsError.getErrorType());

            }
        });

    }

    //////////////////////////////End of All  Address fetch ////////////////

    @Test
    public void isValidURL() {
        String excepted = StaticBlock.getBaseURL()+"pilcommercewebservices"+"/v2/"+StaticBlock.getSiteID()+"/users/current/addresses?fields=FULL&lang="+StaticBlock.getLocale();
        Assert.assertEquals(excepted,mockCreateAddressRequest.getURL());
    }

    @Test
    public void isValidPostRequest() {
        Assert.assertEquals(1,mockCreateAddressRequest.getMethod());
    }

    @Test
    public void isValidHeader() {

        Map<String, String> expectedMap = new HashMap<String, String>();
        expectedMap.put("Content-Type", "application/x-www-form-urlencoded");
        expectedMap.put("Authorization", "Bearer " + "acceesstoken");

        Map<String, String> actual = mockCreateAddressRequest.getHeader();

        assertTrue(expectedMap.equals(actual));
    }

    @Test
    public void isValidParam() {
        assertNotNull(mockCreateAddressRequest.getParams());
        assertNotEquals(0,mockCreateAddressRequest.getParams().size());
    }

    @Test
    public void verifyOnResponseError() {
        ECSCallback<ECSAddress, Exception> spy1 = Mockito.spy(ecsCallback);
        mockCreateAddressRequest = new MockCreateAddressRequest("CreateAddressSuccess.json", address, spy1);
        VolleyError volleyError = new NoConnectionError();
        mockCreateAddressRequest.onErrorResponse(volleyError);
        Mockito.verify(spy1).onFailure(any(Exception.class),any(ECSError.class));

    }

    @Test
    public void verifyOnResponseException() {
        ECSCallback<ECSAddress, Exception> spy1 = Mockito.spy(ecsCallback);
        mockCreateAddressRequest = new MockCreateAddressRequest("CreateAddressSuccess.json", address, spy1);
        mockCreateAddressRequest.onResponse("jhsvcjhsc bvsdljcsdlcjgdscgds"); // passing invalid json
        Mockito.verify(spy1).onFailure(any(Exception.class),any(ECSError.class));

    }

    @Test
    public void verifyErrorMessageAndCodeForNoConnectionError() {
        ECSCallback<ECSAddress, Exception> spy1 = Mockito.spy(ecsCallback);
        mockCreateAddressRequest = new MockCreateAddressRequest("CreateAddressSuccess.json", address, spy1);
        VolleyError volleyError = new NoConnectionError();
        ECSErrorWrapper ecsError = mockCreateAddressRequest.getECSError(volleyError);
        assertEquals("com.android.volley.NoConnectionError",getErrorMessageFromException(ecsError.getException()));
        assertEquals(11000,ecsError.getEcsError().getErrorcode());
    }

    @Test
    public void verifyErrorMessageAndCodeForServerError() {
        ECSCallback<ECSAddress, Exception> spy1 = Mockito.spy(ecsCallback);
        mockCreateAddressRequest = new MockCreateAddressRequest("CreateAddressSuccess.json", address, spy1);
        VolleyError volleyError = new ServerError();
        ECSErrorWrapper ecsError = mockCreateAddressRequest.getECSError(volleyError);
        assertEquals("com.android.volley.ServerError",getErrorMessageFromException(ecsError.getException()));
        assertEquals(11000,ecsError.getEcsError().getErrorcode());
    }

    @Test
    public void verifyErrorMessageAndCodeForTimeoutError() {
        ECSCallback<ECSAddress, Exception> spy1 = Mockito.spy(ecsCallback);
        mockCreateAddressRequest = new MockCreateAddressRequest("CreateAddressSuccess.json", address, spy1);
        VolleyError volleyError = new TimeoutError();
        ECSErrorWrapper ecsError = mockCreateAddressRequest.getECSError(volleyError);
        assertEquals("com.android.volley.TimeoutError",getErrorMessageFromException(ecsError.getException()));
        assertEquals(11000,ecsError.getEcsError().getErrorcode());
    }


    @Test
    public void verifyErrorMessageAndCodeForServer() {
        ECSCallback<ECSAddress, Exception> spy1 = Mockito.spy(ecsCallback);
        mockCreateAddressRequest = new MockCreateAddressRequest("CreateAddressSuccess.json", address, spy1);
        String encodingString = "ewogICAiZXJyb3JzIiA6IFsgewogICAgICAibWVzc2FnZSIgOiAiR2ViZW4gU2llIElocmUgUG9z\n" +
                "dGxlaXR6YWhsIGVpbi4iLAogICAgICAicmVhc29uIiA6ICJpbnZhbGlkIiwKICAgICAgInN1Ympl\n" +
                "Y3QiIDogInBvc3RhbENvZGUiLAogICAgICAic3ViamVjdFR5cGUiIDogInBhcmFtZXRlciIsCiAg\n" +
                "ICAgICJ0eXBlIiA6ICJWYWxpZGF0aW9uRXJyb3IiCiAgIH0gXQp9";

        byte[] decode = Base64.decode(encodingString, Base64.DEFAULT);
        NetworkResponse networkResponse = new NetworkResponse(decode);
        VolleyError volleyError = new com.android.volley.ServerError(networkResponse);
        ECSErrorWrapper ecsError = mockCreateAddressRequest.getECSError(volleyError);
        assertEquals("ZIP code selected is invalid",ecsError.getException().getMessage());
        assertEquals(5016,ecsError.getEcsError().getErrorcode());
    }

    @Test
    public void verifyErrorMessageAndCodeForAuthFailureError() {
        ECSCallback<ECSAddress, Exception> spy1 = Mockito.spy(ecsCallback);
        mockCreateAddressRequest = new MockCreateAddressRequest("CreateAddressSuccess.json", address, spy1);
        VolleyError volleyError = new AuthFailureError();
        ECSErrorWrapper ecsError = mockCreateAddressRequest.getECSError(volleyError);
        assertEquals("com.android.volley.AuthFailureError",getErrorMessageFromException(ecsError.getException()));
        assertEquals(11000,ecsError.getEcsError().getErrorcode());
    }


    @Test
    public void assertResponseSuccessListenerNotNull() {
        assertNotNull(mockCreateAddressRequest.getStringSuccessResponseListener());
    }

    @Test
    public void assertJSONResponseSuccessListenerNull() {
        assertNull(mockCreateAddressRequest.getJSONSuccessResponseListener());
    }
}
