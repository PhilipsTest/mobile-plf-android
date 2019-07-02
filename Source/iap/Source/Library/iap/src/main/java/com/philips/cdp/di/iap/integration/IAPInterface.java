
package com.philips.cdp.di.iap.integration;

import android.content.Context;
import android.net.ConnectivityManager;

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.container.CartModelContainer;
import com.philips.cdp.di.iap.iapHandler.IAPExposedAPI;
import com.philips.cdp.di.iap.utils.IAPConstant;
import com.philips.cdp.di.iap.utils.IAPUtility;
import com.philips.cdp.di.iap.utils.NetworkUtility;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState;
import com.philips.platform.uappframework.UappInterface;
import com.philips.platform.uappframework.launcher.UiLauncher;
import com.philips.platform.uappframework.uappinput.UappDependencies;
import com.philips.platform.uappframework.uappinput.UappLaunchInput;
import com.philips.platform.uappframework.uappinput.UappSettings;

import static com.philips.cdp.di.iap.integration.IAPLaunchInput.IAPFlows.IAP_SHOPPING_CART_VIEW;

/**
 * IAPInterface is the public class for any proposition to consume InAppPurchase micro app. Its the starting initialization point.
 * @since 1.0.0
 */
public class IAPInterface implements UappInterface, IAPExposedAPI {
    protected IAPHandler mIAPHandler;
    protected IAPSettings mIAPSettings;
    private IAPServiceDiscoveryWrapper mIapServiceDiscoveryWrapper;
    private UserDataInterface mUserDataInterface;

    /**
     * API to initialize IAP
     *
     * @param uappDependencies pass instance of UappDependencies
     * @param uappSettings     pass instance of UappSettings
     * @since 1.0.0
     */
    @Override
    public void init(UappDependencies uappDependencies, UappSettings uappSettings) {
        IAPDependencies iapDependencies = (IAPDependencies) uappDependencies;
        mUserDataInterface = iapDependencies.getUserDataInterface();
        if (null == mUserDataInterface)
            throw new RuntimeException("UserDataInterface is not injected in IAPDependencies.");
        IAPUtility.getInstance().setUserDataInterface(mUserDataInterface);
        IAPUtility.getInstance().setAppName(uappDependencies.getAppInfra().getAppIdentity().getAppName());
        IAPUtility.getInstance().setLocaleTag(uappDependencies.getAppInfra().getInternationalization().getUILocaleString());
        mIAPSettings = (IAPSettings) uappSettings;
        mIAPHandler = new IAPHandler(iapDependencies, mIAPSettings);
        mIAPHandler.initPreRequisite();
        mIapServiceDiscoveryWrapper = new IAPServiceDiscoveryWrapper(mIAPSettings);
    }

    /**
     * API to launch IAP
     *
     * @param uiLauncher      pass instance of UiLauncher
     * @param uappLaunchInput pass instance of UappLaunchInput
     * @throws RuntimeException
     * @since 1.0.0
     */
    @Override
    public void launch(UiLauncher uiLauncher, UappLaunchInput uappLaunchInput) throws RuntimeException {

        //This is added to clear pagination data from app memory . This should be taken in tech debt .
        if( ((IAPLaunchInput) uappLaunchInput).mLandingView!=IAP_SHOPPING_CART_VIEW) {
            CartModelContainer.getInstance().clearProductList();
        }
        IAPUtility.getInstance().resetPegination();

        ConnectivityManager connectivityManager
                = (ConnectivityManager) mIAPSettings.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (!NetworkUtility.getInstance().isNetworkAvailable(connectivityManager)) {
            ((IAPLaunchInput) uappLaunchInput).getIapListener().onFailure(IAPConstant.IAP_ERROR_NO_CONNECTION);
            throw new RuntimeException(mIAPSettings.getContext().getString(R.string.iap_no_internet));// Confirm the behaviour on error Callback
        }
        mIapServiceDiscoveryWrapper.getLocaleFromServiceDiscovery(uiLauncher, mIAPHandler, (IAPLaunchInput) uappLaunchInput, null, null);
    }

    /**
     * IAPInterface getProductCartCount will fetch the cart count
     *
     * @param iapListener instance of IAPListener
     * @since 1.0.0
     */
    @Override
    public void getProductCartCount(IAPListener iapListener) {
        if (mUserDataInterface != null && mUserDataInterface.getUserLoggedInState().ordinal() >= UserLoggedInState.PENDING_HSDP_LOGIN.ordinal()){
            mIapServiceDiscoveryWrapper.getLocaleFromServiceDiscovery(null, mIAPHandler, null, iapListener, "productCartCount");
       } else {
            iapListener.onFailure(IAPConstant.IAP_ERROR_AUTHENTICATION_FAILURE);
        }
    }

    /**
     * IAPInterface fetch complete product ctn list from backend
     *
     * @param iapListener instance of IAPListener
     * @since 1.0.0
     */
    @Override
    public void getCompleteProductList(IAPListener iapListener) {
        mIapServiceDiscoveryWrapper.getLocaleFromServiceDiscovery(null, mIAPHandler, null, iapListener, "completeProductList");
    }

    /**
     * IAPInterface isCartVisible method will inform the uApp for cart visibility. It’s an optional method
     *
     * @param iapListener instance of IAPListener
     * @return
     * @since 1.0.0
     */
    @Override
    public void isCartVisible(IAPListener iapListener) {
        if (mIAPHandler != null && mUserDataInterface != null && mUserDataInterface.getUserLoggedInState().ordinal() >= UserLoggedInState.PENDING_HSDP_LOGIN.ordinal()) {
             mIapServiceDiscoveryWrapper.getCartVisiblityByConfigUrl(iapListener, mIAPHandler);
        } else {
            iapListener.onSuccess(false);
            iapListener.onFailure(IAPConstant.IAP_ERROR_AUTHENTICATION_FAILURE);
        }

    }
}



