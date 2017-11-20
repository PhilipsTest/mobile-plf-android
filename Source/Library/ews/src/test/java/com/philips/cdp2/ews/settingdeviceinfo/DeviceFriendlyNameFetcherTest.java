package com.philips.cdp2.ews.settingdeviceinfo;

import android.support.annotation.Nullable;

import com.philips.cdp.dicommclient.port.DICommPortListener;
import com.philips.cdp.dicommclient.port.common.DevicePort;
import com.philips.cdp.dicommclient.port.common.DevicePortProperties;
import com.philips.cdp.dicommclient.request.Error;
import com.philips.cdp2.commlib.lan.context.LanTransportContext;
import com.philips.cdp2.ews.appliance.EWSGenericAppliance;
import com.philips.cdp2.ews.util.SecureStorageUtility;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SecureStorageUtility.class, LanTransportContext.class})
public class DeviceFriendlyNameFetcherTest {

    private DeviceFriendlyNameFetcher subject;

    @Mock private EWSGenericAppliance mockAppliance;
    @Mock private DevicePort mockDevicePort;
    @Mock private DevicePortProperties mockDevicePortProperties;
    @Mock private DeviceFriendlyNameFetcher.Callback mockCallback;
    @Mock private SecureStorageUtility mockSecureStorageUtility;

    @Captor private ArgumentCaptor<DICommPortListener> portListenerCaptor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        mockStatic(SecureStorageUtility.class);
        mockStatic(LanTransportContext.class);
        subject = new DeviceFriendlyNameFetcher(mockAppliance, mockSecureStorageUtility);
        when(mockAppliance.getDevicePort()).thenReturn(mockDevicePort);
    }

    @Test
    public void itShouldAddListenerToDevicePortWhenFetchIsCalled() throws Exception {
        subject.fetchFriendlyName();

        verify(mockDevicePort).addPortListener(any(DICommPortListener.class));
    }

    @Test
    public void itShouldGetTheFriendlyNameFromDevicePortWhenFetchIsCalled() throws Exception {
        subject.fetchFriendlyName();

        verify(mockDevicePort).reloadProperties();
    }

    @Test
    public void itShouldForwardSuccessToCallbackWhenGetPropsIsSuccess() throws Exception {
        when(mockDevicePortProperties.getName()).thenReturn("mockFriendlyName");
        when(mockDevicePort.getPortProperties()).thenReturn(mockDevicePortProperties);
        simulateGetPropsSuccess(mockDevicePort, mockCallback);

        verify(mockCallback).onFriendlyNameFetchingSuccess(anyString());
    }

    @Test
    public void itShouldForwardCorrectNameToCallbackWhenGetPropsIsSuccess() throws Exception {
        when(mockDevicePortProperties.getName()).thenReturn("mockFriendlyName");
        when(mockDevicePort.getPortProperties()).thenReturn(mockDevicePortProperties);
        simulateGetPropsSuccess(mockDevicePort, mockCallback);

        verify(mockCallback).onFriendlyNameFetchingSuccess(eq("mockFriendlyName"));
    }

    @Test
    public void itShouldForwardFailureToCallbackWhenGetPropsIsSuccessAndPortIsNull() throws Exception {
        simulateGetPropsSuccess(null, mockCallback);

        verify(mockCallback).onFriendlyNameFetchingFailed();
    }

    @Test
    public void itShouldNotUpdateSuccessCallbackIfCallbackIsNull() throws Exception {
        simulateGetPropsSuccess(mockDevicePort, null);

        verifyZeroInteractions(mockCallback);
    }

    @Test
    public void itShouldForwardFailureToCallbackWhenGetPropsIsSuccessAndPortPropertiesIsNull() throws Exception {
        when(mockDevicePort.getPortProperties()).thenReturn(null);
        simulateGetPropsSuccess(mockDevicePort, mockCallback);

        verify(mockCallback).onFriendlyNameFetchingFailed();
    }

    @Test
    public void itShouldForwardFailureToCallbackWhenGetPropsIsFailure() throws Exception {
        simulateGetPropsFailed(mockCallback);

        verify(mockCallback).onFriendlyNameFetchingFailed();
    }

    @Test
    public void itShouldNotUpdateFailureCallbackIfCallbackIsNull() throws Exception {
        simulateGetPropsFailed(null);

        verifyZeroInteractions(mockCallback);
    }

    @Test
    public void itShouldClearCallback() throws Exception {
        subject.fetchFriendlyName();

        subject.clear();

        assertNull(subject.getCallback());
    }

    private void simulateGetPropsSuccess(@Nullable DevicePort port, @Nullable DeviceFriendlyNameFetcher.Callback callback) {
        subject.setNameFetcherCallback(callback);
        subject.fetchFriendlyName();
        verify(mockDevicePort).addPortListener(portListenerCaptor.capture());
        portListenerCaptor.getValue().onPortUpdate(port);
    }

    private void simulateGetPropsFailed(@Nullable DeviceFriendlyNameFetcher.Callback callback) {
        subject.setNameFetcherCallback(callback);
        subject.fetchFriendlyName();
        verify(mockDevicePort).addPortListener(portListenerCaptor.capture());
        portListenerCaptor.getValue().onPortError(mockDevicePort, Error.CANNOT_CONNECT, "error!");
    }
}