
package com.philips.cdp.registration.ui.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.philips.cdp.registration.settings.RegistrationHelper;

public class NetworkUtility {

	public static boolean isNetworkAvailable(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context
		        .getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
		return isConnected;

	}

	public static class NetworkStateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			boolean isOnline = NetworkUtility.isNetworkAvailable(context);
			RLog.i(RLog.NETWORK_STATE, "Network state : " + isOnline);
			if (null != RegistrationHelper.getInstance().getNetworkStateListener()) {
				RegistrationHelper.getInstance().getNetworkStateListener()
				        .notifyEventOccurred(isOnline);
			}

		}
	}
}
