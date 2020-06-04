package com.philips.cdp.di.iap.prx;

import android.content.Context;

import com.philips.cdp.di.iap.analytics.IAPAnalytics;
import com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant;
import com.philips.cdp.di.iap.container.CartModelContainer;
import com.philips.cdp.prxclient.PRXDependencies;
import com.philips.cdp.prxclient.PrxConstants;
import com.philips.cdp.prxclient.RequestManager;
import com.philips.cdp.prxclient.datamodels.Disclaimer.DisclaimerModel;
import com.philips.cdp.prxclient.error.PrxError;
import com.philips.cdp.prxclient.request.ProductDisclaimerRequest;
import com.philips.cdp.prxclient.response.ResponseData;
import com.philips.cdp.prxclient.response.ResponseListener;

import java.util.ArrayList;

public class PRXDisclaimerExecutor {


    public interface ProductDisclaimerListener {
        void onFetchProductDisclaimerSuccess(DisclaimerModel disclaimerModel);

        void onFetchProductDisclaimerFailure(String error);
    }



    Context mContext;
    ArrayList<String> mCtns;
    private ProductDisclaimerListener mProductDisclaimerListener;



    public PRXDisclaimerExecutor(Context mContext, ArrayList<String> mCtns, ProductDisclaimerListener aProductDisclaimerListener) {
        this.mContext = mContext;
        this.mCtns = mCtns;
        this.mProductDisclaimerListener = aProductDisclaimerListener;
    }

    public void preparePRXDataRequest() {
        for (String ctn : mCtns) {
            executeRequest(ctn, prepareDisclaimerRequest(ctn));
        }
    }

    protected void executeRequest(final String ctn, final ProductDisclaimerRequest productDisclaimerRequestBuilder) {
        RequestManager mRequestManager = new RequestManager();
        PRXDependencies prxDependencies = new PRXDependencies(mContext, CartModelContainer.getInstance().getAppInfraInstance(), IAPAnalyticsConstant.COMPONENT_NAME);
        mRequestManager.init(prxDependencies);
        mRequestManager.executeRequest(productDisclaimerRequestBuilder, new ResponseListener() {
            @Override
            public void onResponseSuccess(ResponseData responseData) {
                DisclaimerModel disclaimerModel = (DisclaimerModel) responseData;
                if (disclaimerModel.isSuccess()) {
                    CartModelContainer.getInstance().addProductDisclaimer(ctn, disclaimerModel);
                }
                notifySuccess(disclaimerModel);
            }

            @Override
            public void onResponseError(final PrxError prxError) {
                if (prxError.getStatusCode() == 404) {
                    notifyError(ctn, prxError.getStatusCode(), "Product not found in your store");
                } else
                    notifyError(ctn, prxError.getStatusCode(), prxError.getDescription());
            }
        });
    }

    protected void notifyError(final String ctn, final int errorCode, final String error) {
        IAPAnalytics.trackErrorWithPrefix(IAPAnalyticsConstant.SEND_DATA,
                IAPAnalyticsConstant.ERROR, IAPAnalyticsConstant.PRX + ctn + "_" + errorCode + error);
        mProductDisclaimerListener.onFetchProductDisclaimerFailure(error);
    }

    protected void notifySuccess(DisclaimerModel model) {
        if (model.getData() != null) {
            mProductDisclaimerListener.onFetchProductDisclaimerSuccess(model);
        }
    }

    private ProductDisclaimerRequest prepareDisclaimerRequest(final String code) {
        ProductDisclaimerRequest productDisclaimerRequest = new ProductDisclaimerRequest(code, null);
        productDisclaimerRequest.setSector(PrxConstants.Sector.B2C);
        productDisclaimerRequest.setCatalog(PrxConstants.Catalog.CONSUMER);
        return productDisclaimerRequest;
    }
}
