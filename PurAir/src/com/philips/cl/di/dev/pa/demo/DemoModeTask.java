package com.philips.cl.di.dev.pa.demo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.philips.cl.di.dev.pa.util.ALog;
import com.philips.cl.di.dev.pa.util.NetworkUtils;
import com.philips.cl.di.dev.pa.util.ServerResponseListener;

public class DemoModeTask extends Thread {

	private String url ;
	private ServerResponseListener listener ;
	private int responseCode;
	private String result = "" ;
	private boolean stop;

	public DemoModeTask(ServerResponseListener listener, String url) {
		ALog.i(ALog.DEMO_MODE, "Url: " + url);
		this.url = url ;
		this.listener = listener ;
	}

	@Override
	public void run() {
		InputStream inputStream = null;
		HttpURLConnection conn = null ;
		try {
			URL urlConn = new URL(url);
			conn = (HttpURLConnection) urlConn.openConnection() ;
			conn.setRequestMethod("GET");
			conn.connect();
			responseCode = conn.getResponseCode() ;
			if ( responseCode == 200 ) {
				inputStream = conn.getInputStream();					
				result = NetworkUtils.readFully(inputStream) ;
			}				
			
		} catch (IOException e) {
			ALog.e(ALog.DEMO_MODE, e.getMessage());
		}
		finally {
			
			if ( listener != null && !stop) {
				listener.receiveServerResponse(responseCode, result, null) ;
			}

			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					ALog.e(ALog.DEMO_MODE, e.getMessage());
				}
				inputStream = null ;
			} 
			if( conn != null ) {
				conn.disconnect() ;
				conn = null ;
			}
		}
	}
	
	public void stopTask() {
		stop = true;
	}
}
