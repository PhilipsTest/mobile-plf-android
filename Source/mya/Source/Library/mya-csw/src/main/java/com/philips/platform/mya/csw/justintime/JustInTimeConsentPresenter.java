package com.philips.platform.mya.csw.justintime;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.mya.csw.R;
import com.philips.platform.pif.chi.ConsentError;
import com.philips.platform.pif.chi.ConsentHandlerInterface;
import com.philips.platform.pif.chi.PostConsentCallback;
import com.philips.platform.pif.chi.datamodel.Consent;
import com.philips.platform.pif.chi.datamodel.ConsentDefinition;

public class JustInTimeConsentPresenter implements JustInTimeConsentContract.Presenter {
    private final JustInTimeConsentContract.View view;
    private final ConsentHandlerInterface consentHandlerInterface;
    private final ConsentDefinition consentDefinition;
    private final JustInTimeWidgetHandler completionListener;
    private AppInfraInterface appInfra;

    public JustInTimeConsentPresenter(JustInTimeConsentContract.View view, AppInfraInterface appInfra, ConsentHandlerInterface consentHandlerInterface, ConsentDefinition consentDefinition, JustInTimeWidgetHandler completionListener) {
        this.view = view;
        this.appInfra = appInfra;
        this.view.setPresenter(this);
        this.consentHandlerInterface = consentHandlerInterface;
        this.consentDefinition = consentDefinition;
        this.completionListener = completionListener;
    }

    @Override
    public void onConsentGivenButtonClicked() {
        postConsent(true, new JustInTimePostConsentCallback(new PostConsentSuccessHandler() {
            @Override
            public void onSuccess() {
                if (completionListener != null) {
                    completionListener.onConsentGiven();
                }
            }
        }));
    }

    @Override
    public void onConsentRejectedButtonClicked() {
        postConsent(false, new JustInTimePostConsentCallback(new PostConsentSuccessHandler() {
            @Override
            public void onSuccess() {
                if (completionListener != null) {
                    completionListener.onConsentRejected();
                }
            }
        }));
    }

    private void postConsent(boolean status, PostConsentCallback callback) {
        boolean isOnline = appInfra.getRestClient().isInternetReachable();
        if (isOnline) {
            view.showProgressDialog();
            consentHandlerInterface.storeConsentState(consentDefinition, status, callback);
        } else {
            view.showErrorDialog(R.string.csw_offline_title, R.string.csw_offline_message);
        }
    }

    class JustInTimePostConsentCallback implements PostConsentCallback {

        private final JustInTimeConsentPresenter.PostConsentSuccessHandler handler;

        public JustInTimePostConsentCallback(JustInTimeConsentPresenter.PostConsentSuccessHandler handler) {
            this.handler = handler;
        }

        @Override
        public void onPostConsentFailed(ConsentDefinition definition, ConsentError error) {
            view.hideProgressDialog();
            view.showErrorDialogForCode(R.string.csw_problem_occurred_error_title, error.getErrorCode());
        }

        @Override
        public void onPostConsentSuccess(Consent consent) {
            view.hideProgressDialog();
            handler.onSuccess();
        }
    }

    interface PostConsentSuccessHandler {
        void onSuccess();
    }
}