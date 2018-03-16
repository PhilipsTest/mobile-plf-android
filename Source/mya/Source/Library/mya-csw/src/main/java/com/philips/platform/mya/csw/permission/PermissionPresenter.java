/*
 * Copyright (c) 2017 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.mya.csw.permission;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.view.View;

import com.philips.platform.appinfra.rest.RestInterface;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;
import com.philips.platform.mya.csw.CswInterface;
import com.philips.platform.mya.csw.R;
import com.philips.platform.mya.csw.dialogs.ConfirmDialogView;
import com.philips.platform.mya.csw.dialogs.DialogView;
import com.philips.platform.mya.csw.permission.adapter.PermissionAdapter;
import com.philips.platform.mya.csw.permission.helper.ErrorMessageCreator;
import com.philips.platform.pif.chi.CheckConsentsCallback;
import com.philips.platform.pif.chi.ConsentConfiguration;
import com.philips.platform.pif.chi.ConsentError;
import com.philips.platform.pif.chi.ConsentHandlerInterface;
import com.philips.platform.pif.chi.PostConsentCallback;
import com.philips.platform.pif.chi.datamodel.Consent;
import com.philips.platform.pif.chi.datamodel.ConsentDefinition;
import com.philips.platform.pif.chi.datamodel.ConsentStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class PermissionPresenter implements CheckConsentsCallback, ConsentToggleListener, PostConsentCallback {

    public Context mContext;
    @NonNull
    private final PermissionInterface permissionInterface;
    @NonNull
    private final List<ConsentConfiguration> configurationList;
    @NonNull
    private final PermissionAdapter adapter;

    private static final String CONSENT_TYPE_CLICKSTREAM = "clickstream";

    @Inject
    PermissionPresenter(
            @NonNull final PermissionInterface permissionInterface, @NonNull final List<ConsentConfiguration> configurationList, @NonNull final PermissionAdapter adapter) {
        this.permissionInterface = permissionInterface;
        this.configurationList = configurationList;
        this.adapter = adapter;
        this.adapter.setConsentToggleListener(this);
    }

    @NonNull
    PermissionAdapter getAdapter() {
        return adapter;
    }

    void getConsentStatus() {
        if (!configurationList.isEmpty()) {
            permissionInterface.showProgressDialog();
            for (ConsentConfiguration configuration : configurationList) {
                ConsentHandlerInterface handlerInterface = configuration.getHandlerInterface();
                if (handlerInterface != null) {
                    handlerInterface.fetchConsentStates(configuration.getConsentDefinitionList(), this);
                }
            }
        }
    }

    @Override
    public void onToggledConsent(final ConsentDefinition definition, final ConsentHandlerInterface handler, final boolean consentGiven, final ConsentToggleResponse responseHandler) {
        if(definition.hasRevokeWarningText() && !consentGiven) {
            // User has revoked consent
            ConfirmDialogView dialog = new ConfirmDialogView();
            dialog.setupDialog(
                R.string.mya_csw_consent_revoked_confirm_title,
                definition.getRevokeWarningText(),
                R.string.mya_csw_consent_revoked_confirm_btn_ok,
                R.string.mya_csw_consent_revoked_confirm_btn_cancel
            );
            this.permissionInterface.showConfirmRevokeConsentDialog(dialog, new ConfirmDialogView.ConfirmDialogResultHandler() {
                @Override
                public void onOkClicked() {
                    postConsentChange(definition, handler, false);
                }

                @Override
                public void onCancelClicked() {
                    responseHandler.handleResponse(true);
                }
            });
        }
        else {
            postConsentChange(definition, handler, consentGiven);
        }
    }

    private void postConsentChange(ConsentDefinition definition, ConsentHandlerInterface handler, boolean consentGiven) {
        boolean isOnline = getRestClient().isInternetReachable();
        if (isOnline) {
            handler.storeConsentState(definition, consentGiven, this);
            permissionInterface.showProgressDialog();
        } else {
            permissionInterface.showErrorDialog(false, mContext.getString(R.string.csw_offline_title), mContext.getString(R.string.csw_offline_message));
        }
    }

    @Override
    public void onGetConsentsSuccess(@NonNull List<Consent> consents) {
        List<ConsentView> consentViews = adapter.getConsentViews();
        Map<String, Consent> consentMap = new HashMap<>();
        for (Consent consent : consents) {
            consentMap.put(consent.getType(), consent);
        }
        for (ConsentView consentView : consentViews) {
            Consent consent = consentMap.get(consentView.getType());
            if (consent != null) {
                consentView.storeConsent(consent);
                if (consentView.getType().equals(CONSENT_TYPE_CLICKSTREAM)) {
                    updateClickStream(consentView.isChecked());
                }
            }
        }
        adapter.onGetConsentRetrieved(consentViews);
        permissionInterface.hideProgressDialog();
    }

    @Override
    public void onGetConsentsFailed(ConsentError error) {
        adapter.onGetConsentFailed(error);
        permissionInterface.hideProgressDialog();
        permissionInterface.showErrorDialog(true, mContext.getString(R.string.csw_problem_occurred_error_title), toErrorMessage(error));
    }

    @Override
    public void onPostConsentFailed(ConsentDefinition definition, ConsentError error) {
        adapter.onCreateConsentFailed(definition, error);
        permissionInterface.hideProgressDialog();
        permissionInterface.showErrorDialog(false, mContext.getString(R.string.csw_problem_occurred_error_title), toErrorMessage(error));
    }

    @Override
    public void onPostConsentSuccess(Consent consent) {
        if (consent != null && consent.getType().equals(CONSENT_TYPE_CLICKSTREAM)) {
            updateClickStream(consent.getStatus().name().equals(ConsentStatus.active.name()));
        }
        adapter.onCreateConsentSuccess(consent);
        permissionInterface.hideProgressDialog();
    }

    @VisibleForTesting
    protected RestInterface getRestClient() {
        return CswInterface.get().getDependencies().getAppInfra().getRestClient();
    }

    private void updateClickStream(boolean isActive) {
        if (isActive) {
            CswInterface.getCswComponent().getAppTaggingInterface().setPrivacyConsent(AppTaggingInterface.PrivacyStatus.OPTIN);
        } else {
            CswInterface.getCswComponent().getAppTaggingInterface().setPrivacyConsent(AppTaggingInterface.PrivacyStatus.OPTOUT);
        }
    }

    private String toErrorMessage(ConsentError error) {
        return ErrorMessageCreator.getMessageErrorBasedOnErrorCode(mContext, error.getErrorCode());
    }
}