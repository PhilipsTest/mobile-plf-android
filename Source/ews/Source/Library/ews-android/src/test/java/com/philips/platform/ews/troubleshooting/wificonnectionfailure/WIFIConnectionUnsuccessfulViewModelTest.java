/*
 * Copyright (c) Koninklijke Philips N.V., 2017.
 * All rights reserved.
 */
package com.philips.platform.ews.troubleshooting.wificonnectionfailure;

import com.philips.platform.ews.navigation.Navigator;
import com.philips.platform.ews.tagging.EWSTagger;
import com.philips.platform.ews.wifi.WiFiUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(EWSTagger.class)
public class WIFIConnectionUnsuccessfulViewModelTest {

    private WIFIConnectionUnsuccessfulViewModel subject;

    @Mock private Navigator mockNavigator;
    @Mock private EWSTagger ewsTagger;

    @Before
    public void setUp() throws Exception {
        mockStatic(EWSTagger.class);
        initMocks(this);
        subject = new WIFIConnectionUnsuccessfulViewModel(mockNavigator, ewsTagger);
    }

    @Test
    public void itShouldUpdateUpperBody() throws Exception {
        String description = "Awesome description";
        subject.setUpperBodyText(description);
        assertEquals(description, subject.upperBodyText.get());
    }

    @Test
    public void itShouldUpdateStep2() throws Exception {
        String description = "Awesome description";
        subject.setStepTwoText(description);
        assertEquals(description, subject.stepTwoText.get());
    }

    @Test
    public void itShouldUpdateUpperHelper() throws Exception {
        String description = "Awesome description";
        subject.setUpperHelperText(description);
        assertEquals(description, subject.upperHelperText.get());
    }

    @Test
    public void isShouldLowerHelper() throws Exception{
        String note = "Updated Notes";
        subject.setLowerHelperText(note);
        assertEquals(note, subject.lowerHelperText.get());
    }

    @Test
    public void itShouldNavigateToConnectingPhoneToHotspotWifiScreenWhenTryAgainButtonIsClicked() throws Exception {
        subject.onTryAgainClicked();
        verify(mockNavigator).navigateToConnectingPhoneToHotspotWifiScreen();
    }

    @Test
    public void itShouldVerifyTrackPageName() throws Exception {
        subject.trackPageName();
        verify(ewsTagger).trackPage("connectionUnsuccessful");
    }
}