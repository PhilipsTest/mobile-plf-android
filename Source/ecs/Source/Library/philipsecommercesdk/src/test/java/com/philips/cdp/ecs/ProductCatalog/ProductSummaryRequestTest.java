package com.philips.cdp.ecs.ProductCatalog;

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
import com.philips.platform.ecs.model.products.ECSProduct;
import com.philips.platform.ecs.model.summary.ECSProductSummary;
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
import java.util.ArrayList;
import java.util.List;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@RunWith(RobolectricTestRunner.class)
public class ProductSummaryRequestTest {

    private Context mContext;


    MockECSServices mockECSServices;
    ECSServices ecsServices;

    MockInputValidator mockInputValidator;


    private AppInfra appInfra;


    @Mock
    RestInterface mockRestInterface;

    MockGetProductSummaryListRequest mockGetProductSummaryListRequest;

    String samplePRXAssetUrl = "samplePRXURL";

    ECSCallback<ECSProductSummary,Exception> ecsCallback;


    @Before
    public void setUp() throws Exception {


        mContext = getInstrumentation().getContext();
        appInfra = new AppInfra.Builder().setRestInterface(mockRestInterface).build(mContext);
        appInfra.getServiceDiscovery().setHomeCountry("DE");


        mockECSServices = new MockECSServices("", appInfra);
        ecsServices = new ECSServices("",appInfra);

        mockInputValidator = new MockInputValidator();


        StaticBlock.initialize();
        ecsCallback = new ECSCallback<ECSProductSummary, Exception>() {
            @Override
            public void onResponse(ECSProductSummary result) {

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {

            }
        };
        mockGetProductSummaryListRequest = new MockGetProductSummaryListRequest("EmptyJson.json",samplePRXAssetUrl,ecsCallback);

    }


    @Test
    public void getProductListSuccess() {


        mockInputValidator.setJsonFileName("PRXSummaryResponse.json");
        ArrayList<String> ctnList = new ArrayList<>();
        ctnList.add("1234");

        mockECSServices.fetchProductSummaries(ctnList, new ECSCallback<List<ECSProduct>, Exception>() {
            @Override
            public void onResponse(List<ECSProduct> result) {
                assertTrue(true);
                // test case failed

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertEquals(5999, ecsError); // error code for Product List
                // test case passed
            }
        });
    }

    /*
     * In this test case Get Product list API (Hybris) is failed
     * */
    @Test
    public void getProductListHybrisFailure() {

        mockInputValidator.setJsonFileName("EmptyJson.json");
        ArrayList<String> ctnList = new ArrayList<>();
        ctnList.add("1234");

        mockECSServices.fetchProductSummaries(ctnList, new ECSCallback<List<ECSProduct>, Exception>() {
            @Override
            public void onResponse(List<ECSProduct> result) {
                assertTrue(true);
                // test case failed

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertEquals(5999, ecsError); // error code for Product List
                // test case passed
            }
        });
    }

    @Test
    public void shouldValidateAssetURL() {
        assertEquals(samplePRXAssetUrl,mockGetProductSummaryListRequest.getURL());
    }

    @Test
    public void shouldValidGetRequest() {
        assertEquals(0,mockGetProductSummaryListRequest.getMethod());
    }

    @Test
    public void verifyOnResponseError() {
        ECSCallback<ECSProductSummary, Exception> spy1 = Mockito.spy(ecsCallback);
        mockGetProductSummaryListRequest = new MockGetProductSummaryListRequest("EmptyJson.json",samplePRXAssetUrl,spy1);
        VolleyError volleyError = new NoConnectionError();
        mockGetProductSummaryListRequest.onErrorResponse(volleyError);
        Mockito.verify(spy1).onFailure(any(Exception.class),any(ECSError.class));

    }

    @Test
    public void verifyOnResponseSuccess() {

        ECSCallback<ECSProductSummary, Exception> spy1 = Mockito.spy(ecsCallback);
        mockGetProductSummaryListRequest = new MockGetProductSummaryListRequest("EmptyJson.json",samplePRXAssetUrl,spy1);

        JSONObject jsonObject = getJsonObject("PRXSummaryResponse.json");

        mockGetProductSummaryListRequest.onResponse(jsonObject);

        Mockito.verify(spy1).onResponse(any(ECSProductSummary.class));

    }


    @Test
    public void verifyOnResponseJSONException() {

        try {
            JSONObject jsonObject = new JSONObject("123");
            ECSCallback<ECSProductSummary, Exception> spy1 = Mockito.spy(ecsCallback);
            mockGetProductSummaryListRequest = new MockGetProductSummaryListRequest("EmptyJson.json",samplePRXAssetUrl,spy1);


            mockGetProductSummaryListRequest.onResponse(jsonObject);

            Mockito.verify(spy1).onFailure(any(Exception.class),any(ECSError.class));

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }



    @Test
    public void shouldValidJSONSuccessResponseListener() {
        assertNotNull(mockGetProductSummaryListRequest.getJSONSuccessResponseListener());
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
