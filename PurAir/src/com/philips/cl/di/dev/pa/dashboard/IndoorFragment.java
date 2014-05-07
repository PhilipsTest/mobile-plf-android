package com.philips.cl.di.dev.pa.dashboard;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.philips.cl.di.dev.pa.R;
import com.philips.cl.di.dev.pa.activity.IndoorDetailsActivity;
import com.philips.cl.di.dev.pa.activity.MainActivity;
import com.philips.cl.di.dev.pa.datamodel.AirPortInfo;
import com.philips.cl.di.dev.pa.firmware.FirmwarePortInfo;
import com.philips.cl.di.dev.pa.fragment.BaseFragment;
import com.philips.cl.di.dev.pa.newpurifier.PurifierManager;
import com.philips.cl.di.dev.pa.purifier.AirPurifierController;
import com.philips.cl.di.dev.pa.purifier.AirPurifierEventListener;
import com.philips.cl.di.dev.pa.util.ALog;
import com.philips.cl.di.dev.pa.view.FontTextView;

public class IndoorFragment extends BaseFragment implements AirPurifierEventListener, OnClickListener {

	private LinearLayout firmwareUpdatePopup;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.hf_indoor_dashboard, null);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initFirmwareUpdatePopup();
	}
	
	private void initFirmwareUpdatePopup() {
		firmwareUpdatePopup = (LinearLayout) getView().findViewById(R.id.firmware_update_available);
		
		FontTextView firmwareUpdateText = (FontTextView) getView().findViewById(R.id.lbl_firmware_update_available);
		firmwareUpdateText.setOnClickListener(this);
		
		ImageButton firmwareUpdateCloseButton = (ImageButton) getView().findViewById(R.id.btn_firmware_update_available);
		firmwareUpdateCloseButton.setOnClickListener(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		AirPurifierController.getInstance().addAirPurifierEventListener(this);
		if(PurifierManager.getInstance().getCurrentPurifier() != null) {
			updateDashboard(PurifierManager.getInstance().getCurrentPurifier().getAirPortInfo());
		}
		hideFirmwareUpdatePopup();
	}

	private void hideFirmwareUpdatePopup() {
		firmwareUpdatePopup.setVisibility(View.GONE);
	}

	@Override
	public void onPause() {
		super.onPause();
		AirPurifierController.getInstance().removeAirPurifierEventListener(this);
	}

	private void updateDashboard(AirPortInfo airPortInfo) {
		if(airPortInfo == null) {
			return;
		}
		
		int indoorAqi = airPortInfo.getIndoorAQI();
		
		FontTextView fanModeTxt = (FontTextView) getView().findViewById(R.id.hf_indoor_fan_mode);
		fanModeTxt.setText(getString(DashboardUtils.getFanSpeedText(airPortInfo.getFanSpeed())));

		FontTextView filterStatusTxt = (FontTextView) getView().findViewById(R.id.hf_indoor_filter);
		filterStatusTxt.setText(DashboardUtils.getFilterStatus(airPortInfo));

		FontTextView aqiStatusTxt = (FontTextView) getView().findViewById(R.id.hf_indoor_aqi_reading);
		aqiStatusTxt.setText(getString(DashboardUtils.getAqiTitle(indoorAqi)));
		
		FontTextView aqiSummaryTxt = (FontTextView) getView().findViewById(R.id.hf_indoor_aqi_summary);
		aqiSummaryTxt.setText(getString(DashboardUtils.getAqiSummary(indoorAqi)));
		
		FontTextView purifierNameTxt = (FontTextView) getView().findViewById(R.id.hf_indoor_purifier_name);
		if (PurifierManager.getInstance().getCurrentPurifier() != null) {
			purifierNameTxt.setText(PurifierManager.getInstance().getCurrentPurifier().getName());
		}

		ImageView aqiPointer = (ImageView) getView().findViewById(R.id.hf_indoor_circle_pointer);

		aqiPointer.setOnClickListener(this);
		aqiPointer.setImageResource(DashboardUtils.getAqiPointerBackgroundId(indoorAqi));
		aqiPointer.invalidate();
		setRotationAnimation(aqiPointer, DashboardUtils.getAqiPointerRotation(indoorAqi));
	}


	private void setRotationAnimation(ImageView aqiPointer, float rotation) {
		Drawable drawable = aqiPointer.getDrawable();
		ALog.i(ALog.DASHBOARD, "IndoorFragment$getRotationAnimation rotation " + rotation + " aqiPointer.getWidth()/2 " + (aqiPointer.getWidth()/2) + " drawable " + drawable.getMinimumHeight());
		
		Animation aqiCircleRotateAnim = new RotateAnimation(0.0f, rotation, drawable.getMinimumWidth()/2, drawable.getMinimumHeight()/2);
		
	    aqiCircleRotateAnim.setDuration(2000);  
	    aqiCircleRotateAnim.setRepeatCount(0);     
	    aqiCircleRotateAnim.setFillAfter(true);
	 
	    aqiPointer.setAnimation(aqiCircleRotateAnim);
	}
	
	private void showFirmwareUpdatePopup() {
		firmwareUpdatePopup.setVisibility(View.VISIBLE);
	}

	@Override
	public void airPurifierEventReceived(final AirPortInfo airPurifierEvent) {
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				updateDashboard(airPurifierEvent);
			}
		});
	}
	
	@Override
	public void firmwareEventReceived(FirmwarePortInfo firmwarePortInfo) {
		if(getActivity() == null) return;
		if(firmwarePortInfo.isUpdateAvailable()) {
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					showFirmwareUpdatePopup();
				}
			});
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lbl_firmware_update_available:
			((MainActivity)getActivity()).startFirmwareUpgradeActivity();
			hideFirmwareUpdatePopup();
			break;
		case R.id.btn_firmware_update_available:
			hideFirmwareUpdatePopup();
			break;
		case R.id.hf_indoor_circle_pointer:
			Intent intent = new Intent(getActivity(), IndoorDetailsActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

}
