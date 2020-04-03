package com.philips.platform.pim.rest;

import com.android.volley.Response;
import com.philips.platform.appinfra.rest.RestInterface;
import com.philips.platform.pim.manager.PIMSettingManager;

/**
 * A class to invoke rest api request
 */
public class PIMRestClient {

    private RestInterface restInterface;

    public PIMRestClient(RestInterface restInterface) {
        this.restInterface = restInterface;
    }

    public void invokeRequest(PIMRequestInterface pimRequestInterface, Response.Listener<String> successListener, Response.ErrorListener errorListener) {
        PIMRequest request = makePimRequest(pimRequestInterface, successListener, errorListener);
            PIMSettingManager.getInstance().getRestClient().getRequestQueue().add(request);
    }

    private PIMRequest makePimRequest(PIMRequestInterface pimRequestInterface, Response.Listener<String> successListener, Response.ErrorListener errorListener) {
        return new PIMRequest(pimRequestInterface.getMethodType(), pimRequestInterface.getUrl(), pimRequestInterface.getBody(), successListener, errorListener, pimRequestInterface.getHeader());
    }
}
