/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.platform.appframework.connectivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.philips.cdp.dicommclient.request.Error;
import com.philips.cdp.registration.User;
import com.philips.cdp2.commlib.core.exception.MissingPermissionException;
import com.philips.platform.appframework.AbstractConnectivityBaseFragment;
import com.philips.platform.appframework.R;
import com.philips.platform.baseapp.base.AppFrameworkApplication;
import com.philips.platform.baseapp.screens.utility.RALog;

import java.lang.ref.WeakReference;

import static com.philips.platform.baseapp.screens.utility.Constants.DEVICE_DATAPARSING;

public class ConnectivityFragment extends AbstractConnectivityBaseFragment implements View.OnClickListener, ConnectivityContract.View {
    public static final String TAG = ConnectivityFragment.class.getSimpleName();
    private EditText editText = null;
    private EditText momentValueEditText = null;
    private ProgressDialog dialog = null;
    private TextView connectionState;
    private Handler handler = new Handler();
    private WeakReference<ConnectivityFragment> connectivityFragmentWeakReference;
    private Context mContext;

    /**
     * Presenter object for Connectivity
     */
    private ConnectivityPresenter connectivityPresenter;

    public ConnectivityFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public String getActionbarTitle() {
        return getResources().getString(R.string.RA_ConnectivityScreen_Menu_Title);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        RALog.d(TAG, "Connectivity Fragment Oncreate");
        super.onCreate(savedInstanceState);
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        if (!(savedInstanceState != null && !AppFrameworkApplication.isAppDataInitialized())) {
            connectivityFragmentWeakReference = new WeakReference<>(this);
            mBluetoothAdapter = getBluetoothAdapter();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        connectivityPresenter = getConnectivityPresenter();
        View rootView = inflater.inflate(R.layout.af_connectivity_fragment, container, false);
        rootView.setOnTouchListener((v, event) -> {
            ConnectivityUtils.hideSoftKeyboard(getActivity());
            return false;
        });
        editText = rootView.findViewById(R.id.measurement_value_editbox);
        momentValueEditText = rootView.findViewById(R.id.moment_value_editbox);
        Button btnGetMoment = rootView.findViewById(R.id.get_momentumvalue_button);
        btnGetMoment.setOnClickListener(this);
        Button btnStartConnectivity = rootView.findViewById(R.id.start_connectivity_button);
        btnStartConnectivity.setOnClickListener(this);
        connectionState = rootView.findViewById(R.id.connectionState);
        mCommCentral = getCommCentral();
        startAppTagging(TAG);
        return rootView;
    }

    protected ConnectivityPresenter getConnectivityPresenter() {
        return new ConnectivityPresenter(this, new User(getActivity().getApplicationContext()), getActivity());
    }

    @Override
    public void onClick(final View v) {
        ConnectivityUtils.hideSoftKeyboard(getActivity());
        switch (v.getId()) {
            case R.id.start_connectivity_button:
                launchBluetoothActivity();
                break;
            case R.id.get_momentumvalue_button:
                connectivityPresenter.processMoment(editText.getText().toString());
                break;
            default:
        }
    }

    /**
     * Start scanning nearby devices using given strategy
     */
    protected void startDiscovery() {
        RALog.i(TAG, "Ble device discovery started ");
        handler.postDelayed(() -> {
            if (isFragmentLive()) {
                try {
                    bleScanDialogFragment = new BLEScanDialogFragment();
                    bleScanDialogFragment.setSavedApplianceList(mCommCentral.getApplianceManager().getDiscoveredAppliances());
                    bleScanDialogFragment.show(getActivity().getSupportFragmentManager(), "BleScanDialog");
                    bleScanDialogFragment.setBLEDialogListener(bleRefAppliance -> {
                        if (bleRefAppliance == null || bleRefAppliance.getDeviceMeasurementPort() == null) {
                            Toast.makeText(getActivity(), "Reference Node is not valid", Toast.LENGTH_SHORT).show();
                        } else {
                            if (bleRefAppliance.getDeviceMeasurementPort() != null && bleRefAppliance.getDeviceMeasurementPort().getPortProperties() != null) {
                                bleRefAppliance.getDeviceMeasurementPort().reloadProperties();
                            } else {
                                updateConnectionStateText(getString(R.string.RA_Connectivity_Connection_Status_Connected));
                                connectivityPresenter.setUpApplicance(bleRefAppliance);
                                bleRefAppliance.getDeviceMeasurementPort().getPortProperties();
                            }
                        }
                    });

                    mCommCentral.startDiscovery();
                    handler.postDelayed(stopDiscoveryRunnable, STOP_DISCOVERY_TIMEOUT);
                    updateConnectionStateText(getString(R.string.RA_Connectivity_Connection_Status_Disconnected));
                } catch (MissingPermissionException e) {
                    RALog.e(TAG, "Permission missing");
                }
            }
        }, START_DISCOVERY_TIME);
    }

    /**
     * Check if fragment is live
     *
     * @return
     */
    protected boolean isFragmentLive() {
        return connectivityFragmentWeakReference != null && isAdded();
    }

    @Override
    public void onProcessMomentError(String errorText) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        Toast.makeText(mContext, errorText, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProcessMomentSuccess(String momentValue) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (momentValueEditText != null) {
            momentValueEditText.setText(momentValue);
        }
    }

    @Override
    public void onProcessMomentProgress(String message) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = ProgressDialog.show(mContext, "", message);
    }

    @Override
    public void updateDeviceMeasurementValue(final String measurementvalue) {
        if (isFragmentLive()) {
            getActivity().runOnUiThread(() -> {
                try {
                    if (!TextUtils.isEmpty(measurementvalue)) {
                        editText.setText(measurementvalue);
                    } else {
                        editText.setText(Integer.toString(-1));
                    }
                } catch (Exception e) {
                    RALog.d(DEVICE_DATAPARSING,
                            e.getMessage());
                }
            });
        }
    }

    @Override
    public void onDeviceMeasurementError(final Error error, String s) {
        //TODO:Handle device measurement error properly
        if (isFragmentLive()) {
            getActivity().runOnUiThread(() -> Toast.makeText(mContext, "Error while reading measurement from reference board" + error.getErrorMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void updateConnectionStateText(final String text) {
        if (isFragmentLive()) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (connectionState != null) {
                        connectionState.setText(text);
                    }
                });
            }
        }
    }

    @Override
    public void onDestroy() {
        RALog.d(TAG, " Connectivity Fragment Destroyed");
        connectivityFragmentWeakReference = null;
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        ConnectivityUtils.hideSoftKeyboard(getActivity());
        if (handler != null) {
            handler.removeCallbacks(stopDiscoveryRunnable);
            handler.removeCallbacksAndMessages(null);
        }
        mCommCentral = null;
        super.onDestroyView();
    }

}


