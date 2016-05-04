package com.philips.pins.shinelib.dicommsupport;

import com.philips.pins.shinelib.SHNMapResultListener;
import com.philips.pins.shinelib.SHNResult;
import com.philips.pins.shinelib.SHNResultListener;
import com.philips.pins.shinelib.framework.Timer;
import com.philips.pins.shinelib.helper.MockedHandler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class DiCommPortTest {

    public static final String KEY = "data";
    public static final String KEY1 = "data1";
    public static final String KEY2 = "data2";

    public static final int DATA = 5;
    public static final int DATA1 = 6;
    public static final int DATA2 = 7;

    @Mock
    private DiCommChannel diCommChannelMock;

    @Mock
    private DiCommPort.Listener diCommPortListenerMock;

    @Mock
    private DiCommPort.UpdateListener diCommUpdateListenerMock;

    @Mock
    private SHNMapResultListener<String, Object> mapResultListenerMock;

    @Mock
    private SHNResultListener resultListenerMock;

    @Captor
    private ArgumentCaptor<SHNMapResultListener<String, Object>> mapResultListenerArgumentCaptor;

    @Captor
    private ArgumentCaptor<Map<String, Object>> mapArgumentCaptor;

    public static final String PORT_NAME = "Port";
    private DiCommPort diCommPort;
    private Map<String, Object> properties = new HashMap<>();
    private MockedHandler mockedHandler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        mockedHandler = new MockedHandler();
        Timer.setHandler(mockedHandler.getMock());

        diCommPort = new DiCommPort(PORT_NAME);
        diCommPort.setListener(diCommPortListenerMock);
        diCommPort.setDiCommChannel(diCommChannelMock);

        properties.put(KEY, DATA);
        properties.put(KEY1, DATA1);
        properties.put(KEY2, DATA2);
    }

    @Test
    public void canCreate() throws Exception {
        new DiCommPort(PORT_NAME);
    }

    @Test
    public void whenCreatedThenNotAvailable() throws Exception {
        assertFalse(diCommPort.isAvailable());
    }

    @Test
    public void whenInitializedThenPropertiesAreEmpty() throws Exception {
        assertTrue(diCommPort.getProperties().isEmpty());
    }

    @Test
    public void whenChannelBecomesAvailableThenPortIsNotAvailable() throws Exception {
        diCommPort.onChannelAvailabilityChanged(true);

        assertFalse(diCommPort.isAvailable());
    }

    @Test
    public void whenChannelBecomesAvailableThenPortListenerIsNotNotified() throws Exception {
        diCommPort.onChannelAvailabilityChanged(true);

        verify(diCommPortListenerMock, never()).onPortAvailable(diCommPort);
    }

    @Test
    public void whenChannelBecomesAvailableAgainThenPropertiesAreRequested() throws Exception {
        diCommPort.setDiCommChannel(diCommChannelMock);
        diCommPort.onChannelAvailabilityChanged(true);

        verify(diCommChannelMock).reloadProperties(eq(PORT_NAME), mapResultListenerArgumentCaptor.capture());
    }

    @Test
    public void whenPropertiesAreReportedWithResultOkThenChannelIsAvailable() throws Exception {
        whenChannelBecomesAvailableAgainThenPropertiesAreRequested();

        mapResultListenerArgumentCaptor.getValue().onActionCompleted(properties, SHNResult.SHNOk);

        verify(diCommPortListenerMock).onPortAvailable(diCommPort);
        assertTrue(diCommPort.isAvailable());
    }

    @Test
    public void whenPropertiesAreReportedWithPropertiesAreStored() throws Exception {
        whenChannelBecomesAvailableAgainThenPropertiesAreRequested();

        mapResultListenerArgumentCaptor.getValue().onActionCompleted(properties, SHNResult.SHNOk);

        assertEquals(properties, diCommPort.getProperties());
    }

    @Test
    public void whenPropertiesAreReportedWithResultNotOkThenChannelIsUnavailable() throws Exception {
        whenChannelBecomesAvailableAgainThenPropertiesAreRequested();

        mapResultListenerArgumentCaptor.getValue().onActionCompleted(null, SHNResult.SHNErrorInvalidParameter);

        verify(diCommPortListenerMock, never()).onPortAvailable(diCommPort);
        assertFalse(diCommPort.isAvailable());
    }

    @Test
    public void whenReloadPropertiesIsCalledWhileUnavailableThenSHNErrorInvalidStateIsReported() throws Exception {
        diCommPort.setDiCommChannel(diCommChannelMock);
        diCommPort.reloadProperties(mapResultListenerMock);

        verify(diCommChannelMock).reloadProperties(eq(PORT_NAME), mapResultListenerArgumentCaptor.capture());
    }

    @Test
    public void whenPutPropertiesIsCalledWhileUnavailableThenSHNErrorInvalidStateIsReported() throws Exception {
        diCommPort.setDiCommChannel(diCommChannelMock);
        diCommPort.putProperties(properties, mapResultListenerMock);

        verify(diCommChannelMock).sendProperties(eq(properties), eq(PORT_NAME), mapResultListenerArgumentCaptor.capture());
    }

    private void goToAvailableState() {
        diCommPort.setDiCommChannel(diCommChannelMock);
        diCommPort.onChannelAvailabilityChanged(true);

        verify(diCommChannelMock).reloadProperties(eq(PORT_NAME), mapResultListenerArgumentCaptor.capture());

        mapResultListenerArgumentCaptor.getValue().onActionCompleted(properties, SHNResult.SHNOk);
        reset(diCommChannelMock);
    }

    @Test
    public void whenReloadPropertiesWithNoChannelThenSHNErrorInvalidStateIsReported() throws Exception {
        goToAvailableState();
        diCommPort.setDiCommChannel(null);

        diCommPort.reloadProperties(mapResultListenerMock);

        verify(mapResultListenerMock).onActionCompleted(null, SHNResult.SHNErrorInvalidState);
    }

    @Test
    public void whenPutPropertiesWithNoChannelThenSHNErrorInvalidStateIsReported() throws Exception {
        goToAvailableState();
        diCommPort.setDiCommChannel(null);

        diCommPort.putProperties(properties, mapResultListenerMock);

        verify(mapResultListenerMock).onActionCompleted(null, SHNResult.SHNErrorInvalidState);
    }

    @Test
    public void whenReloadPropertiesIsCalledThenSendPropertiesIsCalledOnChannel() throws Exception {
        goToAvailableState();

        diCommPort.reloadProperties(mapResultListenerMock);

        verify(diCommChannelMock).reloadProperties(eq(PORT_NAME), mapResultListenerArgumentCaptor.capture());
    }

    @Test
    public void whenReloadPropertiesResultIsReceivedThenListenerIsNotified() throws Exception {
        whenReloadPropertiesIsCalledThenSendPropertiesIsCalledOnChannel();

        mapResultListenerArgumentCaptor.getValue().onActionCompleted(properties, SHNResult.SHNOk);

        verify(mapResultListenerMock).onActionCompleted(properties, SHNResult.SHNOk);
    }

    @Test
    public void whenReloadPropertiesResultIsReceivedThenPropertiesAreUpdated() throws Exception {
        whenReloadPropertiesIsCalledThenSendPropertiesIsCalledOnChannel();

        Map<String, Object> properties = new HashMap<>();
        properties.put(KEY, 1);
        properties.put(KEY1, 2);
        properties.put(KEY2, 3);

        mapResultListenerArgumentCaptor.getValue().onActionCompleted(properties, SHNResult.SHNOk);

        assertEquals(properties, diCommPort.getProperties());
    }

    @Test
    public void whenPutPropertiesWhileUnavailableThenSendPropertiesISCalled() throws Exception {
        diCommPort.putProperties(properties, mapResultListenerMock);

        verify(diCommChannelMock).sendProperties(eq(properties), eq(PORT_NAME), mapResultListenerArgumentCaptor.capture());
    }

    @Test
    public void whenPutPropertiesIsCalledThenSendPropertiesIsCalledOnChannel() throws Exception {
        goToAvailableState();

        Map<String, Object> properties = new HashMap<>();
        diCommPort.putProperties(properties, mapResultListenerMock);

        verify(diCommChannelMock).sendProperties(eq(properties), eq(PORT_NAME), mapResultListenerArgumentCaptor.capture());
    }

    @Test
    public void whenPutPropertiesResultIsReceivedThenListenerIsNotified() throws Exception {
        whenPutPropertiesIsCalledThenSendPropertiesIsCalledOnChannel();

        mapResultListenerArgumentCaptor.getValue().onActionCompleted(properties, SHNResult.SHNOk);

        verify(mapResultListenerMock).onActionCompleted(properties, SHNResult.SHNOk);
    }

    @Test
    public void whenPutPropertiesResultIsReceivedThenPropertiesAreUpdated() throws Exception {
        whenPutPropertiesIsCalledThenSendPropertiesIsCalledOnChannel();

        Map<String, Object> properties = new HashMap<>();
        properties.put(KEY, 1);
        properties.put(KEY1, 2);
        properties.put(KEY2, 3);

        mapResultListenerArgumentCaptor.getValue().onActionCompleted(properties, SHNResult.SHNOk);

        assertEquals(properties, diCommPort.getProperties());
    }

    @Test
    public void canSubscribeWhileUnavailable() throws Exception {
        diCommPort.subscribe(diCommUpdateListenerMock, resultListenerMock);

        verify(resultListenerMock).onActionCompleted(SHNResult.SHNOk);
    }

    @Test
    public void canSubscribeWhenStateIsAvailable() throws Exception {
        goToAvailableState();

        diCommPort.subscribe(diCommUpdateListenerMock, resultListenerMock);

        verify(resultListenerMock).onActionCompleted(SHNResult.SHNOk);
    }

    @Test
    public void canUnsubscribedWhileUnavailable() throws Exception {
        diCommPort.subscribe(diCommUpdateListenerMock, resultListenerMock);

        reset(resultListenerMock);
        diCommPort.unsubscribe(diCommUpdateListenerMock, resultListenerMock);

        verify(resultListenerMock).onActionCompleted(SHNResult.SHNOk);
    }

    @Test
    public void whenUnsubscribedWhileNotSubscribedThenSHNErrorInvalidStateIsReported() throws Exception {
        goToAvailableState();

        diCommPort.unsubscribe(diCommUpdateListenerMock, resultListenerMock);

        verify(resultListenerMock).onActionCompleted(SHNResult.SHNErrorInvalidState);
    }

    @Test
    public void whenSubscribedWhenReloadPropertiesIsCalled() throws Exception {
        goToAvailableState();

        diCommPort.subscribe(diCommUpdateListenerMock, resultListenerMock);

        verify(diCommChannelMock).reloadProperties(eq(PORT_NAME), mapResultListenerArgumentCaptor.capture());
    }

    @Test
    public void whenSubscribedThenContinuesReloadingProperties() throws Exception {
        whenSubscribedWhenReloadPropertiesIsCalled();

        mapResultListenerArgumentCaptor.getValue().onActionCompleted(properties, SHNResult.SHNOk);

        reset(diCommChannelMock);
        assertEquals(1, mockedHandler.getScheduledExecutionCount());
        mockedHandler.executeFirstScheduledExecution();

        verify(diCommChannelMock).reloadProperties(eq(PORT_NAME), mapResultListenerArgumentCaptor.capture());
    }

    @Test
    public void whenReloadPropertiesReturnsFailThenListenerIsNotified() throws Exception {
        whenSubscribedWhenReloadPropertiesIsCalled();

        reset(diCommChannelMock);
        mapResultListenerArgumentCaptor.getValue().onActionCompleted(null, SHNResult.SHNErrorInvalidState);

        assertEquals(0, mockedHandler.getScheduledExecutionCount());
        verify(diCommChannelMock, never()).reloadProperties(anyString(), any(SHNMapResultListener.class));
        verify(diCommUpdateListenerMock).onSubscriptionFailed(SHNResult.SHNErrorInvalidState);
    }

    @Test
    public void whenChannelBecomesUnavalibleWhileSubscribedThenFailedIsReported() throws Exception {
        whenSubscribedWhenReloadPropertiesIsCalled();
        diCommPort.setDiCommChannel(null);

        mapResultListenerArgumentCaptor.getValue().onActionCompleted(properties, SHNResult.SHNOk);

        assertEquals(1, mockedHandler.getScheduledExecutionCount());
        reset(diCommChannelMock);
        mockedHandler.executeFirstScheduledExecution();

        verify(diCommChannelMock, never()).reloadProperties(anyString(), any(SHNMapResultListener.class));
        verify(diCommUpdateListenerMock).onSubscriptionFailed(SHNResult.SHNErrorInvalidState);
    }

    @Test
    public void whenSubscribedTwiceThenNotifiedOnce() throws Exception {
        whenSubscribedWhenReloadPropertiesIsCalled();
        diCommPort.subscribe(diCommUpdateListenerMock, resultListenerMock);

        mapResultListenerArgumentCaptor.getValue().onActionCompleted(null, SHNResult.SHNErrorInvalidState);

        verify(diCommUpdateListenerMock).onSubscriptionFailed(SHNResult.SHNErrorInvalidState);
        assertEquals(0, mockedHandler.getScheduledExecutionCount());
    }

    @Test
    public void whenSubscribedTwiceThenReloadPropertiesIsCalledOnce() throws Exception {
        whenSubscribedWhenReloadPropertiesIsCalled();
        reset(diCommChannelMock);
        diCommPort.subscribe(diCommUpdateListenerMock, resultListenerMock);

        verify(diCommChannelMock, never()).reloadProperties(anyString(), any(SHNMapResultListener.class));
    }

    @Test
    public void whenSubscribedTwiceThenSubscriptionResultIsReported() throws Exception {
        whenSubscribedWhenReloadPropertiesIsCalled();
        reset(diCommChannelMock, resultListenerMock);
        diCommPort.subscribe(diCommUpdateListenerMock, resultListenerMock);

        verify(resultListenerMock).onActionCompleted(SHNResult.SHNOk);
    }

    @Test
    public void whenUnsubscribedThenNotNotified() throws Exception {
        whenSubscribedWhenReloadPropertiesIsCalled();
        diCommPort.unsubscribe(diCommUpdateListenerMock, resultListenerMock);

        mapResultListenerArgumentCaptor.getValue().onActionCompleted(properties, SHNResult.SHNOk);

        verify(diCommUpdateListenerMock, never()).onSubscriptionFailed(any(SHNResult.class));
    }

    @Test
    public void whenUnsubscribedThenPollingIsStopped() throws Exception {
        whenSubscribedWhenReloadPropertiesIsCalled();
        mapResultListenerArgumentCaptor.getValue().onActionCompleted(properties, SHNResult.SHNOk);

        diCommPort.unsubscribe(diCommUpdateListenerMock, resultListenerMock);

        assertEquals(0, mockedHandler.getScheduledExecutionCount());
    }

    @Test
    public void whenThereIsASubscriptionThenPollingIsNotStopped() throws Exception {
        whenSubscribedWhenReloadPropertiesIsCalled();
        DiCommPort.UpdateListener diCommUpdateListenerMock2 = mock(DiCommPort.UpdateListener.class);
        diCommPort.subscribe(diCommUpdateListenerMock2, resultListenerMock);

        mapResultListenerArgumentCaptor.getValue().onActionCompleted(properties, SHNResult.SHNOk);

        diCommPort.unsubscribe(diCommUpdateListenerMock, resultListenerMock);

        assertEquals(1, mockedHandler.getScheduledExecutionCount());
    }

    @Test
    public void whenUnsubscribedThenPollingIsStopped2() throws Exception {
        whenSubscribedWhenReloadPropertiesIsCalled();
        diCommPort.unsubscribe(diCommUpdateListenerMock, resultListenerMock);

        mapResultListenerArgumentCaptor.getValue().onActionCompleted(properties, SHNResult.SHNOk);

        assertEquals(0, mockedHandler.getScheduledExecutionCount());
    }

    @Test
    public void whenSubscribedAgainThenPollingIsNotRestarted() throws Exception {
        whenSubscribedWhenReloadPropertiesIsCalled();
        reset(diCommChannelMock);

        DiCommPort.UpdateListener diCommUpdateListenerMock2 = mock(DiCommPort.UpdateListener.class);
        diCommPort.subscribe(diCommUpdateListenerMock2, resultListenerMock);

        verify(diCommChannelMock, never()).reloadProperties(eq(PORT_NAME), mapResultListenerArgumentCaptor.capture());
    }

    private void verifyReloadPropertiesSent() {
        goToAvailableState();

        diCommPort.subscribe(diCommUpdateListenerMock, resultListenerMock);

        reset(diCommUpdateListenerMock);
        mockedHandler.executeFirstScheduledExecution();
        verify(diCommChannelMock).reloadProperties(eq(PORT_NAME), mapResultListenerArgumentCaptor.capture());
    }

    @Test
    public void whenPropertiesHaveNotChangedThenUpdateListenerIsNotNotified() throws Exception {
        verifyReloadPropertiesSent();

        Map<String, Object> newProperties = new HashMap<>(properties);
        mapResultListenerArgumentCaptor.getValue().onActionCompleted(newProperties, SHNResult.SHNOk);

        verify(diCommUpdateListenerMock, never()).onPropertiesChanged(anyMap());
    }

    @Test
    public void whenOnePropertyHasChangedThenUpdateListenerIsNotified() throws Exception {
        verifyReloadPropertiesSent();

        Map<String, Object> newProperties = new HashMap<>(properties);
        newProperties.put(KEY, DATA * 2);

        mapResultListenerArgumentCaptor.getValue().onActionCompleted(newProperties, SHNResult.SHNOk);

        verify(diCommUpdateListenerMock).onPropertiesChanged(mapArgumentCaptor.capture());
        assertEquals(1, mapArgumentCaptor.getValue().size());
        assertTrue(mapArgumentCaptor.getValue().containsKey(KEY));
    }

    @Test
    public void whenMultiplePropertiesHaveChangedThenUpdateListenerIsNotified() throws Exception {
        verifyReloadPropertiesSent();

        Map<String, Object> newProperties = new HashMap<>(properties);
        newProperties.put(KEY, DATA * 2);
        newProperties.put(KEY1, DATA * 2);

        mapResultListenerArgumentCaptor.getValue().onActionCompleted(newProperties, SHNResult.SHNOk);

        verify(diCommUpdateListenerMock).onPropertiesChanged(mapArgumentCaptor.capture());
        assertEquals(2, mapArgumentCaptor.getValue().size());
        assertTrue(mapArgumentCaptor.getValue().containsKey(KEY));
        assertTrue(mapArgumentCaptor.getValue().containsKey(KEY1));
    }

    @Test
    public void whenPropertyValueChangesToExistingOneThenUpdateListenerIsNotified() throws Exception {
        verifyReloadPropertiesSent();

        Map<String, Object> newProperties = new HashMap<>(properties);
        newProperties.put(KEY, DATA1);

        mapResultListenerArgumentCaptor.getValue().onActionCompleted(newProperties, SHNResult.SHNOk);

        verify(diCommUpdateListenerMock).onPropertiesChanged(mapArgumentCaptor.capture());
        assertEquals(1, mapArgumentCaptor.getValue().size());
        assertTrue(mapArgumentCaptor.getValue().containsKey(KEY));
    }
}