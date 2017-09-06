/*
 * Copyright © 2016 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.example.cdpp.bluelibexampleapp.device;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.cdpp.bluelibexampleapp.R;
import com.example.cdpp.bluelibexampleapp.uapp.BleDemoMicroAppInterface;
import com.example.cdpp.bluelibexampleapp.util.UiUtils;
import com.philips.pins.shinelib.SHNCapabilityType;
import com.philips.pins.shinelib.SHNDevice;
import com.philips.pins.shinelib.SHNIntegerResultListener;
import com.philips.pins.shinelib.SHNResult;
import com.philips.pins.shinelib.capabilities.SHNCapabilityBattery;
import com.philips.pins.shinelib.capabilities.SHNCapabilityDeviceInformation;
import com.philips.pins.shinelib.utility.SHNLogger;

import java.util.Date;
import java.util.Locale;

public class DeviceDetailActivity extends AppCompatActivity {

    private static final String TAG = "DeviceDetail";

    private static final long CONNECTION_TIMEOUT = 30000L;

    private View mView;
    private FloatingActionButton mFab;

    private SHNDevice mDevice;
    private SHNDevice.SHNDeviceListener mDeviceListener = new SHNDevice.SHNDeviceListener() {

        @Override
        public void onStateUpdated(SHNDevice device) {
            updateUiState(device);
            updateConnectButtonState(device.getState());
        }

        @Override
        public void onFailedToConnect(SHNDevice shnDevice, SHNResult result) {
            SHNLogger.w(TAG, "onFailedToConnect.");
        }

        @Override
        public void onReadRSSI(int rssi) {
            // Nothing to do
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bll_activity_device_detail);

        mView = findViewById(android.R.id.content);

        // Setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.bll_detail_toolbar);
        setSupportActionBar(toolbar);

        // Setup the connect button
        mFab = (FloatingActionButton) findViewById(R.id.bll_fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDevice == null) {
                    return;
                }
                mDevice.connect(CONNECTION_TIMEOUT);
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mDevice = BleDemoMicroAppInterface.getInstance().getSelectedDevice();
        if (mDevice != null) {
            mDevice.registerSHNDeviceListener(mDeviceListener);

            // Update UI
            setTitle(mDevice.getName());
            updateUiState(mDevice);
            updateConnectButtonState(mDevice.getState());
        }
    }

    private void updateConnectButtonState(SHNDevice.State state) {
        if (mFab == null) {
            return;
        }
        mFab.setEnabled(SHNDevice.State.Disconnected.equals(state));

        mFab.setVisibility(mFab.isEnabled() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDevice != null) {
            mDevice.unregisterSHNDeviceListener(mDeviceListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUiState(@NonNull SHNDevice device) {
        switch (device.getState()) {
            case Connected:
                UiUtils.showVolatileMessage(mView, String.format(Locale.US, getString(R.string.bll_device_connected), device.getName()));
                setupDeviceCapabilities(device);
                break;
            case Connecting:
                UiUtils.showPersistentMessage(mView, getString(R.string.bll_device_connecting));
                break;
            case Disconnected:
                UiUtils.showVolatileMessage(mView, getString(R.string.bll_device_disconnected));
                break;
            case Disconnecting:
                UiUtils.showPersistentMessage(mView, getString(R.string.bll_device_disconnecting));
                break;
        }
    }

    private void setupDeviceCapabilities(final SHNDevice shnDevice) {
        // Device Information capability
        SHNCapabilityDeviceInformation di = (SHNCapabilityDeviceInformation) shnDevice.getCapabilityForType(SHNCapabilityType.DEVICE_INFORMATION);

        if (di == null) {
            SHNLogger.w(TAG, "Device Information capability not available.");
        } else {
            displayDeviceInformation(di, SHNCapabilityDeviceInformation.SHNDeviceInformationType.FirmwareRevision, R.id.bll_textViewFirmwareValue);
            displayDeviceInformation(di, SHNCapabilityDeviceInformation.SHNDeviceInformationType.HardwareRevision, R.id.bll_textViewHardwareValue);
            displayDeviceInformation(di, SHNCapabilityDeviceInformation.SHNDeviceInformationType.ManufacturerName, R.id.bll_textViewManufacturerValue);
            displayDeviceInformation(di, SHNCapabilityDeviceInformation.SHNDeviceInformationType.ModelNumber, R.id.bll_textViewModelNumberValue);
            displayDeviceInformation(di, SHNCapabilityDeviceInformation.SHNDeviceInformationType.SerialNumber, R.id.bll_textViewSerialNumberValue);
            displayDeviceInformation(di, SHNCapabilityDeviceInformation.SHNDeviceInformationType.SoftwareRevision, R.id.bll_textViewSoftwareRevisionValue);
            displayDeviceInformation(di, SHNCapabilityDeviceInformation.SHNDeviceInformationType.SystemID, R.id.bll_textViewSystemIDValue);
        }

        // Battery capability
        final SHNCapabilityBattery battery = (SHNCapabilityBattery) shnDevice.getCapabilityForType(SHNCapabilityType.BATTERY);

        if (battery == null) {
            SHNLogger.w(TAG, "Battery capability not available.");
        } else {
            SHNLogger.i(TAG, "Battery capability found.");

            // Battery level
            displayBatteryLevel(battery);

            // Listen for battery level changes
            battery.setSetSHNCapabilityBatteryListener(new SHNCapabilityBattery.SHNCapabilityBatteryListener() {
                @Override
                public void onBatteryLevelChanged(int level) {
                    updateBatteryLevel(level);
                }
            });
        }
    }

    private void displayDeviceInformation(SHNCapabilityDeviceInformation deviceInformation, SHNCapabilityDeviceInformation.SHNDeviceInformationType type, final int textViewId) {
        if (deviceInformation == null) {
            return;
        }

        deviceInformation.readDeviceInformation(type, new SHNCapabilityDeviceInformation.Listener() {
            @Override
            public void onDeviceInformation(@NonNull final SHNCapabilityDeviceInformation.SHNDeviceInformationType deviceInformationType, @NonNull final String value, @NonNull final Date lastCacheUpdate) {
                setTextByViewId(value, textViewId);
            }

            @Override
            public void onError(@NonNull final SHNCapabilityDeviceInformation.SHNDeviceInformationType deviceInformationType, @NonNull final SHNResult error) {
                SHNLogger.e(TAG, "Error reading device information: " + error.name());

                setTextByViewId(getString(R.string.bll_unknown), textViewId);
            }
        });
    }

    private void displayBatteryLevel(SHNCapabilityBattery battery) {
        if (battery == null) {
            return;
        }

        battery.getBatteryLevel(new SHNIntegerResultListener() {
            @Override
            public void onActionCompleted(int value, SHNResult result) {
                if (result == SHNResult.SHNOk) {
                    updateBatteryLevel(value);
                }
            }
        });
    }

    private void updateBatteryLevel(int batteryLevel) {
        setTextByViewId(String.format(Locale.US, "%d%%", batteryLevel), R.id.bll_textViewBatteryValue);
    }

    private void setTextByViewId(final String text, final int textViewId) {
        final TextView tv = (TextView) findViewById(textViewId);
        if (tv == null) {
            return;
        }
        tv.setText(text.trim());
    }
}
