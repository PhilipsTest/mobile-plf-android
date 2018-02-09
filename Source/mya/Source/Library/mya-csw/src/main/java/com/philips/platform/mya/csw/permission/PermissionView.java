/*
 * Copyright (c) 2017 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.mya.csw.permission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.philips.platform.appinfra.rest.RestInterface;
import com.philips.platform.mya.catk.ConsentAccessToolKit;
import com.philips.platform.mya.catk.ConsentInteractor;
import com.philips.platform.mya.chi.ConsentConfiguration;
import com.philips.platform.mya.chi.datamodel.ConsentDefinition;
import com.philips.platform.mya.csw.CswBaseFragment;
import com.philips.platform.mya.csw.CswInterface;
import com.philips.platform.mya.csw.R;
import com.philips.platform.mya.csw.R2;
import com.philips.platform.mya.csw.description.DescriptionView;
import com.philips.platform.mya.csw.dialogs.DialogView;
import com.philips.platform.mya.csw.justintime.JustInTimeFragmentWidget;
import com.philips.platform.mya.csw.justintime.JustInTimeTextResources;
import com.philips.platform.mya.csw.justintime.JustInTimeWidgetHandler;
import com.philips.platform.mya.csw.permission.adapter.PermissionAdapter;
import com.philips.platform.mya.csw.permission.uielement.LinkSpanClickListener;
import com.philips.platform.mya.csw.utils.CswLogger;
import com.philips.platform.uid.view.widget.RecyclerViewSeparatorItemDecoration;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PermissionView extends CswBaseFragment implements PermissionInterface, HelpClickListener, View.OnClickListener {

    public static final String TAG = "PermissionView";
    private ProgressDialog mProgressDialog;

    private Unbinder unbinder;

    @BindView(R2.id.consentsRecycler)
    RecyclerView recyclerView;

    private RecyclerViewSeparatorItemDecoration separatorItemDecoration;
    private List<ConsentConfiguration> configs;
    private PermissionAdapter adapter;

    @Override
    protected void setViewParams(Configuration config, int width) {
    }

    @Override
    protected void handleOrientation(View view) {
        handleOrientationOnView(view);
    }

    @Override
    public int getTitleResourceId() {
        return R.string.csw_privacy_settings;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.csw_permission_view, container, false);
        unbinder = ButterKnife.bind(this, view);

        configs = CswInterface.getCswComponent().getConsentConfigurations();
        if (configs == null) {
            configs = new ArrayList<>();
        }

        handleOrientation(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getRestClient().isInternetReachable()) {
            PermissionPresenter presenter = getPermissionPresenter();
            presenter.getConsentStatus();
        } else {
            showErrorDialog(true, getString(R.string.csw_offline_title), getString(R.string.csw_offline_message));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbindView();
    }

    private void unbindView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new PermissionAdapter(new ArrayList<ConsentView>(), this);
        adapter = new PermissionAdapter(createConsentsList(), this);
        adapter.setPrivacyNoticeClickListener(new LinkSpanClickListener() {
            @Override
            public void onClick() {
                onPrivacyNoticeClicked();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        separatorItemDecoration = new RecyclerViewSeparatorItemDecoration(getContext());
        recyclerView.addItemDecoration(separatorItemDecoration);
        recyclerView.setAdapter(adapter);
    }

    private void onPrivacyNoticeClicked() {
        ConsentDefinition consentDefinition = new ConsentDefinition("Receive promotional communications of Philips based on my preferences and online bahavior.", "consentHelpText",
                Collections.singletonList("moment"), 1, Locale.US);
        ConsentInteractor consentHandlerInterface = new ConsentInteractor(ConsentAccessToolKit.getInstance());
        JustInTimeTextResources textResources = new JustInTimeTextResources();
        textResources.rejectTextRes = R.string.mya_csw_justintime_reject;
        textResources.acceptTextRes = R.string.mya_csw_justintime_accept;
        textResources.titleTextRes = R.string.mya_csw_justintime_title;
        JustInTimeFragmentWidget justInTimeFragmentWidget = JustInTimeFragmentWidget.newInstance(consentDefinition, consentHandlerInterface, textResources, R.id.permissionView);
        justInTimeFragmentWidget.setCompletionListener(new JustInTimeWidgetHandler() {
            @Override
            public void onConsentGiven() {
                openPrivacyNotice();
            }

            @Override
            public void onConsentRejected() {

            }
        });

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.permissionView, justInTimeFragmentWidget, justInTimeFragmentWidget.TAG);
        fragmentTransaction.addToBackStack(justInTimeFragmentWidget.TAG);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void showProgressDialog() {
        if (!(getActivity().isFinishing())) {
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(getActivity(), R.style.reg_Custom_loaderTheme);
                mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
                mProgressDialog.setCancelable(false);
            }
            mProgressDialog.show();
        }
    }

    @Override
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    @Override
    public void showErrorDialog(boolean goBack, String title, String message) {
        CswLogger.e(TAG, message);
        DialogView dialogView = getDialogView(goBack);
        dialogView.showDialog(getActivity(), title, message);
    }

    @Override
    public void onHelpClicked(String helpText) {
        DescriptionView.show(getFragmentManager(), helpText, R.id.permissionView);
    }

    @Override
    public void onClick(View view) {
        getFragmentManager().popBackStack();
    }

    @VisibleForTesting
    protected RestInterface getRestClient() {
        return CswInterface.get().getDependencies().getAppInfra().getRestClient();
    }

    @VisibleForTesting
    protected PermissionPresenter getPermissionPresenter() {
        PermissionPresenter permissionPresenter = new PermissionPresenter(this, configs, adapter);
        permissionPresenter.mContext = getContext();
        return permissionPresenter;
    }

    @NonNull
    private DialogView getDialogView(boolean goBack) {
        DialogView dialogView = new DialogView();
        if (goBack) {
            dialogView = new DialogView(this);
        }
        return dialogView;
    }

    private List<ConsentView> createConsentsList() {
        final List<ConsentView> consentViewList = new ArrayList<>();
        for (ConsentConfiguration configuration : configs) {
            for (final ConsentDefinition definition : configuration.getConsentDefinitionList()) {
                consentViewList.add(new ConsentView(definition, configuration.getHandlerInterface()));
            }
        }
        return consentViewList;
    }

    private void openPrivacyNotice() {
        boolean isOnline = getRestClient().isInternetReachable();
        if (isOnline) {
            PermissionHelper.getInstance().getMyAccountUIEventListener().onPrivacyNoticeClicked();
        } else {
            showErrorDialog(false, getString(R.string.csw_offline_title), getString(R.string.csw_offline_message));
        }
    }
}
