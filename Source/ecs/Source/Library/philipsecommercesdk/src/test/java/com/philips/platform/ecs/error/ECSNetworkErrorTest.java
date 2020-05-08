package com.philips.platform.ecs.error;

import android.content.Context;

import com.philips.platform.ecs.ECSServices;
import com.philips.platform.ecs.MockECSServices;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.rest.RestInterface;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@RunWith(RobolectricTestRunner.class)
public class ECSNetworkErrorTest {

    private Context mContext;


    MockECSServices mockECSServices;
    ECSServices ecsServices;


    private AppInfra appInfra;


    @Mock
    RestInterface mockRestInterface;


    ECSNetworkError ecsNetworkError;

    @Before
    public void setUp() throws Exception {


        mContext = getInstrumentation().getContext();
        appInfra = new AppInfra.Builder().setRestInterface(mockRestInterface).build(mContext);
        appInfra.getServiceDiscovery().setHomeCountry("DE");


        mockECSServices = new MockECSServices("", appInfra);
        ecsServices = new ECSServices("", appInfra);
    }


    @Test
    public void getErrorLocalizedErrorMessage() {
        ecsNetworkError = new ECSNetworkError();
    }

    @Test
    public void getErrorLocalizedErrorMessageForAddress() {
    }

    @Test
    public void getErrorLocalizedErrorMessage1() {
    }

    @Test
    public void testParse() {
        ECSNetworkError.parseServerError("eyJlcnJvciI6ImludmFsaWRfZ3JhbnQiLCJlcnJvcl9kZXNjcmlwdGlvbiI6IkludmFsaWQgSmFucmFpbiBUb2tlbiBwZXNnazJldGZ2ZjNmN2doIn0=");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testException() throws Exception{
        ECSNetworkError.parseServerError("sfffsdagsdagdagsgewgs=");
    }

}