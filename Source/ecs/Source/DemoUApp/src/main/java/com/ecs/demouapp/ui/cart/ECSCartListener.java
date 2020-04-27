/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.ecs.demouapp.ui.cart;

import android.os.Message;

import com.philips.platform.ecs.model.cart.ECSShoppingCart;

public interface ECSCartListener {
    void onSuccess(ECSShoppingCart ecsShoppingCart);

    void onFailure(final Message msg);
}
