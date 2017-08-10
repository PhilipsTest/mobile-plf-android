/*
 * Copyright (c) 2015-2017 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.cdp2.commlib.core.appliance;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.philips.cdp.dicommclient.appliance.DICommApplianceFactory;
import com.philips.cdp.dicommclient.networknode.NetworkNode;
import com.philips.cdp2.commlib.core.discovery.DiscoveryStrategy;
import com.philips.cdp2.commlib.core.util.Availability.AvailabilityListener;
import com.philips.cdp2.commlib.core.util.HandlerProvider;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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
 *
 * @publicApi
 */
public class ApplianceManager {

    public interface ApplianceListener<A extends Appliance> {
        void onApplianceFound(@NonNull A foundAppliance);

        void onApplianceUpdated(@NonNull A updatedAppliance);

        void onApplianceLost(@NonNull A lostAppliance);
    }

    private final DICommApplianceFactory applianceFactory;

    private final Set<ApplianceListener<Appliance>> applianceListeners = new CopyOnWriteArraySet<>();
    private Map<String, Appliance> availableAppliances = new ConcurrentHashMap<>();

    private final Handler handler = HandlerProvider.createHandler();

    private final AvailabilityListener<Appliance> applianceAvailabilityListener = new AvailabilityListener<Appliance>() {
        @Override
        public void onAvailabilityChanged(@NonNull Appliance appliance) {
            final String cppId = appliance.getNetworkNode().getCppId();
            if (appliance.isAvailable()) {
                if (!availableAppliances.containsKey(cppId)) {
                    availableAppliances.put(cppId, appliance);
                    notifyApplianceFound(appliance);
                }
            } else {
                final Appliance lostAppliance = availableAppliances.remove(cppId);

                if (lostAppliance != null) {
                    lostAppliance.removeAvailabilityListener(this);
                    notifyApplianceLost(lostAppliance);
                }
            }
        }
    };

    private final DiscoveryStrategy.DiscoveryListener discoveryListener = new DiscoveryStrategy.DiscoveryListener() {
        @Override
        public void onDiscoveryStarted() {
        }

        @Override
        public void onNetworkNodeDiscovered(NetworkNode networkNode) {
            if (availableAppliances.containsKey(networkNode.getCppId())) {
                onNetworkNodeUpdated(networkNode);
            } else if (applianceFactory.canCreateApplianceForNode(networkNode)) {
                final Appliance appliance = (Appliance) applianceFactory.createApplianceForNode(networkNode);
                appliance.addAvailabilityListener(applianceAvailabilityListener);
                availableAppliances.put(networkNode.getCppId(), appliance);
                notifyApplianceFound(appliance);
            }
        }

        @Override
        public void onNetworkNodeLost(NetworkNode networkNode) {
            final Appliance appliance = availableAppliances.get(networkNode.getCppId());

            if (appliance != null && !appliance.isAvailable()) {
                notifyApplianceLost(availableAppliances.remove(networkNode.getCppId()));
            }
        }

        @Override
        public void onNetworkNodeUpdated(NetworkNode networkNode) {
            final Appliance appliance = availableAppliances.get(networkNode.getCppId());
            appliance.getNetworkNode().updateWithValuesFrom(networkNode);
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
        this.applianceFactory = applianceFactory;

        loadAppliancesFromPersistentStorage();
    }

    /**
     * Gets available appliances.
     *
     * @return The currently available appliances
     */
    public Set<Appliance> getAvailableAppliances() {
        return new CopyOnWriteArraySet<>(availableAppliances.values());
    }

    /**
     * Find appliance by cpp id.
     *
     * @param cppId the cpp id
     * @return the appliance
     */
    public Appliance findApplianceByCppId(final String cppId) {
        return availableAppliances.get(cppId);
    }

    /**
     * Store an appliance.
     *
     * @param appliance the appliance
     */
    public void storeAppliance(@NonNull Appliance appliance) {
        // TODO
        throw new UnsupportedOperationException("Not implemented yet.");
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
        // TODO
    }

    private <A extends Appliance> void notifyApplianceFound(final @NonNull A appliance) {
        for (final ApplianceListener<Appliance> listener : applianceListeners) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onApplianceFound(appliance);
                }
            });
        }
    }

    private void notifyApplianceUpdated(final @NonNull Appliance appliance) {
        for (final ApplianceListener<Appliance> listener : applianceListeners) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onApplianceUpdated(appliance);
                }
            });
        }
    }

    private void notifyApplianceLost(final @NonNull Appliance appliance) {
        for (final ApplianceListener<Appliance> listener : applianceListeners) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onApplianceLost(appliance);
                }
            });
        }
    }
}
