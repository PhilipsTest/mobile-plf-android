/**
 * <p>
 * Copyright (c) 2016 Philips. All rights reserved.
 */
package com.philips.cdp.digitalcare.listeners;


import com.philips.cdp.prxclient.datamodels.support.SupportModel;

/**
 * The Callback interface used across the component for collecting the
 * response from the PRX Server like Summary Request, Asset Request etc..
 */
public interface PrxFaqCallback {
    void onResponseReceived(SupportModel isAvailable);
}
