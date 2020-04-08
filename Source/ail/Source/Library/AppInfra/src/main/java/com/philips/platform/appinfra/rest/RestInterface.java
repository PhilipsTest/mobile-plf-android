/* Copyright (c) Koninklijke Philips N.V. 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.appinfra.rest;

import com.philips.platform.appinfra.rest.request.RequestQueue;

import java.io.Serializable;

/**
 * The Interface for Request and NetWorkType
 */
public interface RestInterface extends Serializable {

    /**
     * Gets request queue.
     *
     * @return the REST request queue
     */
    RequestQueue getRequestQueue();

    
    enum NetworkTypes {WIFI,MOBILE_DATA,NO_NETWORK}

    /**
     * Get the network  connection and sets the wifiConnected and mobileConnected
     * @return enum WIFI,MOBILE_DATA,NO_NETWORK
     * @since 1.0.0
 */
    NetworkTypes  getNetworkReachabilityStatus();

    /**
     * Method to check the network connectivity availability .
     * @return boolean true/false.
     * @since 1.1.0
     */
    boolean isInternetReachable() ;

    void registerNetworkChangeListener(NetworkConnectivityChangeListener networkConnectivityChangeListener);

    void unregisterNetworkChangeListener(NetworkConnectivityChangeListener networkConnectivityChangeListener);

    interface NetworkConnectivityChangeListener {
        void onConnectivityStateChange(boolean isConnected);
    }

    /** @apiNote - api will clear the cache of App-Infra rest client,
     * there will be performance issue if used frequently,
     * since it will be clearing entire cache,
     * hence advised to be used during cleanup activities for an application
     *  @since 1805
     */
    void clearCacheResponse();
}
