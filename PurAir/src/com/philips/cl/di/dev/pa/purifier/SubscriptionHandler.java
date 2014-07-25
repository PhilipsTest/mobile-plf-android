package com.philips.cl.di.dev.pa.purifier;

import java.net.HttpURLConnection;

import android.content.Context;

import com.philips.cl.di.dev.pa.PurAirApplication;
import com.philips.cl.di.dev.pa.constant.AppConstants;
import com.philips.cl.di.dev.pa.constant.AppConstants.Port;
import com.philips.cl.di.dev.pa.cpp.CPPController;
import com.philips.cl.di.dev.pa.cpp.CppDiscoverEventListener;
import com.philips.cl.di.dev.pa.cpp.DCSEventListener;
import com.philips.cl.di.dev.pa.datamodel.SessionDto;
import com.philips.cl.di.dev.pa.newpurifier.ConnectionState;
import com.philips.cl.di.dev.pa.newpurifier.PurAirDevice;
import com.philips.cl.di.dev.pa.newpurifier.PurifierManager;
import com.philips.cl.di.dev.pa.security.Util;
import com.philips.cl.di.dev.pa.util.ALog;
import com.philips.cl.di.dev.pa.util.DataParser;
import com.philips.cl.di.dev.pa.util.JSONBuilder;
import com.philips.cl.di.dev.pa.util.ServerResponseListener;
import com.philips.cl.di.dev.pa.util.Utils;

public class SubscriptionHandler implements UDPEventListener, DCSEventListener, ServerResponseListener {

	private static final int LOCAL_SUBSCRIPTIONTIME = 300; // IN SEC
	private static final int CPP_SUBSCRIPTIONTIME = 5; // IN MIN

	private static SubscriptionHandler mInstance ;
	private SubscriptionEventListener subscriptionEventListener;
	private CppDiscoverEventListener cppDiscoverEventListener;
	private UDPReceivingThread udpReceivingThread ;


	private SubscriptionHandler() {
		// enforce singleton
		udpReceivingThread = new UDPReceivingThread(this);
		CPPController.getInstance(PurAirApplication.getAppContext()).setDCSEventListener(this);
	}

	public static SubscriptionHandler getInstance() {
		if( null == mInstance ) {
			mInstance = new SubscriptionHandler() ;
		}
		return mInstance ;
	}

	public void setSubscriptionListener(SubscriptionEventListener subscriptionEventListener) {
		this.subscriptionEventListener = subscriptionEventListener ;
	}

	public void setCppDiscoverListener(CppDiscoverEventListener discoverListener) {
		this.cppDiscoverEventListener = discoverListener ;
	}

	public void subscribeToPurifierEvents() {
		PurAirDevice purifier = PurifierManager.getInstance().getCurrentPurifier();
		ALog.d(ALog.SUBSCRIPTION, "Subscribing to Purifier events for purifier: " + purifier);
		if (purifier == null) return;

//		if (PurAirApplication.isDemoModeEnable()) purifier.setConnectionState(ConnectionState.CONNECTED_LOCALLY);
		
		final String portUrl = Utils.getPortUrl(Port.AIR, purifier.getIpAddress());
		subscribe(portUrl, purifier);
	}

	public void unSubscribeFromPurifierEvents() {
		PurAirDevice purifier = PurifierManager.getInstance().getCurrentPurifier();
		ALog.d(ALog.SUBSCRIPTION, "Unsubscribing to Purifier events for purifier: " + purifier);
		if (purifier == null) return;
		String portUrl = Utils.getPortUrl(Port.AIR, purifier.getIpAddress());
		unSubscribe(portUrl, purifier);
	}

	public void subscribeToFirmwareEvents() {
		PurAirDevice purifier = PurifierManager.getInstance().getCurrentPurifier();
		if (purifier == null || PurAirApplication.isDemoModeEnable()) return;
		
		boolean isLocalSubscription = purifier.getConnectionState().equals(ConnectionState.CONNECTED_LOCALLY);
		if (isLocalSubscription) {
			ALog.d(ALog.SUBSCRIPTION, "Subscribing to Firmware events for purifier: " + purifier);
			final String portUrl = Utils.getPortUrl(Port.FIRMWARE, purifier.getIpAddress());
			subscribe(portUrl, purifier);
		}
	}

	public void unSubscribeFromFirmwareEvents() {
		PurAirDevice purifier = PurifierManager.getInstance().getCurrentPurifier();
		if (purifier == null) return;
		ALog.d(ALog.SUBSCRIPTION, "Unsubscribing from Firmware events appEui64: " + purifier);
		String portUrl = Utils.getPortUrl(Port.FIRMWARE, purifier.getIpAddress());
		unSubscribe(portUrl, purifier);
	}

	public void enableLocalSubscription() {
		ALog.i(ALog.SUBSCRIPTION, "Enabling local subscription (start udp)") ;
		if( udpReceivingThread == null ) {
			udpReceivingThread = new UDPReceivingThread(this) ;
		}
		if( udpReceivingThread != null && !udpReceivingThread.isAlive() ) {
			udpReceivingThread.start() ;
		}
	}

	public void disableLocalSubscription() {
		ALog.i(ALog.SUBSCRIPTION, "Disabling local subscription (stop udp)") ;
		if(udpReceivingThread != null && udpReceivingThread.isAlive() ) {
			udpReceivingThread.stopUDPListener() ;
			udpReceivingThread = null ;
		}
	}

	public void enableRemoteSubscription(Context context) {
		ALog.i(ALog.SUBSCRIPTION, "Enabling remote subscription (start dcs)") ;
		CPPController.getInstance(context).startDCSService();
	}

	public void disableRemoteSubscription(Context context) {
		ALog.i(ALog.SUBSCRIPTION, "Disabling remote subscription (stop dcs)") ;
		CPPController.getInstance(context).stopDCSService();
	}

	private void subscribe(String url, PurAirDevice purifier) {
		
		boolean isLocal = purifier.getConnectionState().equals(ConnectionState.CONNECTED_LOCALLY);
		String subscriberId = getSubscriberId(isLocal);
		ALog.d(ALog.SUBSCRIPTION, "SubscriptionManager$subscribe bootId " + purifier.getBootId() + " URL " + url + " isLocal " + isLocal);
		if(isLocal) {
			String dataToUpload = JSONBuilder.getDICommBuilderForSubscribe(subscriberId, LOCAL_SUBSCRIPTIONTIME, purifier);
			if(dataToUpload == null) return;

			LocalSubscription subscribe = new LocalSubscription(dataToUpload,this,AppConstants.REQUEST_METHOD_POST) ;
			subscribe.execute(url) ;
		}
		else {
			if (PurAirApplication.isDemoModeEnable()) return;
			CPPController.getInstance(PurAirApplication.getAppContext()).
			publishEvent(JSONBuilder.getPublishEventBuilderForSubscribe(AppConstants.EVENTSUBSCRIBER_KEY, subscriberId), 
					AppConstants.DI_COMM_REQUEST, AppConstants.SUBSCRIBE, subscriberId,"", 20,CPP_SUBSCRIPTIONTIME, purifier.getEui64()) ;

			CPPController.getInstance(PurAirApplication.getAppContext()).
			publishEvent(JSONBuilder.getPublishEventBuilderForSubscribeFirmware(AppConstants.EVENTSUBSCRIBER_KEY, subscriberId), 
					AppConstants.DI_COMM_REQUEST, AppConstants.SUBSCRIBE, subscriberId,"", 20,CPP_SUBSCRIPTIONTIME, purifier.getEui64()) ;
		}
		
	}

	private void unSubscribe(String url, PurAirDevice purifier) {
		boolean isLocal = purifier.getConnectionState().equals(ConnectionState.CONNECTED_LOCALLY);
		String subscriberId = getSubscriberId(isLocal);
		if (isLocal) {
			//LocalSubscription unSubscribe = new LocalSubscription(null,this,AppConstants.REQUEST_METHOD_DELETE) ;
			//unSubscribe.execute(url) ;
			ALog.i(ALog.SUBSCRIPTION, "TODO Unsubscribe") ;
		}
		else {
			CPPController.getInstance(PurAirApplication.getAppContext()).
			publishEvent(JSONBuilder.getPublishEventBuilderForSubscribe(AppConstants.EVENTSUBSCRIBER_KEY, subscriberId),
					AppConstants.DI_COMM_REQUEST, AppConstants.UNSUBSCRIBE, subscriberId,"", 20, CPP_SUBSCRIPTIONTIME, purifier.getEui64()) ;

			CPPController.getInstance(PurAirApplication.getAppContext()).
			publishEvent(JSONBuilder.getPublishEventBuilderForSubscribeFirmware(AppConstants.EVENTSUBSCRIBER_KEY, subscriberId),
					AppConstants.DI_COMM_REQUEST, AppConstants.UNSUBSCRIBE, subscriberId,"", 20, CPP_SUBSCRIPTIONTIME, purifier.getEui64()) ;
		}
	}

	private String getSubscriberId(boolean isLocal) {
		String appEui64 = SessionDto.getInstance().getAppEui64();
		if (appEui64 != null) return appEui64;
		if (isLocal) return Util.getBootStrapID(); // Fallback for local subscription when no cpp connection
		return null;
	}

	@Override
	public void onUDPEventReceived(String data, String fromIp) {
		if (data == null || data.isEmpty()) return;
		if (fromIp == null || fromIp.isEmpty()) return;

		ALog.i(ALog.SUBSCRIPTION, "UDP event received: "+fromIp);
		ALog.d(ALog.SUBSCRIPTION, data);
		if (subscriptionEventListener != null) {
			subscriptionEventListener.onLocalEventReceived(data, fromIp);
		}
	}

	@Override
	public void onDCSEventReceived(String data, String fromEui64, String action) {
		if (data == null || data.isEmpty()) return;

		if (DataParser.parseDiscoverInfo(data) != null) {
			ALog.i(ALog.SUBSCRIPTION, "Discovery event received - " + action) ;
			boolean isResponseToRequest = false;
			if (action != null && action.toUpperCase().trim().equals(AppConstants.DISCOVER)) {
				isResponseToRequest = true;
			}
			if (cppDiscoverEventListener != null) {
				cppDiscoverEventListener.onDiscoverEventReceived(data, isResponseToRequest);
			}
			return;
		}

		if (fromEui64 == null || fromEui64.isEmpty()) return;
		ALog.i(ALog.SUBSCRIPTION, "DCS event received from " + fromEui64) ;
		ALog.i(ALog.SUBSCRIPTION, data) ;
		if (subscriptionEventListener != null) {
			subscriptionEventListener.onRemoteEventReceived(data, fromEui64);
		}
	}

	@Override
	public void receiveServerResponse(int responseCode, String responseData, String fromIp) {
		//TODO if response code not 200? retry?
		if(responseCode != HttpURLConnection.HTTP_OK ) {
			ALog.i(ALog.SUBSCRIPTION, "Subscription failed");
			ALog.d(ALog.SUBSCRIPTION, "ReponseCode:  " + responseCode + "   source Ip: " + fromIp);
			ALog.d(ALog.SUBSCRIPTION, "ReponseData:  " + responseData);
			return;
		}

		ALog.i(ALog.SUBSCRIPTION, "Subscription successfull");
		onUDPEventReceived(responseData, fromIp); // Response already contains first subscription events, treat as UDP
		// TODO fix this
	}

	public static void setDummySubscriptionManagerForTesting(SubscriptionHandler dummyManager) {
		mInstance = dummyManager;
	}
} 
