package com.philips.pins.shinelib.utility;

import com.philips.pins.shinelib.SHNDevice;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class QuickTestConnectionTest {

    @Mock
    SHNDevice deviceMock;

    @Mock
    QuickTestConnection.Listener listenerMock;

    @Captor
    ArgumentCaptor<SHNDevice.SHNDeviceListener> deviceListenerCaptor;

    private QuickTestConnection quickTestConnection;

    @Before
    public void setUp() {
        initMocks(this);

        quickTestConnection = new QuickTestConnection();
    }

    @Test
    public void ShouldCallConnect_WhenExecuteIsCalled() {
        quickTestConnection.execute(deviceMock, listenerMock);

        verify(deviceMock).connect();
    }

    @Test
    public void ShouldInformListenerOfFailure_WhenDeviceCannotBeConnected() {
        quickTestConnection.execute(deviceMock, listenerMock);

        verify(deviceMock).registerSHNDeviceListener(deviceListenerCaptor.capture());

        SHNDevice.SHNDeviceListener deviceListener = deviceListenerCaptor.getValue();
        deviceListener.onFailedToConnect(null, null);

        verify(listenerMock).onFailure();
    }

    @Test
    public void ShouldUnregisterDeviceListener_WhenStopIsCalledAfterStart() {
        quickTestConnection.execute(deviceMock, listenerMock);

        verify(deviceMock).registerSHNDeviceListener(deviceListenerCaptor.capture());
        SHNDevice.SHNDeviceListener deviceListener = deviceListenerCaptor.getValue();

        quickTestConnection.stop();

        verify(deviceMock).unregisterSHNDeviceListener(deviceListener);
    }

    @Test
    public void ShouldUnregisterDeviceListener_WhenDeviceCannotBeConnected() {
        quickTestConnection.execute(deviceMock, listenerMock);

        verify(deviceMock).registerSHNDeviceListener(deviceListenerCaptor.capture());
        SHNDevice.SHNDeviceListener deviceListener = deviceListenerCaptor.getValue();

        deviceListener.onFailedToConnect(null, null);

        verify(deviceMock).unregisterSHNDeviceListener(deviceListener);
    }

    @Test
    public void ShouldUnregisterDeviceListenerOnlyOnce_WhenStopMultipleTimesIsCalledAfterStart() {
        quickTestConnection.execute(deviceMock, listenerMock);

        verify(deviceMock).registerSHNDeviceListener(deviceListenerCaptor.capture());
        SHNDevice.SHNDeviceListener deviceListener = deviceListenerCaptor.getValue();

        quickTestConnection.stop();
        quickTestConnection.stop();

        verify(deviceMock).unregisterSHNDeviceListener(deviceListener);
    }

    @Test
    public void ShouldInformListenerOnSuccess_WhenDeviceBecomesConnected() {
        quickTestConnection.execute(deviceMock, listenerMock);

        when(deviceMock.getState()).thenReturn(SHNDevice.State.Connected);
        verify(deviceMock).registerSHNDeviceListener(deviceListenerCaptor.capture());

        SHNDevice.SHNDeviceListener deviceListener = deviceListenerCaptor.getValue();
        deviceListener.onStateUpdated(deviceMock);

        verify(listenerMock).onSuccess();
    }

    @Test
    public void ShouldUnregisterDeviceListener_WhenDeviceIsConnected() {
        quickTestConnection.execute(deviceMock, listenerMock);

        when(deviceMock.getState()).thenReturn(SHNDevice.State.Connected);
        verify(deviceMock).registerSHNDeviceListener(deviceListenerCaptor.capture());

        SHNDevice.SHNDeviceListener deviceListener = deviceListenerCaptor.getValue();
        deviceListener.onStateUpdated(deviceMock);

        verify(deviceMock).unregisterSHNDeviceListener(deviceListener);
    }

    @Test
    public void ShouldIgnoreNonConnectedStates_WhenDeviceStateIsUpdated() {
        quickTestConnection.execute(deviceMock, listenerMock);

        verify(deviceMock).registerSHNDeviceListener(deviceListenerCaptor.capture());
        SHNDevice.SHNDeviceListener deviceListener = deviceListenerCaptor.getValue();

        when(deviceMock.getState()).thenReturn(SHNDevice.State.Disconnected);
        deviceListener.onStateUpdated(deviceMock);


        when(deviceMock.getState()).thenReturn(SHNDevice.State.Disconnecting);
        deviceListener.onStateUpdated(deviceMock);

        when(deviceMock.getState()).thenReturn(SHNDevice.State.Connecting);
        deviceListener.onStateUpdated(deviceMock);

        verify(deviceMock, never()).unregisterSHNDeviceListener(deviceListener);
    }

    @Test
    public void ShouldReportFailure_WhenStateIsDisconnectingDuringExecute() {
        when(deviceMock.getState()).thenReturn(SHNDevice.State.Disconnecting);

        quickTestConnection.execute(deviceMock, listenerMock);

        verify(listenerMock).onFailure();
    }

    @Test
    public void ShouldReportSuccess_WhenStateIsConnectedDuringExecute() {
        when(deviceMock.getState()).thenReturn(SHNDevice.State.Connected);

        quickTestConnection.execute(deviceMock, listenerMock);

        verify(listenerMock).onSuccess();
    }

    @Test
    public void ShouldReportSuccess_WhenStateIsConnectingDuringExecute() {
        when(deviceMock.getState()).thenReturn(SHNDevice.State.Connecting);

        quickTestConnection.execute(deviceMock, listenerMock);

        verify(deviceMock).registerSHNDeviceListener(isA(SHNDevice.SHNDeviceListener.class));
    }

    @Test
    public void ShouldNotConnect_WhenStateIsDisconnectingDuringExecute() {
        when(deviceMock.getState()).thenReturn(SHNDevice.State.Disconnecting);

        quickTestConnection.execute(deviceMock, listenerMock);

        verify(deviceMock, never()).connect();
    }

    @Test
    public void ShouldNotConnect_WhenStateIsConnectedDuringExecute() {
        when(deviceMock.getState()).thenReturn(SHNDevice.State.Connected);

        quickTestConnection.execute(deviceMock, listenerMock);

        verify(deviceMock, never()).connect();
    }
}
