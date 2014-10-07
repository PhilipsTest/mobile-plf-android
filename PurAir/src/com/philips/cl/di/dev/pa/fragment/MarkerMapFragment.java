package com.philips.cl.di.dev.pa.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnMapLoadedListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.LatLngBounds.Builder;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.philips.cl.di.dev.pa.R;
import com.philips.cl.di.dev.pa.activity.OutdoorDetailsActivity;
import com.philips.cl.di.dev.pa.dashboard.OutdoorAQI;
import com.philips.cl.di.dev.pa.dashboard.OutdoorCity;
import com.philips.cl.di.dev.pa.dashboard.OutdoorController;
import com.philips.cl.di.dev.pa.dashboard.OutdoorEventListener;
import com.philips.cl.di.dev.pa.dashboard.OutdoorManager;
import com.philips.cl.di.dev.pa.dashboard.OutdoorWeather;
import com.philips.cl.di.dev.pa.util.ALog;

/**
 * 
 * MarkerMapFragment class will be showing AQI details of the qualified cities.
 * Author : Ritesh.jha@philips.com Date : 4 Aug 2014
 * 
 */
public class MarkerMapFragment extends BaseFragment implements
		OnMarkerClickListener, OnMapLoadedListener, OutdoorEventListener {

	private AMap aMap;
	private MapView mapView;
	private UiSettings mUiSettings;
//	private List<String> mCitiesList = null;
	private List<String> mCitiesListAll = null;
	private LatLngBounds bounds = null;
	private Builder builder = null;
	private OutdoorCity mOutdoorCity = null;
	private boolean isMapLoaded = false;
	private boolean isAllAqiReceived = false;
	private View mView = null;
	private Bitmap mBitMap = null;
	private LayoutInflater mInflater = null;
	private RelativeLayout mParentLayout = null;
	private ArrayList<Marker> mArrayListMarker = null;
	private TextView textView = null;
	private Canvas mCanvas = null;
	
	private static final String TAG = "MapMarkerFragment";

	@Override
	public void onMapLoaded() {
		isMapLoaded = true;
	}
	
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case 0:
				if(isMapLoaded && isAllAqiReceived){
					mHandler.sendEmptyMessageDelayed(1, 500);
				}
				else{
					mHandler.sendEmptyMessageDelayed(0, 500);
				}
				break;
			case 1:
				isMapLoaded = false;
				isAllAqiReceived = false;
				fillMapWithMarker();
				break;
			}
			
		};
	};
	
	private void fillMapWithMarker(){
		OutdoorCity outdoorCity = OutdoorManager.getInstance().getCityData(
				OutdoorDetailsActivity.getSelectedCityCode());
		
		addMarker();

		if (outdoorCity == null || outdoorCity.getOutdoorCityInfo() == null)
			return;

		LatLngBounds boundsNew = new LatLngBounds.Builder().include(
				new LatLng(outdoorCity.getOutdoorCityInfo().getLatitude(),
						outdoorCity.getOutdoorCityInfo().getLongitude()))
				.build();
		try {
			bounds = builder.build();
			aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
		} catch (IllegalStateException e) {
			ALog.d(TAG, "IllegalStateException");
		}
		aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsNew, 10));
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view;
		/* We inflate the xml which gives us a view */
		view = inflater.inflate(R.layout.marker_activity, container, false);
		mapView = (MapView) view.findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);
		mParentLayout = (RelativeLayout)view.findViewById(R.id.mapParent);
		builder = new LatLngBounds.Builder();
		OutdoorController.getInstance().setOutdoorEventListener(this);
		mInflater = (LayoutInflater) 
				getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = mInflater.inflate(R.layout.circle_lyt, null);
		textView = (TextView) mView.findViewById(R.id.circle_txt); 
		init();
		mArrayListMarker = new ArrayList<Marker>();
		return view;
	}

	private void addMarker() {
		String selectedCityCode = OutdoorDetailsActivity.getSelectedCityCode();
//		OutdoorCity selectedOutdoorCity = OutdoorManager.getInstance().getCityDataAll(selectedCityCode);
		OutdoorCity selectedOutdoorCity = OutdoorManager.getInstance().getCityData(selectedCityCode);
		float selectedLatitude = 50f;
		float selectedLongitude = 50f;
		
		//Adding below condition because UnitTest case is crashing.
		if(selectedOutdoorCity != null && selectedOutdoorCity.getOutdoorCityInfo() != null){
			selectedLatitude = selectedOutdoorCity.getOutdoorCityInfo().getLatitude();
			selectedLongitude = selectedOutdoorCity.getOutdoorCityInfo().getLongitude();
		}
		
		/*
		 * logic to find nearBy cities.
		 */
		float latitudePlus = selectedLatitude + 4;
		float latitudeMinus = selectedLatitude - 4;
		float longitudePlus = selectedLongitude + 4;
		float longitudeMinus = selectedLongitude - 4;
		
		mCitiesListAll = OutdoorManager.getInstance().getAllMatchingCitiesList(latitudePlus, 
				latitudeMinus, longitudePlus, longitudeMinus);
		
		for (int i = 0; i < (mCitiesListAll.size()); i++) {
			OutdoorCity outdoorCity = OutdoorManager.getInstance()
					.getCityDataAll(mCitiesListAll.get(i));
			addMarkerToMap(outdoorCity);
		}
		addMarkerToMap(mOutdoorCity);
	};

	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			mUiSettings = aMap.getUiSettings();
			mUiSettings.setZoomControlsEnabled(false);
			mUiSettings.setCompassEnabled(false);
			mUiSettings.setMyLocationButtonEnabled(false);
			mUiSettings.setScaleControlsEnabled(false);
			mUiSettings.setAllGesturesEnabled(false);
			aMap.setOnMapLoadedListener(this);
			aMap.setOnMarkerClickListener(this);
		}
	}

	private void addMarkerToMap(OutdoorCity outdoorCity) {
		if (outdoorCity == null || outdoorCity.getOutdoorCityInfo() == null
		 || outdoorCity.getOutdoorAQI() == null )
			return;
		float latitude = outdoorCity.getOutdoorCityInfo().getLatitude();
		float longitude = outdoorCity.getOutdoorCityInfo().getLongitude();
		String cityName = outdoorCity.getOutdoorCityInfo().getCityName();
		boolean iconOval = false;
		String cityCode = outdoorCity.getOutdoorCityInfo().getAreaID();

		LatLng latLng = new LatLng(latitude, longitude);
		builder.include(latLng);

		int aqiValue = 0;
		if (outdoorCity.getOutdoorAQI() != null)
			aqiValue = outdoorCity.getOutdoorAQI().getAQI();

		if (OutdoorDetailsActivity.getSelectedCityCode() != null
				&& OutdoorDetailsActivity.getSelectedCityCode()
						.equalsIgnoreCase(cityCode)) {
			mOutdoorCity = outdoorCity;
			iconOval = true;
		}

		if(mBitMap != null){
			mBitMap.recycle();
			mBitMap = null;
		}
		
		mBitMap = writeTextOnDrawable(
				MarkerMapFragment.getAqiPointerImageResId(aqiValue,
						iconOval), aqiValue);
				
		mArrayListMarker.add(aMap.addMarker(new MarkerOptions()
				.anchor(0.5f, 0.5f)
				.position(latLng)
				.title(cityName)
				.snippet(cityName + " : " + latitude + " , " + longitude)
				.draggable(false)
				.icon(BitmapDescriptorFactory.fromBitmap(mBitMap))));
	}

	private Bitmap writeTextOnDrawable(int drawableId, int text) {
		Bitmap bm = BitmapFactory.decodeResource(
				getActivity().getResources(), drawableId)
				.copy(Bitmap.Config.ARGB_8888, true);
		if(mCanvas!=null){
			mCanvas = null;
		}
		mCanvas = new Canvas(bm);
		textView.setText(String.valueOf(text));
		mView.measure(mCanvas.getWidth(), mCanvas.getHeight());
		mView.layout(0, 0, mCanvas.getWidth(), mCanvas.getHeight());
		mView.draw(mCanvas);
		return bm;
	}

	@Override
	public void onResume() {
		super.onResume();
		mapView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		mapView.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		OutdoorController.getInstance().removeOutdoorEventListener(this);
		mView = null;
		mInflater = null;
		
		if(mHandler != null){
			mHandler.removeMessages(0);
			mHandler.removeMessages(1);
			mHandler = null;
		}
		
		if(aMap!=null){		
			aMap.stopAnimation();
			aMap.clear();
			aMap = null;
		}
		if(mapView != null){
			mapView.setBackgroundColor(Color.BLACK);
			mapView.removeAllViewsInLayout();
			mapView.onDestroy();
			mapView = null;
		}
		
		if(mParentLayout != null){
			mParentLayout.removeAllViews();
			mParentLayout = null;
		}
		
		if(mArrayListMarker!=null && mArrayListMarker.size()>0){
			mArrayListMarker.clear();
			mArrayListMarker=null;
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		return false;
	}

	public static int getAqiPointerImageResId(int p2, boolean iconOval) {

		if (!iconOval) {
			if (p2 >= 0 && p2 <= 50) {
				return R.drawable.map_circle_6;
			} else if (p2 > 50 && p2 <= 100) {
				return R.drawable.map_circle_5;
			} else if (p2 > 100 && p2 <= 150) {
				return R.drawable.map_circle_4;
			} else if (p2 > 150 && p2 <= 200) {
				return R.drawable.map_circle_3;
			} else if (p2 > 200 && p2 <= 300) {
				return R.drawable.map_circle_2;
			} else if (p2 > 300) {
				return R.drawable.map_circle_1;
			}
		} else {
			if (p2 >= 0 && p2 <= 50) {
				return R.drawable.map_oval_6;
			} else if (p2 > 50 && p2 <= 100) {
				return R.drawable.map_oval_5;
			} else if (p2 > 100 && p2 <= 150) {
				return R.drawable.map_oval_4;
			} else if (p2 > 150 && p2 <= 200) {
				return R.drawable.map_oval_3;
			} else if (p2 > 200 && p2 <= 300) {
				return R.drawable.map_oval_2;
			} else if (p2 > 300) {
				return R.drawable.map_oval_1;
			}
		}

		return R.drawable.map_circle_6;
	}


	@Override
	public void allOutdoorAQIDataReceived(List<OutdoorAQI> aqis) {
		isAllAqiReceived = true;
		mHandler.sendEmptyMessageDelayed(0, 500);		
	}


	@Override
	public void outdoorAQIDataReceived(OutdoorAQI outdoorAQI, String areaID) {
		// NOP
		
	}


	@Override
	public void outdoorWeatherDataReceived(OutdoorWeather outdoorWeather, String areaID) {
		// NOP
		
	}
}
