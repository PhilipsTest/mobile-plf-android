/**
 * Copyright (c) Koninklijke Philips N.V., 2017.
 * All rights reserved.
 */
package com.philips.platform.ews.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.philips.platform.ews.R;
import com.philips.platform.ews.dialog.EWSAlertDialogFragment;
import com.philips.platform.ews.injections.DaggerEWSComponent;
import com.philips.platform.ews.injections.DependencyHelper;
import com.philips.platform.ews.injections.EWSComponent;
import com.philips.platform.ews.injections.EWSConfigurationModule;
import com.philips.platform.ews.injections.EWSDependencyProviderModule;
import com.philips.platform.ews.injections.EWSModule;
import com.philips.platform.ews.microapp.EWSActionBarListener;
import com.philips.platform.ews.microapp.EWSLauncherInput;
import com.philips.platform.ews.microapp.EwsResultListener;
import com.philips.platform.ews.tagging.EWSTagger;
import com.philips.platform.ews.tagging.Page;
import com.philips.platform.uappframework.listener.ActionBarListener;
import com.philips.platform.uappframework.listener.BackEventListener;
import com.philips.platform.uid.thememanager.UIDHelper;
import com.philips.platform.uid.utils.DialogConstants;
import com.philips.platform.uid.view.widget.AlertDialogFragment;
import com.philips.platform.uid.view.widget.Button;

public abstract class BaseFragment extends Fragment implements BackEventListener {

    @NonNull
    private EWSComponent ewsComponent;
    @NonNull
    private int deviceName;
    @NonNull
    private EWSTagger ewsTagger;
    @NonNull
    private EWSConfigurationModule ewsConfigurationModule;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!DependencyHelper.areDependenciesInitialized()) {
            this.getActivity().finish();
            return;
        }
        EWSDependencyProviderModule ewsDependencyProviderModule = new EWSDependencyProviderModule(DependencyHelper.getAppInfraInterface(), DependencyHelper.getProductKeyMap());
        ewsConfigurationModule = new EWSConfigurationModule(this.getActivity(), DependencyHelper.getContentConfiguration());
        deviceName = ewsConfigurationModule.provideBaseContentConfiguration().getDeviceName();
        ewsTagger = ewsDependencyProviderModule.provideEWSTagger();
        ewsComponent = DaggerEWSComponent.builder()
                .eWSModule(new EWSModule(this.getActivity()
                        , this.getActivity().getSupportFragmentManager()
                        , EWSLauncherInput.getContainerFrameId(), DependencyHelper.getCommCentral()))
                .eWSConfigurationModule(ewsConfigurationModule)
                .eWSDependencyProviderModule(ewsDependencyProviderModule)
                .build();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EWSActionBarListener actionBarListener = null;
        try {
            actionBarListener = ((EWSActionBarListener) getContext());
        } catch (ClassCastException e) {}

        if (actionBarListener != null) {
            actionBarListener.closeButton(true);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        setToolbarTitle();
        if (getChildFragmentManager().getFragments().isEmpty()) {
            callTrackPageName();
        }
    }

    public void handleCancelButtonClicked() {
        showCancelDialog(deviceName, ewsTagger);
    }

    @VisibleForTesting
    void showCancelDialog(@StringRes int deviceName, @NonNull final EWSTagger ewsTagger) {
        Context context = getContext();
        View view = LayoutInflater.from(context).cloneInContext(UIDHelper.getPopupThemedContext(context)).inflate(R.layout.cancel_setup_dialog,
                null, false);

        AlertDialogFragment.Builder builder = new AlertDialogFragment.Builder(context)
                .setDialogView(view)
                .setDialogType(DialogConstants.TYPE_DIALOG)
                .setDimLayer(DialogConstants.DIM_STRONG)
                .setCancelable(false);

        final EWSAlertDialogFragment alertDialogFragment = (EWSAlertDialogFragment) builder.create(new EWSAlertDialogFragment());
        alertDialogFragment.setDialogLifeCycleListener(new EWSAlertDialogFragment.DialogLifeCycleListener() {
            @Override
            public void onStart() {
                ewsTagger.trackPage(Page.CANCEL_WIFI_SETUP);
            }
        });

        alertDialogFragment.show(getChildFragmentManager(), AlertDialogFragment.class.getCanonicalName());

        Button yesButton = view.findViewById(R.id.ews_04_02_button_cancel_setup_yes);
        Button noButton = view.findViewById(R.id.ews_04_02_button_cancel_setup_no);
        ((TextView) view.findViewById(R.id.ews_verify_device_body)).setText(getString(R.string.label_ews_cancel_setup_body, getString(deviceName)));
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EwsResultListener ewsResultListener = ewsConfigurationModule.provideBaseContentConfiguration().getEwsResultListener();
                if(ewsResultListener == null) {
                    try {
                        ewsResultListener = ((EwsResultListener) getContext());
                    } catch (ClassCastException ignored) {
                    }
                    if (ewsResultListener != null) {
                        ewsResultListener.onEWSCancelled();
                    }
                    getActivity().finish();
                } else {
                    ewsResultListener.onEWSCancelled();
                }
            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callTrackPageName();
                alertDialogFragment.dismiss();
            }
        });
    }

    @Override
    public boolean handleBackEvent() {
        return false;
    }

    public void setToolbarTitle() {
        ActionBarListener actionBarListener = null;
        try {
            actionBarListener = ((ActionBarListener) getContext());
        } catch (ClassCastException e) {}

        if (actionBarListener != null) {
            actionBarListener.updateActionBar(R.string.ews_title, true);
        }
    }

    protected abstract void callTrackPageName();

    public EWSComponent getEWSComponent() {
        return ewsComponent;
    }

}
