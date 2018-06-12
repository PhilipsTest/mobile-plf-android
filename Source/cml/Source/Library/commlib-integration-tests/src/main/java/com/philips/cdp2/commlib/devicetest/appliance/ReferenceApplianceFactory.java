/*
 * Copyright (c) 2015-2017 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.cdp2.commlib.devicetest.appliance;

import android.support.annotation.NonNull;
import com.philips.cdp.dicommclient.networknode.NetworkNode;
import com.philips.cdp2.commlib.ble.context.BleTransportContext;
import com.philips.cdp2.commlib.cloud.context.CloudTransportContext;
import com.philips.cdp2.commlib.core.appliance.Appliance;
import com.philips.cdp2.commlib.core.appliance.ApplianceFactory;
import com.philips.cdp2.commlib.devicetest.CombinedCommunicationTestingStrategy;
import com.philips.cdp2.commlib.lan.context.LanTransportContext;

public final class ReferenceApplianceFactory implements ApplianceFactory {
    @NonNull
    private final BleTransportContext bleTransportContext;
    @NonNull
    private final LanTransportContext lanTransportContext;
    @NonNull
    private final CloudTransportContext cloudTransportContext;

    public ReferenceApplianceFactory(@NonNull BleTransportContext bleTransportContext,
                                     @NonNull LanTransportContext lanTransportContext,
                                     @NonNull CloudTransportContext cloudTransportContext) {
        this.bleTransportContext = bleTransportContext;
        this.lanTransportContext = lanTransportContext;
        this.cloudTransportContext = cloudTransportContext;
    }

    @Override
    public boolean canCreateApplianceForNode(@NonNull NetworkNode networkNode) {
        return networkNode.isValid();
    }

    @Override
    public Appliance createApplianceForNode(@NonNull NetworkNode networkNode) {
        if (canCreateApplianceForNode(networkNode)) {

            final CombinedCommunicationTestingStrategy communicationStrategy = new CombinedCommunicationTestingStrategy(
                    bleTransportContext.createCommunicationStrategyFor(networkNode),
                    lanTransportContext.createCommunicationStrategyFor(networkNode),
                    cloudTransportContext.createCommunicationStrategyFor(networkNode));

            switch (networkNode.getDeviceType()) {
                case BleReferenceAppliance.DEVICETYPE:
                    return new BleReferenceAppliance(networkNode, communicationStrategy);
                case WifiReferenceAppliance.DEVICETYPE:
                    return new WifiReferenceAppliance(networkNode, communicationStrategy);
                default:
                    return new Appliance(networkNode, communicationStrategy) {
                        @Override
                        public String getDeviceType() {
                            return null;
                        }
                    };
            }
        }
        return null;
    }
}
