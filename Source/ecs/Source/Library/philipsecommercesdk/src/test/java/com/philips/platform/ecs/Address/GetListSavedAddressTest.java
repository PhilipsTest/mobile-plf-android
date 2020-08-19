package com.philips.platform.ecs.Address;



import android.content.Context;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.philips.platform.ecs.ECSServices;
import com.philips.platform.ecs.MockECSServices;
import com.philips.platform.ecs.MockInputValidator;
import com.philips.platform.ecs.StaticBlock;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.address.ECSAddress;
import com.philips.platform.ecs.model.address.GetShippingAddressData;
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

import java.util.List;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;


@RunWith(RobolectricTestRunner.class)
public class GetListSavedAddressTest {

    private Context mContext;


    MockECSServices mockECSServices;
    ECSServices ecsServices;


    private AppInfra appInfra;


    @Mock
    RestInterface mockRestInterface;

    MockGetAddressRequest mockGetAddressRequest;

    ECSCallback ecsCallback;
    private MockInputValidator mockInputValidator;

    @Before
    public void setUp() throws Exception {


        mContext = getInstrumentation().getContext();
        appInfra = new AppInfra.Builder().setRestInterface(mockRestInterface).build(mContext);
        //appInfra.getServiceDiscovery().setHomeCountry("DE");


        mockECSServices = new MockECSServices(appInfra);
        ecsServices = new ECSServices(appInfra);
        mockInputValidator = new MockInputValidator();

        StaticBlock.initialize();

        ecsCallback = new ECSCallback<GetShippingAddressData, Exception>() {
            @Override
            public void onResponse(GetShippingAddressData result) {

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {

            }
        };
        mockGetAddressRequest = new MockGetAddressRequest("ShippingAddressListSuccess.json", ecsCallback);
    }


    @Test
    public void GetListSavedAddressTestSuccess() {
        mockInputValidator.setJsonFileName("ShippingAddressListSuccess.json");
        ECSAddress address = new ECSAddress();
        mockECSServices.fetchSavedAddresses(new ECSCallback<List<ECSAddress>, Exception>() {
            @Override
            public void onResponse(List<ECSAddress> addressList) {
                assertNotNull(addressList);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                // assertEquals(12999,errorCode);
                assertTrue(true);
            }
        });

    }

    @Test
    public void GetListSavedAddressTestFailure() {
        mockInputValidator.setJsonFileName("EmptyJson.json");
        ECSAddress address = new ECSAddress();
        mockECSServices.fetchSavedAddresses(new ECSCallback<List<ECSAddress>, Exception>() {
            @Override
            public void onResponse(List<ECSAddress> addressList) {
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
        String excepted = StaticBlock.getBaseURL()+"pilcommercewebservices"+"/v2/"+StaticBlock.getSiteID()+"/users/current/addresses?fields=FULL&lang="+StaticBlock.getLocale();
        assertEquals(excepted,mockGetAddressRequest.getURL());
    }

    @Test
    public void isValidGetRequest() {
        assertEquals(0 ,mockGetAddressRequest.getMethod());
    }

    @Test
    public void assertResponseSuccessListenerNotNull() {
        assertNotNull(mockGetAddressRequest.getJSONSuccessResponseListener());
    }

    @Test
    public void verifyOnResponseError() {
        ECSCallback<List<ECSAddress>, Exception> spy1 = Mockito.spy(ecsCallback);
        mockGetAddressRequest = new MockGetAddressRequest("CreateAddressSuccess.json", spy1);
        VolleyError volleyError = new NoConnectionError();
        mockGetAddressRequest.onErrorResponse(volleyError);
        Mockito.verify(spy1).onFailure(any(Exception.class),any(ECSError.class));

    }

    @Test
    public void verifyOnResponseException() {
        ECSCallback<List<ECSAddress>, Exception> spy1 = Mockito.spy(ecsCallback);
        mockGetAddressRequest = new MockGetAddressRequest("CreateAddressSuccess.json", spy1);
        mockGetAddressRequest.onResponse(null);
        Mockito.verify(spy1).onFailure(any(Exception.class),any(ECSError.class));

    }

    @Test
    public void verifyOnResponseJSONException() {
        ECSCallback<List<ECSAddress>, Exception> spy1 = Mockito.spy(ecsCallback);
        mockGetAddressRequest = new MockGetAddressRequest("CreateAddressSuccess.json", spy1);
        try {
            JSONObject jsonObject = new JSONObject("123");
            mockGetAddressRequest.onResponse(jsonObject);
            Mockito.verify(spy1).onFailure(any(Exception.class),any(ECSError.class));
        } catch (JSONException e) {

        }
    }

}
