/*
 * Copyright (c) 2018 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.csw.justintime;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.VisibleForTesting;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.philips.platform.csw.CswBaseFragment;
import com.philips.platform.csw.R;
import com.philips.platform.csw.description.DescriptionView;
import com.philips.platform.csw.dialogs.DialogView;
import com.philips.platform.csw.dialogs.ProgressDialogView;
import com.philips.platform.csw.permission.helper.ErrorMessageCreator;
import com.philips.platform.csw.permission.uielement.LinkSpan;
import com.philips.platform.csw.permission.uielement.LinkSpanClickListener;
import com.philips.platform.uid.view.widget.Button;
import com.philips.platform.uid.view.widget.Label;

public class JustInTimeConsentFragment extends CswBaseFragment implements JustInTimeConsentContract.View {
    private ProgressDialogView progressDialogView;
    @LayoutRes
    private int containerId;
    private JustInTimeConsentContract.Presenter presenter;
    private DialogView dialogView;

    public static JustInTimeConsentFragment newInstance(final int containerId) {
        JustInTimeConsentFragment fragment = new JustInTimeConsentFragment();
        fragment.containerId = containerId;
        new JustInTimeConsentPresenter(fragment, JustInTimeConsentDependencies.appInfra, JustInTimeConsentDependencies.consentDefinition, JustInTimeConsentDependencies.completionListener);
        if (fragment.dialogView == null) {
            fragment.dialogView = new DialogView();
        }
        if (fragment.progressDialogView == null) {
            fragment.progressDialogView = new ProgressDialogView();
        }
        return fragment;
    }

    @Override
    public void setPresenter(JustInTimeConsentContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public android.view.View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View justInTimeConsentView = inflater.inflate(R.layout.csw_just_in_time_consent_view, container, false);
        initializeDescriptionLabel(justInTimeConsentView);
        initializeHelpLabel(justInTimeConsentView);
        initializeGiveConsentButton(justInTimeConsentView);
        initializeConsentRejectButton(justInTimeConsentView);
        initializeUserBenefitsDescriptionLabel(justInTimeConsentView);
        initializeUserBenefitsTitleLabel(justInTimeConsentView);

        return justInTimeConsentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.trackPageName();
    }

    @Override
    public int getTitleResourceId() {
        return JustInTimeConsentDependencies.textResources.titleTextRes;
    }

    private void initializeConsentRejectButton(android.view.View justInTimeConsentView) {
        Button rejectConsentButton = justInTimeConsentView.findViewById(R.id.csw_justInTimeView_consentLater_label);
        rejectConsentButton.setText(JustInTimeConsentDependencies.textResources.rejectTextRes);
        rejectConsentButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                presenter.onConsentRejectedButtonClicked();
            }
        });
    }

    private void initializeGiveConsentButton(android.view.View justInTimeConsentView) {
        Button giveConsentButton = justInTimeConsentView.findViewById(R.id.csw_justInTimeView_consentOk_button);
        giveConsentButton.setText(JustInTimeConsentDependencies.textResources.acceptTextRes);
        giveConsentButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                presenter.onConsentGivenButtonClicked();
            }
        });
    }

    private void initializeDescriptionLabel(android.view.View justInTimeConsentView) {
        Label descriptionLabel = justInTimeConsentView.findViewById(R.id.csw_justInTimeView_consentDescription_label);
        descriptionLabel.setText(JustInTimeConsentDependencies.consentDefinition.getText());
    }

    private void initializeUserBenefitsDescriptionLabel(android.view.View justInTimeConsentView) {
        Label descriptionLabel = justInTimeConsentView.findViewById(R.id.csw_justInTimeView_consentUserBenefitsDescription_label);
        if (JustInTimeConsentDependencies.textResources.userBenefitsDescriptionRes != 0) {
            descriptionLabel.setText(JustInTimeConsentDependencies.textResources.userBenefitsDescriptionRes);
        }
    }

    private void initializeUserBenefitsTitleLabel(android.view.View justInTimeConsentView) {
        Label descriptionLabel = justInTimeConsentView.findViewById(R.id.csw_justInTimeView_consentUserBenefitsTile_label);
        if (JustInTimeConsentDependencies.textResources.userBenefitsTitleRes != 0) {
            descriptionLabel.setText(JustInTimeConsentDependencies.textResources.userBenefitsTitleRes);
        }
    }

    private void initializeHelpLabel(android.view.View justInTimeConsentView) {
        Spannable helpLink = new SpannableString(getContext().getString(R.string.mya_Consent_Help_Label));
        helpLink.setSpan(new LinkSpan(new LinkSpanClickListener() {
            @Override
            public void onClick() {
                DescriptionView.show(getFragmentManager(), JustInTimeConsentDependencies.consentDefinition.getHelpText(), containerId);
            }
        }), 0, helpLink.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Label descriptionLabel = justInTimeConsentView.findViewById(R.id.csw_justInTimeView_consentHelplink_button);
        descriptionLabel.setText(helpLink);
    }

    private void showErrorDialog(String errorTitle, String errorMessage) {
        dialogView.showDialog(getActivity(), errorTitle, errorMessage);
    }

    @Override
    public void showErrorDialog(int errorTitleId, int errorMessageId) {
        showErrorDialog(getString(errorTitleId), getString(errorMessageId));
    }

    @Override
    public void showErrorDialogForCode(int errorTitleId, int errorCode) {
        String errorTitle = getContext().getString(errorTitleId);
        String errorMessage = ErrorMessageCreator.getMessageErrorBasedOnErrorCode(getContext(), errorCode);
        showErrorDialog(errorTitle, errorMessage);
    }

    @Override
    public void showProgressDialog() {
        if(progressDialogView != null) {
            progressDialogView.showDialog(getActivity());
        }
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialogView != null && progressDialogView.isDialogShown()) {
            progressDialogView.hideDialog();
        }
    }

    @VisibleForTesting
    protected void setProgressDialogView(ProgressDialogView view) {
        this.progressDialogView = view;
    }

    @VisibleForTesting
    protected void setDialogView(DialogView view) {
        this.dialogView = view;
    }
}
