/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.products;

import android.content.Context;
import android.os.Message;

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.analytics.IAPAnalytics;
import com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant;
import com.philips.cdp.di.iap.integration.IAPListener;
import com.philips.cdp.di.iap.model.AbstractModel;
import com.philips.cdp.di.iap.response.products.Products;
import com.philips.cdp.di.iap.response.products.ProductsEntity;
import com.philips.cdp.di.iap.session.IAPNetworkError;
import com.philips.cdp.di.iap.utils.NetworkUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LocalProductCatalog implements ProductCatalogAPI, AbstractModel.DataLoadListener {
    private Context mContext;
    private ProductCatalogHelper mProductCatalogHelper;
    private ProductCatalogPresenter.ProductCatalogListener mProductCatalogListener;
    private Products mProductCatalog;

    public LocalProductCatalog(final Context context, final ProductCatalogPresenter.ProductCatalogListener productCatalogListener) {
        mContext = context;
        mProductCatalogListener = productCatalogListener;

        mProductCatalogHelper = new ProductCatalogHelper(context, productCatalogListener, this);
    }

    @Override
    public boolean getProductCatalog(int currentPage, int pageSize, IAPListener listener) {
        return true;
    }

    @Override
    public void getCategorizedProductList(final ArrayList<String> productList) {
        mProductCatalog = new Products();
        List<ProductsEntity> productsEntityList = new ArrayList<>();

        if (productList != null) {
            for (String productCode : productList) {
                ProductsEntity productsEntity = new ProductsEntity();
                productsEntity.setCode(productCode);
                productsEntityList.add(productsEntity);
            }
        }
        mProductCatalog.setProducts(productsEntityList);
        mProductCatalogHelper.sendPRXRequest(mProductCatalog);
    }

    @Override
    public void getCompleteProductList(IAPListener iapListener) {
    }

    @Override
    public void getCatalogCount(IAPListener listener) {
    }

    @Override
    public void onModelDataLoadFinished(final Message msg) {
        if (msg.obj instanceof HashMap) {
            mProductCatalogHelper.processPRXResponse(msg, mProductCatalog, null);
        }
    }

    @Override
    public void onModelDataError(final Message msg) {
        if (msg.obj instanceof IAPNetworkError)
            mProductCatalogListener.onLoadError((IAPNetworkError) msg.obj);
        else {
            IAPAnalytics.trackErrorWithPrefix(IAPAnalyticsConstant.SEND_DATA,
                    IAPAnalyticsConstant.ERROR, IAPAnalyticsConstant.PRX + IAPAnalyticsConstant.NO_PRODUCT_FOUND);

            mProductCatalogListener.onLoadError(NetworkUtility.getInstance()
                    .createIAPErrorMessage(IAPAnalyticsConstant.PRX, mContext.getString(R.string.iap_no_product_available)));
        }
    }
}
