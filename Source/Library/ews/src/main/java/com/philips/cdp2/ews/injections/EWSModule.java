/**
 * Copyright (c) Koninklijke Philips N.V., 2017.
 * All rights reserved.
 */
package com.philips.cdp2.ews.injections;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.philips.cdp.dicommclient.networknode.NetworkNode;
import com.philips.cdp.digitalcare.CcDependencies;
import com.philips.cdp.digitalcare.CcInterface;
import com.philips.cdp.digitalcare.CcLaunchInput;
import com.philips.cdp.digitalcare.CcSettings;
import com.philips.cdp2.commlib.core.communication.CommunicationStrategy;
import com.philips.cdp2.commlib.core.devicecache.DeviceCache;
import com.philips.cdp2.commlib.core.util.ConnectivityMonitor;
import com.philips.cdp2.commlib.lan.LanDeviceCache;
import com.philips.cdp2.commlib.lan.communication.LanCommunicationStrategy;
import com.philips.cdp2.ews.EWSApplication;
import com.philips.cdp2.ews.R;
import com.philips.cdp2.ews.appliance.ApplianceSessionDetailsInfo;
import com.philips.cdp2.ews.appliance.EWSGenericAppliance;
import com.philips.cdp2.ews.communication.ApplianceAccessEventMonitor;
import com.philips.cdp2.ews.communication.DiscoveryHelper;
import com.philips.cdp2.ews.communication.EventingChannel;
import com.philips.cdp2.ews.communication.WiFiEventMonitor;
import com.philips.cdp2.ews.configuration.BaseContentConfiguration;
import com.philips.cdp2.ews.microapp.EWSDependencyProvider;
import com.philips.cdp2.ews.navigation.ActivityNavigator;
import com.philips.cdp2.ews.navigation.FragmentNavigator;
import com.philips.cdp2.ews.navigation.Navigator;
import com.philips.cdp2.ews.navigation.ScreenFlowController;
import com.philips.cdp2.ews.permission.PermissionHandler;
import com.philips.cdp2.ews.settingdeviceinfo.SetDeviceInfoViewModel;
import com.philips.cdp2.ews.util.StringProvider;
import com.philips.cdp2.ews.view.ConnectionEstablishDialogFragment;
import com.philips.cdp2.ews.view.dialog.GPSEnableDialogFragment;
import com.philips.cdp2.ews.viewmodel.BlinkingAccessPointViewModel;
import com.philips.cdp2.ews.viewmodel.EWSPressPlayAndFollowSetupViewModel;
import com.philips.cdp2.ews.viewmodel.ProductSupportViewModel;
import com.philips.cdp2.ews.wifi.WiFiUtil;
import com.philips.platform.uappframework.uappinput.UappDependencies;
import com.philips.platform.uappframework.uappinput.UappSettings;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@SuppressWarnings("WeakerAccess")
@Module
public class EWSModule {

    @NonNull
    private final Context context;
    @NonNull
    private final FragmentManager fragmentManager;

    @NonNull
    private Map<String, Serializable> configurationMap;

    public EWSModule(Context context, @NonNull FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @Provides
    WifiManager providesWiFiManager() {
        return (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    @Singleton
    @Provides
    @Named("ews.event.bus")
    EventBus providesEWSEventBus() {
        return EventBus.builder().build();
    }

    @SuppressWarnings("unchecked")
    @Singleton
    @Provides
    EventingChannel<EventingChannel.ChannelCallback> providesEWSEventingChannel(
            @NonNull final ApplianceAccessEventMonitor applianceAccessEventMonitor,
            @NonNull final WiFiEventMonitor wiFiEventMonitor) {
        return new EventingChannel<>(
                Arrays.<EventingChannel.ChannelCallback>asList(applianceAccessEventMonitor,
                        wiFiEventMonitor));
    }

    @Provides
    @Named("ews.temporary.appliance")
    EWSGenericAppliance provideTemporaryAppliance() {
        NetworkNode fakeNetworkNode = createFakeNetworkNodeForHotSpot();
        ConnectivityMonitor monitor =
                ConnectivityMonitor.forNetworkTypes(context, ConnectivityManager.TYPE_WIFI);
        LanDeviceCache lanDeviceCache = createLanCache();
        injectFakeNodeIntoDeviceCache(lanDeviceCache, fakeNetworkNode);
        // We are intentionally not creating the strategy from the transport context!
        CommunicationStrategy communicationStrategy = new LanCommunicationStrategy(fakeNetworkNode,
                lanDeviceCache, monitor);
        return new EWSGenericAppliance(fakeNetworkNode, communicationStrategy);
    }

    private void injectFakeNodeIntoDeviceCache(@NonNull LanDeviceCache lanDeviceCache,
                                               @NonNull NetworkNode fakeNetworkNode) {
        lanDeviceCache.addNetworkNode(fakeNetworkNode, new DeviceCache.ExpirationCallback() {
            @Override
            public void onCacheExpired(NetworkNode networkNode) {
                // Do nothing
            }
        }, 300);
        lanDeviceCache.stopTimers();
    }

    private LanDeviceCache createLanCache() {
        LanDeviceCache lanDeviceCache =
                new LanDeviceCache(Executors.newSingleThreadScheduledExecutor());

        return lanDeviceCache;
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
        return new DiscoveryHelper(
                ((EWSApplication) context.getApplicationContext()).getCommCentral());
    }


    @Provides
    SetDeviceInfoViewModel providesSetDeviceConnectViewModel(@NonNull final WiFiUtil wifiUtil,
                                                             @NonNull final ApplianceSessionDetailsInfo sessionInfo,
                                                             @NonNull final Navigator navigator,
                                                             @NonNull BaseContentConfiguration baseContentConfiguration,
                                                             @NonNull StringProvider stringProvider) {
        final ConnectionEstablishDialogFragment dialogFragment =
                ConnectionEstablishDialogFragment
                        .getInstance(R.string.label_ews_establishing_connection_body);
        return new SetDeviceInfoViewModel(wifiUtil, sessionInfo, navigator,
                dialogFragment, baseContentConfiguration, stringProvider);
    }

    @Provides
    EWSPressPlayAndFollowSetupViewModel providesEWSPressPlayAndFollowSetupViewModel(
            @NonNull final Navigator navigator,
            @NonNull final @Named("ews.event.bus") EventBus eventBus,
            @NonNull final PermissionHandler permissionHandler) {
        final ConnectionEstablishDialogFragment dialogFragment =
                ConnectionEstablishDialogFragment
                        .getInstance(R.string.label_ews_establishing_connection_body);
        return new EWSPressPlayAndFollowSetupViewModel(navigator, eventBus, permissionHandler,
                dialogFragment,
                null, new GPSEnableDialogFragment(), new Handler(context.getMainLooper()));
    }

    @Provides
    BlinkingAccessPointViewModel providesBlinkingAccessPointViewModel(
            @NonNull final Navigator navigator,
            @NonNull final @Named("ews.event.bus") EventBus eventBus,
            @NonNull final PermissionHandler permissionHandler) {
        final ConnectionEstablishDialogFragment dialogFragment =
                ConnectionEstablishDialogFragment
                        .getInstance(R.string.label_ews_establishing_connection_body);
        return new BlinkingAccessPointViewModel(navigator, eventBus, permissionHandler,
                dialogFragment,
                null, new GPSEnableDialogFragment(), new Handler(context.getMainLooper()));
    }

    @Provides
    ProductSupportViewModel productSupportViewModel(@NonNull final ScreenFlowController screenFlowController) {
        final CcLaunchInput ccLaunchInput = new CcLaunchInput();
        final CcInterface ccInterface = new CcInterface();
        final UappDependencies dependencies =
                new CcDependencies(EWSDependencyProvider.getInstance().getAppInfra());
        final UappSettings settings = new CcSettings(context);

        return new ProductSupportViewModel(ccLaunchInput, ccInterface, dependencies, settings,
                screenFlowController);
    }

    @Provides
    Navigator provideNavigator() {
        return new Navigator(new FragmentNavigator(fragmentManager),
                new ActivityNavigator(context));
    }

    @Provides
    @Named("mainLooperHandler")
    Handler provideHandlerWithMainLooper() {
        return new Handler(Looper.getMainLooper());
    }

}
