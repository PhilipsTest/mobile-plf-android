package com.philips.cdp2.commlib.ble.request;

import com.philips.cdp.dicommclient.request.Error;
import com.philips.cdp.dicommclient.request.ResponseHandler;
import com.philips.cdp2.commlib.ble.BleDeviceCache;
import com.philips.pins.shinelib.SHNCapabilityType;
import com.philips.pins.shinelib.SHNDevice;
import com.philips.pins.shinelib.SHNDevice.SHNDeviceListener;
import com.philips.pins.shinelib.capabilities.CapabilityDiComm;
import com.philips.pins.shinelib.dicommsupport.DiCommResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static com.philips.cdp.dicommclient.request.Error.TIMED_OUT;
import static com.philips.pins.shinelib.SHNDevice.State.Connected;
import static com.philips.pins.shinelib.SHNDevice.State.Disconnected;
import static com.philips.pins.shinelib.dicommsupport.StatusCode.NoError;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class BleRequestTest {
    private static final String CPP_ID = "Sinterklaas";
    private static final String PORT_NAME = "PoliticallyCorrectPiet";
    private static final int PRODUCT_ID = 1337;

    private BleRequest request;

    private Map<String, SHNDevice> deviceMap = new HashMap<>();

    @Mock
    private BleDeviceCache deviceCacheMock;

    @Mock
    private ResponseHandler responseHandlerMock;

    @Mock
    private DiCommResponse mockDicommResponse;

    @Mock
    private SHNDevice mockDevice;

    @Mock
    private CapabilityDiComm mockCapability;

    @Mock
    CountDownLatch mockInProgressLatch;

    SHNDeviceListener stateListener;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        deviceMap.put(CPP_ID, mockDevice);
        when(mockDevice.getCapabilityForType(SHNCapabilityType.DI_COMM)).thenReturn(mockCapability);
        when(mockDevice.getState()).thenReturn(Connected);

        when(deviceCacheMock.getDeviceMap()).thenReturn(deviceMap);

        doAnswer(new Answer() {
            @Override
            public Void answer(final InvocationOnMock invocation) throws Throwable {
                request.processDicommResponse(mockDicommResponse);
                return null;
            }
        }).when(mockInProgressLatch).await();

        doAnswer(new Answer() {
            @Override
            public Object answer(final InvocationOnMock invocation) throws Throwable {
                stateListener = (SHNDeviceListener) invocation.getArguments()[0];
                return null;
            }
        }).when(mockDevice).registerSHNDeviceListener(any(SHNDeviceListener.class));

        when(mockDicommResponse.getStatus()).thenReturn(NoError);
        when(mockDicommResponse.getPropertiesAsString()).thenReturn("{}");

        request = new BleGetRequest(deviceCacheMock, CPP_ID, PORT_NAME, PRODUCT_ID, responseHandlerMock);
        request.inProgressLatch = mockInProgressLatch;
    }

    @Test
    public void whenRequestIsCancelledAfterSuccessThenNoErrorIsReported() throws Exception {
        request.run();

        request.cancel("timeout");

        verify(responseHandlerMock, times(0)).onError(any(Error.class), anyString());
    }

    @Test
    public void whenTimeoutOccursBeforeRequestIsExecutedThenErrorIsReported() throws Exception {

        request.cancel("timeout");

        verify(responseHandlerMock).onError(eq(TIMED_OUT), anyString());
    }

    @Test
    public void whenTimeoutOccursBeforeRequestIsExecutedThenRequestIsNeverExecuted() throws Exception {
        request.cancel("timeout");

        request.run();

        verify(responseHandlerMock, times(0)).onSuccess(anyString());
    }

    @Test
    public void callsDisconnectAfterOnSuccess() {

        request.run();

        InOrder inOrder = inOrder(responseHandlerMock, mockDevice);
        inOrder.verify(responseHandlerMock).onSuccess(anyString());
        inOrder.verify(mockDevice).disconnect();
    }

    @Test
    public void callsConnectWhenNotConnected() {
        when(mockDevice.getState()).thenReturn(Disconnected);

        request.run();

        verify(mockDevice).connect();
    }

    @Test
    public void unregistersListenerAfterDisconnected() throws InterruptedException {
        doAnswer(new Answer() {
            @Override
            public Void answer(final InvocationOnMock invocation) throws Throwable {
                request.processDicommResponse(mockDicommResponse);
                when(mockDevice.getState()).thenReturn(Disconnected);
                stateListener.onStateUpdated(mockDevice);
                return null;
            }
        }).when(mockInProgressLatch).await();

        request.run();

        verify(mockDevice).registerSHNDeviceListener(null);
    }
}