package com.philips.platform.aildemo;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.philips.platform.appinfra.appupdate.AppUpdateInterface;
import com.philips.platform.appinfra.appupdate.AppUpdateManager;
import com.philips.platform.appinfra.demo.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class AppUpdateActivity extends AppCompatActivity {

	private Button appUpdateRefresh;
	private AppUpdateInterface appupdateInterface;
	private TextView tvappversionval;
	private TextView tvminversionval;
	private TextView tvToBeDeprecatedDate;
	private TextView tvisDeprecated;
	private TextView tvisToBeDeprecated;
	private TextView tvisUpdateAvailable;
	private TextView tvDeprecateMessage;
	private TextView tvToBeDeprecatedMessage;
	private TextView tvUpdateMessage;
	private TextView tvMinimumOSverion;
	private Button fetchappupdateValues;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appupdate);
		appUpdateRefresh = (Button) findViewById(R.id.appUpdateRefresh);
		fetchappupdateValues = (Button) findViewById(R.id.fetchappupdateValues);
		tvappversionval = (TextView) findViewById(R.id.tvappversionval);
		tvminversionval = (TextView) findViewById(R.id.tvminversionval);

		tvisDeprecated = (TextView) findViewById(R.id.tvisDeprecated);
		tvToBeDeprecatedDate = (TextView) findViewById(R.id.tvToBeDeprecatedDate);


		tvisToBeDeprecated = (TextView) findViewById(R.id.tvisToBeDeprecated);

		tvisUpdateAvailable = (TextView) findViewById(R.id.tvisUpdateAvailable);

		tvDeprecateMessage = (TextView) findViewById(R.id.tvDeprecateMessage);

		tvToBeDeprecatedMessage = (TextView) findViewById(R.id.tvToBeDeprecatedMessage);

		tvUpdateMessage = (TextView) findViewById(R.id.tvUpdateMessage);

		tvMinimumOSverion = (TextView) findViewById(R.id.tvMinimumOSverion);


		appupdateInterface = AILDemouAppInterface.getInstance().getAppInfra().getAppUpdate();

		appUpdateRefresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				appupdateInterface.refresh(new AppUpdateInterface.OnRefreshListener() {
					@Override
					public void onError(AIAppUpdateRefreshResult error, String message) {
						Toast.makeText(AppUpdateActivity.this, message, Toast.LENGTH_LONG).show();
					}

					@Override
					public void onSuccess(AIAppUpdateRefreshResult result) {
						Toast.makeText(AppUpdateActivity.this, result.toString(), Toast.LENGTH_LONG).show();
					}
				});
			}
		});

		fetchappupdateValues.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tvappversionval.setText(AILDemouAppInterface.getInstance().getAppInfra().getAppIdentity().getAppVersion());
				tvminversionval.setText(appupdateInterface.getMinimumVersion());
				tvisDeprecated.setText(String.valueOf(appupdateInterface.isDeprecated()));
				tvisToBeDeprecated.setText(String.valueOf(appupdateInterface.isToBeDeprecated()));
				tvisUpdateAvailable.setText(String.valueOf(appupdateInterface.isUpdateAvailable()));
				tvDeprecateMessage.setText(appupdateInterface.getDeprecateMessage());
				tvToBeDeprecatedMessage.setText(appupdateInterface.getToBeDeprecatedMessage());
				tvUpdateMessage.setText(appupdateInterface.getUpdateMessage());
				tvMinimumOSverion.setText(appupdateInterface.getMinimumOSverion());
				SimpleDateFormat formatter = new SimpleDateFormat(AppUpdateManager.APPUPDATE_DATE_FORMAT
						, Locale.ENGLISH);
				if(appupdateInterface.getToBeDeprecatedDate() != null) {
					String s = formatter.format(appupdateInterface.getToBeDeprecatedDate());
					tvToBeDeprecatedDate.setText(s);
				} else {
					tvToBeDeprecatedDate.setText(null);
				}
			}
		});

	}
}
