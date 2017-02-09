/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.store;

import android.content.Context;

import com.philips.cdp.di.iap.integration.IAPSettings;

import org.mockito.Mockito;

import static org.mockito.Mockito.when;

public class MockStore {

    private Context mContext;
    private IAPUser mUser;

    public MockStore(Context context, IAPUser user) {
        mContext = context;
        mUser = user;
        if (context == null) {
            mContext = Mockito.mock(Context.class);
        }
        if (user == null) {
            mUser = Mockito.mock(IAPUser.class);
        }
        when(mUser.getJanRainEmail()).thenReturn(NetworkURLConstants.JANRAIN_EMAIL);
        when(mUser.getJanRainID()).thenReturn(NetworkURLConstants.JANRAIN_ID);
    }

    public StoreListener getStore(IAPSettings pIAPSettings) {
        HybrisStore hybrisStore = new HybrisStore(mContext, pIAPSettings) {
            @Override
            protected StoreConfiguration getStoreConfig(final Context context, final IAPSettings mIapSettings) {
                return getStoreConfiguration(this, mIapSettings);
            }

            @Override
            IAPUser createUser(final Context context) {
                return mUser;
            }

            @Override
            public String getCountry() {
                return "US";
            }
        };
        hybrisStore.createNewUser(mContext);
        return hybrisStore;
    }

    private StoreConfiguration getStoreConfiguration(HybrisStore hybrisStore, IAPSettings pIapSettings) {
        return new StoreConfiguration(mContext, hybrisStore, pIapSettings) {

            @Override
            StoreController getWebStoreConfig(final Context context) {
                return new MockStoreController(mContext, this);
            }
        };
    }
}
