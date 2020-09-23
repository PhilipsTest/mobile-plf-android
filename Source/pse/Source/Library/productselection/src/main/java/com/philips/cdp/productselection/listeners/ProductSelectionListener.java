package com.philips.cdp.productselection.listeners;

import com.philips.cdp.prxclient.datamodels.summary.SummaryModel;

/**
 * ProductSelectionListener is the callback interface can be used by vertical applications
 * for receiving the SummmaryModel of the Selected product by the ProductSelection Component.
 * <p/>
 */
public interface ProductSelectionListener {


    void onProductModelSelected(final SummaryModel summaryModel);
}
