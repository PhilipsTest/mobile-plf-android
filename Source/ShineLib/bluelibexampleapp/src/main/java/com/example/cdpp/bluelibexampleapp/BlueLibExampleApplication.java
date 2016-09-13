/*
 * Copyright © 2016 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.example.cdpp.bluelibexampleapp;

import android.app.Application;

import com.philips.cdp.pluginreferenceboard.DeviceDefinitionInfoReferenceBoard;
import com.philips.pins.shinelib.SHNCentral;
import com.philips.pins.shinelib.SHNDevice;
import com.philips.pins.shinelib.SHNDeviceDefinitionInfo;
import com.philips.pins.shinelib.exceptions.SHNBluetoothHardwareUnavailableException;
import com.philips.pins.shinelib.utility.SHNLogger;
import com.philips.pins.shinepluginmoonshinelib.SHNMoonshineDeviceDefinitionInfo;

public class BlueLibExampleApplication extends Application {

    private static final String TAG = "BlueLibExampleApplication";

    private static BlueLibExampleApplication sApplication;

    private SHNCentral mShnCentral;
    private SHNDevice mSelectedDevice;

    @Override
    public void onCreate() {
        super.onCreate();

        sApplication = this;

        // Setup logger
        SHNLogger.registerLogger(new SHNLogger.LogCatLogger());

        // Obtain BlueLib instance
        SHNCentral.Builder builder = new SHNCentral.Builder(this);
        builder.showPopupIfBLEIsTurnedOff(true);

        try {
            mShnCentral = builder.create();
        } catch (SHNBluetoothHardwareUnavailableException e) {
            SHNLogger.e(TAG, "Error obtaining BlueLib instance: " + e.getMessage());
        }

        setupDeviceDefinitions();
    }

    public static BlueLibExampleApplication get() {
        if (sApplication == null) {
            throw new IllegalStateException("Application not initialized yet.");
        }
        return sApplication;
    }

    public final SHNCentral getShnCentral() {
        return mShnCentral;
    }

    private void setupDeviceDefinitions() {
        SHNDeviceDefinitionInfo shnDeviceDefinitionInfo = new DeviceDefinitionInfoReferenceBoard();
        mShnCentral.registerDeviceDefinition(shnDeviceDefinitionInfo);

        SHNMoonshineDeviceDefinitionInfo shnMoonshineDeviceDefinitionInfo = new SHNMoonshineDeviceDefinitionInfo();
        mShnCentral.registerDeviceDefinition(shnMoonshineDeviceDefinitionInfo);
    }

    public void setSelectedDevice(SHNDevice device) {
        mSelectedDevice = device;
    }

    public SHNDevice getSelectedDevice() {
        return mSelectedDevice;
    }
}
