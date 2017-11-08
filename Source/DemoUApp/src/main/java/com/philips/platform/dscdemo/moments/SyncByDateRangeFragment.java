/* Copyright (c) Koninklijke Philips N.V., 2017
* All rights are reserved. Reproduction or dissemination
* in whole or in part is prohibited without the prior written
* consent of the copyright holder.
*/
package com.philips.platform.dscdemo.moments;


import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.philips.platform.core.datatypes.Moment;
import com.philips.platform.core.datatypes.SyncType;
import com.philips.platform.core.listeners.DBChangeListener;
import com.philips.platform.core.listeners.DBFetchRequestListner;
import com.philips.platform.core.listeners.DBRequestListener;
import com.philips.platform.core.listeners.SynchronisationCompleteListener;
import com.philips.platform.core.trackers.DataServicesManager;
import com.philips.platform.dscdemo.DSBaseFragment;
import com.philips.platform.dscdemo.R;
import com.philips.platform.dscdemo.utility.SyncScheduler;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SyncByDateRangeFragment extends DSBaseFragment
		implements View.OnClickListener, DBFetchRequestListner<Moment>,
		DBRequestListener<Moment>, DBChangeListener, SynchronisationCompleteListener {

	private Context mContext;
	private MomentPresenter mMomentPresenter;
	private Button btnCompleteSync;
	private Button btnSyncMomentByDate;
	private LinearLayout dateRangeSelectorLayout;
	private Button btnStartSyncByDateRange;

	private Date mStartDate;
	private Date mEndDate;
	private EditText mMomentStartDateEt;
	private EditText mMomentEndDateEt;
	private ToggleButton mEnableDisableSync;
	private Calendar myCalendar;
	private TextView tvSyncStatus;
	private EnableDisableSync enableDisableSync;


	final DatePickerDialog.OnDateSetListener startDate = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			myCalendar.set(Calendar.YEAR, year);
			myCalendar.set(Calendar.MONTH, monthOfYear);
			myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			try {
				updateStartDate();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	};

	final DatePickerDialog.OnDateSetListener endDate = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			myCalendar.set(Calendar.YEAR, year);
			myCalendar.set(Calendar.MONTH, monthOfYear);
			myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			try {
				updateEndDate();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	};

	public interface EnableDisableSync {
		void isSyncEnabled(boolean isChecked);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMomentPresenter = new MomentPresenter(mContext, this);
		myCalendar = Calendar.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.momentsync_by_daterange, container, false);
		btnCompleteSync = view.findViewById(R.id.btn_completeSync);
		btnSyncMomentByDate = view.findViewById(R.id.btn_syncMoment_by_dateRange);
		mMomentStartDateEt = view.findViewById(R.id.et_moment_startDate);
		mMomentEndDateEt = view.findViewById(R.id.et_moment_endDate);
		dateRangeSelectorLayout = view.findViewById(R.id.dateRangeSelectorLayout);
		btnStartSyncByDateRange = view.findViewById(R.id.btn_startSyncBy_dateRange);
		tvSyncStatus = view.findViewById(R.id.tvSyncStatus);
		mEnableDisableSync = view.findViewById(R.id.toggleButton);
		//final SyncScheduler syncScheduler = new SyncScheduler((FragmentActivity) mContext);

		mEnableDisableSync.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					SyncScheduler.getInstance().scheduleSync();
					enableDisableSync.isSyncEnabled(true);
				} else {
					SyncScheduler.getInstance().stopSync();
					enableDisableSync.isSyncEnabled(false);
				}
			}
		});

		if(SyncScheduler.getInstance().getSyncStatus()) {
			updateTextView("InProgress");
		} else {
			updateTextView("Stopped");
		}

		mMomentStartDateEt.setOnClickListener(this);
		mMomentEndDateEt.setOnClickListener(this);
		btnSyncMomentByDate.setOnClickListener(this);
		btnStartSyncByDateRange.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View view) {
		if (view == btnCompleteSync) {
			DataServicesManager.getInstance().synchronize();
			updateTextView("InProgress");
		} else if (view == btnSyncMomentByDate) {
			dateRangeSelectorLayout.setVisibility(View.VISIBLE);
		} else if (view == mMomentStartDateEt) {
			new DatePickerDialog(mContext, startDate, myCalendar
					.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
					myCalendar.get(Calendar.DAY_OF_MONTH)).show();
		} else if (view == mMomentEndDateEt) {
			new DatePickerDialog(mContext, endDate, myCalendar
					.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
					myCalendar.get(Calendar.DAY_OF_MONTH)).show();
		} else if (view == btnStartSyncByDateRange) {
			fetchSyncByDateRange();
			updateTextView("InProgress");
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		DataServicesManager.getInstance().registerDBChangeListener(this);
		DataServicesManager.getInstance().registerSynchronisationCompleteListener(this);
	}

	private void fetchSyncByDateRange() {
		mMomentPresenter.fetchSyncByDateRange(new DateTime(mStartDate), new DateTime(mEndDate), this);
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mContext = context;
	}


	@Override
	public void onSyncComplete() {
		updateTextView("Sync Completed");
		Toast.makeText(mContext, "OnSyncComplete", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onSyncFailed(Exception exception) {
		updateTextView("Sync Failed");
		Toast.makeText(mContext, "OnSyncFailed", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onFetchSuccess(List<? extends Moment> data) {

	}

	@Override
	public void onFetchFailure(Exception exception) {
		Toast.makeText(mContext, "onFetchFailure", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onSuccess(List<? extends Moment> data) {

	}

	@Override
	public void onFailure(Exception exception) {
		Toast.makeText(mContext, "onFailure", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void dBChangeSuccess(SyncType type) {
		Toast.makeText(mContext, "dBChangeSuccess", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void dBChangeFailed(Exception e) {
		Toast.makeText(mContext, "dBChangeFailed", Toast.LENGTH_SHORT).show();
	}

	@Override
	public int getActionbarTitleResId() {
		return 0;
	}

	@Override
	public String getActionbarTitle() {
		return null;
	}

	@Override
	public boolean getBackButtonState() {
		return false;
	}

	public void setListener(SyncByDateRangeFragment.EnableDisableSync enableDisableSync){
		this.enableDisableSync = enableDisableSync;
	}

	private void updateStartDate() throws ParseException {
		String myFormat = "yyyy-MM-dd'T' K mm:ss.SSS Z";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
		String dateAsString = sdf.format(myCalendar.getTime());
		mMomentStartDateEt.setText(dateAsString);
		mStartDate = sdf.parse(dateAsString);
	}

	private void updateEndDate() throws ParseException {
		String myFormat = "yyyy-MM-dd'T' K mm:ss.SSS Z";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
		String dateAsString = sdf.format(myCalendar.getTime());
		mMomentEndDateEt.setText(dateAsString);
		mEndDate = sdf.parse(dateAsString);
	}

	private void updateTextView(String text) {
		tvSyncStatus.setText(text);
	}

	@Override
	public void onStop() {
		super.onStop();
		DataServicesManager.getInstance().unRegisterDBChangeListener();
		DataServicesManager.getInstance().unRegisterSynchronisationCosmpleteListener();
	}


}
