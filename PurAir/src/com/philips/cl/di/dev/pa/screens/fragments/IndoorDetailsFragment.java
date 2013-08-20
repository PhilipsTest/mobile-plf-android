package com.philips.cl.di.dev.pa.screens.fragments;

import com.philips.cl.di.dev.pa.R;
import com.philips.cl.di.dev.pa.screens.customviews.BarOneDayView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class IndoorDetailsFragment extends Fragment {
	
	public static final String TAG = IndoorDetailsFragment.class.getSimpleName();
	
	View vMain ; 
	ImageView ivLeftMenu, ivCenterLabel , ivRightDeviceIcon ; 
	BarOneDayView ivGraphView ; 
	int [] arrayAQIValues = {1,2,3,4,5,6,7,8,9,10,11,111,123,145,145,255,376,500,1,126,255,235,123,175};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		vMain =  inflater.inflate(R.layout.activity_indoor_details, container,false);
		initialiseNavigationBar();
		initilizeViews();
		return vMain;
	}
	
	private void initilizeViews()
	{
		ivGraphView= (BarOneDayView) vMain.findViewById(R.id.ivGraph);
		ivGraphView.drawGraph(arrayAQIValues);
		
	}
	
	private void initialiseNavigationBar()
	{
		ivLeftMenu= (ImageView) getActivity().findViewById(R.id.ivLeftMenu);
		ivRightDeviceIcon= (ImageView) getActivity().findViewById(R.id.ivRightDeviceIcon);
		ivCenterLabel= (ImageView) getActivity().findViewById(R.id.ivCenterLabel);
		
		ivLeftMenu.setBackgroundResource(R.drawable.left_menu_black);
		ivRightDeviceIcon.setBackgroundResource(R.drawable.right_device_icon_black);
		ivCenterLabel.setBackgroundResource(R.drawable.home_label_black);
	}

}
