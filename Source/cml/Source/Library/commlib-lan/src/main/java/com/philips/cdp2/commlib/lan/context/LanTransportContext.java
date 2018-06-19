/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.cdp2.commlib.lan.context;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import com.philips.cdp.dicommclient.networknode.NetworkNode;
import com.philips.cdp.dicommclient.util.DICommLog;
import com.philips.cdp2.commlib.core.appliance.Appliance;
import com.philips.cdp2.commlib.core.appliance.ApplianceManager;
import com.philips.cdp2.commlib.core.communication.CommunicationStrategy;
import com.philips.cdp2.commlib.core.configuration.RuntimeConfiguration;
import com.philips.cdp2.commlib.core.context.TransportContext;
import com.philips.cdp2.commlib.core.devicecache.DeviceCache;
import com.philips.cdp2.commlib.core.discovery.DiscoveryStrategy;
import com.philips.cdp2.commlib.core.util.ConnectivityMonitor;
import com.philips.cdp2.commlib.lan.communication.LanCommunicationStrategy;
import com.philips.cdp2.commlib.lan.discovery.LanDiscoveryStrategy;
import com.philips.cdp2.commlib.lan.security.PublicKeyPin;
import com.philips.cdp2.commlib.lan.util.SsidProvider;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Executors;

import static android.net.ConnectivityManager.TYPE_WIFI;

/**
 * Implementation of the TransportContext for local network traffic.
 * Handles all communication to an appliance in case it's a local Wi-Fi appliance.
 *
 * @publicApi
 */
public class LanTransportContext implements TransportContext {

    private static final String TAG = "LanTransportContext";

    private final DeviceCache deviceCache;
    @NonNull
    private final DiscoveryStrategy discoveryStrategy;
    @NonNull
    private final ConnectivityMonitor connectivityMonitor;
    @NonNull
    private final SsidProvider ssidProvider;

    /**
     * Instantiates a LanTransportContext.
     *
     * @param runtimeConfiguration RuntimeConfiguration Configuration to be used by CommLib
     */
    public LanTransportContext(@NonNull final RuntimeConfiguration runtimeConfiguration) {
        this.connectivityMonitor = ConnectivityMonitor.forNetworkTypes(runtimeConfiguration.getContext(), TYPE_WIFI);
        this.ssidProvider = new SsidProvider(runtimeConfiguration.getContext());
        this.deviceCache = new DeviceCache(Executors.newSingleThreadScheduledExecutor());
        this.discoveryStrategy = createLanDiscoveryStrategy();
    }

    @VisibleForTesting
    @NonNull
    DiscoveryStrategy createLanDiscoveryStrategy() {
        return new LanDiscoveryStrategy(deviceCache, connectivityMonitor, ssidProvider);
    }

    /**
     * Returns a DiscoveryStrategy for local discovery.
     *
     * @return DiscoveryStrategy A discovery strategy to discover appliances in the local network.
     */
    @Override
    @NonNull
    public DiscoveryStrategy getDiscoveryStrategy() {
        return this.discoveryStrategy;
    }

    /**
     * Creates a CommunicationStrategy for communicating within a local network.
     *
     * @param networkNode NetworkNode the network node
     * @return CommunicationStrategy A communication strategy for communicating with an appliance in the local network.
     */
    @Override
    @NonNull
    public CommunicationStrategy createCommunicationStrategyFor(@NonNull NetworkNode networkNode) {
        return new LanCommunicationStrategy(networkNode, connectivityMonitor, ssidProvider);
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

        acceptPinFor(appliance, networkNode.getMismatchedPin());
    }

    /**
     * Accept supplied pin for appliance.
     * <p>
     * The currently stored pin will be overwritten with the
     * supplied pin and the mismatched pin will be cleared.
     * </p>
     *
     * @param appliance the appliance
     * @param pin       the pin, may be null to reset any stored pin
     * @throws IllegalArgumentException when supplied pin cannot be parsed into valid {@link PublicKeyPin}
     */
    @SuppressWarnings("WeakerAccess")
    public static void acceptPinFor(final @NonNull Appliance appliance, final @Nullable String pin) throws IllegalArgumentException {
        final NetworkNode networkNode = appliance.getNetworkNode();

        if (pin != null) {
            new PublicKeyPin(pin);
        }

        networkNode.setPin(pin);
        networkNode.setMismatchedPin(null);

        DICommLog.i(TAG, String.format(Locale.US, "Re-pinned appliance with cppid [%s]", networkNode.getCppId()));
    }

    /**
     * Read pin from appliance.
     *
     * @param appliance the appliance
     * @return the current pin
     */
    public static String readPin(final @NonNull Appliance appliance) {
        return appliance.getNetworkNode().getPin();
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
