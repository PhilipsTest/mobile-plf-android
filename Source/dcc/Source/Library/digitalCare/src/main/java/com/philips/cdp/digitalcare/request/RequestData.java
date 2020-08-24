/*
 * RequestData will help to perform network operations in UI.
 *
 * @since : 16 Jan 2015
 * Copyright (c) 2016 Philips. All rights reserved.
 */

package com.philips.cdp.digitalcare.request;


import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import com.philips.cdp.digitalcare.util.DigiCareLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * RequestData class respnsobile for performing the metwork operation in UI Thread.
 * Once it performed the remote response will trigger through the respective listeners.
 */
public class RequestData {

    private static final String TAG = RequestData.class.getSimpleName();

    private ResponseCallback mResponseCallback = null;
    private String mResponse = null;
    private String mRequestUrl = null;
    private Handler mResponseHandler = null;


    public void setRequestUrl(final String url) {
        DigiCareLogger.d(TAG, "url : " + url);
        mRequestUrl = url;
    }

    public void setResponseCallback(final ResponseCallback responseCallback) {
        mResponseCallback = responseCallback;
        mResponseHandler = new Handler(Looper.getMainLooper());
    }

    public void execute() {
        final NetworkThread networkThread = new NetworkThread();
        networkThread.setPriority(Thread.MAX_PRIORITY);
        networkThread.start();
    }

    protected void notifyResponseHandler() {
        if (mResponse != null) {

            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    mResponseCallback.onResponseReceived(mResponse);
                }
            });

        }
    }

    /**
     * NetworkThread class is an high priority thhread to perform the UI operations.
     */
    class NetworkThread extends Thread {

        @Override
        public void run() {
            try {
                final URL url = getRemoteUrl();
                final BufferedReader in = getResponseReaders(url);
                //     String inputLine = getaNull();
                final StringBuffer response = new StringBuffer();
                readResponse(in, response);
                in.close();
                mResponse = response.toString();
            } catch (IOException e) {
                printErrorLog(e);
            } finally {
                DigiCareLogger.d(TAG, "Response: [" + mResponse + "]");
                notifyResponseHandler();
            }
        }

        @NonNull
        private URL getRemoteUrl() throws MalformedURLException {
            final String requestUrl = mRequestUrl;
            return new URL(requestUrl);
        }

        @NonNull
        private BufferedReader getResponseReaders(URL url) throws IOException {
            final HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();
            urlConnection.setRequestMethod("GET");
            final InputStream inputStream = urlConnection.getInputStream();
            final Reader reader = new InputStreamReader(inputStream, "UTF-8");
            return new BufferedReader(reader);
        }

        private void printErrorLog(IOException e) {
            DigiCareLogger.e(
                    TAG,
                    "Failed to fetch Response Data : "
                            + e.getLocalizedMessage());
        }

        private void readResponse(BufferedReader in, StringBuffer response) throws IOException {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }
    }
}