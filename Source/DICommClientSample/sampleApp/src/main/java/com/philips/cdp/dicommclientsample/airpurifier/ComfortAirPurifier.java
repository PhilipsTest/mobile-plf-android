/*
 * (C) Koninklijke Philips N.V., 2015, 2016, 2017.
 * All rights reserved.
 */
package com.philips.cdp.dicommclientsample.airpurifier;

import com.philips.commlib.core.communication.CommunicationStrategy;
import com.philips.cdp.dicommclient.networknode.NetworkNode;

public class ComfortAirPurifier extends AirPurifier {

    public static final String MODELNUMBER = "AC2889";

    public ComfortAirPurifier(NetworkNode networkNode, CommunicationStrategy communicationStrategy) {
        super(networkNode, communicationStrategy);

        airPort = new ComfortAirPort(communicationStrategy);
        addPort(airPort);
    }
}
