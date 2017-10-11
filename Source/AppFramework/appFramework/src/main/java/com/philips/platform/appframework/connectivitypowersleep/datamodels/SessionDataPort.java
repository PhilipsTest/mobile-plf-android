/* Copyright (c) Koninklijke Philips N.V., 2017
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/

package com.philips.platform.appframework.connectivitypowersleep.datamodels;

import com.philips.cdp2.commlib.core.communication.CommunicationStrategy;

public class SessionDataPort extends GenericPort<SessionDataPortProperties> {

    private long sessionNumber;

    public SessionDataPort(final CommunicationStrategy communicationStrategy, String name, int productID, Class<SessionDataPortProperties> propertiesClass) {
        super(communicationStrategy, name, productID, propertiesClass);
    }

    @Override
    public String getDICommPortName() {
        return name + "/" + sessionNumber;
    }

    public void setSpecificSession(long sessionnum) {
        sessionNumber = sessionnum;
    }
}
