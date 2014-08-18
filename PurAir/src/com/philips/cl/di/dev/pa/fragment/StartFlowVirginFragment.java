package com.philips.cl.di.dev.pa.fragment;

import java.util.List;

import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.philips.cl.di.dev.pa.R;
import com.philips.cl.di.dev.pa.activity.MainActivity;
import com.philips.cl.di.dev.pa.constant.AppConstants;
import com.philips.cl.di.dev.pa.dashboard.HomeFragment;
import com.philips.cl.di.dev.pa.fragment.StartFlowDialogFragment.StartFlowListener;
import com.philips.cl.di.dev.pa.newpurifier.ConnectPurifier;
import com.philips.cl.di.dev.pa.newpurifier.PurAirDevice;
import com.philips.cl.di.dev.pa.util.ALog;
import com.philips.cl.di.dev.pa.view.FontTextView;

public class StartFlowVirginFragment extends BaseFragment implements OnClickListener {

	private StartFlowDialogFragment mDialog;
	private ConnectPurifier mConnectPurifier;	
	private Bundle mBundle;
	private Button mBtnConnectPurifier;
	private Button mBtnNoPurifier;
	private ImageView mBtnPlayMovie;
	private FontTextView mTvBenefits;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.start_flow_virgin_fragment, container, false);
		View includeView = view.findViewById(R.id.start_flow_bottom_bar_include);
				
		mBtnConnectPurifier = (Button) view.findViewById(R.id.start_flow_virgin_btn_connect);
		mBtnPlayMovie = (ImageView) view.findViewById(R.id.start_flow_virgin_btn_play);
		mTvBenefits = (FontTextView) view.findViewById(R.id.start_flow_virgin_tv_benefits);
		mBtnNoPurifier = (Button) includeView.findViewById(R.id.start_flow_bottom_btn_continue_without);
		
		mBtnConnectPurifier.setOnClickListener(this);
		mBtnPlayMovie.setOnClickListener(this);
		mTvBenefits.setOnClickListener(this);
		mBtnNoPurifier.setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onResume() {
		mBundle = new Bundle();
		mDialog = new StartFlowDialogFragment();
		mDialog.setListener(mStartFlowListener);
		ConnectPurifier.reset();
		mConnectPurifier = ConnectPurifier.getInstance(getActivity());		
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		ALog.i(ALog.APP_START_UP, "StartVirginFlowFragment$onClick");
		switch (v.getId()) {
		case R.id.start_flow_virgin_btn_connect:
			mConnectPurifier.startAddPurifierToAppFlow();
			break;
		case R.id.start_flow_virgin_btn_play:
			startVideo();
			break;
		case R.id.start_flow_virgin_tv_benefits:
			startBenefitsFlow();
			break;
		case R.id.start_flow_bottom_btn_continue_without:
			startUseNoPurifierFlow();
			break;
		default:
			break;
		}
	}
	
	private void showLocationServiceDialog() {
		try {
			mBundle.clear();
			mDialog = new StartFlowDialogFragment();
			mDialog.setListener(mStartFlowListener);
			mBundle.putInt(StartFlowDialogFragment.DIALOG_NUMBER, StartFlowDialogFragment.LOCATION_SERVICES);
			mDialog.setArguments(mBundle);
			mDialog.show(getActivity().getSupportFragmentManager(), "start_flow_dialog");
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}	
	}
	
	
	private void showLocationServiceTurnedOffDialog() {
		try {
			mBundle.clear();
			mDialog = new StartFlowDialogFragment();
			mDialog.setListener(mStartFlowListener);
			mBundle.putInt(StartFlowDialogFragment.DIALOG_NUMBER, StartFlowDialogFragment.LOCATION_SERVICES_TURNED_OFF);
			mDialog.setArguments(mBundle);
			mDialog.show(getActivity().getSupportFragmentManager(), "start_flow_dialog");
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}	
	}
		
	private void startBenefitsFlow() {
		if (getActivity() == null) return;
		((MainActivity) getActivity()).setTitle(getString(R.string.dashboard_title));
		((MainActivity) getActivity()).showFragment(new AirQualityFragment());
	}
	
	private void startVideo() {
		// TODO start video
	}
	
	private void startUseNoPurifierFlow() {
		WifiManager wifiManager = (WifiManager) getActivity().getSystemService(MainActivity.WIFI_SERVICE);
		if (!wifiManager.isWifiEnabled()) {
			try {
				mBundle.clear();
				mDialog = new StartFlowDialogFragment();
				mDialog.setListener(mStartFlowListener);
				mBundle.putInt(StartFlowDialogFragment.DIALOG_NUMBER, StartFlowDialogFragment.NO_INTERNET);
				mDialog.setArguments(mBundle);
				mDialog.show(getActivity().getSupportFragmentManager(), "start_flow_dialog");
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}	
		} else {
			// Ask user if we are allowed to use there location
			showLocationServiceDialog();
		}

	}
	
	private void startHomeScreenWithoutAPLayout() {
		mBundle.clear();
		mBundle.putBoolean(AppConstants.NO_PURIFIER_FLOW, true);
		
		HomeFragment homeFragment = new HomeFragment();
		homeFragment.setArguments(mBundle);
		
		try {
			getActivity().getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.llContainer, homeFragment)
				.addToBackStack(null)
				.commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}	
	}

	private StartFlowListener mStartFlowListener = new StartFlowListener() {
		
		@Override
		public void noInternetTurnOnClicked(DialogFragment dialog) {
			showLocationServiceDialog();
		}
		
		@Override
		public void locationServiceAllowClicked(DialogFragment dialog) {
			// The user allowed to use location services
			
			//Check if device location service is turned on otherwise show pop-up
			LocationManager locationManager = (LocationManager) getActivity().getSystemService(MainActivity.LOCATION_SERVICE);
			List<String> enabledProviders = locationManager.getProviders(true);
			
			if(enabledProviders.isEmpty()) {
				showLocationServiceTurnedOffDialog();
			} else {
				startHomeScreenWithoutAPLayout();
			}
		}
		
		@Override
		public void locationServiceTurnOnClicked(DialogFragment dialog) {
			startHomeScreenWithoutAPLayout();
		}
		
		@Override
		public void noWifiTurnOnClicked(DialogFragment dialog) {}
		
		@Override
		public void dialogCancelClicked(DialogFragment dialog) {
		}

		@Override
		public void onPurifierSelect(PurAirDevice purifier) {
		}
		
	};
}
