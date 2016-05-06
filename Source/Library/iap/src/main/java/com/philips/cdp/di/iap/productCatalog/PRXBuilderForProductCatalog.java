/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.productcatalog;

import android.content.Context;
import android.os.Message;

import com.philips.cdp.di.iap.model.AbstractModel;
import com.philips.cdp.di.iap.response.products.Products;
import com.philips.cdp.di.iap.response.products.ProductsEntity;
import com.philips.cdp.di.iap.session.HybrisDelegate;
import com.philips.cdp.localematch.enums.Catalog;
import com.philips.cdp.localematch.enums.Sector;
import com.philips.cdp.prxclient.Logger.PrxLogger;
import com.philips.cdp.prxclient.RequestManager;
import com.philips.cdp.prxclient.datamodels.summary.Data;
import com.philips.cdp.prxclient.datamodels.summary.SummaryModel;
import com.philips.cdp.prxclient.error.PrxError;
import com.philips.cdp.prxclient.request.ProductSummaryRequest;
import com.philips.cdp.prxclient.response.ResponseData;
import com.philips.cdp.prxclient.response.ResponseListener;

import java.util.ArrayList;
import java.util.List;

public class PRXBuilderForProductCatalog {
    private static final String TAG = PRXBuilderForProductCatalog.class.getSimpleName();
    private AbstractModel.DataLoadListener mDataLoadListener;
    private Context mContext;
    private List<ProductCatalogData> mProduct;
    private Products mProducts;
    List<ProductsEntity> mProductList;

    //Handling error cases where Product is in Hybris but not in PRX store.
    private volatile int mProudctUpdateCount;
    private int mProductPresentInPRX;

    public PRXBuilderForProductCatalog(Context context, Products products,
                                 AbstractModel.DataLoadListener listener) {
        mProducts = products;
        mProductList = products.getProducts();
        mContext = context;
        mDataLoadListener = listener;
    }

    public void build() {
        int count = mProductList.size();
        PrxLogger.enablePrxLogger(true);
        for (int index = 0; index < count; index++) {
            ProductsEntity entry = mProductList.get(index);
            String code = entry.getCode();
            executeRequest(entry, code, prepareSummaryBuilder(code));
        }
    }

    public void executeRequest(final ProductsEntity entry, final String code
                               , final ProductSummaryRequest productSummaryBuilder) {
        RequestManager mRequestManager = new RequestManager();
        mRequestManager.init(mContext);
        mRequestManager.executeRequest(productSummaryBuilder, new ResponseListener() {
            @Override
            public void onResponseSuccess(ResponseData responseData) {
                mProudctUpdateCount++;
                mProductPresentInPRX++;
                updateSuccessData((SummaryModel) responseData, code,  entry);
            }

            @Override
            public void onResponseError(final PrxError prxError) {
                mProudctUpdateCount++;
                notifyError(prxError.getDescription());
            }
        });
    }

    private void updateSuccessData(final SummaryModel responseData, final String code, final ProductsEntity entry) {
        ProductCatalogData productItem = new ProductCatalogData();
        SummaryModel mSummaryModel = responseData;
        Data data = mSummaryModel.getData();
        productItem.setImageUrl(data.getImageURL());
        productItem.setProductTitle(data.getProductTitle());
        productItem.setCtnNumber(code);
        productItem.setFormatedPrice(entry.getPrice().getFormattedValue());
        productItem.setMarketingTextHeader(data.getMarketingTextHeader());
        if (entry.getDiscountPrice() != null && entry.getDiscountPrice().getFormattedValue() != null
                && !entry.getDiscountPrice().getFormattedValue().isEmpty()) {
            productItem.setDiscountedPrice(entry.getDiscountPrice().getFormattedValue());
        }
        addWithNotify(productItem);
    }

    private void notifyError(final String error) {
        Message result = Message.obtain();
        result.obj = error;
        if (mDataLoadListener != null && mProudctUpdateCount == mProductList.size()) {
            if (mProductPresentInPRX > 0) {
                result.obj = mProduct;
                mDataLoadListener.onModelDataLoadFinished(result);
            } else {
                mDataLoadListener.onModelDataError(result);
            }
        }
    }

    private void addWithNotify(ProductCatalogData productCatalogItem) {
        if (mProduct == null) {
            mProduct = new ArrayList<ProductCatalogData>();
        }
        mProduct.add(productCatalogItem);
        if (mDataLoadListener != null && mProudctUpdateCount == mProductList.size()) {
            Message result = Message.obtain();
            result.obj = mProduct;
            mDataLoadListener.onModelDataLoadFinished(result);
        }
    }

    private ProductSummaryRequest prepareSummaryBuilder(final String code) {
        String locale = HybrisDelegate.getInstance(mContext).getStore().getLocale();

        ProductSummaryRequest productSummaryBuilder = new ProductSummaryRequest(code, null);
        productSummaryBuilder.setSector(Sector.B2C);
        productSummaryBuilder.setLocaleMatchResult(locale);
        productSummaryBuilder.setCatalog(Catalog.CONSUMER);
        return productSummaryBuilder;
    }
}