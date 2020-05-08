package com.philips.platform.ecs.DeliveryMode;

import android.content.Context;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.philips.platform.ecs.ECSServices;
import com.philips.platform.ecs.MockECSServices;
import com.philips.platform.ecs.MockInputValidator;
import com.philips.platform.ecs.StaticBlock;
import com.philips.platform.ecs.constants.ModelConstants;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.address.ECSDeliveryMode;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.rest.RestInterface;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import java.util.Map;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

@RunWith(RobolectricTestRunner.class)
public class SetDeliveryModesRequestTest {

    private Context mContext;


    MockECSServices mockECSServices;
    ECSServices ecsServices;


    private AppInfra appInfra;

    ECSCallback<Boolean, Exception> ecsCallback;


    @Mock
    RestInterface mockRestInterface;

    MockSetDeliveryModesRequest mockSetDeliveryModesRequest;
    final static  String passedDeliveryMode = "UPS_COLLECTION_POINT";
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

        ecsCallback = new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {

            }
        };

        mockSetDeliveryModesRequest = new MockSetDeliveryModesRequest(passedDeliveryMode, ecsCallback,"SetDeliveryModeFailure.json");
    }

    @Test
    public void setDeliveryModeSuccess() {

        mockInputValidator.setJsonFileName("EmptyString.json");
        ECSDeliveryMode deliveryModes = new ECSDeliveryMode();
        deliveryModes.setCode("UPS_PARCEL");
        mockECSServices.setDeliveryMode(deliveryModes, new ECSCallback<Boolean, Exception>() {
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
    public void setDeliveryModeFailure() {

        mockInputValidator.setJsonFileName("SetDeliveryModeFailure.json");

        ECSDeliveryMode deliveryModes = new ECSDeliveryMode();
        deliveryModes.setCode("UPS_PARCEL");

        mockECSServices.setDeliveryMode(deliveryModes, new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {
                assertFalse(true);
                //test case failed
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertFalse(false);
                //test case passed

            }
        });
    }

    @Test
    public void isValidURL() {
        String excepted = StaticBlock.getBaseURL()+"pilcommercewebservices"+"/v2/"+StaticBlock.getSiteID()+"/users/current/carts/current/deliverymode?fields=FULL&lang="+StaticBlock.getLocale();
        assertEquals(excepted,mockSetDeliveryModesRequest.getURL());
    }

    @Test
    public void getMethodTest(){
        assertEquals(Request.Method.PUT,mockSetDeliveryModesRequest.getMethod());
    }

    @Test
    public  void getParamsTest(){
        Map<String, String> Actualparams = mockSetDeliveryModesRequest.getParams();
        assertTrue(Actualparams.containsKey(ModelConstants.DELIVERY_MODE_ID));
        Assert.assertEquals(Actualparams.get(ModelConstants.DELIVERY_MODE_ID), passedDeliveryMode );
    }

    @Test
    public  void getHeaderTest(){
        Map<String, String> ActualHeader = mockSetDeliveryModesRequest.getHeader();
        assertTrue(ActualHeader.containsKey("Content-Type"));
        assertTrue(ActualHeader.containsKey("Authorization"));
        Assert.assertEquals("application/x-www-form-urlencoded",ActualHeader.get("Content-Type")  );
        Assert.assertEquals("Bearer "+StaticBlock.mockAccessToken, ActualHeader.get("Authorization")  );
    }

    @Test
    public void onErrorResponseTest(){
        ECSCallback<Boolean, Exception> spy1 = Mockito.spy(ecsCallback);
        mockSetDeliveryModesRequest = new MockSetDeliveryModesRequest(passedDeliveryMode, spy1,"SetDeliveryModeFailure.json");
        VolleyError volleyError = new NoConnectionError();
        mockSetDeliveryModesRequest.onErrorResponse(volleyError);
        Mockito.verify(spy1).onFailure(any(Exception.class),any(ECSError.class));

    }

    @Test
    public void assertResponseSuccessListenerNotNull() {
        assertNotNull(mockSetDeliveryModesRequest.getStringSuccessResponseListener());
    }
}