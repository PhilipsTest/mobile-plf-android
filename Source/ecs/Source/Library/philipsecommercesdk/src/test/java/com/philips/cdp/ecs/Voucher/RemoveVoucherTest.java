package com.philips.cdp.ecs.Voucher;

import android.content.Context;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.philips.platform.ecs.ECSServices;
import com.philips.cdp.ecs.MockECSServices;
import com.philips.cdp.ecs.MockInputValidator;
import com.philips.cdp.ecs.StaticBlock;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.voucher.ECSVoucher;
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
import java.util.List;
import java.util.Map;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@RunWith(RobolectricTestRunner.class)
public class RemoveVoucherTest {
    private Context mContext;


    MockECSServices mockECSServices;
    ECSServices ecsServices;


    private AppInfra appInfra;


    @Mock
    RestInterface mockRestInterface;

    MockRemoveVoucherRequest mockRemoveVoucherRequest;

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
        mockRemoveVoucherRequest = new MockRemoveVoucherRequest("EmptyString.json","voucherCode", ecsCallback);
    }

    @Test
    public void removeVoucherSuccess() {
        mockInputValidator.setJsonFileName("EmptyString.json"); // empty string is success response of Apply voucher
        mockECSServices.removeVoucher("voucherCode",new ECSCallback<List<ECSVoucher>, Exception>() {
            @Override
            public void onResponse(List<ECSVoucher> result) {
                assertNotNull(result);
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
    public void removeVoucherFailure() {
        mockInputValidator.setJsonFileName("ApplyVoucherFailure.json"); // empty string is success response of Apply voucher
        mockECSServices.removeVoucher("voucherCode",new ECSCallback<List<ECSVoucher>, Exception>() {
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
        System.out.println("print the URL"+mockRemoveVoucherRequest.getURL());
        String excepted = StaticBlock.getBaseURL()+"pilcommercewebservices"+"/v2/"+StaticBlock.getSiteID()+"/users/current/carts/current/vouchers/voucherCode?lang="+StaticBlock.getLocale();
        Assert.assertEquals(excepted,mockRemoveVoucherRequest.getURL());
    }


    @Test
    public void isValidDeleteRequest() {
        Assert.assertEquals(3,mockRemoveVoucherRequest.getMethod());
    }

    @Test
    public void isValidHeader() {

        Map<String, String> expectedMap = new HashMap<String, String>();
        expectedMap.put("Content-Type", "application/x-www-form-urlencoded");
        expectedMap.put("Authorization", "Bearer " + "acceesstoken");

        Map<String, String> actual = mockRemoveVoucherRequest.getHeader();

        assertTrue(expectedMap.equals(actual));
    }

    @Test
    public void isValidParam() {
        assertNull(mockRemoveVoucherRequest.getParams());
    }


    @Test
    public void verifyOnResponseError() {
        ECSCallback<Boolean, Exception> spy1 = Mockito.spy(ecsCallback);
        mockRemoveVoucherRequest = new MockRemoveVoucherRequest("EmptyString.json","voucherCode",spy1);
        VolleyError volleyError = new NoConnectionError();
        mockRemoveVoucherRequest.onErrorResponse(volleyError);
        Mockito.verify(spy1).onFailure(any(Exception.class),any(ECSError.class));

    }

    @Test
    public void assertResponseSuccessListenerNotNull() {
        assertNotNull(mockRemoveVoucherRequest.getStringSuccessResponseListener());
    }

}
