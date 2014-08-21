package com.philips.cl.di.dev.pa.fragment;

import java.util.Hashtable;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.philips.cl.di.dev.pa.R;
import com.philips.cl.di.dev.pa.constant.AppConstants;
import com.philips.cl.di.dev.pa.dashboard.OutdoorManager;
import com.philips.cl.di.dev.pa.outdoorlocations.OutdoorLocationDatabase;
import com.philips.cl.di.dev.pa.outdoorlocations.OutdoorLocationHandler;
import com.philips.cl.di.dev.pa.outdoorlocations.OutdoorSelectedCityListener;
import com.philips.cl.di.dev.pa.util.Utils;
import com.philips.cl.di.dev.pa.view.FontTextView;

public class OutdoorLocationsFragment extends BaseFragment implements ConnectionCallbacks, OnConnectionFailedListener, OutdoorSelectedCityListener {
	private static final String TAG = OutdoorLocationsFragment.class.getSimpleName();
	
	private boolean isGooglePlayServiceAvailable;
	
	private ListView mOutdoorLocationListView;
	private CursorAdapter mOutdoorLocationAdapter;
	private Hashtable<String, Boolean> selectedItemHashtable;
	
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
		View view = inflater.inflate(R.layout.outdoor_locations_fragment, container, false);
		mOutdoorLocationListView = (ListView) view.findViewById(R.id.outdoor_locations_list);
		
		mOutdoorLocationListView.setOnItemClickListener(mOutdoorLocationsItemClickListener);
		
		return view;
	}
	
	@Override
	public void onResume() {
		OutdoorLocationHandler.getInstance().setSelectedCityListener(this); 
		OutdoorLocationHandler.getInstance().fetchSelectedCity();
		super.onResume();
	}
	
	@Override
	public void onPause() {
		OutdoorLocationHandler.getInstance().removeSelectedCityListener();
		super.onPause();
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
	
	private void addAreaIdToCityList(Cursor cursor) {
		if (cursor.getCount() > 0) {
			OutdoorManager.getInstance().clearCitiesList();
			cursor.moveToFirst();
			do {
				OutdoorManager.getInstance().addAreaIDToUsersList(
						cursor.getString(cursor.getColumnIndex(AppConstants.KEY_AREA_ID)));
			} while (cursor.moveToNext());
		}
	}

	private void fillListViewFromDatabase(Cursor cursor) {
		if (cursor != null) {
			
			if (mOutdoorLocationAdapter != null) mOutdoorLocationAdapter = null;
			
			mOutdoorLocationAdapter = new CursorAdapter(getActivity(), cursor, false) {
				
				@Override
				public View newView(Context context, Cursor cursor, ViewGroup parent) {
					LayoutInflater inflater = LayoutInflater.from(parent.getContext());
					View retView = inflater.inflate(R.layout.simple_list_item, parent, false);
					
					return retView;
				}
				
				@Override
				public void bindView(View view, Context context, Cursor cursor) {
					ImageView deleteSign = (ImageView) view.findViewById(R.id.list_item_delete);
					FontTextView tvName = (FontTextView) view.findViewById(R.id.list_item_name);
					
					deleteSign.setVisibility(View.VISIBLE);
					
					String city = cursor.getString(cursor.getColumnIndex(AppConstants.KEY_CITY));
					String cityCN = cursor.getString(cursor.getColumnIndex(AppConstants.KEY_CITY_CN));
					String cityTW = cursor.getString(cursor.getColumnIndex(AppConstants.KEY_CITY_TW));

					tvName.setText(city + ", " + cityCN+ ", "+ cityTW);
					tvName.setTag(cursor.getString(cursor.getColumnIndex(AppConstants.KEY_AREA_ID)));
					
					FontTextView delete = (FontTextView) view.findViewById(R.id.list_item_right_text);
					
					final String areaId = cursor.getString(cursor.getColumnIndexOrThrow(AppConstants.KEY_AREA_ID));
					if (selectedItemHashtable.containsKey(areaId) && selectedItemHashtable.get(areaId)) {
						delete.setVisibility(View.VISIBLE);
						deleteSign.setImageResource(R.drawable.delete_t2b);
					} else {
						delete.setVisibility(View.GONE);
						deleteSign.setImageResource(R.drawable.delete_l2r);
					}
					
					delete.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							OutdoorManager.getInstance().removeAreaIDFromUsersList(areaId);
							OutdoorManager.getInstance().removeCityDataFromMap(areaId);
							
							OutdoorLocationDatabase database =  new OutdoorLocationDatabase();

							database.open();
							database.updateOutdoorLocationShortListItem(areaId, false);
							database.close();
							
							if (selectedItemHashtable.containsKey(areaId)) {
								selectedItemHashtable.remove(areaId);
							}
							OutdoorLocationHandler.getInstance().fetchSelectedCity();
						}
					});
				}
			};
			
			mOutdoorLocationListView.setAdapter(mOutdoorLocationAdapter);
			//Add city to list
			addAreaIdToCityList(cursor);
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
	}
	
	private OnItemClickListener mOutdoorLocationsItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
			ImageView deleteSign = (ImageView) view.findViewById(R.id.list_item_delete);
			FontTextView delete = (FontTextView) view.findViewById(R.id.list_item_right_text);
			
			Cursor cursor = (Cursor) mOutdoorLocationAdapter.getItem(position);
			cursor.moveToPosition(position);
			
			final String areaId = cursor.getString(cursor.getColumnIndexOrThrow(AppConstants.KEY_AREA_ID));
			
			if(delete.getVisibility() == View.GONE) {
				delete.setVisibility(View.VISIBLE);
				deleteSign.setImageResource(R.drawable.delete_t2b);
				selectedItemHashtable.put(areaId, true);
			} else {
				delete.setVisibility(View.GONE);
				deleteSign.setImageResource(R.drawable.delete_l2r);
				selectedItemHashtable.put(areaId, false);
			}
		}
	};

	@Override
	public void onSelectedCityLoad(final Cursor cursor) {
		if (getActivity() == null) return;
		
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				fillListViewFromDatabase(cursor);
			}
		});
	}
}
