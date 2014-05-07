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
import android.widget.ImageView;
import com.philips.cl.di.dev.pa.R;
import com.philips.cl.di.dev.pa.activity.MainActivity;
import com.philips.cl.di.dev.pa.activity.OutdoorDetailsActivity;
import com.philips.cl.di.dev.pa.constant.AppConstants;
import com.philips.cl.di.dev.pa.fragment.BaseFragment;
import com.philips.cl.di.dev.pa.util.ALog;
import com.philips.cl.di.dev.pa.view.FontTextView;

public class OutdoorFragment extends BaseFragment {
	
	private FontTextView cityName,updated,temp,aqi,aqiTitle,aqiSummary1,aqiSummary2;
	private ImageView aqiPointerCircle;
	private ImageView weatherIcon ;
	
	public static OutdoorFragment newInstance(OutdoorDto outdoorDto) {
		ALog.i(ALog.DASHBOARD, "OutdoorFragment$newInstance");
		OutdoorFragment fragment = new OutdoorFragment();
		if(outdoorDto != null) {
			Bundle args = new Bundle();
			args.putSerializable(AppConstants.KEY_CITY, outdoorDto);
			fragment.setArguments(args);
		}
		return fragment;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ALog.i(ALog.DASHBOARD, "OutdoorFragment onActivityCreated");
		initViews(getView());
	}
	
	private void initViews(View view) {
		OutdoorManager.getInstance().getOutdoorDashboardData(0);
		cityName = (FontTextView) view.findViewById(R.id.hf_outdoor_city);
		updated = (FontTextView) view.findViewById(R.id.hf_outdoor_time_update);
		temp = (FontTextView) view.findViewById(R.id.hf_outdoor_temprature);
		aqi = (FontTextView) view.findViewById(R.id.hf_outdoor_aqi_reading);
		aqiTitle = (FontTextView) view.findViewById(R.id.hf_outdoor_aqi_title);
		aqiSummary1 = (FontTextView) view.findViewById(R.id.hf_outdoor_aqi_summary1);
		aqiSummary2 = (FontTextView) view.findViewById(R.id.hf_outdoor_aqi_summary2);
		aqiPointerCircle = (ImageView) view.findViewById(R.id.hf_outdoor_circle_pointer);
		weatherIcon = (ImageView) view.findViewById(R.id.hf_outdoor_weather_image) ;
		Bundle bundle = getArguments();

		if(bundle != null) {
			ALog.i(ALog.DASHBOARD, "OutdoorFragment$onCreateView");
			OutdoorDto city= (OutdoorDto) getArguments().getSerializable(AppConstants.KEY_CITY);
			updateUI(city);
		} 
	}
	
	private void updateUI(OutdoorDto outdoorDto) {
		ALog.i(ALog.DASHBOARD, "UpdateUI");		
		cityName.setText(outdoorDto.getCityName());
		if( outdoorDto.getUpdatedTime() != null && !outdoorDto.getUpdatedTime().isEmpty()) {
			String [] splitDateandTime = outdoorDto.getUpdatedTime().split(" ") ;
			if( splitDateandTime.length > 1 ) {
				// Updating only time - Ignoring the date
				updated.setText(splitDateandTime[1]);
			}			
		}
				
		temp.setText(outdoorDto.getTemperature()+AppConstants.UNICODE_DEGREE);		
		aqi.setText(outdoorDto.getAqi());
		aqiTitle.setText(outdoorDto.getAqiTitle());
		String outdoorAQISummary [] = outdoorDto.getAqiSummary() ;
		if( outdoorAQISummary != null && outdoorAQISummary.length > 1 ) {
			aqiSummary1.setText(outdoorDto.getAqiSummary()[0]);
			aqiSummary2.setText(outdoorDto.getAqiSummary()[1]);
		}
		aqiPointerCircle.setImageResource(outdoorDto.getAqiPointerImageResId());
		aqiPointerCircle.setOnClickListener(pointerImageClickListener);
		aqiPointerCircle.invalidate();
		weatherIcon.setImageDrawable(getResources().getDrawable(outdoorDto.getWeatherIconResId()));
		setRotationAnimation(aqiPointerCircle, outdoorDto.getAqiPointerRotaion());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ALog.i(ALog.DASHBOARD, "OutdoorFragment$onCreateView");
		
		View view = inflater.inflate(R.layout.hf_outdoor_dashboard, null);
		return view;
	}
	
	private void setRotationAnimation(ImageView aqiPointer, float rotation) {
		Drawable drawable = aqiPointer.getDrawable();
		ALog.i(ALog.DASHBOARD, "OutdoorFragment$getRotationAnimation rotation " + rotation + " aqiPointer.getWidth()/2 " + (aqiPointer.getWidth()/2) + " drawable " + drawable.getMinimumHeight());
		
		Animation aqiCircleRotateAnim = new RotateAnimation(0.0f, rotation, drawable.getMinimumWidth()/2, drawable.getMinimumHeight()/2);
		
	    aqiCircleRotateAnim.setDuration(2000);  
	    aqiCircleRotateAnim.setRepeatCount(0);     
	    aqiCircleRotateAnim.setFillAfter(true);
	 
	    aqiPointer.setAnimation(aqiCircleRotateAnim);
	}
	
	private OnClickListener pointerImageClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			((MainActivity)getActivity()).isClickEvent = true ;
			Bundle bundle = getArguments();
			if (bundle != null) {
				Intent intent = new Intent(getActivity(), OutdoorDetailsActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		}
	};
}
