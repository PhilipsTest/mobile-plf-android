/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.cdp2.commlib.ble.context;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.philips.cdp.dicommclient.networknode.NetworkNode;
import com.philips.cdp2.bluelib.plugindefinition.ReferenceNodeDeviceDefinitionInfo;
import com.philips.cdp2.commlib.ble.communication.BleCommunicationStrategy;
import com.philips.cdp2.commlib.ble.discovery.BleDiscoveryStrategy;
import com.philips.cdp2.commlib.core.communication.CommunicationStrategy;
import com.philips.cdp2.commlib.core.configuration.RuntimeConfiguration;
import com.philips.cdp2.commlib.core.context.TransportContext;
import com.philips.cdp2.commlib.core.devicecache.DeviceCache;
import com.philips.cdp2.commlib.core.discovery.DiscoveryStrategy;
import com.philips.cdp2.commlib.core.exception.TransportUnavailableException;
import com.philips.pins.shinelib.SHNCentral;
import com.philips.pins.shinelib.SHNCentral.SHNCentralListener;
import com.philips.pins.shinelib.exceptions.SHNBluetoothHardwareUnavailableException;
import com.philips.pins.shinelib.tagging.AppInfraTagger;
import com.philips.pins.shinelib.tagging.SHNTagger;
import com.philips.pins.shinelib.utility.LoggingExceptionHandler;
import com.philips.pins.shinelib.utility.SHNLogger;

import static com.philips.pins.shinelib.SHNCentral.State.SHNCentralStateReady;

/**
 * Implementation of a TransportContext for BLE traffic.
 * Handles all communication to an appliance in case it is a BLE appliance.
 *
 * @publicApi
 */
public class BleTransportContext implements TransportContext {

    private final DeviceCache deviceCache;
    private final SHNCentral shnCentral;
    private final DiscoveryStrategy discoveryStrategy;

    private final SHNCentralListener shnCentralListener = new SHNCentralListener() {
        @Override
        public void onStateUpdated(@NonNull SHNCentral shnCentral, @NonNull SHNCentral.State state) {
            if (!(state == SHNCentralStateReady)) {
                discoveryStrategy.clearDiscoveredNetworkNodes();
            }
        }
    };

    /**
     * Instantiates a new BleTransportContext.
     * <p>
     * This constructor implicitly disables the showing of a popup when BLE is turned off.
     * </p>
     *
     * @param runtimeConfiguration the runtime configuration object
     */
    public BleTransportContext(final @NonNull RuntimeConfiguration runtimeConfiguration) {
        this(runtimeConfiguration, false);
    }

    /**
     * Instantiates a new BleTransportContext.
     *
     * @param runtimeConfiguration      the runtime configuration object
     * @param showPopupIfBLEIsTurnedOff show popup if BLE is turned off
     * @throws TransportUnavailableException thrown when the underlying transport is not available
     */
    public BleTransportContext(@NonNull final RuntimeConfiguration runtimeConfiguration, boolean showPopupIfBLEIsTurnedOff) {
        if (runtimeConfiguration.isLogEnabled()) {
            SHNLogger.registerLogger(new SHNLogger.LogCatLogger());
        }

        if(runtimeConfiguration.isTaggingEnabled()) {
            SHNTagger.registerTagger(new AppInfraTagger(runtimeConfiguration.getAppInfraInterface()));
        }

        try {
            this.shnCentral = createCentral(runtimeConfiguration, showPopupIfBLEIsTurnedOff);
        } catch (SHNBluetoothHardwareUnavailableException e) {
            throw new TransportUnavailableException("Bluetooth hardware unavailable.", e);
        }

        shnCentral.registerDeviceDefinition(new ReferenceNodeDeviceDefinitionInfo());
        shnCentral.registerShnCentralListener(shnCentralListener);

        deviceCache = createDeviceCache();
        discoveryStrategy = createDiscoveryStrategy(runtimeConfiguration);
    }

    /**
     * Returns a DiscoveryStrategy for discovering BLE appliances.
     *
     * @return DiscoveryStrategy A discovery strategy to discover BLE appliances.
     * @see TransportContext#getDiscoveryStrategy()
     */
    @Override
    public DiscoveryStrategy getDiscoveryStrategy() {
        return this.discoveryStrategy;
    }

    /**
     * Creates a CommunicationStrategy for communicating with BLE appliances.
     *
     * @param networkNode NetworkNode The network node
     * @return CommunicationStrategy A communication strategy for communicating with BLE appliances.
     * @see TransportContext#createCommunicationStrategyFor(NetworkNode)
     */
    @NonNull
    @Override
    public CommunicationStrategy createCommunicationStrategyFor(@NonNull NetworkNode networkNode) {
        return new BleCommunicationStrategy(shnCentral, networkNode);
    }

    @NonNull
    @VisibleForTesting
    DeviceCache createDeviceCache() {
        return new DeviceCache();
    }

    @VisibleForTesting
    @NonNull
    SHNCentral createCentral(RuntimeConfiguration runtimeConfiguration, boolean showPopupIfBLEIsTurnedOff) throws SHNBluetoothHardwareUnavailableException {
        SHNCentral.Builder builder = new SHNCentral.Builder(runtimeConfiguration.getContext());

        HandlerThread thread = new HandlerThread("CommLibThreadForBlueLib");
        thread.setUncaughtExceptionHandler(new LoggingExceptionHandler());
        thread.start();
        builder.setHandler(new Handler(thread.getLooper()));

        builder.showPopupIfBLEIsTurnedOff(showPopupIfBLEIsTurnedOff);

        return builder.create();
    }

    @NonNull
    @VisibleForTesting
    DiscoveryStrategy createDiscoveryStrategy(@NonNull RuntimeConfiguration runtimeConfiguration) {
        return new BleDiscoveryStrategy(runtimeConfiguration.getContext(), deviceCache, shnCentral.getShnDeviceScanner());
    }
}
