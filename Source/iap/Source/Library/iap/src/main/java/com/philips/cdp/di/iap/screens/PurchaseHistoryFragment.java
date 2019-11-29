/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.screens;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.activity.IAPActivity;
import com.philips.cdp.di.iap.adapters.OrderHistoryAdapter;
import com.philips.cdp.di.iap.analytics.IAPAnalytics;
import com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant;
import com.philips.cdp.di.iap.controller.ControllerFactory;
import com.philips.cdp.di.iap.controller.OrderController;
import com.philips.cdp.di.iap.eventhelper.EventHelper;
import com.philips.cdp.di.iap.eventhelper.EventListener;
import com.philips.cdp.di.iap.model.AbstractModel;
import com.philips.cdp.di.iap.response.orders.OrderDetail;
import com.philips.cdp.di.iap.response.orders.Orders;
import com.philips.cdp.di.iap.response.orders.OrdersData;
import com.philips.cdp.di.iap.response.orders.ProductData;
import com.philips.cdp.di.iap.session.IAPNetworkError;
import com.philips.cdp.di.iap.session.NetworkConstants;
import com.philips.cdp.di.iap.session.RequestCode;
import com.philips.cdp.di.iap.utils.AlertListener;
import com.philips.cdp.di.iap.utils.IAPConstant;
import com.philips.cdp.di.iap.utils.IAPLog;
import com.philips.cdp.di.iap.utils.IAPUtility;
import com.philips.cdp.di.iap.utils.NetworkUtility;
import com.philips.cdp.prxclient.datamodels.summary.SummaryModel;
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PurchaseHistoryFragment extends InAppBaseFragment implements OrderController.OrderListener, EventListener, AbstractModel.DataLoadListener {

    public static final String TAG = PurchaseHistoryFragment.class.getName();
    private OrderHistoryAdapter mAdapter;
    private Context mContext;
    private RecyclerView mOrderHistoryView;
    private List<Orders> mOrders = new ArrayList<>();
    private OrderController mController;
    private int mTotalOrders = 0;
    private int mPageSize = 0;
    private int mPageNo = 0;
    private int mRemainingOrders = 0;
    private boolean mIsLoading = false;
    private int mOrderCount = 0;
    private RelativeLayout mParentLayout;
    ArrayList<OrderDetail> mOrderDetails = new ArrayList<>();
    ArrayList<ProductData> mProducts = new ArrayList<>();


    @Override
    public void onResume() {
        super.onResume();
        IAPAnalytics.trackPage(IAPAnalyticsConstant.ORDER_HISTORY_PAGE_NAME);
        setTitleAndBackButtonVisibility(R.string.iap_my_orders, false);
        setCartIconVisibility(false);

        if (mOrders.isEmpty()) {
            updateHistoryListOnResume();
        }else
        {
            hideProgressBar();
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.iap_order_history_fragment, container, false);

        EventHelper.getInstance().registerEventNotification(String.valueOf(IAPConstant.PURCHASE_HISTORY_DETAIL), this);

        mOrderHistoryView = rootView.findViewById(R.id.order_history);
        mParentLayout = rootView.findViewById(R.id.order_history_container);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mOrderHistoryView.setLayoutManager(layoutManager);

        mAdapter = new OrderHistoryAdapter(mContext, mOrders, mProducts);
        mOrderHistoryView.setAdapter(mAdapter);
        mOrderHistoryView.addOnScrollListener(mRecyclerViewOnScrollListener);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public boolean handleBackEvent() {
        if (getActivity() != null && getActivity() instanceof IAPActivity) {
            finishActivity();
        }

        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideProgressBar();
        EventHelper.getInstance().unregisterEventNotification(String.valueOf(IAPConstant.PURCHASE_HISTORY_DETAIL), this);
    }

    private void updateHistoryListOnResume() {
        if(isUserLoggedIn() && ! ControllerFactory.getInstance().isPlanB()){
            createCustomProgressBar(mParentLayout,BIG);
            mController = new OrderController(mContext, this);
            mController.getOrderList(mPageNo);
        }else{
            if(mIapListener!=null && !isUserLoggedIn()){
                mIapListener.onFailure(IAPConstant.IAP_ERROR_AUTHENTICATION_FAILURE);
            }else if(mIapListener!=null){
                mIapListener.onFailure(IAPConstant.IAP_ERROR_SERVER_ERROR);
            }
            moveToVerticalAppByClearingStack();
        }
    }

    @Override
    public void onGetOrderList(Message msg) {
        if (msg.obj instanceof IAPNetworkError) {
            hideProgressBar();
            NetworkUtility.getInstance().showErrorMessage(msg, getFragmentManager(), mContext);
        } else {
            if (msg.what == RequestCode.GET_ORDERS) {
                if (msg.obj instanceof OrdersData) {
                    OrdersData orderData = (OrdersData) msg.obj;
                    if (orderData.getOrders() == null || orderData.getOrders().size() == 0) {
                        hideProgressBar();
                        addFragment(EmptyPurchaseHistoryFragment.createInstance(new Bundle(),
                                InAppBaseFragment.AnimationType.NONE), EmptyPurchaseHistoryFragment.TAG,true);
                    } else {
                        for (Orders order : orderData.getOrders())
                            mOrders.add(order);
                        if (mTotalOrders == 0) {
                            mRemainingOrders = orderData.getPagination().getTotalResults();
                        }
                        mTotalOrders = orderData.getPagination().getTotalResults();
                        mPageSize = orderData.getPagination().getPageSize();
                        mPageNo = orderData.getPagination().getCurrentPage();
                        mIsLoading = false;
                        mOrderCount = mPageNo * mPageSize;
                        for (int i = mPageNo * mPageSize; i < mOrders.size(); i++) {
                            if (mController == null)
                                mController = new OrderController(mContext, this);
                            mController.getOrderDetails(mOrders.get(i).getCode());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onGetOrderDetail(Message msg) {
        mOrderCount++;
        if (msg.obj instanceof IAPNetworkError) {
            IAPLog.d(TAG, ((IAPNetworkError) msg.obj).getMessage());
        } else {
            if (msg.what == RequestCode.GET_ORDER_DETAIL) {
                if (msg.obj instanceof OrderDetail) {
                    OrderDetail orderDetail = (OrderDetail) msg.obj;
                    mOrderDetails.add(orderDetail);
                }
            }
        }
        if (mOrderCount == mOrders.size()) {
            updateProductDetails(mOrderDetails);
        }
    }

    @Override
    public void updateUiOnProductList() {
        if (mController == null)
            mController = new OrderController(mContext, this);
        ArrayList<ProductData> productList = mController.getProductData(mOrderDetails);
        mProducts.clear();
        for (ProductData product : productList)
            mProducts.add(product);
        mAdapter.notifyDataSetChanged();
        hideProgressBar();
    }

    @Override
    public void onGetPhoneContact(Message msg) {

    }

    private void startOrderDetailFragment() {
        if (!isNetworkConnected()) return;
        int pos = mAdapter.getSelectedPosition();
        Orders order = mOrders.get(pos);
        Bundle bundle = new Bundle();
        if (order != null) {
            bundle.putString(IAPConstant.PURCHASE_ID, order.getCode());
            bundle.putString(IAPConstant.ORDER_STATUS, order.getStatusDisplay());
            for (OrderDetail detail : mOrderDetails) {
                if (detail.getCode().equals(order.getCode())) {
                    bundle.putParcelable(IAPConstant.ORDER_DETAIL, detail);
                    break;
                }
            }
            if (bundle.getParcelable(IAPConstant.ORDER_DETAIL) != null)
                addFragment(OrderDetailsFragment.createInstance(bundle, AnimationType.NONE), OrderDetailsFragment.TAG,true);
        }
    }

    public static PurchaseHistoryFragment createInstance
            (Bundle args, InAppBaseFragment.AnimationType animType) {
        PurchaseHistoryFragment fragment = new PurchaseHistoryFragment();
        args.putInt(NetworkConstants.EXTRA_ANIMATIONTYPE, animType.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onEventReceived(String event) {
        if (event.equalsIgnoreCase(String.valueOf(IAPConstant.PURCHASE_HISTORY_DETAIL))) {
            startOrderDetailFragment();
        }
    }

    private void updateProductDetails(List<OrderDetail> orderDetails) {
        if (mController == null)
            mController = new OrderController(mContext, this);
        mController.requestPrxData(orderDetails, this);
    }

    //TODO
    @SuppressWarnings({"rawtype", "unchecked"})
    private boolean processResponseFromPRX(final Message msg) {
        if (msg.obj instanceof HashMap) {
            HashMap<String, SummaryModel> prxModel = (HashMap<String, SummaryModel>) msg.obj;
            if (prxModel == null || prxModel.size() == 0) {
                hideProgressBar();
                return true;
            }
            updateUiOnProductList();
        }
        return false;
    }

    @Override
    public void onModelDataLoadFinished(Message msg) {
        if (processResponseFromPRX(msg)) return;
        hideProgressBar();
    }

    @Override
    public void onModelDataError(Message msg) {
        hideProgressBar();
    }

    private RecyclerView.OnScrollListener
            mRecyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView,
                                         int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            LinearLayoutManager mLayoutManager = (LinearLayoutManager) mOrderHistoryView
                    .getLayoutManager();

            int visibleItemCount = mLayoutManager.getChildCount();
            int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();

            if (!mIsLoading) {
                //if scrolled beyond page size and we have more items to load
                if ((visibleItemCount + firstVisibleItemPosition) >= mLayoutManager.getItemCount()
                        && firstVisibleItemPosition >= 0
                        && mRemainingOrders > mPageSize) {
                    mIsLoading = true;
                    IAPLog.d(TAG, "visibleItem " + visibleItemCount + ", firstvisibleItemPistion " + firstVisibleItemPosition + "itemCount " + mLayoutManager.getItemCount());
                    loadMoreItems();
                }
            }
        }
    };

    private void loadMoreItems() {
        mRemainingOrders = mRemainingOrders - mPageSize;
        mController.getOrderList(++mPageNo);
    }
}
