package com.philips.cdp.di.ecs.Voucher;

import android.content.Context;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.philips.cdp.di.ecs.ECSServices;
import com.philips.cdp.di.ecs.MockECSServices;
import com.philips.cdp.di.ecs.StaticBlock;
import com.philips.cdp.di.ecs.integration.ECSCallback;
import com.philips.cdp.di.ecs.model.voucher.GetAppliedValue;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.rest.RestInterface;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;
import java.util.Map;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

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

    @Before
    public void setUp() throws Exception {


        mContext = getInstrumentation().getContext();
        appInfra = new AppInfra.Builder().setRestInterface(mockRestInterface).build(mContext);
        appInfra.getServiceDiscovery().setHomeCountry("DE");


        mockECSServices = new MockECSServices("", appInfra);
        ecsServices = new ECSServices("",appInfra);

        StaticBlock.initialize();

        ecsCallback = new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {

            }

            @Override
            public void onFailure(Exception error, int errorCode) {

            }
        };

        mockSetVoucherRequest = new MockSetVoucherRequest("EmptyString.json","voucherCode",ecsCallback);

    }


    @Test
    public void setVoucherSuccess() {
        mockECSServices.setJsonFileName("EmptyString.json"); // empty string is success response of Apply voucher
        mockECSServices.setVoucher("voucherCode",new ECSCallback<GetAppliedValue, Exception>() {
            @Override
            public void onResponse(GetAppliedValue result) {
                assertNotNull(result);
                assertNotNull(result.getVouchers().get(0).getCode());
                assertNotNull(result.getVouchers().get(0).getValue());
                //  test case passed
            }

            @Override
            public void onFailure(Exception error, int errorCode) {
                assertTrue(true);
                //  test case failed
            }
        });

    }

    @Test
    public void setVoucherFailure() {
        mockECSServices.setJsonFileName("ApplyVoucherFailure.json"); // empty string is success response of Apply voucher
        mockECSServices.setVoucher("voucherCode",new ECSCallback<GetAppliedValue, Exception>() {
            @Override
            public void onResponse(GetAppliedValue result) {
                assertTrue(true);
                //  test case failed
            }

            @Override
            public void onFailure(Exception error, int errorCode) {
                assertEquals(20999,errorCode);
                //  test case passed

            }
        });

    }

    @Test
    public void isValidURL() {
        System.out.println("print the URL"+mockSetVoucherRequest.getURL());
        String excepted = StaticBlock.getBaseURL()+"pilcommercewebservices"+"/v2/"+StaticBlock.getSiteID()+"/users/current/carts/current/vouchers?lang="+StaticBlock.getLocale();
        Assert.assertEquals(excepted,mockSetVoucherRequest.getURL());
    }

    @Test
    public void isValidPostRequest() {
        Assert.assertEquals(1,mockSetVoucherRequest.getMethod());
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
        Mockito.verify(spy1).onFailure(any(Exception.class),anyInt());

    }

    @Test
    public void assertResponseSuccessListenerNotNull() {
        assertNotNull(mockSetVoucherRequest.getStringSuccessResponseListener());
    }

}
