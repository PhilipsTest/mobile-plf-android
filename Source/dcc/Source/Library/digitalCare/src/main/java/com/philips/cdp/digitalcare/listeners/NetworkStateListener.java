/**
 * <p>
 * <p> NetworkListener Callback method used to to listen network changes across the DigitalCare
 * component fragments </p>
 * <p>
 * Copyright (c) 2016 Philips. All rights reserved.
 */

package com.philips.cdp.digitalcare.listeners;

/**
 * Listener for the Network connectivity callback
 */
public interface NetworkStateListener {
    void onNetworkStateChanged(boolean connectionStatus);
}
