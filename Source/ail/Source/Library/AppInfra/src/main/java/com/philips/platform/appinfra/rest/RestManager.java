/* Copyright (c) Koninklijke Philips N.V. 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.appinfra.rest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.BaseHttpStack;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.AppInfraLogEventID;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.rest.request.RequestQueue;
import com.philips.platform.appinfra.rest.hpkp.HPKPManager;
import com.philips.platform.appinfra.rest.hpkp.HPKPInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The Rest webservice request class .
 */
public class RestManager implements RestInterface {

    private static final long serialVersionUID = -5276610949381468217L;
    private transient RequestQueue mRequestQueue;
    private AppInfra mAppInfra;
    private HPKPInterface pinnedSignatureManager;
    private ArrayList<NetworkConnectivityChangeListener> networkConnectivityChangeListeners = new ArrayList<>();

    public RestManager(AppInfra appInfra) {
        mAppInfra = appInfra;
        VolleyLog.DEBUG = false;
        pinnedSignatureManager = new HPKPManager(mAppInfra);
        appInfra.getAppInfraContext().registerReceiver(new NetworkChangeReceiver(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public synchronized RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone ,passes one in.
            // Instantiate the cache
            ;

            final Cache cache = new DiskBasedCache(getCacheDir(), getCacheSize(), mAppInfra); //

            // Set up the network to use HttpURLConnection as the HTTP client.
            final Network network = getNetwork();
            mRequestQueue = new RequestQueue(cache, network);
            mRequestQueue.start();
            // mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    @Override
    public NetworkTypes  getNetworkReachabilityStatus() {
        NetworkTypes networkStatus=NetworkTypes.NO_NETWORK;
        final NetworkInfo connectionInfo = getNetworkInfo();
        if (null != connectionInfo && connectionInfo.isConnected()) {
            if (connectionInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                networkStatus = NetworkTypes.WIFI;
            } else if (connectionInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                networkStatus = NetworkTypes.MOBILE_DATA;
            }
        }
        return networkStatus;
    }

    @Override
    public boolean isInternetReachable() {
        final NetworkInfo networkInfo = getNetworkInfo();
        return (null != networkInfo && networkInfo.isConnected());
    }

    @Override
    public void registerNetworkChangeListener(NetworkConnectivityChangeListener networkConnectivityChangeListener) {
        if (!networkConnectivityChangeListeners.contains(networkConnectivityChangeListener)) {
            networkConnectivityChangeListeners.add(networkConnectivityChangeListener);
        }
    }

    @Override
    public void unregisterNetworkChangeListener(NetworkConnectivityChangeListener networkConnectivityChangeListener) {
        if (networkConnectivityChangeListeners.contains(networkConnectivityChangeListener)) {
            networkConnectivityChangeListeners.remove(networkConnectivityChangeListener);
        }
    }

    private NetworkInfo getNetworkInfo() {
        //Check for mobile data or Wifi network Info
        final ConnectivityManager connMgr = (ConnectivityManager) mAppInfra.getAppInfraContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return connMgr.getActiveNetworkInfo();
    }

    private Network getNetwork() {
        BaseHttpStack stack = new AppInfraHurlStack(pinnedSignatureManager, new ServiceIDResolver(), mAppInfra.getAppInfraLogInstance());
        return new BasicNetwork(stack);
    }

    public static HashMap<String, String> setTokenProvider(TokenProviderInterface provider) {
        final HashMap<String, String> header = new HashMap<String, String>();
        final TokenProviderInterface.Token token = provider.getToken();
        String scheme = "";
        if (token.getTokenType() == TokenProviderInterface.TokenType.OAUTH2)
            scheme = "Bearer";
        else
            throw new IllegalArgumentException("unsupported token type");
        // String header = "Authorization: " + scheme + " " + token.getTokenValue();
        header.put("Authorization", scheme + " " + token.getTokenValue());

        return header;
    }


    private File getCacheDir() {
        return mAppInfra.getAppInfraContext().getDir("CacheDir", Context.MODE_PRIVATE);
    }

    private int getCacheSize(){
        Integer cacheSize=null;
        final AppConfigurationInterface mAppConfigurationInterface = mAppInfra.getConfigInterface();
        final AppConfigurationInterface.AppConfigurationError configError = new AppConfigurationInterface.AppConfigurationError();
        if (mAppInfra.getConfigInterface() != null) {
            try {
                cacheSize = (Integer) mAppConfigurationInterface.getPropertyForKey("restclient.cacheSizeInKB", "appinfra", configError);//Convert to bytes
                if(cacheSize!=null){
                    cacheSize=cacheSize*1024;
                }
            } catch (IllegalArgumentException i) {
                mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, AppInfraLogEventID.AI_REST,"CONFIG ERROR while getRequestQueue");
            }
        }

        return cacheSize!=null?cacheSize.intValue():DiskBasedCache.DEFAULT_DISK_USAGE_BYTES;
    }

    private class ServiceIDResolver implements HurlStack.UrlRewriter {

        @Override
        public synchronized String rewriteUrl(final String originalUrl) {
            if (!ServiceIDUrlFormatting.isServiceIDUrl(originalUrl))
                return originalUrl;

            // final Lock lock = new ReentrantLock();
            //final Condition waitResult = lock.newCondition();
            final StringBuilder resultURL = new StringBuilder();
            //lock.lock();

            final String sid = ServiceIDUrlFormatting.getServiceID(originalUrl);
            try {
                if (ServiceIDUrlFormatting.getPreference(originalUrl) == ServiceIDUrlFormatting.SERVICEPREFERENCE.BYLANGUAGE) {
                    mAppInfra.getServiceDiscovery().getServiceUrlWithLanguagePreference(sid, new ServiceDiscoveryInterface.OnGetServiceUrlListener() {
                        @Override
                        public void onSuccess(URL url) {
                            resultURL.append(url);
                        }

                        @Override
                        public void onError(ERRORVALUES error, String message) {
                            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR,AppInfraLogEventID.AI_REST, "REST"+error.toString());
                        }
                    });
                } else {
                    mAppInfra.getServiceDiscovery().getServiceUrlWithCountryPreference(sid, new ServiceDiscoveryInterface.OnGetServiceUrlListener() {
                        @Override
                        public void onSuccess(URL url) {
                            resultURL.append(url);
                        }

                        @Override
                        public void onError(ERRORVALUES error, String message) {
                            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR,AppInfraLogEventID.AI_REST, "REST"+error.toString());
                        }
                    });
                }
                //  waitResult.await();
            } catch (Exception e) {
                mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR,AppInfraLogEventID.AI_REST, "REST ERROR");
            }
            if (resultURL.length() == 0)
                return null;
            if (resultURL.toString().contains("v1/")) {
                return resultURL.toString().replace("v1/", "v1");
            }
            return resultURL.toString();
        }

    }

    class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            boolean connected = false;
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                //we are connected to a network
                connected = true;
            } else
                connected = false;
            for (NetworkConnectivityChangeListener networkConnectivityChangeListener : networkConnectivityChangeListeners) {
                networkConnectivityChangeListener.onConnectivityStateChange(connected);
            }
        }

    }

}
