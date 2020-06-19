/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.session;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import com.android.volley.ExecutorDelivery;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BaseHttpStack;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

import java.io.File;

public class VolleyWrapper {
    /**
     * Default on-disk cache directory.
     */
    protected static final String DEFAULT_CACHE_DIR = "volley";

    protected static final int DEFAULT_NETWORK_THREAD_POOL_SIZE = 4;

    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @param stack   An {@link BaseHttpStack} to use for the network, or null for default.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context, BaseHttpStack stack) {
        File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);

        if (stack == null) {
            stack = new HurlStack();
        }

        Network network = new BasicNetwork(stack);
        //Start the thread so that we can pass the looper
        HandlerThread resultThread = new HandlerThread("resultThread");
        resultThread.start();

        ExecutorDelivery responseDelivery = new ExecutorDelivery(new Handler(resultThread.getLooper()));

        RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), network,
                DEFAULT_NETWORK_THREAD_POOL_SIZE, responseDelivery);
        queue.start();

        return queue;
    }
}
