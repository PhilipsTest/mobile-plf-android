/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.ecs.demouapp.ui.cart;

import android.content.Context;
import android.os.Message;

import com.ecs.demouapp.R;
import com.ecs.demouapp.ui.analytics.ECSAnalytics;
import com.ecs.demouapp.ui.analytics.ECSAnalyticsConstant;
import com.ecs.demouapp.ui.controller.ControllerFactory;
import com.ecs.demouapp.ui.session.IAPNetworkError;
import com.ecs.demouapp.ui.utils.ECSUtility;
import com.ecs.demouapp.ui.utils.NetworkUtility;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.cart.ECSShoppingCart;
import com.philips.platform.ecs.model.retailers.ECSRetailer;
import com.philips.platform.ecs.model.retailers.ECSRetailerList;


import java.util.ArrayList;
import java.util.Iterator;

@SuppressWarnings("rawtypes")
public abstract class AbstractShoppingCartPresenter implements ShoppingCartAPI {
    protected static final String PHILIPS_STORE = "Y";
    protected Context mContext;
    protected ArrayList<ECSRetailer> mStoreList;
    protected ShoppingCartListener mLoadListener;

    public interface ShoppingCartListener<T> {

        void onLoadFinished(ArrayList<? extends Object> data);
        void onLoadError(Message msg);
        void onRetailerError(IAPNetworkError errorMsg);
        void onLoadFinished(ECSShoppingCart data);
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

        ECSUtility.getInstance().getEcsServices().fetchRetailers(ctn, new ECSCallback<ECSRetailerList, Exception>() {
            @Override
            public void onResponse(ECSRetailerList webResults) {

                if (webResults == null) {
                    trackRetailer(ctn);
                    mLoadListener.onRetailerError(NetworkUtility.getInstance().
                            createIAPErrorMessage("", mContext.getString(R.string.iap_no_retailer_message)));
                    return;
                }

                if (webResults.getWrbresults().getOnlineStoresForProduct() == null ||
                        webResults.getWrbresults().getOnlineStoresForProduct().getStores().getRetailerList() == null ||
                        webResults.getWrbresults().getOnlineStoresForProduct().getStores().getRetailerList().size() == 0) {
                    if (mLoadListener != null) {
                        trackRetailer(ctn);
                        mLoadListener.onRetailerError(NetworkUtility.getInstance().
                                createIAPErrorMessage("", mContext.getString(R.string.iap_no_retailer_message)));
                    }
                    return;
                }

                mStoreList = (ArrayList<ECSRetailer>) webResults.getWrbresults().
                        getOnlineStoresForProduct().getStores().getRetailerList();
                handlePhilipsFlagShipStore();
                refreshList(mStoreList);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {

                Message message = new Message();
                message.obj = error;
                handleModelDataError(message);
            }
        });
    }




    private void trackRetailer(String pCtn) {
        ECSAnalytics.trackAction(ECSAnalyticsConstant.SEND_DATA,
                ECSAnalyticsConstant.ERROR, ECSAnalyticsConstant.WTB + String.valueOf(pCtn) + "_" + ECSAnalyticsConstant.NO_RETAILER_FOUND);
    }

    private void handlePhilipsFlagShipStore() {
        Iterator<ECSRetailer> iterator = mStoreList.iterator();
        while (iterator.hasNext()) {
            ECSRetailer entity = iterator.next();
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

}
