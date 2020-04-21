/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.integration;

public enum GrantType {

    JANRAIN("janrain"),
    OIDC("oidc"),
    REFRESH_TOKEN("refresh_token");


    public String getType() {
        return type;
    }

    String type;

    GrantType(String type) {
        this.type = type;
    }
}
