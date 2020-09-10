package com.philips.platform.ecs.DeliveryMode;

import android.content.Context;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.rest.RestInterface;
import com.philips.platform.ecs.ECSServices;
import com.philips.platform.ecs.MockECSServices;
import com.philips.platform.ecs.MockInputValidator;
import com.philips.platform.ecs.StaticBlock;
import com.philips.platform.ecs.TestUtil;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.address.ECSDeliveryMode;
import com.philips.platform.ecs.model.address.GetDeliveryModes;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import java.io.InputStream;
import java.util.List;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@RunWith(RobolectricTestRunner.class)
public class GetDeliveryModesRequestTest {

    private Context mContext;


    MockECSServices mockECSServices;
    ECSServices ecsServices;


    private AppInfra appInfra;


    @Mock
    RestInterface mockRestInterface;

    MockDeliveryModesRequest mockDeliveryModesRequest;

    ECSCallback<List<ECSDeliveryMode>, Exception> ecsCallback;
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

        ecsCallback = new ECSCallback<List<ECSDeliveryMode>, Exception>() {
            @Override
            public void onResponse(List<ECSDeliveryMode> result) {

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {

            }
        };

        mockDeliveryModesRequest = new MockDeliveryModesRequest(ecsCallback,"deliverymodes.json");
    }


    @Test
    public void getDeliveryModesSuccess() {
        mockInputValidator.setJsonFileName("deliverymodes.json");
        mockECSServices.fetchDeliveryModes(new ECSCallback<List<ECSDeliveryMode>, Exception>() {
            @Override
            public void onResponse(List<ECSDeliveryMode> result) {
                assertNotNull(result);
                assertNotEquals(0,result.size());
                //  test case passed
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertTrue(false);
                //  test case failed
            }
        });
    }

    @Test
    public void getDeliveryModesFailureEmpty() {
        mockInputValidator.setJsonFileName("EmptyJson.json");
        mockECSServices.fetchDeliveryModes(new ECSCallback<List<ECSDeliveryMode>, Exception>() {
            @Override
            public void onResponse(List<ECSDeliveryMode> result) {
                assertNull(result);
                //  test case failed
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertTrue(false);
                //  test case passed
            }
        });
    }

    @Test
    public void isValidURL() {
        String excepted = StaticBlock.getBaseURL()+"pilcommercewebservices"+"/v2/"+StaticBlock.getSiteID()+"/users/current/carts/current/deliverymodes?fields=FULL&lang="+StaticBlock.getLocale();
        assertEquals(excepted,mockDeliveryModesRequest.getURL());
    }

    @Test
    public void isValidGetRequest() {
        assertEquals(0,mockDeliveryModesRequest.getMethod());
    }


    @Test
    public void verifyOnResponseError() {
        ECSCallback<List<ECSDeliveryMode>, Exception> spy1 = Mockito.spy(ecsCallback);
        mockDeliveryModesRequest = new MockDeliveryModesRequest(spy1,"deliverymodes.json");
        VolleyError volleyError = new NoConnectionError();
        mockDeliveryModesRequest.onErrorResponse(volleyError);
        Mockito.verify(spy1).onFailure(any(Exception.class),any(ECSError.class));

    }

    @Test
    public void verifyOnResponseSuccess() {

        ECSCallback<List<ECSDeliveryMode>, Exception> spy1 = Mockito.spy(ecsCallback);
        mockDeliveryModesRequest = new MockDeliveryModesRequest(spy1,"deliverymodes.json");

        JSONObject jsonObject = getJsonObject("deliverymodes.json");

        mockDeliveryModesRequest.onResponse(jsonObject);

        Mockito.verify(spy1).onResponse(any(List.class));

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
    public void testToRemovePickupPointDeliveryModes() {
        JSONObject jsonObject = getJsonObject("deliverymodes.json");
        GetDeliveryModes getDeliveryModes = new Gson().fromJson(jsonObject.toString(),
                    GetDeliveryModes.class);
        List<ECSDeliveryMode> deliveryModes = getDeliveryModes.getDeliveryModes();
        mockDeliveryModesRequest.removePickupPoints(deliveryModes);
        int alteredSizeOfDeliveryModes = deliveryModes.size();
        assertEquals(2,alteredSizeOfDeliveryModes);

    }

    @Test
    public void assertResponseSuccessListenerNotNull() {
        assertNotNull(mockDeliveryModesRequest.getJSONSuccessResponseListener());
    }
}