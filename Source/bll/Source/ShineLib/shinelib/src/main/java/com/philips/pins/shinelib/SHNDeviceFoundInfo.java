/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.pins.shinelib;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;

import com.philips.pins.shinelib.utility.BleScanRecord;

/**
 * {@code SHNDeviceFoundInfo} contains information obtained from the peripheral during scanning.
 * <p/>
 * {@code SHNDeviceFoundInfo} is returned by {@link com.philips.pins.shinelib.SHNDeviceScanner}.
 *
 * @publicPluginApi
 */
public class SHNDeviceFoundInfo {

    @NonNull
    private final String deviceAddress;

    @NonNull
    private final String deviceName;

    private final int rssi;

    @NonNull
    private final byte[] scanRecord;

    @NonNull
    private final SHNDeviceDefinitionInfo shnDeviceDefinitionInfo;

    @NonNull
    private final SHNDevice shnDevice;

    @NonNull
    private final BleScanRecord bleScanRecord;

    public SHNDeviceFoundInfo(final @NonNull SHNCentral shnCentral, final @NonNull BluetoothDevice bluetoothDevice, final int rssi, final byte[] scanRecord, @NonNull final SHNDeviceDefinitionInfo shnDeviceDefinitionInfo, @NonNull final BleScanRecord bleScanRecord) {
        this.deviceAddress = bluetoothDevice.getAddress();
        this.shnDevice = shnCentral.createSHNDeviceForAddressAndDefinition(deviceAddress, shnDeviceDefinitionInfo);

        String name = bluetoothDevice.getName();
        if (name == null) {
            name = bleScanRecord.getLocalName();
        }
        if (name == null) {
            name = this.shnDevice.getName();
        }
        this.shnDevice.setName(name);
        this.deviceName = name;

        this.rssi = rssi;
        this.scanRecord = scanRecord.clone();
        this.shnDeviceDefinitionInfo = shnDeviceDefinitionInfo;
        this.bleScanRecord = bleScanRecord;
    }

    /**
     * Returns the string representation of MAC address for the bluetooth peripheral.
     *
     * @return string representation of the peripheral MAC address as returned by {@link android.bluetooth.BluetoothDevice#getAddress()}
     */
    @NonNull
    public String getDeviceAddress() {
        return deviceAddress;
    }

    /**
     * Returns the name of the bluetooth peripheral.
     *
     * @return name of the peripheral as returned by {@link android.bluetooth.BluetoothDevice#getName()}
     */
    @NonNull
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * Returns the Remote Signal Strength Indicator.
     *
     * @return The RSSI value for the remote device as reported by the Bluetooth hardware. 0 if no RSSI value is available.
     */
    public int getRssi() {
        return rssi;
    }

    /**
     * Returns the content of the advertisement record offered by the remote peripheral.
     *
     * @return raw advertisement data offered by the remote peripheral.
     */
    @NonNull
    public byte[] getScanRecord() {
        return scanRecord;
    }

    /**
     * Returns {@link SHNDeviceDefinitionInfo} that matches the found peripheral.
     *
     * @return {@link SHNDeviceDefinitionInfo} for the found peripheral
     */
    @NonNull
    public SHNDeviceDefinitionInfo getShnDeviceDefinitionInfo() {
        return shnDeviceDefinitionInfo;
    }

    /**
     * Returns {@link SHNDevice} for the found peripheral.
     *
     * @return {@link SHNDevice} for the found peripheral
     */
    @NonNull
    public SHNDevice getShnDevice() {
        return shnDevice;
    }

    /**
     * Returns {@link BleScanRecord} instance of the found peripheral.
     *
     * @return {@link BleScanRecord} that wraps raw advertisement data
     */
    @NonNull
    public BleScanRecord getBleScanRecord() {
        return bleScanRecord;
    }

    /**
     * Returns meaningful representation of {@code SHNDeviceFoundInfo}.
     *
     * @return string representing {@code SHNDeviceFoundInfo} including deviceName, deviceAddress and rssi
     */
    @Override
    @NonNull
    public String toString() {
        return deviceName + " [" + deviceAddress + "]" + " (" + rssi + ")";
    }
}
