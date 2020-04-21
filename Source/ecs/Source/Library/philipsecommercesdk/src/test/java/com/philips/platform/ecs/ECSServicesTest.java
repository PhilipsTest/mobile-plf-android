package com.philips.platform.ecs;

import android.content.Context;

import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.rest.RestInterface;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertNotNull;


@RunWith(RobolectricTestRunner.class)

public class ECSServicesTest {
    private Context mContext;


    MockECSServices mockECSServices;
    ECSServices ecsServices;


    private AppInfra appInfra;

    @Mock
    RestInterface mockRestInterface;


    @Before
    public void setUp() throws Exception {


        mContext = getInstrumentation().getContext();
        appInfra = new AppInfra.Builder().setRestInterface(mockRestInterface).build(mContext);
        appInfra.getServiceDiscovery().setHomeCountry("DE");


        mockECSServices = new MockECSServices("", appInfra);
        ecsServices = new ECSServices("",appInfra);

    }

    @Test
    public void init() {
    }


    ////////////////Start of Auth Test cases/////////////





    ////////////////End of Auth Test cases/////////////

    @Test
    public void getIAPConfig() {
    }









    @Test
    public void getProductAsset() {
    }

    @Test
    public void getProductDisclaimer() {
    }
}