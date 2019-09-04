/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.ecs.request;

import com.android.volley.Request;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.philips.cdp.di.ecs.error.ECSError;
import com.philips.cdp.di.ecs.error.ECSNetworkError;
import com.philips.cdp.di.ecs.integration.ECSCallback;
import com.philips.cdp.di.ecs.integration.OAuthInput;
import com.philips.cdp.di.ecs.model.oauth.OAuthResponse;
import com.philips.cdp.di.ecs.store.ECSURLBuilder;
import com.philips.cdp.di.ecs.util.ECSConfig;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

public class OAuthRequest extends AppInfraAbstractRequest  implements Response.Listener<JSONObject>{

    private final ECSCallback<OAuthResponse,Exception> ecsCallback;
    private final  OAuthInput oAuthInput;
    String oAuthID;

    String mRetryUrl = null;

    //For handling 307 - Temporary redirect
    public static final int HTTP_REDIRECT = 307;

    public OAuthRequest(OAuthInput oAuthInput, ECSCallback<OAuthResponse, Exception> ecsListener) {
        this.ecsCallback = ecsListener;
        this.oAuthInput = oAuthInput;
        oAuthID = oAuthInput.getOAuthID();
    }

    /*
    * Janrain detail has to be send in request body
    * Note: These janrain details should not be passed in request url as query string
    *
    * */
    private Map getJanrainDetail(){
        Map map = new HashMap<String,String>();
        if(oAuthID !=null)
        map.put(oAuthInput.getGrantType().getType(), oAuthID);
        map.put("grant_type",oAuthInput.getGrantType().getType());
        map.put("client_id",oAuthInput.getClientID());
        map.put("client_secret",oAuthInput.getClientSecret());
        return  map;
    }

    @Override
    public Map<String, String> getHeader() {
        return getJanrainDetail();
    }

    @Override
    public JSONObject getJSONRequest() {
        return new JSONObject(getJanrainDetail());
    }

    @Override
    public int getMethod() {
        return Request.Method.POST;
    }

    @Override
    public String getURL() {
        return  mRetryUrl!=null? mRetryUrl :new ECSURLBuilder().getOauthUrl(oAuthInput);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        retryForUrlRedirection(error);
    }

    @Override
    public void onResponse(JSONObject response) {
        if (response != null) {
            OAuthResponse oAuthResponse = new Gson().fromJson(response.toString(),
                    OAuthResponse.class);
            ECSConfig.INSTANCE.setAuthToken( oAuthResponse.getAccessToken());
            ecsCallback.onResponse(oAuthResponse);
        }
    }

    private void retryForUrlRedirection(VolleyError error) {
        // Handle 30x
        if (isRedirectionRequired(error)) {
            mRetryUrl = getLocation(error);
            executeRequest();
        } else {
            ECSError ecsError = ECSNetworkError.getErrorLocalizedErrorMessage(error);
            ecsCallback.onFailure(ecsError.getException(), ecsError.getErrorcode());
        }
    }



    public boolean isRedirectionRequired(VolleyError volleyError) {
        int status = -1;

        if(volleyError!=null && volleyError.networkResponse!=null) {
            status = volleyError.networkResponse.statusCode;
        }
        return status == HTTP_REDIRECT || HttpURLConnection.HTTP_MOVED_PERM == status ||
                status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_SEE_OTHER &&
                getLocation(volleyError) != null && !getURL().equalsIgnoreCase(getLocation(volleyError));
    }

    protected String getLocation(VolleyError volleyError) {
        String location = null;
        if(volleyError!=null && volleyError.networkResponse!=null && volleyError.networkResponse.headers!=null) {
            location = volleyError.networkResponse.headers.get("Location");
        }
        return location;
    }

    @Override
    public Response.Listener<JSONObject> getJSONSuccessResponseListener() {
        return this;
    }
}
