/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.iapHandler;

import android.content.Context;
import android.os.Message;

import com.philips.cdp.di.iap.cart.IAPCartListener;
import com.philips.cdp.di.iap.cart.ShoppingCartAPI;
import com.philips.cdp.di.iap.cart.ShoppingCartPresenter;
import com.philips.cdp.di.iap.controller.ControllerFactory;
import com.philips.cdp.di.iap.integration.IAPInterface;
import com.philips.cdp.di.iap.integration.IAPListener;
import com.philips.cdp.di.iap.products.ProductCatalogAPI;
import com.philips.cdp.di.iap.session.HybrisDelegate;
import com.philips.cdp.di.iap.session.IAPNetworkError;
import com.philips.cdp.di.iap.session.RequestListener;
import com.philips.cdp.di.iap.utils.IAPConstant;

public class HybrisHandler extends IAPInterface implements IAPExposedAPI {
    private static final long serialVersionUID = -3158097808313878846L;
    private Context mContext;

    public HybrisHandler(Context context) {
        mContext = context;
    }

    @Override
    public void getProductCartCount(final IAPListener iapListener) {
        if (isStoreInitialized()) {
            getProductCount(iapListener);
        } else {
            HybrisDelegate.getInstance(mContext).getStore().
                    initStoreConfig(new RequestListener() {
                        @Override
                        public void onSuccess(final Message msg) {
                            getProductCount(iapListener);
                        }

                        @Override
                        public void onError(final Message msg) {
                            iapListener.onFailure(getIAPErrorCode(msg));
                        }
                    });
        }
    }

    private void getProductCount(final IAPListener iapListener) {
        ShoppingCartAPI presenter = new ShoppingCartPresenter();
        presenter.getProductCartCount(mContext, new IAPCartListener() {
            @Override
            public void onSuccess(final int count) {
                iapListener.onGetCartCount(count);
            }

            @Override
            public void onFailure(final Message msg) {
                iapListener.onFailure(getIAPErrorCode(msg));
            }
        });
    }

    @Override
    public void getCompleteProductList(final IAPListener iapListener) {
        final ProductCatalogAPI presenter =
                ControllerFactory.getInstance().getProductCatalogPresenter(mContext, null);
        if (isStoreInitialized()) {
            presenter.getCompleteProductList(iapListener);
        } else {
            HybrisDelegate.getInstance(mContext).getStore().
                    initStoreConfig(/*mLanguage, mCountry,*/ new RequestListener() {
                        @Override
                        public void onSuccess(final Message msg) {
                            presenter.getCompleteProductList(iapListener);
                        }

                        @Override
                        public void onError(final Message msg) {
                            iapListener.onFailure(getIAPErrorCode(msg));
                        }
                    });
        }
    }

    public int getIAPErrorCode(Message msg) {
        if (msg.obj instanceof IAPNetworkError) {
            return ((IAPNetworkError) msg.obj).getIAPErrorCode();
        }
        return IAPConstant.IAP_ERROR_UNKNOWN;
    }

    public boolean isStoreInitialized() {
        return HybrisDelegate.getInstance(mContext).getStore().isStoreInitialized();
    }
}
