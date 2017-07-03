/*
 * Copyright (c) 2015-2017 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.cdp2.demouapp.fragment.port;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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

import static com.philips.cdp2.commlib.lan.context.LanTransportContext.acceptNewPinFor;
import static com.philips.cdp2.commlib.lan.context.LanTransportContext.rejectNewPinFor;

public class DevicePortFragment extends Fragment {

    private static final String TAG = "DevicePortFragment";

    private View rootview;
    private EditText deviceNameEdit;
    private Appliance currentAppliance;

    private DICommPortListener<DevicePort> portListener = new DICommPortListener<DevicePort>() {
        @Override
        public void onPortUpdate(DevicePort port) {
            if (isAdded()) {
                DevicePortProperties properties = port.getPortProperties();
                deviceNameEdit.setText(properties.getName());
            }
        }

        @Override
        public void onPortError(DevicePort port, Error error, @Nullable String errorData) {
            DICommLog.e(TAG, String.format(Locale.US, "Device port error: [%s], data: [%s]", error.getErrorMessage(), errorData));

            if (error == Error.INSECURE_CONNECTION) {
                promptCertificateMismatch();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_device_port, container, false);

        deviceNameEdit = (EditText) rootview.findViewById(R.id.device_name);
        Button setButton = (Button) rootview.findViewById(R.id.btn_set);
        Button getButton = (Button) rootview.findViewById(R.id.btn_get);

        ((CompoundButton) rootview.findViewById(R.id.switchSubscription)).setOnCheckedChangeListener(subscriptionCheckedChangeListener);

        setButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentAppliance.getDevicePort().setDeviceName(deviceNameEdit.getText().toString());
                    }
                }
        );

        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentAppliance.getDevicePort().reloadProperties();
            }
        });

        return rootview;
    }

    @Override
    public void onResume() {
        super.onResume();

        currentAppliance = CurrentApplianceManager.getInstance().getCurrentAppliance();
        if (currentAppliance == null) {
            getFragmentManager().popBackStack();
            return;
        }

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

    private void promptCertificateMismatch() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.prompt_certificate_mismatch_message);
        builder.setNegativeButton(R.string.prompt_certificate_mismatch_reject, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (currentAppliance != null) {
                    rejectNewPinFor(currentAppliance);
                }
            }
        });
        builder.setPositiveButton(R.string.prompt_certificate_mismatch_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (currentAppliance != null) {
                    acceptNewPinFor(currentAppliance);
                }
            }
        });
        builder.show();
    }
}
