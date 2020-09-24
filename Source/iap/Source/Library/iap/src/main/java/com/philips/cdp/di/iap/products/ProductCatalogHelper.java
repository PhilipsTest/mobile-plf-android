/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.products;

import android.content.Context;
import android.os.Message;

import com.philips.cdp.di.iap.container.CartModelContainer;
import com.philips.cdp.di.iap.eventhelper.EventHelper;
import com.philips.cdp.di.iap.integration.IAPListener;
import com.philips.cdp.di.iap.model.AbstractModel;
import com.philips.cdp.di.iap.prx.PRXSummaryListExecutor;
import com.philips.cdp.di.iap.response.products.PaginationEntity;
import com.philips.cdp.di.iap.response.products.Products;
import com.philips.cdp.di.iap.response.products.ProductsEntity;
import com.philips.cdp.di.iap.utils.IAPConstant;
import com.philips.cdp.prxclient.datamodels.summary.Summary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductCatalogHelper {
    private Context mContext;
    private ProductCatalogPresenter.ProductCatalogListener mProductCatalogListener;
    private AbstractModel.DataLoadListener mGetProductCatalogListener;

    public ProductCatalogHelper(Context context, ProductCatalogPresenter.ProductCatalogListener productCatalogListener,
                                AbstractModel.DataLoadListener productListener) {
        mContext = context;
        mProductCatalogListener = productCatalogListener;
        mGetProductCatalogListener = productListener;
    }

    public void sendPRXRequest(Products productData) {
        ArrayList<String> productsToBeShown = new ArrayList<>();
        String ctn;

        if (productData != null) {
            final List<ProductsEntity> productsEntities = productData.getProducts();
            if (productsEntities != null)
                for (ProductsEntity entry : productsEntities) {
                    ctn = entry.getCode();
                    productsToBeShown.add(ctn);
                }
        }
        PRXSummaryListExecutor builder = new PRXSummaryListExecutor(mContext, productsToBeShown,
                mGetProductCatalogListener);

        builder.preparePRXDataRequest();
    }

    @SuppressWarnings("unchecked")
    public boolean processPRXResponse(final Message msg, Products productData, IAPListener listener) {
        if (msg.obj instanceof HashMap) {
            HashMap<String, Summary> prxModel = (HashMap<String, Summary>) msg.obj;
            if (checkForEmptyCart(prxModel))
                return true;

            ArrayList<ProductCatalogData> products = mergeHybrisAndPRX(productData, prxModel);
            PaginationEntity pagination = null;
            if (productData != null && products.size() != 0)
                pagination = productData.getPagination();
            refreshList(products, pagination, listener);

        } else {
            notifyEmptyCartFragment();
        }
        return false;
    }

    private ArrayList<ProductCatalogData> mergeHybrisAndPRX(Products productData,
                                                            HashMap<String, Summary> prxModel) {
        List<ProductsEntity> entries = productData.getProducts();
        ArrayList<ProductCatalogData> products = new ArrayList<>();
        String ctn;
        if (entries != null)
            for (ProductsEntity entry : entries) {
                ctn = entry.getCode();
                ProductCatalogData productItem = new ProductCatalogData();
                Summary data;
                if (prxModel.containsKey(ctn)) {
                    data = prxModel.get(ctn);
                } else if (CartModelContainer.getInstance().isPRXSummaryPresent(ctn)) {
                    data = CartModelContainer.getInstance().getProductSummary(ctn);
                } else {
                    continue;
                }
                productItem.setImageUrl(data.getImageURL());
                productItem.setProductTitle(data.getProductTitle());
                productItem.setCtnNumber(ctn);
                productItem.setMarketingTextHeader(data.getMarketingTextHeader());
                fillEntryBaseData(entry, productItem);
                products.add(productItem);
            }
        return products;
    }

    private void fillEntryBaseData(final ProductsEntity entry, final ProductCatalogData productItem) {
        if (null!=entry.getPrice()  && null!= entry.getPrice().getFormattedValue() ){
            productItem.setFormattedPrice(entry.getPrice().getFormattedValue());
            productItem.setPriceValue(String.valueOf(entry.getPrice().getValue()));
        }
        if (entry.getDiscountPrice() != null && entry.getDiscountPrice().getFormattedValue() != null
                && !entry.getDiscountPrice().getFormattedValue().isEmpty()) {
            productItem.setDiscountedPrice(entry.getDiscountPrice().getFormattedValue());
        }
        if(null!=entry.getStock() && null!=entry.getStock().getStockLevelStatus()){
            productItem.setStockLevelStatus(entry.getStock().getStockLevelStatus());
            productItem.setStockLevel(entry.getStock().getStockLevel());
        }

    }

    private void notifyEmptyCartFragment() {
        EventHelper.getInstance().notifyEventOccurred(IAPConstant.EMPTY_CART_FRAGMENT_REPLACED);
    }

    private boolean checkForEmptyCart(final HashMap<String, Summary> prxModel) {
        if (prxModel == null || prxModel.size() == 0) {
            notifyEmptyCartFragment();
            return true;
        }
        return false;
    }

    public void refreshList(ArrayList<ProductCatalogData> data, PaginationEntity paginationEntity, IAPListener listener) {
        storeData(data);
        if (mProductCatalogListener != null) {
            mProductCatalogListener.onLoadFinished(data, paginationEntity);
        }
        if (listener != null) {
            listener.onGetCompleteProductList(getProductCTNs(data));
        }
    }

    protected ArrayList<String> getProductCTNs(final ArrayList<ProductCatalogData> data) {
        ArrayList<String> productCodes = new ArrayList<>();
        for (ProductCatalogData entry : data) {
            productCodes.add(entry.getCtnNumber());
        }
        return productCodes;
    }

    private void storeData(final ArrayList<ProductCatalogData> data) {
        CartModelContainer container = CartModelContainer.getInstance();
        if (data == null) return;

        String CTN;
        for (ProductCatalogData entry : data) {
            CTN = entry.getCtnNumber();
                if (CTN != null ) {
                    if (!container.isProductCatalogDataPresent(CTN)) {
                        container.addProduct(CTN, entry);
                    }
                } else {
                    CartModelContainer.getInstance().clearCategorisedProductList();
                }
        }
    }
}
