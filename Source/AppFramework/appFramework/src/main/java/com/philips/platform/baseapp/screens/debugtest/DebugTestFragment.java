/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/

package com.philips.platform.baseapp.screens.debugtest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.philips.cdp.registration.configuration.Configuration;
import com.philips.platform.appframework.R;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.baseapp.base.AppFrameworkApplication;
import com.philips.platform.baseapp.base.AppFrameworkBaseActivity;
import com.philips.platform.baseapp.base.AppFrameworkBaseFragment;
import com.philips.platform.baseapp.screens.userregistration.UserRegistrationSettingsState;
import com.philips.platform.baseapp.screens.userregistration.UserRegistrationState;
import com.philips.platform.baseapp.screens.utility.Constants;

import java.util.Arrays;
import java.util.List;

/**
 * This fragment if for internal testing of dynamic configuration change of User registration
 */

public class DebugTestFragment extends AppFrameworkBaseFragment {
    public static final String TAG = DebugTestFragment.class.getSimpleName();
    private String configurationType[] = {Configuration.STAGING.getValue(),
            Constants.TESTING, Configuration.DEVELOPMENT.getValue()};
    private List<String> list = Arrays.asList(configurationType);
    private TextView configurationTextView;
    private Spinner spinner;
    private Context context;
    private UserRegistrationState userRegistrationState;
    private static final String APPIDENTITY_APP_STATE = "appidentity.appState";
    private final String AppInfra = "appinfra";


    @Override
    public String getActionbarTitle() {
        return getResources().getString(R.string.RA_DebugScreen_Title);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppFrameworkBaseActivity)getActivity()).updateActionBarIcon(false);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.af_debug_fragment, container, false);
        setUp(view);
        return view;
    }

    private void setUp(final View view) {
        context = getActivity();
        initViews(view);
        setSpinnerAdaptor();
        final int position = list.indexOf(getAppState());
        setSpinnerSelection(position);
        spinner.setOnItemSelectedListener(getSpinnerListener());
        configurationTextView.setTextColor(ContextCompat.getColor(context, R.color.uikit_white));
    }

    @NonNull
    private AdapterView.OnItemSelectedListener getSpinnerListener() {
        return new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(final AdapterView<?> adapter, View v,
                                       final int position, long id) {

                int position1 = list.indexOf(getAppState());
                if (position1 != position) {

                    new AlertDialog.Builder(context, R.style.alertDialogStyle)
                            .setTitle(getString(R.string.RA_Change_Configuration))
                            .setMessage(context.getResources().getString(R.string.RA_change_config_desc))

                            .setPositiveButton(getString(R.string.RA_OK),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            final String configuration = adapter.getItemAtPosition(position).toString();
                                            if (adapter != null && ((TextView) adapter.getChildAt(position)) != null) {
                                                ((TextView) adapter.getChildAt(position)).setTextColor(Color.WHITE);
                                            }
                                            userRegistrationState = new UserRegistrationSettingsState();
                                            userRegistrationState.getUserObject(context).logout(null);
                                            if (configuration.equalsIgnoreCase(Configuration.DEVELOPMENT.getValue())) {
                                                initialiseUserRegistration(Configuration.DEVELOPMENT.getValue());
                                            } else if (configuration.equalsIgnoreCase(Constants.TESTING)) {
                                                initialiseUserRegistration(Constants.TESTING);
                                            } else {
                                                initialiseUserRegistration(Configuration.STAGING.getValue());
                                            }
                                            configurationTextView.setText(configuration);
                                        }
                                    })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
                @Override
                public void onNothingSelected (AdapterView < ? > arg0){
                }
            };
    }

    private void setSpinnerSelection(final int position) {
        if (position >= 0) {
            spinner.setSelection(position);

            configurationTextView.setText(configurationType[position]);
        } else {
            configurationTextView.setText(configurationType[0]);
        }
    }

    private void setSpinnerAdaptor() {
        final ArrayAdapter<String> configType = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, configurationType);
        configType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(configType);
    }

    private void initViews(final View view) {
        spinner = (Spinner) view.findViewById(R.id.spinner);
        configurationTextView = (TextView) view.findViewById(R.id.configuration);
    }

    private void initialiseUserRegistration(final String development) {
        AppInfraInterface appInfra = ((AppFrameworkApplication) getActivity().getApplicationContext()).getAppInfra();
        AppConfigurationInterface appConfigurationInterface = appInfra.getConfigInterface();

        AppConfigurationInterface.AppConfigurationError configError = new
                AppConfigurationInterface.AppConfigurationError();

        appConfigurationInterface.setPropertyForKey(APPIDENTITY_APP_STATE,
                AppInfra, development, configError);
    }

    protected String getAppState() {
        AppInfraInterface appInfra = ((AppFrameworkApplication) getActivity().getApplicationContext()).getAppInfra();
        AppConfigurationInterface appConfigurationInterface = appInfra.getConfigInterface();

        AppConfigurationInterface.AppConfigurationError configError = new
                AppConfigurationInterface.AppConfigurationError();

        return (String) (appConfigurationInterface.getPropertyForKey(APPIDENTITY_APP_STATE,
                AppInfra, configError));
    }
}
