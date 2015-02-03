package com.philips.cl.di.dev.pa.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.philips.cl.di.dev.pa.PurAirApplication;
import com.philips.cl.di.dev.pa.R;
import com.philips.cl.di.dev.pa.activity.MainActivity;
import com.philips.cl.di.dev.pa.activity.PrivacyPolicyActivity;
import com.philips.cl.di.dev.pa.constant.AppConstants;
import com.philips.cl.di.dev.pa.constant.AppConstants.Port;
import com.philips.cl.di.dev.pa.demo.DemoModeTask;
import com.philips.cl.di.dev.pa.ews.EWSConstant;
import com.philips.cl.di.dev.pa.newpurifier.PurAirDevice;
import com.philips.cl.di.dev.pa.newpurifier.PurifierManager;
import com.philips.cl.di.dev.pa.util.ALog;
import com.philips.cl.di.dev.pa.util.JSONBuilder;
import com.philips.cl.di.dev.pa.util.MetricsTracker;
import com.philips.cl.di.dev.pa.util.TrackPageConstants;
import com.philips.cl.di.dev.pa.util.Utils;

public class SettingsFragment extends BaseFragment implements OnClickListener, OnCheckedChangeListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.settings_fragment, container, false);
		initViews(view);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		MetricsTracker.trackPage(TrackPageConstants.SETTINGS);
	}

	private void initViews(View view) {
		ToggleButton demoModeTButton = (ToggleButton) view.findViewById(R.id.settings_demo_mode_toggle);

		demoModeTButton.setChecked(PurAirApplication.isDemoModeEnable());
		demoModeTButton.setOnCheckedChangeListener(this);

		TextView privacyPolicy = (TextView) view.findViewById(R.id.tv_privacy_policy);
		privacyPolicy.setOnClickListener(this);
		
		TextView eula = (TextView) view.findViewById(R.id.tv_eula);
		eula.setOnClickListener(this);		
		
		TextView terms = (TextView) view.findViewById(R.id.tv_terms_and_conditions);
		terms.setOnClickListener(this);	
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.tv_privacy_policy:
			intent=new Intent(getActivity(), PrivacyPolicyActivity.class);
			intent.putExtra(AppConstants.ACTIVITY, AppConstants.PRIVACY_POLICY_SCREEN);
			getActivity().startActivity(intent);
			break;
			
		case R.id.tv_eula:
			intent=new Intent(getActivity(), PrivacyPolicyActivity.class);
			intent.putExtra(AppConstants.ACTIVITY, AppConstants.EULA_SCREEN);
			getActivity().startActivity(intent);
			break;
			
		case R.id.tv_terms_and_conditions:
			intent=new Intent(getActivity(), PrivacyPolicyActivity.class);
			intent.putExtra(AppConstants.ACTIVITY, AppConstants.TERMS_AND_CONDITIONS_SCREEN);
			getActivity().startActivity(intent);
			break;

		default:
			break;
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (getActivity() == null) return;
		MainActivity mainActivity = (MainActivity) getActivity();
		if (buttonView.getId() == R.id.settings_demo_mode_toggle) {
			ALog.i(ALog.DEMO_MODE, "Demo mode enable: " + isChecked);
			PurAirApplication.setDemoModeEnable(isChecked);
			if (!isChecked) {
				if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ) {
					android.provider.Settings.System.putString(getActivity().getContentResolver(), android.provider.Settings.System.WIFI_USE_STATIC_IP, "0");  
				}
				PurAirDevice purAirDevice = PurifierManager.getInstance().getCurrentPurifier();
				if (purAirDevice != null && purAirDevice.isDemoPurifier()) {
					String dataToSend = JSONBuilder.getDICommUIBuilder(purAirDevice);
					DemoModeTask task = new DemoModeTask(
							null, Utils.getPortUrl(Port.WIFIUI, EWSConstant.PURIFIER_ADHOCIP),dataToSend , "PUT") ;
					task.start();
				}
				PurifierManager.getInstance().setCurrentIndoorViewPagerPosition(0);
				mainActivity.startNormalMode();
			} else {
				PurifierManager.getInstance().setCurrentIndoorViewPagerPosition(1);
				mainActivity.startDemoMode();
			}
			PurifierManager.getInstance().removeCurrentPurifier();
			((MainActivity) getActivity()).setActionBar(new SettingsFragment());
			((MainActivity) getActivity()).onAirPurifierChanged();

		}
	}

}
