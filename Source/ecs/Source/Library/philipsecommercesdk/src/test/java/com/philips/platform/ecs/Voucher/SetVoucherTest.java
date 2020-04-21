package com.philips.platform.ecs.Voucher;

import android.content.Context;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.philips.platform.ecs.ECSServices;
import com.philips.platform.ecs.MockECSServices;
import com.philips.platform.ecs.MockInputValidator;
import com.philips.platform.ecs.StaticBlock;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.voucher.ECSVoucher;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.rest.RestInterface;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@RunWith(RobolectricTestRunner.class)
public class SetVoucherTest {
    private Context mContext;


    MockECSServices mockECSServices;
    ECSServices ecsServices;


    private AppInfra appInfra;


    @Mock
    RestInterface mockRestInterface;

    MockSetVoucherRequest mockSetVoucherRequest;

    ECSCallback<Boolean, Exception> ecsCallback;

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

        ecsCallback = new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {

            }
        };

        mockSetVoucherRequest = new MockSetVoucherRequest("EmptyString.json","voucherCode",ecsCallback);

    }


    @Test
    public void setVoucherSuccess() {


        mockInputValidator.setJsonFileName("EmptyString.json"); // empty string is success response of Apply voucher
        mockECSServices.applyVoucher("voucherCode",new ECSCallback<List<ECSVoucher>, Exception>() {
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
    public void setVoucherFailure() {
        mockInputValidator.setJsonFileName("ApplyVoucherFailure.json"); // empty string is success response of Apply voucher
        mockECSServices.applyVoucher("voucherCode",new ECSCallback<List<ECSVoucher>, Exception>() {
            @Override
            public void onResponse(List<ECSVoucher> result) {
                assertTrue(true);
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
        System.out.println("print the URL"+mockSetVoucherRequest.getURL());
        String excepted = StaticBlock.getBaseURL()+"pilcommercewebservices"+"/v2/"+StaticBlock.getSiteID()+"/users/current/carts/current/vouchers?lang="+StaticBlock.getLocale();
        assertEquals(excepted,mockSetVoucherRequest.getURL());
    }

    @Test
    public void isValidPostRequest() {
        assertEquals(1,mockSetVoucherRequest.getMethod());
    }

    @Test
    public void isValidHeader() {

        Map<String, String> expectedMap = new HashMap<String, String>();
        expectedMap.put("Content-Type", "application/x-www-form-urlencoded");
        expectedMap.put("Authorization", "Bearer " + "acceesstoken");

        Map<String, String> actual = mockSetVoucherRequest.getHeader();

        assertTrue(expectedMap.equals(actual));
    }

    @Test
    public void isValidParam() {
        assertNotNull(mockSetVoucherRequest.getParams());
        assertNotEquals(0,mockSetVoucherRequest.getParams().size());
    }


    @Test
    public void verifyOnResponseError() {
        ECSCallback<Boolean, Exception> spy1 = Mockito.spy(ecsCallback);
        mockSetVoucherRequest = new MockSetVoucherRequest("EmptyString.json","voucherCode",spy1);
        VolleyError volleyError = new NoConnectionError();
        mockSetVoucherRequest.onErrorResponse(volleyError);
        Mockito.verify(spy1).onFailure(any(Exception.class),any(ECSError.class));

    }

    @Test
    public void assertResponseSuccessListenerNotNull() {
        assertNotNull(mockSetVoucherRequest.getStringSuccessResponseListener());
    }

}
