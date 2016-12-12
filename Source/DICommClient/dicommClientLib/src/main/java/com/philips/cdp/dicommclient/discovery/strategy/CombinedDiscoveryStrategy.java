/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights reserved.
 */
package com.philips.cdp.dicommclient.discovery.strategy;

import android.content.Context;
import android.support.annotation.NonNull;

import com.philips.cdp.dicommclient.discovery.exception.MissingPermissionException;
import com.philips.cdp.dicommclient.networknode.NetworkNode;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class CombinedDiscoveryStrategy implements DiscoveryStrategy, DiscoveryStrategy.DiscoveryListener {

    private final Set<DiscoveryStrategy> strategies = new CopyOnWriteArraySet<>();
    private final Set<DiscoveryListener> discoveryListeners = new CopyOnWriteArraySet<>();

    public CombinedDiscoveryStrategy(Set<DiscoveryStrategy> strategies) {
        final int minimumRequiredNumberOfStrategies = 2;

        if (strategies.size() < minimumRequiredNumberOfStrategies) {
            throw new IllegalArgumentException(String.format(Locale.US, "A minimum of %d discovery strategies is required.", minimumRequiredNumberOfStrategies));
        }

        for (DiscoveryStrategy strategy : strategies) {
            addDiscoveryStrategy(strategy);
        }
    }

    @Override
    public void start(Context context, @NonNull DiscoveryListener listener) throws MissingPermissionException {
        start(context, listener, null);
    }

    @Override
    public void start(Context context, @NonNull DiscoveryListener discoveryListener, Collection<String> deviceTypes) throws MissingPermissionException {
        for (DiscoveryStrategy strategy : strategies) {
            strategy.start(context, this, deviceTypes);
        }
    }

    @Override
    public void stop() {
        for (DiscoveryStrategy strategy : strategies) {
            strategy.stop();
        }
    }

    @Override
    public void onDiscoveryStarted() {
        // TODO
    }

    @Override
    public void onNetworkNodeDiscovered(NetworkNode networkNode) {
        // TODO define combined logic here

        notifyNetworkNodeDiscovered(networkNode);
    }

    @Override
    public void onNetworkNodeLost(NetworkNode networkNode) {
        // TODO define combined logic here

        notifyNetworkNodeLost(networkNode);
    }

    @Override
    public void onNetworkNodeUpdated(NetworkNode networkNode) {
        // TODO define combined logic here

        notifyNetworkNodeUpdated(networkNode);
    }

    @Override
    public void onDiscoveryFinished() {
        // TODO
    }

    private void addDiscoveryStrategy(@NonNull DiscoveryStrategy strategy) {
        strategies.add(strategy);
    }

    private void notifyNetworkNodeDiscovered(@NonNull NetworkNode networkNode) {
        for (DiscoveryListener listener : discoveryListeners) {
            listener.onNetworkNodeDiscovered(networkNode);
        }
    }

    private void notifyNetworkNodeLost(@NonNull NetworkNode networkNode) {
        for (DiscoveryListener listener : discoveryListeners) {
            listener.onNetworkNodeLost(networkNode);
        }
    }

    private void notifyNetworkNodeUpdated(@NonNull NetworkNode networkNode) {
        for (DiscoveryListener listener : discoveryListeners) {
            listener.onNetworkNodeUpdated(networkNode);
        }
    }
}
