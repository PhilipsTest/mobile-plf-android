/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.cdp.di.ecs.integration;

import com.philips.cdp.di.ecs.util.ECSConfiguration;
import com.philips.platform.appinfra.appidentity.AppIdentityInterface;

/**
 * The type Ecs oauth provider contains OAuth related data. It is passed as input parameter for hybrisOAthAuthentication and hybrisRefreshOAuth
 */
public abstract class ECSOAuthProvider {

    public static final String CLIENT_SECRET_OTHERS = "secret";
    public static final String CLIENT_SECRET_ACCEPTANCE = "acc_inapp_12345";
    public static final String CLIENT_SECRET_PRODUCTION = "prod_inapp_54321";


    public abstract String getOAuthID();

    public ClientType getClientType() {
        return ClientType.JANRAIN;
    }

    public String getClientSecret() {
        if(ECSConfiguration.INSTANCE.getAppInfra().getAppIdentity().getAppState().equals(AppIdentityInterface.AppState.PRODUCTION)){
            return CLIENT_SECRET_PRODUCTION;
        } else if ((ECSConfiguration.INSTANCE.getAppInfra().getAppIdentity().getAppState().equals(AppIdentityInterface.AppState.ACCEPTANCE))|| (ECSConfiguration.INSTANCE.getAppInfra().getAppIdentity().getAppState().equals(AppIdentityInterface.AppState.STAGING))) {
            return CLIENT_SECRET_ACCEPTANCE;
        } else {
            return CLIENT_SECRET_OTHERS;
        }
    }

    public GrantType getGrantType() {
        return GrantType.JANRAIN;
    }
}
