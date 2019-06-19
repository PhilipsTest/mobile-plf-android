/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.integration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;

import com.philips.cdp.di.iap.activity.IAPActivity;
import com.philips.cdp.di.iap.analytics.IAPAnalytics;
import com.philips.cdp.di.iap.controller.ControllerFactory;
import com.philips.cdp.di.iap.iapHandler.HybrisHandler;
import com.philips.cdp.di.iap.iapHandler.IAPExposedAPI;
import com.philips.cdp.di.iap.iapHandler.LocalHandler;
import com.philips.cdp.di.iap.networkEssential.NetworkEssentials;
import com.philips.cdp.di.iap.networkEssential.NetworkEssentialsFactory;
import com.philips.cdp.di.iap.screens.BuyDirectFragment;
import com.philips.cdp.di.iap.screens.InAppBaseFragment;
import com.philips.cdp.di.iap.screens.ProductCatalogFragment;
import com.philips.cdp.di.iap.screens.ProductDetailFragment;
import com.philips.cdp.di.iap.screens.PurchaseHistoryFragment;
import com.philips.cdp.di.iap.screens.ShoppingCartFragment;
import com.philips.cdp.di.iap.session.HybrisDelegate;
import com.philips.cdp.di.iap.session.IAPNetworkError;
import com.philips.cdp.di.iap.session.RequestListener;
import com.philips.cdp.di.iap.utils.IAPConstant;
import com.philips.cdp.di.iap.utils.Utility;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;

import java.util.ArrayList;

class IAPHandler {
    private IAPDependencies mIAPDependencies;
    private IAPSettings mIAPSetting;

    IAPHandler(IAPDependencies pIAPDependencies, IAPSettings pIapSettings) {
        mIAPDependencies = pIAPDependencies;
        mIAPSetting = pIapSettings;
    }

    void initPreRequisite() {
        IAPAnalytics.initIAPAnalytics(mIAPDependencies);
    }

    void initIAPRequisite() {
        initHybrisDelegate();
    }

    void initControllerFactory() {
        ControllerFactory.getInstance().init(mIAPSetting.isUseLocalData());
    }

    void initHybrisDelegate() {
        NetworkEssentials essentials = NetworkEssentialsFactory.getNetworkEssentials(mIAPSetting.isUseLocalData());
        HybrisDelegate.getDelegateWithNetworkEssentials(essentials, mIAPSetting,mIAPDependencies);
    }

    void initIAP(final UiLauncher uiLauncher, final IAPLaunchInput pLaunchInput) {
        final IAPListener iapListener = pLaunchInput.getIapListener();
        HybrisDelegate delegate = HybrisDelegate.getInstance(mIAPSetting.getContext());
        delegate.getStore().initStoreConfig(new RequestListener() {
            @Override
            public void onSuccess(final Message msg) {
                onSuccessOfInitialization(uiLauncher, pLaunchInput, iapListener);
            }

            @Override
            public void onError(final Message msg) {
                onFailureOfInitialization(msg, iapListener);
            }
        });
    }

    protected void onSuccessOfInitialization(UiLauncher uiLauncher, IAPLaunchInput pLaunchInput, IAPListener iapListener) {
        if (uiLauncher instanceof ActivityLauncher) {
            launchAsActivity(mIAPSetting.getContext(), pLaunchInput, (ActivityLauncher) uiLauncher);
        } else {
            launchAsFragment(pLaunchInput, (FragmentLauncher) uiLauncher);
        }

        if (iapListener != null) {
            iapListener.onSuccess();
        }
    }

    protected void onFailureOfInitialization(Message msg, IAPListener iapListener) {
        if (iapListener != null) {
            iapListener.onFailure(getIAPErrorCode(msg));
        }
    }

    void launchIAP(UiLauncher uiLauncher, IAPLaunchInput pLaunchInput) {
        if(!verifyInput(pLaunchInput, pLaunchInput.mIAPFlowInput))return;
        Utility.setVoucherCode(pLaunchInput.getVoucher());
        if (uiLauncher instanceof ActivityLauncher) {
            launchAsActivity(mIAPSetting.getContext(), pLaunchInput, (ActivityLauncher) uiLauncher);
        } else if (uiLauncher instanceof FragmentLauncher) {
            launchAsFragment(pLaunchInput, (FragmentLauncher) uiLauncher);
        }
    }

    protected boolean verifyInput(IAPLaunchInput launchInput, IAPFlowInput input) {

        int landingScreen = launchInput.mLandingView;
        if (landingScreen == IAPLaunchInput.IAPFlows.IAP_BUY_DIRECT_VIEW
                || landingScreen == IAPLaunchInput.IAPFlows.IAP_PRODUCT_DETAIL_VIEW) {
            if (input.getProductCTN() == null
                    || input.getProductCTN().equalsIgnoreCase("")) {

                launchInput.getIapListener().onFailure(IAPConstant.IAP_ERROR_INVALID_CTN);
                return false;
            }
        } else if (landingScreen == IAPLaunchInput.IAPFlows.IAP_PRODUCT_CATALOG_VIEW
                && (input == null || input.getProductCTNs() == null ||
                (input.getProductCTNs() != null && input.getProductCTNs().size() == 0))) {
            launchInput.getIapListener().onFailure(IAPConstant.IAP_ERROR_INVALID_CTN);
            return false;
        }
        return true;
    }

    protected void launchAsActivity(Context pContext, IAPLaunchInput pLaunchInput,
                                    ActivityLauncher activityLauncher) {
        Intent intent = new Intent(pContext, IAPActivity.class);
        intent.putExtra(IAPConstant.IAP_LANDING_SCREEN, pLaunchInput.mLandingView);

        if (pLaunchInput.mIAPFlowInput != null) {
            if (pLaunchInput.mIAPFlowInput.getProductCTN() != null) {
                intent.putExtra(IAPConstant.IAP_PRODUCT_CATALOG_NUMBER_FROM_VERTICAL,
                        pLaunchInput.mIAPFlowInput.getProductCTN());
            }
            if (pLaunchInput.mIAPFlowInput.getProductCTNs() != null) {
                intent.putStringArrayListExtra(IAPConstant.CATEGORISED_PRODUCT_CTNS,
                        pLaunchInput.mIAPFlowInput.getProductCTNs());
            }
            intent.putExtra(IAPConstant.IAP_IGNORE_RETAILER_LIST, pLaunchInput.getIgnoreRetailers());
        }
        if(pLaunchInput.getVoucher()!=null) {
            Utility.setVoucherCode(pLaunchInput.getVoucher());
        }
        intent.putExtra(IAPConstant.IAP_KEY_ACTIVITY_THEME, activityLauncher.getUiKitTheme());
        pContext.startActivity(intent);
    }

    protected void launchAsFragment(IAPLaunchInput iapLaunchInput, FragmentLauncher uiLauncher) {
        InAppBaseFragment target = getFragment(iapLaunchInput.mLandingView, iapLaunchInput);
        addFragment(target, uiLauncher, iapLaunchInput.getIapListener());
    }

    protected InAppBaseFragment getFragment(final int screen, final IAPLaunchInput iapLaunchInput) {
        InAppBaseFragment fragment;
        Bundle bundle = new Bundle();
        final ArrayList<String> ignoreRetailers = iapLaunchInput.getIgnoreRetailers();
        final String voucherCode=iapLaunchInput.getVoucher();
        switch (screen) {
            case IAPLaunchInput.IAPFlows.IAP_SHOPPING_CART_VIEW:
                fragment = new ShoppingCartFragment();
                bundle.putStringArrayList(IAPConstant.IAP_IGNORE_RETAILER_LIST, ignoreRetailers);
                Utility.setVoucherCode(iapLaunchInput.getVoucher());
                break;
            case IAPLaunchInput.IAPFlows.IAP_PURCHASE_HISTORY_VIEW:
                fragment = new PurchaseHistoryFragment();
                break;
            case IAPLaunchInput.IAPFlows.IAP_PRODUCT_DETAIL_VIEW:
                fragment = new ProductDetailFragment();
                if (iapLaunchInput.mIAPFlowInput!= null  && iapLaunchInput.mIAPFlowInput.getProductCTN() != null) {
                    bundle.putString(IAPConstant.IAP_PRODUCT_CATALOG_NUMBER_FROM_VERTICAL, iapLaunchInput.mIAPFlowInput.getProductCTN());
                }
                bundle.putStringArrayList(IAPConstant.IAP_IGNORE_RETAILER_LIST, ignoreRetailers);
                fragment.setArguments(bundle);
                break;
            case IAPLaunchInput.IAPFlows.IAP_BUY_DIRECT_VIEW:
                fragment = new BuyDirectFragment();
                if (iapLaunchInput.mIAPFlowInput!= null  && iapLaunchInput.mIAPFlowInput.getProductCTN() != null) {
                    bundle.putString(IAPConstant.IAP_PRODUCT_CATALOG_NUMBER_FROM_VERTICAL, iapLaunchInput.mIAPFlowInput.getProductCTN());
                }
                bundle.putStringArrayList(IAPConstant.IAP_IGNORE_RETAILER_LIST, ignoreRetailers);
                break;
            case IAPLaunchInput.IAPFlows.IAP_PRODUCT_CATALOG_VIEW:
                fragment = new ProductCatalogFragment();
                if (iapLaunchInput.mIAPFlowInput!= null && iapLaunchInput.mIAPFlowInput.getProductCTNs()!= null) {
                    ArrayList<String> CTNs = iapLaunchInput.mIAPFlowInput.getProductCTNs();
                    bundle.putStringArrayList(IAPConstant.CATEGORISED_PRODUCT_CTNS, CTNs);
                }

                bundle.putStringArrayList(IAPConstant.IAP_IGNORE_RETAILER_LIST, ignoreRetailers);
                fragment.setArguments(bundle);
                break;
            default:
                fragment = new ProductCatalogFragment();
                bundle.putString(IAPConstant.CATEGORISED_PRODUCT_CTNS, null);
                bundle.putStringArrayList(IAPConstant.IAP_IGNORE_RETAILER_LIST, ignoreRetailers);
                fragment.setArguments(bundle);
                break;
        }
        return fragment;
    }

    protected void addFragment(InAppBaseFragment newFragment, FragmentLauncher fragmentLauncher, IAPListener iapListener) {
        newFragment.setActionBarListener(fragmentLauncher.getActionbarListener(), iapListener);
        String tag = newFragment.getClass().getName();
        FragmentTransaction transaction = fragmentLauncher.getFragmentActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(fragmentLauncher.getParentContainerResourceID(), newFragment, tag);
        transaction.addToBackStack(tag);
        transaction.commitAllowingStateLoss();
    }

    protected IAPExposedAPI getExposedAPIImplementor() {
        IAPExposedAPI api;
        if (mIAPSetting.isUseLocalData()) {
            api = new LocalHandler();
        } else {
            api = new HybrisHandler(mIAPSetting.getContext());
        }
        return api;
    }

    protected boolean isStoreInitialized(Context pContext) {
        return HybrisDelegate.getInstance(pContext).getStore().isStoreInitialized();
    }

    protected int getIAPErrorCode(Message msg) {
        if (msg.obj instanceof IAPNetworkError) {
            return ((IAPNetworkError) msg.obj).getIAPErrorCode();
        }
        return IAPConstant.IAP_ERROR_UNKNOWN;
    }
}
