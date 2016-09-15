package com.philips.platform.appinfra.rest.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;

/**
 * Created by 310238655 on 8/31/2016.
 */
public class StringRequest extends Request<String> {
    private final Response.Listener<String> mListener;

    /**
     * Creates a new request with the given method.
     *
     * @param method        the request {@link com.android.volley.Request.Method} to use
     * @param url           URL to fetch the string at
     * @param listener      Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public StringRequest(int method, String url, Response.Listener<String> listener,
                         Response.ErrorListener errorListener) throws HttpForbiddenException {
        super(method, url, errorListener);
        if(!url.contains("https")){
            throw new HttpForbiddenException();
        }

        mListener = listener;
    }

    /**
     * Creates a new GET request.
     *
     * @param url           URL to fetch the string at
     * @param listener      Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public StringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) throws HttpForbiddenException{
        this(Method.GET, url, listener, errorListener) ;
        if(!url.contains("https")){
            throw new HttpForbiddenException();
        }
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }
}
