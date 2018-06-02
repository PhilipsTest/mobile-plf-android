package com.philips.cdp.registration.ui.social;

interface MergeAccountContract {

    void connectionStatus(boolean isOnline);

    void mergeStatus(boolean isOnline);

    void mergeSuccess();

    void mergeFailure(int errorCode);

    void mergePasswordFailure();
}
