/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.request;

import com.philips.platform.ecs.util.ECSConfiguration;
import com.philips.platform.appinfra.rest.TokenProviderInterface;
import com.philips.platform.ecs.util.ECSConfiguration;

import java.util.HashMap;
import java.util.Map;

public abstract class OAuthAppInfraAbstractRequest extends AppInfraAbstractRequest {

    @Override
    public Token getToken() {
        return new Token() {
            @Override
            public TokenType getTokenType() {
                return TokenType.OAUTH2;
            }

            @Override
            public String getTokenValue() {
                return ECSConfiguration.INSTANCE.getAccessToken();
            }
        };
    }

    @Override
    public TokenProviderInterface getTokenProviderInterface() {
        return this;
    }

    @Override
    public Map<String, String> getHeader() {
        HashMap<String, String> authMap =new HashMap<String, String>();
        authMap.put("Authorization","Bearer " + ECSConfiguration.INSTANCE.getAccessToken());
        return authMap;
    }

}
