/*
 * Copyright (c) Koninklijke Philips N.V., 2017.
 * All rights reserved.
 */
package com.philips.cdp2.ews.viewmodel;

import android.databinding.Observable;

import com.philips.cdp2.ews.navigation.ScreenFlowController;
import com.philips.cdp2.ews.view.EWSDevicePowerOnFragment;
import com.philips.cdp2.ews.view.TroubleshootHomeWiFiFragment;
import com.philips.cdp2.ews.wifi.WiFiUtil;
import com.philips.cdp2.powersleep.BR;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EWSHomeWifiDisplayViewModelTest {

    @Mock
    private WiFiUtil wifiUtilMock;
    @Mock
    private ScreenFlowController screenFlowControllerMock;

    private EWSHomeWifiDisplayViewModel viewModel;
    @Mock
    private Observable.OnPropertyChangedCallback callbackListenerMock;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        viewModel = new EWSHomeWifiDisplayViewModel(screenFlowControllerMock, wifiUtilMock);
    }

    @Test
    public void shouldGetHomeWiFiSSIDWhenAsked() throws Exception {
        String ssid = "brighteyes";

        when(wifiUtilMock.getConnectedWiFiSSID()).thenReturn(ssid);

        assertEquals(ssid, viewModel.getHomeWiFiSSID());
    }

    @Test
    public void shouldShowDevicePowerOnScreenWhenClickedOnYesButton() throws Exception {
        viewModel.onYesButtonClicked();

        verify(screenFlowControllerMock).showFragment(isA(EWSDevicePowerOnFragment.class));
    }

    @Test
    public void shouldShowNetworkTroubleShootingScreenOnNoButtonClicked() throws Exception {
        viewModel.setHierarchyLevel(1);
        viewModel.onNoButtonClicked();

        verify(screenFlowControllerMock).showFragment(isA(TroubleshootHomeWiFiFragment.class));
    }

    @Test
    public void shouldNotifyPropertyChangedWhenHomeWiFiSSIDIsCalled() throws Exception {
        viewModel.addOnPropertyChangedCallback(callbackListenerMock);
        viewModel.updateHomeWiFiSSID();

        verify(callbackListenerMock).onPropertyChanged(viewModel, BR.homeWiFiSSID);
    }
}