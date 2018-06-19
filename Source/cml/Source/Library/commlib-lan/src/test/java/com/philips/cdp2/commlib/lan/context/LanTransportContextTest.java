/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.cdp2.commlib.lan.context;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import com.philips.cdp.dicommclient.networknode.NetworkNode;
import com.philips.cdp.dicommclient.testutil.RobolectricTest;
import com.philips.cdp.dicommclient.util.DICommLog;
import com.philips.cdp2.commlib.core.appliance.Appliance;
import com.philips.cdp2.commlib.core.communication.CommunicationStrategy;
import com.philips.cdp2.commlib.core.configuration.RuntimeConfiguration;
import com.philips.cdp2.commlib.core.discovery.DiscoveryStrategy;
import com.philips.cdp2.commlib.core.util.HandlerProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.HashSet;
import java.util.Set;

import static com.philips.cdp2.commlib.lan.context.LanTransportContext.acceptNewPinFor;
import static com.philips.cdp2.commlib.lan.context.LanTransportContext.acceptPinFor;
import static com.philips.cdp2.commlib.lan.context.LanTransportContext.findAppliancesWithMismatchedPinIn;
import static com.philips.cdp2.commlib.lan.context.LanTransportContext.readPin;
import static com.philips.cdp2.commlib.lan.context.LanTransportContext.rejectNewPinFor;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LanTransportContextTest extends RobolectricTest {

    private static final String PIN2 = "4Tcsx5yChNF8AR7cRuFjrT3tRCkSMpIsklVLAO0ONxF=";
    private static final String PIN1 = "4Tcsx5yChNF8AR7cRuFjrT3tRCkSMpIsklVL/O0ONxE=";
    private static final String PIN3 = "4Bcsx5yChNF8AR7cRuFjrT3tRCkSMpIsklVLAO0ONxF=";

    @Mock
    private CommunicationStrategy communicationStrategyMock;

    @Mock
    private Handler handlerMock;

    @Mock
    private Context contextMock;

    @Mock
    private ConnectivityManager connectivityManagerMock;

    @Mock
    private NetworkInfo activeNetworkInfoMock;

    @Mock
    private DiscoveryStrategy lanDiscoveryStrategyMock;

    @Mock
    private RuntimeConfiguration runtimeConfigurationMock;

    private LanTransportContext lanTransportContext;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        HandlerProvider.enableMockedHandler(handlerMock);
        DICommLog.disableLogging();

        when(runtimeConfigurationMock.getContext()).thenReturn(contextMock);
        when(contextMock.getApplicationContext()).thenReturn(contextMock);
        when(contextMock.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mock(ConnectivityManager.class));
    }

    @Test
    public void whenRejectingNewPinForAppliance_thenThePinShouldRemainUntouched_andTheMismatchedPinShouldBeNull() {
        NetworkNode networkNode = new NetworkNode();
        networkNode.setPin("1234567890");
        networkNode.setMismatchedPin("ABCDEF");

        Appliance appliance = createTestAppliance(networkNode);
        rejectNewPinFor(appliance);

        assertEquals("1234567890", appliance.getNetworkNode().getPin());
        assertNull(appliance.getNetworkNode().getMismatchedPin());
    }

    @Test
    public void whenAcceptingNewPinForAppliance_thenThePinShouldBeTheNewPin_andTheMismatchedPinShouldBeNull() {
        NetworkNode networkNode = new NetworkNode();
        networkNode.setPin(PIN1);
        networkNode.setMismatchedPin(PIN2);

        Appliance appliance = createTestAppliance(networkNode);

        acceptNewPinFor(appliance);

        assertEquals(PIN2, appliance.getNetworkNode().getPin());
        assertNull(appliance.getNetworkNode().getMismatchedPin());
    }

    @Test
    public void whenAcceptingAnExplicitPinForAppliance_thenThePinShouldBeTheNewPin_andTheMismatchedPinShouldBeNull() {
        final String newPin = PIN1;

        NetworkNode networkNode = new NetworkNode();
        networkNode.setPin(PIN2);
        networkNode.setMismatchedPin(PIN3);

        Appliance appliance = createTestAppliance(networkNode);

        acceptPinFor(appliance, newPin);

        assertEquals(newPin, appliance.getNetworkNode().getPin());
        assertNull(appliance.getNetworkNode().getMismatchedPin());
    }

    @Test
    public void whenAcceptingAnExplicitNullPinForAppliance_thenThePinIsSetToNull_andTheMismatchedPinShouldBeNull() {
        final String newPin = null;

        NetworkNode networkNode = new NetworkNode();
        networkNode.setPin(PIN2);
        networkNode.setMismatchedPin(PIN3);

        Appliance appliance = createTestAppliance(networkNode);

        acceptPinFor(appliance, newPin);

        assertEquals(newPin, appliance.getNetworkNode().getPin());
        assertNull(appliance.getNetworkNode().getMismatchedPin());
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenAcceptingAPinForAppliance_andPinIsNotAValidPublicKeyPin_thenIllegalArgumentExceptionIsThrown() {
        final String newPin = "1234567890";

        NetworkNode networkNode = new NetworkNode();
        networkNode.setPin("9876543210");
        networkNode.setMismatchedPin("ABCDEF");

        Appliance appliance = createTestAppliance(networkNode);

        acceptPinFor(appliance, newPin);
    }

    @Test
    public void whenReadingThePinFromAnAppliance_thenThePinOfItsNetworkNodeShouldBeReturned() {
        NetworkNode networkNode = new NetworkNode();
        networkNode.setPin("1234567890");

        Appliance appliance = createTestAppliance(networkNode);

        assertEquals("1234567890", readPin(appliance));
    }

    @Test
    public void whenFindingAppliancesWithMismatchedPinInEmptySet_ThenEmptySetIsReturned() {
        Set result = findAppliancesWithMismatchedPinIn(new HashSet());

        assertTrue(result.isEmpty());
    }

    @Test
    public void whenFindingApplianceWithMismatchedPinInSetOfAppliancesWithNoMismatch_ThenEmptySetIsReturned() {
        final NetworkNode networkNode = new NetworkNode();
        networkNode.setCppId("cpp");
        networkNode.setPin("1234567890");

        Set<Appliance> appliances = new HashSet<Appliance>() {{
            add(createTestAppliance(networkNode));
        }};

        Set result = findAppliancesWithMismatchedPinIn(appliances);

        assertTrue(result.isEmpty());
    }

    @Test
    public void whenFindingApplianceWithMismatchedPinInSetOfAppliancesWithMismatch_ThenSetHasOneAppliance() {
        final NetworkNode networkNode = new NetworkNode();
        networkNode.setCppId("cpp");
        networkNode.setPin("1234567890");
        networkNode.setMismatchedPin("0987654321");

        Set<Appliance> appliances = new HashSet<Appliance>() {{
            add(createTestAppliance(networkNode));
        }};

        Set result = findAppliancesWithMismatchedPinIn(appliances);

        assertEquals(1, result.size());
        assertEquals(result.toArray()[0], appliances.toArray()[0]);
    }

    @Test
    public void whenFindingApplianceWithMismatchedPinInSetOfAppliancesWithMismatchAndWithoutMismatch_ThenSetHasOneAppliance() {
        final NetworkNode mismatchedNetworkNode = new NetworkNode();
        mismatchedNetworkNode.setCppId("cpp");
        mismatchedNetworkNode.setPin("1234567890");
        mismatchedNetworkNode.setMismatchedPin("0987654321");

        final NetworkNode matchingNetworkNode = new NetworkNode();
        matchingNetworkNode.setCppId("cpp2");
        matchingNetworkNode.setPin("1234567890");

        Set<Appliance> appliances = new HashSet<Appliance>() {{
            add(createTestAppliance(mismatchedNetworkNode));
            add(createTestAppliance(matchingNetworkNode));
        }};

        Set result = findAppliancesWithMismatchedPinIn(appliances);

        assertEquals(1, result.size());
        assertEquals(result.toArray()[0], createTestAppliance(mismatchedNetworkNode));
    }

    @Test
    public void whenCreatingTransportContext_thenDiscoveryStrategyIsCreated() {

        lanTransportContext = new LanTransportContext(runtimeConfigurationMock) {
            @NonNull
            @Override
            DiscoveryStrategy createLanDiscoveryStrategy() {
                return lanDiscoveryStrategyMock;
            }
        };

        assertThat(lanTransportContext.getDiscoveryStrategy()).isNotNull();
    }

    @NonNull
    private Appliance createTestAppliance(final NetworkNode networkNode) {
        return new Appliance(networkNode, communicationStrategyMock) {
            @Override
            public String getDeviceType() {
                return "TEST";
            }
        };
    }
}
