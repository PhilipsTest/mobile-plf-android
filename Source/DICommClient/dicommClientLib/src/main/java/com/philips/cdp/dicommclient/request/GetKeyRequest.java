/*
 * Copyright (c) 2015-2017 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.cdp.dicommclient.request;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.philips.cdp.dicommclient.port.common.SecurityPortProperties;
import com.philips.cdp.dicommclient.util.DICommLog;
import com.philips.cdp.dicommclient.util.GsonProvider;
import com.philips.cdp2.commlib.lan.communication.LanRequest;
import com.philips.cdp2.commlib.lan.communication.LanRequestType;

import java.util.HashMap;

import static com.philips.cdp.dicommclient.util.DICommLog.Verbosity.ERROR;

public class GetKeyRequest extends LanRequest {

    static final String KEY_MISSING_IN_RESPONSE_MESSAGE = "Key missing in response";
    private static final String SECURITY_PORTNAME = "security";
    private static final int SECURITY_PRODUCTID = 0;

    public GetKeyRequest(String applianceIpAddress, int protocolVersion, boolean isHttps, ResponseHandler responseHandler) {
        super(applianceIpAddress, protocolVersion, isHttps, SECURITY_PORTNAME, SECURITY_PRODUCTID, LanRequestType.GET, new HashMap<String, Object>(), responseHandler, null);
    }

    @Override
    public Response execute() {
        Response response = doExecute();
        String responseData = response.getResponseMessage();

        if (response.getError() == null) {
            try {
                Gson gson = GsonProvider.get();
                SecurityPortProperties securityPortProperties = gson.fromJson(responseData, SecurityPortProperties.class);
                final String key = securityPortProperties.getKey();

                if (key == null || key.isEmpty()) {
                    return new Response("Key missing in response", Error.REQUEST_FAILED, mResponseHandler);
                }
                return new Response(securityPortProperties.getKey(), null, mResponseHandler);
            } catch (JsonSyntaxException e) {
                log(ERROR, DICommLog.SECURITY, e.getMessage());
                return new Response(e.getMessage(), Error.REQUEST_FAILED, mResponseHandler);
            }
        } else {
            return new Response(responseData, Error.REQUEST_FAILED, mResponseHandler);
        }
    }

    @VisibleForTesting
    Response doExecute() {
        return super.execute();
    }

    @Override
    protected void log(DICommLog.Verbosity verbosity, @NonNull String tag, @NonNull String message) {
        // Logging disabled
    }
}
