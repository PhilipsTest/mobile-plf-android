package com.philips.platform.ecs.ProductDetail;

import android.content.Context;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.philips.platform.ecs.ECSServices;
import com.philips.platform.ecs.MockECSServices;
import com.philips.platform.ecs.StaticBlock;
import com.philips.platform.ecs.TestUtil;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.asset.Assets;
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

@RunWith(RobolectricTestRunner.class)
public class ProductAssetRequestTest {

    private Context mContext;


    MockECSServices mockECSServices;
    ECSServices ecsServices;


    private AppInfra appInfra;


    @Mock
    RestInterface mockRestInterface;

    MockGetProductAssetRequest mockGetProductAssetRequest;

    String samplePRXAssetUrl = "samplePRXURL";

    ECSCallback<Assets,Exception> ecsCallback;


    @Before
    public void setUp() throws Exception {


        mContext = getInstrumentation().getContext();
        appInfra = new AppInfra.Builder().setRestInterface(mockRestInterface).build(mContext);
        appInfra.getServiceDiscovery().setHomeCountry("DE");


        mockECSServices = new MockECSServices("", appInfra);
        ecsServices = new ECSServices("",appInfra);


        StaticBlock.initialize();
        ecsCallback = new ECSCallback<Assets, Exception>() {
            @Override
            public void onResponse(Assets result) {

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {

            }
        };
        mockGetProductAssetRequest = new MockGetProductAssetRequest("EmptyJson.json",samplePRXAssetUrl,ecsCallback);

    }

    @Test
    public void shouldValidateAssetURL() {
        assertEquals(samplePRXAssetUrl,mockGetProductAssetRequest.getURL());
    }

    @Test
    public void shouldValidGetRequest() {
        assertEquals(0,mockGetProductAssetRequest.getMethod());
    }

    @Test
    public void verifyOnResponseError() {
        ECSCallback<Assets, Exception> spy1 = Mockito.spy(ecsCallback);
        mockGetProductAssetRequest = new MockGetProductAssetRequest("EmptyJson.json",samplePRXAssetUrl,spy1);
        VolleyError volleyError = new NoConnectionError();
        mockGetProductAssetRequest.onErrorResponse(volleyError);
        Mockito.verify(spy1).onFailure(any(Exception.class),any(ECSError.class));

    }

    @Test
    public void verifyOnResponseSuccess() {

        ECSCallback<Assets, Exception> spy1 = Mockito.spy(ecsCallback);
        mockGetProductAssetRequest = new MockGetProductAssetRequest("PRXProductAssets.json",samplePRXAssetUrl,spy1);

        JSONObject jsonObject = getJsonObject("PRXProductAssets.json");

        mockGetProductAssetRequest.onResponse(jsonObject);

        Mockito.verify(spy1).onResponse(any(Assets.class));

    }

    @Test
    public void shouldValidJSONSuccessResponseListener() {
        assertNotNull(mockGetProductAssetRequest.getJSONSuccessResponseListener());
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
