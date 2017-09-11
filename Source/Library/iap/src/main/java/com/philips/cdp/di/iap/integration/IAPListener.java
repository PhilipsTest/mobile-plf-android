/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.integration;


import java.util.ArrayList;

public interface IAPListener {

    void onGetCartCount(int count);

    void onUpdateCartCount();

    void updateCartIconVisibility(boolean shouldShow);

    void onGetCompleteProductList(final ArrayList<String> productList);

    @Deprecated
    void onSuccess();

    void onSuccess(Object bool);

    void onFailure(final int errorCode);

}
