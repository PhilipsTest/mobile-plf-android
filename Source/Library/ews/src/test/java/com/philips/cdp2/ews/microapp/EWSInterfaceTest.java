/**
 * Copyright (c) Koninklijke Philips N.V., 2017.
 * All rights reserved.
 */
package com.philips.cdp2.ews.microapp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.philips.cdp2.commlib.core.CommCentral;
import com.philips.cdp2.ews.communication.EventingChannel;
import com.philips.cdp2.ews.configuration.ContentConfiguration;
import com.philips.cdp2.ews.injections.EWSComponent;
import com.philips.cdp2.ews.logger.EWSLogger;
import com.philips.cdp2.ews.navigation.Navigator;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({EWSDependencyProvider.class, EWSCallbackNotifier.class,EWSLogger.class, CommCentral.class})
public class EWSInterfaceTest {

    @Rule
    private ExpectedException thrownException = ExpectedException.none();

    private EWSInterface subject;

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
    private EventingChannel<EventingChannel.ChannelCallback> mockEWSEventingChannel;

    @Mock
    private Map<String, String> productKeyMap;

    @Mock
    private EWSComponent mockEwsComponent;

    @Mock
    LoggingInterface mockLoggingInterface;

    @Mock
    EWSDependencyProvider mockEWSDependencyProvider;

    @Mock
    private CommCentral mockCommCentral;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mock(EWSDependencyProvider.class);
        PowerMockito.mock(EWSCallbackNotifier.class);
        mockStatic(EWSLogger.class);
        mockStatic(CommCentral.class);
        initMocks(this);

        subject = spy(new EWSInterface());
        productKeyMap = new HashMap<>();
        productKeyMap.put(EWSInterface.PRODUCT_NAME, "product");

        EWSDependencyProvider.instance = spy(new EWSDependencyProvider());

        doReturn(mockEwsComponent).when(EWSDependencyProvider.getInstance()).getEwsComponent();
        doReturn(mockLoggingInterface).when(EWSDependencyProvider.getInstance()).getLoggerInterface();
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                return null;
            }
        }).when(EWSDependencyProvider.getInstance()).createEWSComponent(any(FragmentLauncher.class),any(ContentConfiguration.class));

        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                subject.navigator = mockNavigator;
                subject.ewsEventingChannel = mockEWSEventingChannel;
                return null;
            }
        }).when(mockEwsComponent).inject(any(EWSInterface.class));
    }

    @Test
    public void itShouldEnsureEWSDependenciesAreInitializedWhenOnInitIsCalled() throws Exception {
        verifyStatic();

        EWSDependencyProvider.getInstance().initDependencies(appInfraInterfaceMock, productKeyMap, mockCommCentral);
    }

    @Test
    public void itShouldInitEWSDependenciesWhenOnInitIsCalled() throws Exception {
        initEWS();
        verifyStatic();
        EWSDependencyProvider.getInstance().initDependencies(appInfraInterfaceMock, productKeyMap, mockCommCentral);
    }

    @Test
    public void itShouldLaunchEWSActivityIfLauncherConfigurationIsValid() throws Exception {
        initEWS();
        subject.launch(activityLauncherMock, new EWSLauncherInput());
        verify(contextMock).startActivity(isA(Intent.class));
    }

    @Test
    public void itShouldLaunchEWSAsFragmentIfLauncherConfigurationIsValid() throws Exception{
        initEWS();
        doReturn(mock(FragmentActivity.class,withSettings().extraInterfaces(EWSActionBarListener.class)))
                .when(fragmentLauncherMock).getFragmentActivity();
        subject.launch(fragmentLauncherMock,new EWSLauncherInput());
        verify(subject).launchAsFragment(any(FragmentLauncher.class),any(UappLaunchInput.class));
    }

    @Test
    public void itShouldLaunchEWSAsFragmentIfLauncherConfigurationIsNotValid() throws Exception{
        thrownException.expect(UnsupportedOperationException.class);
        thrownException.expectMessage(EWSInterface.ERROR_MSG_INVALID_IMPLEMENTATION);
        initEWS();
        subject.launch(fragmentLauncherMock,new EWSLauncherInput());
        verify(subject).launchAsFragment(any(FragmentLauncher.class),any(UappLaunchInput.class));
    }

    @Test
    public void itShouldThrowAnErrorIfLauncherConfigurationIsNotValid() throws Exception{
        thrownException.expect(UnsupportedOperationException.class);
        thrownException.expectMessage(EWSInterface.ERROR_MSG_INVALID_CALL);

        subject.launch(fragmentLauncherMock,new EWSLauncherInput());
    }

    @Test
    public void itShouldNavigateToFirstFragmentOnFragmentLauncher() throws  Exception{
        doReturn(mock(FragmentActivity.class)).when(fragmentLauncherMock).getFragmentActivity();
        doReturn(1).when(fragmentLauncherMock).getParentContainerResourceID();
        subject.launchAsFragment(fragmentLauncherMock,new EWSLauncherInput());
        verify(mockNavigator).navigateToGettingStartedScreen();
        verify(mockEWSEventingChannel).start();
    }

    @Test
    public void itShouldVerifyLaunchAsFragmentOnErrorCatchBlockCalled() throws Exception{
        doThrow(new IllegalStateException("error")).when(mockNavigator).navigateToGettingStartedScreen();
        subject.launchAsFragment(fragmentLauncherMock,new EWSLauncherInput());
        verifyStatic();
        EWSLogger.e(anyString(),anyString());
    }

    private void initEWS() {
        when(ewsDependenciesMock.getAppInfra()).thenReturn(appInfraInterfaceMock);
        when(ewsDependenciesMock.getProductKeyMap()).thenReturn(productKeyMap);
        when(uappSettingsMock.getContext()).thenReturn(contextMock);
        subject.init(ewsDependenciesMock, uappSettingsMock);
    }
}