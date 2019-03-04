/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */

package com.philips.cdp.di.iap.prx;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.philips.cdp.di.iap.analytics.IAPAnalytics;
import com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant;
import com.philips.cdp.di.iap.container.CartModelContainer;
import com.philips.cdp.di.iap.model.AbstractModel;
import com.philips.cdp.prxclient.PRXDependencies;
import com.philips.cdp.prxclient.PrxConstants;
import com.philips.cdp.prxclient.RequestManager;
import com.philips.cdp.prxclient.datamodels.summary.Data;
import com.philips.cdp.prxclient.datamodels.summary.PRXSummaryListResponse;
import com.philips.cdp.prxclient.datamodels.summary.SummaryModel;
import com.philips.cdp.prxclient.error.PrxError;
import com.philips.cdp.prxclient.request.ProductSummaryListRequest;
import com.philips.cdp.prxclient.request.ProductSummaryRequest;
import com.philips.cdp.prxclient.response.ResponseData;
import com.philips.cdp.prxclient.response.ResponseListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PRXSummaryListExecutor {

    Context mContext;
    ArrayList<String> mCtns;
    AbstractModel.DataLoadListener mDataLoadListener;
    private HashMap<String, PRXSummaryListResponse> mPRXSummaryData;

    //Handling error cases where Product is in Hybris but not in PRX store.
    protected int mProductPresentInPRX;

    public PRXSummaryListExecutor(Context context, ArrayList<String> ctns, AbstractModel.DataLoadListener listener) {
        mContext = context;
        mCtns = ctns;
        mDataLoadListener = listener;
        mPRXSummaryData = new HashMap<>();
    }

    public void preparePRXDataRequest() {
        executeRequest(prepareProductSummaryListRequest(mCtns));
    }

    protected void executeRequest(final ProductSummaryListRequest productSummaryListBuilder) {
        RequestManager mRequestManager = new RequestManager();
        PRXDependencies prxDependencies = new PRXDependencies(mContext, CartModelContainer.getInstance().getAppInfraInstance(), IAPAnalyticsConstant.COMPONENT_NAME);
        mRequestManager.init(prxDependencies);
        mRequestManager.executeRequest(productSummaryListBuilder, new ResponseListener() {
            @Override
            public void onResponseSuccess(ResponseData responseData) {
                notifySuccess((PRXSummaryListResponse) responseData);
            }

            @Override
            public void onResponseError(final PrxError prxError) {

            }
        });
    }

    protected void notifyError(final String ctn, final int errorCode, final String error) {
        Message result = Message.obtain();
        result.obj = error;

        IAPAnalytics.trackAction(IAPAnalyticsConstant.SEND_DATA,
                IAPAnalyticsConstant.ERROR, IAPAnalyticsConstant.PRX + ctn + "_" + errorCode + error);

    }

    protected void notifySuccess(PRXSummaryListResponse model) {

        if (!model.getData().isEmpty()) {

            for(Data data:model.getData())
            mPRXSummaryData.put(data.getCtn(), model);
        }

        Log.d("pabitra", "got data");
    }
    private ProductSummaryListRequest prepareProductSummaryListRequest(List<String> ctns) {
        return new ProductSummaryListRequest(ctns, PrxConstants.Sector.B2C, PrxConstants.Catalog.CONSUMER, null);
    }
}
