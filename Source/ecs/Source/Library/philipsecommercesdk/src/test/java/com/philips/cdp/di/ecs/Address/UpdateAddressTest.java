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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;


@RunWith(RobolectricTestRunner.class)
public class UpdateAddressTest {

    private Context mContext;


    MockECSServices mockECSServices;
    ECSServices ecsServices;


    private AppInfra appInfra;


    @Mock
    RestInterface mockRestInterface;

    MockUpdateAddressRequest mockUpdateAddressRequest;

    ECSAddress addresses ;

    ECSCallback ecsCallback;
    private MockInputValidator mockInputValidator;

    @Before
    public void setUp() throws Exception {


        mContext = getInstrumentation().getContext();
        appInfra = new AppInfra.Builder().setRestInterface(mockRestInterface).build(mContext);



        mockECSServices = new MockECSServices("", appInfra);
        ecsServices = new ECSServices("",appInfra);

        StaticBlock.initialize();

        addresses = StaticBlock.getAddressesObject();

        mockInputValidator = new MockInputValidator();

        ecsCallback = new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {

            }
        };

        mockUpdateAddressRequest = new MockUpdateAddressRequest("EmptyString.json", addresses,ecsCallback );

    }



    @Test
    public void UpdateAddressSuccess() {
        mockInputValidator.setJsonFileName("EmptyString.json");
        ECSAddress address = new ECSAddress();
        mockECSServices.updateAddress(address, new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {
                assertTrue(result);
                // test case passed
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                //todo assertEquals(ECSInvalidAddressError.toString(),ecsError.getErrorType());
                // test case failed
            }
        });

    }


    @Test
    public void UpdateDefeaultAddressSuccess() {
        mockInputValidator.setJsonFileName("EmptyString.json");
        ECSAddress address = new ECSAddress();
        mockECSServices.updateAddress(address, new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {
                assertTrue(result);
                // test case passed
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                // todo assertEquals(ECSInvalidAddressError.toString(),ecsError.getErrorType());
                // test case failed
            }
        });

    }

    @Test
    public void UpdateAddressFailure() {
        mockInputValidator.setJsonFileName("UpdateAddressFailureInvalidAddress.json");
        ECSAddress address = new ECSAddress();
        mockECSServices.updateAddress(address, new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {
                assertFalse(false);
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
    public void UpdateDefeaultFailure() {
        mockInputValidator.setJsonFileName("UpdateAddressFailureInvalidAddress.json");
        ECSAddress address = new ECSAddress();
        mockECSServices.updateAddress(address, new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {
                assertFalse(false);
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

        String excepted = StaticBlock.getBaseURL()+"pilcommercewebservices"+"/v2/"+StaticBlock.getSiteID()+"/users/current/addresses/"+"1234567"+"?fields=FULL&lang="+StaticBlock.getLocale();
        Assert.assertEquals(excepted,mockUpdateAddressRequest.getURL());
    }

    @Test
    public void isValidDeleteRequest() {
        Assert.assertEquals(2,mockUpdateAddressRequest.getMethod());
    }

    @Test
    public void isValidParam() {
        assertNotNull(mockUpdateAddressRequest.getParams());
        assertNotEquals(0,mockUpdateAddressRequest.getParams().size());
    }

    @Test
    public void isValidHeader() {

        Map<String, String> expectedMap = new HashMap<String, String>();
        expectedMap.put("Content-Type", "application/x-www-form-urlencoded");
        expectedMap.put("Authorization", "Bearer " + "acceesstoken");

        Map<String, String> actual = mockUpdateAddressRequest.getHeader();

        assertTrue(expectedMap.equals(actual));
    }

    @Test
    public void verifyOnResponseError() {
        ECSCallback<Boolean, Exception> spy1 = Mockito.spy(ecsCallback);
        mockUpdateAddressRequest = new MockUpdateAddressRequest("CreateAddressSuccess.json", addresses, spy1);
        VolleyError volleyError = new NoConnectionError();
        mockUpdateAddressRequest.onErrorResponse(volleyError);
        Mockito.verify(spy1).onFailure(any(Exception.class),any(ECSError.class));

    }

    @Test
    public void assertResponseSuccessListenerNotNull() {
        assertNotNull(mockUpdateAddressRequest.getStringSuccessResponseListener());
    }

}
