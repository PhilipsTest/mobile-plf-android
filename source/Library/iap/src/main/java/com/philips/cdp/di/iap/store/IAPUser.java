/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.store;

import android.content.Context;

import com.philips.cdp.registration.User;
import com.philips.cdp.registration.handlers.RefreshLoginSessionHandler;

public class IAPUser {

    private User mJanRainUser;
    private Store mStore;

    public IAPUser(final Context context, final Store store) {
        mStore = store;
        mJanRainUser = new User(context);
    }

    public String getJanRainID() {
        return mJanRainUser.getAccessToken();
    }

    public String getJanRainEmail() {
        return mJanRainUser.getEmail();
    }

    public void refreshLoginSession() {
        mJanRainUser.refreshLoginSession(new RefreshLoginSessionHandler() {
            @Override
            public void onRefreshLoginSessionSuccess() {
                mStore.updateJanRainIDBasedUrls();
            }

            @Override
            public void onRefreshLoginSessionFailedWithError(final int i) {
                mStore.updateJanRainIDBasedUrls();
            }

            @Override
            public void onRefreshLoginSessionInProgress(final String s) {
            }
        });
    }
}