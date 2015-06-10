
package com.philips.cl.di.reg.ui.utils;

import android.util.Log;

public class RLog {

	public static final String FRAGMENT_LIFECYCLE = "FragmentLifecycle";

	public static final String ACTIVITY_LIFECYCLE = "ActivityLifecycle";
	
	public static final String EVENT_LISTENERS = "EventListeners";

	public static final String APPLICATION = "RegistrationApplication";

	public static final String NETWORK_STATE = "NetworkState";

	public static final String JANRAIN_INITIALIZE = "JanrainInitialize";
	
	public static final String EXCEPTION = "Exception";
	
	public static final String ONCLICK = "onClick";
	
	public static final String CALLBACK = "CallBack";
	
	private static boolean isLoggingEnabled = true;

	public static void enableLogging() {
		isLoggingEnabled = true;
	}

	public static void disableLogging() {
		isLoggingEnabled = false;
	}

	public static boolean isLoggingEnabled() {
		return isLoggingEnabled;
	}

	public static void d(String tag, String message) {
		if (isLoggingEnabled) {
			Log.d(tag, message);
		}
	}

	public static void e(String tag, String message) {
		if (isLoggingEnabled) {
			Log.e(tag, message);
		}
	}

	public static void i(String tag, String message) {
		if (isLoggingEnabled) {
			Log.i(tag, message);
		}
	}

	public static void v(String tag, String message) {
		if (isLoggingEnabled) {
			Log.v(tag, message);
		}
	}

}
