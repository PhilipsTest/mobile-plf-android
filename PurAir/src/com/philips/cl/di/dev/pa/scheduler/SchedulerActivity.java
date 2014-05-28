package com.philips.cl.di.dev.pa.scheduler;

import java.net.HttpURLConnection;

import java.util.ArrayList;
import java.util.List;
import android.app.ProgressDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TimePicker;

import com.philips.cl.di.dev.pa.R;
import com.philips.cl.di.dev.pa.activity.BaseActivity;
import com.philips.cl.di.dev.pa.constant.AppConstants;
import com.philips.cl.di.dev.pa.constant.AppConstants.Port;
import com.philips.cl.di.dev.pa.cpp.CPPController;
import com.philips.cl.di.dev.pa.datamodel.SessionDto;
import com.philips.cl.di.dev.pa.newpurifier.ConnectionState;
import com.philips.cl.di.dev.pa.newpurifier.PurAirDevice;
import com.philips.cl.di.dev.pa.newpurifier.PurifierManager;
import com.philips.cl.di.dev.pa.purifier.TaskGetHttp;
import com.philips.cl.di.dev.pa.purifier.TaskPutDeviceDetails;
import com.philips.cl.di.dev.pa.scheduler.SchedulerConstants.SCHEDULE_TYPE;
import com.philips.cl.di.dev.pa.scheduler.SchedulerConstants.SchedulerID;
import com.philips.cl.di.dev.pa.security.DISecurity;
import com.philips.cl.di.dev.pa.util.ALog;
import com.philips.cl.di.dev.pa.util.DataParser;
import com.philips.cl.di.dev.pa.util.Fonts;
import com.philips.cl.di.dev.pa.util.JSONBuilder;
import com.philips.cl.di.dev.pa.util.ServerResponseListener;
import com.philips.cl.di.dev.pa.util.Utils;
import com.philips.cl.di.dev.pa.view.FontTextView;

public class SchedulerActivity extends BaseActivity implements OnClickListener,
		ServerResponseListener, OnTimeSetListener, SchedulerListener {

	private static boolean cancelled;
	private Button actionBarCancelBtn;
	private Button actionBarBackBtn;
	private FontTextView actionbarTitle;
	private String selectedDays = "";
	private String selectedTime;
	private String selectedFanspeed = SchedulerConstants.DEFAULT_FANSPEED_SCHEDULER;
	
	private SCHEDULE_TYPE scheduleType;
	private PurAirDevice purAirDevice ;
	private List<Integer> SchedulerMarked4Deletion = new ArrayList<Integer>();
	private SchedulerOverviewFragment schFragment;
	
	private List<SchedulePortInfo> schedulesList ;
	private ProgressDialog progressDialog ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scheduler_container);
		SchedulerMarked4Deletion.clear();
		initActionBar();
		showSchedulerOverviewFragment();
		purAirDevice = PurifierManager.getInstance().getCurrentPurifier() ;
		if( purAirDevice != null)	schedulesList = purAirDevice.getmSchedulerPortInfoList() ;
		PurifierManager.getInstance().setSchedulerListener(this) ;
		
	}
	
	private void initActionBar() {
		ActionBar actionBar;
		actionBar = getSupportActionBar();
		actionBar.setIcon(null);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);

		Drawable d = getResources().getDrawable(R.drawable.ews_nav_bar_2x);
		actionBar.setBackgroundDrawable(d);

		View view = getLayoutInflater().inflate(R.layout.scheduler_actionbar, null);
		actionbarTitle = (FontTextView) view.findViewById(R.id.scheduler_actionbar_title);
		actionbarTitle.setText(getString(R.string.scheduler));

		actionBarCancelBtn = (Button) view.findViewById(R.id.scheduler_actionbar_add_btn);
		actionBarCancelBtn.setTypeface(Fonts.getGillsansLight(this));
		actionBarCancelBtn.setOnClickListener(this);
		actionBarCancelBtn.setOnClickListener(onClickListener);

		actionBarBackBtn = (Button) view.findViewById(R.id.larrow);
		actionBarBackBtn.setOnClickListener(onClickListener);

		actionBar.setCustomView(view);
	}
	
	private void showProgressDialog() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Please wait...");
		progressDialog.show();
	}
	
	private void cancelProgressDialog() {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(progressDialog != null && progressDialog.isShowing()) {
					progressDialog.cancel() ;
				}				
			}			
		});	
	}
	
	/**
	 * 
	 */
	private void save() {
		ALog.i(ALog.SCHEDULER, "SchedulerActivity::Save() method enter");		
		if (scheduleType == SCHEDULE_TYPE.ADD) {
			addScheduler();
		} else if (scheduleType == SCHEDULE_TYPE.EDIT) {
			updateScheduler();
		}
	}
	
	/**
	 * Create the scheduler
	 */
	private void addScheduler() {
		scheduleType = SCHEDULE_TYPE.ADD ;
		ALog.i(ALog.SCHEDULER, "createScheduler") ;
		String addSchedulerJson = "" ;
		
		if(purAirDevice == null) return ;
		
		if( purAirDevice.getConnectionState() == ConnectionState.CONNECTED_LOCALLY) {
			 addSchedulerJson = JSONBuilder.getSchedulesJson(selectedTime, selectedFanspeed, selectedDays, true) ;
			TaskPutDeviceDetails addSchedulerTask =
					new TaskPutDeviceDetails(new DISecurity(null).encryptData(addSchedulerJson, purAirDevice), Utils.getPortUrl(Port.SCHEDULES, purAirDevice.getIpAddress()), this,"POST") ;
			Thread addSchedulerThread = new Thread(addSchedulerTask) ;
			addSchedulerThread.start() ;
			showSchedulerOverviewFragment();
		}
		else if( purAirDevice.getConnectionState() == ConnectionState.CONNECTED_REMOTELY) {
			addSchedulerJson = JSONBuilder.getSchedulesJsonforCPP(selectedTime, selectedFanspeed, selectedDays, true) ;
			CPPController.getInstance(this).publishEvent(JSONBuilder.getPublishEventBuilderForAddScheduler(addSchedulerJson), AppConstants.DI_COMM_REQUEST, AppConstants.ADD_PROPS, SessionDto.getInstance().getAppEui64(), "", 20, 120, purAirDevice.getEui64()) ;
		}
		showProgressDialog() ;
		//TODO - Implement Add scheduler Via CPP
	}
	
	private void updateScheduler() {
		scheduleType = SCHEDULE_TYPE.EDIT ;
	}
	
	
	public void deleteScheduler(int index) {
		ALog.i(ALog.SCHEDULER, "DELETE SCHEDULER: "+index) ;
		scheduleType = SCHEDULE_TYPE.DELETE ;
		int scheduleNumber = schedulesList.get(index).getScheduleNumber() ;
		if( purAirDevice == null || purAirDevice.getConnectionState() == ConnectionState.DISCONNECTED ) return ;
		if( purAirDevice.getConnectionState() == ConnectionState.CONNECTED_LOCALLY) {
			String url = Utils.getPortUrl(Port.SCHEDULES, purAirDevice.getIpAddress())+"/"+scheduleNumber ;
			ALog.i(ALog.SCHEDULER, url) ;
			TaskPutDeviceDetails deleteScheduleRunnable = new TaskPutDeviceDetails("", url, this,"DELETE") ;
			Thread deleteScheduleThread = new Thread(deleteScheduleRunnable) ;
			deleteScheduleThread.start() ;
			
		}		
		else if(purAirDevice.getConnectionState() == ConnectionState.CONNECTED_REMOTELY ) {
			CPPController.getInstance(this).publishEvent(JSONBuilder.getPublishEventBuilderForDeleteScheduler(scheduleNumber), AppConstants.DI_COMM_REQUEST, AppConstants.DEL_PROPS, SessionDto.getInstance().getAppEui64(), "", 20, 120, purAirDevice.getEui64()) ;
		}
		showProgressDialog() ;
	}
	
	private OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(final View v) {
			switch (v.getId()) {
			case R.id.scheduler_actionbar_add_btn:
				save();
				break;
				
			case R.id.larrow:
				showPreviousScreen4BackPressed();
				break;
			default:
				break;
			}
		}
	};
	
	public List<Integer> getSchedulerMarked4Deletion() {
		return SchedulerMarked4Deletion;
	}

	public void setActionBar(SchedulerID id) {
		switch (id) {
		case OVERVIEW_EVENT:
			setActionBar(R.string.scheduler, View.INVISIBLE, View.INVISIBLE);
			break;
		case ADD_EVENT:
			if (scheduleType == SCHEDULE_TYPE.ADD) {
				setActionBar(R.string.set_schedule, View.VISIBLE, View.VISIBLE);
			} else {
				setActionBar(R.string.edit_schedule, View.VISIBLE, View.VISIBLE);
			}
			break;
		case DELETE_EVENT:
			setActionBar(R.string.set_schedule, View.INVISIBLE, View.VISIBLE);
			break;
		case REPEAT:
			setActionBar(R.string.repeat_text, View.INVISIBLE, View.VISIBLE);
			break;
		case FAN_SPEED:
			setActionBar(R.string.fanspeed, View.INVISIBLE, View.VISIBLE);
			break;
		default:
			break;
		}
	}

	private void setActionBar(int textId, int cancelButton, int backButton) {
		ALog.i(ALog.SCHEDULER, "SchedulerActivity::setActionBar() method enter");
		actionbarTitle.setText(textId);
		actionBarCancelBtn.setVisibility(cancelButton);
		actionBarBackBtn.setVisibility(backButton);
		ALog.i(ALog.SCHEDULER, "SchedulerActivity::setActionBar() method exit");
	}


	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		String time;
		time = String.format("%2d:%02d", hourOfDay, minute);
		// String time = hourOfDay + ":" + minute;
		
		selectedTime = time;
		ALog.i(ALog.SCHEDULER, "SchedulerActivity::onTimeSet() method - SelectedTime is " + selectedTime);
		showAddSchedulerFragment();
	}
	

	public void dispatchInformations(String days) {
		updateCRUDOperationData(SchedulerConstants.EMPTY_STRING, days, SchedulerConstants.EMPTY_STRING, null);
	}

	public void dispatchInformations2(String fanspeed) {
		updateCRUDOperationData(SchedulerConstants.EMPTY_STRING, SchedulerConstants.EMPTY_STRING, fanspeed, null);
	}
	
	public void dispatchInformationsForCRUD(SCHEDULE_TYPE scheduleType) {
		ALog.i(ALog.SCHEDULER, "SchedulerActivity::dispatchInformationsForCRUD() method - crud is " + scheduleType);
		this.scheduleType = scheduleType;
	}
	
	/**
	 * Retrieves the list of schedules from Purifier
	 */
	private void getSchedulesFromPurifier() {
		scheduleType = SCHEDULE_TYPE.GET ;
		showProgressDialog() ;
		if( purAirDevice == null || purAirDevice.getConnectionState() ==
				ConnectionState.DISCONNECTED) return ;
		ALog.i(ALog.SCHEDULER, "Connection state: "+purAirDevice.getConnectionState()) ;
		if(	purAirDevice.getConnectionState() == ConnectionState.CONNECTED_LOCALLY) {
			TaskGetHttp getScheduleListRunnable = new TaskGetHttp(Utils.getPortUrl(Port.SCHEDULES, purAirDevice.getIpAddress()
					), this, this) ;
			Thread thread = new Thread(getScheduleListRunnable) ;
			thread.start() ;
			
		}
		else if(purAirDevice.getConnectionState() == ConnectionState.CONNECTED_REMOTELY) {
			ALog.i(ALog.SCHEDULER, "getAllSchedules from CPP ") ;
			CPPController.getInstance(this).publishEvent(JSONBuilder.getPublishEventBuilderForScheduler("","{}"), AppConstants.DI_COMM_REQUEST, AppConstants.GET_PROPS, SessionDto.getInstance().getAppEui64(), "", 20, 120, purAirDevice.getEui64());
		}
	}
	
	private void updateCRUDOperationData(String time, String date, String speed, List<Integer> markedDelete) {
			if (!time.isEmpty())
				selectedTime = time;
			if (!date.isEmpty() && !date.equals(SchedulerConstants.ONE_TIME))
				selectedDays = date;
			if (!speed.isEmpty())
				selectedFanspeed = speed;
		
		if (markedDelete != null) {
			SchedulerMarked4Deletion = markedDelete;
		}
		ALog.d(ALog.DISCOVERY, "SchedulerMarked4Deletion in updateCRUDOperationData: " + SchedulerMarked4Deletion.toString());
	}
	
	private void showSchedulerOverviewFragment() {
		schFragment =  new SchedulerOverviewFragment();
		ALog.i(ALog.SCHEDULER, "SchedulerActivity::showSchedulerOverviewFragment() method enter");
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.ll_scheduler_container, schFragment, "SchedulerOverviewFragment").commit();
	}
	
	private void showAddSchedulerFragment() {
		Bundle bundle = new Bundle();
		
		if (scheduleType == SCHEDULE_TYPE.ADD || scheduleType == SCHEDULE_TYPE.EDIT) {
			ALog.i(ALog.SCHEDULER, "SchedulerActivity::onTimeSet() method - create SelectedTime is " + selectedTime);
			bundle.putString(SchedulerConstants.TIME, selectedTime);
			bundle.putString(SchedulerConstants.DAYS, selectedDays);
			bundle.putString(SchedulerConstants.SPEED, selectedFanspeed);
		} 
		
		AddSchedulerFragment fragAddSch = new AddSchedulerFragment();
		fragAddSch.setArguments(bundle);
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.ll_scheduler_container, fragAddSch, "AddSchedulerFragment").commit();		
	}

	
	private void showPreviousScreen4BackPressed() {
		
		if (actionbarTitle.getText().equals(SchedulerConstants.SET_SCHEDULE) || actionbarTitle.getText().equals(SchedulerConstants.EDIT_SCHEDULE)) {
			showSchedulerOverviewFragment(); 
		} else if (actionbarTitle.getText().equals(SchedulerConstants.REPEAT) || actionbarTitle.getText().equals(SchedulerConstants.FANSPEED)) {
			showAddSchedulerFragment();
		} else {
			finish();
		}
	}


	@Override
	public void onBackPressed() {
		showPreviousScreen4BackPressed();
	}

	@Override
	protected void onPause() {
		super.onPause();
		setCancelled(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		getSchedulesFromPurifier() ;
		setCancelled(false);
	}

	@Override
	public void onClick(View v) {
	}
	
	@Override
	public void receiveServerResponse(int responseCode, String responseData, String fromIp) {
		cancelProgressDialog() ;
		switch (responseCode) {
		case HttpURLConnection.HTTP_OK:
			parseResponse(responseData) ;
			break;

		default:
			break;
		}
	}
	
	private void parseResponse(String response) {
		String decryptedResponse = new DISecurity(null).decryptData(response, purAirDevice);
		schedulesList = DataParser.parseSchedulerDto(decryptedResponse) ;
		//purAirDevice.setmSchedulerPortInfoList(schedulesList) ;
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if (schFragment == null) return;
				schFragment.updateList();
			}
		});
	}
	
	public List<SchedulePortInfo> getSchedulerList() {
		return schedulesList;
	}
		
	public static void setCancelled(boolean cancelled) {
	}

	public static boolean isCancelled() {
		return cancelled;
	}	
	
	@Override
	public void onSchedulesReceived(List<SchedulePortInfo> scheduleList) {
		ALog.i(ALog.SCHEDULER, "onSchedulers list response");
		cancelProgressDialog() ;
		if( scheduleList != null ) {
			this.schedulesList = scheduleList ;
			//purAirDevice.setmSchedulerPortInfoList(scheduleList) ;
			showSchedulerOverviewFragment();
		}
	}

	@Override
	public void onScheduleReceived(SchedulePortInfo schedule) {
		
	}
}
