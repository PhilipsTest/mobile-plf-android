package com.philips.cl.di.dev.pa.pureairui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.philips.cl.di.dev.pa.R;
import com.philips.cl.di.dev.pa.controller.SensorDataController;
import com.philips.cl.di.dev.pa.customviews.FilterStatusView;
import com.philips.cl.di.dev.pa.customviews.ListViewItem;
import com.philips.cl.di.dev.pa.customviews.adapters.ListItemAdapter;
import com.philips.cl.di.dev.pa.customviews.adapters.RightMenuControlPanelAdapter;
import com.philips.cl.di.dev.pa.dto.AirPurifierEventDto;
import com.philips.cl.di.dev.pa.interfaces.ICPEventListener;
import com.philips.cl.di.dev.pa.interfaces.SensorEventListener;
import com.philips.cl.di.dev.pa.listeners.RightMenuClickListener;
import com.philips.cl.di.dev.pa.pureairui.fragments.AirQualityFragment;
import com.philips.cl.di.dev.pa.pureairui.fragments.BuyOnlineFragment;
import com.philips.cl.di.dev.pa.pureairui.fragments.HelpAndDocFragment;
import com.philips.cl.di.dev.pa.pureairui.fragments.HomeFragment;
import com.philips.cl.di.dev.pa.pureairui.fragments.NotificationsFragment;
import com.philips.cl.di.dev.pa.pureairui.fragments.OutdoorLocationsFragment;
import com.philips.cl.di.dev.pa.pureairui.fragments.ProductRegFragment;
import com.philips.cl.di.dev.pa.pureairui.fragments.ToolsFragment;
import com.philips.cl.di.dev.pa.util.Fonts;
import com.philips.cl.di.dev.pa.util.Utils;

public class MainActivity extends ActionBarActivity implements SensorEventListener {

	private static final String TAG = MainActivity.class.getSimpleName();

	private static int screenWidth, screenHeight;

	private ActionBar mActionBar;
	private DrawerLayout mDrawerLayout;
	private ListView mListViewLeft;
	private ScrollView mScrollViewRight;
	private RightMenuClickListener rightMenuClickListener;

	//Right menu stuff
	private RightMenuControlPanelAdapter rightMenuControlPanelAdapter;
	private TextView tvAirStatusAqiValue;
	
	//Filter status bars
	private FilterStatusView preFilterView, multiCareFilterView, activeCarbonFilterView, hepaFilterView;
	
	//Filter status texts
	private TextView preFilterText, multiCareFilterText, activeCarbonFilterText, hepaFilterText;

	private static HomeFragment homeFragment;

	private boolean mRightDrawerOpened = false;

	private ActionBarDrawerToggle mActionBarDrawerToggle;

	private SensorDataController sensorDataController;
	
	private static AirPurifierEventDto airPurifierEventDto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_aj);

		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		screenWidth = displayMetrics.widthPixels;
		screenHeight = displayMetrics.heightPixels;

		initActionBar();

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));

		mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_launcher, R.string.app_name, R.string.action_settings) 
		{

			@Override
			public void onDrawerClosed(View drawerView) {
				if(drawerView.getId() == R.id.right_menu) {
				}
				mRightDrawerOpened = false;
				supportInvalidateOptionsMenu();

			}

			@Override
			public void onDrawerOpened(View drawerView) {
				if(drawerView.getId() == R.id.right_menu) {
					mRightDrawerOpened = true;
				}
				supportInvalidateOptionsMenu();
			}

		};

		mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

		mListViewLeft = (ListView) findViewById(R.id.left_menu);
		mScrollViewRight = (ScrollView) findViewById(R.id.right_menu);

		rightMenuClickListener = new RightMenuClickListener(this);
		

		ViewGroup group = (ViewGroup)findViewById(R.id.right_menu_layout);
		setAllButtonListener(group);
		
		tvAirStatusAqiValue = (TextView) findViewById(R.id.tv_rm_air_status_aqi_value);
		
		mListViewLeft.setAdapter(new ListItemAdapter(this, getLeftMenuItems()));
		mListViewLeft.setOnItemClickListener(new MenuItemClickListener());

		int id = getSupportFragmentManager().beginTransaction()
				.add(R.id.llContainer, getDashboard(), HomeFragment.TAG)
				.addToBackStack(null)
				.commit();

		Log.i(TAG, "Home Fragment id " + id); 
		
		initFilterStatusViews();
		
		sensorDataController = new SensorDataController(this, this) ;
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume") ;
		super.onResume();
		sensorDataController.startPolling() ;
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "OnPause") ;
     	sensorDataController.stopPolling();
	}

	@Override
	public void onBackPressed() {
		Log.i(TAG, "onBackPressed");
		FragmentManager manager = getSupportFragmentManager();
		int count = manager.getBackStackEntryCount();
		Fragment fragment = manager.findFragmentById(R.id.llContainer);
		Log.i(TAG, "currentFragment " + fragment);
		Log.i(TAG, "onBackPressed count " + count);
		if(count > 1 && !(fragment instanceof HomeFragment)) {
			manager. popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			showFragment(getDashboard());
			setTitle("PHILIPS Smart Air Purifier");
		} else {

			finish();
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/** Need to have only one instance of the HomeFragment */

	public static HomeFragment getDashboard() {
		if(homeFragment == null) {
			homeFragment = new HomeFragment();
		}
		return homeFragment;
	}

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
		mActionBar = getSupportActionBar();
		mActionBar.setIcon(R.drawable.left_slidermenu_icon_2x);
		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
		mActionBar.setCustomView(R.layout.action_bar);

		TextView actionBarTitle = (TextView) findViewById(R.id.action_bar_title);
		actionBarTitle.setTypeface(Fonts.getGillsans(this));
	}

	//Create the left menu items.

	private List<ListViewItem> getLeftMenuItems() {
		List<ListViewItem> leftMenuItems = new ArrayList<ListViewItem>();

		leftMenuItems.add(new ListViewItem(R.string.list_item_1, R.drawable.icon_1_2x));
		leftMenuItems.add(new ListViewItem(R.string.list_item_2, R.drawable.icon_2_2x));
		leftMenuItems.add(new ListViewItem(R.string.list_item_3, R.drawable.icon_3_2x));
		leftMenuItems.add(new ListViewItem(R.string.list_item_4, R.drawable.icon_4_2x));
		leftMenuItems.add(new ListViewItem(R.string.list_item_5, R.drawable.icon_5_2x));
		leftMenuItems.add(new ListViewItem(R.string.list_item_6, R.drawable.icon_6_2x));
		leftMenuItems.add(new ListViewItem(R.string.list_item_7, R.drawable.icon_7_2x));
		leftMenuItems.add(new ListViewItem(R.string.list_item_8, R.drawable.icon_8_2x));

		return leftMenuItems;
	} 

	private void setRightMenuAQIValue(int aqiValue) {
		tvAirStatusAqiValue.setText(String.valueOf(aqiValue));
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem item = menu.getItem(0);

		//TODO : Check connection status and set right menu icon accordingly
		//		if(connected)
		item.setIcon(R.drawable.right_bar_icon_blue_2x);
		//		else
		//			item.setIcon(R.drawable.right_bar_icon_orange_2x);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(mActionBarDrawerToggle.onOptionsItemSelected(item)) {
			mDrawerLayout.closeDrawer(mScrollViewRight);
			return true;
		}
		switch (item.getItemId()) {
		case R.id.right_menu:
			if(mRightDrawerOpened) {
				mDrawerLayout.closeDrawer(mListViewLeft);
				mDrawerLayout.closeDrawer(mScrollViewRight);
			} else {
				mDrawerLayout.closeDrawer(mListViewLeft);
				mDrawerLayout.openDrawer(mScrollViewRight);
			}
			break;

		default:
			break;
		}
		return false;
	}

	public void showFragment(Fragment fragment) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.llContainer, fragment, fragment.getTag());
		fragmentTransaction.addToBackStack(null); 
		int id = fragmentTransaction.commit();
		Log.i(TAG, "showFragment position " + fragment + " id " + id);
		mDrawerLayout.closeDrawer(mListViewLeft);
	}

	public void setTitle(String title) {
		TextView textView = (TextView) findViewById(R.id.action_bar_title);
		textView.setTypeface(Fonts.getGillsansLight(MainActivity.this));
		textView.setTextSize(24); //TODO : Pass as param
		textView.setText(title);
	}


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
			leftMenuItems.add(new ToolsFragment());
			leftMenuItems.add(new ProductRegFragment());
			leftMenuItems.add(new BuyOnlineFragment());
		}

		private void setTitle(String title) {
			TextView textView = (TextView) findViewById(R.id.action_bar_title);
			textView.setTypeface(Fonts.getGillsansLight(MainActivity.this));
			textView.setTextSize(24); //TODO : Pass as param
			textView.setText(title);
		}

		@Override
		public void onItemClick(AdapterView<?> listView, View listItem, int position, long id) {

			switch (position) {
			case 0:
				showFragment(leftMenuItems.get(position));
				setTitle("PHILIPS Smart Air Purifier");
				mDrawerLayout.closeDrawer(mListViewLeft);
				break;
			case 1: 
				showFragment(leftMenuItems.get(position));
				setTitle("Air Quality Explained");
				mDrawerLayout.closeDrawer(mListViewLeft);
				break;
			case 2:
				//Outdoor locations
				showFragment(leftMenuItems.get(position));
				setTitle("Outdoor Locations");
				break;
			case 3:
				//Notifications
				showFragment(leftMenuItems.get(position));
				setTitle("Notifications");
				break;
			case 4:
				//Help and documentation
				showFragment(leftMenuItems.get(position));
				setTitle("Help and Documentation");
				break;
			case 5:
				//Tools
				showFragment(leftMenuItems.get(position));
				setTitle("Tools");
				break;
			case 6:
				//Product registration
				showFragment(leftMenuItems.get(position));
				setTitle("Product Registration");
				break;
			case 7:
				//Buy Online
				showFragment(leftMenuItems.get(position));
				setTitle("Buy Online");
				break;

			default:
				break;
			}
		}
	}

	public static int getScreenWidth() {
		return screenWidth;
	}

	public static int getScreenHeight() {
		return screenHeight;
	}

	@Override
	public void sensorDataReceived(AirPurifierEventDto airPurifierEventDto) {
		Log.i(TAG, "SensorDataReceived " + (!(airPurifierEventDto == null))) ;
		if ( null != airPurifierEventDto ) {
			setAirPurifierEventDto(airPurifierEventDto);
			updateDashboardFields(airPurifierEventDto) ;
			setRightMenuAQIValue(airPurifierEventDto.getIndoorAQI());
			rightMenuClickListener.setSensorValues(airPurifierEventDto);
			updateFilterStatus(airPurifierEventDto.getFilterStatus1(), airPurifierEventDto.getFilterStatus2(), airPurifierEventDto.getFilterStatus3(), airPurifierEventDto.getFilterStatus4());
		} 
		
	}

	private void updateFilterStatus(int preFilterStatus, int multiCareFilterStatus, int activeCarbonFilterStatus, int hepaFilterStatus) {
		Log.i(TAG, "filter values pre " + preFilterStatus + " multi " + multiCareFilterStatus + " active " + activeCarbonFilterStatus + " hepa " + hepaFilterStatus);
		Log.i(TAG, "screenwidth " + screenWidth);
		preFilterView.setPrefilterValue(preFilterStatus);
		multiCareFilterView.setMultiCareFilterValue(multiCareFilterStatus);
		activeCarbonFilterView.setActiveCarbonFilterValue(activeCarbonFilterStatus);
		hepaFilterView.setHEPAfilterValue(hepaFilterStatus);
		
		preFilterText.setText(Utils.getPreFilterStatusText(preFilterStatus));
		multiCareFilterText.setText(Utils.getMultiCareFilterStatusText(multiCareFilterStatus));
		activeCarbonFilterText.setText(Utils.getActiveCarbonFilterStatusText(activeCarbonFilterStatus));
		hepaFilterText.setText(Utils.getHEPAFilterFilterStatusText(hepaFilterStatus));
	}

	private void updateDashboardFields(AirPurifierEventDto airPurifierEventDto) {
		homeFragment.setIndoorAQIValue(airPurifierEventDto.getIndoorAQI()) ;
	}

	public static AirPurifierEventDto getAirPurifierEventDto() {
		return airPurifierEventDto;
	}

	private static void setAirPurifierEventDto(AirPurifierEventDto airPurifierEventDto) {
		MainActivity.airPurifierEventDto = airPurifierEventDto;
	}
}

