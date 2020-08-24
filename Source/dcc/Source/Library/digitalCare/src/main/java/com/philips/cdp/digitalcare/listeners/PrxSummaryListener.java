/**
 * Description : Summary Callback listener for the Summary PRX Data.
 * Project : PRX Common Component.
 *
 * <p/>
 * Copyright (c) 2016 Philips. All rights reserved.
 */
package com.philips.cdp.digitalcare.listeners;

import com.philips.cdp.prxclient.datamodels.summary.SummaryModel;

/**
 * The Callback interface for receiving the Summary Response from the PRX Server.
 */
public interface PrxSummaryListener {
    void onResponseReceived(SummaryModel isAvailable);
}
