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
import com.philips.cdp.di.iap.container.CartModelContainer;
import com.philips.cdp.di.iap.integration.IAPListener;
import com.philips.cdp.di.iap.model.AbstractModel;
import com.philips.cdp.di.iap.model.GetProductCatalogRequest;
import com.philips.cdp.di.iap.response.products.PaginationEntity;
import com.philips.cdp.di.iap.response.products.Products;
import com.philips.cdp.di.iap.session.HybrisDelegate;
import com.philips.cdp.di.iap.session.IAPNetworkError;
import com.philips.cdp.di.iap.session.RequestListener;
import com.philips.cdp.di.iap.store.StoreListener;
import com.philips.cdp.di.iap.utils.ModelConstants;
import com.philips.cdp.di.iap.utils.NetworkUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductCatalogPresenter implements ProductCatalogAPI, AbstractModel.DataLoadListener {

    private Context mContext;

    private HybrisDelegate mHybrisDelegate;
    private StoreListener mStore;

    private Products mProducts;
    private ProductCatalogHelper mProductCatalogHelper;

    protected ProductCatalogListener mProductCatalogListener;
    IAPListener mIAPListener;

    private final int PAGE_SIZE = 20;
    private final int CURRENT_PAGE = 0;

    public interface ProductCatalogListener {
        void onLoadFinished(ArrayList<ProductCatalogData> data, PaginationEntity paginationEntity);

        void onLoadError(IAPNetworkError error);
    }

    public ProductCatalogPresenter(Context context, ProductCatalogListener productCatalogListener) {
        mContext = context;
        mProductCatalogListener = productCatalogListener;
        mProductCatalogHelper = new ProductCatalogHelper(mContext, mProductCatalogListener, this);
    }

    public void getCompleteProductList(final IAPListener iapListener) {
        completeProductList(iapListener);
    }

    public void completeProductList(IAPListener iapListener) {
        if (
                CartModelContainer.getInstance().getProductList() != null
                        && CartModelContainer.getInstance().getProductList().size() != 0) {
            iapListener.onGetCompleteProductList(getProductCatalogDataFromStoredData());
        } else {
            CartModelContainer.getInstance().clearCategorisedProductList();
            getCatalogCount(iapListener);
        }
    }

    ArrayList<String> getProductCatalogDataFromStoredData() {
        ArrayList<String> catalogList = new ArrayList<>();
        HashMap<String, ProductCatalogData> productCatalogDataSaved =
                CartModelContainer.getInstance().getProductList();
        for (Map.Entry<String, ProductCatalogData> entry : productCatalogDataSaved.entrySet()) {
            if (entry != null) {
                catalogList.add(entry.getValue().getCtnNumber());
            }
        }
        return catalogList;
    }

    @Override
    public void getCatalogCount(final IAPListener iapListener) {
        final AbstractModel.DataLoadListener listener = this;
        HashMap<String, String> query = new HashMap<>();
        query.put(ModelConstants.CURRENT_PAGE, String.valueOf(CURRENT_PAGE));
        query.put(ModelConstants.PAGE_SIZE, String.valueOf(PAGE_SIZE));

        HybrisDelegate delegate = HybrisDelegate.getInstance(mContext);

        GetProductCatalogRequest model = new GetProductCatalogRequest(getStore(), query, listener);
        delegate.sendRequest(0, model, new RequestListener() {

            @Override
            public void onSuccess(final Message msg) {
                Products products = (Products) msg.obj;
                int totalProduct = products.getPagination().getTotalResults();
                if (totalProduct == 0) {
                    iapListener.onSuccess();
                    iapListener.onGetCompleteProductList(null);
                } else
                    getProductCatalog(CURRENT_PAGE, totalProduct, iapListener);

            }

            @Override
            public void onError(final Message msg) {
                iapListener.onFailure(0);
            }
        });
    }

    @Override
    public boolean getProductCatalog(int currentPage, int pageSize, IAPListener listener) {
        this.mIAPListener = listener;

        HashMap<String, String> query = new HashMap<>();
        query.put(ModelConstants.CURRENT_PAGE, String.valueOf(currentPage));
        query.put(ModelConstants.PAGE_SIZE, String.valueOf(pageSize));

        GetProductCatalogRequest model = new GetProductCatalogRequest(getStore(), query, this);
        getHybrisDelegate().sendRequest(0, model, model);
        return true;
    }

    @Override
    public void getCategorizedProductList(ArrayList<String> ctnList) {
        if (CartModelContainer.getInstance().getProductList() != null) {
            ArrayList<ProductCatalogData> productCatalogList = new ArrayList<>();
            CartModelContainer container = CartModelContainer.getInstance();
            for (String ctn : ctnList) {
                if (container.isProductCatalogDataPresent(ctn)) {
                    productCatalogList.add(container.getProduct(ctn));
                }
            }
            mProductCatalogListener.onLoadFinished(productCatalogList, null);
        }
    }

    @Override
    public void onModelDataLoadFinished(final Message msg) {
        if (msg.obj instanceof Products) {
            mProducts = (Products) msg.obj;

            if (mProducts.getPagination().getTotalResults() < 1) {
                trackNoProductFoundInPRX();
                mProductCatalogListener.onLoadError(NetworkUtility.getInstance().createIAPErrorMessage
                        ("", mContext.getString(R.string.iap_no_product_available)));
            } else {
                mProductCatalogHelper.sendPRXRequest(mProducts);
            }
        } else if (msg.obj instanceof HashMap) {
            mProductCatalogHelper.processPRXResponse(msg, mProducts, mIAPListener);
        }
    }

    @Override
    public void onModelDataError(final Message msg) {
        if (mProductCatalogListener == null) return;
        if (msg.obj instanceof IAPNetworkError)
            mProductCatalogListener.onLoadError((IAPNetworkError) msg.obj);
        else {
            trackNoProductFoundInPRX();
            mProductCatalogListener.onLoadError(NetworkUtility.getInstance().createIAPErrorMessage
                    ("", mContext.getString(R.string.iap_no_product_available)));
        }
    }

    private void trackNoProductFoundInPRX() {
        IAPAnalytics.trackTechnicalErrorWithPrefix(IAPAnalyticsConstant.PRX + IAPAnalyticsConstant.NO_PRODUCT_FOUND);
    }

    public void setHybrisDelegate(HybrisDelegate delegate) {
        mHybrisDelegate = delegate;
    }

    public HybrisDelegate getHybrisDelegate() {
        if (mHybrisDelegate == null) {
            mHybrisDelegate = HybrisDelegate.getInstance(mContext);
        }
        return mHybrisDelegate;
    }

    private StoreListener getStore() {
        if (mStore == null) {
            mStore = getHybrisDelegate().getStore();
        }
        return mStore;
    }

    public void setStore(StoreListener store) {
        mStore = store;
    }
}
