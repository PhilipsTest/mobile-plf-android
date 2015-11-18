/*
 * Copyright (c) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */

package com.philips.pins.shinelib;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.util.Log;

import com.philips.pins.shinelib.bluetoothwrapper.BTGatt;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by 310188215 on 26/03/15.
 */
public class SHNCharacteristic {
    private static final String TAG = SHNCharacteristic.class.getSimpleName();
    private static final boolean LOGGING = false;
    private static final UUID CLIENT_CHARACTERISTIC_CONFIG_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private final UUID uuid;
    private BluetoothGattCharacteristic bluetoothGattCharacteristic;
    private BTGatt btGatt;
    private State state;
    private SHNCharacteristicChangedListener shnCharacteristicChangedListener;
    private List<SHNCommandResultReporter> pendingCompletions;

    public interface SHNCharacteristicChangedListener {
        void onCharacteristicChanged(SHNCharacteristic shnCharacteristic, byte[] data);
    }

    public enum State {Inactive, Active}

    public SHNCharacteristic(UUID characteristicUUID) {
        this.uuid = characteristicUUID;
        this.state = State.Inactive;
        this.pendingCompletions = new LinkedList<>();
        if (LOGGING) Log.i(TAG, "created: " + uuid);
    }

    public State getState() {
        return state;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void connectToBLELayer(BTGatt btGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        if (LOGGING) Log.i(TAG, "connectToBLELayer: " + uuid);
        this.btGatt = btGatt;
        this.bluetoothGattCharacteristic = bluetoothGattCharacteristic;
        state = State.Active;
    }

    public void disconnectFromBLELayer() {
        if (LOGGING) Log.i(TAG, "disconnectFromBLELayer: " + uuid);
        bluetoothGattCharacteristic = null;
        btGatt = null;
        state = State.Inactive;
    }

    public byte[] getValue() {
        if (bluetoothGattCharacteristic != null) {
            return bluetoothGattCharacteristic.getValue();
        }
        return null;
    }

    public boolean write(byte[] data, SHNCommandResultReporter resultReporter) {
        if (state == State.Active) {
            btGatt.writeCharacteristic(bluetoothGattCharacteristic, data);
            pendingCompletions.add(resultReporter);
        } else {
            if (LOGGING) Log.i(TAG, "Error write; characteristic not active: " + uuid);
            return false;
        }
        return true;
    }

    public boolean read(SHNCommandResultReporter resultReporter) {
        if (state == State.Active) {
            btGatt.readCharacteristic(bluetoothGattCharacteristic);
            pendingCompletions.add(resultReporter);
        } else {
            if (LOGGING) Log.i(TAG, "Error read; characteristic not active: " + uuid);
            return false;
        }
        return true;
    }

    public boolean setNotification(boolean enable, SHNCommandResultReporter resultReporter) {
        return writeToBtGatt(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, enable, resultReporter);
    }

    public boolean setIndication(boolean enable, SHNCommandResultReporter resultReporter) {
        return writeToBtGatt(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE, enable, resultReporter);
    }

    public void setShnCharacteristicChangedListener(SHNCharacteristicChangedListener shnCharacteristicChangedListener) {
        this.shnCharacteristicChangedListener = shnCharacteristicChangedListener;
    }

    public void onReadWithData(BTGatt gatt, int status, byte[] data) {
        if (LOGGING) Log.i(TAG, "onReadWithData");
        SHNResult shnResult = translateGATTResultToSHNResult(status);
        reportResultToCaller(data, shnResult);
    }

    public void onWrite(BTGatt gatt, int status) {
        if (LOGGING) Log.i(TAG, "onWrite");
        SHNResult shnResult = translateGATTResultToSHNResult(status);
        reportResultToCaller(null, shnResult);
    }

    public void onChanged(BTGatt gatt, byte[] data) {
        if (LOGGING) Log.i(TAG, "onChanged");
        if (shnCharacteristicChangedListener != null) {
            shnCharacteristicChangedListener.onCharacteristicChanged(this, data);
        }
    }

    public void onDescriptorReadWithData(BTGatt gatt, BluetoothGattDescriptor descriptor, int status, byte[] data) {
        throw new UnsupportedOperationException("onDescriptorReadWithData");
    }

    public void onDescriptorWrite(BTGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        if (LOGGING) Log.i(TAG, "onDescriptorWrite " + getUuid() + " size = " + pendingCompletions.size());
        SHNResult shnResult = translateGATTResultToSHNResult(status);
        reportResultToCaller(null, shnResult);
    }

    private SHNResult translateGATTResultToSHNResult(int status) {
        SHNResult shnResult = SHNResult.SHNErrorUnknownDeviceType;
        if (status == BluetoothGatt.GATT_SUCCESS) {
            shnResult = SHNResult.SHNOk;
        }
        return shnResult;
    }

    private void reportResultToCaller(byte[] data, SHNResult shnResult) {
        SHNCommandResultReporter completion = pendingCompletions.remove(0);
        if (completion != null) completion.reportResult(shnResult, data);
    }

    private boolean writeToBtGatt(byte[] value, boolean enable, SHNCommandResultReporter resultReporter){
        if (state == State.Active) {
            if (btGatt.setCharacteristicNotification(bluetoothGattCharacteristic, enable)) {
                BluetoothGattDescriptor descriptor = bluetoothGattCharacteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID);
                if(descriptor==null){
                    resultReporter.reportResult(SHNResult.SHNErrorUnsupportedOperation, null);
                    return false;
                }
                btGatt.writeDescriptor(descriptor, value);
                pendingCompletions.add(resultReporter);
                return true;
            }
        }
        return false;
    }
}
