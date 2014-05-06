package com.philips.cl.di.dev.pa.activity;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.view.ActionMode;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.mobeta.android.dslv.DragSortListView;
import com.philips.cl.di.common.ssdp.contants.DiscoveryMessageID;
import com.philips.cl.di.common.ssdp.controller.InternalMessage;
import com.philips.cl.di.common.ssdp.lib.SsdpService;
import com.philips.cl.di.common.ssdp.models.DeviceModel;
import com.philips.cl.di.dev.pa.R;
import com.philips.cl.di.dev.pa.adapter.ListItemAdapter;
import com.philips.cl.di.dev.pa.constant.AppConstants;
import com.philips.cl.di.dev.pa.constant.AppConstants.Port;
import com.philips.cl.di.dev.pa.cpp.CPPController;
import com.philips.cl.di.dev.pa.cpp.ICPDeviceDetailsListener;
import com.philips.cl.di.dev.pa.cpp.PairingListener;
import com.philips.cl.di.dev.pa.cpp.PairingManager;
import com.philips.cl.di.dev.pa.cpp.SignonListener;
import com.philips.cl.di.dev.pa.dashboard.HomeFragment;
import com.philips.cl.di.dev.pa.datamodel.AirPortInfo;
import com.philips.cl.di.dev.pa.datamodel.City;
import com.philips.cl.di.dev.pa.datamodel.SessionDto;
import com.philips.cl.di.dev.pa.datamodel.Weatherdto;
import com.philips.cl.di.dev.pa.ews.EWSDialogFactory;
import com.philips.cl.di.dev.pa.firmware.FirmwarePortInfo;
import com.philips.cl.di.dev.pa.firmware.FirmwareUpdateActivity;
import com.philips.cl.di.dev.pa.firmware.FirmwareUpdateFragment;
import com.philips.cl.di.dev.pa.fragment.AirQualityFragment;
import com.philips.cl.di.dev.pa.fragment.BuyOnlineFragment;
import com.philips.cl.di.dev.pa.fragment.HelpAndDocFragment;
import com.philips.cl.di.dev.pa.fragment.NotificationsFragment;
import com.philips.cl.di.dev.pa.fragment.OutdoorLocationsFragment;
import com.philips.cl.di.dev.pa.fragment.PairingDialogFragment;
import com.philips.cl.di.dev.pa.fragment.ProductRegFragment;
import com.philips.cl.di.dev.pa.fragment.ProductRegistrationStepsFragment;
import com.philips.cl.di.dev.pa.fragment.SettingsFragment;
import com.philips.cl.di.dev.pa.fragment.ToolsFragment;
import com.philips.cl.di.dev.pa.newpurifier.ConnectionState;
import com.philips.cl.di.dev.pa.newpurifier.PurAirDevice;
import com.philips.cl.di.dev.pa.newpurifier.PurifierManager;
import com.philips.cl.di.dev.pa.purifier.AirPurifierController;
import com.philips.cl.di.dev.pa.purifier.AirPurifierEventListener;
import com.philips.cl.di.dev.pa.purifier.PurifierDatabase;
import com.philips.cl.di.dev.pa.purifier.SubscriptionManager;
import com.philips.cl.di.dev.pa.security.DISecurity;
import com.philips.cl.di.dev.pa.security.KeyDecryptListener;
import com.philips.cl.di.dev.pa.util.ALog;
import com.philips.cl.di.dev.pa.util.Fonts;
import com.philips.cl.di.dev.pa.util.RightMenuClickListener;
import com.philips.cl.di.dev.pa.util.Utils;
import com.philips.cl.di.dev.pa.view.FilterStatusView;
import com.philips.cl.di.dev.pa.view.FontTextView;
import com.philips.cl.di.dev.pa.view.ListViewItem;

public class MainActivity extends BaseActivity implements
ICPDeviceDetailsListener, Callback, KeyDecryptListener,
OnClickListener, AirPurifierEventListener, SignonListener, PairingListener {

	private static final String PREFS_NAME = "AIRPUR_PREFS";
	private static final String OUTDOOR_LOCATION_PREFS = "outdoor_location_prefs";

	private static int screenWidth, screenHeight;

	private DrawerLayout mDrawerLayout;
	private ListView mListViewLeft;
	private ScrollView mScrollViewRight;
	private RightMenuClickListener rightMenuClickListener;

	/** Right off canvas elements */
	private TextView tvAirStatusAqiValue;
	private TextView tvConnectionStatus;
	private TextView tvAirStatusMessage;
	private ImageView ivAirStatusBackground;
	private ImageView ivConnectedImage;
	private boolean connected;
	
	/**
	 * Action bar
	 */
	private ImageView rightMenu;
	private ImageView leftMenu;
	private ImageView addLocation;
	private ImageView backToHome;
	private FontTextView noOffFirmwareUpdate;

	/** Filter status bars */
	private FilterStatusView preFilterView, multiCareFilterView,
	activeCarbonFilterView, hepaFilterView;

	// Filter status texts
	private TextView preFilterText, multiCareFilterText,
	activeCarbonFilterText, hepaFilterText;

	private static HomeFragment homeFragment;

	private boolean isDeviceDiscovered;

	private boolean mRightDrawerOpened, mLeftDrawerOpened;
	private DISecurity diSecurity;
	private ActionBarDrawerToggle mActionBarDrawerToggle;

	private MenuItem rightMenuItem;
	private SharedPreferences mPreferences;
	private int mVisits;
	private BroadcastReceiver networkReceiver;
	private CPPController cppController;

	private int isGooglePlayServiceAvailable;
	private TextView cancelSearchItem;
	private SharedPreferences outdoorLocationPrefs;
	private ArrayList<String> outdoorLocationsList;

	public boolean isTutorialPromptShown = false;

	private IntentFilter filter;

	private static boolean stopService;

	private AirPurifierController airPurifierController;

	public boolean isDiagnostics;
	private PurifierDatabase purifierDatabase;
	private List<PurAirDevice> dbPurifierDetailDtoList;

	private String localDeviceUsn;

	public static boolean isClickEvent;

	public boolean isPairingDialogShown;
	protected ProgressDialog progressDialog;

	private ConnectivityManager connManager ;
	private String secretKey;
	private ActionMode actionMode;
	public boolean isEWSStarted;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main_aj);
		
		// TODO replace with discoveryManager
		SsdpService.getInstance().startDeviceDiscovery(this);
		
		airPurifierController = AirPurifierController.getInstance();

		/**
		 * Create database and tables
		 */
		purifierDatabase = new PurifierDatabase();
		dbPurifierDetailDtoList = purifierDatabase.getAllPurifiers(ConnectionState.DISCONNECTED);
		/**
		 * Diffie Hellman key exchange
		 */
		diSecurity = new DISecurity(this);

		mPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		mVisits = mPreferences.getInt("NoOfVisit", 0);
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.putInt("NoOfVisit", ++mVisits);
		editor.commit();

		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		screenWidth = displayMetrics.widthPixels;
		screenHeight = displayMetrics.heightPixels;

		initActionBar();

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		mDrawerLayout.setScrimColor(Color.parseColor("#60FFFFFF"));
		mDrawerLayout.setFocusableInTouchMode(false);

		mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_launcher, R.string.app_name,
				R.string.action_settings) {

			@Override
			public void onDrawerClosed(View drawerView) {
				if (drawerView.getId() == R.id.right_menu_scrollView) {
					ALog.i(ALog.MAINACTIVITY, "Right drawer close");
					mRightDrawerOpened = false;
				} else if (drawerView.getId() == R.id.left_menu_listView) {
					ALog.i(ALog.MAINACTIVITY, "Left drawer close");
					mLeftDrawerOpened = false;
				}
				
				supportInvalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				if (drawerView.getId() == R.id.right_menu_scrollView) {
					mRightDrawerOpened = true;
					ALog.i(ALog.MAINACTIVITY, "Right drawer open");
				} else if (drawerView.getId() == R.id.left_menu_listView) {
					mLeftDrawerOpened = true;
					ALog.i(ALog.MAINACTIVITY, "Left drawer open");
				}
				supportInvalidateOptionsMenu();
			}
		};

		mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

		/** Initialise left menu items and click listener */
		mListViewLeft = (ListView) findViewById(R.id.left_menu_listView);
		mListViewLeft.setAdapter(new ListItemAdapter(this, getLeftMenuItems()));
		mListViewLeft.setOnItemClickListener(new MenuItemClickListener());

		/** Initiazlise right menu items and click listener */
		mScrollViewRight = (ScrollView) findViewById(R.id.right_menu_scrollView);
		rightMenuClickListener = new RightMenuClickListener(this);

		ViewGroup group = (ViewGroup) findViewById(R.id.right_menu_layout);
		setAllButtonListener(group);

		tvAirStatusAqiValue = (TextView) findViewById(R.id.tv_rm_air_status_aqi_value);
		tvConnectionStatus = (TextView) findViewById(R.id.tv_connection_status);
		tvAirStatusMessage = (TextView) findViewById(R.id.tv_rm_air_status_message);
		ivAirStatusBackground = (ImageView) findViewById(R.id.iv_rm_air_status_background);
		ivConnectedImage = (ImageView) findViewById(R.id.iv_connection_status);
		initFilterStatusViews();
		connected = false;
		
		getSupportFragmentManager().beginTransaction()
				.add(R.id.llContainer, getDashboard())
				.addToBackStack(null)
				.commit();


		filter = new IntentFilter();
		filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);

		isGooglePlayServiceAvailable = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		outdoorLocationPrefs = getSharedPreferences(OUTDOOR_LOCATION_PREFS,
				Context.MODE_PRIVATE);
		HashMap<String, String> outdoorLocationsMap = 
				(HashMap<String, String>) outdoorLocationPrefs.getAll();
		outdoorLocationsList = new ArrayList<String>();
		int size = outdoorLocationsMap.size();
		for (int i = 0; i < size; i++) {
			outdoorLocationsList.add(outdoorLocationsMap.get("" + i));
		}
		outdoorLocationsAdapter = new ArrayAdapter<String>(this,
				R.layout.list_item, R.id.list_text, outdoorLocationsList);

		
		initializeCPPController();
		createNetworkReceiver();
		this.registerReceiver(networkReceiver, filter);
		connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		removeFirmwareUpdateUI();
		hideFirmwareUpdateHomeIcon();
	}
	

	private void removeFirmwareUpdateUI() {
		setFirmwareSuperScript(0, false);
	}

	@Override
	protected void onPause() {
		super.onPause();
		/**
		 * Close Database
		 */
		purifierDatabase.closeDb();
		/**
		 * 
		 */
		if (outdoorLocationPrefs != null) {
			Editor editor = outdoorLocationPrefs.edit();
			editor.clear();
			int count = outdoorLocationsAdapter.getCount();
			for (int i = 0; i < count; i++) {
				editor.putString("" + i, outdoorLocationsAdapter.getItem(i));
			}
			editor.commit();
		}
		EWSDialogFactory.getInstance(this).cleanUp();

		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		boolean isScreenOn = powerManager.isScreenOn();

		if (!isScreenOn && !isDiagnostics) {
			if (!isClickEvent) {
				disableRightMenuControls();
				stopService = true;
				stopAllServices();
			}
			isClickEvent = false;
		}
	}

	@Override
	protected void onRestart() {
		ALog.i(ALog.MAINACTIVITY, "onRestart: stopService is: " + stopService);
		isEWSStarted = false;
		if (stopService) {
			SsdpService.getInstance().startDeviceDiscovery(this);
			stopService = false;
			this.registerReceiver(networkReceiver, filter);
		}
		super.onRestart();
	}

	@Override
	protected void onStop() {
		if (progressDialog != null) {
			progressDialog.cancel();
		}
		super.onStop();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus && !connected) {
			disableRightMenuControls();
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onBackPressed() {
		FragmentManager manager = getSupportFragmentManager();
		int count = manager.getBackStackEntryCount();
		Fragment fragment = manager.findFragmentById(R.id.llContainer);

		if (fragment instanceof OutdoorLocationsFragment
				&& android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
			invalidateOptionsMenu();
		}

		if (mLeftDrawerOpened || mRightDrawerOpened) {
			mDrawerLayout.closeDrawer(mListViewLeft);
			mDrawerLayout.closeDrawer(mScrollViewRight);
			mLeftDrawerOpened = false;
			mRightDrawerOpened = false;
		} else if (!(fragment instanceof HomeFragment)
				&& !(fragment instanceof ProductRegistrationStepsFragment)) {
			manager.popBackStackImmediate(null,
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
			showFragment(getDashboard());
			setDashboardActionbarIconVisible();
			setTitle(getString(R.string.dashboard_title));
		} else if (fragment instanceof ProductRegistrationStepsFragment) {
			manager.popBackStack();
		} else {
//			manager.popBackStackImmediate(null,	FragmentManager.POP_BACK_STACK_INCLUSIVE);
			finish();
		}
	}

	@Override
	protected void onUserLeaveHint() {
		ALog.i(ALog.MAINACTIVITY, "onUserLeaveHint");
		if (!isClickEvent && !isDiagnostics) {
			disableRightMenuControls();
			stopService = true;
			stopAllServices();
		}
		isClickEvent = false;
		super.onUserLeaveHint();
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		ALog.i(ALog.MAINACTIVITY, "onActivityResult: " + resultCode
				+ " requestCode " + requestCode);
		switch (requestCode) {
		case AppConstants.EWS_REQUEST_CODE:
			if (resultCode == RESULT_OK) {
				isDeviceDiscovered = true;
				updatePurifierName();
				toggleConnection(true);
			}

			SsdpService.getInstance().startDeviceDiscovery(this);
			
			if (dbPurifierDetailDtoList != null
					&& dbPurifierDetailDtoList.size() > 0) {
				dbPurifierDetailDtoList.clear();
			}

			dbPurifierDetailDtoList = purifierDatabase.getAllPurifiers(ConnectionState.DISCONNECTED);

			this.registerReceiver(networkReceiver, filter);

			isEWSStarted = false;
			break;

		case AppConstants.FIRMWARE_REQUEST_CODE:
			ALog.i(ALog.ACTIVITY,
					"MainActivity$onActivityResult FIRMWARE_REQUEST_CODE");
			break;

		default:
			break;
		}
	}

//	private void checkForFirmwareUpdate() {
//		String firmwareUrl = Utils.getPortUrl(Port.FIRMWARE, Utils.getIPAddress());
//		FirmwareUpdateTask task = new FirmwareUpdateTask(this);
//		task.execute(firmwareUrl);
//	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_cancel_search:
			cancelSearch = true;
			actionMode.finish();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getWindow().getCurrentFocus()
					.getWindowToken(), 0);
			break;
		default:
			break;
		}
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		this.menu = menu;
		MenuItem item = menu.findItem(R.id.right_menu);
		rightMenuItem = menu.findItem(R.id.right_menu);

		if (connected)
			item.setIcon(R.drawable.right_bar_icon_blue_2x);
		else
			item.setIcon(R.drawable.right_bar_icon_orange_2x);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		FragmentManager manager = getSupportFragmentManager();
		Fragment fragment = manager.findFragmentById(R.id.llContainer);
		MenuItem item = this.menu.findItem(R.id.add_location_menu);
		if (fragment instanceof OutdoorLocationsFragment) {
			item.setVisible(true);
		} else {
			item.setVisible(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}*/


	@Override
	protected void onDestroy() {
		resetSessionObject();
		stopAllServices();
		super.onDestroy();
	}

	private void initializeCPPController() {
		cppController = CPPController.getInstance(this);
		cppController.addDeviceDetailsListener(this);
		cppController.addSignonListener(this) ;
	}

	private void createNetworkReceiver() {
		ALog.i(ALog.MAINACTIVITY, "createNetworkReceiver");
		networkReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

				if (intent.getAction().equals(
						WifiManager.NETWORK_STATE_CHANGED_ACTION)
						|| intent.getAction().equals(
								ConnectivityManager.CONNECTIVITY_ACTION)) {
					NetworkInfo netInfo = conMan.getActiveNetworkInfo();
					if (netInfo != null && netInfo.isConnected()) {
						ALog.i(ALog.MAINACTIVITY, "onReceive---CONNECTED");

						if (cppController != null) {
							cppController.signOnWithProvisioning();
						}
						discoveryTimer.start() ;
						if (SessionDto.getInstance().getOutdoorEventDto() == null) {
							//TODO : 
//							getDashboard().startOutdoorAQITask();
						}

						List<Weatherdto> weatherDto = SessionDto.getInstance()
								.getWeatherDetails();
						if (weatherDto == null || weatherDto.size() < 1) {
							//TODO
//							getDashboard().startWeatherDataTask();
						}
					}
				}

				NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				NetworkInfo mobileWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) ;

				if( !mWifi.isConnected() && !mobileWifi.isConnected() ) {
					disableRightMenuControls() ;
				}
			}
		};
	}

	private void startLocalConnection() {
		connected = true;
		stopRemoteConnection() ;

		PurAirDevice purifier = getCurrentPurifier();
		purifier.setConnectionState(ConnectionState.CONNECTED_LOCALLY);
		ALog.i(ALog.SUBSCRIPTION, "Start LocalConnection for purifier: " + purifier) ;
		
		//Start the subscription every time it discovers the Purifier
		airPurifierController.addAirPurifierEventListener(this);
		airPurifierController.subscribeToAllEvents(purifier);
		SubscriptionManager.getInstance().enableLocalSubscription();
	}

	private void stopLocalConnection() {
		ALog.i(ALog.CONNECTIVITY, "Stop LocalConnection") ;
		airPurifierController.removeAirPurifierEventListener(this);
		SubscriptionManager.getInstance().disableLocalSubscription();
	}

	private void startRemoteConnection() {
		ALog.i(ALog.CONNECTIVITY, "Start RemoteConnection") ;
		PurAirDevice purifier = getCurrentPurifier();
		
		if (purifier == null) {
			ALog.e(ALog.CONNECTIVITY, "Failed to start RemoteConnection - purifier was null") ;
			return;
		}
		ALog.e(ALog.CONNECTIVITY, "Trying to remote connect to Purifier with eui64 - " + purifier.getEui64()) ;
		
		long pairedOn = purifierDatabase.getPurifierLastPairedOn(purifier);
		if( pairedOn > 0 ) {
			stopLocalConnection() ;
			
			purifier.setConnectionState(ConnectionState.CONNECTED_REMOTELY);
			airPurifierController.subscribeToAllEvents(purifier) ;
			cppController.startDCSService() ;
			ALog.e(ALog.CONNECTIVITY, "Successfully started remote connection") ;
		}
	}

	private void stopRemoteConnection() {
		ALog.i(ALog.CONNECTIVITY, "Stop RemoteConnection") ;
		cppController.stopDCSService() ;
	}

	public DrawerLayout getDrawerLayout() {
		return mDrawerLayout;
	}

	public ScrollView getScrollViewRight() {
		return mScrollViewRight;
	}

	public void stopAllServices() {
		secretKey = null;
		stopRemoteConnection() ;
		stopLocalConnection() ;

		if (discoveryTimer != null) {
			discoveryTimer.cancel();
		}

		if (networkReceiver != null) {
			try {
				this.unregisterReceiver(networkReceiver);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		SsdpService.getInstance().stopDeviceDiscovery();
		isDeviceDiscovered = false;
		CPPController.reset();
	}

	private void resetSessionObject() {
		SessionDto.reset();
	}

	/** Need to have only one instance of the HomeFragment */
	public static HomeFragment getDashboard() {
		if (homeFragment == null) {
			homeFragment = new HomeFragment();
		}
		return homeFragment;
	}

	/**
	 * @param viewGroup
	 *            loops through the entire view group and adds an
	 *            onClickListerner to the buttons.
	 */
	public void setAllButtonListener(ViewGroup viewGroup) {
		View v;
		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			v = viewGroup.getChildAt(i);
			if (v instanceof ViewGroup) {
				setAllButtonListener((ViewGroup) v);
			} else if (v instanceof Button) {
				((Button) v).setOnClickListener(rightMenuClickListener);
			}
		}
	}

	private void initActionBar() {
		ActionBar mActionBar = getSupportActionBar();
		mActionBar.setIcon(null);
		mActionBar.setHomeButtonEnabled(false);
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		View view = getLayoutInflater().inflate(R.layout.action_bar, null);
		rightMenu = (ImageView) view.findViewById(R.id.right_menu_img);
		leftMenu = (ImageView) view.findViewById(R.id.left_menu_img);
		backToHome = (ImageView) view.findViewById(R.id.back_to_home_img);
		addLocation = (ImageView) view.findViewById(R.id.add_location_img);
		noOffFirmwareUpdate = (FontTextView) view.findViewById(R.id.actionbar_firmware_no_off_update);
		
		rightMenu.setOnClickListener(actionBarClickListener);
		leftMenu.setOnClickListener(actionBarClickListener);
		backToHome.setOnClickListener(actionBarClickListener);
		addLocation.setOnClickListener(actionBarClickListener);
		mActionBar.setCustomView(view);
		
		//setTitle(getString(R.string.dashboard_title));
	}
	
	private OnClickListener actionBarClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.right_menu_img:
				
				if (mRightDrawerOpened) {
					mDrawerLayout.closeDrawer(mScrollViewRight);
				} else {
					mDrawerLayout.closeDrawer(mListViewLeft);
					mDrawerLayout.openDrawer(mScrollViewRight);
				}
				break;
			case R.id.left_menu_img:
				if (mLeftDrawerOpened) {
					mDrawerLayout.closeDrawer(mListViewLeft);
				} else {
					mDrawerLayout.closeDrawer(mScrollViewRight);
					mDrawerLayout.openDrawer(mListViewLeft);
				}
				break;
			case R.id.add_location_img:
				//TODO
				break;
			case R.id.back_to_home_img:
				//TODO
				break;
			default:
				break;
			}
			
		}
	}; 
	
	private void setDashboardActionbarIconVisible() {
		rightMenu.setVisibility(View.VISIBLE);
		leftMenu.setVisibility(View.VISIBLE);
		backToHome.setVisibility(View.INVISIBLE);
		addLocation.setVisibility(View.INVISIBLE);
	}

	/** Create the left menu items. */
	private List<ListViewItem> getLeftMenuItems() {
		List<ListViewItem> leftMenuItems = new ArrayList<ListViewItem>();

		leftMenuItems.add(new ListViewItem(R.string.list_item_home,
				R.drawable.icon_1_2x));
		leftMenuItems
		.add(new ListViewItem(R.string.list_item_air_quality_explained,
				R.drawable.icon_2_2x));
		leftMenuItems.add(new ListViewItem(R.string.list_item_outdoor_loc,
				R.drawable.icon_3_2x));
		leftMenuItems.add(new ListViewItem(R.string.list_item_notifications,
				R.drawable.icon_4_2x));
		leftMenuItems.add(new ListViewItem(R.string.list_item_help_and_doc,
				R.drawable.icon_5_2x));
		leftMenuItems.add(new ListViewItem(R.string.list_item_settings,
				R.drawable.icon_6_2x));
		leftMenuItems.add(new ListViewItem(R.string.list_item_firmware,
				R.drawable.icon_8_2x));
		leftMenuItems.add(new ListViewItem(R.string.list_item_prod_reg,
				R.drawable.icon_7_2x));
		leftMenuItems.add(new ListViewItem(R.string.list_item_buy_online,
				R.drawable.icon_8_2x));
		leftMenuItems
		.add(new ListViewItem(R.string.tools, R.drawable.icon_6_2x));
		return leftMenuItems;
	}

	private void setRightMenuAQIValue(float indoorAQI) {
		if (indoorAQI <= 1.4f) {
			tvAirStatusAqiValue.setText(getString(R.string.good));
		} else if (indoorAQI > 1.4f && indoorAQI <= 2.3f) {
			tvAirStatusAqiValue.setText(getString(R.string.moderate));
		} else if (indoorAQI > 2.3f && indoorAQI <= 3.5f) {
			tvAirStatusAqiValue.setText(getString(R.string.unhealthy));
		} else if (indoorAQI > 3.5f) {
			tvAirStatusAqiValue.setText(getString(R.string.very_unhealthy));
		}
	}

	private void setRightMenuAirStatusMessage(String message) {
		tvAirStatusMessage.setText(message);
	}

	private void setRightMenuAirStatusBackground(float indoorAQI) {
		Drawable imageDrawable = null;

		if (indoorAQI <= 1.4f) {
			imageDrawable = getResources().getDrawable(
					R.drawable.aqi_small_circle_2x);
		} else if (indoorAQI > 1.4f && indoorAQI <= 2.3f) {
			imageDrawable = getResources().getDrawable(
					R.drawable.aqi_small_circle_100_150_2x);
		} else if (indoorAQI > 2.3f && indoorAQI <= 3.5f) {
			imageDrawable = getResources().getDrawable(
					R.drawable.aqi_small_circle_200_300_2x);
		} else if (indoorAQI > 3.5f) {
			imageDrawable = getResources().getDrawable(
					R.drawable.aqi_small_circle_300_500_2x);
		}
		ivAirStatusBackground.setImageDrawable(imageDrawable);
	}

	private void setRightMenuConnectedStatus(ConnectionState state) {
		ALog.i(ALog.MAINACTIVITY, "Connection status: " + state);
		if (state.equals(ConnectionState.CONNECTED_LOCALLY)) {
			tvConnectionStatus.setText(getString(R.string.connected));
			ivConnectedImage.setImageDrawable(getResources().getDrawable(R.drawable.wifi_icon_blue_2x));
			rightMenu.setImageDrawable(getResources().getDrawable(R.drawable.right_bar_icon_blue_2x)); 
		} else if (state.equals(ConnectionState.CONNECTED_REMOTELY)) {
			tvConnectionStatus.setText(getString(R.string.connected_via_philips));
			ivConnectedImage.setImageDrawable(getResources().getDrawable(R.drawable.wifi_icon_blue_2x));
			rightMenu.setImageDrawable(getResources().getDrawable(R.drawable.right_bar_icon_blue_2x));
		} else {
			tvConnectionStatus.setText(getString(R.string.not_connected));
			ivConnectedImage.setImageDrawable(getResources().getDrawable(R.drawable.wifi_icon_lost_connection_2x));
			rightMenu.setImageDrawable(getResources().getDrawable(R.drawable.right_bar_icon_orange_2x));
		}
	}
	
	private void initFilterStatusViews() {
		preFilterView = (FilterStatusView) findViewById(R.id.iv_pre_filter);
		multiCareFilterView = (FilterStatusView) findViewById(R.id.iv_multi_care_filter);
		activeCarbonFilterView = (FilterStatusView) findViewById(R.id.iv_active_carbon_filter);
		hepaFilterView = (FilterStatusView) findViewById(R.id.iv_hepa_filter);

		preFilterText = (TextView) findViewById(R.id.tv_rm_pre_filter_status);
		multiCareFilterText = (TextView) findViewById(R.id.tv_rm_multi_care_filter_status);
		activeCarbonFilterText = (TextView) findViewById(R.id.tv_rm_active_carbon_filter_status);
		hepaFilterText = (TextView) findViewById(R.id.tv_rm_hepa_filter_status);
	}


	private ActionMode.Callback callback = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getWindow().getCurrentFocus()
					.getWindowToken(), 0);
			actionMode = null;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.context_search_bar, menu);
			LinearLayout searchlayout = (LinearLayout) getLayoutInflater()
					.inflate(R.layout.search_bar, null);
			cancelSearchItem = (TextView) searchlayout
					.findViewById(R.id.tv_cancel_search);
			cancelSearchItem.setOnClickListener(MainActivity.this);
			mode.setCustomView(searchlayout);
			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem menu) {
			return true;
		}
	};

	private ArrayAdapter<String> outdoorLocationsAdapter;

	public ArrayAdapter<String> getOutdoorLocationsAdapter() {
		return outdoorLocationsAdapter;
	}

	public void setOutdoorLocationsAdapter(
			ArrayAdapter<String> outdoorLocationsAdapter) {
		this.outdoorLocationsAdapter = outdoorLocationsAdapter;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
			mDrawerLayout.closeDrawer(mScrollViewRight);
			return true;
		}

		FragmentManager manager = getSupportFragmentManager();
		Fragment fragment = manager.findFragmentById(R.id.llContainer);

		switch (item.getItemId()) {
		case R.id.right_menu:
			if (mRightDrawerOpened) {
				mDrawerLayout.closeDrawer(mListViewLeft);
				mDrawerLayout.closeDrawer(mScrollViewRight);
			} else {
				mDrawerLayout.closeDrawer(mListViewLeft);
				mDrawerLayout.openDrawer(mScrollViewRight);
			}
			break;
		case R.id.add_location_menu:
			if (fragment instanceof OutdoorLocationsFragment) {
				actionMode = startSupportActionMode(callback);

				Map<String, City> citiesMap = SessionDto.getInstance()
						.getCityDetails().getCities();
				List<City> citiesList = new ArrayList<City>(citiesMap.values());

				final List<String> cityNamesList = new ArrayList<String>();
				Iterator<City> iterator = citiesList.iterator();

				while (iterator.hasNext()) {
					String cityName = iterator.next().getKey();
					cityName = cityName.substring(0, 1).toUpperCase()
							+ cityName.substring(1);
					cityNamesList.add(cityName);
				}
				Collections.sort(cityNamesList);

				ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, cityNamesList);
				final DragSortListView listView = (DragSortListView) findViewById(R.id.outdoor_locations_list);
				AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.actv_cities_list);
				actv.setAdapter(adapter);
				actv.setThreshold(0);
				actv.showDropDown();
				actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						actionMode.finish();

					}
				});

				actv.setValidator(new AutoCompleteTextView.Validator() {

					@Override
					public boolean isValid(CharSequence text) {
						if (cancelSearch) {
							cancelSearch = false;
							return false;
						}
						if (cityNamesList.contains(text.toString())) {
							if (outdoorLocationsAdapter.getPosition(text
									.toString()) != -1) {
								return false;
							}
							outdoorLocationsAdapter.add(text.toString());
							outdoorLocationsAdapter.notifyDataSetChanged();
							listView.invalidate();
							return true;
						} else {
							Toast.makeText(MainActivity.this, "Inalid text",
									Toast.LENGTH_LONG).show();
							return false;
						}
					}

					@Override
					public CharSequence fixText(CharSequence invalidText) {
						// TODO Auto-generated method stub
						return null;
					}
				});
			}
			break;
		default:
			break;
		}
		return false;
	}

	public void showFragment(Fragment fragment) {

		stopService = false ;
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.llContainer, fragment,
				fragment.getTag());
		fragmentTransaction.addToBackStack(fragment.getTag()) ;
		fragmentTransaction.commit();

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (getWindow() != null && getWindow().getCurrentFocus() != null) {
			imm.hideSoftInputFromWindow(getWindow().getCurrentFocus()
					.getWindowToken(), 0);
		}		
		
	}

	public void setTitle(String title) {
		TextView textView = (TextView) findViewById(R.id.action_bar_title);
		textView.setTypeface(Fonts.getGillsansLight(MainActivity.this));
		textView.setTextSize(24);
		textView.setText(title);
		super.setTitle(title);
	}

	/** Left menu item clickListener */
	private class MenuItemClickListener implements OnItemClickListener {

		private List<Fragment> leftMenuItems = new ArrayList<Fragment>();

		public MenuItemClickListener() {
			initLeftMenu();
		}

		private void initLeftMenu() {
			leftMenuItems.add(getDashboard());
			leftMenuItems.add(new AirQualityFragment());
			leftMenuItems.add(new OutdoorLocationsFragment());
			leftMenuItems.add(new NotificationsFragment());
			leftMenuItems.add(new HelpAndDocFragment());
			leftMenuItems.add(new SettingsFragment());
			leftMenuItems.add(new FirmwareUpdateFragment());
			leftMenuItems.add(new ProductRegFragment());
			leftMenuItems.add(new BuyOnlineFragment());
			leftMenuItems.add(new ToolsFragment());
		}

		@Override
		public void onItemClick(AdapterView<?> listView, View listItem,
				int position, long id) {
			isClickEvent = true;
			setDashboardActionbarIconVisible();
			mDrawerLayout.closeDrawer(mListViewLeft);
			switch (position) {
			case 0:
				showFragment(leftMenuItems.get(position));
				setTitle(getString(R.string.dashboard_title));
				break;
			case 1:
				showFragment(leftMenuItems.get(position));
				setTitle(getString(R.string.list_item_air_quality_explained));
				break;
			case 2:
				// Outdoor locations
				showFragment(leftMenuItems.get(position));
				setTitle(getString(R.string.list_item_outdoor_loc));
				rightMenu.setVisibility(View.INVISIBLE);
				addLocation.setVisibility(View.VISIBLE);
				break;
			case 3:
				// Notifications
				showFragment(leftMenuItems.get(position));
				setTitle(getString(R.string.list_item_notifications));
				break;
			case 4:
				// Help and documentation
				showFragment(leftMenuItems.get(position));
				setTitle(getString(R.string.list_item_help_and_doc));
				break;
			case 5:
				// Settings
				showFragment(leftMenuItems.get(position));
				setTitle(getString(R.string.list_item_settings));
				break;
			case 6:
				// Firmware update
				startFirmwareUpgradeActivity();
				break;
			case 7:
				// Product registration
				break;
			case 8:
				// Buy Online
				showFragment(leftMenuItems.get(position));
				setTitle(getString(R.string.list_item_buy_online));
				break;
			case 9:
				// Tools
				showFragment(leftMenuItems.get(position));
				setTitle(getString(R.string.tools));
				break;
			default:
				break;
			}
		}
	}

	public void startFirmwareUpgradeActivity() {
		if (getCurrentPurifier() == null) {
			ALog.d(ALog.MAINACTIVITY, "Did not start FirmwareUpdateActivity - Current Purifier null");
			Toast.makeText(MainActivity.this, R.string.firmware_toast_nodeviceselected, Toast.LENGTH_SHORT).show();
			return;
		}
		Intent firmwareIntent = new Intent(MainActivity.this, FirmwareUpdateActivity.class);
		startActivityForResult(firmwareIntent, AppConstants.FIRMWARE_REQUEST_CODE);
	}

	public static int getScreenWidth() {
		return screenWidth;
	}

	public static int getScreenHeight() {
		return screenHeight;
	}

	@Override
	public void airPurifierEventReceived(AirPortInfo airPurifierDetails) {
		ALog.i(ALog.AIRPURIFIER_CONTROLER, "Controller callback: "+airPurifierDetails) ;
		if (airPurifierDetails != null) {
			setAirPortInfo(airPurifierDetails);
			
			ALog.i(ALog.MAINACTIVITY, "UDP Event Received");
			PurAirDevice purifier = getCurrentPurifier();
			if (purifier != null) {
				purifier.setConnectionState(ConnectionState.CONNECTED_LOCALLY);
			}
			handler.sendEmptyMessage(0) ;
		}
	}
	
	@Override
	public void firmwareEventReceived(final FirmwarePortInfo firmwarePortInfo) {
		ALog.i(ALog.FIRMWARE, "MainActivity$firmwareEventReceived firmwareEventDto Version " + firmwarePortInfo.getVersion() + " Upgrade " + firmwarePortInfo.getUpgrade() + " UpdateAvailable " + firmwarePortInfo.isUpdateAvailable());
		if(firmwarePortInfo != null) {
			setFirmwarePortInfo(firmwarePortInfo);

			if (firmwarePortInfo.isUpdateAvailable()) {
				ALog.i(ALog.FIRMWARE, "Update Dashboard UI");

				this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//TODO
						// getDashboard().showFirmwareUpdatePopup();
						// Change hardcoded value "1" to number of devices discovered after SSDP once multiple purifiers are implemented.
						updateDashboardOnFirmwareUpdate(1);
						setFirmwareSuperScript(1, true);
					}
				});
			}
		}
	}
	
	private void updateDashboardOnFirmwareUpdate(int purifierCount) {
		showFirmwareUpdateHomeIcon(purifierCount);
	}
	
	private void showFirmwareUpdateHomeIcon(int purifierCount) {
		noOffFirmwareUpdate.setVisibility(View.VISIBLE);
		noOffFirmwareUpdate.setText(String.valueOf(purifierCount));
	}
	
	private void hideFirmwareUpdateHomeIcon() {
		noOffFirmwareUpdate.setVisibility(View.INVISIBLE);
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			updatePurifierUIFields() ;
		}
	};

	private void updatePurifierUIFields() {
		ALog.i(ALog.MAINACTIVITY, "updatePurifierUIFields");
		PurAirDevice purifier = getCurrentPurifier();
		AirPortInfo info = getAirPortInfo();
		if (null != info) {
			connected = true;
			float indoorAQIUsableValue = info.getIndoorAQI() / 10.0f;
			setRightMenuAQIValue(indoorAQIUsableValue);
			rightMenuClickListener.setSensorValues(info);
			updateFilterStatus(info.getFilterStatus1(),
					info.getFilterStatus2(),
					info.getFilterStatus3(),
					info.getFilterStatus4());
			if (purifier != null) {
				setRightMenuConnectedStatus(purifier.getConnectionState());
			} else {
				setRightMenuConnectedStatus(ConnectionState.DISCONNECTED);
			}
			setRightMenuAirStatusMessage(getString(
					Utils.getIndoorAQIMessage(indoorAQIUsableValue),
					getString(R.string.philips_home)));
			setRightMenuAirStatusBackground(indoorAQIUsableValue);
			rightMenuClickListener.disableControlPanel(connected,
					info);
		}
	}

	private void disableRightMenuControls() {
		ALog.i(ALog.MAINACTIVITY, "disableRightMenuControls");
		connected = false;
		setRightMenuConnectedStatus(ConnectionState.DISCONNECTED);
		rightMenuClickListener.disableControlPanel(connected,
				getAirPortInfo());
		setRightMenuAirStatusMessage(getString(R.string.rm_air_quality_message));
		setRightMenuAirStatusBackground(0);
	}

	private void updateFilterStatus(int preFilterStatus,
			int multiCareFilterStatus, int activeCarbonFilterStatus,
			int hepaFilterStatus) {
		/** Update filter bars */
		preFilterView.setPrefilterValue(preFilterStatus);
		multiCareFilterView.setMultiCareFilterValue(multiCareFilterStatus);
		activeCarbonFilterView
		.setActiveCarbonFilterValue(activeCarbonFilterStatus);
		hepaFilterView.setHEPAfilterValue(hepaFilterStatus);

		/** Update filter texts */
		preFilterText.setText(Utils.getPreFilterStatusText(preFilterStatus));
		multiCareFilterText.setText(Utils
				.getMultiCareFilterStatusText(multiCareFilterStatus));
		activeCarbonFilterText.setText(Utils
				.getActiveCarbonFilterStatusText(activeCarbonFilterStatus));
		hepaFilterText.setText(Utils
				.getHEPAFilterFilterStatusText(hepaFilterStatus));
	}

	public AirPortInfo getAirPortInfo() {
		PurAirDevice currentPurifier = getCurrentPurifier();
		if (currentPurifier == null) return null;
		
		return currentPurifier.getAirPortInfo();
	}

	private void setAirPortInfo(AirPortInfo airPortInfo) {
		PurAirDevice currentPurifier = getCurrentPurifier();
		if (currentPurifier == null) return;
		
		currentPurifier.setAirPortInfo(airPortInfo);
	}
	
	private void setFirmwarePortInfo(FirmwarePortInfo firmwarePortInfo) {
		PurAirDevice currentPurifier = getCurrentPurifier();
		if (currentPurifier == null) return;
		
		currentPurifier.setFirmwarePortInfo(firmwarePortInfo);
	}

	public void setRightMenuVisibility(boolean visible) {
		rightMenuItem.setVisible(visible);
	}

	public int getVisits() {
		return mVisits;
	}

	public boolean isGooglePlayServiceAvailable() {
		if (ConnectionResult.SUCCESS == isGooglePlayServiceAvailable) {
			return true;
		}
		return false;
	}

	public String getVersionNumber() {
		String versionCode = "";
		try {
			versionCode = getPackageManager().getPackageInfo(getPackageName(),
					0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	private boolean cancelSearch = false;

	/**
	 * Receive the device details from CPP
	 * 
	 */
	@Override
	public void onReceivedDeviceDetails(AirPortInfo airPortInfo) {
		ALog.i(ALog.MAINACTIVITY, "OnReceive device details from DCS: " + airPortInfo);
		if (airPortInfo != null) {
			PurAirDevice purifier = getCurrentPurifier();
			if (purifier != null) {
				purifier.setConnectionState(ConnectionState.CONNECTED_REMOTELY);
			}
			setAirPortInfo(airPortInfo);
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					updatePurifierUIFields();
				}
			});
		}
	}

	public void toggleConnection(boolean isLocal) {
		ALog.i(ALog.MAINACTIVITY, "toggleConnection: " + isLocal);

		if (isLocal) {
			discoveryTimer.cancel() ;

			startLocalConnection();
		} else {
			startRemoteConnection() ;
		}
	}


	private CountDownTimer discoveryTimer = new CountDownTimer(10000, 1000) {
		@Override
		public void onTick(long millisUntilFinished) {

		}

		@Override
		public void onFinish() {
			ALog.i(ALog.MAINACTIVITY, "Discovery timeout");
			toggleConnection(false);
		}
	};	

	private long ssdpDiscoveredBootId = 0L;
	@Override
	public boolean handleMessage(Message msg) {
		DeviceModel device = null;
		if (null != msg) {
			final DiscoveryMessageID message = DiscoveryMessageID
					.getID(msg.what);
			final InternalMessage internalMessage = (InternalMessage) msg.obj;
			if (null != internalMessage
					&& internalMessage.obj instanceof DeviceModel) {
				device = (DeviceModel) internalMessage.obj;
			}
			if (device == null) {
				return false;
			}

			switch (message) {
			case DEVICE_DISCOVERED:
				if (device.getSsdpDevice() == null 
					|| device.getSsdpDevice().getCppId() == null
					|| device.getSsdpDevice().getModelName() == null
					|| device.getSsdpDevice().getFriendlyName() == null) {
					return false;
				}
				ALog.i(ALog.MAINACTIVITY,
						"Device discovered usn: " + device.getUsn() + ", Ip: "
								+ device.getIpAddress()
								+ ", isDeviceDiscovered: " + isDeviceDiscovered
								+ ", isEWSStarted: " + isEWSStarted);
				long newDiscoveredBootId = 0L;
				try {
					newDiscoveredBootId = Long.parseLong(device.getBootID());
				} catch (NumberFormatException e) {
					// NOP
					e.printStackTrace();
				}
				
				if (newDiscoveredBootId != ssdpDiscoveredBootId) {
					isDeviceDiscovered = false;
				}
				
				if (device.getSsdpDevice().getModelName().contains(AppConstants.MODEL_NAME)
						&& !isDeviceDiscovered && !isEWSStarted) {
					onFirstDeviceDiscovered(device);
				}
				break;
			case DEVICE_LOST:
				onDeviceLost(device);
				break;
			default:
				break;
			}
			return false;
		}
		return false;
	}

	private boolean onFirstDeviceDiscovered(DeviceModel deviceModel) {

		isDeviceDiscovered = true;
		
		Long bootId = -1l;
		if (deviceModel.getBootID() != null && deviceModel.getBootID().length() > 0) {
			bootId = Long.parseLong(deviceModel.getBootID());
		}
		
		PurAirDevice purifier = new PurAirDevice(deviceModel.getSsdpDevice().getCppId(), deviceModel.getUsn(), deviceModel.getIpAddress(), deviceModel.getSsdpDevice().getFriendlyName(), bootId, ConnectionState.CONNECTED_LOCALLY);
		PurifierManager.getInstance().setCurrentPurifier(purifier);
		
		updatePurifierName();
		String ssdpDiscoveredUsn = purifier.getUsn();
		if (ssdpDiscoveredUsn == null || ssdpDiscoveredUsn.length() <= 0) {
			return true;
		}

		localDeviceUsn = ssdpDiscoveredUsn;
		
		try {
			ssdpDiscoveredBootId = purifier.getBootId();
		} catch (NumberFormatException e) {
			// NOP
			e.printStackTrace();
		}

		if (ssdpDiscoveredBootId == 0L) {
			startKeyExchange();
		}
		if (dbPurifierDetailDtoList != null) {
			boolean isDeviceInDb = false;
			for (PurAirDevice infoDto : dbPurifierDetailDtoList) {
				String dbUsn = infoDto.getUsn();
				if (dbUsn == null || dbUsn.length() <= 0) {
					continue;
				}
				if (dbUsn.equalsIgnoreCase(ssdpDiscoveredUsn)) {
					isDeviceInDb = true;
					long dbBootId = infoDto.getBootId();
					if (dbBootId == ssdpDiscoveredBootId
							&& infoDto.getEncryptionKey() != null) {
						ALog.i(ALog.MAINACTIVITY, "Device boot id is same: "
								+ dbBootId + " ssdp bootid: "
								+ ssdpDiscoveredBootId);
						secretKey = infoDto.getEncryptionKey();
						purifier.setEncryptionKey(infoDto.getEncryptionKey());
						toggleConnection(true);
					} else {
						startKeyExchange();
					}
					break;
				}
			}

			if (!isDeviceInDb) {
				startKeyExchange();
			}
		}

		return true;
	}

	private boolean onDeviceLost(DeviceModel device) {
		String ssdpDeviceUsn = device.getId();
		if (ssdpDeviceUsn == null || ssdpDeviceUsn.length() <= 0) {
			return false;
		}

		if (ssdpDeviceUsn.equalsIgnoreCase(localDeviceUsn)) {
			ALog.i(ALog.MAINACTIVITY, "Device Lost: " + ssdpDeviceUsn);
			disableRightMenuControls();
			toggleConnection(false);
			isDeviceDiscovered = false;
			secretKey = null;
		}
		return true;
	}

	private void updatePurifierName() {
		PurAirDevice purifier = getCurrentPurifier();
		if (purifier != null) {
			// set purifier name in dashboard
			//TODO : Set purifier name in IndoorFragment
//			homeFragment.setHomeName(purifier.getName());
		} else {
			// TODO decide on what to show when not connected
//			homeFragment.setHomeName(getString(R.string.not_connected));
		}
	}

	private void startKeyExchange() {
		PurAirDevice purifier = getCurrentPurifier();
		if (purifier == null) return;
		
		ALog.i(ALog.MAINACTIVITY, "start key exchange: isDeviceDiscovered-"
				+ isDeviceDiscovered);

		if (isDeviceDiscovered) {
			diSecurity.initializeExchangeKeyCounter(purifier.getEui64());
			diSecurity.exchangeKey(Utils.getPortUrl(Port.SECURITY,	purifier.getIpAddress()), purifier.getEui64());
		}
	}

	@Override
	public void keyDecrypt(String key, String deviceEui64) {
		ALog.i(ALog.MAINACTIVITY, "Key Decrypt: " + key + " DeviceID: " + deviceEui64);
		if (secretKey == null && key != null) {
			this.secretKey = key;
			pairToPurifierIfNecessary();

			PurAirDevice purifier = getCurrentPurifier();
			if (purifier != null && purifier.getEui64().equals(deviceEui64)) {
				purifier.setEncryptionKey(key);
				purifierDatabase.insertPurAirDevice(purifier);
			}


			if (dbPurifierDetailDtoList != null
					&& dbPurifierDetailDtoList.size() > 0) {
				dbPurifierDetailDtoList.clear();
			}
			dbPurifierDetailDtoList = purifierDatabase.getAllPurifiers(ConnectionState.DISCONNECTED);

			toggleConnection(true);

		}
	}

	private void setFirmwareSuperScript(int superscriptNumber, boolean isUpdateAvailable) {
		ListItemAdapter adapter = (ListItemAdapter) mListViewLeft.getAdapter();
		ListViewItem item = adapter.getItem(6);
		adapter.remove(item);
		if(!isUpdateAvailable) {
			item.setSuperScriptNumber(0);
		} else {
			item.setSuperScriptNumber(superscriptNumber);
		}
		adapter.insert(item, 6);
		adapter.notifyDataSetChanged();
		ALog.i(ALog.FIRMWARE, "LeftMenuList$firmwareItem " + item.getTextId());
	}

	@Override
	public void signonStatus(boolean signon) {
		if( signon && isDeviceDiscovered && secretKey != null) {
			pairToPurifierIfNecessary() ;
		}

	}

	private void pairToPurifierIfNecessary() {
		// Only show PairingDialog once (can be called via discovery and signon)
		if (isPairingDialogShown) return;

		if (!cppController.isSignOn()) return;

		PurAirDevice purifier = getCurrentPurifier();
		long lastPairingCheckTime = purifierDatabase.getPurifierLastPairedOn(purifier);
		if (lastPairingCheckTime <= 0) 
		{
			showPairingDialog(purifier);
			return;
		}

		long diffInDays = Utils.getDiffInDays(lastPairingCheckTime);
		if (diffInDays != 0) {
			startPairingWithoutListener(purifier);
		}
	}

	private void showPairingDialog(PurAirDevice purifier) {
		try
		{
			PairingDialogFragment dialog = PairingDialogFragment.newInstance(purifier, PairingDialogFragment.dialog_type.SHOW_DIALOG);
			FragmentManager fragMan = getSupportFragmentManager();
			dialog.show(fragMan, null);
			isPairingDialogShown = true;
		}catch(IllegalStateException e){
			isPairingDialogShown = false;
		}
	}

	public void startPairing(PurAirDevice purifier) {
		isClickEvent = true ;
		progressDialog = new ProgressDialog(MainActivity.this);
		progressDialog.setMessage(getString(R.string.pairing_progress));
		progressDialog.setCancelable(false);
		progressDialog.show();

		PairingManager pm = new PairingManager(this, purifier);
		pm.startPairing();
	}

	private void startPairingWithoutListener(PurAirDevice purifier) {
		PairingManager pm = new PairingManager(null, purifier);
		pm.startPairing();
	}

	@Override
	public void onPairingSuccess() {		
		if (progressDialog != null) {
			progressDialog.cancel();
		}
		try{
			PairingDialogFragment dialog = PairingDialogFragment.newInstance(null, PairingDialogFragment.dialog_type.PAIRING_SUCCESS);
			FragmentManager fragMan = getSupportFragmentManager();
			dialog.show(fragMan, null);
		}catch(IllegalStateException ex){
			ex.printStackTrace();
		}
		
	}

	@Override
	public void onPairingFailed() {
		if (progressDialog != null) {
			progressDialog.cancel();
		}
		try{
		PairingDialogFragment dialog = PairingDialogFragment.newInstance(getCurrentPurifier(), PairingDialogFragment.dialog_type.PAIRING_FAILED);
		FragmentManager fragMan = getSupportFragmentManager();
		dialog.show(fragMan, null);
		}catch(IllegalStateException ex){
			ex.printStackTrace();
		}
	}
	
	public PurAirDevice getCurrentPurifier() {
		// TODO change to field in class
		return PurifierManager.getInstance().getCurrentPurifier();
	}
}
