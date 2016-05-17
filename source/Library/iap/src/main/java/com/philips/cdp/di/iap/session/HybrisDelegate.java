/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */

package com.philips.cdp.di.iap.session;

import android.content.Context;

import com.philips.cdp.di.iap.core.StoreSpec;
import com.philips.cdp.di.iap.model.AbstractModel;

public class HybrisDelegate {

    private static HybrisDelegate delegate = new HybrisDelegate();

    NetworkController controller;
    private Context mContext;

    private HybrisDelegate() {
    }

    public NetworkController getNetworkController(Context context) {
        if (controller == null) {
            controller = new NetworkController(context);
        }
        return controller;
    }

    public static HybrisDelegate getInstance(Context context) {
        if (delegate.controller == null) {
            delegate.mContext = context.getApplicationContext();
            delegate.controller = delegate.getNetworkController(context);
        }
        return delegate;
    }

    public static HybrisDelegate getInstance() {
        return delegate;
    }

    public static NetworkController getNetworkController() {
        return delegate.controller;
    }

    public void sendRequest(int requestCode, AbstractModel model, final RequestListener
            requestListener) {
        controller.sendHybrisRequest(requestCode, model, requestListener);
    }

    public StoreSpec getStore() {
        return controller.getStore();
    }
}