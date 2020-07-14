/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.prx;

import android.content.Context;
import android.os.Message;

import com.android.volley.NoConnectionError;
import com.android.volley.TimeoutError;
import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.analytics.IAPAnalytics;
import com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant;
import com.philips.cdp.di.iap.container.CartModelContainer;
import com.philips.cdp.di.iap.session.IAPNetworkError;
import com.philips.cdp.di.iap.session.NetworkConstants;
import com.philips.cdp.prxclient.PRXDependencies;
import com.philips.cdp.prxclient.PrxConstants;
import com.philips.cdp.prxclient.RequestManager;
import com.philips.cdp.prxclient.datamodels.assets.Asset;
import com.philips.cdp.prxclient.datamodels.assets.AssetModel;
import com.philips.cdp.prxclient.datamodels.assets.Assets;
import com.philips.cdp.prxclient.datamodels.assets.Data;
import com.philips.cdp.prxclient.error.PrxError;
import com.philips.cdp.prxclient.request.ProductAssetRequest;
import com.philips.cdp.prxclient.response.ResponseData;
import com.philips.cdp.prxclient.response.ResponseListener;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class PRXAssetExecutor {
    private AssetListener mAssetListener;
    private Context mContext;
    private String mCTN;

    private static final int RTP = 1;
    private static final int APP = 2;
    private static final int DPP = 3;
    private static final int MI1 = 4;
    private static final int PID = 5;

    public interface AssetListener {
        void onFetchAssetSuccess(Message msg);

        void onFetchAssetFailure(Message msg);
    }

    public PRXAssetExecutor(Context context, String CTN,
                            AssetListener listener) {
        mContext = context;
        mAssetListener = listener;
        mCTN = CTN;
    }

    public void build() {
        executeRequest(prepareAssetBuilder(mCTN));
    }

    public void executeRequest(final ProductAssetRequest productAssetBuilder) {
        RequestManager mRequestManager = new RequestManager();
        PRXDependencies prxDependencies = new PRXDependencies(mContext, CartModelContainer.getInstance().getAppInfraInstance(), IAPAnalyticsConstant.COMPONENT_NAME);
        mRequestManager.init(prxDependencies);
        mRequestManager.executeRequest(productAssetBuilder, new ResponseListener() {
            @Override
            public void onResponseSuccess(ResponseData responseData) {
                notifySuccess(responseData);
            }

            @Override
            public void onResponseError(final PrxError prxError) {
                notifyError(prxError);
            }
        });
    }

    protected void notifySuccess(ResponseData responseData) {
        Message result = Message.obtain();
        AssetModel assetModel = (AssetModel) responseData;
        if (assetModel == null) {
            if (mAssetListener != null) {
                result.obj = mContext.getString(R.string.iap_something_gone_wrong);
                mAssetListener.onFetchAssetFailure(result);
            }
            return;
        }
        Data data = assetModel.getData();
        if (data == null) {
            if (mAssetListener != null) {
                result.obj = mContext.getString(R.string.iap_something_gone_wrong);
                mAssetListener.onFetchAssetFailure(result);
            }
            return;
        }
        Assets assets = data.getAssets();
        if (assets == null) {
            if (mAssetListener != null) {
                result.obj = mContext.getString(R.string.iap_something_gone_wrong);
                mAssetListener.onFetchAssetFailure(result);
            }
            return;
        }
        List<Asset> asset = assets.getAsset();
        if (asset == null) {
            if (mAssetListener != null) {
                result.obj = mContext.getString(R.string.iap_something_gone_wrong);
                mAssetListener.onFetchAssetFailure(result);
            }
            return;
        }

        result.obj = fetchImageUrlsFromPRXAssets(asset);
        if (mAssetListener != null) {
            mAssetListener.onFetchAssetSuccess(result);
        }
    }

    private ArrayList<String> fetchImageUrlsFromPRXAssets(List<Asset> assets) {
        ArrayList<String> mAssetsFromPRX = new ArrayList<>();
        TreeMap<Integer, String> sortedAssetsFromPRX = new TreeMap<>();
        GetHeightAndWidth getHeightAndWidth = new GetHeightAndWidth().invoke();
        int width = getHeightAndWidth.getWidth();
        int height = getHeightAndWidth.getHeight();

        for (Asset asset : assets) {
            int assetType = getAssetType(asset);
            if (assetType != -1) {
                String imagepath = asset.getAsset() + "?wid=" + width +
                        "&hei=" + height + "&$pnglarge$" + "&fit=fit,1";
                sortedAssetsFromPRX.put(assetType, imagepath);
            }
            mAssetsFromPRX = new ArrayList<>(sortedAssetsFromPRX.values());
        }

        return mAssetsFromPRX;
    }

    private int getAssetType(final Asset asset) {
        switch (asset.getType()) {
            case "RTP":
                return RTP;
            case "APP":
                return APP;
            case "DPP":
                return DPP;
            case "MI1":
                return MI1;
            case "PID":
                return PID;
            default:
                return -1;
        }
    }

    protected void notifyError(final PrxError prxError) {
        Message result = Message.obtain();
        if (PrxError.PrxErrorType.NO_INTERNET_CONNECTION.getId() == prxError.getStatusCode()) {
            result.obj = new IAPNetworkError(new NoConnectionError(), 0, null);
            IAPAnalytics.trackInformationalErrorWithPrefix(IAPAnalyticsConstant.PRX + prxError.getStatusCode() + "_" + prxError.getDescription());
        } else if (PrxError.PrxErrorType.TIME_OUT.getId() == prxError.getStatusCode()) {
            result.obj = new IAPNetworkError(new TimeoutError(), 0, null);
            IAPAnalytics.trackTechnicalErrorWithPrefix(IAPAnalyticsConstant.PRX + prxError.getStatusCode() + "_" + prxError.getDescription());
        } else {
            result.obj = prxError.getStatusCode();
            IAPAnalytics.trackTechnicalErrorWithPrefix(IAPAnalyticsConstant.PRX + prxError.getStatusCode() + "_" + prxError.getDescription());
        }
        if (mAssetListener != null) {
            mAssetListener.onFetchAssetFailure(result);
        }
    }

    private ProductAssetRequest prepareAssetBuilder(final String code) {
        ProductAssetRequest productAssetBuilder = new ProductAssetRequest(code, null);
        productAssetBuilder.setSector(PrxConstants.Sector.B2C);
        productAssetBuilder.setCatalog(PrxConstants.Catalog.CONSUMER);
        productAssetBuilder.setRequestTimeOut(NetworkConstants.DEFAULT_TIMEOUT_MS);

        return productAssetBuilder;
    }

    private class GetHeightAndWidth {
        private int width;
        private int height;

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        GetHeightAndWidth invoke() {
            width = 0;
            height = 0;
            width = mContext.getResources().getDisplayMetrics().widthPixels;
            height = (int) mContext.getResources().getDimension(R.dimen.iap_product_detail_image_height);

            return this;
        }
    }
}