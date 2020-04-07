/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.cdp.di.ecs.integration;

public enum ClientType {
    JANRAIN("inApp_client"),
    OIDC("inApp_client");


    String type;

    public String getType() {
        return type;
    }


    ClientType(String type) {
        this.type = type;
    }
}
