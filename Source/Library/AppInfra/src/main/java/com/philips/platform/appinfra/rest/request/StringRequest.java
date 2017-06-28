package com.philips.platform.appinfra.rest.request;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.philips.platform.appinfra.AppInfraLogEventID;
import com.philips.platform.appinfra.rest.RestManager;
import com.philips.platform.appinfra.rest.ServiceIDUrlFormatting;
import com.philips.platform.appinfra.rest.TokenProviderInterface;

import java.util.Map;

/**
 * A wrapper class of canned request for retrieving the response body at a given URL as a String.
 */

public class StringRequest extends com.android.volley.toolbox.StringRequest {
    private Map<String, String> mHeader;
    private TokenProviderInterface mProvider;
    private Map<String, String> mParams;

    public StringRequest(int method, String url, Response.Listener<String> listener,
                         Response.ErrorListener errorListener, Map<String, String> header, Map<String, String>
                                 params, TokenProviderInterface tokenProviderInterface) {
        super(method, url, listener, errorListener);
        this.mProvider = tokenProviderInterface;
        this.mHeader = header;
        this.mParams = params;
        Log.v(AppInfraLogEventID.AI_REST, "String Request");
    }


    public StringRequest(int method, String serviceID, ServiceIDUrlFormatting.SERVICEPREFERENCE pref,
                         String urlExtension, Response.Listener<String> listener,
                         Response.ErrorListener errorListener) {
        super(method, ServiceIDUrlFormatting.formatUrl(serviceID, pref, urlExtension), listener,
                errorListener);
        Log.v(AppInfraLogEventID.AI_REST, "String Request");
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (mHeader != null) {
            if (mProvider != null) {
                final Map<String, String> tokenHeader = RestManager.setTokenProvider(mProvider);
                mHeader.putAll(tokenHeader);
                Log.v(AppInfraLogEventID.AI_REST, "String Request get Headers"+mHeader);
                return mHeader;
            } else {
                Log.v(AppInfraLogEventID.AI_REST, "String Request get Headers"+mHeader);
                return mHeader;
            }
        }
        return super.getHeaders();
    }

    @Override
    protected Map<String, String> getParams()
            throws AuthFailureError {
        if (mParams != null)
            return mParams;

        return super.getParams();
    }
}
