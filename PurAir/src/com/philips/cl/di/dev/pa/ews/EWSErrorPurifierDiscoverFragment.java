package com.philips.cl.di.dev.pa.ews;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.philips.cl.di.dev.pa.R;
import com.philips.cl.di.dev.pa.util.ALog;
import com.philips.cl.di.dev.pa.util.Fonts;
import com.philips.cl.di.dev.pa.view.FontTextView;

public class EWSErrorPurifierDiscoverFragment  extends Fragment {
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.ews_error_purifier_not_detect, null);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		((EWSActivity) getActivity()).setActionBarHeading(EWSConstant.EWS_STEP_ERROR_DISCOVERY);
		
		FontTextView errorPurifierNotDectNetwork = 
				(FontTextView) getView().findViewById(R.id.ews_purifier_not_dect_network);
		Button tryAgainBtn = (Button) getView().findViewById(R.id.ews_purifier_not_dect_btn);
		tryAgainBtn.setTypeface(Fonts.getGillsansLight(getActivity()));
		tryAgainBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				EWSDialogFactory.getInstance(getActivity()).getDialog(EWSDialogFactory.ERROR_TS01_05).show();
				try {
					FragmentManager fragMan = getActivity().getSupportFragmentManager();
					fragMan.beginTransaction().add(
							EWSDialogFragment.newInstance(), "ews_error").commitAllowingStateLoss();
				} catch (IllegalStateException  e) {
					ALog.e(ALog.EWS, e.getMessage());
				} catch (Exception e) {
					ALog.e(ALog.EWS, e.getMessage());
				}
			}
		});
		
		errorPurifierNotDectNetwork.setText(((EWSActivity)getActivity()).getNetworkSSID());
	}
}
