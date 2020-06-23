/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.oauth;

import java.io.Serializable;

/**
 * The type Ecs oauth data which contains OAuth data ad returns Janrain id for hybrisOAthAuthentication and returns refresh token for hybrisRefreshOAuth.
 */
public class ECSOAuthData  implements Serializable {
    private static final long serialVersionUID = -7966705143102982019L;
    private String access_token;
    private String token_type;
    private String refresh_token;
    private int expires_in;
    private String scope;

    public String getAccessToken() {
        return access_token;
    }

    public String getTokenType() {
        return token_type;
    }

    public String getRefreshToken() {
        return refresh_token;
    }

    public int getExpiresIn() {
        return expires_in;
    }

    public String getScope() {
        return scope;
    }
}