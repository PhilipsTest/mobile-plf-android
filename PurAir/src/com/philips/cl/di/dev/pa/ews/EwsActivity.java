package com.philips.cl.di.dev.pa.ews;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.philips.cl.di.common.ssdp.contants.ConnectionLibContants;
import com.philips.cl.di.common.ssdp.contants.MessageID;
import com.philips.cl.di.common.ssdp.controller.InternalMessage;
import com.philips.cl.di.common.ssdp.lib.SsdpService;
import com.philips.cl.di.common.ssdp.models.DeviceModel;
import com.philips.cl.di.dev.pa.R;
import com.philips.cl.di.dev.pa.constants.AppConstants;
import com.philips.cl.di.dev.pa.customviews.CustomTextView;
import com.philips.cl.di.dev.pa.detail.utils.GraphConst;
import com.philips.cl.di.dev.pa.dto.SessionDto;
import com.philips.cl.di.dev.pa.pureairui.MainActivity;
import com.philips.cl.di.dev.pa.utils.Fonts;

public class EwsActivity extends ActionBarActivity implements OnClickListener, EWSListener, Callback {

	private int step = -1;
	/**
	 * Action bar variable
	 */
	private ActionBar mActionBar;
	private CustomTextView actionbarTitle;
	private Button actionBarCancelBtn;
	/**
	 * Step1 variable declare
	 */
	private View viewStart;
	/**
	 * Step1 variable declare
	 */
	private CustomTextView wifiNetworkNameStep1;
	private View viewStep1;
	/**
	 * Step2 variable declare
	 */
	private View viewStep2;
	/**
	 * Step3 variable declare
	 */
	private CustomTextView passwordLabelStep3, wifiNetworkAddStep3;
	private EditText passwordStep3, deviceNameStep3, 
	ipAddStep3, subnetMaskStep3, routerAddStep3;
	private ImageView showPasswordImgStep3;
	private Button editSavePlaceNameBtnStep3;
	private RelativeLayout advSettingLayoutStep3;
	private LinearLayout advSettingBtnLayoutStep3;
	private boolean isPasswordVisibelStep3 = true;
	//private String placeBtnTxtStep3;
	private View viewStep3;
	/**
	 * Congratulation variable declare
	 */
	private View viewCongratulation;
	/**
	 * Congratulation variable declare
	 */
	private View viewErrorPurifierNotDect;
	private CustomTextView errorPurifierNotDectNetwork;
	/**
	 * Error connect to your network variable declare
	 */
	private View viewErrorConnect2Network;
	/**
	 * Error SSID variable declare
	 */
	private View viewErrorSSID;
	private CustomTextView errorSSIDNetwork;
	/**
	 * Contact Philips support variable declare
	 */
	private View viewContactPhilipsSupport;
	private CustomTextView contactPhilipsMessage1, contactPhilipsPhone, 
	contactPhilipsEmail, contactPhilipsWeb;

	private LayoutInflater inflater;

	private WifiManager wifiManager ;
	private String networkSSID ;
	private String password ;

	private SsdpService ssdpService ;
	
	private Dialog progressDialogForStep2 ;
	
	private EWSService ewsService ;
	private String ipAddress ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.ews_error_purifier_not_detect);

		initActionBar();
		
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		inflater = getLayoutInflater();

		initializeIntroVariable();
		initializeStep1Variable();
		initializeStep2Variable();
		initializeStep3Variable();
		initializeCongraltVariable();
		initializeErrorPurifierNotDetect();
		initializeErrorConnect2Network();
		initializeErrorSSID();
		initializeContactPhilips();
		setActionBarHeading(1);
		setContentView(viewStart);
		progressDialogForStep2 = EWSDialogFactory.getInstance(this).getDialog(EWSDialogFactory.CHECK_SIGNAL_STRENGTH) ;
	}

	private void initializeIntroVariable() {
		viewStart = inflater.inflate(R.layout.ews_intro_screen, null);
		((Button) viewStart.findViewById(R.id.ews_get_start_btn)).setOnClickListener(this);
		((Button) viewStart.findViewById(R.id.ews_get_start_btn)).setTypeface(Fonts.getGillsansLight(this));
	}

	private void initializeStep1Variable() {
		viewStep1 = inflater.inflate(R.layout.ews_step1, null);

		wifiNetworkNameStep1 = (CustomTextView) viewStep1.findViewById(R.id.ews_step1_wifi_network);

		((Button) viewStep1.findViewById(R.id.ews_step1_yes_btn)).setOnClickListener(this);
		((Button) viewStep1.findViewById(R.id.ews_step1_yes_btn)).setTypeface(Fonts.getGillsansLight(this));
		
		((Button) viewStep1.findViewById(R.id.ews_step1_no_btn)).setOnClickListener(this);
		((Button) viewStep1.findViewById(R.id.ews_step1_no_btn)).setTypeface(Fonts.getGillsansLight(this));

	}

	private void initializeStep2Variable() {
		viewStep2 = inflater.inflate(R.layout.ews_step2, null);

		CustomTextView step2Message1 = (CustomTextView) viewStep2.findViewById(R.id.ews_step2_message1);
		CustomTextView step2Message2 = (CustomTextView) viewStep2.findViewById(R.id.ews_step2_message2);

		String msg1 = getString(R.string.step2_msg1) + " <font color=#EF6921>"+getString(R.string.orange)+"</font>.";
		step2Message1.setText(Html.fromHtml(msg1));

		String msg2 = getString(R.string.step2_msg2) + " <font color=#EF6921>"+getString(R.string.orange)+"</font>?";
		step2Message2.setText(Html.fromHtml(msg2));
		
		((Button) viewStep2.findViewById(R.id.ews_step2_yes_btn)).setOnClickListener(this);
		((Button) viewStep2.findViewById(R.id.ews_step2_yes_btn)).setTypeface(Fonts.getGillsansLight(this));
		
		((Button) viewStep2.findViewById(R.id.ews_step2_no_btn)).setOnClickListener(this);
		((Button) viewStep2.findViewById(R.id.ews_step2_no_btn)).setTypeface(Fonts.getGillsansLight(this));

	}

	private void initializeStep3Variable() {

		viewStep3 = inflater.inflate(R.layout.ews_step3, null);

		passwordLabelStep3 = (CustomTextView) viewStep3.findViewById(R.id.ews_step3_password_lb);
		wifiNetworkAddStep3 = (CustomTextView) viewStep3.findViewById(R.id.ews_step3_wifi_add);

		passwordStep3 = (EditText) viewStep3.findViewById(R.id.ews_step3_password);
		passwordStep3.setTypeface(Fonts.getGillsansLight(this));
		deviceNameStep3 = (EditText) viewStep3.findViewById(R.id.ews_step3_place_name_edittxt); 
		deviceNameStep3.setTypeface(Fonts.getGillsansLight(this));
		ipAddStep3 = (EditText) viewStep3.findViewById(R.id.ews_step3_ip_edittxt); 
		ipAddStep3.setTypeface(Fonts.getGillsansLight(this));
		subnetMaskStep3 = (EditText) viewStep3.findViewById(R.id.ews_step3_subnet_edittxt); 
		subnetMaskStep3.setTypeface(Fonts.getGillsansLight(this));
		routerAddStep3 = (EditText) viewStep3.findViewById(R.id.ews_step3_router_edittxt);
		routerAddStep3.setTypeface(Fonts.getGillsansLight(this));

		showPasswordImgStep3 = (ImageView) viewStep3.findViewById(R.id.ews_password_enable_img);
		((ImageView) viewStep3.findViewById(R.id.ews_adv_config_img)).setOnClickListener(this);

		((Button) viewStep3.findViewById(R.id.ews_step3_next_btn)).setOnClickListener(this);
		((Button) viewStep3.findViewById(R.id.ews_step3_next_btn)).setTypeface(Fonts.getGillsansLight(this));
		editSavePlaceNameBtnStep3 = (Button) viewStep3.findViewById(R.id.ews_step3_edit_name_btn);
		editSavePlaceNameBtnStep3.setTypeface(Fonts.getGillsansLight(this));

		advSettingLayoutStep3 = (RelativeLayout) viewStep3.findViewById(R.id.ews_step3_adv_config_layout);
		advSettingBtnLayoutStep3 = (LinearLayout) viewStep3.findViewById(R.id.ews_adv_config_layout);

		showPasswordImgStep3.setOnClickListener(this);
		editSavePlaceNameBtnStep3.setOnClickListener(this);

	}

	/*Initialize action bar */
	private void initActionBar() {
		mActionBar = getSupportActionBar();
		mActionBar.setIcon(null);
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
		Drawable d=getResources().getDrawable(R.drawable.ews_nav_bar_2x);  
		mActionBar.setBackgroundDrawable(d);
		View view  = getLayoutInflater().inflate(R.layout.ews_actionbar, null);
		actionbarTitle = (CustomTextView) view.findViewById(R.id.ews_actionbar_title);
		actionbarTitle.setText(getString(R.string.wifi_setup));
		actionBarCancelBtn = (Button) view.findViewById(R.id.ews_actionbar_cancel_btn);
		actionBarCancelBtn.setTypeface(Fonts.getGillsansLight(this));
		actionBarCancelBtn.setOnClickListener(this);
		mActionBar.setCustomView(view);	
	}

	private void initializeCongraltVariable() {
		viewCongratulation = inflater.inflate(R.layout.ews_congratulation, null);

		((Button) viewCongratulation.findViewById(R.id.ews_congratulation_btn)).setOnClickListener(this);
		((Button) viewCongratulation.findViewById(R.id.ews_congratulation_btn)).setTypeface(Fonts.getGillsansLight(this));

	}

	private void initializeErrorPurifierNotDetect() {
		viewErrorPurifierNotDect = inflater.inflate(R.layout.ews_error_purifier_not_detect, null);

		errorPurifierNotDectNetwork = 
				(CustomTextView) viewErrorPurifierNotDect.findViewById(R.id.ews_purifier_not_dect_network);
		((Button) viewErrorPurifierNotDect.findViewById(R.id.ews_purifier_not_dect_btn)).setOnClickListener(this);
		((Button) viewErrorPurifierNotDect.findViewById(R.id.ews_purifier_not_dect_btn)).setTypeface(Fonts.getGillsansLight(this));

	}


	private void initializeContactPhilips() {
		viewContactPhilipsSupport = inflater.inflate(R.layout.contact_philips_support, null);

		contactPhilipsMessage1 = (CustomTextView) 
				viewContactPhilipsSupport.findViewById(R.id.contact_philips_support_message1); 
		contactPhilipsPhone = (CustomTextView) 
				viewContactPhilipsSupport.findViewById(R.id.contact_support_phone); 
		contactPhilipsEmail = (CustomTextView) 
				viewContactPhilipsSupport.findViewById(R.id.contact_support_email); 
		contactPhilipsWeb = (CustomTextView) 
				viewContactPhilipsSupport.findViewById(R.id.contact_support_website);

		((RelativeLayout) viewContactPhilipsSupport.findViewById
				(R.id.contact_support_phone_layout)).setOnClickListener(this);  
		((RelativeLayout) viewContactPhilipsSupport.findViewById
				(R.id.contact_support_email_layout)).setOnClickListener(this);  						
		((RelativeLayout)viewContactPhilipsSupport.findViewById
				(R.id.contact_support_website_layout)).setOnClickListener(this); 

	}

	private void initializeErrorSSID() {
		viewErrorSSID  = inflater.inflate(R.layout.ews_error_ssid, null) ;
		errorSSIDNetwork = (CustomTextView) viewErrorSSID.findViewById(R.id.ews_error_ssid_network); 
	}

	private void initializeErrorConnect2Network() {
		viewErrorConnect2Network = inflater.inflate(R.layout.ews_connect_2_your_network, null) ;
	}
	
	private void setActionBarHeading (int index) {
		switch (index) {
		case 1:
			actionBarCancelBtn.setVisibility(View.VISIBLE);
			actionbarTitle.setText(getString(R.string.wifi_setup));
			break;
		case 2:
			//congra
			actionBarCancelBtn.setVisibility(View.INVISIBLE);
			actionbarTitle.setText(getString(R.string.congratulations));
			break;
		case 3:
			//support
			actionBarCancelBtn.setVisibility(View.VISIBLE);
			actionbarTitle.setText(getString(R.string.support));
			break;
		case 4:
			//notconnect
			actionBarCancelBtn.setVisibility(View.INVISIBLE);
			actionbarTitle.setText(getString(R.string.error_purifier_not_detect_head));
			break;
		}
	}
	
	//Initialize - End
	
	//Activity Override methods - Start
	@Override
	protected void onStop() {
		if(ewsService != null)
			ewsService.unRegisterListener() ;
		if(ssdpService != null)
			ssdpService.stopDeviceDiscovery() ;
		super.onStop();
	}


	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		Log.i("ews", "onWindowFocused") ;
		super.onWindowFocusChanged(hasFocus);
		if ( hasFocus ) {
			registerNetworkListener() ;
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ews_get_start_btn:
			checkWifiConnectivity() ;
			break;
		case R.id.ews_step1_yes_btn:
			if( ewsService != null ) {
				ewsService.setSSID(networkSSID) ;
			}
			setActionBarHeading(1);
			setContentView(viewStep2);
			break;
		case R.id.ews_step1_no_btn:
			step = 0 ;
			if( ewsService == null ) {
				ewsService = new EWSService(this, this, null, null) ;
			}
			ewsService.setSSID(null) ;
			setActionBarHeading(1);
			setContentView(viewErrorConnect2Network) ;
			break;
		case R.id.ews_step2_yes_btn:
			connectToAirPurifier() ;
			break;
		case R.id.ews_step2_no_btn:
			//setContentView(viewStep1);
			// TODO - Show the dialog here
			showEWSSetUpInstructionsDialog() ;
			break;	
		case R.id.ews_password_enable_img:
			if (isPasswordVisibelStep3) {
				isPasswordVisibelStep3 = false;
				showPasswordImgStep3.setImageResource(R.drawable.ews_password_off_3x);
				passwordStep3.setTransformationMethod(new PasswordTransformationMethod());
				Editable editable = passwordStep3.getText();
				Selection.setSelection(editable, passwordStep3.length());
			} else {
				isPasswordVisibelStep3 = true;
				showPasswordImgStep3.setImageResource(R.drawable.ews_password_on_2x);
				passwordStep3.setTransformationMethod(null);
				Editable editable = passwordStep3.getText();
				Selection.setSelection(editable, passwordStep3.length());

			}
			break;
		case R.id.ews_adv_config_img:
			advSettingLayoutStep3.setVisibility(View.VISIBLE);
			advSettingBtnLayoutStep3.setVisibility(View.INVISIBLE);
			break;
		case R.id.ews_step3_edit_name_btn:

			if (editSavePlaceNameBtnStep3.getText().toString().equals(
					getResources().getString(R.string.edit))) {
				deviceNameStep3.setBackgroundResource(R.drawable.ews_edit_txt_2_bg);
				deviceNameStep3.setEnabled(true);
				deviceNameStep3.setTextColor(GraphConst.COLOR_TITLE_GRAY);
				Editable editable = deviceNameStep3.getText();
				Selection.setSelection(editable, deviceNameStep3.length());
				editSavePlaceNameBtnStep3.setText(getResources().getString(R.string.save));
			} else {
				deviceNameStep3.setBackgroundColor(Color.WHITE);
				deviceNameStep3.setEnabled(false);
				deviceNameStep3.setTextColor(GraphConst.COLOR_PHILIPS_BLUE);
				editSavePlaceNameBtnStep3.setText(getResources().getString(R.string.edit));
				if (deviceNameStep3.getText().toString() != null && deviceNameStep3.getText().toString().length() > 0) {
					sendDeviceNameToPurifier(deviceNameStep3.getText().toString()) ;
				} else {
					deviceNameStep3.setText(SessionDto.getInstance().getDeviceDto().getName());
				}
				
			}

			break;
		case R.id.ews_step3_next_btn:
			sendNetworkDetails() ;
			break;
		case R.id.ews_purifier_not_dect_btn:
			EWSDialogFactory.getInstance(this).getDialog(EWSDialogFactory.ERROR_TS01_05).show(); 
			break;
		case R.id.ews_congratulation_btn:
			showHomeScreen() ;
			break;
		case R.id.contact_support_phone_layout:
			//TODO : Uncomment SIM_STATE check, commented for testing.
//			TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
//			if(tm != null && tm.getSimState() == TelephonyManager.SIM_STATE_READY) {
				Intent dialSupportIntent = new Intent(Intent.ACTION_DIAL);
				dialSupportIntent.setData(Uri.parse("tel:" + getString(R.string.contact_philips_support_phone_num)));
				startActivity(Intent.createChooser(dialSupportIntent, "Air Purifier support"));
//			}
			break;
		case R.id.contact_support_email_layout:
			Intent supportEmailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","sangamesh.bn@philips.com", null));
			supportEmailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support");
			supportEmailIntent.putExtra(Intent.EXTRA_TEXT, "No template");
			startActivity(Intent.createChooser(supportEmailIntent, "Air Purifier support"));
			break;
		case R.id.contact_support_website_layout:
			Intent gotoSupportWebisteIntent = new Intent(Intent.ACTION_VIEW);
			gotoSupportWebisteIntent.setData(Uri.parse("http://" + getString(R.string.contact_philips_support_website)));
			startActivity(gotoSupportWebisteIntent);
			break;
		case R.id.ews_actionbar_cancel_btn:
			onCancel() ;
			break;
		default:
			break;
		}
	}
	@Override
	protected void onResume() {
		registerNetworkListener() ;
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	//Activity Override methods - End
	
	
	private void onCancel() {
		EWSDialogFactory.getInstance(this).getDialog(EWSDialogFactory.CANCEL_WIFI_SETUP).show() ;
	}
	
	//
	private void checkWifiConnectivity() {
		//wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		Log.i("wifi", "mWifi.isConnected()== " +mWifi.isConnected());
		if(!mWifi.isConnected()) {
			step = 0 ;
			setActionBarHeading(1);
			setContentView(viewErrorConnect2Network) ;
			ewsService = new EWSService(this, this, networkSSID, password) ;
		}
		else {
			showStepOne() ;
		}
	}

	private void showStepOne() {
		if( wifiManager.getConnectionInfo() != null ) {
			networkSSID = wifiManager.getConnectionInfo().getSSID() ;
			if ( networkSSID != null ) {
				Log.i("ews", networkSSID) ;
				networkSSID = networkSSID.replace("\"", "") ;
				if( ! networkSSID.contains(EWSService.DEVICE_SSID)) {
					if( step != 1 ) {
						step = 1 ;
						setActionBarHeading(1);
						setContentView(viewStep1) ;
					}					
					wifiNetworkNameStep1.setText(networkSSID) ;
				}
				else {
					networkSSID = null ;
					step = 0 ;
					setActionBarHeading(1);
					setContentView(viewErrorConnect2Network) ;
				}			
			}
		}
		else {
			networkSSID = null ;
			step = 0 ;
			setActionBarHeading(1);
			setContentView(viewErrorConnect2Network) ;
		}		
	}

	
	public void connectToAirPurifier() {
		progressDialogForStep2.show();
		if ( ewsService == null)
			ewsService = new EWSService(this, this, networkSSID, password) ;
		ewsService.setSSID(networkSSID) ;
		ewsService.connectToDeviceAP() ;
	}
	
	
	private void showEWSSetUpInstructionsDialog() {
		EWSDialogFactory.getInstance(this).getDialog(EWSDialogFactory.ERROR_TS01_01).show() ;
	}	

	private void registerNetworkListener() {
		if( step == 0 || step == 1) {
			if( ewsService == null ) {
				ewsService = new EWSService(this, this, null, null) ;
			}
			ewsService.setSSID(null) ;
			ewsService.registerListener() ;
		}
	}

	// This method will send the Device name to the AirPurifier when user selects Save
	private void sendDeviceNameToPurifier(String deviceName) {
		if( ! deviceName.equals("") && !deviceName.equals(SessionDto.getInstance().getDeviceDto().getName())) {
			ewsService.setDeviceName(deviceName) ;
			ewsService.putDeviceDetails() ;
		}

	}
	private void sendNetworkDetails() {
		EWSDialogFactory.getInstance(this).setNetworkName(networkSSID);
		EWSDialogFactory.getInstance(this).getDialog(EWSDialogFactory.CONNECTING_TO_PRODUCT).show() ;
		String enteredPassword = passwordStep3.getText().toString() ;
		ewsService.setSSID(networkSSID) ;
		ewsService.setPassword(enteredPassword) ;
		ewsService.putWifiDetails() ;
		
	}
	
	private void showHomeScreen() {
		Intent intent = new Intent(this,MainActivity.class) ;
		intent.putExtra("ipaddress", ipAddress) ;
		setResult(RESULT_OK,intent) ;
		finish() ;
	}

	// Override methods - EWSListener - Start
	@Override
	public void onDeviceAPMode() {

	}


	@Override
	public void onSelectHomeNetwork() {

	}


	@Override
	public void onHandShakeWithDevice() {
		if( progressDialogForStep2.isShowing() ) {
			progressDialogForStep2.dismiss() ;
		}
		step = 3 ;
		setActionBarHeading(1);
		setContentView(viewStep3);
		
		if (EWSService.isNoPasswordSSID()) {
			passwordStep3.setEnabled(false);
			passwordStep3.setBackgroundResource(R.drawable.ews_edit_txt_2_bg_gray);
		} else {
			passwordStep3.setEnabled(true);
			passwordStep3.setBackgroundResource(R.drawable.ews_edit_txt_2_bg);
		}
		
		String passwordLabel = getString(R.string.step3_msg1) + " <font color=#3285FF>"+networkSSID+"</font>";
		passwordLabelStep3.setText(Html.fromHtml(passwordLabel));
		deviceNameStep3.setText(SessionDto.getInstance().getDeviceDto().getName()) ;
		ipAddStep3.setText(SessionDto.getInstance().getDeviceWifiDto().getIpaddress()) ;
		subnetMaskStep3.setText(SessionDto.getInstance().getDeviceWifiDto().getNetmask()) ; 
		routerAddStep3.setText(SessionDto.getInstance().getDeviceWifiDto().getGateway()) ;
		wifiNetworkAddStep3.setText(SessionDto.getInstance().getDeviceWifiDto().getMacaddress()) ;
	}


	@Override
	public void onDeviceConnectToHomeNetwork() {
		Toast.makeText(this, "Start Device Discovery", Toast.LENGTH_LONG).show() ;
		ssdpService = SsdpService.getInstance() ;
		ssdpService.startDeviceDiscovery(this) ;
	}
	
	@Override
	public void foundHomeNetwork() {
		showStepOne() ;
	}


	@Override
	public void onErrorOccurred(int errorCode) {
		//Toast.makeText(this, "Error Code: "+errorCode, Toast.LENGTH_LONG).show() ;
		if( progressDialogForStep2.isShowing())
			progressDialogForStep2.dismiss() ;
		else if( EWSDialogFactory.getInstance(this).getDialog(EWSDialogFactory.CONNECTING_TO_PRODUCT).isShowing())
			EWSDialogFactory.getInstance(this).getDialog(EWSDialogFactory.CONNECTING_TO_PRODUCT).dismiss() ;
		switch (errorCode) {
		case EWSListener.ERROR_CODE_COULDNOT_RECEIVE_DATA_FROM_DEVICE:
			EWSDialogFactory.getInstance(this).getDialog(EWSDialogFactory.ERROR_TS01_03).show() ;
			break;
		case EWSListener.ERROR_CODE_PHILIPS_SETUP_NOT_FOUND:				
			EWSDialogFactory.getInstance(this).getDialog(EWSDialogFactory.ERROR_TS01_01).show() ;
			break;
		case EWSListener.ERROR_CODE_COULDNOT_SEND_DATA_TO_DEVICE:
			EWSDialogFactory.getInstance(this).getDialog(EWSDialogFactory.ERROR_TS01_04).show() ;
			break;
		case EWSListener.ERROR_CODE_COULDNOT_FIND_DEVICE:				
			showErrorScreen();
			break;
		}
	}
	
	@Override
	public void onWifiDisabled() {
		switch (step) {
		case 1:
			//checkWifiConnectivity() ;
			break;
		}		
	}
	
	// Override methods - EWSListener - End
	
	private void showErrorScreen() {
        //wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled()) {
        	setActionBarHeading(4);
            setContentView(viewErrorSSID) ;
            errorSSIDNetwork.setText(networkSSID);
        }
        else {
             if( wifiManager.getConnectionInfo() != null &&
                       wifiManager.getConnectionInfo().getSSID().equals(networkSSID)) {
            	 setActionBarHeading(4);
                 setContentView(viewErrorPurifierNotDect) ;
                 errorPurifierNotDectNetwork.setText(networkSSID);
             }
             else {
            	 setActionBarHeading(4);
                 setContentView(viewErrorSSID) ;
                 errorSSIDNetwork.setText(wifiManager.getConnectionInfo().getSSID());
             }
        }
        
	}

	// SSDPListener Callback handler
	public boolean handleMessage(Message msg) {
		DeviceModel device = null ;
		if (null != msg) {
			final MessageID message = MessageID.getID(msg.what);
			final InternalMessage internalMessage = (InternalMessage) msg.obj;
			if (null != internalMessage && internalMessage.obj instanceof DeviceModel) {
				device = (DeviceModel) internalMessage.obj;
				//	Log.i(TAG, "Device Information " + device);

			}
			String ip = "";
			switch (message) {
			case DEVICE_DISCOVERED:
				final String xml = msg.getData().getString(ConnectionLibContants.XML_KEY);
				//ip = msg.getData().getString(ConnectionLibContants.IP_KEY);
				final int port = msg.getData().getInt(ConnectionLibContants.PORT_KEY);


				if (device != null && device.getSsdpDevice() != null &&
						device.getSsdpDevice().getModelName().contains(AppConstants.MODEL_NAME) &&
						device.getIpAddress() != null && ipAddress == null) {
					ipAddress = device.getIpAddress() ;
					com.philips.cl.di.dev.pa.utils.Utils.setIPAddress(device.getIpAddress(), this) ;
					deviceDiscoveryCompleted();
					//diSecurity.exchangeKey(String.format(AppConstants.URL_SECURITY, device.getIpAddress()), "dev01");
				}
				break;
			case DEVICE_LOST:
				ip = msg.getData().getString("ip");
				//Log.i(TAG, "DEVICE LOST USN  " + device.getUsn());
				if (device != null && device.getIpAddress() != null) {
					//deviceList.remove(device.getIpAddress());
				}
				ip = null;
				break;

			default:
				//Log.i(TAG, "default");
				break;
			}
			if (null != ip && (!ip.isEmpty())) {
				//deviceList.add(ip);
			} 

			return false;
		}
		return false;
	}
	
	private void deviceDiscoveryCompleted() {
		Toast.makeText(this, "Device discovered ", Toast.LENGTH_LONG).show() ;
		EWSDialogFactory.getInstance(this).getDialog(EWSDialogFactory.CONNECTING_TO_PRODUCT).dismiss() ;
		if( ewsService != null ) {
			ewsService.stopNetworkDetailsTimer() ;
		}
		setActionBarHeading(2);
		setContentView(viewCongratulation) ;
	}

	// Public methods called from DialogFactory class
	public void showSupportScreen() {
		step = -1 ;
		Toast.makeText(getApplicationContext(), "Please enable home network..!", Toast.LENGTH_LONG).show();
		setActionBarHeading(3);
		setContentView(viewContactPhilipsSupport) ;
	}
	
	public void airPurifierInSetupMode() {
		
        //wifiManager.disconnect();
		
		if ( step == 2 ) {
			connectToAirPurifier() ;			
		}
		else {
			passwordStep3.setText("") ;
			step = 2 ;
			setActionBarHeading(1);
			setContentView(viewStep2) ;
		}
		/*if( step == 2 ) {
			connectToAirPurifier() ;
		} */
		
 	}
}
