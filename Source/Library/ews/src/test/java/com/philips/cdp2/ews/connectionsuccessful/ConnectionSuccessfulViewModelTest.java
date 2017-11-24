/**
 * Copyright (c) Koninklijke Philips N.V., 2017.
 * All rights reserved.
 */
package com.philips.cdp2.ews.connectionsuccessful;

import com.philips.cdp2.ews.R;
import com.philips.cdp2.ews.common.callbacks.FragmentCallback;
import com.philips.cdp2.ews.configuration.BaseContentConfiguration;
import com.philips.cdp2.ews.microapp.EWSCallbackNotifier;
import com.philips.cdp2.ews.tagging.EWSTagger;
import com.philips.cdp2.ews.util.StringProvider;
import com.philips.cdp2.ews.wifi.WiFiUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({EWSCallbackNotifier.class, EWSTagger.class})

public class ConnectionSuccessfulViewModelTest {

    private ConnectionSuccessfulViewModel subject;

    @Mock
    private FragmentCallback mockFragmentCallback;
    @Mock
    private EWSCallbackNotifier callbackNotifierMock;
    @Mock
    private BaseContentConfiguration mockBaseContentConfig;

    @Mock
    private StringProvider mockStringProvider;
    @Mock
    private WiFiUtil mockWiFiUtil;

    @Before
    public void setup() {
        initMocks(this);
        mockStatic(EWSTagger.class);
        PowerMockito.mockStatic(EWSCallbackNotifier.class);
        subject = new ConnectionSuccessfulViewModel(mockBaseContentConfig, mockStringProvider, mockWiFiUtil);
        when(EWSCallbackNotifier.getInstance()).thenReturn(callbackNotifierMock);
        when(mockBaseContentConfig.getDeviceName()).thenReturn(R.string.ews_device_name_default);
        subject.setFragmentCallback(mockFragmentCallback);
    }

    @Test
    public void itShouldGiveOnSuccessCallbackWhenClickedOnStartButton() throws Exception {
        subject.onStartClicked();

        verify(callbackNotifierMock).onSuccess();
    }

    @Test
    public void itShouldFinishMicroAppWhenOnStartClicked() throws Exception {
        subject.onStartClicked();

        verify(mockFragmentCallback).finishMicroApp();
    }

    @Test
    public void itShouldNotFinishMicroAppWhenOnStartClickedWhenCallbackIsNull() throws Exception {
        subject.setFragmentCallback(null);

        subject.onStartClicked();

        verify(mockFragmentCallback, never()).finishMicroApp();
    }

    @Test
    public void itShouldVerifyTitleForViewModel() throws Exception {
        subject.getTitle(mockBaseContentConfig);
        verify(mockStringProvider).getString(R.string.label_ews_succesful_body,
                mockBaseContentConfig.getDeviceName(),mockWiFiUtil.getHomeWiFiSSD());

    }

    @Test
    public void itShouldVerifyTitleForViewMatches() throws Exception {
        when(mockStringProvider.getString(R.string.label_ews_succesful_body, mockBaseContentConfig.getDeviceName())).thenReturn("device name");
        assertEquals("device name", mockStringProvider.getString(R.string.label_ews_succesful_body,
                mockBaseContentConfig.getDeviceName()));
    }

    @Test
    public void itShouldSendPageNameToAnalytics() throws Exception {
        subject.trackPageName();
        verifyStatic();
        EWSTagger.trackPage("connectionSuccessful");
    }

}