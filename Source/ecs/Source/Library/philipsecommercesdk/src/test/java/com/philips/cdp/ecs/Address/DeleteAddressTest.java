package com.philips.cdp.ecs.Address;

import android.content.Context;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.philips.platform.ecs.ECSServices;
import com.philips.cdp.ecs.MockECSServices;
import com.philips.cdp.ecs.MockInputValidator;
import com.philips.cdp.ecs.StaticBlock;
import com.philips.platform.ecs.error.ECSError;
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
import java.util.Map;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@RunWith(RobolectricTestRunner.class)
public class DeleteAddressTest {


    private Context mContext;


    MockECSServices mockECSServices;
    ECSServices ecsServices;


    private AppInfra appInfra;


    @Mock
    RestInterface mockRestInterface;

    MockDeleteAddressRequest mockDeleteAddressRequest;

    ECSCallback ecsCallback;

    ECSAddress addresses;

    MockInputValidator mockInputValidator;

    @Before
    public void setUp() throws Exception {


        mContext = getInstrumentation().getContext();
        appInfra = new AppInfra.Builder().setRestInterface(mockRestInterface).build(mContext);



        mockECSServices = new MockECSServices("", appInfra);
        ecsServices = new ECSServices("",appInfra);

        mockInputValidator = new MockInputValidator();

        StaticBlock.initialize();
        addresses = new ECSAddress();
        addresses.setId("1234");


        ecsCallback = new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {

            }
        };
        mockDeleteAddressRequest = new MockDeleteAddressRequest("EmptyString.json", addresses,ecsCallback );

    }

    @Test
    public void deleteAddressSingleSuccess() {
        mockInputValidator.setJsonFileName("EmptyString.json");
        ECSAddress address = new ECSAddress();
        mockECSServices.deleteAddress(address, new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean addressList) {
                assertNotNull(addressList);

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
               // todo assertEquals(ECSInvalidAddressError.toString(),ecsError.getErrorType());
            }
        });

    }


    @Test
    public void deleteAddressSingleFailureInvalidBaseSite() {
        mockInputValidator.setJsonFileName("DeleteAddressFailureInvalidBaseSite.json");
        ECSAddress address = new ECSAddress();
        mockECSServices.deleteAddress(address, new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean addressList) {
                assertTrue(true);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertTrue(true);
            }
        });

    }

    @Test
    public void isValidURL() {
        String excepted = StaticBlock.getBaseURL()+"pilcommercewebservices"+"/v2/"+StaticBlock.getSiteID()+"/users/current/addresses/"+"1234"+"?fields=FULL&lang="+StaticBlock.getLocale();
        Assert.assertEquals(excepted,mockDeleteAddressRequest.getURL());
    }

    @Test
    public void isValidDeleteRequest() {
        Assert.assertEquals(3,mockDeleteAddressRequest.getMethod());
    }


    @Test
    public void isValidHeader() {

        Map<String, String> expectedMap = new HashMap<String, String>();
        expectedMap.put("Content-Type", "application/x-www-form-urlencoded");
        expectedMap.put("Authorization", "Bearer " + "acceesstoken");

        Map<String, String> actual = mockDeleteAddressRequest.getHeader();

        assertTrue(expectedMap.equals(actual));
    }

    @Test
    public void verifyOnResponseError() {
        ECSCallback<Boolean, Exception> spy1 = Mockito.spy(ecsCallback);
        mockDeleteAddressRequest = new MockDeleteAddressRequest("CreateAddressSuccess.json", addresses, spy1);
        VolleyError volleyError = new NoConnectionError();
        mockDeleteAddressRequest.onErrorResponse(volleyError);
        Mockito.verify(spy1).onFailure(any(Exception.class),any(ECSError.class));

    }

    @Test
    public void assertResponseSuccessListenerNotNull() {
        assertNotNull(mockDeleteAddressRequest.getStringSuccessResponseListener());
    }

}
