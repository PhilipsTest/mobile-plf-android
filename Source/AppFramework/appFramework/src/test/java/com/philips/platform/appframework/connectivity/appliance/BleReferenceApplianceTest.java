package com.philips.platform.appframework.connectivity.appliance;

import com.philips.cdp.dicommclient.networknode.NetworkNode;
import com.philips.cdp2.commlib.core.communication.CommunicationStrategy;
import com.philips.platform.appframework.ConnectivityDeviceType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by philips on 16/01/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class BleReferenceApplianceTest {

    @Mock
    NetworkNode networkNode;

    @Mock
    CommunicationStrategy communicationStrategy;
    @Test
    public void getDeviceType_ReturnsTrue(){
        assertEquals("ReferenceNode", new BleReferenceAppliance(networkNode,communicationStrategy, ConnectivityDeviceType.REFERENCE_NODE).getDeviceType());
    }

    @Test
    public void getDeviceMeasurement_NotNull(){
        assertNotNull(new BleReferenceAppliance(networkNode,communicationStrategy, ConnectivityDeviceType.REFERENCE_NODE).getDeviceMeasurementPort());
    }
}
