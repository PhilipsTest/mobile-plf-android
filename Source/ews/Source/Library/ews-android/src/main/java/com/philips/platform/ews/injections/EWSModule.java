/*
 * Copyright (c) Koninklijke Philips N.V., 2017.
 * All rights reserved.
 */
package com.philips.platform.ews.injections;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import com.philips.cdp.dicommclient.networknode.NetworkNode;
import com.philips.cdp2.commlib.core.CommCentral;
import com.philips.cdp2.commlib.core.communication.CommunicationStrategy;
import com.philips.cdp2.commlib.core.devicecache.DeviceCache;
import com.philips.cdp2.commlib.core.util.ConnectivityMonitor;
import com.philips.cdp2.commlib.lan.communication.LanCommunicationStrategy;
import com.philips.platform.ews.appliance.EWSGenericAppliance;
import com.philips.platform.ews.communication.DiscoveryHelper;
import com.philips.platform.ews.configuration.BaseContentConfiguration;
import com.philips.platform.ews.configuration.HappyFlowContentConfiguration;
import com.philips.platform.ews.logger.EWSLogger;
import com.philips.platform.ews.navigation.FragmentNavigator;
import com.philips.platform.ews.navigation.Navigator;
import com.philips.platform.ews.permission.PermissionHandler;
import com.philips.platform.ews.settingdeviceinfo.ConnectWithPasswordViewModel;
import com.philips.platform.ews.setupsteps.SecondSetupStepsViewModel;
import com.philips.platform.ews.tagging.EWSTagger;
import com.philips.platform.ews.util.StringProvider;
import com.philips.platform.ews.wifi.WiFiUtil;
import dagger.Module;
import dagger.Provides;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.UUID;
import java.util.concurrent.Executors;


@SuppressWarnings("WeakerAccess")
@Module
public class EWSModule {

    @NonNull
    private final Context context;
    @NonNull
    private final FragmentManager fragmentManager;
    @IdRes
    int parentContainerResourceID;
    @NonNull
    private CommCentral commCentral;

    public EWSModule(@NonNull Context context, @NonNull FragmentManager fragmentManager, @IdRes int parentContainerResourceID, @NonNull CommCentral commCentral) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.parentContainerResourceID = parentContainerResourceID;
        this.commCentral = commCentral;
    }

    @Provides
    WifiManager providesWiFiManager() {
        return (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    @Provides
    @Singleton
    CommCentral provideCommCentral() {
        return commCentral;
    }

    @Provides
    @Named("ews.temporary.appliance")
    EWSGenericAppliance provideTemporaryAppliance() {
        NetworkNode fakeNetworkNode = createFakeNetworkNodeForHotSpot();
        ConnectivityMonitor monitor =
                ConnectivityMonitor.forNetworkTypes(context, ConnectivityManager.TYPE_WIFI);
        DeviceCache deviceCache = createLanCache();
        injectFakeNodeIntoDeviceCache(deviceCache, fakeNetworkNode);
        // We are intentionally not creating the strategy from the transport context!
        CommunicationStrategy communicationStrategy = new LanCommunicationStrategy(fakeNetworkNode, monitor);
        return new EWSGenericAppliance(fakeNetworkNode, communicationStrategy);
    }

    private void injectFakeNodeIntoDeviceCache(@NonNull DeviceCache deviceCache,
                                               @NonNull NetworkNode fakeNetworkNode) {
        deviceCache.add(fakeNetworkNode, new DeviceCache.ExpirationCallback() {
            @Override
            public void onCacheExpired(NetworkNode networkNode) {
                // Do nothing
            }
        }, 300);
        deviceCache.stopTimers();
    }

    private DeviceCache createLanCache() {
        return new DeviceCache(Executors.newSingleThreadScheduledExecutor());
    }

    private NetworkNode createFakeNetworkNodeForHotSpot() {
        String tempEui64 = UUID.randomUUID().toString();
        NetworkNode networkNode = new NetworkNode();
        networkNode.setCppId(tempEui64);
        networkNode.setIpAddress("192.168.1.1");
        networkNode.setBootId(-1);
        networkNode.setName(null);
        return networkNode;
    }

    @Provides
    DiscoveryHelper providesDiscoverHelper() {
        return new DiscoveryHelper(commCentral);
    }

    @Provides
    ConnectWithPasswordViewModel providesSetDeviceConnectViewModel(@NonNull final WiFiUtil wifiUtil,
                                                                   @NonNull final Navigator navigator,
                                                                   @NonNull BaseContentConfiguration baseContentConfiguration,
                                                                   @NonNull StringProvider stringProvider,
                                                                   @NonNull final EWSTagger ewsTagger) {
        return new ConnectWithPasswordViewModel(wifiUtil, navigator,
                baseContentConfiguration, stringProvider, ewsTagger);
    }

    @Provides
    SecondSetupStepsViewModel provideSecondSetupStepsViewModel(
            @NonNull final Navigator navigator,
            @NonNull final PermissionHandler permissionHandler,
            @NonNull HappyFlowContentConfiguration happyFlowContentConfiguration,
            @NonNull StringProvider stringProvider, @NonNull BaseContentConfiguration baseContentConfiguration,
            @NonNull final EWSTagger ewsTagger,
            @NonNull final EWSLogger ewsLogger) {

        return new SecondSetupStepsViewModel(navigator,
                permissionHandler, stringProvider, happyFlowContentConfiguration, baseContentConfiguration, ewsTagger, ewsLogger);
    }


    @Provides
    public Navigator provideNavigator() {
        return new Navigator(new FragmentNavigator(fragmentManager, parentContainerResourceID));
    }

    @Provides
    @Named("mainLooperHandler")
    Handler provideHandlerWithMainLooper() {
        return new Handler(Looper.getMainLooper());
    }
}


