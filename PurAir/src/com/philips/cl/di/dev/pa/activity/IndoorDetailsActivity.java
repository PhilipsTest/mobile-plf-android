package com.philips.cl.di.dev.pa.activity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.philips.cl.di.dev.pa.R;
import com.philips.cl.di.dev.pa.controller.CPPController;
import com.philips.cl.di.dev.pa.controller.SensorDataController;
import com.philips.cl.di.dev.pa.detail.utils.Coordinates;
import com.philips.cl.di.dev.pa.detail.utils.GraphConst;
import com.philips.cl.di.dev.pa.dto.AirPurifierEventDto;
import com.philips.cl.di.dev.pa.dto.SessionDto;
import com.philips.cl.di.dev.pa.fragment.IndoorAQIExplainedDialogFragment;
import com.philips.cl.di.dev.pa.interfaces.ICPDownloadListener;
import com.philips.cl.di.dev.pa.interfaces.PercentDetailsClickListener;
import com.philips.cl.di.dev.pa.interfaces.SensorEventListener;
import com.philips.cl.di.dev.pa.utils.ALog;
import com.philips.cl.di.dev.pa.utils.Fonts;
import com.philips.cl.di.dev.pa.utils.Utils;
import com.philips.cl.di.dev.pa.view.CustomTextView;
import com.philips.cl.di.dev.pa.view.GraphView;
import com.philips.cl.di.dev.pa.view.PercentBarLayout;
import com.philips.icpinterface.data.Errors;

public class IndoorDetailsActivity extends BaseActivity implements OnClickListener,
PercentDetailsClickListener, SensorEventListener, ICPDownloadListener {

	private ActionBar mActionBar;
	private final String TAG = "IndoorDetailsActivity";
	private LinearLayout graphLayout;
	private TextView lastDayBtn, lastWeekBtn, lastFourWeekBtn;
	private TextView heading;
	private ImageView circleImg, modeIcon, filterIcon;
	private CustomTextView msgFirst, msgSecond, indoorDbIndexName;
	private ImageView indexBottBg;
	private HorizontalScrollView horizontalScrollView;
	private ProgressBar rdcpDownloadProgressBar;
	private PercentBarLayout percentBarLayout;
	private CustomTextView barTopNum, barTopName, selectedIndexBottom;
	private CustomTextView modeLabel, filterLabel, mode, filter, aqiStatus, aqiSummary;
	private List<int[]> powerOnReadingsValues;
	private List<float[]> lastDayRDCPValues;
	private List<float[]> last7daysRDCPValues;
	private List<float[]> last4weeksRDCPValues;

	private List<Float> hrlyAqiValues;
	private List<Float> dailyAqiValues ;
	private List<Integer> goodAirInfos;
	private List<Integer> powerOnStatusList;
	private Coordinates coordinates;

	private String outdoorTitle = "";

	private int goodAirCount = 0;
	private int totalAirCount = 0;
	private Handler handler = new Handler();

	private int powerOnReadings[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0 };

	public float lastDayRDCPVal[] = { -1F, -1F, -1F, -1F, -1F, -1F, -1F, -1F,
			-1F, -1F, -1F, -1F, -1F, -1F, -1F, -1F,-1F, -1F, -1F, -1F, -1F, -1F, -1F, -1F, };

	public float last7daysRDCPVal[] = { -1F, -1F, -1F, -1F, -1F, -1F, -1F};

	public float last4weeksRDCPVal[] = { -1F, -1F, -1F, -1F, -1F, -1F, -1F, -1F, -1F, -1F, -1F, -1F,
			-1F, -1F, -1F, -1F, -1F, -1F, -1F, -1F,-1F, -1F, -1F, -1F, -1F, -1F, -1F, -1F, };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_trends_indoor);
		coordinates = Coordinates.getInstance(this);
		initializeUI();

		SensorDataController.getInstance(this).addListener(this) ;
		initActionBar();
		getDataFromDashboard();

		rdcpDownloadProgressBar.setVisibility(View.VISIBLE);
		if (SessionDto.getInstance().getIndoorTrendDto() == null &&
				CPPController.getInstance(this) != null) {
//			if(CPPController.getInstance(this).isSignOn()) {
				CPPController.getInstance(this).setDownloadDataListener(this) ;
				//"Clientid=1c5a6bfffe6341fe;datatype=airquality.1;startDate=2014-01-12T05:46:05.1508314Z;endDate=2014-02-13T06:46:05.1508314Z"
				CPPController.getInstance(this).downloadDataFromCPP(Utils.getCPPQuery(this), 2048) ;
//			}
//			else {
//				Toast.makeText(this, "Please signon", Toast.LENGTH_LONG).show() ;
//				parseReading();
//			}
		} 
		else if( SessionDto.getInstance().getIndoorTrendDto()  != null ) {
			hrlyAqiValues = SessionDto.getInstance().getIndoorTrendDto().getHourlyList() ;
			dailyAqiValues = SessionDto.getInstance().getIndoorTrendDto().getDailyList() ;
			powerOnStatusList = SessionDto.getInstance().getIndoorTrendDto().getPowerDetailsList() ;
			parseReading();
		} 
	}


	private Runnable downloadDataRunnble = new Runnable() {

		@Override
		public void run() {
			handler.removeCallbacks(downloadDataRunnble);
			rdcpDownloadProgressBar.setVisibility(View.GONE);
			callGraphViewOnClickEvent(0, lastDayRDCPValues);
		}
	};


	
	/**
	 * Initialize UI widget
	 * */
	private void initializeUI() {
		powerOnReadingsValues = new ArrayList<int[]>();
		lastDayRDCPValues = new ArrayList<float[]>();
		last7daysRDCPValues = new ArrayList<float[]>();
		last4weeksRDCPValues = new ArrayList<float[]>();
		goodAirInfos = new ArrayList<Integer>();

		graphLayout = (LinearLayout) findViewById(R.id.trendsOutdoorlayoutGraph);

		lastDayBtn = (TextView) findViewById(R.id.detailsOutdoorLastDayLabel);
		lastWeekBtn = (TextView) findViewById(R.id.detailsOutdoorLastWeekLabel);
		lastFourWeekBtn = (TextView) findViewById(R.id.detailsOutdoorLastFourWeekLabel);

		modeIcon = (ImageView) findViewById(R.id.inModeIcon); 
		filterIcon = (ImageView) findViewById(R.id.inFilterIcon); 
		circleImg = (ImageView) findViewById(R.id.inDetailsDbCircle); 
		indexBottBg= (ImageView) findViewById(R.id.indoorDbIndexBottBg); 

		msgFirst = (CustomTextView) findViewById(R.id.idFirstMsg);
		msgSecond = (CustomTextView) findViewById(R.id.idSecondMsg);
		modeLabel = (CustomTextView) findViewById(R.id.inModeTxt);
		mode = (CustomTextView) findViewById(R.id.inModeType);
		filterLabel = (CustomTextView) findViewById(R.id.inFilterTxt);
		filter = (CustomTextView) findViewById(R.id.inFilterType);
		aqiStatus = (CustomTextView) findViewById(R.id.inDetailsDbStatus);
		aqiSummary = (CustomTextView) findViewById(R.id.inDetailsDbSummary);
		indoorDbIndexName = (CustomTextView) findViewById(R.id.indoorDbIndexName);

		horizontalScrollView = (HorizontalScrollView) findViewById(R.id.indoorDbHorizontalScroll);
		barTopNum = (CustomTextView) findViewById(R.id.indoorDbBarTopNum);
		barTopName = (CustomTextView) findViewById(R.id.indoorDbBarTopName);
		selectedIndexBottom = (CustomTextView) findViewById(R.id.indoorDbIndexBott);
		selectedIndexBottom.setText(String.valueOf(1));
		
		rdcpDownloadProgressBar = (ProgressBar) findViewById(R.id.rdcpDownloadProgressBar);
		
		/**
		 * Set click listener
		 * */
		lastDayBtn.setOnClickListener(this);
		lastWeekBtn.setOnClickListener(this);
		lastFourWeekBtn.setOnClickListener(this);
	}

	/**
	 * InitActionBar
	 */
	private void initActionBar() {
		mActionBar = getSupportActionBar();
		mActionBar.setIcon(null);
		mActionBar.setHomeButtonEnabled(false);
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
		mActionBar.setCustomView(R.layout.action_bar);

	}

	/*Sets Action bar title */
	public void setActionBarTitle(String name) {    	
		heading = (TextView) findViewById(R.id.action_bar_title);
		heading.setTypeface(Fonts.getGillsansLight(this));
		heading.setTextSize(24);
		heading.setText(name + getString(R.string.apos_s)  + " "+  getString(R.string.room));
		indoorDbIndexName.setText(name + getString(R.string.apos_s)  + " "+  getString(R.string.room));
		barTopName.setText(name + getString(R.string.apos_s)  + " "+  getString(R.string.room));

	}

	/**
	 * Parsing reading
	 * */
	public void parseReading() {

		if (goodAirInfos != null) {
			goodAirInfos.clear();
		}

		/**Last day*/
		if (lastDayRDCPValues != null) {
			lastDayRDCPValues.clear();
		} 

		if (powerOnReadingsValues != null) {
			powerOnReadingsValues.clear();
		} 

		goodAirCount = 0;
		totalAirCount = 0;
		if (hrlyAqiValues != null && hrlyAqiValues.size() == 24) {

			for (int i = 0; i < lastDayRDCPVal.length; i++) {
				lastDayRDCPVal[i] = hrlyAqiValues.get(i);
				if (powerOnStatusList != null && powerOnStatusList.size() == 24) {
					powerOnReadings[i] = powerOnStatusList.get(i);
				} 
				if (hrlyAqiValues.get(i) != -1) {
					if (hrlyAqiValues.get(i) <= 2) {
						goodAirCount ++;
					}
					totalAirCount ++;
				}
			}
		}
		goodAirInfos.add(Utils.getPercentage(goodAirCount, totalAirCount));
		lastDayRDCPValues.add(lastDayRDCPVal);
		powerOnReadingsValues.add(powerOnReadings);

		/**Last 7 days and last 4 weeks*/
		if (last7daysRDCPValues != null) {
			last7daysRDCPValues.clear();
		} 

		if (last4weeksRDCPValues != null) {
			last4weeksRDCPValues.clear();
		} 

		int tempIndex = 0;
		goodAirCount = 0;
		totalAirCount = 0;
		int tempGood = 0;
		int tempCount = 0;
		if (dailyAqiValues != null && dailyAqiValues.size() > 0
				&& dailyAqiValues.size() == 28) {

			for (int i = 0; i < last4weeksRDCPVal.length; i++) {
				if (i > 20) {
					last7daysRDCPVal[tempIndex] = dailyAqiValues.get(i);
					tempIndex++;
					if (dailyAqiValues.get(i) != -1) {
						if (dailyAqiValues.get(i) <= 2) {
							tempGood ++;
						}
						tempCount ++;
					}
				}
				last4weeksRDCPVal[i] = dailyAqiValues.get(i);
				if (dailyAqiValues.get(i) != -1) {
					if (dailyAqiValues.get(i) <= 2) {
						goodAirCount ++;
					}
					totalAirCount ++;
				}
			}

		}
		goodAirInfos.add(Utils.getPercentage(tempGood, tempCount));
		goodAirInfos.add(Utils.getPercentage(goodAirCount, totalAirCount));
		last7daysRDCPValues.add(last7daysRDCPVal);
		last4weeksRDCPValues.add(last4weeksRDCPVal);
		Utils.calculateOutdoorAQIValues();

		handler.removeCallbacks(downloadDataRunnble);
		handler.post(downloadDataRunnble);
	}

	
	/**
	 * onClick
	 * */
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.detailsOutdoorLastDayLabel: {
			callGraphViewOnClickEvent(0, lastDayRDCPValues);
			
			lastDayBtn.setTextColor(GraphConst.COLOR_DODLE_BLUE);
			lastWeekBtn.setTextColor(Color.LTGRAY);
			lastFourWeekBtn.setTextColor(Color.LTGRAY);
			msgFirst.setText(getString(R.string.good_air_message_last_day));
			msgSecond.setText(getString(R.string.detail_aiq_message_last_day));
			break;
		}
		case R.id.detailsOutdoorLastWeekLabel: {
			callGraphViewOnClickEvent(1, last7daysRDCPValues);

			lastDayBtn.setTextColor(Color.LTGRAY);
			lastWeekBtn.setTextColor(GraphConst.COLOR_DODLE_BLUE);
			lastFourWeekBtn.setTextColor(Color.LTGRAY);
			msgFirst.setText(getString(R.string.goor_air_message_last7day));
			msgSecond.setText(getString(R.string.detail_aiq_message_last7day));
			break;
		}
		case R.id.detailsOutdoorLastFourWeekLabel: {
			callGraphViewOnClickEvent(2, last4weeksRDCPValues);
			
			lastDayBtn.setTextColor(Color.LTGRAY);
			lastWeekBtn.setTextColor(Color.LTGRAY);
			lastFourWeekBtn.setTextColor(GraphConst.COLOR_DODLE_BLUE);
			msgFirst.setText(getString(R.string.good_air_message_last4week));
			msgSecond.setText(getString(R.string.detail_aiq_message_last4week));
			break;
		}
		default:
			break;
		}

	}
	
	private void callGraphViewOnClickEvent(int index, List<float[]> rdcpValues) {
		removeChildViewFromBar();
		if (goodAirInfos != null && goodAirInfos.size() > 0) {
			percentBarLayout = new PercentBarLayout(IndoorDetailsActivity.this,
					null, goodAirInfos, this, index, 0);
			percentBarLayout.setClickable(true);
			horizontalScrollView.addView(percentBarLayout);
		}

		if (rdcpValues != null && rdcpValues.size() > 0) {
			graphLayout.addView(new GraphView(this, rdcpValues, null, coordinates, 0, indexBottBg));
		}	
	}

	private void removeChildViewFromBar() {

		barTopNum.setText("1");
		//barTopName.setText("Living room");
		selectedIndexBottom.setText("1");

		if (horizontalScrollView.getChildCount() > 0) {
			horizontalScrollView.removeAllViews();
		}

		if (graphLayout.getChildCount() > 0) {
			graphLayout.removeAllViews();
		}
	}

	@Override
	public void clickedPosition(int position, int index) {
		selectedIndexBottom.setText(""+(position+1));
		barTopNum.setText(""+(position+1));

		if (horizontalScrollView.getChildCount() > 0) {
			horizontalScrollView.removeAllViews();
		}
		if (goodAirInfos != null && goodAirInfos.size() > 0) {
			percentBarLayout = new PercentBarLayout(IndoorDetailsActivity.this, 
					null, goodAirInfos, this, index, position);
			percentBarLayout.setClickable(true);
			horizontalScrollView.addView(percentBarLayout);
		}

		if (graphLayout.getChildCount() > 0) {
			graphLayout.removeViewAt(0);
		}
	}

	/**
	 * 
	 */
	private void getDataFromDashboard() {
		String datas[] = getIntent().getStringArrayExtra("indoor");
		/**
		 * Updating all the details in the screen, which is passed from Dashboard
		 */
		//Log.i(TAG, "Data from Dashboard= " + datas);
		if (datas != null && datas.length > 0) {

			if (datas[0] != null) {
				mode.setText(datas[0]);
			}

			if (datas[1] != null) {
				filter.setText(datas[1]);
			}
			
			if (datas[2] != null) {
				try {
					float indoorAQI = Float.parseFloat(datas[2].trim());
					circleImg.setImageDrawable(Utils.getIndoorAQICircleBackground(this, indoorAQI));

					Utils.setIndoorAQIStatusAndComment(this, indoorAQI, aqiStatus, aqiSummary, datas[6]);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}

			if (datas[5] != null) {
				outdoorTitle = datas[5];
			}
			
			if (datas[6] != null) {
				setActionBarTitle(datas[6]);
			}
		}
		
	}

	

	@Override
	protected void onStop() {
		super.onStop();
		SensorDataController.getInstance(this).removeListener(this) ;
	}

	public void aqiAnalysisClick(View v) {
		FragmentManager fragMan = getSupportFragmentManager();
		fragMan.beginTransaction().add(IndoorAQIExplainedDialogFragment.newInstance(aqiStatus.getText().toString(), outdoorTitle), "outdoor").commit();
	}

	@Override
	public void sensorDataReceived(AirPurifierEventDto airPurifierEventDto) {
		// TODO Auto-generated method stub
		Log.i("Indoor", "Sensor Event Received") ;
	}

	/**
	 * rDcp values download
	 */
	@Override
	public void onDataDownload(int status, String downloadedData) {
		
		if( status == Errors.SUCCESS) {
			Utils.parseIndoorDetails(downloadedData) ;
			
			if( SessionDto.getInstance().getIndoorTrendDto() != null ) {
				hrlyAqiValues = SessionDto.getInstance().getIndoorTrendDto().getHourlyList() ;
				dailyAqiValues = SessionDto.getInstance().getIndoorTrendDto().getDailyList() ;
				powerOnStatusList = SessionDto.getInstance().getIndoorTrendDto().getPowerDetailsList() ;
				ALog.i(ALog.INDOOR_DETAILS, "hrlyAqiValues==  " + hrlyAqiValues);
				ALog.i(ALog.INDOOR_DETAILS, "dailyAqiValues==  " + dailyAqiValues);
				ALog.i(ALog.INDOOR_DETAILS, "powerOnStatusList==  " + powerOnStatusList);
			}
			parseReading();
		} else  {
			ALog.i(ALog.INDOOR_DETAILS, "onDataDownload status: " + status);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		handler.removeCallbacks(downloadDataRunnble);
		finish();
	}
}

