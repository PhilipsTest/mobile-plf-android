/*
 * Copyright © 2016 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.cdp2.bluelib.demouapp.util;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.philips.pins.shinelib.SHNCentral;
import com.philips.pins.shinelib.SHNDeviceFoundInfo;
import com.philips.pins.shinelib.SHNDeviceScanner;
import com.philips.pins.shinelib.utility.SHNLogger;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class DeviceScanner {

    private static final String TAG = "DeviceScanner";

    public interface OnDeviceScanListener {
        void onDeviceScanStarted();

        void onDeviceFoundInfo(SHNDeviceFoundInfo deviceFoundInfo);

        void onDeviceScanFinished();
    }

    private ScanTask mScanTask;

    private boolean mIsScanning;
    private SHNDeviceScanner mDeviceScanner;
    private final Set<OnDeviceScanListener> mDeviceScanListeners = new CopyOnWriteArraySet<>();
    private Handler mUIHandler;

    public DeviceScanner(@NonNull SHNCentral shnCentral, Handler handler) {
        mDeviceScanner = shnCentral.getShnDeviceScanner();
        mUIHandler = handler;
    }

    public void startScan() {
        mScanTask = new ScanTask();
        mScanTask.execute();
    }

    public void stopScan() {
        if (mScanTask == null) {
            return;
        }
        mScanTask.cancel(true);
    }

    public void addOnScanListener(OnDeviceScanListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("OnDeviceScanListener is null.");
        }
        mDeviceScanListeners.add(listener);
    }

    public void removeOnScanListener(OnDeviceScanListener listener) {
        mDeviceScanListeners.remove(listener);
    }

    private void notifyOnScanStarted() {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                for (OnDeviceScanListener listener : mDeviceScanListeners) {
                    listener.onDeviceScanStarted();
                }
            }
        });
    }

    private void notifyOnDeviceFoundInfo(final SHNDeviceFoundInfo deviceFoundInfo) {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                for (OnDeviceScanListener listener : mDeviceScanListeners) {
                    listener.onDeviceFoundInfo(deviceFoundInfo);
                }
            }
        });
    }

    private void notifyOnScanFinished() {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                for (OnDeviceScanListener listener : mDeviceScanListeners) {
                    listener.onDeviceScanFinished();
                }
            }
        });
    }

    private class ScanTask extends AsyncTask<Long, Void, Void> implements SHNDeviceScanner.SHNDeviceScannerListener {

        private static final long SCAN_TIMEOUT_MS = 30000L;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            notifyOnScanStarted();
        }

        @Override
        protected Void doInBackground(Long... params) {
            SHNLogger.d(TAG, "doInBackground");

            if (mIsScanning) {
                return null;
            }
            mIsScanning = true;

            // If a timeout value is provided use that instead of the default
            long scanTimeoutMs = params.length == 1 ? params[0] : SCAN_TIMEOUT_MS;

            mDeviceScanner.startScanning(this, SHNDeviceScanner.ScannerSettingDuplicates.DuplicatesNotAllowed, scanTimeoutMs);
            notifyOnScanStarted();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            notifyOnScanFinished();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            mDeviceScanner.stopScanning();
        }

        @Override
        public void deviceFound(SHNDeviceScanner shnDeviceScanner, @NonNull SHNDeviceFoundInfo shnDeviceFoundInfo) {
            SHNLogger.i(TAG, String.format("Device found: %s", shnDeviceFoundInfo.getDeviceName()));

            notifyOnDeviceFoundInfo(shnDeviceFoundInfo);
        }

        @Override
        public void scanStopped(SHNDeviceScanner shnDeviceScanner) {
            mIsScanning = false;

            notifyOnScanFinished();
        }
    }
}
