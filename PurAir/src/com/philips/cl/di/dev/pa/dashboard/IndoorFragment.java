package com.philips.cl.di.dev.pa.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.philips.cl.di.dev.pa.R;
import com.philips.cl.di.dev.pa.constant.AppConstants;
import com.philips.cl.di.dev.pa.datamodel.AirPurifierEventDto;
import com.philips.cl.di.dev.pa.firmware.FirmwareEventDto;
import com.philips.cl.di.dev.pa.fragment.BaseFragment;
import com.philips.cl.di.dev.pa.purifier.AirPurifierController;
import com.philips.cl.di.dev.pa.purifier.AirPurifierEventListener;
import com.philips.cl.di.dev.pa.util.ALog;
import com.philips.cl.di.dev.pa.util.Utils;
import com.philips.cl.di.dev.pa.view.FontTextView;

public class IndoorFragment extends BaseFragment implements AirPurifierEventListener{
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.hf_indoor_dashboard, null);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		AirPurifierController.getInstance().addAirPurifierEventListener(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		AirPurifierController.getInstance().removeAirPurifierEventListener(this);
	}

	@Override
	public void airPurifierEventReceived(final AirPurifierEventDto airPurifierEvent) {
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				updateDashboard(airPurifierEvent);
			}
		});
	}
	
	private void updateDashboard(AirPurifierEventDto airPurifierEvent) {
		int indoorAqi = airPurifierEvent.getIndoorAQI();
		
		FontTextView fanMode = (FontTextView) getView().findViewById(R.id.hf_indoor_fan_mode);
		setFanSpeedText(fanMode, airPurifierEvent.getFanSpeed());
		
		FontTextView filterStatus = (FontTextView) getView().findViewById(R.id.hf_indoor_filter);
		setFilterStatus(filterStatus, airPurifierEvent);
		
		FontTextView aqiStatus = (FontTextView) getView().findViewById(R.id.hf_indoor_aqi_reading);
		setAqiText(aqiStatus, indoorAqi);
		
		ImageView aqiPointer = (ImageView) getView().findViewById(R.id.hf_indoor_circle_pointer);
		
		setAqiPointerBackground(aqiPointer, indoorAqi);
		aqiPointer.invalidate();
		setAqiPointerRotation(aqiPointer, indoorAqi);
	}

	//TODO : Move all following methods to DashboardUtils.
	private void setFanSpeedText(FontTextView fanMode, String fanSpeed) {
		if(fanSpeed.equals(AppConstants.FAN_SPEED_SILENT)) {
			fanMode.setText(R.string.silent);
		} else if (fanSpeed.equals(AppConstants.FAN_SPEED_AUTO)) {
			fanMode.setText(R.string.auto);
		} else if (fanSpeed.equals(AppConstants.FAN_SPEED_TURBO)) {
			fanMode.setText(R.string.turbo);
		} else if (fanSpeed.equals(AppConstants.FAN_SPEED_ONE)) {
			fanMode.setText(R.string.one);
		} else if (fanSpeed.equals(AppConstants.FAN_SPEED_TWO)) {
			fanMode.setText(R.string.two);
		} else if (fanSpeed.equals(AppConstants.FAN_SPEED_THREE)) {
			fanMode.setText(R.string.three);
		}
	}
	
	private void setFilterStatus(FontTextView filterStatus, AirPurifierEventDto airPurifierEvent) {
		filterStatus.setText(Utils.getFilterStatusForDashboard(airPurifierEvent));
	}
	

	private void setAqiText(FontTextView aqiStatus, int indoorAQI) {
		ALog.i(ALog.DASHBOARD, "setAqiText indoorAqi " + indoorAQI);
		if(indoorAQI >= 0 && indoorAQI <= 14) {
			aqiStatus.setText(R.string.good);
		} else if (indoorAQI > 14 && indoorAQI <= 23) {
			aqiStatus.setText(R.string.moderate);
		} else if (indoorAQI > 23 && indoorAQI <= 35) {
			aqiStatus.setText(R.string.unhealthy);
		} else if (indoorAQI > 35) {
			aqiStatus.setText(R.string.very_unhealthy_split);
		}
	}
	

	private void setAqiPointerBackground(ImageView aqiPointer, int indoorAQI) {
		ALog.i(ALog.DASHBOARD, "setAqiPointerBackground indoorAqi " + indoorAQI);
		if(indoorAQI >= 0 && indoorAQI <= 14) {
			ALog.i(ALog.DASHBOARD, "blue_circle_with_arrow_2x indoorAqi " + indoorAQI);
			aqiPointer.setImageResource(R.drawable.blue_circle_with_arrow_2x);
		} else if (indoorAQI > 14 && indoorAQI <= 23) {
			ALog.i(ALog.DASHBOARD, "light_pink_circle_arrow1_2x indoorAqi " + indoorAQI);
			aqiPointer.setImageResource(R.drawable.light_pink_circle_arrow1_2x);
		} else if (indoorAQI > 23 && indoorAQI <= 35) {
			ALog.i(ALog.DASHBOARD, "red_circle_arrow_2x indoorAqi " + indoorAQI);
			aqiPointer.setImageResource(R.drawable.red_circle_arrow_2x);
		} else if (indoorAQI > 35) {
			ALog.i(ALog.DASHBOARD, "light_red_circle_arrow_2x indoorAqi " + indoorAQI);
			aqiPointer.setImageResource(R.drawable.light_red_circle_arrow_2x );
		}
	}

	private void setAqiPointerRotation(ImageView aqiPointer, int indoorAQI) {
		float rotation = 0.0f;
		if(indoorAQI >= 0 && indoorAQI <= 14) {
			rotation = indoorAQI * 1.9f;
		} else if (indoorAQI > 14 && indoorAQI <= 23) {
			indoorAQI -= 14;
			rotation = 27.0f + (indoorAQI * 3.25f);
		} else if (indoorAQI > 23 && indoorAQI <= 35) {
			indoorAQI -= 23;
			rotation = 56.0f + (indoorAQI * 2.33f);
		} else if (indoorAQI > 35) {
			indoorAQI -= 35;
			rotation = 86.0f + (indoorAQI * 1.0f);
			if(rotation > 302) {
				rotation = 302;
			}
		}
		ALog.i(ALog.DASHBOARD, "blue_circle_with_arrow_2x indoorAqi " + indoorAQI + " rotation " + rotation + " aqiPointer.getWidth()/2 " + aqiPointer.getWidth()/2);
		Animation aqiCircleRotateAnim = new RotateAnimation(0.0f, rotation, aqiPointer.getWidth()/2, aqiPointer.getWidth()/2);
	
	    aqiCircleRotateAnim.setDuration(2000);  
	    aqiCircleRotateAnim.setRepeatCount(0);     
	    aqiCircleRotateAnim.setFillAfter(true);

		aqiPointer.setAnimation(aqiCircleRotateAnim);
	}


	
	@Override
	public void firmwareEventReceived(FirmwareEventDto firmwareEventDto) {
		//NOP
	}

}
