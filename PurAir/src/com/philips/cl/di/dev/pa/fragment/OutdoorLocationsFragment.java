package com.philips.cl.di.dev.pa.fragment;

import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.philips.cl.di.dev.pa.R;
import com.philips.cl.di.dev.pa.dashboard.HomeFragment;
import com.philips.cl.di.dev.pa.dashboard.OutdoorCityInfo;
import com.philips.cl.di.dev.pa.dashboard.OutdoorController;
import com.philips.cl.di.dev.pa.dashboard.OutdoorController.CurrentCityAreaIdReceivedListener;
import com.philips.cl.di.dev.pa.dashboard.OutdoorManager;
import com.philips.cl.di.dev.pa.outdoorlocations.AddOutdoorLocationActivity;
import com.philips.cl.di.dev.pa.outdoorlocations.AddOutdoorLocationHelper;
import com.philips.cl.di.dev.pa.outdoorlocations.OutdoorDataProvider;
import com.philips.cl.di.dev.pa.outdoorlocations.UserCitiesDatabase;
import com.philips.cl.di.dev.pa.util.ALog;
import com.philips.cl.di.dev.pa.util.Fonts;
import com.philips.cl.di.dev.pa.util.LanguageUtils;
import com.philips.cl.di.dev.pa.util.LocationUtils;
import com.philips.cl.di.dev.pa.util.MetricsTracker;
import com.philips.cl.di.dev.pa.util.TrackPageConstants;
import com.philips.cl.di.dev.pa.util.Utils;
import com.philips.cl.di.dev.pa.view.FontTextView;

public class OutdoorLocationsFragment extends BaseFragment implements ConnectionCallbacks,
	OnConnectionFailedListener, OnCheckedChangeListener, CurrentCityAreaIdReceivedListener {
	private static final String TAG = OutdoorLocationsFragment.class.getSimpleName();
	
	private boolean isGooglePlayServiceAvailable;
	//private ProgressBar searchingLoctionProgress;
	private ListView mOutdoorLocationListView;
	private Hashtable<String, Boolean> selectedItemHashtable;
	private ToggleButton edittogglebutton;
	private UserSelectedCitiesAdapter userSelectedCitiesAdapter;
	private UserCitiesDatabase userCitiesDatabase;
	private List<String> userCitiesId;
	private List<OutdoorCityInfo> outdoorCityInfoList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		isGooglePlayServiceAvailable = Utils.isGooglePlayServiceAvailable();
		Log.i(TAG, "isGooglePlayServiceAvailable " + isGooglePlayServiceAvailable);
		selectedItemHashtable = new Hashtable<String, Boolean>();
		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");
		View view = inflater.inflate(R.layout.my_outdoor_locations_fragment, container, false);
		edittogglebutton = (ToggleButton) view.findViewById(R.id.outdoor_location_edit_tb);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            //padding left
			edittogglebutton.setPadding(16,0,0,0);
        }
		edittogglebutton.setTypeface(Fonts.getCentraleSansLight(getActivity()));
		mOutdoorLocationListView = (ListView) view.findViewById(R.id.outdoor_locations_list);
		mOutdoorLocationListView.setOnItemClickListener(mOutdoorLocationsItemClickListener);
		LinearLayout addlocation = (LinearLayout)view.findViewById(R.id.add_outdoor_location_ll);
		LinearLayout currentlocation = (LinearLayout)view.findViewById(R.id.current_location_ll);
		addlocation.setOnClickListener(locationOnClickListener);
		currentlocation.setOnClickListener(locationOnClickListener);
		edittogglebutton.setChecked(false);
		edittogglebutton.setOnCheckedChangeListener(this);
		return view;
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		userCitiesDatabase = new UserCitiesDatabase();
		MetricsTracker.trackPage(TrackPageConstants.OUTDOOR_LOCATIONS);
	}
	
	@Override
	public void onResume() {
		showCurrentCityVisibility();
		if (!selectedItemHashtable.isEmpty()) selectedItemHashtable.clear();// remove selected items
		OutdoorController.getInstance().setCurrentCityAreaIdReceivedListener(this);
		setAdapter();
		super.onResume();
	}
	
	@Override
	public void onPause() {
		OutdoorController.getInstance().removeCurrentCityAreaIdReceivedListener();
		super.onPause();
	}
	
	private void showCurrentCityVisibility() {
		if (LocationUtils.getCurrentLocationAreaId().isEmpty()) {
			startCurrentCityAreaIdTask();
		}
	}
	
	private void startCurrentCityAreaIdTask() {
		String lat = LocationUtils.getCurrentLocationLat();
		String lon = LocationUtils.getCurrentLocationLon();
		if (!lat.isEmpty() && !lon.isEmpty()) {
			try {
				OutdoorController.getInstance().startGetAreaIDTask(Double.parseDouble(lon), Double.parseDouble(lat));
			} catch (NumberFormatException e) {
				ALog.e(ALog.ERROR, "OutdoorLocationFragment$showCurrentCityVisibility: " + "Error: " + e.getMessage());
			}
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if(result.hasResolution()) {
			Log.i(TAG, "onConnectionFailed#hasResolution");
		} else {
//			showErrorDialog(result.getErrorCode());
			Log.i(TAG, "onConnectionFailed#noResolution");
		}
	}
	
	private void addAreaIdToCityList() {
		OutdoorManager.getInstance().clearCitiesList();
		
		if (outdoorCityInfoList != null && !outdoorCityInfoList.isEmpty()) {
			
			for (OutdoorCityInfo outdoorCityInfo : outdoorCityInfoList) {
				String key = AddOutdoorLocationHelper.getCityKeyWithRespectDataProvider(outdoorCityInfo);
				OutdoorManager.getInstance().addAreaIDToUsersList(key);
			}
		}
		
		//Add current location 
		if (LocationUtils.isCurrentLocationEnabled()) {
			OutdoorManager.getInstance().addCurrentCityAreaIDToUsersList(LocationUtils.getCurrentLocationAreaId());
		} 
	}
	
	@Override
	public void onConnected(Bundle arg0) {/**NOP*/}

	@Override
	public void onDisconnected() {/**NOP*/}
	
	
	private void gotoPage(int position) {
		HomeFragment homeFragment = (HomeFragment) getParentFragment();
		if (homeFragment != null) {
			homeFragment.gotoOutdoorViewPage(position);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.getId() == R.id.outdoor_location_edit_tb) {
			setAdapter();
		}
	}

	@Override
	public void areaIdReceived() {
		if (getActivity() == null) return;
		
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				showCurrentCityVisibility();
			}
		});
	}
	
	
	private class UserSelectedCitiesAdapter extends ArrayAdapter<OutdoorCityInfo> {

		private Context context;
		private List<OutdoorCityInfo> outdoorCityInfoListAdapter;
		
		public UserSelectedCitiesAdapter(Context context, int resource, List<OutdoorCityInfo> outdoorCityInfoList) {
			super(context, resource, outdoorCityInfoList);
			this.context = context;
			this.outdoorCityInfoListAdapter = outdoorCityInfoList;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.simple_list_item, null);

			OutdoorCityInfo info = outdoorCityInfoListAdapter.get(position);
			final String key = AddOutdoorLocationHelper.getCityKeyWithRespectDataProvider(info);
			ImageView deleteSign = (ImageView) view.findViewById(R.id.list_item_delete);
			FontTextView tvName = (FontTextView) view.findViewById(R.id.list_item_name);
			FontTextView delete = (FontTextView) view.findViewById(R.id.list_item_right_text);
			deleteSign.setClickable(false);
		    deleteSign.setFocusable(false);
			
			String cityName = info.getCityName();
			if(LanguageUtils.getLanguageForLocale(Locale.getDefault()).contains("ZH-HANS")) {
				cityName = info.getCityNameCN();
			} else if(LanguageUtils.getLanguageForLocale(Locale.getDefault()).contains("ZH-HANT")) {
				cityName = info.getCityNameTW();
			}
			//Replace first latter Capital and append US Embassy
			if( info.getDataProvider() == OutdoorDataProvider.US_EMBASSY.ordinal()) {
				StringBuilder builder = new StringBuilder(AddOutdoorLocationHelper.getFirstWordCapitalInSentence(cityName)) ;
				builder.append(" (").append(context.getString(R.string.us_embassy)).append(" )") ;

				cityName = builder.toString() ;
			}
			tvName.setText(cityName);
			
			tvName.setTag(key);
			
			if (!edittogglebutton.isChecked()) {
				deleteSign.setVisibility(View.GONE);
				delete.setVisibility(View.GONE);
			} else {
				deleteSign.setVisibility(View.VISIBLE);
				if (selectedItemHashtable.containsKey(key) && selectedItemHashtable.get(key)) {
					delete.setVisibility(View.VISIBLE);
					deleteSign.setImageResource(R.drawable.red_cross);
				} else {
					delete.setVisibility(View.GONE);
					deleteSign.setImageResource(R.drawable.white_cross);
				}
			}
			
			delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					deleteItem(key);
					reloadList();
				}
			});

			return view;
		}
	}
	
	private void deleteItem(String key) {
		OutdoorManager.getInstance().removeAreaIDFromUsersList(key);
		new UserCitiesDatabase().deleteCity(key);
		
		if (selectedItemHashtable.containsKey(key)) {
			selectedItemHashtable.remove(key);
		}
	}
	
	private void reloadList() {
		HomeFragment homeFragment = (HomeFragment) getParentFragment();
		if (homeFragment != null) {
			int count = 0;
			OutdoorManager.getInstance().processDataBaseInfo();
			List<String> myCitiesList = OutdoorManager.getInstance().getUsersCitiesList()  ;
			if(myCitiesList != null ) {
				count = myCitiesList.size() ;
			}
			OutdoorManager.getInstance().setOutdoorViewPagerCurrentPage(count);
			homeFragment.notifyOutdoorPager();
		}
	}
	
	private OnItemClickListener mOutdoorLocationsItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
			OutdoorCityInfo outdoorCityInfo = (OutdoorCityInfo) userSelectedCitiesAdapter.getItem(position);
			final String key = AddOutdoorLocationHelper.getCityKeyWithRespectDataProvider(outdoorCityInfo);
			
			if (edittogglebutton.isChecked()) {
				ImageView deleteSign = (ImageView) view.findViewById(R.id.list_item_delete);
				FontTextView delete = (FontTextView) view.findViewById(R.id.list_item_right_text);
				
				if(delete.getVisibility() == View.GONE) {
					delete.setVisibility(View.VISIBLE);
					deleteSign.setImageResource(R.drawable.red_cross);
					selectedItemHashtable.put(key, true);
				} else {
					delete.setVisibility(View.GONE);
					deleteSign.setImageResource(R.drawable.white_cross);
					selectedItemHashtable.put(key, false);
				}
			} else {
				int index = OutdoorManager.getInstance().getUsersCitiesList().indexOf(key);
				gotoPage(index);
			}
		}
	};
	
	private OnClickListener locationOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.add_outdoor_location_ll:
				Intent	intent = new Intent(getActivity(), AddOutdoorLocationActivity.class);
				startActivity(intent);
				break;
			case R.id.current_location_ll:
				gotoPage(0);
				break;
			default:
				break;
			}
			
		}
	};
	
	private void setAdapter() {
		userCitiesId = userCitiesDatabase.getAllCities();
		outdoorCityInfoList = AddOutdoorLocationHelper.getSortedUserSelectedCitiesInfo(userCitiesId) ;
		userSelectedCitiesAdapter = new UserSelectedCitiesAdapter(getActivity(), R.layout.simple_list_item, outdoorCityInfoList);
		mOutdoorLocationListView.setAdapter(userSelectedCitiesAdapter);
		addAreaIdToCityList();
	}

}
