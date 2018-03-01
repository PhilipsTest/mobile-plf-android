/*
 * Copyright (c) 2017 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.mya.settings;

import android.content.Context;
import android.os.Bundle;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.rest.RestInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;
import com.philips.platform.mya.MyaHelper;
import com.philips.platform.mya.R;
import com.philips.platform.mya.launcher.MyaDependencies;
import com.philips.platform.mya.launcher.MyaInterface;
import com.philips.platform.mya.launcher.MyaLaunchInput;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.uappinput.UappSettings;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MyaSettingsPresenterTest {

    private MyaSettingsContract.View view;
    private Context context;
    private MyaSettingsPresenter myaSettingsPresenter;
    private MyaInterface myaInterface;

    @Before
    public void setup() {
        view = mock(MyaSettingsContract.View.class);
        context = mock(Context.class);
        when(view.getContext()).thenReturn(context);
        myaSettingsPresenter = new MyaSettingsPresenter(view);
        MyaSettingsFragment myaSettingsFragment = new MyaSettingsFragment();
        myaSettingsPresenter.onViewActive(myaSettingsFragment);
        myaSettingsPresenter.onViewInactive();
        assertNull(myaSettingsPresenter.getView());
    }

    @Test
    public void testGettingSettingItems() {
        AppInfraInterface appInfraInterface = mock(AppInfraInterface.class);
        when(context.getString(R.string.MYA_Country)).thenReturn("some country");
        when(context.getString(R.string.MYA_change_country_message)).thenReturn("some message");
        ArrayList arrayList = new ArrayList();
        AppConfigurationInterface.AppConfigurationError error = new AppConfigurationInterface.AppConfigurationError();
        AppConfigurationInterface appConfigurationInterface = mock(AppConfigurationInterface.class);
        ServiceDiscoveryInterface serviceDiscoveryInterface = mock(ServiceDiscoveryInterface.class);
        when(serviceDiscoveryInterface.getHomeCountry()).thenReturn("some country");
        when(appConfigurationInterface.getPropertyForKey("settings.menuItems", "mya", error)).thenReturn(arrayList);
        when(appInfraInterface.getConfigInterface()).thenReturn(appConfigurationInterface);
        when(appInfraInterface.getServiceDiscovery()).thenReturn(serviceDiscoveryInterface);
        MyaHelper.getInstance().setMyaLaunchInput(new MyaLaunchInput(context,null));
        myaSettingsPresenter.getSettingItems(appInfraInterface, error);
        verify(view).showSettingsItems(ArgumentMatchers.<String, SettingsModel>anyMap());
    }

    @Test
    public void testHandleOnClickSettingsItem() {
        MyaDependencies mockDependencies = mock(MyaDependencies.class);
        AppInfraInterface mockAppInfra = mock(AppInfraInterface.class);
        when(mockDependencies.getAppInfra()).thenReturn(mockAppInfra);
        RestInterface mockRestClient = mock(RestInterface.class);
        when(mockRestClient.isInternetReachable()).thenReturn(true);
        when(mockAppInfra.getRestClient()).thenReturn(mockRestClient);
        LoggingInterface mockLoggingInterface = mock(LoggingInterface.class);
        when(mockAppInfra.getLogging()).thenReturn(mockLoggingInterface);
        AppTaggingInterface appTaggingInterfaceMock = mock(AppTaggingInterface.class);
        when(mockAppInfra.getTagging()).thenReturn(appTaggingInterfaceMock);
        MyaInterface.get().init(mockDependencies, new UappSettings(view.getContext()));
        final FragmentLauncher fragmentLauncher = mock(FragmentLauncher.class);
        myaSettingsPresenter = new MyaSettingsPresenter(view);
        String key = "Mya_Privacy_Settings";
        assertFalse(myaSettingsPresenter.handleOnClickSettingsItem(key, fragmentLauncher));
        key = "MYA_My_details";
        assertFalse(myaSettingsPresenter.handleOnClickSettingsItem(key, fragmentLauncher));
    }

    @Test
    public void testHandleOnClickSettingsWhenDeviceIsOffline() {
        MyaDependencies mockDependencies = mock(MyaDependencies.class);
        AppInfraInterface mockAppInfra = mock(AppInfraInterface.class);
        when(mockDependencies.getAppInfra()).thenReturn(mockAppInfra);
        RestInterface mockRestClient = mock(RestInterface.class);
        LoggingInterface mockLoggingInterface = mock(LoggingInterface.class);
        AppTaggingInterface appTaggingInterfaceMock = mock(AppTaggingInterface.class);
        when(mockRestClient.isInternetReachable()).thenReturn(false);
        when(mockAppInfra.getRestClient()).thenReturn(mockRestClient);
        when(mockAppInfra.getLogging()).thenReturn(mockLoggingInterface);
        when(mockAppInfra.getTagging()).thenReturn(appTaggingInterfaceMock);
        MyaInterface.get().init(mockDependencies, new UappSettings(view.getContext()));
        String testTitle = "Test title";
        when(context.getString(R.string.MYA_Offline_title)).thenReturn(testTitle);
        String testMessage = "Test message";
        when(context.getString(R.string.MYA_Offline_message)).thenReturn(testMessage);
        final FragmentLauncher fragmentLauncher = mock(FragmentLauncher.class);
        myaSettingsPresenter = new MyaSettingsPresenter(view);
        String key = "Mya_Privacy_Settings";
        assertFalse(myaSettingsPresenter.handleOnClickSettingsItem(key, fragmentLauncher));
    }


    private Bundle getArguments() {
        Bundle arguments = new Bundle();
        MyaLaunchInput value = new MyaLaunchInput(context, null);
        String[] profileItems = {"profile1","profile2"};
        value.setProfileMenuList(Arrays.asList(profileItems));
        return arguments;
    }
}