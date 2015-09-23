package com.philips.cl.di.regsample.app;

import android.test.ActivityInstrumentationTestCase2;

import com.philips.dhpclient.DhpApiClientConfiguration;
import com.philips.dhpclient.DhpAuthenticationManagementClient;

/**
 * Created by 310190722 on 9/8/2015.
 */
public class DhpApiClientTest extends ActivityInstrumentationTestCase2<RegistrationSampleActivity> {

    private DhpAuthenticationManagementClient authenticationManagementClient;
    private final DhpApiClientConfiguration dhpApiClientConfiguration = new DhpApiClientConfiguration(
            /*"http://ugrow_user_registration.hsdpcph-consumer.cloud.pcftest.com",
            "uGrowApplication",
            "2eaec11e-1a2e-11e5-b60b-1697f925ec7b",
            "2eaec60a-1a2e-11e5-b60b-1697f925ec7b");*/
            "https://cph-integration-user-registration-assembly.cloud.pcftest.com/",
            "CPHApplication",
            "9b65af93-7ab5-11e4-b31d-005056849f9f",
            "83068cae-bd6f-11e4-8dfc-aa07a5b093db");

    public DhpApiClientTest() {
        super(RegistrationSampleActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", getInstrumentation()
                .getTargetContext().getCacheDir().getPath());
        authenticationManagementClient = new DhpAuthenticationManagementClient(dhpApiClientConfiguration);
    }

    public void testCLientTest() {

        System.out.println("********************* Authen : "+authenticationManagementClient.authenticate("mimesisaugtest1@mailinator.com", "@Password1"));
        assertNotNull(authenticationManagementClient.authenticate("mimesisaugtest1@mailinator.com", "@Password1"));
    }
}
