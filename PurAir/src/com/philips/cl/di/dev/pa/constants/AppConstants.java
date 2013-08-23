package com.philips.cl.di.dev.pa.constants;

import android.graphics.Color;

// TODO: Auto-generated Javadoc
/**
 * The Class AppConstants.
 */
public class AppConstants {
	// Icon for left menu
	/** The icon home. */
	public static String ICON_HOME = "icon_home";

	/** The icon mycity. */
	public static String ICON_MYCITY = "icon_mycity";

	/** The icon cloud. */
	public static String ICON_CLOUD = "icon_cloud";

	/** The icon reg. */
	public static String ICON_REG = "icon_registration";

	/** The icon help. */
	public static String ICON_HELP = "icon_help";

	/** The icon setting. */
	public static String ICON_SETTING = "icon_setting";

	// Label for left menu
	/** The label home. */
	public static String LABEL_HOME = "Home";

	/** The label mycity. */
	public static String LABEL_MYCITY = "My Cities";

	/** The label cloud. */
	public static String LABEL_CLOUD = "About Air Quality";

	/** The label reg. */
	public static String LABEL_REG = "Product Registration";

	/** The label help. */
	public static String LABEL_HELP = "Help & Documentation";

	/** The label setting. */
	public static String LABEL_SETTING = "Settings";

	// Font file -
	/** The font. */
	public static String FONT = "fonts/gillsans.ttf";

	// Drawable
	/** The drawable. */
	public static String DRAWABLE = "drawable";

	/** The scrolltoviewid. */
	public static int SCROLLTOVIEWID = 1;

	/** The scaleleftmenu. */
	public static float SCALELEFTMENU = .75f;

	// Network related data
	/** The Constant UPDATE_INTERVAL. */
	public static final long UPDATE_INTERVAL = 3 * 1000;

	/** The Constant URL. */
	public static final String URL = "http://%s/di/v1/status";

	/** The Constant URL_CURRENT. */
	public static final String URL_CURRENT = "http://%s/di/v1/status/current";

	/** The Constant URL_HISTORY. */
	public static final String URL_HISTORY = "http://%s/di/v1/activity";

	/** The Constant URL_FILTER_STATUS. */
	public static final String URL_FILTER_STATUS = "http://%s/di/v1/device/current";
	
	public static final String URL_OUTDOOR_AQI = "http://www.stateair.net/web/rss/1/%s.xml" ;

	/** The Constant URL_FILTER. */
	public static final String URL_FILTER = "http://%s/di/v1/device";

	/** The Constant defaultIPAddress. */
	public static final String defaultIPAddress = "192.168.10.198";

	/** The Constant MESSAGE_INCORRECT_IP. */
	public static final String MESSAGE_INCORRECT_IP = "Incorrect IP Address";

	/** The Constant MESSAGE_OK. */
	public static final String MESSAGE_OK = "OK";

	/** The Constant MESSAGE_ENTER_IP_ADDRESS. */
	public static final String MESSAGE_ENTER_IP_ADDRESS = "Enter IP Address :";

	/** The Constant HOME. */
	public static final int HOME = 1;

	/** The Constant MYCITIES. */
	public static final int MYCITIES = 2;

	/** The Constant ABOUTAQI. */
	public static final int ABOUTAQI = 3;

	/** The Constant PRODUCTREG. */
	public static final int PRODUCTREG = 4;

	/** The Constant HELP. */
	public static final int HELP = 5;

	/** The Constant SETTINGS. */
	public static final int SETTINGS = 6;

	/** Server Request Type **/
	public static final int GET_SENSOR_DATA_REQUEST_TYPE = 1;

	/** The Constant TABLENAME. */
	public static final String TABLENAME = "CityDetails";
	
	public static final String TABLE_AIRPURIFIER_EVENT = "AirPurifierEvent" ;
	
	public static final String LAST_SYNC_DATETIME = "lastsyncdatetime" ;

	// Contacts Table Columns names
	/** The Constant KEY_ID. */
	public static final String KEY_ID = "id";

	/** The Constant KEY_CITY. */
	public static final String KEY_CITY = "CITY";

	/** The Constant KEY_PROVINCE. */
	public static final String KEY_PROVINCE = "PROVINCE";

	/** The Constant KEY_DATE. */
	public static final String KEY_DATE = "DATE";

	/** The Constant KEY_AQI. */
	public static final String KEY_AQI = "AQI";

	/** The Constant KEY_TIME. */
	public static final String KEY_TIME = "TIME";

	/** The Constant DB_NAME. */
	public static final String DB_NAME = "City.db";

	public static final String TABLE_OUTDOOR_AQI = "OutdoorAQITable" ;
	public static final String ID = "ID" ;
	public static final String OUTDOOR_AQI = "Aqi" ;
	public static final String LOG_DATETIME = "Datetime" ;
	public static final String CITY_ID = "CityID" ;
	
	/** The Constant DB_VERS. */
	public static final int DB_VERS = 1;

	public static String sCityNameQuery = "Select distinct "
			+ AppConstants.KEY_CITY + " from " + AppConstants.TABLENAME;
	
	public static String airPurifierEventQuery = "Select * from " + AppConstants.TABLE_AIRPURIFIER_EVENT ;
	
	public static String selectLatestOutdoorAQI = "Select * from " + AppConstants.TABLE_OUTDOOR_AQI + " where Aqi > 0 order by " + LOG_DATETIME + " DESC"  ;
	
	public static String selectOutdoorAQIOnLogDateTime = "Select * from " + AppConstants.TABLE_OUTDOOR_AQI + " where " + AppConstants.LOG_DATETIME + "= '%s'" ;
	
	
	public static final String INDOOR_AQI = "aqi" ;

	public static final float SWIPE_THRESHOLD = 100;

	public static final float SWIPE_VELOCITY_THRESHOLD = 100;

	public static final long DURATION = 1000;
	
	public static final long FADEDURATION = 600;
	
	public static final long FADEDELAY = 100;

	public static final float MINIMUNFILTER = 0;
	public static final float MAXIMUMFILTER = 1000;

	public static final int MAXWIDTH = 516;

	// Colors for each category
	public static final int COLOR_VGOOD = Color.rgb(0, 169, 231);
	public static final int COLOR_GOOD = Color.rgb(129, 107, 172);
	public static final int COLOR_FAIR = Color.rgb(222, 74, 138);
	public static final int COLOR_BAD = Color.rgb(255, 0, 0);

	public static final String[] BAD_RING = { "bad_quadrant1",
			"bad_quadrant2", "bad_quadrant3", "bad_quadrant4" };
	public static final String[] GOOD_RING = { "good_quadrant1",
			"good_quadrant2", "good_quadrant3", "good_quadrant4" };
	public static final String[] FAIR_RING = { "fair_quadrant1",
			"fair_quadrant2", "fair_quadrant3", "fair_quadrant4" };
	public static final String[] VGOOD_RING = { "vgood_quadrant1",
			"vgood_quadrant2", "vgood_quadrant3", "vgood_quadrant4" };
	
	public static final String HOME_INDOOR_GOOD = "home_indoor_good";
	public static final String HOME_INDOOR_VGOOD = "home_indoor_vgood";
	public static final String HOME_INDOOR_BAD = "home_indoor_bad";
	public static final String HOME_INDOOR_FAIR = "home_indoor_fair";

	
	
	
	public static final float  DAYWIDTH = 680;

	// Shanghai
	public static final String SHANGHAI_GOOD = "shanghai_good";
	public static final String SHANGHAI_VGOOD = "shanghai_vgood";
	public static final String SHANGHAI_BAD = "shanghai_bad";
	public static final String SHANGHAI_FAIR = "shanghai_fair";

	
	public static final int OUTDOOR_AQI_UPDATE_DURATION = 60 * 60 * 1000 ;

	public static final String WARNING_VGOOD = "warning_vgood";
	public static final String WARNING_GOOD = "warning_good";
	public static final String WARNING_FAIR = "warning_fair";
	public static final String WARNING_BAD = "warning_bad";

}
