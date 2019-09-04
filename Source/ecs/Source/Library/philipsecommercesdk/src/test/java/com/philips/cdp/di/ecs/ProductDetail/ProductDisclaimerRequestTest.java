package com.philips.cdp.di.ecs.ProductDetail;

import android.content.Context;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.philips.cdp.di.ecs.ECSServices;
import com.philips.cdp.di.ecs.MockECSServices;
import com.philips.cdp.di.ecs.StaticBlock;
import com.philips.cdp.di.ecs.TestUtil;
import com.philips.cdp.di.ecs.integration.ECSCallback;
import com.philips.cdp.di.ecs.model.asset.Assets;
import com.philips.cdp.di.ecs.model.disclaimer.Disclaimers;
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

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@RunWith(RobolectricTestRunner.class)
public class ProductDisclaimerRequestTest {

    private Context mContext;


    MockECSServices mockECSServices;
    ECSServices ecsServices;


    private AppInfra appInfra;


    @Mock
    RestInterface mockRestInterface;

    MockGetProductDisclaimerRequest mockGetProductDisclaimerRequest;

    String samplePRXAssetUrl = "samplePRXURL";

    ECSCallback<Disclaimers,Exception> ecsCallback;


    @Before
    public void setUp() throws Exception {


        mContext = getInstrumentation().getContext();
        appInfra = new AppInfra.Builder().setRestInterface(mockRestInterface).build(mContext);
        appInfra.getServiceDiscovery().setHomeCountry("DE");


        mockECSServices = new MockECSServices("", appInfra);
        ecsServices = new ECSServices("",appInfra);


        StaticBlock.initialize();
        ecsCallback = new ECSCallback<Disclaimers, Exception>() {
            @Override
            public void onResponse(Disclaimers result) {

            }

            @Override
            public void onFailure(Exception error, int errorCode) {

            }
        };
        mockGetProductDisclaimerRequest = new MockGetProductDisclaimerRequest("EmptyJson.json",samplePRXAssetUrl,ecsCallback);

    }

    @Test
    public void shouldValidateAssetURL() {
        assertEquals(samplePRXAssetUrl,mockGetProductDisclaimerRequest.getURL());
    }

    @Test
    public void shouldValidGetRequest() {
        assertEquals(0,mockGetProductDisclaimerRequest.getMethod());
    }

    @Test
    public void verifyOnResponseError() {
        ECSCallback<Disclaimers, Exception> spy1 = Mockito.spy(ecsCallback);
        mockGetProductDisclaimerRequest = new MockGetProductDisclaimerRequest("PRXDisclaimers.json",samplePRXAssetUrl,spy1);
        VolleyError volleyError = new NoConnectionError();
        mockGetProductDisclaimerRequest.onErrorResponse(volleyError);
        Mockito.verify(spy1).onFailure(any(Exception.class),anyInt());

    }

    @Test
    public void verifyOnResponseSuccess() {

        ECSCallback<Disclaimers, Exception> spy1 = Mockito.spy(ecsCallback);
        mockGetProductDisclaimerRequest = new MockGetProductDisclaimerRequest("PRXDisclaimers.json",samplePRXAssetUrl,spy1);

        JSONObject jsonObject = getJsonObject("PRXDisclaimers.json");

        mockGetProductDisclaimerRequest.onResponse(jsonObject);

        Mockito.verify(spy1).onResponse(any(Disclaimers.class));

    }

    @Test
    public void shouldValidJSONSuccessResponseListener() {
        assertNotNull(mockGetProductDisclaimerRequest.getJSONSuccessResponseListener());
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
