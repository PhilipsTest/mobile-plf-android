/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.prx.serviceDiscovery;

public class DisclaimerServiceDiscoveryRequest extends ServiceDiscoveryRequest {

    private static final String PRXDisclaimerDataServiceID = "prxclient.disclaimers";

    public DisclaimerServiceDiscoveryRequest(String ctn) {
        super(ctn, PRXDisclaimerDataServiceID);
    }
}
