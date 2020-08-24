/**
 * Network status Receiver class. it responsible for getting the status of the connectivity across the device.
 *
 *
 *  Copyright (c) 2016 Philips. All rights reserved.
 */

package com.philips.cdp.digitalcare.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.philips.cdp.digitalcare.listeners.NetworkStateListener;

public class NetworkReceiver extends BroadcastReceiver {

	private final String TAG = NetworkReceiver.class.getSimpleName();
	private NetworkStateListener mNetworkCallback = null;

	public NetworkReceiver() {
	}

	public NetworkReceiver(NetworkStateListener callback) {
		this();
		mNetworkCallback = callback;
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		ConnectivityManager mConnectionManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (mConnectionManager != null) {
			NetworkInfo mActiveNetwork = mConnectionManager
					.getActiveNetworkInfo();
			if (mActiveNetwork != null) {
				DigiCareLogger.v(TAG, "Connection Available");
				if (mNetworkCallback != null)
					mNetworkCallback.onNetworkStateChanged(true);
			} else {
				DigiCareLogger.v(TAG, "Connection Not Available");

				if (mNetworkCallback != null)
					mNetworkCallback.onNetworkStateChanged(false);

			}
		}

	}

}
