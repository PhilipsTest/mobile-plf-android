package com.philips.cdp.di.iap.core;

import com.philips.cdp.di.iap.session.IAPHandlerListener;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public interface IAPExposedAPI {
    void launchIAP(int landingView, String ctnNumber, IAPHandlerListener listener);

    void getProductCartCount(IAPHandlerListener iapHandlerListener);

    void getCompleteProductList(IAPHandlerListener iapHandlerListener);
}
