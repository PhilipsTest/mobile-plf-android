package com.philips.cdp.ecs.Config;

import android.content.Context;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.philips.platform.ecs.ECSServices;
import com.philips.cdp.ecs.MockECSServices;
import com.philips.cdp.ecs.MockInputValidator;
import com.philips.cdp.ecs.StaticBlock;
import com.philips.cdp.ecs.TestUtil;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.config.ECSConfig;
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

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@RunWith(RobolectricTestRunner.class)
public class GetConfigurationRequestTest {
    private Context mContext;


    MockECSServices mockECSServices;
    ECSServices ecsServices;


    private AppInfra appInfra;


    @Mock
    RestInterface mockRestInterface;

    MockGetConfigurationRequest mockGetConfigurationRequest;

    ECSCallback<ECSConfig, Exception> ecsCallback;
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

        ecsCallback = new ECSCallback<ECSConfig, Exception>() {
            @Override
            public void onResponse(ECSConfig result) {

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {

            }
        };

        //Creating config request object
        mockGetConfigurationRequest = new MockGetConfigurationRequest("GetConfigSuccess.json", ecsCallback);
    }


    @Test
    public void getConfigurationRequestSuccess() {
        mockInputValidator.setJsonFileName("GetConfigSuccess.json");
        mockECSServices.configureECSToGetConfiguration(new ECSCallback<ECSConfig, Exception>() {
            @Override
            public void onResponse(ECSConfig result) {
                assertNotNull(result);
                assertNotNull(result.getSiteId());
                assertNotNull(result.getRootCategory());
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
    public void getConfigurationRequestFailure() {
        mockInputValidator.setJsonFileName("GetConfigFailure.json");
        mockECSServices.configureECSToGetConfiguration(new ECSCallback<ECSConfig, Exception>() {
            @Override
            public void onResponse(ECSConfig result) {
                assertTrue(false);
                //test case failed

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertTrue(true);
                //test case passed
            }
        });;

    }

    @Test
    public void isValidURL() {
        String excepted = StaticBlock.getBaseURL()+"pilcommercewebservices"+"/v2/inAppConfig/"+StaticBlock.getLocale()+"/"+StaticBlock.getPropositionID();
        Assert.assertEquals(excepted,mockGetConfigurationRequest.getURL());
    }

    @Test
    public void isValidGetRequest() {
        Assert.assertEquals(0,mockGetConfigurationRequest.getMethod());
    }


    @Test
    public void verifyOnResponseError() {
        ECSCallback<ECSConfig, Exception> spy1 = Mockito.spy(ecsCallback);
        mockGetConfigurationRequest = new MockGetConfigurationRequest("GetConfigSuccess.json", spy1);
        VolleyError volleyError = new NoConnectionError();
        mockGetConfigurationRequest.onErrorResponse(volleyError);
        Mockito.verify(spy1).onFailure(any(Exception.class),any(ECSError.class));

    }

    @Test
    public void verifyOnResponseSuccess() {

        ECSCallback<ECSConfig, Exception> spy1 = Mockito.spy(ecsCallback);
        mockGetConfigurationRequest = new MockGetConfigurationRequest("GetConfigSuccess.json",spy1);

        JSONObject jsonObject = getJsonObject("GetConfigSuccess.json");

        mockGetConfigurationRequest.onResponse(jsonObject);

        Mockito.verify(spy1).onResponse(any(ECSConfig.class));

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
        assertNotNull(mockGetConfigurationRequest.getJSONSuccessResponseListener());
    }
}
