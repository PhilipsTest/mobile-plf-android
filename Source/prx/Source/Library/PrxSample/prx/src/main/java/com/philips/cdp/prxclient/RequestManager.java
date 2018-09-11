package com.philips.cdp.prxclient;

import android.util.Log;

import com.philips.cdp.prxclient.error.PrxError;
import com.philips.cdp.prxclient.network.NetworkWrapper;
import com.philips.cdp.prxclient.request.PrxRequest;
import com.philips.cdp.prxclient.response.ResponseListener;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;

import static android.content.ContentValues.TAG;

/**
 * This is the entry class to start the PRX Request.
 * It provides set of public APIs for placing requests from client.
 * @since 1.0.0
 *
 */
public class RequestManager {

	private PRXDependencies mPrxDependencies;

	/**
	 * Initialises RequestManager instance.
	 * @param prxDependencies PRX dependencies
	 * @since 2.2.0
	 */
	public void init(PRXDependencies prxDependencies) {
		mPrxDependencies = prxDependencies;
		if (mPrxDependencies != null) {
			AppInfraInterface appInfra = mPrxDependencies.getAppInfra();
			if (appInfra != null) {
				if (mPrxDependencies.getParentTLA() != null) {
					mPrxDependencies.mAppInfraLogging = appInfra.getLogging().createInstanceForComponent(String.format("%s/prx", mPrxDependencies.getParentTLA()), getLibVersion());
					mPrxDependencies.mAppInfraLogging.log(LoggingInterface.LogLevel.DEBUG, PrxConstants.PRX_REQUEST_MANAGER, String.format("PRX is initialized with  %s", mPrxDependencies.getParentTLA()));

				} else {
					mPrxDependencies.mAppInfraLogging = appInfra.getLogging().createInstanceForComponent("/prx", getLibVersion());
					mPrxDependencies.mAppInfraLogging.log(LoggingInterface.LogLevel.INFO, PrxConstants.PRX_REQUEST_MANAGER, "PRX is initialized ");
				}
			}
		}
	}

	/**
	 * Performs a network request.
	 * @param prxRequest PRX Request
	 * @param listener Response listener
	 * @since 1.0.0
	 */
	public void executeRequest(PrxRequest prxRequest, ResponseListener listener) {
		makeRequest(prxRequest, listener);
	}


	private void makeRequest(final PrxRequest prxRequest, final ResponseListener listener) {
		try {
			mPrxDependencies.mAppInfraLogging.log(LoggingInterface.LogLevel.INFO, PrxConstants.PRX_REQUEST_MANAGER, "execute prx request");
			new NetworkWrapper(mPrxDependencies).executeCustomJsonRequest(prxRequest, listener);
		} catch (Exception e) {
			mPrxDependencies.mAppInfraLogging.log(LoggingInterface.LogLevel.ERROR, PrxConstants.PRX_REQUEST_MANAGER, "Error in execute prx request");
			listener.onResponseError(new PrxError(PrxError.PrxErrorType.UNKNOWN_EXCEPTION.getDescription(),PrxError.PrxErrorType.UNKNOWN_EXCEPTION.getId()));
		}
	}

	/**
	 * Get the library version.
	 * @return returns the library version
	 * @since 2.2.0
	 *
	 */
	public String getLibVersion() {
		String mAppVersion = null;
		try {
			mAppVersion = BuildConfig.VERSION_NAME;
		} catch (Exception e) {
			Log.d(TAG, "Error in Version name ");
		}
		if (mAppVersion != null && !mAppVersion.isEmpty()) {
			if (!mAppVersion.matches("[0-9]+\\.[0-9]+\\.[0-9]+([_(-].*)?")) {
				throw new IllegalArgumentException("AppVersion should in this format " +
						"\" [0-9]+\\.[0-9]+\\.[0-9]+([_(-].*)?]\" ");
			}
		} else {
			throw new IllegalArgumentException("Prx Appversion cannot be null");
		}
		return mAppVersion;
	}


}
