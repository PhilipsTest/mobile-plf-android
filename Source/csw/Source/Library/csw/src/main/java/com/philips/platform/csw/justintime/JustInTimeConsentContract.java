package com.philips.platform.csw.justintime;

public interface JustInTimeConsentContract {
    interface View {
        void setPresenter(Presenter presenter);

        void showErrorDialog(int errorTitleId, int errorMessageId);

        void showErrorDialogForCode(int errorTitleId, int errorCode);

        void showProgressDialog();

        void hideProgressDialog();
    }

    interface Presenter {
        void onConsentGivenButtonClicked();

        void onConsentRejectedButtonClicked();

        void trackPageName();
    }
}
