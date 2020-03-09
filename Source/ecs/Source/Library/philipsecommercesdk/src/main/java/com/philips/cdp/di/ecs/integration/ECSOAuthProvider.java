/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.cdp.di.ecs.integration;

/**
 * The type Ecs oauth provider contains OAuth related data. It is passed as input parameter for hybrisOAthAuthentication and hybrisRefreshOAuth
 */
public abstract class ECSOAuthProvider {


    public static final String CLIENT_SECRET = "secret";


    public abstract String getOAuthID();

    public ClientType getClientID() {
        return ClientType.JANRAIN;
    }

    public String getClientSecret() {
        return CLIENT_SECRET;
    }

    public GrantType getGrantType() {
        return GrantType.JANRAIN;
    }
}
