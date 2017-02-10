package com.philips.platform.baseapp.screens.dataservices.settings;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.philips.platform.appframework.R;
import com.philips.platform.core.datatypes.Settings;
import com.philips.platform.core.listeners.DBChangeListener;
import com.philips.platform.core.listeners.DBRequestListener;
import com.philips.platform.core.trackers.DataServicesManager;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by sangamesh on 09/01/17.
 */

public class SettingsFragment extends DialogFragment implements DBRequestListener, DBChangeListener, View.OnClickListener {

    private Button mBtnOk;
    private Button mBtnCancel;
    SettingsFragmentPresenter settingsFragmentPresenter;
    private ProgressDialog mProgressDialog;
    private DataServicesManager mDataServicesManager;
    private Spinner mSpinner_Unit, mSpinner_Local;
    private Settings settings;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dialog_settings, container,
                false);

        mDataServicesManager = DataServicesManager.getInstance();
        mBtnOk = (Button) rootView.findViewById(R.id.btnOK);
        mBtnOk.setOnClickListener(this);
        mBtnCancel = (Button) rootView.findViewById(R.id.btnCancel);
        mBtnCancel.setOnClickListener(this);
        settingsFragmentPresenter = new SettingsFragmentPresenter(getActivity(), this);
        mProgressDialog = new ProgressDialog(getActivity());
        mDataServicesManager.registerDBChangeListener(this);

        mSpinner_Unit = (Spinner) rootView.findViewById(R.id.spinner_metrics);
        mSpinner_Local = (Spinner) rootView.findViewById(R.id.spinner_locale);
        ArrayAdapter<CharSequence> adapterMetrics = ArrayAdapter.createFromResource(getActivity(),
                R.array.metrics, android.R.layout.simple_spinner_item);
        adapterMetrics.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner_Unit.setAdapter(adapterMetrics);

        ArrayAdapter<CharSequence> adapterLocale = ArrayAdapter.createFromResource(getActivity(),
                R.array.locals, android.R.layout.simple_spinner_item);
        adapterLocale.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner_Local.setAdapter(adapterLocale);

        fetchSettings();
        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSuccess(final ArrayList<? extends Object> ormObjectList) {


    }

    private void updateUi(String unit, String locale) {

        mSpinner_Unit.setSelection(Arrays.asList(getResources().getStringArray(R.array.metrics)).indexOf(unit));
        mSpinner_Local.setSelection(Arrays.asList(getResources().getStringArray(R.array.locals)).indexOf(locale));

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onSuccess(final Object data) {

        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (data != null) {
                        settings = (Settings) data;
                        updateUi(settings.getUnit(), settings.getLocale());

                    }
                    dismissProgressDialog();
                }
            });
        }
    }

    @Override
    public void onFailure(final Exception exception) {

        if (getActivity() != null) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dismissProgressDialog();
                    Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOK:

                settings.setUnit(mSpinner_Unit.getSelectedItem().toString());
                settings.setLocale(mSpinner_Local.getSelectedItem().toString());
                settingsFragmentPresenter.updateSettings(settings);

                dismissConsentDialog(getDialog());
                break;
            case R.id.btnCancel:
                dismissConsentDialog(getDialog());
                break;

        }
    }

    private void dismissConsentDialog(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        DataServicesManager.getInstance().unRegisterDBChangeListener();
        dismissProgressDialog();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        dialog.setTitle(R.string.settings);
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void fetchSettings() {
        showProgressDialog();
        DataServicesManager.getInstance().fetchSettings(this);
    }

    @Override
    public void dBChangeSuccess() {
        DataServicesManager.getInstance().fetchSettings(this);
    }

    @Override
    public void dBChangeFailed(final Exception e) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), "Exception :" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
