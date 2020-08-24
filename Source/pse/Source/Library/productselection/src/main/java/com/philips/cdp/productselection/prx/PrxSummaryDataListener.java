package com.philips.cdp.productselection.prx;

/**
 * This is the PrxSummaryDataListener class to receive the data from the Multiple Prx data requests.
 * This callback class  is used to manage to multiple data requests in the many data requests scenario.
 * <p/>
 * <p/>
 */
public interface PrxSummaryDataListener {

    /**
     * onprxData Successfull scenario's - it gives the loaded data.
     *
     * @param summaryModel
     */
    abstract void onSuccess(com.philips.cdp.prxclient.datamodels.summary.SummaryModel summaryModel);

    /**
     * This method is responsible to get the error messages which has been intimated by volley library.
     *
     * @param errorMessage
     */
    void onFail(String errorMessage);

}
