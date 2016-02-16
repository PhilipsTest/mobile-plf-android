/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */

package com.philips.cdp.di.iap.ShoppingCart;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Message;
import android.widget.Toast;

import com.philips.cdp.di.iap.activity.EmptyCartActivity;
import com.philips.cdp.di.iap.model.AbstractModel;
import com.philips.cdp.di.iap.model.CartCurrentInfoRequest;
import com.philips.cdp.di.iap.model.CartDeleteProductRequest;
import com.philips.cdp.di.iap.model.CartModel;
import com.philips.cdp.di.iap.model.CartUpdateProductQuantityRequest;
import com.philips.cdp.di.iap.session.HybrisDelegate;
import com.philips.cdp.di.iap.session.RequestListener;
import com.philips.cdp.di.iap.store.Store;
import com.philips.cdp.di.iap.utils.IAPLog;
import com.philips.cdp.di.iap.utils.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCartPresenter {
    private static final String TAG = ShoppingCartPresenter.class.getName();
    Context mContext;
    ArrayList<ShoppingCartData> mProductData;
    private LoadListener mLoadListener;
    private Resources mResources;
    private final String UPDATE = "update";
    private final String ADD = "add";
    private HybrisDelegate mHybrisDelegate;
    private Store mStore;

    public interface LoadListener {
        void onLoadFinished(ArrayList<ShoppingCartData> data);

        void updateStock(boolean isOutOfStock);
    }

    public ShoppingCartPresenter(Context context, LoadListener listener) {
        mContext = context;
        mProductData = new ArrayList<ShoppingCartData>();
        mLoadListener = listener;
        mResources = mContext.getResources();
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

    private Store getStore() {
        if (mStore == null) {
            mStore = getHybrisDelegate().getStore();
        }
        return mStore;
    }

    //TODO: fix with TAG
    private void addShippingCostRowToTheList() {
        ShoppingCartData summary = new ShoppingCartData();
        mProductData.add(summary);
        mProductData.add(summary);
        mProductData.add(summary);
    }

    public void refreshList(ArrayList<ShoppingCartData> data) {
        if (mLoadListener != null) {
            mLoadListener.onLoadFinished(data);
        }
    }

    private void sendHybrisRequest(int code, AbstractModel model, RequestListener listener) {
//        Utility.showProgressDialog(mContext, msg);
        getHybrisDelegate().sendRequest(code, model, model);
    }

    public void getCurrentCartDetails() {
        CartCurrentInfoRequest model = new CartCurrentInfoRequest(getStore(), null,
                new AbstractModel.DataLoadListener() {
                    @Override
                    public void onModelDataLoadFinished(final Message msg) {
                        mProductData = (ArrayList<ShoppingCartData>) msg.obj;
                        refreshList(mProductData);
                        Utility.dismissProgressDialog();
                    }

                    @Override
                    public void onModelDataError(final Message msg) {
                        IAPLog.e(TAG, "Error:" + msg.obj);
                        Toast.makeText(mContext, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        Utility.dismissProgressDialog();
                    }
                });
        model.setContext(mContext);
        sendHybrisRequest(0,model, model);
    }

    public void deleteProduct(final ShoppingCartData summary) {
        Map<String, String> query = new HashMap<>();
        query.put(AbstractModel.ENTRY_CODE, String.valueOf(summary.getEntryNumber()));

        CartDeleteProductRequest model = new CartDeleteProductRequest(getStore(), query,
                new AbstractModel.DataLoadListener() {
                    @Override
                    public void onModelDataLoadFinished(final Message msg) {
                        removeItemFromList(summary);
                        Utility.dismissProgressDialog();
                        refreshList(mProductData);
                        checkIfCartIsEmpty();
                    }

                    @Override
                    public void onModelDataError(final Message msg) {
                        Toast.makeText(mContext, "Delete Request Error", Toast.LENGTH_SHORT).show();
                        refreshList(mProductData);
                        Utility.dismissProgressDialog();
                    }
                });
        sendHybrisRequest(0,model, model);
    }

    public void updateProductQuantity(final ShoppingCartData data, final int count) {
        HashMap<String, String> query = new HashMap<String, String>();
        query.put(CartModel.PRODUCT_CODE, data.getCtnNumber());
        query.put(CartModel.PRODUCT_QUANTITY, String.valueOf(count));
        query.put(CartModel.PRODUCT_ENTRYCODE, String.valueOf(data.getEntryNumber()));

        CartUpdateProductQuantityRequest model = new CartUpdateProductQuantityRequest(getStore(),
                query, new AbstractModel.DataLoadListener() {
            @Override
            public void onModelDataLoadFinished(final Message msg) {
                getCurrentCartDetails();
            }

            @Override
            public void onModelDataError(final Message msg) {
                Toast.makeText(mContext, "Something went wrong!" + msg.obj.toString(), Toast.LENGTH_SHORT).show();
                Utility.dismissProgressDialog();
            }
        });
        sendHybrisRequest(0, model, model);
    }

    private void checkIfCartIsEmpty() {
        if (mProductData.size() <= 3) {
            Intent intent = new Intent(mContext, EmptyCartActivity.class);
            mContext.startActivity(intent);
        }
    }

    private void removeItemFromList(ShoppingCartData pProductdata) {
        if (mProductData.size() <= 4) {
            mProductData.removeAll(mProductData);
        } else {
            mProductData.remove(pProductdata);
        }
    }
}