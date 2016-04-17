/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.store;

import android.content.Context;

import static org.mockito.Mockito.when;

public class MockStore {

    private Context mContext;
    private IAPUser mUser;

    public MockStore(Context context, IAPUser user) {
        mContext = context;
        mUser = user;
        when(user.getJanRainEmail()).thenReturn(NetworkURLConstants.JANRAIN_EMAIL);
        when(user.getJanRainID()).thenReturn(NetworkURLConstants.JANRAIN_ID);
    }

    public Store getStore() {
        return new Store(mContext) {
            @Override
            protected StoreConfiguration getStoreConfig(final Context context) {
                return getStoreConfiguration(this);
            }

            @Override
            IAPUser initIAPUser(final Context context) {
                return mUser;
            }
        };
    }

    private StoreConfiguration getStoreConfiguration(Store store) {
        return new StoreConfiguration(mContext, store) {
            @Override
            VerticalAppConfig getVerticalAppConfig(final Context context) {
                return new MockVerticalAppConfig(mContext);
            }

            @Override
            WebStoreConfig getWebStoreConfig(final Context context) {
                return new MockWebStoreConfig(mContext, this);
            }
        };
    }
}
