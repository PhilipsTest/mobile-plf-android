/**
 * Copyright (c) Koninklijke Philips N.V., 2017.
 * All rights reserved.
 */
package com.philips.platform.ews.microapp;

import android.content.Context;
import android.content.Intent;

import com.philips.cdp2.commlib.core.CommCentral;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;
import com.philips.platform.ews.injections.DaggerEWSComponent;
import com.philips.platform.ews.injections.EWSComponent;
import com.philips.platform.ews.logger.EWSLogger;
import com.philips.platform.ews.navigation.Navigator;
import com.philips.platform.ews.tagging.EWSTagger;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.uappinput.UappLaunchInput;
import com.philips.platform.uappframework.uappinput.UappSettings;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({EWSLogger.class, CommCentral.class, DaggerEWSComponent.class})
public class EWSUappTest {

    @Mock
    private
    LoggingInterface mockLoggingInterface;
    @Mock
    private
    AppTaggingInterface mockAppTaggingInterface;
    @Rule
    private ExpectedException thrownException = ExpectedException.none();
    private EWSUapp subject;
    @Mock
    private AppInfraInterface appInfraInterfaceMock;
    @Mock
    private EWSDependencies ewsDependenciesMock;
    @Mock
    private UappSettings uappSettingsMock;
    @Mock
    private FragmentLauncher fragmentLauncherMock;
    @Mock
    private ActivityLauncher activityLauncherMock;
    @Mock
    private Context contextMock;
    @Mock
    private Navigator mockNavigator;
    @Mock
    private Map<String, String> productKeyMap;
    @Mock
    private EWSComponent mockEwsComponent;
    @Mock
    private EWSTagger mockEWSTagger;
    @Mock
    private EWSLogger mockEWSLogger;

    @Mock
    DaggerEWSComponent.Builder mockDaggerEWSComponentBuilder;

    @Before
    public void setUp() throws Exception {
        mockStatic(EWSLogger.class);
        mockStatic(CommCentral.class);
        mockStatic(DaggerEWSComponent.class);
        initMocks(this);
        subject = spy(new EWSUapp());
        productKeyMap = new HashMap<>();
        productKeyMap.put(EWSUapp.PRODUCT_NAME, "product");


    }


    @Test
    public void itShouldLaunchEWSActivityIfLauncherConfigurationIsValid() throws Exception {
        initEWS();
        subject.launch(activityLauncherMock, new EWSLauncherInput());
        verify(contextMock).startActivity(isA(Intent.class));
    }

    private void initEWS() {
        when(ewsDependenciesMock.getAppInfra()).thenReturn(appInfraInterfaceMock);
        when(ewsDependenciesMock.getAppInfra().getLogging()).thenReturn(mockLoggingInterface);
        when(ewsDependenciesMock.getAppInfra().getTagging()).thenReturn(mockAppTaggingInterface);
        when(ewsDependenciesMock.getProductKeyMap()).thenReturn(productKeyMap);
        when(uappSettingsMock.getContext()).thenReturn(contextMock);
        subject.init(ewsDependenciesMock, uappSettingsMock);
    }
}