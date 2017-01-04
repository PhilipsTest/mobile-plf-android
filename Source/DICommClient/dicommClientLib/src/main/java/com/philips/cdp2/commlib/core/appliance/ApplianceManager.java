/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights reserved.
 */
package com.philips.cdp2.commlib.core.appliance;

import android.support.annotation.NonNull;

import com.philips.cdp.dicommclient.appliance.DICommApplianceFactory;
import com.philips.cdp.dicommclient.networknode.NetworkNode;
import com.philips.cdp2.commlib.core.discovery.DiscoveryStrategy;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * The type ApplianceManager.
 * <p>
 * Acts as a facade between an application and multiple {@link DiscoveryStrategy}s.
 * Any observer subscribed to an instance of this type is notified of events such as
 * when an appliance is found or updated, or whenever an error occurs while performing discovery.
 * <p>
 * The application should subscribe to notifications using the {@link ApplianceListener} interface.
 * It's also possible to just obtain the set of available appliances using {@link #getAvailableAppliances()}
 */
public class ApplianceManager {

    public interface ApplianceListener<A extends Appliance> {
        void onApplianceFound(@NonNull A foundAppliance);

        void onApplianceUpdated(@NonNull A updatedAppliance);
    }

    private final DICommApplianceFactory applianceFactory;

    private final Set<ApplianceListener> applianceListeners = new CopyOnWriteArraySet<>();
    private Set<Appliance> availableAppliances = new CopyOnWriteArraySet<>();

    private final DiscoveryStrategy.DiscoveryListener discoveryListener = new DiscoveryStrategy.DiscoveryListener() {
        @Override
        public void onDiscoveryStarted() {
        }

        @Override
        public void onNetworkNodeDiscovered(NetworkNode networkNode) {
            final Appliance appliance = createAppliance(networkNode);

            if (appliance == null) {
                return;
            }
            availableAppliances.add(appliance);

            notifyApplianceFound(appliance);
        }

        @Override
        public void onNetworkNodeLost(NetworkNode networkNode) {
            // TODO find Appliance and remove from availableAppliances, notify

            final Appliance appliance = createAppliance(networkNode);
            availableAppliances.remove(appliance);

            // FIXME Remove appliance from availableAppliances using the NetworkNode's cppId.
        }

        @Override
        public void onNetworkNodeUpdated(NetworkNode networkNode) {
            // TODO find Appliance and update in availableAppliances, notify

            for (Appliance appliance : availableAppliances) {
                if (networkNode.getCppId().equals(appliance.getNetworkNode().getCppId())) {
                    // TODO Perform merge/update
                    break;
                }
            }
        }

        @Override
        public void onDiscoveryStopped() {
        }
    };

    /**
     * Instantiates a new ApplianceManager.
     *
     * @param discoveryStrategies the discovery strategies
     * @param applianceFactory    the appliance factory
     */
    public ApplianceManager(@NonNull Set<DiscoveryStrategy> discoveryStrategies, @NonNull DICommApplianceFactory applianceFactory) {
        if (discoveryStrategies.isEmpty()) {
            throw new IllegalArgumentException("This class needs to be constructed with at least one discovery strategy.");
        }
        for (DiscoveryStrategy strategy : discoveryStrategies) {
            strategy.addDiscoveryListener(discoveryListener);
        }

        if (applianceFactory == null) {
            throw new IllegalArgumentException("This class needs to be constructed with a non-null appliance factory.");
        }
        this.applianceFactory = applianceFactory;

        loadAppliancesFromPersistentStorage();
    }

    /**
     * Gets available appliances.
     *
     * @return The currently available appliances
     */
    public Set<? extends Appliance> getAvailableAppliances() {
        return availableAppliances;
    }

    /**
     * Store an appliance.
     *
     * @param <A>       the appliance type parameter
     * @param appliance the appliance
     */
    public <A extends Appliance> void storeAppliance(@NonNull A appliance) {
        // TODO store, notify availableAppliances
    }

    /**
     * Add an appliance listener.
     *
     * @param applianceListener the listener
     * @return true, if the listener didn't exist yet and was therefore added
     */
    public boolean addApplianceListener(@NonNull ApplianceListener applianceListener) {
        return applianceListeners.add(applianceListener);
    }

    /**
     * Remove an appliance listener.
     *
     * @param applianceListener the listener
     * @return true, if the listener was present and therefore removed
     */
    public boolean removeApplianceListener(@NonNull ApplianceListener applianceListener) {
        return applianceListeners.remove(applianceListener);
    }

    private void loadAppliancesFromPersistentStorage() {
        // TODO implement
    }

    private Appliance createAppliance(NetworkNode networkNode) {
        if (this.applianceFactory.canCreateApplianceForNode(networkNode)) {
            return (Appliance) applianceFactory.createApplianceForNode(networkNode);
        }
        return null;
    }

    private <A extends Appliance> void notifyApplianceFound(@NonNull A appliance) {
        for (ApplianceListener listener : applianceListeners) {
            listener.onApplianceFound(appliance);
        }
    }
}
