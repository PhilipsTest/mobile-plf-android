/* Copyright (c) Koninklijke Philips N.V. 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.appinfra.timesync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.SntpClient;
import android.util.Log;

import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.AppInfraSingleton;
import com.philips.platform.appinfra.R;
import com.philips.platform.appinfra.logging.LoggingInterface;


import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by 310243577 on 6/27/2016.
 * * This provides API's to retrieve and refresh the server time .
 */
public class TimeSyncSntpClient extends BroadcastReceiver implements TimeInterface {
    private static final String TAG = "TimeSyncSntpClient";

    private static final String OFFSET = "offset";
    private static final String SERVERTIME_PREFERENCE = "timeSync";
    private static final int MAX_SERVER_TIMEOUT_IN_MSEC = 30000;
    private static final int REFRESH_INTERVAL_IN_HOURS = 24;
    private static final int FAILED_REFRESH_DELAY_IN_MINUTES = 5;

    private AppInfra mAppInfra;
    private SharedPreferences mSharedPreferences;
    private Boolean mIsRefreshInProgress = false;
    private long mOffset;
    private Calendar mNextRefreshTime;

    public static final String UTC = "UTC";
    public static final String DATE_FORMAT = "yyyy-MM-dd'T' K mm:ss.SSS Z";

    public TimeSyncSntpClient(AppInfra aAppInfra) {
        mAppInfra = aAppInfra;
        init();
        refreshTime();
    }

    public TimeSyncSntpClient() {
    }


    private void refreshIfNeeded() {
        Calendar now = Calendar.getInstance();
        if (!mIsRefreshInProgress && now.after(mNextRefreshTime)) {
            refreshTime();
        }
    }

    private synchronized void init() {
        mSharedPreferences = mAppInfra.getAppInfraContext().getSharedPreferences(SERVERTIME_PREFERENCE, Context.MODE_PRIVATE);
        mOffset = getOffset();
        mNextRefreshTime = Calendar.getInstance();
    }

    private void saveOffset(final long pOffset) {
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong(OFFSET, pOffset);
        editor.apply();
    }

    private long getOffset() {
        return mSharedPreferences.getLong(OFFSET, 0L);
    }

    private void refreshOffset() {
        synchronized (mIsRefreshInProgress) { // only lock this object to prevent locking the complete class
            mIsRefreshInProgress = true;

            boolean offsetUpdated = false;
            long offsetOfLowestRoundTrip = 0;
            long lowestRoundTripDelay = Long.MAX_VALUE;
            String[] serverPool;
            long[] offSets;
            long[] roundTripDelays;

            serverPool = mAppInfra.getAppInfraContext().getResources().getStringArray(R.array.server_pool);
            if (serverPool == null || serverPool.length == 0)
                throw new IllegalArgumentException("NTP server pool string array asset missing");

            offSets = new long[serverPool.length];
            roundTripDelays = new long[serverPool.length];
            SntpClient sntpClient = new SntpClient();

            for (int i = 0; i < serverPool.length; i++) {

                if (sntpClient.requestTime(serverPool[i], MAX_SERVER_TIMEOUT_IN_MSEC)) {
                    long deviceTime = System.currentTimeMillis();
                    offSets[i] = sntpClient.getNtpTime() - deviceTime;
                    roundTripDelays[i] = sntpClient.getRoundTripTime();
                } else {
                    roundTripDelays[i] = Long.MAX_VALUE;
                }
            }
            for (int i = 0; i < serverPool.length; i++) {
                if (roundTripDelays[i] < lowestRoundTripDelay) {
                    lowestRoundTripDelay = roundTripDelays[i];
                    offsetOfLowestRoundTrip = offSets[i];
                    offsetUpdated = true;
                }
            }

            if (offsetUpdated) {
                mNextRefreshTime.add(Calendar.HOUR, REFRESH_INTERVAL_IN_HOURS);
                mOffset = offsetOfLowestRoundTrip;
                saveOffset(mOffset);
            } else {
                mNextRefreshTime.add(Calendar.MINUTE, FAILED_REFRESH_DELAY_IN_MINUTES);
            }
            mIsRefreshInProgress = false;
        }
    }

    @Override
    public Date getUTCTime() {
        try {
            refreshIfNeeded();
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS a");
//            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));


            Date date = new Date(getOffset() + System.currentTimeMillis());
//            cal.setTime(date);
//
//            System.out.println("UTC---"+" "+formatter.format(cal.getTime()));
            //formatter.setTimeZone(TimeZone.getTimeZone(TimeConstants.UTC));
            //final String utcTime = formatter.format(date);
            //Log.i("UTCTime", "" + utcTime);
            //System.out.println("UTC" + " " + utcTime);

            return date;
        } catch (Exception e) {
            mAppInfra.getLogging().log(LoggingInterface.LogLevel.ERROR, "TimeSyncError", e.getMessage());
        }
        return null;
    }

    @Override
    public void refreshTime() {
        if (!mIsRefreshInProgress) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    refreshOffset();
                }
            }).start();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (AppInfraSingleton.getInstance() != null) {
            refreshTime();
        }
    }

}
