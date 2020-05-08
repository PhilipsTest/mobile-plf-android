package com.philips.platform.ecs.Voucher;

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
import com.philips.platform.ecs.model.voucher.ECSVoucher;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;

@RunWith(RobolectricTestRunner.class)
public class GetVoucherListTest {

    private Context mContext;


    MockECSServices mockECSServices;
    ECSServices ecsServices;


    private AppInfra appInfra;


    @Mock
    RestInterface mockRestInterface;

    MockGetVouchersRequest mockGetVouchersRequest;

    ECSCallback<List<ECSVoucher>, Exception> ecsCallback;
    private MockInputValidator mockInputValidator;


    @Before
    public void setUp() throws Exception {


        mContext = getInstrumentation().getContext();
        appInfra = new AppInfra.Builder().setRestInterface(mockRestInterface).build(mContext);
        appInfra.getServiceDiscovery().setHomeCountry("DE");


        mockECSServices = new MockECSServices("", appInfra);
        ecsServices = new ECSServices("",appInfra);

        StaticBlock.initialize();

        mockInputValidator = new MockInputValidator();
        ecsCallback = new ECSCallback<List<ECSVoucher>, Exception> () {

            @Override
            public void onResponse(List<ECSVoucher> result) {

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {

            }
        };

        mockGetVouchersRequest = new MockGetVouchersRequest("GetVoucherSuccess.json",ecsCallback);

    }

    @Test
    public void getAppliedVoucherListSuccess() {
        mockInputValidator.setJsonFileName("GetVoucherSuccess.json");
        mockECSServices.fetchAppliedVouchers(new ECSCallback<List<ECSVoucher>, Exception>() {
            @Override
            public void onResponse(List<ECSVoucher> result) {
                assertNotNull(result);
                assertNotNull(result.get(0).getCode());
                assertNotNull(result.get(0).getValue());
                //  test case passed
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertTrue(true);
                //  test case failed
            }
        });

    }

    @Test
    public void getAppliedVoucherListSuccessWithNoVoucher() {
        mockInputValidator.setJsonFileName("EmptyJson.json");// {}
        mockECSServices.fetchAppliedVouchers(new ECSCallback<List<ECSVoucher>, Exception>() {
            @Override
            public void onResponse(List<ECSVoucher> result) {
                assertTrue(true);
                //  test case failed
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertEquals(19999, ecsError);
                //  test case passed

            }
        });

    }

    @Test
    public void getAppliedVoucherListFailure() {
        mockInputValidator.setJsonFileName("EmptyString.json");
        mockECSServices.fetchAppliedVouchers(new ECSCallback<List<ECSVoucher>, Exception>() {
            @Override
            public void onResponse(List<ECSVoucher> result) {
                assertTrue(false);
                //  test case failed
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertEquals(5999, ecsError);
                //  test case passed
            }
        });

    }

    @Test
    public void isValidURL() {
        System.out.println("print the URL"+mockGetVouchersRequest.getURL());
        String excepted = StaticBlock.getBaseURL()+"pilcommercewebservices"+"/v2/"+StaticBlock.getSiteID()+"/users/current/carts/current/vouchers?lang="+StaticBlock.getLocale();
        assertEquals(excepted,mockGetVouchersRequest.getURL());
    }


    @Test
    public void isValidGetRequest() {
        assertEquals(0,mockGetVouchersRequest.getMethod());
    }

    @Test
    public void isValidHeader() {

        Map<String, String> expectedMap = new HashMap<String, String>();
        expectedMap.put("Content-Type", "application/x-www-form-urlencoded");
        expectedMap.put("Authorization", "Bearer " + "acceesstoken");

        Map<String, String> actual = mockGetVouchersRequest.getHeader();

        assertTrue(expectedMap.equals(actual));
    }

    @Test
    public void isValidParam() {
        assertNull(mockGetVouchersRequest.getParams());
    }


    @Test
    public void verifyOnResponseError() {
        ECSCallback<List<ECSVoucher>, Exception> spy1 = Mockito.spy(ecsCallback);
        mockGetVouchersRequest = new MockGetVouchersRequest("GetVoucherSuccess.json",spy1);
        VolleyError volleyError = new NoConnectionError();
        mockGetVouchersRequest.onErrorResponse(volleyError);
        Mockito.verify(spy1).onFailure(any(Exception.class),any(ECSError.class));

    }

    @Test
    public void verifyOnResponseSuccess() {

        ECSCallback<List<ECSVoucher>, Exception> spy1 = Mockito.spy(ecsCallback);
        mockGetVouchersRequest = new MockGetVouchersRequest("GetVoucherSuccess.json",spy1);

        JSONObject jsonObject = getJsonObject("GetVoucherSuccess.json");

        mockGetVouchersRequest.onResponse(jsonObject);

        Mockito.verify(spy1).onResponse(anyList());

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
        assertNotNull(mockGetVouchersRequest.getJSONSuccessResponseListener());
    }

}
