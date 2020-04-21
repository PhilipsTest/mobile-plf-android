/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.prx.serviceDiscovery;

public class AssetServiceDiscoveryRequest extends ServiceDiscoveryRequest {

    private static final String PRXAssetAssetServiceID = "prxclient.assets";

    public AssetServiceDiscoveryRequest(String ctn) {
        super(ctn, PRXAssetAssetServiceID);
    }
}
