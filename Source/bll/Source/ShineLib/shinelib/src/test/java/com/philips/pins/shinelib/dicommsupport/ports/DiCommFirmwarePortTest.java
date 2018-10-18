/*
 * Copyright (c) 2015-2017 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.pins.shinelib.dicommsupport.ports;

import com.philips.pins.shinelib.SHNMapResultListener;
import com.philips.pins.shinelib.SHNResult;
import com.philips.pins.shinelib.dicommsupport.DiCommChannel;
import com.philips.pins.shinelib.helper.MockedHandler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import static com.philips.pins.shinelib.dicommsupport.ports.DiCommFirmwarePort.State.Downloading;
import static com.philips.pins.shinelib.dicommsupport.ports.DiCommFirmwarePort.State.Unknown;
import static com.philips.pins.shinelib.dicommsupport.ports.DiCommFirmwarePort.getProgressFromProps;
import static com.philips.pins.shinelib.dicommsupport.ports.DiCommFirmwarePort.getStateFromProps;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class DiCommFirmwarePortTest {
    @Mock
    private DiCommChannel diCommChannelMock;

    @Captor
    private ArgumentCaptor<SHNMapResultListener> mapResultListenerArgumentCaptor;

    private Map<String, Object> properties = new HashMap<>();
    private DiCommFirmwarePort diCommFirmwarePort;
    private MockedHandler mockedHandler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        mockedHandler = new MockedHandler();
        diCommFirmwarePort = new DiCommFirmwarePort(mockedHandler.getMock());
    }

    @Test
    public void canConvertStringToAKnownState() throws Exception {
        assertEquals(DiCommFirmwarePort.State.Ready, DiCommFirmwarePort.State.fromString("ready"));
    }

    @Test
    public void canConvertUpperCaseStringToAKnownState() throws Exception {
        assertEquals(DiCommFirmwarePort.State.Ready, DiCommFirmwarePort.State.fromString("Ready"));
    }

    @Test
    public void whenStringIsNotAValidStateThenStateIsUnknown() throws Exception {
        assertEquals(DiCommFirmwarePort.State.Unknown, DiCommFirmwarePort.State.fromString("notAState"));
    }

    @Test
    public void whenCancelIsReceivedThenStateIsCanceling() throws Exception {
        assertEquals(DiCommFirmwarePort.State.Canceling, DiCommFirmwarePort.State.fromString("cancel"));
    }

    @Test
    public void whenCancelingIsReceivedThenStateIsCanceling() throws Exception {
        assertEquals(DiCommFirmwarePort.State.Canceling, DiCommFirmwarePort.State.fromString("canceling"));
    }

    @Test
    public void whenCancellingIsReceivedThenStateIsCanceling() throws Exception {
        assertEquals(DiCommFirmwarePort.State.Canceling, DiCommFirmwarePort.State.fromString("cancelling"));
    }

    @Test
    public void canConvertCommandToAString() throws Exception {
        assertEquals("go", DiCommFirmwarePort.Command.DeployGo.getName());
    }

    @Test
    public void whenCreatedThenStateIsUnknown() throws Exception {
        DiCommFirmwarePort diCommFirmwarePort = new DiCommFirmwarePort(mockedHandler.getMock());

        assertEquals(DiCommFirmwarePort.State.Unknown, diCommFirmwarePort.getState());
    }

    private void reloadProperties(String key, Object data) {
        diCommFirmwarePort.setDiCommChannel(diCommChannelMock);
        diCommFirmwarePort.onChannelAvailabilityChanged(true);
        verify(diCommChannelMock).reloadProperties(eq(DiCommFirmwarePort.FIRMWARE), mapResultListenerArgumentCaptor.capture());

        properties.put(key, data);
        mapResultListenerArgumentCaptor.getValue().onActionCompleted(properties, SHNResult.SHNOk);
    }

    @Test
    public void whenPropertiesAreReceivedThenStateIsUpdated() throws Exception {
        reloadProperties("state", "idle");

        assertEquals(DiCommFirmwarePort.State.Idle, diCommFirmwarePort.getState());
    }

    @Test
    public void whenCreatedThenMaxChunkSizeIsNotDefined() throws Exception {
        DiCommFirmwarePort diCommFirmwarePort = new DiCommFirmwarePort(mockedHandler.getMock());

        assertEquals(Integer.MAX_VALUE, diCommFirmwarePort.getMaxChunkSize());
    }

    @Test
    public void whenPropertiesAreReceivedThenMaxChunkSizeIsUpdated() throws Exception {
        reloadProperties("maxchunksize", 255);

        int expected = (int) (255 * 0.75);
        assertEquals(expected, diCommFirmwarePort.getMaxChunkSize());
    }

    @Test
    public void whenPropertiesAreReceivedWithDoubleChunkSizeThenMaxChunkSizeIsUpdated() throws Exception {
        reloadProperties("maxchunksize", 255d);

        int expected = (int) (255 * 0.75);
        assertEquals(expected, diCommFirmwarePort.getMaxChunkSize());
    }

    @Test
    public void whenCreatedThenStatusMessageIsUndefined() throws Exception {
        DiCommFirmwarePort diCommFirmwarePort = new DiCommFirmwarePort(mockedHandler.getMock());

        assertNull(diCommFirmwarePort.getStatusMessage());
    }

    @Test
    public void whenPropertiesAreReceivedThenStatusMessageIsUpdated() throws Exception {
        String data = "Error downloading";
        reloadProperties("statusmsg", data);

        assertEquals(data, diCommFirmwarePort.getStatusMessage());
    }

    @Test
    public void whenCreatedThenUpgradeIsUndefined() throws Exception {
        DiCommFirmwarePort diCommFirmwarePort = new DiCommFirmwarePort(mockedHandler.getMock());

        assertNull(diCommFirmwarePort.getUploadedUpgradeVersion());
    }

    @Test
    public void whenPropertiesAreReceivedThenUpgradeIsUpdated() throws Exception {
        String data = "Latest";
        reloadProperties("upgrade", data);

        assertEquals(data, diCommFirmwarePort.getUploadedUpgradeVersion());
    }

    @Test
    public void whenCreatedThenCanUpgradeIsUndefined() throws Exception {
        DiCommFirmwarePort diCommFirmwarePort = new DiCommFirmwarePort(mockedHandler.getMock());

        assertFalse(diCommFirmwarePort.getCanUpgrade());
    }

    @Test
    public void whenPropertiesAreReceivedThenCanUpgradeIsUpdated() throws Exception {
        reloadProperties("canupgrade", true);

        assertTrue(diCommFirmwarePort.getCanUpgrade());
    }

    @Test
    public void whenPropertiesAreReceivedWithInvalidValueThenCanUpgradeIsFalse() throws Exception {
        String data = "gtrue";
        reloadProperties("canupgrade", data);

        assertFalse(diCommFirmwarePort.getCanUpgrade());
    }

    @Test
    public void itReturnsCorrectStateFromPropertiesThatContainAValidStateString() {
        properties.put(DiCommFirmwarePort.Key.STATE, "downloading");

        DiCommFirmwarePort.State state = getStateFromProps(properties);

        assertThat(state).isEqualTo(Downloading);
    }

    @Test
    public void itReturnsStateUnknownFromPropertiesThatContainAnInvalidStateValueType() {
        properties.put(DiCommFirmwarePort.Key.STATE, new String[]{"downloading"});

        DiCommFirmwarePort.State state = getStateFromProps(properties);

        assertThat(state).isEqualTo(Unknown);
    }

    @Test
    public void itReturnsStateUnknownFromPropertiesThatContainANullStateValue() {
        properties.clear();

        DiCommFirmwarePort.State state = getStateFromProps(properties);

        assertThat(state).isEqualTo(Unknown);
    }

    @Test
    public void itReturnsStateUnknownFromPropertiesThatContainAnInvalidStateValue() {
        properties.put(DiCommFirmwarePort.Key.STATE, "downloadingz");

        DiCommFirmwarePort.State state = getStateFromProps(properties);

        assertThat(state).isEqualTo(Unknown);
    }

    @Test
    public void itReturnsCorrectProgressValueFromPropertiesThatContainAValidProgressValue() {
        properties.put(DiCommFirmwarePort.Key.PROGRESS, 22.0);

        int progress = getProgressFromProps(properties);

        assertThat(progress).isEqualTo(22);
    }
}