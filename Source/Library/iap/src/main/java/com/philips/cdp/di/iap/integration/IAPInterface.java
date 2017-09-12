/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.integration;

import android.content.Context;
import android.net.ConnectivityManager;

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.iapHandler.IAPExposedAPI;
import com.philips.cdp.di.iap.utils.NetworkUtility;
import com.philips.cdp.registration.User;
import com.philips.platform.uappframework.UappInterface;
import com.philips.platform.uappframework.launcher.UiLauncher;
import com.philips.platform.uappframework.uappinput.UappDependencies;
import com.philips.platform.uappframework.uappinput.UappLaunchInput;
import com.philips.platform.uappframework.uappinput.UappSettings;

public class IAPInterface implements UappInterface, IAPExposedAPI {
    protected IAPHandler mIAPHandler;
    protected IAPSettings mIAPSettings;
    private User mUser;
    private IAPServiceDiscoveryWrapper mIapServiceDiscoveryWrapper;

    @Override
    public void init(UappDependencies uappDependencies, UappSettings uappSettings) {
        IAPDependencies mIAPDependencies = (IAPDependencies) uappDependencies;
        mIAPSettings = (IAPSettings) uappSettings;
        mIAPHandler = new IAPHandler(mIAPDependencies, mIAPSettings);
        mIAPHandler.initPreRequisite();
        // mIAPHandler.initIAPRequisite();
        mIapServiceDiscoveryWrapper = new IAPServiceDiscoveryWrapper(mIAPSettings);
        // mIapServiceDiscoveryWrapper.initializeStoreFromServiceDiscoveryResponse(mIAPHandler);
    }

    @Override
    public void launch(UiLauncher uiLauncher, UappLaunchInput uappLaunchInput) throws RuntimeException {
        mUser = new User(mIAPSettings.getContext());// User can be inject as dependencies
        if (mUser.isUserSignIn()) {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) mIAPSettings.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (!NetworkUtility.getInstance().isNetworkAvailable(connectivityManager)) {
                throw new RuntimeException(mIAPSettings.getContext().getString(R.string.iap_no_internet));// Confirm the behaviour on error Callback
            }
            mIapServiceDiscoveryWrapper.getLocaleFromServiceDiscovery(uiLauncher, mIAPHandler, (IAPLaunchInput) uappLaunchInput, null, null);
        } else {
            throw new RuntimeException("User is not logged in.");// Confirm the behaviour on error Callback
        }
    }

    @Override
    public void getProductCartCount(IAPListener iapListener) {
        mUser = new User(mIAPSettings.getContext());
        if (mUser.isUserSignIn())
            mIapServiceDiscoveryWrapper.getLocaleFromServiceDiscovery(null, mIAPHandler, null, iapListener, "productCartCount");
        else throw new RuntimeException("User is not logged in.");
    }

    @Override
    public void getCompleteProductList(IAPListener iapListener) {
        mUser = new User(mIAPSettings.getContext());
        if (mUser.isUserSignIn()) {
            mIapServiceDiscoveryWrapper.getLocaleFromServiceDiscovery(null, mIAPHandler, null, iapListener, "completeProductList");
        } else throw new RuntimeException("User is not logged in.");
    }

    @Override
    public boolean isCartVisible(IAPListener iapListener) {
        mUser = new User(mIAPSettings.getContext());
        if (mUser.isUserSignIn()) {
            return mIAPHandler != null && mIapServiceDiscoveryWrapper.getCartVisiblityByConfigUrl(iapListener, mIAPHandler);
        } else throw new RuntimeException("User is not logged in.");
    }
}
