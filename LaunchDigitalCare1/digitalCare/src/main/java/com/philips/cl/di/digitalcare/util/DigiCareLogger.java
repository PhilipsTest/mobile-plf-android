package com.philips.cl.di.digitalcare.util;

import android.util.Log;

/**
 * Custom log class: - Defines all log tags - Intercepts logs so more processing
 * is possible
 * 
 * @author : Ritesh.jha@philips.com
 * 
 * @since: 5 Dec 2015
 */
public class DigiCareLogger {

	public static final String ERROR = "Error"; // Use to log errors
	public static final String APPLICATION = "DigitalCareApp";
	public static final String ACTIVITY = "ActivityLifecycle";
	public static final String FRAGMENT = "FragmentLifecycle";
	public static final String DIGICAREACTIVITY = "DigitalCareActivity";

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
			Log.d(tag, message + "");
		}
	}

	public static void e(String tag, String message) {
		if (isLoggingEnabled) {
			Log.e(tag, message + "");
		}
	}

	public static void i(String tag, String message) {
		if (isLoggingEnabled) {
			Log.i(tag, message + "");
		}
	}

	public static void v(String tag, String message) {
		if (isLoggingEnabled) {
			Log.v(tag, message + "");
		}
	}

	public static void w(String tag, String message) {
		if (isLoggingEnabled) {
			Log.w(tag, message + "");
		}
	}
}
