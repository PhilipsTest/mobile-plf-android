/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.cart;

import android.content.Context;
import android.os.Message;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.analytics.IAPAnalytics;
import com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant;
import com.philips.cdp.di.iap.controller.ControllerFactory;
import com.philips.cdp.di.iap.model.AbstractModel;
import com.philips.cdp.di.iap.model.GetRetailersInfoRequest;
import com.philips.cdp.di.iap.response.retailers.StoreEntity;
import com.philips.cdp.di.iap.response.retailers.WebResults;
import com.philips.cdp.di.iap.session.HybrisDelegate;
import com.philips.cdp.di.iap.session.IAPJsonRequest;
import com.philips.cdp.di.iap.session.IAPNetworkError;
import com.philips.cdp.di.iap.session.NetworkConstants;
import com.philips.cdp.di.iap.session.VolleyWrapper;
import com.philips.cdp.di.iap.store.StoreListener;
import com.philips.cdp.di.iap.utils.IAPLog;
import com.philips.cdp.di.iap.utils.ModelConstants;
import com.philips.cdp.di.iap.utils.NetworkUtility;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings("rawtypes")
public abstract class AbstractShoppingCartPresenter implements ShoppingCartAPI {
    protected static final String PHILIPS_STORE = "Y";
    protected Context mContext;
    protected HybrisDelegate mHybrisDelegate;
    protected ArrayList<StoreEntity> mStoreList;
    protected StoreListener mStore;
    protected ShoppingCartListener mLoadListener;

    public interface ShoppingCartListener<T> {

        void onLoadFinished(ArrayList<? extends Object> data);

        void onLoadError(Message msg);

        void onRetailerError(IAPNetworkError errorMsg);
    }

    public AbstractShoppingCartPresenter() {
    }

    public AbstractShoppingCartPresenter(final Context context, final ShoppingCartListener listener) {
        mContext = context;
        mLoadListener = listener;
    }

    protected void handleModelDataError(final Message msg) {
        if (mLoadListener != null) {
            mLoadListener.onLoadError(msg);
        }
    }

    @Override
    public void getRetailersInformation(final String ctn) {
        Map<String, String> query = new HashMap<>();
        query.put(ModelConstants.PRODUCT_CODE, String.valueOf(ctn));

        GetRetailersInfoRequest model = new GetRetailersInfoRequest(getStore(), query,
                new AbstractModel.DataLoadListener() {
                    @Override
                    public void onModelDataLoadFinished(Message msg) {
                        WebResults webResults = null;

                        if (msg.obj instanceof WebResults) {
                            webResults = (WebResults) msg.obj;
                        }

                        if (webResults == null) {
                            trackRetailer(ctn);
                            mLoadListener.onRetailerError(NetworkUtility.getInstance().
                                    createIAPErrorMessage("", mContext.getString(R.string.iap_no_retailer_message)));
                            return;
                        }

                        if (webResults.getWrbresults().getOnlineStoresForProduct() == null ||
                                webResults.getWrbresults().getOnlineStoresForProduct().getStores().getStore() == null ||
                                webResults.getWrbresults().getOnlineStoresForProduct().getStores().getStore().size() == 0) {
                            if (mLoadListener != null) {
                                trackRetailer(ctn);
                                mLoadListener.onRetailerError(NetworkUtility.getInstance().
                                        createIAPErrorMessage("", mContext.getString(R.string.iap_no_retailer_message)));
                            }
                            return;
                        }

                        mStoreList = (ArrayList<StoreEntity>) webResults.getWrbresults().
                                getOnlineStoresForProduct().getStores().getStore();
                        handlePhilipsFlagShipStore();
                        refreshList(mStoreList);
                    }

                    @Override
                    public void onModelDataError(final Message msg) {
                        handleModelDataError(msg);
                    }
                });


        Response.ErrorListener error = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {

                if (model.getUrl() != null && error != null) {
                    IAPLog.d(IAPLog.LOG, "Response from sendHybrisRequest onError =" + error
                            .getLocalizedMessage() + " requestCode=" + 0 + "in " +
                            model.getClass().getSimpleName() + " " + model.getUrl().substring(0, 20));
                }
                if (error != null && error.getMessage() != null) {
                    IAPAnalytics.trackTechnicalErrorWithPrefix(error.getMessage());
                }
                if (model != null) {
                    new IAPNetworkError(error, 0, model);
                }
            }
        };

        Response.Listener<JSONObject> response = new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(final JSONObject response) {

                if (model != null) {
                    Message msg = Message.obtain();
                    msg.what = 0;

                    if (response != null && response.length() == 0) {
                        msg.obj = NetworkConstants.EMPTY_RESPONSE;
                    } else {
                        msg.obj = model.parseResponse(response);
                    }

                    model.onSuccess(msg);


                    //For testing purpose
                    if (model.getUrl() != null) {
                        IAPLog.d(IAPLog.LOG, "Response from sendHybrisRequest onFetchOfProductList =" + msg + " requestCode=" + 0 + "in " +
                                model.getClass().getSimpleName() + "env = " + " " + model.getUrl().substring(0, 15));
                    }
                }
            }
        };

        IAPJsonRequest iapJsonRequest = getIapJsonRequest(model, error, response);

        RequestQueue requestQueue = VolleyWrapper.newRequestQueue(mContext, null);
        requestQueue.add(iapJsonRequest);

        //getHybrisDelegate().sendRequest(0, model, model);
    }

    IAPJsonRequest getIapJsonRequest(final AbstractModel model, final Response.ErrorListener error, final Response.Listener<JSONObject> response) {
        return new IAPJsonRequest(model.getMethod(), model.getUrl(),
                model.requestBody(), response, error);
    }


    private void trackRetailer(String pCtn) {
        IAPAnalytics.trackTechnicalErrorWithPrefix(IAPAnalyticsConstant.WTB + String.valueOf(pCtn) + "_" + IAPAnalyticsConstant.NO_RETAILER_FOUND);
    }

    private void handlePhilipsFlagShipStore() {
        Iterator<StoreEntity> iterator = mStoreList.iterator();
        while (iterator.hasNext()) {
            StoreEntity entity = iterator.next();
            if (PHILIPS_STORE.equalsIgnoreCase(entity.getIsPhilipsStore())
                    && !ControllerFactory.getInstance().isPlanB()) {
                iterator.remove();
            }
        }
    }

    @SuppressWarnings({"unchecked"})
    public void refreshList(ArrayList data) {
        if (mLoadListener == null) {
            return;
        }
        if (!data.isEmpty())
            mLoadListener.onLoadFinished(data);
        else
            mLoadListener.onRetailerError(NetworkUtility.getInstance().
                    createIAPErrorMessage("", mContext.getString(R.string.iap_no_retailer_message)));
    }

    public HybrisDelegate getHybrisDelegate() {
        if (mHybrisDelegate == null) {
            mHybrisDelegate = HybrisDelegate.getInstance(mContext);
        }
        return mHybrisDelegate;
    }

    protected StoreListener getStore() {
        if (mStore == null) {
            mStore = getHybrisDelegate().getStore();
        }
        return mStore;
    }
}
