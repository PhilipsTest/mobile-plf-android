package com.philips.cl.di.dev.pa.ews;

import java.net.HttpURLConnection;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;

import com.philips.cl.di.dev.pa.PurAirApplication;
import com.philips.cl.di.dev.pa.constant.AppConstants;
import com.philips.cl.di.dev.pa.constant.AppConstants.Port;
import com.philips.cl.di.dev.pa.datamodel.DevicePortProperties;
import com.philips.cl.di.dev.pa.datamodel.DeviceWifiDto;
import com.philips.cl.di.dev.pa.datamodel.SessionDto;
import com.philips.cl.di.dev.pa.newpurifier.AirPurifier;
import com.philips.cl.di.dev.pa.newpurifier.ConnectionState;
import com.philips.cl.di.dev.pa.security.DISecurity;
import com.philips.cl.di.dev.pa.security.KeyDecryptListener;
import com.philips.cl.di.dev.pa.util.ALog;
import com.philips.cl.di.dev.pa.util.DataParser;
import com.philips.cl.di.dev.pa.util.JSONBuilder;
import com.philips.cl.di.dev.pa.util.MetricsTracker;
import com.philips.cl.di.dev.pa.util.Utils;
import com.philips.cl.di.dicomm.communication.CommunicationMarshal;


public class EWSBroadcastReceiver extends BroadcastReceiver
		implements KeyDecryptListener, EWSTaskListener, Runnable {

	private EWSListener listener ;
	private AirPurifier tempEWSPurifier;
	private IntentFilter filter = new IntentFilter();
	private EWSTasks task ;

	public static final int DEVICE_GET = 1;
	public static final int DEVICE_PUT = 2;
	public static final int WIFI_PUT = 3;
	public static final int WIFI_GET = 4;
	private int errorCodeStep2 = EWSListener.ERROR_CODE_PHILIPS_SETUP_NOT_FOUND  ;
	private int errorCodeStep3 = EWSListener.ERROR_CODE_COULDNOT_SEND_DATA_TO_DEVICE ;
	private int taskType ;
	private int totalTime = 10 * 1000 ;

	private String password;
	private String devKey;
	private String homeSSID ;

	private boolean stop = true;
	private boolean isRegistered ;
	private boolean startSSDPCountDownTimer;
	private boolean startDeviceSSIDTimer;
	private boolean isOpenNetwork;

	/**
	 *
	 * @param listener
	 * @param context
	 * @param homeSSID
	 * @param password
	 */
	public EWSBroadcastReceiver(EWSListener listener, String homeSSID) {
		this.listener = listener ;
		this.homeSSID = homeSSID ;
		generateTempEWSDevice();
	}

	public void registerListener() {
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		if( !isRegistered ) {
			PurAirApplication.getAppContext().registerReceiver(this, filter);
			isRegistered = true ;
		}
	}

	public void startScanForDeviceAp() {
		registerListener() ;

		if(stop) {
			stop = false ;
			new Thread(this).start() ;
		}
	}

	public void unRegisterListener() {
		if( isRegistered ) {
			stop = true ;
			PurAirApplication.getAppContext().unregisterReceiver(this) ;
			isRegistered = false ;
		}
	}

	public boolean isNoPasswordSSID() {
		return isOpenNetwork;
	}

	public void setPassword(String password) {
		this.password = password ;
	}

	public void setSSID(String ssid) {
		this.homeSSID = ssid ;
	}

	public void setDeviceName(String deviceName) {
		tempEWSPurifier.getNetworkNode().setName(deviceName);
	}

	public void initializeKey() {
		ALog.i(ALog.EWS, "initiliazekey") ;
		DISecurity di = new DISecurity(this) ;
		di.initializeExchangeKeyCounter(tempEWSPurifier.getNetworkNode().getCppId());
		di.exchangeKey(Utils.getPortUrl(Port.SECURITY, EWSConstant.PURIFIER_ADHOCIP), tempEWSPurifier.getNetworkNode().getCppId()) ;
	}

	public void putWifiDetails(String ipAdd, String subnetMask, String gateWay) {
		ALog.i(ALog.EWS, "putWifiDetails");
		startSSDPCountDownTimer();

		taskType = WIFI_PUT ;

		task = new EWSTasks(taskType,getWifiPortJson(ipAdd, subnetMask, gateWay),"PUT",this) ;
		task.execute(Utils.getPortUrl(Port.WIFI, EWSConstant.PURIFIER_ADHOCIP));
	}

	public void putDeviceDetails() {
		ALog.i(ALog.EWS, "putDeviceDetails");
		taskType = DEVICE_PUT ;

		task = new EWSTasks(taskType,getDevicePortJson(),"PUT",this) ;
		task.execute(Utils.getPortUrl(Port.DEVICE, EWSConstant.PURIFIER_ADHOCIP));
	}

	public void connectToDeviceAP() {
		ALog.i(ALog.EWS, "connecttoDevice AP");
		WifiManager wifiManager =
				(WifiManager) PurAirApplication.getAppContext().getSystemService(Context.WIFI_SERVICE);
		wifiManager.disconnect();

		new Thread(new Runnable() {

			@Override
			public void run() {
				EWSWifiManager.connectToPhilipsSetup();
			}
		}).start();

		startScanForDeviceAp() ;
		deviceSSIDTimer.start() ;
		startDeviceSSIDTimer = true;
	}

	public void stopSSDPCountDownTimer() {
		startSSDPCountDownTimer = false;
	}

	public void stopSSIDTimer() {
		if(deviceSSIDTimer != null ) {
			deviceSSIDTimer.cancel() ;
			startDeviceSSIDTimer = false;
		}
	}

	public void setDevKey(String devKey) {
		this.devKey = devKey;
	}

	public String getDevKey() {
		return devKey;
	}

	private synchronized void startSSDPCountDownTimer() {
		Thread thread = new Thread(new SSDPCountDownTimer());
		thread.start();
	}

	private void getDeviceDetails() {
		ALog.i(ALog.EWS,"device details") ;
		taskType = DEVICE_GET ;
		task = new EWSTasks(taskType, this) ;
		task.execute(Utils.getPortUrl(Port.DEVICE, EWSConstant.PURIFIER_ADHOCIP));
	}

	private String getWifiPortJson(String ipAdd, String subnetMask, String gateWay) {
		ALog.i(ALog.EWS, "getWifiPortJson");
		String encryptedData = "";
		if (ipAdd.equals(SessionDto.getInstance().getDeviceWifiDto().getIpaddress())
				&& subnetMask.equals(SessionDto.getInstance().getDeviceWifiDto().getNetmask())
				&& gateWay.equals(SessionDto.getInstance().getDeviceWifiDto().getGateway())) {
			encryptedData = JSONBuilder.getWifiPortJson(homeSSID, password, tempEWSPurifier.getNetworkNode());
			MetricsTracker.trackActionAdvanceNetworkConfig(false);
		} else {
			if (ipAdd.isEmpty()) ipAdd = SessionDto.getInstance().getDeviceWifiDto().getIpaddress();
			if (subnetMask.isEmpty()) subnetMask = SessionDto.getInstance().getDeviceWifiDto().getNetmask();
			if (gateWay.isEmpty()) gateWay = SessionDto.getInstance().getDeviceWifiDto().getGateway();
			encryptedData = JSONBuilder.getWifiPortWithAdvConfigJson(homeSSID, password, ipAdd, subnetMask,
			gateWay, tempEWSPurifier.getNetworkNode());
			MetricsTracker.trackActionAdvanceNetworkConfig(true);
		}
		return encryptedData ;
	}

	private String getDevicePortJson() {
		ALog.i(ALog.EWS, "getDevicePortJson");
		JSONObject holder = new JSONObject();
		try {
			holder.put("name", tempEWSPurifier.getNetworkNode().getName());
		} catch (JSONException e) {
			ALog.e(ALog.EWS, "Error: " + e.getMessage());
		}

		String js = holder.toString();
		String encryptedData = new DISecurity(null).encryptData(js, tempEWSPurifier.getNetworkNode());

		return encryptedData ;
	}

	private void getWifiDetails() {
		ALog.i(ALog.EWS, "gettWifiDetails");
		taskType = WIFI_GET ;

		task = new EWSTasks(taskType,this) ;
		task.execute(Utils.getPortUrl(Port.WIFI, EWSConstant.PURIFIER_ADHOCIP));
	}

	private void cancelEWSTasks() {
		if( task != null && !task.isCancelled()) {
			task.cancel(true) ;
		}
	}

	private void generateTempEWSDevice() {
		String tempEui64 = UUID.randomUUID().toString();
		tempEWSPurifier = new AirPurifier(new CommunicationMarshal(new DISecurity(null)), tempEui64, null,
				EWSConstant.PURIFIER_ADHOCIP, null, -1,	ConnectionState.CONNECTED_LOCALLY);
	}

	private void updateTempDevice(String eui64) {
		String encryptionKey = tempEWSPurifier.getNetworkNode().getEncryptionKey();
		String purifierName = tempEWSPurifier.getNetworkNode().getName();
		tempEWSPurifier = new AirPurifier(new CommunicationMarshal(new DISecurity(null)), eui64, null,
				EWSConstant.PURIFIER_ADHOCIP, purifierName, -1,	ConnectionState.CONNECTED_LOCALLY);
		tempEWSPurifier.getNetworkNode().setEncryptionKey(encryptionKey);
	}

	@Override
	public void keyDecrypt(String key, String deviceId) {
		ALog.i(ALog.EWS, "Key: "+key) ;
		tempEWSPurifier.getNetworkNode().setEncryptionKey(key);

		if ( key != null ) {
			setDevKey(key);
			getDeviceDetails() ;
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {

			NetworkInfo netInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			WifiManager wifiMan = (WifiManager) PurAirApplication.getAppContext()
					.getSystemService(Context.WIFI_SERVICE);

			WifiInfo connectedWifiNetwork = wifiMan.getConnectionInfo();

			if (connectedWifiNetwork.getSupplicantState() == SupplicantState.COMPLETED) {

				String ssid = EWSWifiManager.getSsidOfSupplicantNetwork();
				if (ssid == null) {
					ALog.i(ALog.EWS, "Failed to get ssid of connected network");
					return;
				}

				ALog.i(ALog.EWS, "Connected to AirPurifier - Ssid= " + ssid
						+ "; startDeviceSSIDTimer: " + startDeviceSSIDTimer + "; homeSSID:" + homeSSID);

				if (ssid.contains(EWSWifiManager.DEVICE_SSID)
						&& startDeviceSSIDTimer && homeSSID != null) {
					ALog.i(ALog.EWS,"Connected to PHILIPS Setup");
					errorCodeStep2 = EWSListener.ERROR_CODE_COULDNOT_RECEIVE_DATA_FROM_DEVICE ;
					listener.onDeviceAPMode() ;
					initializeKey() ;
					isOpenNetwork = EWSWifiManager.isOpenNetwork(homeSSID);
					return;
				}

				if (homeSSID != null && ssid.contains(homeSSID)) {
					ALog.i(ALog.EWS,"Selected Home network");
					errorCodeStep3 = EWSListener.ERROR_CODE_COULDNOT_FIND_DEVICE ;
					listener.onSelectHomeNetwork() ;
					return;
				}

				if (homeSSID == null ) {
					listener.foundHomeNetwork() ;
				}

			} else if (netInfo.getState() == NetworkInfo.State.DISCONNECTED ||
					netInfo.getState() == NetworkInfo.State.DISCONNECTING) {
				ALog.i(ALog.EWS, "Network State: "+ netInfo.getState()) ;
				listener.onWifiDisabled() ;
			}
		}

	}

	@Override
	public void onTaskCompleted(int responseCode, String response) {

		stop = true ;
		ALog.i(ALog.EWS, "onTaskCompleted:"+responseCode +", response: " + response) ;
		switch (responseCode) {
		case HttpURLConnection.HTTP_OK:
			if( taskType == DEVICE_GET ) {
				String decryptedResponse = new DISecurity(null).decryptData(response, tempEWSPurifier.getNetworkNode());
				if( decryptedResponse != null ) {
					ALog.i(ALog.EWS,decryptedResponse) ;
					DevicePortProperties deviceDto = DataParser.getDeviceDetails(decryptedResponse) ;

					SessionDto.getInstance().setDeviceDto(deviceDto) ;
					if (deviceDto == null) return;
					tempEWSPurifier.getNetworkNode().setName(deviceDto.getName());
					getWifiDetails() ;
				}
			}
			else if(taskType == WIFI_GET) {
				String decryptedResponse = new DISecurity(null).decryptData(response, tempEWSPurifier.getNetworkNode());
				if( decryptedResponse != null ) {
					ALog.i(ALog.EWS,decryptedResponse) ;
					DeviceWifiDto deviceWifiDto = DataParser.getDeviceWifiDetails(decryptedResponse);

					SessionDto.getInstance().setDeviceWifiDto(deviceWifiDto) ;

					if (deviceWifiDto != null) {
						this.updateTempDevice(deviceWifiDto.getCppid());
					}

					stopSSIDTimer();
					listener.onHandShakeWithDevice() ;
				}
			}
			else if(taskType == DEVICE_PUT ) {
				String decryptedResponse = new DISecurity(null).decryptData(response, tempEWSPurifier.getNetworkNode());
				ALog.i(ALog.EWS, decryptedResponse) ;
				if( decryptedResponse != null ) {
					DevicePortProperties deviceDto = DataParser.getDeviceDetails(decryptedResponse) ;
					SessionDto.getInstance().setDeviceDto(deviceDto) ;
					//listener.onHandShakeWithDevice() ;
				}
			}
			else if(taskType == WIFI_PUT ) {
				String decryptedResponse = new DISecurity(null).decryptData(response, tempEWSPurifier.getNetworkNode());
				ALog.i(ALog.EWS, "taskType == WIFI_PUT: "+decryptedResponse) ;
				if( decryptedResponse != null ) {
					EWSWifiManager.connectToHomeNetwork(homeSSID);
					listener.onDeviceConnectToHomeNetwork() ;
					errorCodeStep3 = EWSListener.ERROR_CODE_COULDNOT_CONNECT_HOME_NETWORK ;
				}
			}
			break;
		case HttpURLConnection.HTTP_BAD_REQUEST:
			if(taskType == WIFI_PUT ) {
				stop = true ;
				stopSSDPCountDownTimer() ;
				if( response != null && response.length() > 0 && response.contains(AppConstants.INVALID_WIFI_SETTINGS)) {
					listener.onErrorOccurred(EWSListener.ERROR_CODE_INVALID_PASSWORD) ;
				} else {
					listener.onErrorOccurred(EWSListener.ERROR_CODE_COULDNOT_SEND_DATA_TO_DEVICE) ;
				}
			}
			break;
		case HttpURLConnection.HTTP_BAD_GATEWAY:
			ALog.i(ALog.EWS, "Connect purifier to home network request send succesfully," +
					" but conn.getResponseCode() failed. Task type: " + taskType);
			if (taskType == WIFI_PUT) {
				EWSWifiManager.connectToHomeNetwork(homeSSID);
				listener.onDeviceConnectToHomeNetwork() ;
				errorCodeStep3 = EWSListener.ERROR_CODE_COULDNOT_CONNECT_HOME_NETWORK ;
				break;
			}
		default:
			stop = true ;
			stopSSDPCountDownTimer() ;
			stopSSIDTimer() ;

			if( taskType == WIFI_GET || taskType == DEVICE_GET) {
				listener.onErrorOccurred(EWSListener.ERROR_CODE_COULDNOT_RECEIVE_DATA_FROM_DEVICE) ;
			}
			else if( taskType == WIFI_PUT || taskType == DEVICE_PUT) {
				listener.onErrorOccurred(EWSListener.ERROR_CODE_COULDNOT_SEND_DATA_TO_DEVICE) ;
			}
			break;
		}
	}

	@Override
	public void run() {
		int timeElapsed = 0 ;
		while(!stop) {
			try {
				Thread.sleep(1000) ;
				timeElapsed = timeElapsed + 1000 ;
				if( timeElapsed == totalTime) {
					timeElapsed = 0 ;
					// StartScan
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private CountDownTimer deviceSSIDTimer = new CountDownTimer(60000, 1000) {
		@Override
		public void onTick(long millisUntilFinished) {
		}

		@Override
		public void onFinish() {
			stop = true ;
			unRegisterListener() ;
			cancelEWSTasks() ;
			listener.onErrorOccurred(errorCodeStep2) ;
		}
	};

	private class SSDPCountDownTimer implements Runnable {
		private static final int TOTAL_TIME_COUNT = 90;
		private static final int HOME_NETWORK_TIME_COUNT = 30;
		private static final int TIME_INTERVAL = 1000;
		private int timeCount;

		public SSDPCountDownTimer() {
			timeCount = 0;
			startSSDPCountDownTimer = true;
		}

		@Override
		public void run() {

			while (startSSDPCountDownTimer) {
				ALog.i(ALog.EWS, "New timer count down: ......." + timeCount);
				if (timeCount == HOME_NETWORK_TIME_COUNT) {
					ALog.i(ALog.EWS, "ssdpCountDownTimer after 30Sec");
					String currentNetworkSSID = EWSWifiManager.getSsidOfSupplicantNetwork();
					if (currentNetworkSSID == null || !currentNetworkSSID.equals(homeSSID)) {
						errorCodeStep3 = EWSListener.ERROR_CODE_COULDNOT_CONNECT_HOME_NETWORK ;
						onTimeCountDownTaskCompleted();
					}
				} else if (timeCount == TOTAL_TIME_COUNT) {
					ALog.i(ALog.EWS, "ssdpCountDownTimer after 90Sec");
					errorCodeStep3 = EWSListener.ERROR_CODE_COULDNOT_FIND_DEVICE ;
					onTimeCountDownTaskCompleted();
				}
				try {
					Thread.sleep(TIME_INTERVAL);
				} catch (InterruptedException e) {
					ALog.e(ALog.EWS, "EWSBroadcastReceiver$ssdpCountDownTimer: " + "Error: " + e.getMessage());
				}
				timeCount ++;
			}
		}

		private void onTimeCountDownTaskCompleted() {
			ALog.i(ALog.EWS, "ssdpCountDownTimer$onTimeCountDownTaskCompleted");
			cancelEWSTasks() ;
			timeCount = 0;
			stop = true;
			if (startSSDPCountDownTimer) {
				ssdpHandler.sendEmptyMessage(1);
				startSSDPCountDownTimer = false;
			}
			unRegisterListener() ;
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler ssdpHandler = new Handler(){
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				ALog.i(ALog.EWS, "Handler handle msg 1");
				listener.onErrorOccurred(errorCodeStep3) ;
			}
		};
	};
}
