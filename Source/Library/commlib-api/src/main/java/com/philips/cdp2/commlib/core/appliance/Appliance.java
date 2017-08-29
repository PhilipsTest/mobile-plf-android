/*
 * Copyright (c) 2015-2017 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.cdp2.commlib.core.appliance;

import android.support.annotation.NonNull;

import com.philips.cdp.dicommclient.networknode.NetworkNode;
import com.philips.cdp.dicommclient.port.DICommPort;
import com.philips.cdp.dicommclient.port.DICommPortListener;
import com.philips.cdp.dicommclient.port.common.DevicePort;
import com.philips.cdp.dicommclient.port.common.PairingPort;
import com.philips.cdp.dicommclient.port.common.WifiPort;
import com.philips.cdp.dicommclient.port.common.WifiUIPort;
import com.philips.cdp.dicommclient.subscription.SubscriptionEventListener;
import com.philips.cdp.dicommclient.util.DICommLog;
import com.philips.cdp2.commlib.core.communication.CombinedCommunicationStrategy;
import com.philips.cdp2.commlib.core.communication.CommunicationStrategy;
import com.philips.cdp2.commlib.core.port.firmware.FirmwarePort;
import com.philips.cdp2.commlib.core.util.Availability;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static java.util.Objects.requireNonNull;

/**
 * {@linkplain Appliance} represents a physical appliance/peripheral/product that implements the DiComm communication protocol.
 *
 * @publicApi
 */
public abstract class Appliance implements Availability<Appliance> {

    protected final NetworkNode networkNode;

    private final DevicePort devicePort;
    private final FirmwarePort firmwarePort;
    private final PairingPort pairingPort;
    private final WifiPort wifiPort;
    private final WifiUIPort wifiUIPort;

    protected final CommunicationStrategy communicationStrategy;

    private final Set<DICommPort> ports = new HashSet<>();

    // TODO expose
    private final Set<AvailabilityListener<Appliance>> availabilityListeners = new CopyOnWriteArraySet<>();

    private SubscriptionEventListener subscriptionEventListener = new SubscriptionEventListener() {

        @Override
        public void onSubscriptionEventReceived(String data) {
            DICommLog.d(DICommLog.APPLIANCE, "Notify subscription listeners - " + data);

            for (DICommPort port : getAllPorts()) {
                if (port.isResponseForThisPort(data)) {
                    port.handleResponse(data);
                }
            }
        }
    };

    /**
     * Create an appliance.
     * <p>
     * Appliances are usually created inside the {@link ApplianceFactory}. If multiple
     * <code>CommunicationStrategies</code> are provided, they are wrapped with a {@link CombinedCommunicationStrategy}.
     *
     * @param networkNode             The {@link NetworkNode} this <code>Appliance</code> is discovered with.
     * @param communicationStrategies One or multiple <code>CommunicationStrategies</code> used to communicate with the Appliance.
     *                                The order in which they are supplied determines the order in which communication is attemped.
     * @see ApplianceFactory
     */
    public Appliance(final @NonNull NetworkNode networkNode, final @NonNull CommunicationStrategy... communicationStrategies) {
        this.networkNode = requireNonNull(networkNode);

        if (communicationStrategies.length == 1) {
            this.communicationStrategy = communicationStrategies[0];
        } else if (communicationStrategies.length == 0) {
            throw new IllegalArgumentException("Need at least one CommunicationStrategy");
        } else {
            this.communicationStrategy = new CombinedCommunicationStrategy(communicationStrategies);
        }
        this.communicationStrategy.addSubscriptionEventListener(subscriptionEventListener);

        devicePort = new DevicePort(this.communicationStrategy);
        firmwarePort = new FirmwarePort(this.communicationStrategy);
        pairingPort = new PairingPort(this.communicationStrategy);
        wifiPort = new WifiPort(this.communicationStrategy);
        wifiUIPort = new WifiUIPort(this.communicationStrategy);

        addPort(devicePort);
        addPort(firmwarePort);
        addPort(pairingPort);
        addPort(wifiPort);
        addPort(wifiUIPort);
    }

    /**
     * @return the device type used by CPP to identify this appliance
     */
    public abstract String getDeviceType();

    /**
     * Gets the {@link NetworkNode} node representing this {@linkplain Appliance}.
     *
     * @return the network node that is associated with this appliance
     * @see NetworkNode
     */
    public NetworkNode getNetworkNode() {
        return networkNode;
    }

    protected void addPort(final @NonNull DICommPort port) {
        ports.add(port);
    }

    /**
     * Subscribe on all ports to get notified on changes to properties.
     */
    public void subscribe() {
        DICommLog.i(DICommLog.APPLIANCE, "Subscribe to all ports for appliance: " + toString());

        for (DICommPort port : ports) {
            if (port.supportsSubscription()) {
                port.subscribe();
            }
        }
    }

    /**
     * Unsubscribe on all ports.
     */
    public void unsubscribe() {
        DICommLog.i(DICommLog.APPLIANCE, "Unsubscribe from all ports for appliance: " + toString());

        for (DICommPort port : ports) {
            if (port.supportsSubscription()) {
                port.unsubscribe();
            }
        }
    }

    /**
     * Stop resubscribe.
     * <p>
     * Prevents subscriptions to automatically renew after they expire.
     */
    public void stopResubscribe() {
        DICommLog.i(DICommLog.APPLIANCE, "Stop resubscribing to all ports for appliance: " + toString());

        for (DICommPort port : ports) {
            if (port.supportsSubscription()) {
                port.stopResubscribe();
            }
        }
    }

    public DevicePort getDevicePort() {
        return devicePort;
    }

    public FirmwarePort getFirmwarePort() {
        return firmwarePort;
    }

    public PairingPort getPairingPort() {
        return pairingPort;
    }

    public WifiPort getWifiPort() {
        return wifiPort;
    }

    public WifiUIPort getWifiUIPort() {
        return wifiUIPort;
    }

    public Set<DICommPort> getAllPorts() {
        return ports;
    }

    public synchronized String getName() {
        return getNetworkNode().getName();
    }

    public void addListenerForAllPorts(final @NonNull DICommPortListener portListener) {
        for (DICommPort port : getAllPorts()) {
            port.addPortListener(portListener);
        }
    }

    public void removeListenerForAllPorts(final @NonNull DICommPortListener portListener) {
        for (DICommPort port : getAllPorts()) {
            port.removePortListener(portListener);
        }
    }

    /**
     * Currently only used to switch BLE fast/slow functionality.
     */
    @Deprecated
    public void enableCommunication() {
        communicationStrategy.enableCommunication();
    }

    /**
     * Currently only used to switch BLE fast/slow functionality.
     */
    @Deprecated
    public void disableCommunication() {
        communicationStrategy.disableCommunication();
    }

    /**
     * Indicates that this Appliance is available for communication.
     *
     * @return true, if communication to this Appliance is possible
     */
    public boolean isAvailable() {
        return communicationStrategy.isAvailable();
    }

    @Override
    public void addAvailabilityListener(@NonNull final AvailabilityListener<Appliance> listener) {
        communicationStrategy.addAvailabilityListener(new AvailabilityListener<CommunicationStrategy>() {
            @Override
            public void onAvailabilityChanged(@NonNull CommunicationStrategy communicationStrategy) {
                listener.onAvailabilityChanged(Appliance.this);
            }
        });
    }

    @Override
    public void removeAvailabilityListener(@NonNull final AvailabilityListener<Appliance> listener) {
        communicationStrategy.removeAvailabilityListener(new AvailabilityListener<CommunicationStrategy>() {
            @Override
            public void onAvailabilityChanged(@NonNull CommunicationStrategy communicationStrategy) {
                listener.onAvailabilityChanged(Appliance.this);
            }
        });
    }

    @Override
    public String toString() {
        return "name: " + getName() +
                "   ip: " + getNetworkNode().getIpAddress() +
                "   eui64: " + getNetworkNode().getCppId() +
                "   bootId: " + getNetworkNode().getBootId() +
                "   paired: " + getNetworkNode().getPairedState() +
                "   homeSsid: " + getNetworkNode().getHomeSsid();
    }

    @Override
    public int hashCode() {
        return networkNode.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (!(other instanceof Appliance)) {
            return false;
        }
        return networkNode.equals(((Appliance) other).getNetworkNode());
    }
}
