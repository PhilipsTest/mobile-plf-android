/*
 * Copyright (c) 2015-2017 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.cdp2.commlib.lan.context;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.philips.cdp.dicommclient.networknode.NetworkNode;
import com.philips.cdp.dicommclient.util.DICommLog;
import com.philips.cdp2.commlib.core.appliance.Appliance;
import com.philips.cdp2.commlib.core.appliance.ApplianceManager;
import com.philips.cdp2.commlib.core.context.TransportContext;
import com.philips.cdp2.commlib.core.discovery.DiscoveryStrategy;
import com.philips.cdp2.commlib.core.util.ConnectivityMonitor;
import com.philips.cdp2.commlib.lan.LanDeviceCache;
import com.philips.cdp2.commlib.lan.communication.LanCommunicationStrategy;
import com.philips.cdp2.commlib.lan.discovery.LanDiscoveryStrategy;
import com.philips.cdp2.commlib.lan.util.WifiNetworkProvider;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;

import static android.net.ConnectivityManager.TYPE_WIFI;

public class LanTransportContext implements TransportContext<LanTransportContext> {

    private static final String TAG = "LanTransportContext";

    private final LanDeviceCache deviceCache;
    @NonNull
    private final DiscoveryStrategy discoveryStrategy;
    private final ConnectivityMonitor connectivityMonitor;
    private final WifiNetworkProvider wifiNetworkProvider;

    private AvailabilityListener<ConnectivityMonitor> lanAvailabilityListener = new AvailabilityListener<ConnectivityMonitor>() {
        @Override
        public void onAvailabilityChanged(@NonNull ConnectivityMonitor connectivityMonitor) {
            isAvailable = connectivityMonitor.isAvailable();
            notifyAvailabilityListeners();
        }
    };

    private boolean isAvailable;
    private Set<AvailabilityListener<LanTransportContext>> availabilityListeners = new CopyOnWriteArraySet<>();

    public LanTransportContext(@NonNull final Context context) {
        this.connectivityMonitor = ConnectivityMonitor.forNetworkTypes(context, TYPE_WIFI);
        this.connectivityMonitor.addAvailabilityListener(lanAvailabilityListener);

        this.wifiNetworkProvider = WifiNetworkProvider.get(context);

        this.deviceCache = new LanDeviceCache(Executors.newSingleThreadScheduledExecutor());
        this.discoveryStrategy = createLanDiscoveryStrategy();
    }

    @VisibleForTesting
    @NonNull
    DiscoveryStrategy createLanDiscoveryStrategy() {
        return new LanDiscoveryStrategy(deviceCache, connectivityMonitor, wifiNetworkProvider);
    }

    @Override
    @NonNull
    public DiscoveryStrategy getDiscoveryStrategy() {
        return this.discoveryStrategy;
    }

    @Override
    @NonNull
    public LanCommunicationStrategy createCommunicationStrategyFor(@NonNull NetworkNode networkNode) {
        return new LanCommunicationStrategy(networkNode, deviceCache, connectivityMonitor);
    }

    @Override
    public boolean isAvailable() {
        return isAvailable;
    }

    @Override
    public void addAvailabilityListener(@NonNull AvailabilityListener<LanTransportContext> listener) {
        availabilityListeners.add(listener);
        listener.onAvailabilityChanged(this);
    }

    @Override
    public void removeAvailabilityListener(@NonNull AvailabilityListener<LanTransportContext> listener) {
        availabilityListeners.remove(listener);
    }

    private void notifyAvailabilityListeners() {
        for (AvailabilityListener<LanTransportContext> listener : availabilityListeners) {
            listener.onAvailabilityChanged(this);
        }
    }

    /**
     * Reject a new pin for an appliance.
     * <p>
     * When the appliance has a stored pin and a new (mismatching) pin was received,
     * this method rejects that new pin. The currently stored pin will remain untouched.
     * </p>
     *
     * @param appliance the appliance to reject the new pin for
     */
    public static void rejectNewPinFor(final @NonNull Appliance appliance) {
        final NetworkNode networkNode = appliance.getNetworkNode();

        networkNode.setMismatchedPin(null);

        DICommLog.i(TAG, String.format(Locale.US, "Mismatched pin rejected for appliance with cppid [%s]", networkNode.getCppId()));
    }

    /**
     * Accept new pin for an appliance.
     * <p>
     * When the appliance has a stored pin and a new (mismatching) pin was received,
     * this accepts that new pin. The currently stored pin will be overwritten with the
     * new pin and the mismatched pin will be cleared.
     * </p>
     *
     * @param appliance the appliance to accept the new pin for
     */
    public static void acceptNewPinFor(final @NonNull Appliance appliance) {
        final NetworkNode networkNode = appliance.getNetworkNode();

        networkNode.setPin(networkNode.getMismatchedPin());
        networkNode.setMismatchedPin(null);

        DICommLog.i(TAG, String.format(Locale.US, "Re-pinned appliance with cppid [%s]", networkNode.getCppId()));
    }

    /**
     * Find appliances with a mismatched pin.
     *
     * @param <A>        the appliance type parameter
     * @param appliances the appliances to find the appliances with a mismatched pin in, usually retrieved from the {@link ApplianceManager}.
     * @return the set of appliances that have a mismatched pin
     */
    @NonNull
    public static <A extends Appliance> Set<A> findAppliancesWithMismatchedPinIn(final @NonNull Set<A> appliances) {
        Set<A> appliancesWithMismatchedPin = new HashSet<>();

        for (A appliance : appliances) {
            if (appliance.getNetworkNode().getMismatchedPin() != null) {
                appliancesWithMismatchedPin.add(appliance);
            }
        }
        return appliancesWithMismatchedPin;
    }
}
