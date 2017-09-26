/* Copyright (c) Koninklijke Philips N.V., 2017
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.platform.appframework.connectivitypowersleep;


import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.philips.cdp.dicommclient.request.Error;
import com.philips.cdp2.commlib.core.exception.MissingPermissionException;
import com.philips.platform.appframework.ConnectivityBaseFragment;
import com.philips.platform.appframework.ConnectivityDeviceType;
import com.philips.platform.appframework.R;
import com.philips.platform.appframework.connectivity.BLEScanDialogFragment;
import com.philips.platform.appframework.connectivity.ConnectivityUtils;
import com.philips.platform.appframework.connectivity.appliance.BleReferenceAppliance;
import com.philips.platform.baseapp.base.AbstractAppFrameworkBaseActivity;
import com.philips.platform.baseapp.base.UIView;
import com.philips.platform.baseapp.screens.utility.RALog;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class PowerSleepConnectivityFragment extends ConnectivityBaseFragment implements View.OnClickListener, ConnectivityPowerSleepContract.View, UIView {
    public static final String TAG = PowerSleepConnectivityFragment.class.getSimpleName();
    private ProgressDialog dialog = null;
    private Handler handler = new Handler();
    private WeakReference<PowerSleepConnectivityFragment> connectivityFragmentWeakReference;
    private Context mContext;
    private SleepScoreProgressView sleepScoreProgressView;
    private TextView sleepTime, deepSleepTime, sleepPercentageScore, syncUpdated;

    private Button insights;

    /**
     * Presenter object for Connectivity
     */
    private PowerSleepConnectivityPresenter connectivityPresenter;

    private final String BLE_SCAN_DIALOG_TAG = "BleScanDialog";
    private final String SLEEP_PROGRESS_VIEW_PROPERTY = "scoreAngle";
    private final int PROGRESS_SCORE_MAX = 360;
    private final int PROGRESS_PERCENTAGE_MAX = 100;
    private final int IDEAL_DEEP_SLEEP_TIME = 120;
    private final int PROGRESS_DRAW_TIME = 1500;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public String getActionbarTitle() {
        return getResources().getString(R.string.RA_DLS_power_sleep_connectivity);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AbstractAppFrameworkBaseActivity) getActivity()).updateActionBarIcon(false);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        RALog.d(TAG, "Connectivity Fragment Oncreate");
        super.onCreate(savedInstanceState);
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        connectivityFragmentWeakReference = new WeakReference<PowerSleepConnectivityFragment>(this);

        mBluetoothAdapter = getBluetoothAdapter();

    }

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            return view;
        }
        connectivityPresenter = getConnectivityPresenter();
        view = inflater.inflate(R.layout.overview_power_sleep, container, false);
        sleepScoreProgressView = (SleepScoreProgressView) view.findViewById(R.id.arc_progress);
        sleepTime = (TextView) view.findViewById(R.id.sleep_time_value);
        deepSleepTime = (TextView) view.findViewById(R.id.deep_sleep_time_value);
        insights = (Button) view.findViewById(R.id.insights);
        sleepPercentageScore = (TextView) view.findViewById(R.id.sleepoverview_score);
        syncUpdated = (TextView) view.findViewById(R.id.powersleep_updated);
        view.findViewById(R.id.powersleep_sync).setOnClickListener(this);
        insights.setOnClickListener(this);
        insights.setEnabled(false);
        insights.setAlpha(0.5f);
        mCommCentral = getCommCentral(ConnectivityDeviceType.POWER_SLEEP);
        setHasOptionsMenu(true);
        startAppTagging(TAG);
        return view;
    }

    protected PowerSleepConnectivityPresenter getConnectivityPresenter() {
        return new PowerSleepConnectivityPresenter(getActivity(), this, this);
    }

    @Override
    public void onClick(final View v) {
        ConnectivityUtils.hideSoftKeyboard(getActivity());
        switch (v.getId()) {
            case R.id.powersleep_sync:
                launchBluetoothActivity();
                break;
            case R.id.insights:
                connectivityPresenter.onEvent(R.id.insights);
                break;
        }
    }

    private BleReferenceAppliance bleReferenceAppliance = null;


    /**
     * Start scanning nearby devices using given strategy
     */
    protected void startDiscovery() {

        if (bleReferenceAppliance != null) {
            fetchData(bleReferenceAppliance);
            return;
        }

        RALog.i(TAG, "Ble device discovery started ");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFragmentLive()) {
                    try {
                        bleScanDialogFragment = new BLEScanDialogFragment();
                        bleScanDialogFragment.setSavedApplianceList(mCommCentral.getApplianceManager().getAvailableAppliances());
                        bleScanDialogFragment.show(getActivity().getSupportFragmentManager(), BLE_SCAN_DIALOG_TAG);
                        bleScanDialogFragment.setCancelable(false);
                        bleScanDialogFragment.setBLEDialogListener(new BLEScanDialogFragment.BLEScanDialogListener() {
                            @Override
                            public void onDeviceSelected(final BleReferenceAppliance bleRefAppliance) {
                                bleReferenceAppliance = bleRefAppliance;
                                connectivityPresenter.setUpApplicance(bleRefAppliance);

                                fetchData(bleReferenceAppliance);
                            }
                        });

                        mCommCentral.startDiscovery();
                        handler.postDelayed(stopDiscoveryRunnable, STOP_DISCOVERY_TIMEOUT);
                    } catch (MissingPermissionException e) {
                        RALog.e(TAG, "Permission missing");
                    }
                }
            }
        }, START_DISCOVERY_TIME);

    }

    protected void showProgressBar() {
        dialog = ProgressDialog.show(mContext, "", getString(R.string.RA_DLS_data_fetch_wait));
        dialog.setCancelable(false);
    }

    private void fetchData(BleReferenceAppliance bleRefAppliance) {
        showProgressBar();
        bleRefAppliance.getSessionDataPort().reloadProperties();
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
    public void updateSessionData(long sleepTime, long numberOfInteruptions, long deepSleepTime) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        RALog.d(TAG, "Session data updated");
        insights.setEnabled(true);
        insights.setAlpha(1.0f);
        this.sleepTime.setText(getString(R.string.label_sleep_time_format, TimeUnit.MILLISECONDS.toMinutes(sleepTime)));
        this.deepSleepTime.setText(getString(R.string.label_sleep_time_format, TimeUnit.MILLISECONDS.toMinutes(deepSleepTime)));
        setLastSyncDate();
        setSleepProgressPercentage(TimeUnit.MILLISECONDS.toMinutes(deepSleepTime));
    }

    @Override
    public void showError(Error error, String s) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        RALog.d(TAG, "Device Error : " + error.getErrorMessage() + ", "+s);
        Toast.makeText(getContext(), getString(R.string.RA_DLS_data_fetch_error), Toast.LENGTH_LONG).show();
    }

    private void setLastSyncDate() {
        SimpleDateFormat syncFormat = new SimpleDateFormat();
        syncUpdated.setText(getString(R.string.label_last_synced, syncFormat.format(new Date(System.currentTimeMillis()))));
    }

    private void setSleepProgressPercentage(long targetScore) {
        int percentage = calculatePercentage(targetScore);
        this.sleepPercentageScore.setText(String.valueOf(percentage));
        targetScore = (PROGRESS_SCORE_MAX * percentage) / PROGRESS_PERCENTAGE_MAX;
        final ObjectAnimator scoreAnim = ObjectAnimator.ofFloat(sleepScoreProgressView, SLEEP_PROGRESS_VIEW_PROPERTY, 0, targetScore);
        scoreAnim.setInterpolator(new AccelerateInterpolator());
        scoreAnim.setDuration(PROGRESS_DRAW_TIME);
        scoreAnim.start();
    }

    private int calculatePercentage(long targetScore) {
        return (int) (targetScore * PROGRESS_PERCENTAGE_MAX) / IDEAL_DEEP_SLEEP_TIME;
    }

    @Override
    public void onDestroy() {
        RALog.d(TAG, " Connectivity Fragment Destroyed");
        connectivityFragmentWeakReference = null;
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        //ConnectivityUtils.hideSoftKeyboard(getActivity());
        removeApplianceListener();
        connectivityPresenter.removeSessionPortListener(bleReferenceAppliance);
        if (handler != null) {
            handler.removeCallbacks(stopDiscoveryRunnable);
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroyView();
    }

    protected void removeApplianceListener() {
        if (mCommCentral != null && mCommCentral.getApplianceManager() != null) {
            mCommCentral.getApplianceManager().removeApplianceListener(this.applianceListener);
        }
    }

    @Override
    public FragmentActivity getFragmentActivity() {
        return getActivity();
    }
}
