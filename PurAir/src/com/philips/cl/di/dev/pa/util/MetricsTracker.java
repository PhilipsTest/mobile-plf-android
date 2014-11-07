package com.philips.cl.di.dev.pa.util;

import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

import com.adobe.mobile.Analytics;
import com.adobe.mobile.Config;
import com.philips.cl.di.dev.pa.PurAirApplication;

public class MetricsTracker {

	/* ---------------DEFAULT LIST ----------------------- */
	private static String DEFAULT_COUNTRY = "NL";
	private static String DEFAULT_LANGUAGE = "en";
	private static String DEFAULT_CURRENCY = "EUR";

	/* ---------------KEY LIST ----------------------- */
	private static final String KEY_CP = "sector";
	private static final String KEY_APPNAME = "app.name";
	private static final String KEY_VERSION = "app.version";
	private static final String KEY_OS = "app.os";
	private static final String KEY_COUNTRY = "locale.country";
	private static final String KEY_LANGUAGE = "locale.language";
	private static final String KEY_CURRENCY = "locale.currency";
	private static final String KEY_FILENAME = "fileName";
	private static final String KEY_EXIT_LINK = "exitLinkName";
	private static final String KEY_ERROR_USER = "userError";
	private static final String KEY_ERROR_TECHNICAL = "technicalerror";
	private static final String KEY_PAGE_EVENT = "pageEvents";
	private static final String KEY_LOCATION_PURIFIER = "newPurifyLocation";
	private static final String KEY_LOCATION_WEATHER = "newPurifyLocation";
	private static final String KEY_OPTION_DETAILS = "optionDetail";
	private static final String KEY_VIDEO_NAME = "videoName";
	// private static final String KEY_FIRMWARE_VERSION = "firmwareVersion";
	private static final String KEY_PRODUCT_MODEL = "productModel";
	private static final String KEY_APP_ID = "appId";

	/* ---------------ACTION LIST ----------------------- */
	private static final String ACTION_VIDEO_START = "videoStart";
	private static final String ACTION_VIDEO_END = "videoEnd";
	private static final String ACTION_EXIT_LINK = "exit link";
	private static final String ACTION_DOWNLOAD = "download";
	private static final String ACTION_ERROR_SET = "setError";
	private static final String ACTION_LOCATION_NEW_PURIFIER = "newPurifierRequest";
	private static final String ACTION_LOCATION_NEW_WEATHER = "newWeatherRequest";
	private static final String ACTION_SET_OPTION = "setOption";

	/*----------------PAGE LIST------------------------*/
	private static final String PAGE_USER_REGISTRATION = "UserRegistration";

	/* ---------------VALUE LIST ----------------------- */
	private static String VALUE_CP = "CP";
	private static String VALUE_APPNAME = "PurAir";

	public static void initContext(Context context) {
		Config.setContext(context);
	}

	// This needs to call on onResume() of every activity.
	public static void startCollectLifecycleData(Activity activity) {
		Config.collectLifecycleData(activity);
	}

	// This needs to call on onPause() of every activity.
	public static void stopCollectLifecycleData() {
		Config.pauseCollectingLifecycleData();
	}

	// This has to be tracked by each page.
	public static void trackPage(String pageName) {
		Analytics.trackState(pageName, addAnalyticsDataObject());
	}

	public static void trackPageStartUserRegistration() {
		Map<String, Object> contextData = addAnalyticsDataObject();
		contextData.put(KEY_PAGE_EVENT, "startProductRegistration");
		Analytics.trackState(PAGE_USER_REGISTRATION, contextData);
	}

	public static void trackPageFinishedUserRegistration() {
		Map<String, Object> contextData = addAnalyticsDataObject();
		contextData.put(KEY_PAGE_EVENT, "successProductRegistration");
		Analytics.trackState(PAGE_USER_REGISTRATION, contextData);
	}

	public static void trackActionUserError() {
		Map<String, Object> contextData = new HashMap<String, Object>();
		contextData.put(KEY_ERROR_USER, "incorrect e-mail address");
		Analytics.trackAction(ACTION_ERROR_SET, contextData);
	}

	public static void trackActionTechnicalError() {
		Map<String, Object> contextData = new HashMap<String, Object>();
		contextData.put(KEY_ERROR_TECHNICAL,
				"we're having trouble connecting to your Air Purifier");
		Analytics.trackAction(ACTION_ERROR_SET, contextData);
	}

	/*
	 * When the app is left for a different activity or app. Note that this is
	 * triggered by a link/button/functionality of the app. Do not use this when
	 * the visitor swiches using the home button.
	 */
	public static void trackActionExit(String link) {
		Map<String, Object> contextData = new HashMap<String, Object>();
		contextData.put(KEY_EXIT_LINK, link);
		Analytics.trackAction(ACTION_EXIT_LINK, contextData);
	}

	public static void trackActionDownloaded(String fileName) {
		Map<String, Object> contextData = new HashMap<String, Object>();
		contextData.put(KEY_FILENAME, fileName);
		Analytics.trackAction(ACTION_DOWNLOAD, contextData);
	}

	public static void trackActionLocationPurifier(String location) {
		Map<String, Object> contextData = new HashMap<String, Object>();
		contextData.put(KEY_LOCATION_PURIFIER, location);
		Analytics.trackAction(ACTION_LOCATION_NEW_PURIFIER, contextData);
	}

	public static void trackActionLocationWeather(String location) {
		Map<String, Object> contextData = new HashMap<String, Object>();
		contextData.put(KEY_LOCATION_WEATHER, location);
		Analytics.trackAction(ACTION_LOCATION_NEW_WEATHER, contextData);
	}

	public static void trackActionVideoStart(String videoName) {
		Map<String, Object> contextData = new HashMap<String, Object>();
		contextData.put(KEY_VIDEO_NAME, videoName);
		Analytics.trackAction(ACTION_VIDEO_START, contextData);
	}

	public static void trackActionVideoEnd(String videoName) {
		Map<String, Object> contextData = new HashMap<String, Object>();
		contextData.put(KEY_VIDEO_NAME, videoName);
		Analytics.trackAction(ACTION_VIDEO_END, contextData);
	}

	/*------------------------------ CONTROL AIR PURIFIER -----------START----------*/

	public static void trackActionTogglePower(String powerStatus) {
		Map<String, Object> contextData = new HashMap<String, Object>();
		contextData.put(KEY_OPTION_DETAILS, powerStatus);
		Analytics.trackAction(ACTION_SET_OPTION, contextData);
	}

	public static void trackActionIndicatorLight(String lightStatus) {
		Map<String, Object> contextData = new HashMap<String, Object>();
		contextData.put(KEY_OPTION_DETAILS, lightStatus);
		Analytics.trackAction(ACTION_SET_OPTION, contextData);
	}

	public static void trackActionScheduleAdd() {
		Map<String, Object> contextData = new HashMap<String, Object>();
		contextData.put(KEY_OPTION_DETAILS, "schedule added");
		Analytics.trackAction(ACTION_SET_OPTION, contextData);
	}

	public static void trackActionTimerAdded(String time) {
		Map<String, Object> contextData = new HashMap<String, Object>();
		contextData.put(KEY_OPTION_DETAILS, "timer " + time);
		Analytics.trackAction(ACTION_SET_OPTION, contextData);
	}

	public static void trackActionNotificationAirQuality(String airQuality) {
		Map<String, Object> contextData = new HashMap<String, Object>();
		contextData.put(KEY_OPTION_DETAILS, "notification air quality:"
				+ airQuality);
		Analytics.trackAction(ACTION_SET_OPTION, contextData);
	}

	public static void trackActionFanSpeed(String speed) {
		Map<String, Object> contextData = new HashMap<String, Object>();
		contextData.put(KEY_OPTION_DETAILS, "speed " + speed);
		Analytics.trackAction(ACTION_SET_OPTION, contextData);
	}

	public static void trackActionChildLock(String childLockStatus) {
		Map<String, Object> contextData = new HashMap<String, Object>();
		contextData.put(KEY_OPTION_DETAILS, childLockStatus);
		Analytics.trackAction(ACTION_SET_OPTION, contextData);
	}

	public static void trackActionNotification(boolean notification) {
		Map<String, Object> contextData = new HashMap<String, Object>();
		if (notification) {
			contextData.put(KEY_OPTION_DETAILS, "notification_on");
		} else {
			contextData.put(KEY_OPTION_DETAILS, "notification_off");
		}
		Analytics.trackAction(ACTION_SET_OPTION, contextData);
	}

	public static void trackActionRemoteControl(Boolean remote) {
		Map<String, Object> contextData = new HashMap<String, Object>();
		if (remote) {
			contextData.put(KEY_OPTION_DETAILS, "remote_control_on");
		} else {
			contextData.put(KEY_OPTION_DETAILS, "remote_control_off");
		}
		Analytics.trackAction(ACTION_SET_OPTION, contextData);
	}

	public static void trackActionAdvanceNetworkConfig(Boolean config) {
		Map<String, Object> contextData = new HashMap<String, Object>();
		if (config) {
			contextData.put(KEY_OPTION_DETAILS, "advance_network:yes");
		} else {
			contextData.put(KEY_OPTION_DETAILS, "advance_network:no");
		}
		Analytics.trackAction(ACTION_SET_OPTION, contextData);
	}

	public static void trackActionTFanSpeed(String speed) {
		Map<String, Object> contextData = new HashMap<String, Object>();
		contextData.put(KEY_OPTION_DETAILS, speed);
		Analytics.trackAction(ACTION_SET_OPTION, contextData);
	}

	/*------------------------------ CONTROL AIR PURIFIER -----------------END----------*/

	private static Map<String, Object> addAnalyticsDataObject() {
		System.out.println("ADBMobile.addAnalyticsDataObject()");
		Map<String, Object> contextData = new HashMap<String, Object>();
		contextData.put(KEY_CP, VALUE_CP);
		contextData.put(KEY_APPNAME, VALUE_APPNAME);
		contextData.put(KEY_VERSION, PurAirApplication.getAppVersion());
		contextData.put(KEY_OS, "Android " + Build.VERSION.RELEASE);
		contextData.put(KEY_COUNTRY, getCountry());
		contextData.put(KEY_LANGUAGE, getLanguage());
		contextData.put(KEY_CURRENCY, getCurrency());
		// contextData.put(KEY_FIRMWARE_VERSION, "TODO");
		contextData.put(KEY_PRODUCT_MODEL, "TODO");
		contextData.put(KEY_APP_ID, "TODO");
		return contextData;
	}

	private static String getCountry() {
		String country = PurAirApplication.getAppContext().getResources()
				.getConfiguration().locale.getCountry().toLowerCase(
				Locale.getDefault());
		if (country == null)
			country = DEFAULT_COUNTRY;
		return country;
	}

	private static String getLanguage() {
		String language = PurAirApplication.getAppContext().getResources()
				.getConfiguration().locale.getLanguage();
		if (language == null)
			language = DEFAULT_LANGUAGE;
		return language;
	}

	private static String getCurrency() {
		Currency currency = Currency.getInstance(Locale.getDefault());
		String currencyCode = currency.getCurrencyCode();
		if (currencyCode == null)
			currencyCode = DEFAULT_CURRENCY;
		return currencyCode;
	}
}
