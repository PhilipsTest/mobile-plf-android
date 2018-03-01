package com.philips.platform.appframework.connectivity.appliance;

import com.philips.cdp.dicommclient.networknode.NetworkNode;
import com.philips.cdp2.commlib.core.communication.CommunicationStrategy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BleReferenceApplianceTest {

    @Mock
    private NetworkNode networkNode;

    @Mock
    private CommunicationStrategy communicationStrategy;

    @Before
    public void setUp() throws Exception {
        when(networkNode.getModelId()).thenReturn(RefAppBleReferenceAppliance.MODEL_NAME_HH1600);
    }

    @Test
    public void getDeviceType_ReturnsTrue(){
        assertEquals("ReferenceNode", new RefAppBleReferenceAppliance(networkNode, communicationStrategy).getDeviceType());
    }

    @Test
    public void getDeviceMeasurement_NotNull(){
        assertNotNull(new RefAppBleReferenceAppliance(networkNode, communicationStrategy).getDeviceMeasurementPort());
    }

    @Test
    public void getSessionDataPortTest() {
        RefAppBleReferenceAppliance bleReferenceAppliance = new RefAppBleReferenceAppliance(networkNode, communicationStrategy);
        assertNotNull(bleReferenceAppliance.getSessionDataPort());
    }

    @After
    public void tearDown() {
        networkNode = null;
        communicationStrategy = null;
    }
}
