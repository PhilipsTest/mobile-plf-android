package com.philips.cl.di.dev.pa.demo;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.philips.cl.di.dev.pa.PurAirApplication;
import com.philips.cl.di.dev.pa.R;
import com.philips.cl.di.dev.pa.activity.MainActivity;
import com.philips.cl.di.dev.pa.ews.EWSWifiManager;
import com.philips.cl.di.dev.pa.fragment.DownloadAlerDialogFragement;
import com.philips.cl.di.dev.pa.newpurifier.AirPurifier;
import com.philips.cl.di.dev.pa.newpurifier.AirPurifierManager;
import com.philips.cl.di.dev.pa.newpurifier.ConnectionState;
import com.philips.cl.di.dev.pa.util.ALog;
import com.philips.cl.di.dev.pa.util.networkutils.NetworkReceiver;
import com.philips.cl.di.dev.pa.util.networkutils.NetworkStateListener;

public class AppInDemoMode implements NetworkStateListener {
	private Context ctx;
	private KeyInitializeState keyInitializeState = KeyInitializeState.NONE;

	public AppInDemoMode(Context ctx) {
		this.ctx = ctx;
	}

	public void addNetworkListenerForDemoMode() {
		NetworkReceiver.getInstance().addNetworkStateListener(this);

	}

	public void rmoveNetworkListenerForDemoMode() {
		NetworkReceiver.getInstance().removeNetworkStateListener(this);
	}

	public void connectPurifier() {
		keyInitializeState = KeyInitializeState.NONE;

		String ssid = EWSWifiManager.getSsidOfSupplicantNetwork();
		if (ssid != null && ssid.contains(EWSWifiManager.DEVICE_SSID)) {
//			initializeKeyExchange();
		} else {
			showAlertDialogAppInDemoMode(
					ctx.getString(R.string.app_in_demo_mode_title),
					ctx.getString(R.string.app_in_demo_mode_msg));
		}
	}

	public void checkPhilipsSetupWifiSelected() {
		String ssid = EWSWifiManager.getSsidOfSupplicantNetwork();
		if (ssid == null || !ssid.contains(EWSWifiManager.DEVICE_SSID)) {
			showAlertDialogAppInDemoMode(
					ctx.getString(R.string.app_in_demo_mode_title),
					ctx.getString(R.string.app_in_demo_mode_msg));
		}
	}

	private void showAlertDialogAppInDemoMode(String title, String message) {
		try {
			FragmentTransaction fragTransaction = ((MainActivity) ctx)
					.getSupportFragmentManager().beginTransaction();

			Fragment prevFrag = ((MainActivity) ctx)
					.getSupportFragmentManager().findFragmentByTag(
							"app_in_demo_mode");
			if (prevFrag != null) {
				return;
			}

			fragTransaction.add(
					DownloadAlerDialogFragement.newInstance(title, message),
					"app_in_demo_mode").commitAllowingStateLoss();

		} catch (IllegalStateException e) {
			ALog.e(ALog.INDOOR_DETAILS, "Error: " + e.getMessage());
		}
	}

//	private void initializeKeyExchange() {
//		if (keyInitializeState == KeyInitializeState.NONE) {
//			keyInitializeState = KeyInitializeState.START;
//			DISecurity di = new DISecurity(this);
//			di.initializeExchangeKeyCounter(PurAirApplication
//					.getDemoModePurifierEUI64());
//			di.exchangeKey(Utils.getPortUrl(Port.SECURITY,
//					EWSConstant.PURIFIER_ADHOCIP), PurAirApplication
//					.getDemoModePurifierEUI64());
//		}
//	}

	@Override
	public void onConnected(String ssid) {
		AirPurifier current = AirPurifierManager.getInstance()
				.getCurrentPurifier();
		if (PurAirApplication.isDemoModeEnable()) {
			if (current != null
					&& current.getNetworkNode().getConnectionState() == ConnectionState.CONNECTED_LOCALLY)
				return;
			if (ssid != null && ssid.contains(EWSWifiManager.DEVICE_SSID)) {
//				initializeKeyExchange();
				return;
			}
		}
	}

	@Override
	public void onDisconnected() {
		// NOP
	}

//	@Override
//	public void keyDecrypt(String key, String deviceEui64) {
//		ALog.i(ALog.MAINACTIVITY, "Key exchange succesfull for shop demo mode");
//		keyInitializeState = KeyInitializeState.NONE;
//		AirPurifier demoModePurifier = new AirPurifier(new CommunicationMarshal(new DISecurity()), deviceEui64, null,
//				EWSConstant.PURIFIER_ADHOCIP, DemoModeConstant.DEMO, -1,
//				ConnectionState.CONNECTED_LOCALLY);
//		demoModePurifier.getNetworkNode().setEncryptionKey(key);
//		AirPurifierManager.getInstance().setCurrentPurifier(demoModePurifier);
//	}
}
