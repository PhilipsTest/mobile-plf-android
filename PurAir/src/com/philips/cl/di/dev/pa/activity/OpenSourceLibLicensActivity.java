package com.philips.cl.di.dev.pa.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.philips.cl.di.dev.pa.R;

public class OpenSourceLibLicensActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.opensource_lib);
		
		this.getSupportActionBar().hide();
		ImageButton closePrivacyPolicy= (ImageButton) findViewById(R.id.close_opensource_lib_imgbtn);
		closePrivacyPolicy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

}
