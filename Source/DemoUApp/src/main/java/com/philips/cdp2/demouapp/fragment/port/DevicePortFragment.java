/*
 * Copyright (c) 2015-2017 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.cdp2.demouapp.fragment.port;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.philips.cdp.dicommclient.appliance.CurrentApplianceManager;
import com.philips.cdp.dicommclient.port.DICommPortListener;
import com.philips.cdp.dicommclient.port.common.DevicePort;
import com.philips.cdp.dicommclient.port.common.DevicePortProperties;
import com.philips.cdp.dicommclient.request.Error;
import com.philips.cdp.dicommclient.util.DICommLog;
import com.philips.cdp2.commlib.core.appliance.Appliance;
import com.philips.cdp2.commlib.demouapp.R;

import java.util.Locale;

public class DevicePortFragment extends Fragment {

    private static final String TAG = "DevicePortFragment";

    private Appliance currentAppliance;
    private EditText deviceNameEdit;

    private DICommPortListener<DevicePort> portListener = new DICommPortListener<DevicePort>() {
        @Override
        public void onPortUpdate(DevicePort port) {
            DevicePortProperties properties = port.getPortProperties();
            deviceNameEdit.setText(properties.getName());
        }

        @Override
        public void onPortError(DevicePort port, Error error, @Nullable String errorData) {
            DICommLog.e(TAG, String.format(Locale.US, "Device port error: %s, data: %s", error.getErrorMessage(), errorData));
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_device_port, container, false);

        deviceNameEdit = (EditText) rootView.findViewById(R.id.device_name);
        Button setButton = (Button) rootView.findViewById(R.id.btn_set);
        Button getButton = (Button) rootView.findViewById(R.id.btn_get);

        ((CompoundButton) rootView.findViewById(R.id.switchSubscription)).setOnCheckedChangeListener(subscriptionCheckedChangeListener);

        setButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentAppliance.getDevicePort().putProperties("name", deviceNameEdit.getText().toString());
                    }
                }
        );

        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentAppliance.getDevicePort().reloadProperties();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        currentAppliance = CurrentApplianceManager.getInstance().getCurrentAppliance();

        currentAppliance.getDevicePort().addPortListener(portListener);
        currentAppliance.getDevicePort().reloadProperties();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (currentAppliance != null) {
            currentAppliance.getDevicePort().removePortListener(portListener);
        }
    }

    private final CompoundButton.OnCheckedChangeListener subscriptionCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
            if (currentAppliance == null) {
                return;
            }

            if (isChecked) {
                currentAppliance.getDevicePort().subscribe();
            } else {
                currentAppliance.getDevicePort().unsubscribe();
            }
        }
    };
}
