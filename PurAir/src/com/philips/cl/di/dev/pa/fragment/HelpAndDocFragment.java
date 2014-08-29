package com.philips.cl.di.dev.pa.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.philips.cl.di.dev.pa.R;
import com.philips.cl.di.dev.pa.activity.OpenSourceLibLicensActivity;
import com.philips.cl.di.dev.pa.util.Fonts;

public class HelpAndDocFragment extends BaseFragment implements OnClickListener{
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.help_and_doc_fragment, container, false);
		initializeView(view);
		return view;
	}

	private void initializeView(View rootView) {
		
		TextView lblAppTutorial=(TextView) rootView.findViewById(R.id.app_tutorial);
		TextView lblFAQ= (TextView) rootView.findViewById(R.id.faq);
		TextView lblUserManual=(TextView) rootView.findViewById(R.id.lbl_user_manual);
		TextView lblPhilipsSupport=(TextView) rootView.findViewById(R.id.lbl_philips_support);
		TextView lblCallUs=(TextView) rootView.findViewById(R.id.lbl_call_us);
		TextView lblSupport=(TextView) rootView.findViewById(R.id.lbl_support);	
		TextView lblOpensource = (TextView) rootView.findViewById(R.id.opensource_lb);
		
		lblAppTutorial.setTypeface(Fonts.getGillsans(getActivity()));		
		lblFAQ.setTypeface(Fonts.getGillsans(getActivity()));
		lblUserManual.setTypeface(Fonts.getGillsans(getActivity()));
		lblPhilipsSupport.setTypeface(Fonts.getGillsans(getActivity()));
		lblCallUs.setTypeface(Fonts.getGillsans(getActivity()));
		lblSupport.setTypeface(Fonts.getGillsans(getActivity()));	
		lblOpensource.setTypeface(Fonts.getGillsans(getActivity()));	
		
		lblAppTutorial.setOnClickListener(this);
		
		RelativeLayout callUs = (RelativeLayout) rootView.findViewById(R.id.layout_call_us);
		callUs.setOnClickListener(this);
		
		RelativeLayout contactUs = (RelativeLayout) rootView.findViewById(R.id.layout_help);
		contactUs.setOnClickListener(this);
		
		lblFAQ.setOnClickListener(this);
		lblUserManual.setOnClickListener(this);
		lblOpensource.setOnClickListener(this);
	}	
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_call_us:
			//TODO : Move to one place.
			Intent dialSupportIntent = new Intent(Intent.ACTION_DIAL);
			dialSupportIntent.setData(Uri.parse("tel:" + getString(R.string.contact_philips_support_phone_num)));
			startActivity(Intent.createChooser(dialSupportIntent, "Air Purifier support"));
			break;
			
		case R.id.layout_help:
			Intent gotoSupportWebisteIntent = new Intent(Intent.ACTION_VIEW);
			gotoSupportWebisteIntent.setData(Uri.parse("http://" + getString(R.string.contact_philips_support_website)));
			startActivity(gotoSupportWebisteIntent);
			break;
			
		case R.id.faq:
		case R.id.lbl_user_manual:
		case R.id.app_tutorial:
			Intent faq = new Intent(Intent.ACTION_VIEW);
			faq.setData(Uri.parse("http://www.philips.com.cn/AC4373_00/prd"));
			startActivity(faq);
			break;
		
		case R.id.opensource_lb:
			getActivity().startActivity(new Intent(getActivity(), OpenSourceLibLicensActivity.class));
			break;
			
		default:
			break;
		}
	}
}
