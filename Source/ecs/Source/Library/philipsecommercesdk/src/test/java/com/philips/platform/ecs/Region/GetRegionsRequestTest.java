package com.philips.platform.ecs.Region;


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
import com.philips.platform.ecs.model.region.ECSRegion;
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
import java.util.List;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@RunWith(RobolectricTestRunner.class)
public class GetRegionsRequestTest {

    private Context mContext;


    MockECSServices mockECSServices;
    ECSServices ecsServices;


    private AppInfra appInfra;


    @Mock
    RestInterface mockRestInterface;

    MockGetRegionsRequest mockGetRegionsRequest;

    ECSCallback<List<ECSRegion>, Exception> ecsCallback;
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

        ecsCallback = new ECSCallback<List<ECSRegion>, Exception>() {
            @Override
            public void onResponse(List<ECSRegion> result) {

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {

            }
        };

        mockGetRegionsRequest = new MockGetRegionsRequest("GetRegionsSuccess.json",ecsCallback, "en");
    }

    @Test
    public void getRegionRequestSuccess() {
        mockInputValidator.setJsonFileName("GetRegionsSuccess.json");
        mockECSServices.fetchRegions("en",new ECSCallback<List<ECSRegion>, Exception>() {
            @Override
            public void onResponse(List<ECSRegion> result) {
                assertNotNull(result);


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
    public void getRegionRequestFailure() {
        mockInputValidator.setJsonFileName("EmptyJson.json");
        mockECSServices.fetchRegions("en",new ECSCallback<List<ECSRegion>, Exception>() {
            @Override
            public void onResponse(List<ECSRegion> result) {
                assertTrue(true);

                //test case failed
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertTrue(false);
                //test case passed
            }
        });
    }


    @Test
    public void isValidURL() {
        System.out.println("print the URL"+mockGetRegionsRequest.getURL());

        //acc.us.pil.shop.philips.com/pilcommercewebservices/v2/metainfo/regions/null?fields=FULL&lang=en_US

        String excepted = StaticBlock.getBaseURL()+"pilcommercewebservices"+"/v2/"+"metainfo/regions/"+"en"+"?fields=FULL&lang="+StaticBlock.getLocale();
        assertEquals(excepted,mockGetRegionsRequest.getURL());
    }

    @Test
    public void isValidGetRequest() {
        assertEquals(0,mockGetRegionsRequest.getMethod());
    }

    @Test
    public void isValidParam() {
        assertNull(mockGetRegionsRequest.getParams());
    }

    @Test
    public void isValidJSONRequest() {
        assertNull(mockGetRegionsRequest.getJSONRequest());
    }


    @Test
    public void verifyOnResponseSuccess() {

        ECSCallback<List<ECSRegion>, Exception> spy1 = Mockito.spy(ecsCallback);
        mockGetRegionsRequest = new MockGetRegionsRequest("GetRegionsSuccess.json",spy1, "en");

        JSONObject jsonObject = getJsonObject("GetProductForCTN.json");

        mockGetRegionsRequest.onResponse(jsonObject);

      //  todo Mockito.verify(spy1).onResponse(anyList());

    }


    @Test
    public void verifyOnResponseError() {
        ECSCallback<List<ECSRegion>, Exception> spy1 = Mockito.spy(ecsCallback);
        mockGetRegionsRequest = new MockGetRegionsRequest("GetRegionsSuccess.json",spy1, "en");
        VolleyError volleyError = new NoConnectionError();
        mockGetRegionsRequest.onErrorResponse(volleyError);
        Mockito.verify(spy1).onFailure(any(Exception.class),any(ECSError.class));

    }


    @Test
    public void assertResponseSuccessListenerNotNull() {
        assertNotNull(mockGetRegionsRequest.getJSONSuccessResponseListener());
    }

    JSONObject getJsonObject(String jsonfileName){

        JSONObject result = null;
        InputStream in = getClass().getClassLoader().getResourceAsStream(jsonfileName);//"PRXProductAssets.json"
        String jsonString = TestUtil.loadJSONFromFile(in);
        try {
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            return null;
        }
    }
}
