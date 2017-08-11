/*
 * Copyright (c) 2015-2017 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.cdp.dicommclientsample;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.philips.cdp.dicommclient.appliance.DICommApplianceFactory;
import com.philips.cdp.dicommclient.networknode.NetworkNode;
import com.philips.cdp.dicommclientsample.appliance.airpurifier.AirPurifier;
import com.philips.cdp.dicommclientsample.appliance.airpurifier.ComfortAirPurifier;
import com.philips.cdp.dicommclientsample.appliance.airpurifier.JaguarAirPurifier;
import com.philips.cdp.dicommclientsample.appliance.reference.WifiReferenceAppliance;
import com.philips.cdp2.commlib.cloud.context.CloudTransportContext;
import com.philips.cdp2.commlib.core.appliance.Appliance;
import com.philips.cdp2.commlib.core.communication.CombinedCommunicationStrategy;
import com.philips.cdp2.commlib.core.communication.CommunicationStrategy;
import com.philips.cdp2.commlib.lan.context.LanTransportContext;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

class SampleApplianceFactory implements DICommApplianceFactory<Appliance> {

    @NonNull
    private final LanTransportContext lanTransportContext;

    @NonNull
    private final CloudTransportContext cloudTransportContext;

    public SampleApplianceFactory(@NonNull final LanTransportContext lanTransportContext, @NonNull final CloudTransportContext cloudTransportContext) {
        this.lanTransportContext = lanTransportContext;
        this.cloudTransportContext = cloudTransportContext;
    }

    @Override
    public boolean canCreateApplianceForNode(NetworkNode networkNode) {
        return getSupportedDeviceTypes().contains(networkNode.getDeviceType());
    }

    @Override
    public Appliance createApplianceForNode(NetworkNode networkNode) {
        if (canCreateApplianceForNode(networkNode)) {
            final CommunicationStrategy communicationStrategy = new CombinedCommunicationStrategy(
                    lanTransportContext.createCommunicationStrategyFor(networkNode),
                    cloudTransportContext.createCommunicationStrategyFor(networkNode));

            switch (networkNode.getDeviceType()) {
                case AirPurifier.DEVICETYPE:
                    networkNode.useLegacyHttp();

                    final String modelId = networkNode.getModelId();

                    if (TextUtils.isEmpty(modelId)) {
                        return new JaguarAirPurifier(networkNode, communicationStrategy);
                    } else if (modelId.equals(ComfortAirPurifier.MODELID)) {
                        return new ComfortAirPurifier(networkNode, communicationStrategy);
                    }
                case WifiReferenceAppliance.DEVICETYPE:
                    return new WifiReferenceAppliance(networkNode, communicationStrategy);
            }
        }
        return null;
    }

    @Override
    public Set<String> getSupportedDeviceTypes() {
        return Collections.unmodifiableSet(new HashSet<String>() {{
            add(AirPurifier.DEVICETYPE);
            add(WifiReferenceAppliance.DEVICETYPE);
        }});
    }
}
