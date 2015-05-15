package com.philips.cl.di.dev.pa.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.philips.cl.di.dev.pa.PurAirApplication;
import com.philips.cl.di.dev.pa.R;
import com.philips.cl.di.dev.pa.activity.MainActivity;
import com.philips.cl.di.dev.pa.activity.PrivacyPolicyActivity;
import com.philips.cl.di.dev.pa.buyonline.AppUtils;
import com.philips.cl.di.dev.pa.buyonline.FeedbackActivity;
import com.philips.cl.di.dev.pa.constant.AppConstants;
import com.philips.cl.di.dev.pa.constant.AppConstants.Port;
import com.philips.cl.di.dev.pa.demo.DemoModeTask;
import com.philips.cl.di.dev.pa.ews.EWSConstant;
import com.philips.cl.di.dev.pa.newpurifier.AirPurifier;
import com.philips.cl.di.dev.pa.newpurifier.AirPurifierManager;
import com.philips.cl.di.dev.pa.util.ALog;
import com.philips.cl.di.dev.pa.util.JSONBuilder;
import com.philips.cl.di.dev.pa.util.MetricsTracker;
import com.philips.cl.di.dev.pa.util.TrackPageConstants;
import com.philips.cl.di.dev.pa.util.Utils;
import com.philips.cl.di.dev.pa.view.FontTextView;

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
		ViewGroup container = (RelativeLayout) getView().findViewById(R.id.containerRL);
		setBackground(container, R.drawable.ews_nav_bar_2x, Color.BLACK, .1F);
	}

	private void initViews(View view) {
		ImageButton backButton = (ImageButton) view.findViewById(R.id.heading_back_imgbtn);
		backButton.setVisibility(View.VISIBLE);
		backButton.setOnClickListener(this);
		FontTextView heading=(FontTextView) view.findViewById(R.id.heading_name_tv);
		heading.setText(getString(R.string.list_item_settings));
		ToggleButton demoModeTButton = (ToggleButton) view.findViewById(R.id.settings_demo_mode_toggle);

		demoModeTButton.setChecked(PurAirApplication.isDemoModeEnable());
		demoModeTButton.setOnCheckedChangeListener(this);
		
		FontTextView rateThisApp = (FontTextView) view.findViewById(R.id.rate_this_app);
		rateThisApp.setOnClickListener(this);
		
		FontTextView shareFeedback = (FontTextView) view.findViewById(R.id.share_feedback);
		shareFeedback.setOnClickListener(this);

		TextView privacyPolicy = (TextView) view.findViewById(R.id.tv_privacy_policy);
		privacyPolicy.setOnClickListener(this);
		
		TextView eula = (TextView) view.findViewById(R.id.tv_eula);
		eula.setOnClickListener(this);		
		
		TextView terms = (TextView) view.findViewById(R.id.tv_terms_and_conditions);
		terms.setOnClickListener(this);	
	}

	@Override
	public void onClick(View v) {
		MainActivity activity = (MainActivity) getActivity();
		if (activity == null) return;
		Intent intent;
		switch (v.getId()) {
		case R.id.rate_this_app:
			MetricsTracker.trackActionRateThisApp();
			AppUtils.startMarketCommend(getActivity(), getActivity().getPackageName());
			break;
		case R.id.share_feedback:
			intent=new Intent(getActivity(), FeedbackActivity.class);
//			intent.putExtra(AppConstants.ACTIVITY, AppConstants.PRIVACY_POLICY_SCREEN);
			activity.startActivity(intent);
			break;
		case R.id.tv_privacy_policy:
			intent=new Intent(getActivity(), PrivacyPolicyActivity.class);
			intent.putExtra(AppConstants.ACTIVITY, AppConstants.PRIVACY_POLICY_SCREEN);
			activity.startActivity(intent);
			break;
		case R.id.tv_eula:
			intent=new Intent(getActivity(), PrivacyPolicyActivity.class);
			intent.putExtra(AppConstants.ACTIVITY, AppConstants.EULA_SCREEN);
			activity.startActivity(intent);
			break;
		case R.id.tv_terms_and_conditions:
			intent=new Intent(getActivity(), PrivacyPolicyActivity.class);
			intent.putExtra(AppConstants.ACTIVITY, AppConstants.TERMS_AND_CONDITIONS_SCREEN);
			activity.startActivity(intent);
			break;
		case R.id.heading_back_imgbtn:
			activity.onBackPressed();
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
				AirPurifier purAirDevice = AirPurifierManager.getInstance().getCurrentPurifier();
				if (purAirDevice != null && purAirDevice.isDemoPurifier()) {
					String dataToSend = JSONBuilder.getDICommUIBuilder(purAirDevice.getNetworkNode());
					DemoModeTask task = new DemoModeTask(
							null, Utils.getPortUrl(Port.WIFIUI, EWSConstant.PURIFIER_ADHOCIP),dataToSend , "PUT") ;
					task.start();
				}
				AirPurifierManager.getInstance().setCurrentIndoorViewPagerPosition(0);
				mainActivity.startNormalMode();
			} else {
				AirPurifierManager.getInstance().setCurrentIndoorViewPagerPosition(1);
				mainActivity.startDemoMode();
			}
			AirPurifierManager.getInstance().removeCurrentPurifier();
//			((MainActivity) getActivity()).setActionBar(new SettingsFragment());
			((MainActivity) getActivity()).onAirPurifierChanged();

		}
	}

}
