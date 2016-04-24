/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */

package com.philips.cdp.di.iap.session;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;

import com.philips.cdp.di.iap.BuildConfig;
import com.philips.cdp.di.iap.ShoppingCart.IAPCartListener;
import com.philips.cdp.di.iap.ShoppingCart.ShoppingCartPresenter;
import com.philips.cdp.di.iap.activity.IAPActivity;
import com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant;
import com.philips.cdp.di.iap.utils.IAPConstant;
import com.philips.cdp.tagging.Tagging;

public class IAPHandler {

    private int mThemeIndex;
    private Context mContext;
    private String mLanguage;
    private String mCountry;

    private IAPHandler() {
    }

    public static IAPHandler init(Context context, IAPConfig config) {
        IAPHandler handler = new IAPHandler();
        handler.mThemeIndex = config.themeIndex;
        handler.mContext = context.getApplicationContext();
        handler.mLanguage = config.language;
        handler.mCountry = config.country;

        //Update store about country change
        HybrisDelegate.getInstance(context).getStore().setLangAndCountry(handler.mLanguage, handler.mCountry);

        return handler;
    }

    public void launchIAP(int screen, String ctnNumber, IAPHandlerListener listener) {
        if (isStoreInitialized()) {
            checkLaunchOrBuy(screen, ctnNumber, listener);
        } else {
            initIAP(screen, ctnNumber, listener);
        }
    }

    void checkLaunchOrBuy(int screen, String ctnNumber, IAPHandlerListener listener) {
        if (screen == IAPConstant.Screen.PRODUCT_CATALOG) {
            launchIAPActivity(IAPConstant.Screen.PRODUCT_CATALOG);
        } else if (screen == IAPConstant.Screen.SHOPPING_CART && TextUtils.isEmpty(ctnNumber)) {
            launchIAPActivity(IAPConstant.Screen.SHOPPING_CART);
        } else {
            buyProduct(ctnNumber, listener);
        }
    }

    void initIAP(final int screen, final String ctnNumber, final IAPHandlerListener listener) {
        HybrisDelegate delegate = HybrisDelegate.getInstance(mContext);
        delegate.getStore().initStoreConfig(mLanguage, mCountry, new RequestListener() {
            @Override
            public void onSuccess(final Message msg) {
                checkLaunchOrBuy(screen, ctnNumber, listener);
            }

            @Override
            public void onError(final Message msg) {
                listener.onFailure(getIAPErrorCode(msg));
            }
        });
    }

    void launchIAPActivity(int screen) {
        //Set component version key and value for InAppPurchase
        Tagging.setComponentVersionKey(IAPAnalyticsConstant.COMPONENT_VERSION);
        Tagging.setComponentVersionVersionValue("In app purchase " + BuildConfig.VERSION_NAME);

        Intent intent = new Intent(mContext, IAPActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //Check flag to differentiate shopping cart / product catalog
        if (screen != IAPConstant.Screen.SHOPPING_CART) {
            intent.putExtra(IAPConstant.IAP_IS_SHOPPING_CART_VIEW_SELECTED, false);
        }

        intent.putExtra(IAPConstant.IAP_KEY_ACTIVITY_THEME, mThemeIndex);
        mContext.startActivity(intent);
    }

    public void getProductCartCount(final IAPHandlerListener iapHandlerListener) {
        if (isStoreInitialized()) {
            getProductCount(iapHandlerListener);
        } else {
            HybrisDelegate.getInstance(mContext).getStore().
                    initStoreConfig(mLanguage, mCountry, new RequestListener() {
                        @Override
                        public void onSuccess(final Message msg) {
                            getProductCount(iapHandlerListener);
                        }

                        @Override
                        public void onError(final Message msg) {
                            iapHandlerListener.onFailure(getIAPErrorCode(msg));
                        }
                    });
        }
    }

    private void getProductCount(final IAPHandlerListener iapHandlerListener) {
        ShoppingCartPresenter presenter = new ShoppingCartPresenter();
        presenter.getProductCartCount(mContext, new IAPCartListener() {
            @Override
            public void onSuccess(final int count) {
                updateSuccessListener(count, iapHandlerListener);
            }

            @Override
            public void onFailure(final Message msg) {
                updateErrorListener(msg, iapHandlerListener);
            }
        });
    }

    private void buyProduct(final String ctnNumber, final IAPHandlerListener listener) {
        ShoppingCartPresenter presenter = new ShoppingCartPresenter();
        presenter.buyProduct(mContext, ctnNumber, new IAPCartListener() {
            @Override
            public void onSuccess(final int count) {
                launchIAPActivity(IAPConstant.Screen.SHOPPING_CART);
            }

            @Override
            public void onFailure(final Message msg) {
                updateErrorListener(msg, listener);
            }
        });
    }

    private void updateErrorListener(final Message msg, final IAPHandlerListener iapHandlerListener) {
        if (iapHandlerListener != null) {
            iapHandlerListener.onFailure(getIAPErrorCode(msg));
        }
    }

    private void updateSuccessListener(final int count, final IAPHandlerListener iapHandlerListener) {
        if (iapHandlerListener != null) {
            iapHandlerListener.onSuccess(count);
        }
    }

    private int getIAPErrorCode(Message msg) {
        if (msg.obj instanceof IAPNetworkError) {
            return ((IAPNetworkError) msg.obj).getIAPErrorCode();
        }
        return IAPConstant.IAP_ERROR_UNKNOWN;
    }

    private boolean isStoreInitialized() {
        return HybrisDelegate.getInstance(mContext).getStore().isStoreInitialized();
    }
}