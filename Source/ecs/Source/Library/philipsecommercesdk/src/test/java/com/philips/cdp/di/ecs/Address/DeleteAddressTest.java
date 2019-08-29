package com.philips.cdp.di.ecs.Address;

import android.content.Context;

import com.philips.cdp.di.ecs.ECSServices;
import com.philips.cdp.di.ecs.MockECSServices;
import com.philips.cdp.di.ecs.StaticBlock;
import com.philips.cdp.di.ecs.integration.ECSCallback;
import com.philips.cdp.di.ecs.model.address.Addresses;
import com.philips.cdp.di.ecs.model.address.GetShippingAddressData;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.rest.RestInterface;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class DeleteAddressTest {


    private Context mContext;


    MockECSServices mockECSServices;
    ECSServices ecsServices;


    private AppInfra appInfra;


    @Mock
    RestInterface mockRestInterface;

    MockDeleteAddressRequest mockDeleteAddressRequest;

    @Before
    public void setUp() throws Exception {


        mContext = getInstrumentation().getContext();
        appInfra = new AppInfra.Builder().setRestInterface(mockRestInterface).build(mContext);



        mockECSServices = new MockECSServices("", appInfra);
        ecsServices = new ECSServices("",appInfra);

        StaticBlock.initialize();
        Addresses addresses = new Addresses();
        addresses.setId("1234");
        mockDeleteAddressRequest = new MockDeleteAddressRequest("EmptyString.json", addresses, new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {

            }

            @Override
            public void onFailure(Exception error, int errorCode) {

            }
        });

    }

    @Test
    public void addAddressSingleSuccess() {
        mockECSServices.setJsonFileName("EmptyString.json");
        Addresses address = new Addresses();
        mockECSServices.deleteAddress(address, new ECSCallback<GetShippingAddressData, Exception>() {
            @Override
            public void onResponse(GetShippingAddressData addressList) {
                assertNotNull(addressList);
                assertNotNull(addressList.getAddresses());

            }

            @Override
            public void onFailure(Exception error, int errorCode) {
                assertTrue(false);
            }
        });

    }


    @Test
    public void addAddressSingleFailureInvalidBaseSite() {
        mockECSServices.setJsonFileName("DeleteAddressFailureInvalidBaseSite.json");
        Addresses address = new Addresses();
        mockECSServices.deleteAddress(address, new ECSCallback<GetShippingAddressData, Exception>() {
            @Override
            public void onResponse(GetShippingAddressData addressList) {
                assertTrue(false);

            }

            @Override
            public void onFailure(Exception error, int errorCode) {
                assertTrue(true);
            }
        });

    }

    @Test
    public void isValidURL() {
        String excepted = StaticBlock.getBaseURL()+"pilcommercewebservices"+"/v2/"+StaticBlock.getSiteID()+"/users/current/addresses/"+"1234"+"?fields=FULL&lang="+StaticBlock.getLocale();
        Assert.assertEquals(excepted,mockDeleteAddressRequest.getURL());
    }

}
