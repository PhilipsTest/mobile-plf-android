
package com.philips.cdp.localematch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.philips.cdp.localematch.enums.LocaleMatchError;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashSet;
import java.util.Locale;


public class LocaleMatchThreadManager {

	private static HashSet<String> mThreadList = new HashSet<String>();

	private String mLanguageCode;

	private String mCountryCode;

	private String mInputLocale;

	private static final String REQUESTTYPE = "GET";

	private static final int CONN_TIMEOUT = 5000;

	private static final int READ_TIMEOUT = 5000; // TODO Ideally it should be 30 seconds, check

	public static final String URL ="http://www.philips.co.uk/prx/i18n/matchLocale/";	
	
	private static final String LOG_TAG ="LocaleMatchThreadManager";

	private LocaleMatchNotifier mLocaleMatchNotifier = null;

	private Context mContext;
	
	public LocaleMatchThreadManager(LocaleMatchNotifier notifier, Context context){
		mLocaleMatchNotifier = notifier;
		mContext = context;
	}
	public void processRequest(String languageCode,
	        String countryCode) {
		mLanguageCode = languageCode;
		mCountryCode = countryCode;
		Thread thread = new Thread(new LocaleMatchRunnable());
		thread.start();
	}

	public static boolean isProcessingRequest(String locale) {
		return mThreadList.contains(locale);
	}

	@SuppressLint("NewApi")
	private class LocaleMatchRunnable implements Runnable {

		@Override
		public void run() {
			mInputLocale = mLanguageCode + "_" + mCountryCode;
			if (mThreadList != null) {
				mThreadList.add(mInputLocale);
			}
			performHttpRequest();
		}

		private void performHttpRequest() {
			HttpURLConnection connection = null;

			try {
				String urlStr = URL
				        + mCountryCode.toUpperCase(Locale.getDefault()) + "/"
				        + mLanguageCode.toLowerCase(Locale.getDefault())+".json";
				Log.d(LOG_TAG,"LocaleMatchThreadManager, performHttpRequest,URL = "
				                + urlStr);
				URL url = new URL(urlStr);
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod(REQUESTTYPE);
				connection.setConnectTimeout(CONN_TIMEOUT);
				connection.setReadTimeout(READ_TIMEOUT);
				connection.connect();
				int responseCode = connection.getResponseCode();
				Log.d(LOG_TAG,
				        "LocaleMatchThreadManager, performHttpRequest(), responseCode"
				                + responseCode);
				InputStream ipStream = new BufferedInputStream(
				        connection.getInputStream());
				handleResponse(responseCode, ipStream);

			} catch (SocketTimeoutException e) {
				Log.i(LOG_TAG,
				        "LocaleMatchThreadManager, performHttpRequest() caught IO Exception");
				e.printStackTrace();
				sendCallback(true, LocaleMatchError.DEFAULT);
			} catch (IOException e) {
				Log.i(LOG_TAG,
				        "LocaleMatchThreadManager, performHttpRequest() caught IO Exception");
				e.printStackTrace();
				sendCallback(true, LocaleMatchError.DEFAULT);
			} catch (Exception e) {
				Log.i(LOG_TAG,
				        "LocaleMatchThreadManager, performHttpRequest() caught Generic Exception");
				e.printStackTrace();
				sendCallback(true, LocaleMatchError.DEFAULT);
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
			}
		}

		private void handleResponse(int responseCode, InputStream ipStream) {
			switch (responseCode) {
				case HttpURLConnection.HTTP_OK:
					LocaleMatchFileHelper.writeResponseToFile(ipStream, mInputLocale,mContext);
					sendCallback(false, null);
					break;
				case HttpURLConnection.HTTP_INTERNAL_ERROR:
					sendCallback(true, LocaleMatchError.SERVER_ERROR);
					break;
				case HttpURLConnection.HTTP_NOT_FOUND:
					sendCallback(true, LocaleMatchError.NOT_FOUND);
					break;
				case HttpURLConnection.HTTP_BAD_REQUEST:
					sendCallback(true, LocaleMatchError.INPUT_VALIDATION_ERROR);
					break;
				default:sendCallback(true, LocaleMatchError.DEFAULT);
					break;
			}

		}

		private void sendCallback(boolean isError, LocaleMatchError error) {
			Log.i(LOG_TAG, "sendCallback,isError = "+ isError);
			if (mThreadList != null && mThreadList.contains(mInputLocale)) {
				mThreadList.remove(mInputLocale);
			}
			if (isError) {
				mLocaleMatchNotifier.notifyLocaleMatchError(error);
			} else {
				mLocaleMatchNotifier.notifyLocaleMatchSuccess(mInputLocale);
			}
		}
		
	};
}
