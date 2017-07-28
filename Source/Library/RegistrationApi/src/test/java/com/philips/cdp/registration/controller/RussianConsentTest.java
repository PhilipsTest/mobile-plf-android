package com.philips.cdp.registration.controller;

import com.philips.cdp.registration.injection.RegistrationComponent;
import com.philips.cdp.registration.ui.utils.URInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class RussianConsentTest {

    @Mock
    private RegistrationComponent mockRegistrationComponent;

    @Mock
    private ServiceDiscoveryInterface mockServiceDiscoveryInterface;

    private RussianConsent russianConsent;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        URInterface.setComponent(mockRegistrationComponent);

        russianConsent = new RussianConsent();
        russianConsent.injectMocks(mockServiceDiscoveryInterface);
    }

    @After
    public void tearDown() throws Exception {
        mockRegistrationComponent = null;
        mockServiceDiscoveryInterface = null;
    }

    @Test
    public void testNonRussianParams() throws Exception {
        Mockito.when(mockServiceDiscoveryInterface.getHomeCountry()).thenReturn("IN");
        JSONObject resultJsonObject = russianConsent.addRussianConsent(new JSONObject());
        Assert.assertEquals(null, resultJsonObject);
    }

    @Test
    public void testRussianParams() throws Exception {
        Mockito.when(mockServiceDiscoveryInterface.getHomeCountry()).thenReturn("RU");
        JSONObject actual = russianConsent.addRussianConsent(new JSONObject());
        JSONObject expected = new JSONObject().put("janrain", new JSONObject().put("controlFields",
                new JSONObject().put("one", "true")));
        Assert.assertEquals(actual.toString(), expected.toString());
    }

}