/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.pins.shinelib.statemachine.state;

import android.bluetooth.BluetoothGatt;
import android.support.annotation.NonNull;

import com.philips.pins.shinelib.bluetoothwrapper.BTGatt;
import com.philips.pins.shinelib.statemachine.SHNDeviceStateMachine;
import com.philips.pins.shinelib.utility.SHNLogger;

public class SHNDiscoveringServicesState extends SHNConnectingState {

    private static final String TAG = "SHNDiscoveringServicesState";
    private static final long SERVICE_DISCOVERY_TIMEOUT = 20_000L;

    public SHNDiscoveringServicesState(@NonNull SHNDeviceStateMachine stateMachine) {
        super(stateMachine, SERVICE_DISCOVERY_TIMEOUT);
    }

    @Override
    protected void onEnter() {
        super.onEnter();

        BTGatt btGatt = sharedResources.getBtGatt();
        if (btGatt != null) {
            btGatt.discoverServices();
        }
    }

    @Override
    public void onServicesDiscovered(BTGatt gatt, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            if (gatt.getServices().size() == 0) {
                SHNLogger.i(TAG, "No services found, rediscover the services");
                gatt.disconnect();
                // TODO send tag no services found
                stateMachine.setState(new SHNGattConnectingState(stateMachine));
                return;
            }

            stateMachine.setState(new SHNInitializingServicesState(stateMachine));
        } else {
            SHNLogger.e(TAG, "onServicedDiscovered: error discovering services (status = '" + status + "'); disconnecting");
            // TODO send tag that GATT connection failed, include status
            stateMachine.setState(new SHNDisconnectingState(stateMachine));
        }
    }
}
