package com.philips.platform.appinfra.rest.request;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import com.android.volley.Cache;
import com.android.volley.ExecutorDelivery;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.ResponseDelivery;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.AppInfraInterface;
import com.readystatesoftware.chuck.Chuck;
import com.readystatesoftware.chuck.ChuckInterceptor;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Response;


/**
 * A wrapper clas of request dispatch queue with a thread pool of dispatchers.
 *
 * Calling {@link #add(Request)} will enqueue the given Request for dispatch,
 * resolving from either cache or network on a worker thread, and then delivering
 * a parsed response on the main thread.
 */

public class RequestQueue extends com.android.volley.RequestQueue {
    private final ResponseDelivery mHttpErrorDelivery;
    private AppInfraInterface mAppInfra;

    public RequestQueue(Cache cache, Network network, AppInfraInterface appInfra) {
        super(cache, network);
        VolleyLog.DEBUG = false;
        this.mAppInfra = appInfra;
        mHttpErrorDelivery = new ExecutorDelivery(new Handler(Looper.getMainLooper()));
    }

    @Override
    public <T> Request<T> add(Request<T> request) {
        final String url = request.getUrl();
        SharedPreferences shared = mAppInfra.getAppInfraContext().getSharedPreferences("chuckEnabled",Context.MODE_PRIVATE);
        boolean chuck = (shared.getBoolean("CHUCK", false));
        if(chuck) {
            addHttpClient(url);
        }
        if (!url.trim().toLowerCase().startsWith("https://")) {
            if (url.trim().startsWith("serviceid://")) {
                return super.add(request);
            } else {
                mHttpErrorDelivery.postError(request, new VolleyError("HttpForbiddenException-http calls are" +
                        " deprecated use https calls only"));
                return null;
            }
        }
        return super.add(request);
    }


    private void addHttpClient(String url) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new ChuckInterceptor(mAppInfra.getAppInfraContext()))
                .build();
        //Chuck.getLaunchIntent(context);
        Thread thread = new Thread(() -> {
            try  {
                client.newCall(new okhttp3.Request.Builder().url(url).build()).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
    }
}