package com.philips.cdp.productselection.prx;

import com.philips.cdp.prxclient.datamodels.summary.SummaryModel;

import java.util.List;

/**
 */
public interface SummaryDataListener {

    public void onSuccess(List<SummaryModel> summaryModels);

}
