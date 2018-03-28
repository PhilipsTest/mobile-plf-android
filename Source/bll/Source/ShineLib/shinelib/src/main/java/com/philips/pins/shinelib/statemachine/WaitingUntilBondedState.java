package com.philips.pins.shinelib.statemachine;

import android.bluetooth.BluetoothDevice;

import com.philips.pins.shinelib.SHNCentral;
import com.philips.pins.shinelib.SHNDeviceImpl;
import com.philips.pins.shinelib.SHNResult;
import com.philips.pins.shinelib.framework.Timer;
import com.philips.pins.shinelib.utility.SHNLogger;

public class WaitingUntilBondedState extends ConnectingState implements SHNCentral.SHNBondStatusListener {

    private static final String TAG = WaitingUntilBondedState.class.getSimpleName();

    private static final long WAIT_UNTIL_BONDED_TIMEOUT_IN_MS = 3_000L;
    private static final long BT_STACK_HOLD_OFF_TIME_AFTER_BONDED_IN_MS = 1_000L; // Prevent either the Thermometer or the BT stack on some devices from getting in a error state

    private Timer bondingTimer = Timer.createTimer(new Runnable() {
        @Override
        public void run() {
            SHNLogger.w(TAG, "Timed out waiting until bonded; trying service discovery");
            stateMachine.setState(WaitingUntilBondedState.this, new DiscoveringServicesState(stateMachine, sharedResources));
        }
    }, WAIT_UNTIL_BONDED_TIMEOUT_IN_MS);

    public WaitingUntilBondedState(StateMachine stateMachine, SharedResources sharedResources) {
        super(stateMachine, sharedResources);
    }

    @Override
    protected void onEnter() {
        sharedResources.getShnCentral().registerBondStatusListenerForAddress(this, sharedResources.getBtDevice().getAddress());

        bondingTimer.restart();

        if (sharedResources.getShnBondInitiator() == SHNDeviceImpl.SHNBondInitiator.APP) {
            if (!sharedResources.getBtDevice().createBond()) {
                SHNLogger.w(TAG, "Failed to start bond creation procedure");
                stateMachine.setState(this, new DiscoveringServicesState(stateMachine, sharedResources));
            }
        }
    }

    @Override
    protected void onExit() {
        sharedResources.getShnCentral().unregisterBondStatusListenerForAddress(this, sharedResources.getBtDevice().getAddress());
        bondingTimer.stop();
    }

    @Override
    public void onBondStatusChanged(BluetoothDevice device, int bondState, int previousBondState) {
        if (sharedResources.getBtDevice().getAddress().equals(device.getAddress())) {
            SHNLogger.i(TAG, "Bond state changed ('" + bondStateToString(previousBondState) + "' -> '" + bondStateToString(bondState) + "')");

            if (bondState == BluetoothDevice.BOND_BONDING) {
                bondingTimer.restart();
            } else if (bondState == BluetoothDevice.BOND_BONDED) {
                bondingTimer.stop();

                sharedResources.getShnCentral().getInternalHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stateMachine.setState(WaitingUntilBondedState.this, new DiscoveringServicesState(stateMachine, sharedResources));
                    }
                }, BT_STACK_HOLD_OFF_TIME_AFTER_BONDED_IN_MS);
            } else if (bondState == BluetoothDevice.BOND_NONE) {
                sharedResources.notifyFailureToListener(SHNResult.SHNErrorBondLost);
                stateMachine.setState(this, new DisconnectingState(stateMachine, sharedResources));
            }
        }
    }

    private static String bondStateToString(int bondState) {
        return (bondState == BluetoothDevice.BOND_NONE) ? "None" :
                (bondState == BluetoothDevice.BOND_BONDING) ? "Bonding" :
                        (bondState == BluetoothDevice.BOND_BONDED) ? "Bonded" : "Unknown";
    }
}
