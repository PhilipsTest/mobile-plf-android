/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.networkEssentials;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;

/**
 * Custom implementation of Volley Request Queue
 */
public class NetworkImageLoader {

    private static NetworkImageLoader mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private LruCache<String, Bitmap> mMemoryCache;

    public NetworkImageLoader(Context context) {
        mRequestQueue = getRequestQueue(context);
        setUpCacheLoader();
    }

    protected void setUpCacheLoader() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    @Override
                    public Bitmap getBitmap(String url) {
                        return mMemoryCache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        addBitmapToMemoryCache(url, bitmap);
                    }

                    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
                        if (getBitmapFromMemCache(key) == null) {
                            mMemoryCache.put(key, bitmap);
                        }
                    }

                    public Bitmap getBitmapFromMemCache(String key) {
                        return mMemoryCache.get(key);
                    }
                });
    }

    public static synchronized NetworkImageLoader getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NetworkImageLoader(context);
        }
        return mInstance;
    }

    protected RequestQueue getRequestQueue(final Context context) {
        if (mRequestQueue == null) {
            Cache cache = new DiskBasedCache(context.getCacheDir(), 10 * 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            mRequestQueue = new RequestQueue(cache, network);
            mRequestQueue.start();
        }
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}